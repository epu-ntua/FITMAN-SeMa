package de.wdilab.ml.impl.matcher.simple.strongweaksim;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

public class WeakSim {
	protected static final Logger log = Logger.getLogger(WeakSim.class);

	/**
	 * Computes cosine similarity for qgrams
	 * 
	 * @param min
	 *            minimal threshold
	 * @return Map (id1~#~id2 -> sim)
	 */
	public static HashMap<String, Double> cosine(
			HashMap<String, HashMap<String, Double>>[] qgram, double min) {

		// System.out.print("Creating cosine for " + qgram[0].size() +
		// " pairs ... ");

		HashMap<String, Double> result = new HashMap<String, Double>();
		@SuppressWarnings("unchecked")
		HashMap<String, Double>[] q = new HashMap[2];
		double[] size = new double[2];
		for (String idX : qgram[0].keySet()) {
			q[0] = qgram[0].get(idX);
			size[0] = 0;
			for (double d : q[0].values())
				size[0] += d * d;
			for (String idY : qgram[1].keySet()) {
				q[1] = qgram[1].get(idY);
				size[1] = 0;
				for (double d : q[1].values())
					size[1] += d * d;

				double overlap = 0d;
				Double[] w = new Double[2];
				for (String o : q[0].keySet()) {
					if ((w[1] = q[1].get(o)) == null)
						continue;
					w[0] = q[0].get(o);
					overlap += w[0] * w[1];
				}

				double sim = overlap / Math.sqrt(size[0] * size[1]);
				if (sim >= min) {
					result.put(idX + StrongWeakSim.concat + idY, sim);
				}
			}

		}

		// System.out.println("done.");
		return result;
	}

	/**
	 * Standard Q-Gram
	 * 
	 * @param lucene
	 *            Lucene Dir[]
	 * @param q
	 *            qgram size (e.g., 3)
	 * @return Map[] (id -> ( qgram -> weights ))
	 * @throws Exception
	 */
	public static HashMap<String, HashMap<String, Double>>[] createQGramsSimple(
			Directory lucene[], int q) throws Exception {

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, Double>>[] result = new HashMap[2];

		// System.out.print("Creating qgrams ... ");

		for (int x = 0; x < 2; x++) {

			result[x] = new HashMap<String, HashMap<String, Double>>();
			IndexReader reader = new IndexSearcher(lucene[x]).getIndexReader();
			for (int i = 0; i < reader.numDocs(); i++) {

				HashMap<String, Double> qgrams = new HashMap<String, Double>();

				StringBuffer buf = new StringBuffer();
				for (int j = 0; j < q - 1; j++)
					buf.append("#");
				buf.append(reader.document(i).getField("value").stringValue());
				for (int j = 0; j < q - 1; j++)
					buf.append("#");

				for (int j = 0; j < buf.length() - q; j++) {
					qgrams.put(buf.substring(j, j + q), 1d);
				}

				result[x].put(reader.document(i).getField("id").stringValue(),
						qgrams);
			}
		}

		// System.out.println("done.");
		return result;
	}

	/**
	 * Create Q-Gram for weak similarity
	 * 
	 * @param lucene
	 *            Lucene Dir[]
	 * @param q
	 *            qgram size (e.g., 3)
	 * @return Map[] (id -> ( qgram -> weights ))
	 * @throws Exception
	 */
	public static HashMap<String, HashMap<String, Double>>[] createQGramsWeak(
			Directory lucene[], int q) throws Exception {

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, Double>>[] result = new HashMap[2];
		System.out.print("Creating qgrams ... ");

		for (int x = 0; x < 2; x++) {

			log.info(x);
			result[x] = new HashMap<String, HashMap<String, Double>>();
			IndexReader reader = new IndexSearcher(lucene[x]).getIndexReader();

			for (int i = 0; i < reader.numDocs(); i++) {

				HashMap<String, Double> qgrams = new HashMap<String, Double>();
				TermPositionVector v = (TermPositionVector) reader
						.getTermFreqVector(i, "value");

				if (v != null) {
					for (int k = 0; k < v.getTerms().length; k++) {
						addQGrams(qgrams, v.getTerms()[k], reader, q);
					}
				}
				result[x].put(reader.document(i).get("id"), qgrams);
			}
		}

		System.out.println("done.");
		return result;
	}

	/**
	 * Helper method for adding qgrams
	 * 
	 * @param qgrams
	 *            Map (QGram -> Weight)
	 * @param term
	 * @param reader
	 * @param q
	 *            qgram size
	 * @throws Exception
	 */
	public static void addQGrams(HashMap<String, Double> qgrams, String term,
			IndexReader reader, int q) throws Exception {

		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < q - 1; j++)
			buf.append("#");
		buf.append(term);
		for (int j = 0; j < q - 1; j++)
			buf.append("#");

		for (int j = 0; j < buf.length() - q; j++) {
			Double w = qgrams.get(buf.substring(j, j + q));
			double w2 = Math.log(((double) reader.numDocs())
					/ ((double) reader.docFreq(new Term("value", term))));
			qgrams.put(buf.substring(j, j + q), w == null ? w2 : w2 + w);
		}

	}

}
