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

package de.wdilab.coma.matching;

import java.util.HashMap;

import junit.framework.TestCase;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.TRepository;

/**
 * Test for some simple restriction of the default workflow components
 * 
 * @author Sabine Massmann
 *
 */
public class TWorkflow extends TestCase {
	

	public void testPredefined1() throws Exception {
		TRepository.setDatabaseProperties();
		boolean printWithVariable = true;
		boolean printWithoutVariable = true;
		
		for (int i = 0; i < Matcher.MATCHER.length; i++) {
			int current = Matcher.MATCHER[i];
			Matcher matcher = new Matcher(current);
			assertNotNull(matcher.getResolution());
			assertNotNull(matcher.getSimMeasures());
			if (matcher.getSimMeasures().length>1){
				assertNotNull(matcher.getSimCombination());
			}
			if (printWithVariable) System.out.println(matcher.toString(true));
			if (printWithoutVariable) System.out.println(matcher.toString(false));
		}
		System.out.println();
		for (int i = 0; i < ComplexMatcher.COMPLEXMATCHER.length; i++) {
			int current = ComplexMatcher.COMPLEXMATCHER[i];
			ComplexMatcher cmatcher = new ComplexMatcher(current);
			assertNotNull(cmatcher.getResolution());
			assertNotNull(cmatcher.getMatcherAndComplexMatcher());
			if (cmatcher.getMatcherAndComplexMatcher().length>1){
				assertNotNull(cmatcher.getSimCombination());
			}
			if (printWithVariable) System.out.println(cmatcher.toString(true));
			if (printWithoutVariable) System.out.println(cmatcher.toString(false));
		}
		System.out.println();
		for (int i = 0; i < Strategy.STRATEGY.length; i++) {
			int current = Strategy.STRATEGY[i];
			Strategy strategy = new Strategy(current);
			assertNotNull(strategy.getResolution());
			assertNotNull(strategy.getComplexMatcher());
			if (strategy.getComplexMatcher().length>1){
				assertNotNull(strategy.getSimCombination());
			}
			if (printWithVariable) System.out.println(strategy.toString(true));
			if (printWithoutVariable) System.out.println(strategy.toString(false));
		}
		System.out.println();
		for (int i = 0; i < Workflow.WORKFLOW.length; i++) {
			int current = Workflow.WORKFLOW[i];
			Workflow workflow = new Workflow(current);
			assertNotNull(workflow.getBegins());
			assertFalse(workflow.getBegins().length==0);
			if (printWithVariable) System.out.println(workflow.toString(true));
			if (printWithoutVariable) System.out.println(workflow.toString(false));
		}
	}
	
	public void testPredefined2() throws Exception {
		TRepository.setDatabaseProperties();
		DataImport importer = new DataImport();
		importer.dropRepositorySchema();
		importer.createRepositorySchema();
		Workflow.insertDefaults(importer);
		DataAccess accessor = new DataAccess();
		HashMap<String, String> list = accessor.getWorkflowVariables();
		System.out.println(list);
	}

	

}
