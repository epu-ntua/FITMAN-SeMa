/**
 * 
 */
package de.wdilab.ml.impl.matcher.simple.strongweaksim;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * @author koepcke1
 * 
 */
public class StrongWeakSim extends AbstractSimpleAttributeObjectMatcher {
	protected static final Logger log = Logger.getLogger(StrongWeakSim.class);
	final static String concat = "~#~";
	private Directory[] index;
	private IndexWriter[] indexWriter;
	int q = 3; // q-gram size
	double t_min = 0.5d; // minimal threshold
	double jaccard = 0.9d; // Jaccard Sim for matching words
	double alpha = 0.1d; // minimal ratio for pattern distribution

	/**
	 * @param attr1
	 * @param attr2
	 */
	public StrongWeakSim(String attr1, String attr2) {
		super(attr1, attr2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param attr1
	 */
	public StrongWeakSim(String attr1) {
		super(attr1);
		index = new Directory[2];
		indexWriter = new IndexWriter[2];
		for (int x = 0; x < 2; x++) {
			index[x] = new RAMDirectory();
			try {
				indexWriter[x] = new IndexWriter(index[x],
						new StandardAnalyzer(Version.LUCENE_30), true,
						new IndexWriter.MaxFieldLength(10));
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LockObtainFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	public StrongWeakSim() {
		index = new Directory[2];
		indexWriter = new IndexWriter[2];
		for (int x = 0; x < 2; x++) {
			index[x] = new RAMDirectory();
			try {
				indexWriter[x] = new IndexWriter(index[x],
						new StandardAnalyzer(Version.LUCENE_30), true,
						new IndexWriter.MaxFieldLength(10));
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LockObtainFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param attr1
	 * @param threshold
	 */
	public StrongWeakSim(String attr1, float threshold) {
		super(attr1, threshold);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param attr1
	 * @param attr2
	 * @param threshold
	 */
	public StrongWeakSim(String attr1, String attr2, float threshold) {
		super(attr1, attr2, threshold);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
	 * .oi.IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.oi.IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(IObjectInstanceProvider oip1,
			IObjectInstanceProvider oip2, IMappingStore mrs)
			throws MappingStoreException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
	 * .oi.IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(IObjectInstanceProvider oip, IMappingStore mrs)
			throws MappingStoreException {
		try {
			this.indexData(oip);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			log.info("\n------------------------\nWeak Similarity for q=" + q);
			HashMap<String, Double> match_weak = WeakSim.cosine(
					WeakSim.createQGramsWeak(index, q), t_min);

			log.info("\n------------------------\nStrong Similarity for q=" + q);
			HashMap<String, Double> match_strong = StrongSim.strongSim(index, match_weak, t_min, q, jaccard, alpha);
			for (String k: match_strong.keySet()) {
				String id[] = k.split(StrongWeakSim.concat);
				mrs.add(oip.getInstance(id[0]), oip.getInstance(id[1]), new Similarity(match_strong.get(k)));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
	 * .mapping.IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(IMappingProvider mp, IMappingStore mrs)
			throws MappingStoreException {
		// TODO Auto-generated method stub

	}

	public void indexData(IObjectInstanceProvider oip) throws Exception {

		log.info("Indexing ... ");

		for (final IObjectInstance oi : oip) {
			Document doc = new Document();
			doc.add(new Field("id", oi.getId(), Field.Store.YES,
					Field.Index.NOT_ANALYZED, Field.TermVector.NO));
			String value = oi.getStringValue(this.attrLinks);
			if (value == null)
				value = "";
			doc.add(new Field("value",value,
					Field.Store.YES, Field.Index.ANALYZED,
					Field.TermVector.WITH_POSITIONS_OFFSETS));
			indexWriter[0].addDocument(doc);
			indexWriter[1].addDocument(doc);
		}
		indexWriter[0].optimize();
		indexWriter[0].close();
		
		indexWriter[1].optimize();
		indexWriter[1].close();

		log.info("done.");

	}

}
