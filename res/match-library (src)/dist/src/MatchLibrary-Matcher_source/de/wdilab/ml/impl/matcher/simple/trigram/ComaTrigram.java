/**
 * 
 */
package de.wdilab.ml.impl.matcher.simple.trigram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.ifuice.utils.StringSimilarity;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Trigram matcher from iFuice.
 * <p>
 * Note: Does NOT keep the total amount of <code>IObjectInstance</code>s in main
 * memory. Instead of this it keeps the index->ID. ObjectInstanceProvider (OIP)
 * should implement getInstance fastly! hasCapability(
 * EOIProviderCapabilityHint.FAST_GET_INSTANCE) should be TRUE.
 * </p>
 * 
 * @author Nico Heller
 */
public class ComaTrigram extends AbstractSimpleAttributeObjectMatcher implements
		IAttributeObjectMatcher {

	protected static final Logger log = Logger.getLogger(ComaTrigram.class);

	/**
	 * @param attr1
	 * @param attr2
	 */
	public ComaTrigram(final String attr1, final String attr2) {
		super(attr1, attr2, 0f);
	}

	/**
	 * @param attr
	 */
	public ComaTrigram(final String attr) {
		super(attr, 0f);
	}

	/**
   * 
   */
	public ComaTrigram() {
		super();
	}

	/**
	 * @param attr1
	 * @param attr2
	 * @param threshold
	 */
	public ComaTrigram(final String attr1, final String attr2,
			final float threshold) {
		super(attr1, attr2, threshold);
	}

	/**
	 * @param attr
	 * @param threshold
	 */
	public ComaTrigram(final String attr, final float threshold) {
		super(attr, threshold);
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
		Directory directoryLeft = null;
		Directory directoryRight = null;
		try {
			directoryLeft = new RAMDirectory();
			directoryRight = new RAMDirectory();
			try {
				final IndexWriter writerLeft = new IndexWriter(directoryLeft,
						analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);

				for (final IObjectInstance oi : oip1) {
					final Document document = new Document();
					document.add(new Field("id", oi.getId(), Field.Store.YES,
							Field.Index.NOT_ANALYZED, Field.TermVector.NO));
					String value = oi.getStringValue(attrLinks);
					if (value == null)
						value = "";
					
					document.add(new Field("content", value, Field.Store.YES,
							Field.Index.ANALYZED,
							Field.TermVector.WITH_POSITIONS_OFFSETS));

					writerLeft.addDocument(document);
				}
				writerLeft.optimize();
				writerLeft.close();
				final IndexWriter writerRight = new IndexWriter(directoryRight,
						analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
				for (final IObjectInstance oi : oip2) {
					final Document document = new Document();
					document.add(new Field("id", oi.getId(), Field.Store.YES,
							Field.Index.NOT_ANALYZED, Field.TermVector.NO));
					String value = oi.getStringValue(attrRechts);
					if (value == null)
						value = "";
					document.add(new Field("content", value, Field.Store.YES,
							Field.Index.ANALYZED,
							Field.TermVector.WITH_POSITIONS_OFFSETS));

					writerRight.addDocument(document);
				}
				writerRight.optimize();
				writerRight.close();
			} catch (final Exception e) {
				log.info(e);
			}
		} catch (final Exception e) {
			log.info(e);
		}

		IndexReader readerLeft = null;
		IndexReader readerRight = null;

		try {
			readerLeft = IndexReader.open(directoryLeft, true);
			readerRight = IndexReader.open(directoryRight, true);
			final ArrayList<String[]> leftnGrams = new ArrayList<String[]>();
			final ArrayList<int[]> leftnGramsId = new ArrayList<int[]>();
			final ArrayList<String> leftStrings = new ArrayList<String>();

			String string_left;
			int left = 0;
			TermEnum termEnumLeft = readerLeft.terms();

			HashMap<String, Vector<Integer>> tLeft = new HashMap<String, Vector<Integer>>();

			while (termEnumLeft.next()) {
				Term term = termEnumLeft.term();

				if (term.field().equals("content")) {

					TermDocs termDocs = readerLeft.termDocs(term);
					while (termDocs.next()) {
						int doc = termDocs.doc();
						Document document = readerLeft.document(doc);
						String id = document.get("id");
						if (tLeft.containsKey(id)) {
							Vector<Integer> t = tLeft.get(id);
							t.add(left);
							tLeft.put(id, t);
						} else {
							
							Vector<Integer> t = new Vector<Integer>();
							t.add(left);
							tLeft.put(id, t);
						}
					}
					string_left = term.text();
					if (string_left != null) {
						string_left = string_left.toUpperCase();
						leftStrings.add(string_left);
						final String trgs[] = StringSimilarity.generateNGrams(
								string_left, 3);
						leftnGrams.add(trgs);
						leftnGramsId
								.add(StringSimilarity.generateNGramId(trgs));
					} else {
						leftStrings.add(NULL);
						leftnGrams.add(NULL_TRGS);
						leftnGramsId.add(NULL_IDS);
					}

					left++;
				}

			}
			termEnumLeft.close();

			final ArrayList<String[]> rightnGrams = new ArrayList<String[]>();
			final ArrayList<int[]> rightnGramsId = new ArrayList<int[]>();
			final ArrayList<String> rightStrings = new ArrayList<String>();

			String string_right;
			int right = 0;
			TermEnum termEnumRight = readerRight.terms();
			HashMap<String, Vector<Integer>> tRight = new HashMap<String, Vector<Integer>>();
			while (termEnumRight.next()) {
				Term term = termEnumRight.term();
				if (term.field().equals("content")) {
					TermDocs termDocs = readerRight.termDocs(term);
					while (termDocs.next()) {
						int doc = termDocs.doc();
						Document document = readerRight.document(doc);
						String id = document.get("id");
						if (tRight.containsKey(id)) {
							Vector<Integer> t = tRight.get(id);
							t.add(right);
							tRight.put(id, t);
						} else {
							Vector<Integer> t = new Vector<Integer>();
							t.add(right);
							tRight.put(id, t);
						}
					}
					string_right = term.text();
					if (string_right != null) {
						string_right = string_right.toUpperCase();
						rightStrings.add(string_right);
						final String trgs[] = StringSimilarity.generateNGrams(
								string_right, 3);
						rightnGrams.add(trgs);
						rightnGramsId.add(StringSimilarity
								.generateNGramId(trgs));
					} else {
						rightStrings.add(NULL);
						rightnGrams.add(NULL_TRGS);
						rightnGramsId.add(NULL_IDS);
					}

					right++;
				}

			}
			termEnumRight.close();

			final int inputSize = leftStrings.size();
			final int outputSize = rightStrings.size();
			float[][] simMatrix = new float[inputSize][outputSize];

			for (left = 0; left < inputSize; left++) {
				final String left_string = leftStrings.get(left);
				if (left_string == NULL)
					continue;

				final int[] leftnGramsIds = leftnGramsId.get(left);

				for (right = 0; right < outputSize; right++) {
					final String right_string = rightStrings.get(right);
					if (right_string == NULL)
						continue;

					final float sim = StringSimilarity.computeNGramSimilarity(
							left_string, right_string, 3, leftnGramsIds,
							rightnGramsId.get(right));

					if (sim > 0) {

						simMatrix[left][right] = sim;

					}

				}
			}

			for (final IObjectInstance oiLeft : oip1) {
				String idLeft = oiLeft.getId();

				
				Vector<Integer> tL = tLeft.get(idLeft);
				int sL = tL.size();

				for (final IObjectInstance oiRight : oip2) {

					String idRight = oiRight.getId();
					Vector<Integer> tR = tRight.get(idRight);
					int sR = tR.size();
					double sim = 0;

					for (int i = 0; i < sL; i++) {
						int l = tL.get(i);
						double maxR = 0;
						for (int j = 0; j < sR; j++) {
							int r = tR.get(j);
							double s = simMatrix[l][r];
							if (s > maxR) {

								maxR = s;
							}

						}
						sim += maxR;
					}

					for (int i = 0; i < sR; i++) {
						int r = tR.get(i);
						double maxS = 0;
						for (int j = 0; j < sL; j++) {
							int l = tL.get(j);
							double s = simMatrix[l][r];
							if (s > maxS) {

								maxS = s;
							}

						}
						sim += maxS;
					}

					sim = sim / ((double) (sL + sR));

					if (sim > threshold) {
						mrs.add(oiLeft, oiRight, new Similarity(sim));
					}
				}
			}

		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void match(final IObjectInstanceProvider oip, final IMappingStore mrs)
			throws MappingStoreException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/**
   * 
   */
	public static final String NGRAM_KEY = "ifuice_ngram_key";

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
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public static float computeSetSimilarity(float[][] simMatrix,
			float threshold) {
		if (simMatrix == null)
			return 0;
		int m = simMatrix.length;
		if (m == 0)
			return 0;
		int n = simMatrix[0].length;
		if (n == 0)
			return 0;
		float sim = 0;

		float maxSim_i = 0, maxSim_j = 0;
		float sumSim_i = 0, sumSim_j = 0;
		for (int i = 0; i < m; i++) {
			maxSim_i = 0;
			for (int j = 0; j < n; j++)
				if (maxSim_i < simMatrix[i][j])
					maxSim_i = simMatrix[i][j];
			sumSim_i += maxSim_i;
		}
		for (int j = 0; j < n; j++) {
			maxSim_j = 0;
			for (int i = 0; i < m; i++)
				if (maxSim_j < simMatrix[i][j])
					maxSim_j = simMatrix[i][j];
			sumSim_j += maxSim_j;
		}
		sim = (sumSim_i + sumSim_j) / (m + n);

		return sim;
	}

	/**
	 * A <code>NULL</code>-String
	 */
	public static final String NULL = "<NULL>";

	protected static final String[] NULL_TRGS = new String[0];

	protected static final int[] NULL_IDS = new int[0];
}
