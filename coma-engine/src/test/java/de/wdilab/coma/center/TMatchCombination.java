/*
 *  COMA 3.0 Community Edition
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package de.wdilab.coma.center;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import de.wdilab.coma.insert.relationships.TRDFAlignmentParser;
import de.wdilab.coma.matching.Combination;
import de.wdilab.coma.matching.ComplexMatcher;
import de.wdilab.coma.matching.Strategy;
import de.wdilab.coma.matching.Constants;
import de.wdilab.coma.matching.Resolution;
import de.wdilab.coma.matching.Selection;
import de.wdilab.coma.matching.Workflow;
import de.wdilab.coma.matching.execution.ExecWorkflow;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.repository.TRepository;
import de.wdilab.coma.structure.EvaluationMeasure;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;

/**
 * @author Sabine Massmann
 */
public class TMatchCombination extends TestCase {

	public static int[] TEST_MATCHER_IDS = { 
			ComplexMatcher.NAME,
			ComplexMatcher.NAMESYN, // Add to VM arguments -Xmx2500M
			ComplexMatcher.CHILDREN,
	};
	
	static float[][] SINGLE_RESULTS = {
		// Name
		{0.85f, 0.77f, 0.81f},
		// NameSyn
		{0.85f, 0.825f, 0.84f},
		// Children
		{0.67f, 0.61f, 0.64f},
	};

	static float[] AVG_RESULTS = {0.80f,0.76f,0.78f};
	 // TEST_MATCHER_IDS.length = use all matcher together
	public static int TEST_MAXMATCHER_CNT = TEST_MATCHER_IDS.length;
	 // 1 = use each matcher alone
	public static int TEST_MINMATCHER_CNT = 1;

	static float MIN = 0.1f; // minimum weight for a matcher
	static float SUM = 1.0f; // sum of all matcher weights
	static float DIFF = 0.1f; // step size to increase from minimum

	static Selection SELECTION = new Selection(Selection.DIR_BOTH,Selection.SEL_MAXN, 1, 0, 0);

	static String[][] DATA_ONTOLOGY_NODE = {
	// source // target // mapping
	{ "anatomy_mouse_2010", "anatomy_nci_2010", "anatomy_reference_2010_rdf" }, };

	public void test() {
		TRepository.setDatabaseProperties();

		 TRDFAlignmentParser.testWithDatabase();

		Manager manager = new Manager();
		DataAccess accessor = manager.getAccessor();
		manager.loadRepository();

		testAnatomy(manager, accessor);

		System.out.println("DONE.");
	}

	static void getWeights(ArrayList<float[]> weights, int count) {
		getWeights(weights, count, 0f, null);
	}

	static void getWeights(ArrayList<float[]> weights, int count,
			float prevSum, float[] prev) {
		if (count == 0) {
			return;
		}
		// max value of one matcher
		float max = SUM - prevSum;
		float[] current = new float[count];
		if (prevSum > 0) {
			for (float i = MIN; i <= (max + 0.001); i += DIFF) {
				float currentSum = prevSum + i;
				// create empty weights
				current = new float[prev.length + 1];
				// fill previous values (last one is new)
				for (int j = 0; j < prev.length; j++) {
					current[j] = prev[j];
				}
				current[prev.length] = i;
				// be more tolerant because 0.7f not exactly possible in float
				// but only something like 0.70000005
				if ((SUM - currentSum) <= 0.001 && count == 1) {
					// add to final weights because it is "full"
					weights.add(current);
				} else {
					getWeights(weights, count - 1, currentSum, current);
				}
			}
		} else {
			for (float i = MIN; i < max; i += DIFF) {
				float currentSum = i;
				current = new float[1];
				current[0] = i;
				getWeights(weights, count - 1, currentSum, current);
			}
		}
	}

	static void testAnatomy(Manager manager, DataAccess accessor) {
		ArrayList<Strategy> cstrategies = generateCombinations();

		StringBuffer all = new StringBuffer();
		int graphState = Graph.PREP_RESOLVED;
		MatchResult[] intendedResults = new MatchResult[DATA_ONTOLOGY_NODE.length];
		long start1 = System.currentTimeMillis();
		for (int i = 0; i < DATA_ONTOLOGY_NODE.length; i++) {
			String sourceName = DATA_ONTOLOGY_NODE[i][0];
			String targetName = DATA_ONTOLOGY_NODE[i][1];
			String resultName = DATA_ONTOLOGY_NODE[i][2];
			MatchResult intendedResult = TestConfigurations.loadMatchResult(
					manager, accessor, sourceName, targetName, graphState,
					resultName);
			System.out.println("***\t" + sourceName + "\t" + targetName + "\t"
					+ resultName + "\t" + intendedResult.getMatchCount());
			intendedResults[i] = intendedResult;
		}
		long end1 = System.currentTimeMillis();
		
		int size = intendedResults.length * cstrategies.size();
		assertEquals(70, size);
		
		all.append("loading matchresult with graphs:\t" + (end1 - start1)
				/ 1000 + "s\n");
		all.append("\tIntended\tPrecision\tRecall\tFmeasure\n");
		MatchResult[] results = new MatchResult[intendedResults.length];
		float prec = 0, rec = 0, fmeas = 0;
		long time = 0;

		Workflow w = new Workflow();

		String timeSeparate = "";
		ExecWorkflow exec = new ExecWorkflow();

		for (int j = 0; j < cstrategies.size(); j++) {
			// create Strategy, ComplexMatcher or ComplexMatcher with Matcher
			// "inside"
			Strategy strategy = cstrategies.get(j);

			w.setBegin(strategy);
			// System.out.println(strategy.toString(false));
			System.out.println(j + "/" + cstrategies.size() + "\t"
					+ strategy.toString(true));

			for (int i = 0; i < intendedResults.length; i++) {
				MatchResult intendedResult = intendedResults[i];
				w.setSource(intendedResult.getSourceGraph());
				w.setTarget(intendedResult.getTargetGraph());

				// AllContext
				long start = System.currentTimeMillis();
				results[i] = exec.execute(w)[0];
				// System.out.println( allContext[i].toString());

				long end = System.currentTimeMillis();

				// use the specified selection
				int separate = (int) ((end - start) / 1000);
				timeSeparate += separate + "\t";
				time += (end - start);

				EvaluationMeasure measures = intendedResult.compare(results[i]);
				if (measures != null) {
					all.append("\n" + strategy.toString(true)
							+ "\t"
							+
							// intendedResult.getName() + "\t" +
							// intendedResult.getMatchCount()+ "\t"+
							measures.getPrecision() + "\t"
							+ measures.getRecall() + "\t"
							+ measures.getFmeasure());
					if (j<SINGLE_RESULTS.length){
						assertEquals(SINGLE_RESULTS[j][0], measures.getPrecision(), 0.01f);
						assertEquals(SINGLE_RESULTS[j][1], measures.getRecall(), 0.01f);
						assertEquals(SINGLE_RESULTS[j][2], measures.getFmeasure(), 0.01f);
					}
					
					prec += measures.getPrecision();
					rec += measures.getRecall();
					fmeas += measures.getFmeasure();
				} else {
					all.append(intendedResult.getName() + "no results\n");
				}
			}
		}

		prec /= size;
		rec /= size;
		fmeas /= size;

		assertEquals(AVG_RESULTS[0], prec, 0.01f);
		assertEquals(AVG_RESULTS[1], rec, 0.01f);
		assertEquals(AVG_RESULTS[2], fmeas, 0.01f);
		
		System.out.print(all.toString().replace(".", ","));
		System.out
				.println(("\n\nAllContext\t" + prec + "\t" + rec + "\t" + fmeas)
						.replace(".", ","));
		System.out
				.println("Time(ms)\t" + time + "\tsingle(s)\t" + timeSeparate);
	}

	static ArrayList<Strategy> generateCombinations() {
		int[] contains = null;
		// int[] contains = new int[]{
		// Arrays.binarySearch(TEST_NODE_MATCHER_IDS,MatcherLibrary.MATCH_SYS_PARENTS),
		// };
		int[] excludes = null;
		ArrayList<int[]> combs = generateIdCombinations(
				TEST_MATCHER_IDS.length, TEST_MAXMATCHER_CNT,
				TEST_MINMATCHER_CNT, contains, excludes);
		int resolution = Resolution.RES1_NODES;
		int simCombination = Combination.COM_AVERAGE;

		ArrayList<Strategy> cstrategies = new ArrayList<Strategy>();
		for (int i = 0; i < combs.size(); i++) {
			int[] current = combs.get(i);
			ComplexMatcher[] comatch_strat = new ComplexMatcher[current.length];
			for (int j = 0; j < current.length; j++) {
				int currentId = TEST_MATCHER_IDS[current[j]];

				if (Constants.getClass(currentId).equals(ComplexMatcher.class)) {
					// use directly in Strategy
					comatch_strat[j] = new ComplexMatcher(currentId);
				} else {
					System.out.println("Error - not a ComplexMatcher");
				}
			}
			if (current.length == 1) {
				ComplexMatcher cm_s = comatch_strat[0];
				Strategy cs = new Strategy(resolution, cm_s, SELECTION);
				cstrategies.add(cs);
			} else {
				Strategy cs = new Strategy(resolution, comatch_strat,
						simCombination, null, SELECTION);
				cstrategies.add(cs);
				ArrayList<float[]> weights = new ArrayList<float[]>();
				getWeights(weights, current.length);
				for (int j = 0; j < weights.size(); j++) {
					cs = new Strategy(resolution, comatch_strat,
							Combination.COM_WEIGHTED, weights.get(j), SELECTION);
					cstrategies.add(cs);
				}
			}
		}
		return cstrategies;
	}

	// Generate combinations of min and max length from a range of all
	static ArrayList<int[]> generateIdCombinations(int all, int max, int min,
			int[] contains, int[] excludes) {
		ArrayList<int[]> goodCombs = new ArrayList<int[]>();
		if (max == 0) {
			max = all;
		}
		for (int i = 0; i < max - min + 1; i++) {
			// use the following lines to get only combinations have of 1 or the
			// maximum length
			// if (i!=0 && i!=(max-1)){
			// continue;
			// }
			ArrayList<int[]> combs = Combinatorics
					.getCombinations(all, min + i);
			if (contains == null && excludes == null) {
				goodCombs.addAll(combs);
				continue;
			}

			// Check single combinations for contains & excludes
			int combsSize = combs.size();
			for (int j = 0; j < combsSize; j++) {
				int[] comb = combs.get(j);
				// Sort array first
				Arrays.sort(comb);
				// Check contains
				boolean proceed = true;
				if (contains != null) {
					boolean contained = false;
					for (int m = 0; m < contains.length; m++) {
						int current = contains[m];
						if (Arrays.binarySearch(comb, current) < 0) {
							proceed = false;
							// // line1
							// comment this line and comment line2 out to get
							// combination with all contains (not just one)
							break;
						}
						if (!contained) {
							contained = true;
						}

					}
					// // line2
					// // comment this line out and comment line1 to get
					// combination with all contains (not just one)
					// if (contained){
					// proceed=true;
					// }
				}
				if (!proceed)
					continue;

				// Check excludes
				if (excludes != null) {
					for (int m = 0; m < excludes.length; m++) {
						int current = excludes[m];
						if (Arrays.binarySearch(comb, current) >= 0) {
							proceed = false;
							break;
						}
					}
				}
				// // use the following lines to get only combinations have of 1
				// or the maximum length
				// if (comb.length!=1 && comb.length!=max){
				// proceed=false;
				// }

				if (proceed)
					goodCombs.add(comb);
			}
		}
		if (goodCombs.isEmpty())
			return null;
		return goodCombs;
	}

}
