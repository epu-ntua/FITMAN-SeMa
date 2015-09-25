/**
 *
 */
package de.wdilab.ml.impl.matcher.simple.tfidf;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.mahout.common.distance.CosineDistanceMeasure;
import org.apache.mahout.math.Vector;
import org.apache.mahout.utils.vectors.TermInfo;
import org.apache.mahout.utils.vectors.lucene.TFDFMapper;
import org.apache.mahout.utils.vectors.lucene.VectorMapper;
import org.apache.mahout.vectorizer.TFIDF;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleOneSourceAttributeObjectMatcher;
import de.wdilab.ml.impl.oi.enhancement.persistence.MainMemoryFullyCachedObjectInstanceProvider;
import de.wdilab.ml.impl.oi.preprocessing.AdaptedCachedTermInfo;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.EOIProviderCapabilityHint;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Cosine Matcher fully cached.
 *
 * @author Nico Heller
 */
public class LuceneTFIDFFullyCachedAlternative extends
		AbstractSimpleOneSourceAttributeObjectMatcher implements
		IAttributeObjectMatcher {
	protected static final Logger log = Logger
			.getLogger(LuceneTFIDFFullyCachedAlternative.class);

	/**
   */
	public LuceneTFIDFFullyCachedAlternative() {
		super();
	}

	/**
	 * @param attr1
	 * @param threshold
	 */
	public LuceneTFIDFFullyCachedAlternative(final String attr1,
			final float threshold) {
		super(attr1, threshold);
	}

	/**
	 * @param attr1
	 * @param attr2
	 */
	public LuceneTFIDFFullyCachedAlternative(final String attr1,
			final String attr2) {
		super(attr1, attr2, 0f);
	}

	/**
	 * @param attr1
	 * @param attr2
	 * @param threshold
	 */
	public LuceneTFIDFFullyCachedAlternative(final String attr1,
			final String attr2, final float threshold) {
		super(attr1, attr2, threshold);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
	 * .oi. IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.oi.IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(final IObjectInstanceProvider oip1,
			final IObjectInstanceProvider oip2, final IMappingStore mrs)
			throws MappingStoreException {
		log.info("Match Start.");

		final Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		Directory directory = null;
		try {
			directory = new RAMDirectory();
			try {
				final IndexWriter writer = new IndexWriter(directory, analyzer,
						true, IndexWriter.MaxFieldLength.UNLIMITED);

				for (final IObjectInstance oi : oip1) {

					final Document document = new Document();
					document.add(new Field("id", "1_" + oi.getId(),
							Field.Store.YES, Field.Index.NOT_ANALYZED,
							Field.TermVector.NO));
					String value = oi.getStringValue(attrLinks);
					if (value == null)
						value = "";
					document.add(new Field("content", value, Field.Store.YES,
							Field.Index.ANALYZED,
							Field.TermVector.WITH_POSITIONS_OFFSETS));

					writer.addDocument(document);
				}

				for (final IObjectInstance oi : oip2) {

					final Document document = new Document();
					document.add(new Field("id", "2_" + oi.getId(),
							Field.Store.YES, Field.Index.NOT_ANALYZED,
							Field.TermVector.NO));
					String value = oi.getStringValue(attrRechts);
					if (value == null)
						value = "";
					document.add(new Field("content", value, Field.Store.YES,
							Field.Index.ANALYZED,
							Field.TermVector.WITH_POSITIONS_OFFSETS));

					writer.addDocument(document);
				}
				writer.optimize();
				writer.close();
			} catch (final Exception e) {
				log.info(e);
			}
		} catch (final Exception e) {
			log.info(e);
		}

		IndexReader reader = null;
		try {
			reader = IndexReader.open(directory, true);

			final TermInfo termInfo = new AdaptedCachedTermInfo(reader, "content", 1,
					100.0);

			final VectorMapper mapper = new TFDFMapper(reader, new TFIDF(), termInfo);

			final HashMap<String, Vector> vectors = new HashMap<String, Vector>();

			final TermDocs termDocs = reader.termDocs(null);

			while (termDocs.next()) {
				final int doc = termDocs.doc();

				reader.getTermFreqVector(doc, "content", mapper);
				mapper.setDocumentNumber(doc);

				Vector result = mapper.getVector();
				result = result.normalize(2);
				final Document document = reader.document(doc);
				vectors.put(document.get("id"), result);
			}

			final CosineDistanceMeasure measure = new CosineDistanceMeasure();
			for (final IObjectInstance oi1 : oip1) {
				final String id1 = "1_" + oi1.getId();
				final Vector v1 = vectors.get(id1);
				for (final IObjectInstance oi2 : oip2) {
					final String id2 = "2_" + oi2.getId();
					final Vector v2 = vectors.get(id2);
					double sim = 0;
					final double dist = measure.distance(v1, v2);
					if (!Double.isNaN(dist))
						sim = 1.0 - dist;
					if (sim >= threshold) {
						mrs.add(oi1, oi2, new Similarity(sim));
					}

				}
			}
		} catch (final CorruptIndexException e) {
			log.error(e, e);
		} catch (final IOException e) {
			log.error(e, e);
		} // only searching, so read-only=true

		log.info("Match Ended.");
	}

	@Override
	public void match(final IObjectInstanceProvider oip, final IMappingStore mrs)
			throws MappingStoreException {
		log.info("Match Start.");

		IObjectInstanceProvider mmp;
		// Wrapping für HighSpeed getInsatnce
		if (oip.hasCapability(EOIProviderCapabilityHint.MEMORY))
			mmp = oip;
		else
			mmp = new MainMemoryFullyCachedObjectInstanceProvider(oip);

		final Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		Directory directory = null;
		try {
			directory = new RAMDirectory();
			try {
				final IndexWriter writer = new IndexWriter(directory, analyzer,
						true, IndexWriter.MaxFieldLength.UNLIMITED);

				for (final IObjectInstance oi : mmp) {
					final Document document = new Document();
					document.add(new Field("id", oi.getId(), Field.Store.YES,
							Field.Index.NOT_ANALYZED, Field.TermVector.NO));
					document.add(new Field("content", oi
							.getStringValue(attrLinks), Field.Store.YES,
							Field.Index.ANALYZED,
							Field.TermVector.WITH_POSITIONS_OFFSETS));

					writer.addDocument(document);
				}
				writer.optimize();
				writer.close();
			} catch (final Exception e) {
				log.info(e);
			}
		} catch (final Exception e) {
			log.info(e);
		}

		IndexReader reader = null;
		try {
			reader = IndexReader.open(directory, true);

			final TermInfo termInfo = new AdaptedCachedTermInfo(reader, "content", 1,
					100.0);

			final VectorMapper mapper = new TFDFMapper(reader, new TFIDF(), termInfo);

			final HashMap<String, Vector> vectors = new HashMap<String, Vector>();

			final TermDocs termDocs = reader.termDocs(null);

			while (termDocs.next()) {
				final int doc = termDocs.doc();
				reader.getTermFreqVector(doc, "content", mapper);
				mapper.setDocumentNumber(doc);

				Vector result = mapper.getVector();
				result = result.normalize(2);
				final Document document = reader.document(doc);
				vectors.put(document.get("id"), result);
			}

			final java.util.Vector<String> ids = new java.util.Vector<String>(vectors
					.keySet());
			final CosineDistanceMeasure measure = new CosineDistanceMeasure();
			for (int i = 0; i < ids.size() - 1; i++) {
				final String id1 = ids.get(i);
				final Vector v1 = vectors.get(id1);
				for (int j = i + 1; j < ids.size(); j++) {
					final String id2 = ids.get(j);
					final Vector v2 = vectors.get(ids.get(j));
					double sim = 0;
					final double dist = measure.distance(v1, v2);
					if (!Double.isNaN(dist))
						sim = 1.0 - dist;
					if (sim >= threshold) {
						mrs.add(mmp.getInstance(id1), mmp.getInstance(id2),
								new Similarity(sim));
					}
				}
			}
		} catch (final CorruptIndexException e) {
			log.error(e, e);
		} catch (final IOException e) {
			log.error(e, e);
		} // only searching, so read-only=true

		log.info("Match Ended.");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.
	 * interfaces.mapping. IMappingProvider,
	 * de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(final IMappingProvider mp, final IMappingStore mrs)
			throws MappingStoreException {
		log.info("Match Start.");
		final HashSet<IObjectInstance> oiLeft = new HashSet<IObjectInstance>();
		final HashSet<IObjectInstance> oiRight = new HashSet<IObjectInstance>();
		final HashSet<String> mids = new HashSet<String>();
		for (final IMappingEntry e : mp) {
			oiLeft.add(e.getLeft());
			oiRight.add(e.getRight());
			//TODO unklug kann zu kollisionen kommen!
			mids.add(e.getLeft().getId() +"_"
					+ e.getRight().getId());
		}

		final Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		Directory directory = null;
		try {
			directory = new RAMDirectory();
			try {
				final IndexWriter writer = new IndexWriter(directory, analyzer,
						true, IndexWriter.MaxFieldLength.UNLIMITED);

				for (final IObjectInstance oi : oiLeft) {
					final Document document = new Document();
					document.add(new Field("id", "1_" + oi.getId(),
							Field.Store.YES, Field.Index.NOT_ANALYZED,
							Field.TermVector.NO));
					String value = oi.getStringValue(attrLinks);
					if (value == null)
						value = "";
					document.add(new Field("content", value, Field.Store.YES,
							Field.Index.ANALYZED,
							Field.TermVector.WITH_POSITIONS_OFFSETS));

					writer.addDocument(document);
				}

				for (final IObjectInstance oi : oiRight) {
					final Document document = new Document();
					document.add(new Field("id", "2_" + oi.getId(),
							Field.Store.YES, Field.Index.NOT_ANALYZED,
							Field.TermVector.NO));
					String value = oi.getStringValue(attrRechts);
					if (value == null)
						value = "";
					document.add(new Field("content", value, Field.Store.YES,
							Field.Index.ANALYZED,
							Field.TermVector.WITH_POSITIONS_OFFSETS));

					writer.addDocument(document);
				}
				writer.optimize();
				writer.close();
			} catch (final Exception e) {
				log.info(e);
			}
		} catch (final Exception e) {
			log.info(e);
		}

		IndexReader reader = null;
		try {
			reader = IndexReader.open(directory, true);

			final TermInfo termInfo = new AdaptedCachedTermInfo(reader, "content", 1,
					1.0);

			final VectorMapper mapper = new TFDFMapper(reader, new TFIDF(), termInfo);

			final HashMap<String, Vector> vectors = new HashMap<String, Vector>();

			final TermDocs termDocs = reader.termDocs(null);

			while (termDocs.next()) {
				final int doc = termDocs.doc();

				reader.getTermFreqVector(doc, "content", mapper);
				mapper.setDocumentNumber(doc);

				Vector result = mapper.getVector();
				result = result.normalize(2);
				final Document document = reader.document(doc);
				vectors.put(document.get("id"), result);
			}

			final CosineDistanceMeasure measure = new CosineDistanceMeasure();
			for (final IObjectInstance oi1 : oiLeft) {
				final String id1 = "1_" + oi1.getId();
				final Vector v1 = vectors.get(id1);
				for (final IObjectInstance oi2 : oiRight) {
					final String id2 = "2_" + oi2.getId();
					final Vector v2 = vectors.get(id2);
					double sim = 0;
					final double dist = measure.distance(v1, v2);
					if (!Double.isNaN(dist))
						sim = 1.0 - dist;
					if (sim >= threshold
							&& mids.contains(oi1.getId() + "_"
									+ oi2.getId())) {
						mrs.add(oi1, oi2, new Similarity(sim));
					}

				}
			}
		} catch (final CorruptIndexException e) {
			log.error(e, e);
		} catch (final IOException e) {
			log.error(e, e);
		} // only searching, so read-only=true

		log.info("Match Ended.");
	}
}