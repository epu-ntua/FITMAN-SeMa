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

package de.wdilab.coma.matching.execution;

import junit.framework.TestCase;

import de.wdilab.coma.center.TestConfigurations;
import de.wdilab.coma.center.Manager;
import de.wdilab.coma.insert.relationships.TRDFAlignmentParser;
import de.wdilab.coma.matching.ComplexMatcher;
import de.wdilab.coma.matching.Strategy;
import de.wdilab.coma.matching.Matcher;
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
public class TMatchAnatomy  extends TestCase {

	static String[][] DATA_ONTOLOGY = {
	{ "anatomy_mouse_2010", "anatomy_nci_2010", "anatomy_reference_2010_rdf" },
	};

	static int[] CONFIGURATION = {
		ComplexMatcher.CHILDREN,
		Matcher.NAME,
		Matcher.NAMESYNONYM, // Add to VM arguments -Xmx2500M
	};

	static Selection SELECTION = new Selection(Selection.DIR_BOTH, Selection.SEL_MAXN, 1);

	static float[][] RESULT = {
		{0.67f,	0.61f,	0.64f},
		{0.85f,	0.77f,	0.81f},
		{0.85f,	0.83f,	0.84f},
	};
	
	
	/**
	 * @param args
	 */
	public void testMatching() {

		TRepository.setDatabaseProperties();

		 TRDFAlignmentParser.testWithDatabase();

		Manager manager = new Manager();
		DataAccess accessor = manager.getAccessor();
		manager.loadRepository();

		matchAnatomy(manager, accessor);

		System.out.println("DONE.");
	}


	public void matchAnatomy(Manager manager, DataAccess accessor) {

		StringBuffer all = new StringBuffer();
		int graphState = Graph.PREP_RESOLVED;
		MatchResult[] intendedResults = new MatchResult[DATA_ONTOLOGY.length];
		long start1 = System.currentTimeMillis();
		for (int i = 0; i < DATA_ONTOLOGY.length; i++) {
			String sourceName = DATA_ONTOLOGY[i][0];
			String targetName = DATA_ONTOLOGY[i][1];
			String resultName = DATA_ONTOLOGY[i][2];
			MatchResult intendedResult = TestConfigurations.loadMatchResult(
					manager, accessor, sourceName, targetName, graphState,
					resultName);
			System.out.println("***\t" + sourceName + "\t" + targetName + "\t"
					+ resultName + "\t" + intendedResult.getMatchCount());
			intendedResults[i] = intendedResult;
		}
		long end1 = System.currentTimeMillis();
		all.append("loading matchresult with graphs:\t" + (end1 - start1)
				/ 1000 + "s\n");
		all.append("\tIntended\tPrecision\tRecall\tFmeasure\n");
		MatchResult[] results = new MatchResult[intendedResults.length];
		float prec = 0, rec = 0, fmeas = 0;
		long time = 0;

		// NEW Version
		Workflow w = new Workflow();

		String timeSeparate = "";
		ExecWorkflow exec = new ExecWorkflow();

		boolean path = false;
		for (int j = 0; j < CONFIGURATION.length; j++) {
			// create Strategy, ComplexMatcher or ComplexMatcher with Matcher
			// "inside"
			int current = CONFIGURATION[j];
			Strategy strategy = TestConfigurations.getStrategy(current, path,
					SELECTION);
			w.setBegin(strategy);
			System.out.println(strategy.toString(false));
			all.append("\n" + strategy.toString(false) + "\n");
			for (int i = 0; i < intendedResults.length; i++) {
				MatchResult intendedResult = intendedResults[i];
				w.setSource(intendedResult.getSourceGraph());
				w.setTarget(intendedResult.getTargetGraph());

				// AllContext
				long start = System.currentTimeMillis();
				// where graphState???
				results[i] = exec.execute(w)[0];
				// System.out.println( allContext[i].toString());

				long end = System.currentTimeMillis();

				int separate = (int) ((end - start) / 1000);
				timeSeparate += separate + "\t";
				time += (end - start);

				EvaluationMeasure measures = intendedResult.compare(results[i]);
				if (measures != null) {
					all.append(intendedResult.getName() + "\t"
							+ intendedResult.getMatchCount() + "\t"
							+ measures.getPrecision() + "\t"
							+ measures.getRecall() + "\t"
							+ measures.getFmeasure() + "\n");
					prec += measures.getPrecision();
					rec += measures.getRecall();
					fmeas += measures.getFmeasure();
					
					assertEquals(RESULT[j][0], measures.getPrecision(), 0.01f);
					assertEquals(RESULT[j][1], measures.getRecall(), 0.01f);
					assertEquals(RESULT[j][2], measures.getFmeasure(), 0.01f);
					
				} else {
					all.append(intendedResult.getName() + "no results\n");
				}			}
		}
		int size = intendedResults.length * CONFIGURATION.length;
		prec /= size;
		rec /= size;
		fmeas /= size;
		System.out.print(all.toString().replace(".", ","));
		System.out
				.println(("\n\nAllContext\t" + prec + "\t" + rec + "\t" + fmeas)
						.replace(".", ","));
		System.out.println("Time(ms)\t" + time + "\taverage\t" + time / size);
		System.out.println("Time single(s)\t" + timeSeparate);

	}

}
