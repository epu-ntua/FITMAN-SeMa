/**
 * 
 */
package de.wdilab.ml.impl.matcher.simple.trigram;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.log4j.Logger;

import de.ifuice.utils.StringSimilarity;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.impl.util.tokenizer.ComaStringTokenizer;
import de.wdilab.ml.impl.util.tokenizer.IStringTokenizer;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Trigram matcher based on the implementation in COMA++ from Hong Hai Do
 * 
 * @author Do, Koepcke
 */
public class ComaTrigram2 extends AbstractSimpleAttributeObjectMatcher
		implements IAttributeObjectMatcher {

	protected static final Logger log = Logger.getLogger(ComaTrigram2.class);
	private IStringTokenizer tokenizer;
	ArrayList<String> wordList = null, synonymList = null;
	private ArrayList<ArrayList<String>> synonymPairList = new ArrayList<ArrayList<String>>();
	public void setTokenizer(IStringTokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public ComaTrigram2(String attr1, String attr2, IStringTokenizer tokenizer) {
		super(attr1, attr2, 0f);
		this.tokenizer = tokenizer;
	}

	/**
	 * @param attr1
	 * @param attr2
	 */
	public ComaTrigram2(final String attr1, final String attr2) {
		super(attr1, attr2, 0f);
		this.tokenizer = new ComaStringTokenizer();
	}

	/**
	 * @param attr
	 */
	public ComaTrigram2(final String attr) {
		super(attr, 0f);
		this.tokenizer = new ComaStringTokenizer();
	}

	/**
   * 
   */
	public ComaTrigram2() {
		super();
		this.tokenizer = new ComaStringTokenizer();
	}

	/**
	 * @param attr1
	 * @param attr2
	 * @param threshold
	 */
	public ComaTrigram2(final String attr1, final String attr2,
			final float threshold) {
		super(attr1, attr2, threshold);
		this.tokenizer = new ComaStringTokenizer();
	}
	
	public ComaTrigram2(final String attr1, final String attr2,
			final float threshold, ArrayList<String> wordList, ArrayList<String> synonymList) {
		super(attr1, attr2, threshold);
		this.tokenizer = new ComaStringTokenizer();
		if (wordList !=null && synonymList!=null){
			this.wordList = wordList;
			this.synonymList = synonymList;
			
			for (int i = 0; i < wordList.size(); i++) {
	          ArrayList<String> syn = new ArrayList<String>();
	          syn.add(wordList.get(i));
	          syn.add(synonymList.get(i));
	          synonymPairList.add(syn);
			}
			
		}

	}
	

	/**
	 * @param attr
	 * @param threshold
	 */
	public ComaTrigram2(final String attr, final float threshold) {
		super(attr, threshold);
		this.tokenizer = new ComaStringTokenizer();
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
		ArrayList<ArrayList<Object>> aComps = new ArrayList<ArrayList<Object>>();
		IObjectInstance[] idsLeft = new IObjectInstance[oip1.size()];
		final ArrayList<String[]> leftnGrams = new ArrayList<String[]>();
		final ArrayList<int[]> leftnGramsId = new ArrayList<int[]>();
		final ArrayList<Object> leftStrings = new ArrayList<Object>();
		int left = 0;
		int ind = 0;
		for (final IObjectInstance oi : oip1) {
			idsLeft[ind++] = oi;
			String value = oi.getStringValue(attrLinks);
			tokenizer.reset(value);
			String[] tokens = tokenizer.toArray();
			ArrayList<Object> a = new ArrayList<Object>();
			for (int i = 0; i < tokens.length; i++) {
				String string_left = tokens[i];
				if (string_left != null) {
					// string_left = string_left.toUpperCase();
					if (!leftStrings.contains(string_left)) {
						leftStrings.add(string_left);
						final String trgs[] = StringSimilarity.generateNGrams(
								string_left, 3);
						leftnGrams.add(trgs);
						leftnGramsId
								.add(StringSimilarity.generateNGramId(trgs));
						left++;
					}
						a.add(string_left);
					
				} else {
					if (!leftStrings.contains(NULL)) {
						leftStrings.add(NULL);
						leftnGrams.add(NULL_TRGS);
						leftnGramsId.add(NULL_IDS);
						left++;
					}
				}
				
			}
			aComps.add(a);

		}
		ArrayList<ArrayList<Object>> bComps = new ArrayList<ArrayList<Object>>();
		IObjectInstance[] idsRight = new IObjectInstance[oip2.size()];
		final ArrayList<String[]> rightnGrams = new ArrayList<String[]>();
		final ArrayList<int[]> rightnGramsId = new ArrayList<int[]>();
		final ArrayList<Object> rightStrings = new ArrayList<Object>();
		int right = 0;
		ind = 0;
		for (final IObjectInstance oi : oip2) {
			idsRight[ind++] = oi;
			String value = oi.getStringValue(attrRechts);
			tokenizer.reset(value);
			String[] tokens = tokenizer.toArray();
			ArrayList<Object> b = new ArrayList<Object>();
			for (int i = 0; i < tokens.length; i++) {
				String string_right = tokens[i];
				if (string_right != null) {
					// string_right = string_right.toUpperCase();
					if (!rightStrings.contains(string_right)) {
						rightStrings.add(string_right);
						final String trgs[] = StringSimilarity.generateNGrams(
								string_right, 3);
						rightnGrams.add(trgs);
						rightnGramsId.add(StringSimilarity
								.generateNGramId(trgs));
						right++;
					}
					b.add(string_right);
				} else {
					if (!rightStrings.contains(NULL)) {
						rightStrings.add(NULL);
						rightnGrams.add(NULL_TRGS);
						rightnGramsId.add(NULL_IDS);
						right++;
					}
				}

				

			}
			bComps.add(b);

		}

		final int inputSize = leftStrings.size();
		final int outputSize = rightStrings.size();
		float[][] simMatrix = new float[inputSize][outputSize];

		for (left = 0; left < inputSize; left++) {
			final String left_string = (String) leftStrings.get(left);
			if (left_string == NULL)
				continue;

			final int[] leftnGramsIds = leftnGramsId.get(left);

			for (right = 0; right < outputSize; right++) {
				final String right_string = (String) rightStrings.get(right);
				if (right_string == NULL)
					continue;

				float sim = StringSimilarity.computeNGramSimilarity(
						left_string, right_string, 3, leftnGramsIds,
						rightnGramsId.get(right));

				// use synonyms (works only for 1:1 token synonyms)
				if (wordList!=null){
					float synonymSim = 0;
				      if (isDirectSynonym(left_string, right_string)) synonymSim = (float) 1.0;
				      else if (isInheritedSynonym(left_string, right_string)) synonymSim = (float)0.8;
					if (synonymSim>sim){
						// use only if synonym similarity is larger than trigram similarity
						sim=synonymSim;
					}
				}
				
				if (sim > 0) {

					simMatrix[left][right] = sim;
					

				}

			}
		}

		log.info("start");
		this.atomicToCompositeSimMatrix(leftStrings, rightStrings, simMatrix,
				aComps, bComps, idsLeft, idsRight, mrs);

	}

	@Override
	public void match(final IObjectInstanceProvider oip, final IMappingStore mrs)
			throws MappingStoreException {
		log.info("Match Start.");

		ArrayList<ArrayList<Object>> aComps = new ArrayList<ArrayList<Object>>();
		IObjectInstance[] idsLeft = new IObjectInstance[oip.size()];
		final ArrayList<String[]> leftnGrams = new ArrayList<String[]>();
		final ArrayList<int[]> leftnGramsId = new ArrayList<int[]>();
		final ArrayList<Object> leftStrings = new ArrayList<Object>();
		int left = 0;
		int ind = 0;
		for (final IObjectInstance oi : oip) {
			idsLeft[ind++] = oi;
			String value = oi.getStringValue(attrLinks);
			tokenizer.reset(value);
			String[] tokens = tokenizer.toArray();
			ArrayList<Object> a = new ArrayList<Object>();
			for (int i = 0; i < tokens.length; i++) {
				String string_left = tokens[i];
				if (string_left != null) {
					// string_left = string_left.toUpperCase();
					leftStrings.add(string_left);
					final String trgs[] = StringSimilarity.generateNGrams(
							string_left, 3);
					leftnGrams.add(trgs);
					leftnGramsId.add(StringSimilarity.generateNGramId(trgs));
					a.add(string_left);
				} else {
					leftStrings.add(NULL);
					leftnGrams.add(NULL_TRGS);
					leftnGramsId.add(NULL_IDS);
				}
				left++;
			}
			aComps.add(a);

		}
		ArrayList<ArrayList<Object>> bComps = new ArrayList<ArrayList<Object>>();
		IObjectInstance[] idsRight = new IObjectInstance[oip.size()];
		final ArrayList<String[]> rightnGrams = new ArrayList<String[]>();
		final ArrayList<int[]> rightnGramsId = new ArrayList<int[]>();
		final ArrayList<Object> rightStrings = new ArrayList<Object>();
		int right = 0;
		ind = 0;
		for (final IObjectInstance oi : oip) {
			idsRight[ind++] = oi;
			String value = oi.getStringValue(attrRechts);
			tokenizer.reset(value);
			String[] tokens = tokenizer.toArray();
			ArrayList<Object> b = new ArrayList<Object>();
			for (int i = 0; i < tokens.length; i++) {
				String string_right = tokens[i];
				if (string_right != null) {
					// string_right = string_right.toUpperCase();
					rightStrings.add(string_right);
					final String trgs[] = StringSimilarity.generateNGrams(
							string_right, 3);
					rightnGrams.add(trgs);
					rightnGramsId.add(StringSimilarity.generateNGramId(trgs));
					b.add(string_right);
				} else {
					rightStrings.add(NULL);
					rightnGrams.add(NULL_TRGS);
					rightnGramsId.add(NULL_IDS);
				}

				right++;

			}
			bComps.add(b);

		}

		final int inputSize = leftStrings.size();
		final int outputSize = rightStrings.size();
		float[][] simMatrix = new float[inputSize][outputSize];

		for (left = 0; left < inputSize; left++) {
			final String left_string = (String) leftStrings.get(left);
			if (left_string == NULL)
				continue;

			final int[] leftnGramsIds = leftnGramsId.get(left);

			for (right = 0; right < outputSize; right++) {
				final String right_string = (String) rightStrings.get(right);
				if (right_string == NULL)
					continue;

				float sim = StringSimilarity.computeNGramSimilarity(
						left_string, right_string, 3, leftnGramsIds,
						rightnGramsId.get(right));

				// use synonyms (works only for 1:1 token synonyms)
				if (wordList!=null){
					float synonymSim = 0;
				      if (isDirectSynonym(left_string, right_string)) synonymSim = (float) 1.0;
				      else if (isInheritedSynonym(left_string, right_string)) synonymSim = (float)0.8;
					if (synonymSim>sim){
						// use only if synonym similarity is larger than trigram similarity
						sim=synonymSim;
					}
				}
				if (sim > 0) {

					simMatrix[left][right] = sim;

				}

			}
		}

		log.info("start");
		this.atomicToCompositeSimMatrix(leftStrings, rightStrings, simMatrix,
				aComps, bComps, idsLeft, idsRight, mrs);

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
		log.info("Match Start.");
		HashSet<IObjectInstance> oiLeft = new HashSet<IObjectInstance>();
		HashSet<IObjectInstance> oiRight = new HashSet<IObjectInstance>();
		HashSet<Integer> mids = new HashSet<Integer>();
		for (IMappingEntry e : mp) {
			oiLeft.add(e.getLeft());
			oiRight.add(e.getRight());
			mids.add(e.getLeft().getId().hashCode()
					+ e.getRight().getId().hashCode());
		}

		ArrayList<ArrayList<Object>> aComps = new ArrayList<ArrayList<Object>>();
		IObjectInstance[] idsLeft = new IObjectInstance[oiRight.size()];
		final ArrayList<String[]> leftnGrams = new ArrayList<String[]>();
		final ArrayList<int[]> leftnGramsId = new ArrayList<int[]>();
		final ArrayList<Object> leftStrings = new ArrayList<Object>();
		int left = 0;
		int ind = 0;
		for (final IObjectInstance oi : oiLeft) {
			idsLeft[ind++] = oi;
			String value = oi.getStringValue(attrLinks);
			tokenizer.reset(value);
			String[] tokens = tokenizer.toArray();
			ArrayList<Object> a = new ArrayList<Object>();
			for (int i = 0; i < tokens.length; i++) {
				String string_left = tokens[i];
				if (string_left != null) {
					// string_left = string_left.toUpperCase();
					leftStrings.add(string_left);
					final String trgs[] = StringSimilarity.generateNGrams(
							string_left, 3);
					leftnGrams.add(trgs);
					leftnGramsId.add(StringSimilarity.generateNGramId(trgs));
					a.add(string_left);
				} else {
					leftStrings.add(NULL);
					leftnGrams.add(NULL_TRGS);
					leftnGramsId.add(NULL_IDS);
				}
				left++;
			}
			aComps.add(a);

		}
		ArrayList<ArrayList<Object>> bComps = new ArrayList<ArrayList<Object>>();
		IObjectInstance[] idsRight = new IObjectInstance[oiRight.size()];
		final ArrayList<String[]> rightnGrams = new ArrayList<String[]>();
		final ArrayList<int[]> rightnGramsId = new ArrayList<int[]>();
		final ArrayList<Object> rightStrings = new ArrayList<Object>();
		int right = 0;
		ind = 0;
		for (final IObjectInstance oi : oiRight) {
			idsRight[ind++] = oi;
			String value = oi.getStringValue(attrRechts);
			tokenizer.reset(value);
			String[] tokens = tokenizer.toArray();
			ArrayList<Object> b = new ArrayList<Object>();
			for (int i = 0; i < tokens.length; i++) {
				String string_right = tokens[i];
				if (string_right != null) {
					// string_right = string_right.toUpperCase();
					rightStrings.add(string_right);
					final String trgs[] = StringSimilarity.generateNGrams(
							string_right, 3);
					rightnGrams.add(trgs);
					rightnGramsId.add(StringSimilarity.generateNGramId(trgs));
					b.add(string_right);
				} else {
					rightStrings.add(NULL);
					rightnGrams.add(NULL_TRGS);
					rightnGramsId.add(NULL_IDS);
				}

				right++;

			}
			bComps.add(b);

		}

		final int inputSize = leftStrings.size();
		final int outputSize = rightStrings.size();
		float[][] simMatrix = new float[inputSize][outputSize];

		for (left = 0; left < inputSize; left++) {
			final String left_string = (String) leftStrings.get(left);
			if (left_string == NULL)
				continue;

			final int[] leftnGramsIds = leftnGramsId.get(left);

			for (right = 0; right < outputSize; right++) {
				final String right_string = (String) rightStrings.get(right);
				if (right_string == NULL)
					continue;

				float sim = StringSimilarity.computeNGramSimilarity(
						left_string, right_string, 3, leftnGramsIds,
						rightnGramsId.get(right));
				// use synonyms (works only for 1:1 token synonyms)
				if (wordList!=null){
					float synonymSim = 0;
				      if (isDirectSynonym(left_string, right_string)) synonymSim = (float) 1.0;
				      else if (isInheritedSynonym(left_string, right_string)) synonymSim = (float)0.8;
					if (synonymSim>sim){
						// use only if synonym similarity is larger than trigram similarity
						sim=synonymSim;
					}
				}
				if (sim > 0) {

					simMatrix[left][right] = sim;

				}

			}
		}

		log.info("start");
		this.atomicToCompositeSimMatrix(leftStrings, rightStrings, simMatrix,
				aComps, bComps, idsLeft, idsRight, mrs, mids);

	}

	public void atomicToCompositeSimMatrix(ArrayList<Object> aAtoms,
			ArrayList<Object> bAtoms, float[][] atomSimMatrix,
			ArrayList<ArrayList<Object>> aComps,
			ArrayList<ArrayList<Object>> bComps, IObjectInstance[] aIds,
			IObjectInstance[] bIds, final IMappingStore mrs,
			HashSet<Integer> mids) {
		if (aAtoms == null || bAtoms == null || aComps == null
				|| bComps == null)
			return;

		boolean verbose = false;

		int aAtomCnt = aAtoms.size();
		int bAtomCnt = bAtoms.size();
		int aCompCnt = aComps.size();
		int bCompCnt = bComps.size();

//		// Allocate simMatrix for composite objects
//		float[][] compSimMatrix = new float[aCompCnt][bCompCnt];

		long start, end;
		int p, q;
		// Compute combined sim if specified

		if (verbose)
			System.out.println("atomicToCompositeSimMatrix(): ");

		// Index paths
		start = System.currentTimeMillis();
		int[][] aInd = new int[aCompCnt][];
		int[][] bInd = new int[bCompCnt][];
		for (int i = 0; i < aCompCnt; i++) {
			ArrayList<Object> aComp = aComps.get(i);
			if (aComp == null) {
				aInd[i] = null;
				continue;
			}
			int aSize = aComp.size();
			aInd[i] = new int[aSize];
			for (int j = 0; j < aSize; j++) {
				aInd[i][j] = aAtoms.indexOf(aComp.get(j));
			}
		}
		for (int i = 0; i < bCompCnt; i++) {
			ArrayList<Object> bComp = bComps.get(i);
			if (bComp == null) {
				bInd[i] = null;
				continue;
			}
			int bSize = bComp.size();
			bInd[i] = new int[bSize];
			for (int j = 0; j < bSize; j++) {
				bInd[i][j] = bAtoms.indexOf(bComp.get(j));
			}
		}
		end = System.currentTimeMillis();
		if (verbose)
			System.out.println("List: Indexing of " + aAtomCnt + "/" + aCompCnt
					+ "-" + bAtomCnt + "/" + bCompCnt + ": "
					+ (float) (end - start) / 1000);

		// Compute path similarity. Note: Most of the time spent here, further
		// optimize computeSetSimilarity()?
		start = System.currentTimeMillis();
		for (int i = 0; i < aCompCnt; i++) {
			if (aInd[i] == null)
				continue;
			int aSize = aInd[i].length;
			for (int j = 0; j < bCompCnt; j++) {
				if (bInd[j] == null)
					continue;
				int bSize = bInd[j].length;
				float[][] simMatrix = new float[aSize][bSize];
				for (int m = 0; m < aSize; m++) {
					p = aInd[i][m];
					if (p == -1)
						continue;
					for (int n = 0; n < bSize; n++) {
						q = bInd[j][n];
						if (q == -1)
							continue;
						simMatrix[m][n] = atomSimMatrix[p][q];
					}
				}
				float sim = ComaTrigram2.computeSetSimilarity(simMatrix);
				IObjectInstance oiL = aIds[i];
				IObjectInstance oiR = bIds[j];

				if (sim > threshold
						&& mids.contains(oiL.getId().hashCode()
								+ oiR.getId().hashCode())) {
					try {
						mrs.add(oiL, oiR, new Similarity(sim));
					} catch (MappingStoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		end = System.currentTimeMillis();
		if (verbose)
			System.out.println("List: Combined sim: " + aCompCnt + "-"
					+ bCompCnt + ": " + (float) (end - start) / 1000);

	}

	public void atomicToCompositeSimMatrix(ArrayList<Object> aAtoms,
			ArrayList<Object> bAtoms, float[][] atomSimMatrix,
			ArrayList<ArrayList<Object>> aComps,
			ArrayList<ArrayList<Object>> bComps, IObjectInstance[] aIds,
			IObjectInstance[] bIds, final IMappingStore mrs) {
		if (aAtoms == null || bAtoms == null || aComps == null
				|| bComps == null)
			return;

		boolean verbose = false;

		int aAtomCnt = aAtoms.size();
		int bAtomCnt = bAtoms.size();
		int aCompCnt = aComps.size();
		int bCompCnt = bComps.size();

//		// Allocate simMatrix for composite objects
//		float[][] compSimMatrix = new float[aCompCnt][bCompCnt];

		long start, end;
		int p, q;
		// Compute combined sim if specified

		if (verbose)
			System.out.println("atomicToCompositeSimMatrix(): ");

		// Index paths
		start = System.currentTimeMillis();
		int[][] aInd = new int[aCompCnt][];
		int[][] bInd = new int[bCompCnt][];
		for (int i = 0; i < aCompCnt; i++) {
			ArrayList<Object> aComp = aComps.get(i);
			if (aComp == null) {
				aInd[i] = null;
				continue;
			}
			int aSize = aComp.size();
			aInd[i] = new int[aSize];
			for (int j = 0; j < aSize; j++) {
				aInd[i][j] = aAtoms.indexOf(aComp.get(j));
			}
		}
		for (int i = 0; i < bCompCnt; i++) {
			ArrayList<Object> bComp = bComps.get(i);
			if (bComp == null) {
				bInd[i] = null;
				continue;
			}
			int bSize = bComp.size();
			bInd[i] = new int[bSize];
			for (int j = 0; j < bSize; j++) {
				bInd[i][j] = bAtoms.indexOf(bComp.get(j));
			}
		}
		end = System.currentTimeMillis();
		if (verbose)
			System.out.println("List: Indexing of " + aAtomCnt + "/" + aCompCnt
					+ "-" + bAtomCnt + "/" + bCompCnt + ": "
					+ (float) (end - start) / 1000);

		// Compute path similarity. Note: Most of the time spent here, further
		// optimize computeSetSimilarity()?
		start = System.currentTimeMillis();
		for (int i = 0; i < aCompCnt; i++) {
			if (aInd[i] == null)
				continue;
			int aSize = aInd[i].length;
			for (int j = 0; j < bCompCnt; j++) {
				if (bInd[j] == null)
					continue;
				int bSize = bInd[j].length;
				float[][] simMatrix = new float[aSize][bSize];
				for (int m = 0; m < aSize; m++) {
					p = aInd[i][m];
					if (p == -1)
						continue;
					for (int n = 0; n < bSize; n++) {
						q = bInd[j][n];
						if (q == -1)
							continue;
						simMatrix[m][n] = atomSimMatrix[p][q];
					}
				}
				float sim = ComaTrigram2.computeSetSimilarity(simMatrix);

				this.numberOfComparisons++;
				if (sim > threshold) {
					try {
						
							mrs.add(aIds[i], bIds[j], new Similarity(sim));
						
					} catch (MappingStoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		end = System.currentTimeMillis();
		if (verbose)
			System.out.println("List: Combined sim: " + aCompCnt + "-"
					+ bCompCnt + ": " + (float) (end - start) / 1000);

	}

	public static float computeSetSimilarity(float[][] simMatrix) {
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

	  //Direct synonyms: found an entry in the synonym list
	  boolean isDirectSynonym(String elem1, String elem2) {
	    if (elem1 == null || elem2 == null) return false;
	    ArrayList<String> synList = getDirectSynonyms(elem1);
	    if (synList != null)
	      for (int i = 0; i < synList.size(); i++) {
	        String syn = (String) synList.get(i);
	        if (syn.equalsIgnoreCase(elem2))
	          return true;
	      }
	    return false;
	  }
	  
	  ArrayList<String> getDirectSynonyms(String elem) {
			 
		    if (elem == null) return null;
		    ArrayList<String> synList = new ArrayList<String>();
		    for (int i = 0; i < synonymPairList.size(); i++) {
		      ArrayList<String> syn = synonymPairList.get(i);
		      String word1 = (String) syn.get(0);
		      String word2 = (String) syn.get(1);
		      
		      //hung
		      word1 = word1.replaceAll(" ", "");
		      word2 = word2.replaceAll(" ", "");
		      
		      if (elem.equalsIgnoreCase(word1))
		        synList.add(word2);
		      else if (elem.equalsIgnoreCase(word2))
		        synList.add(word1);
		    }
		    if (!synList.isEmpty())
		      return synList;
		    return null;
		  }
	
	  //Inherited synonyms, also contains direct synonyms
	  boolean isInheritedSynonym(String elem1, String elem2) {
	    if (elem1 == null || elem2 == null)
	      return false;
	    ArrayList<String> synList = getInheritedSynonyms(elem1);
	    if (synList != null)
	      for (int i = 0; i < synList.size(); i++) {
	        String syn = (String) synList.get(i);
	        if (syn.equalsIgnoreCase(elem2))
	          return true;
	      }
	    return false;
	  }
	  
	  ArrayList<String> getInheritedSynonyms(String elem) {
		    ArrayList<String> synList = new ArrayList<String>();
		    getInheritedSynonyms(synList, elem);
		    if (synList.contains(elem))
		      synList.remove(elem);
		    return synList;
	  }
	  
	  void getInheritedSynonyms(ArrayList<String> synList, String elem) {
		    if (elem == null) return;
		    ArrayList<String> currentSynList = getDirectSynonyms(elem);
		    if (currentSynList != null) {
		      currentSynList.removeAll(synList);
		      synList.addAll(currentSynList);
		      for (int i=0; i < currentSynList.size(); i++) {
		        String syn = (String) currentSynList.get(i);
		        getInheritedSynonyms(synList, syn);
		      }
		    }
	  }
	  
	/**
	 * A <code>NULL</code>-String
	 */
	public static final String NULL = "<NULL>";

	protected static final String[] NULL_TRGS = new String[0];

	protected static final int[] NULL_IDS = new int[0];
}
