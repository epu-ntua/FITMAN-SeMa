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

import junit.framework.TestCase;
import de.wdilab.coma.matching.execution.Match2Values;

/**
 * Test the different string matcher on a few cases;
 * result similarity should be between 0 and 1
 * 
 * @author Sabine Massmann
 *
 */
public class TSimMeasures extends TestCase {

	
	String[][] pairs={
			{ "name", "name"},
			{ "name", "lastname"},
			{ "firstname", "lastname"},
			{ "first name", "last name"},
			{ "house size", "size of house"},
	};
	
	float[][][] simValues = {
			{{1,1,1,1,	1,1,1,1}, {1,1,1,1}},
			{{0.5f,0.5f,0.5f,0.5f,	0.45833334f,0.5f,0.5f,0}, {0,0,0,0}},
			{{0.5714286f,0.5714286f,0.5714286f,0.5714286f,	0.8055556f,0.6666666f,0.6666666f,0},{0,0,0,0}},
			{{0.65384614f,0.6086956f,0.6086956f,0.6086956f,	 0.8259259f,0.7f,0.7f,0},{0.5f,0.5f,0.33333334f,0.2611593f}},
			{{0.8f,0.44444445f, 0.7407407f,0.44444445f,	0.5760684f,0.15384614f,0.15384614f,0},{0.8164966f,0.8164966f,0.6666667f, 1f}},
	};
	
	public void testCompareSingleValues(){

		for (int i = 0; i < pairs.length; i++) {
			String value1 = pairs[i][0];
			String value2 = pairs[i][1];
			System.out.println("\tcompare: " + value1 + " <-> " + value2);
			for (int j = 0; j < SimilarityMeasure.SIMMEASURE_SHORT_STRING.length; j++) {	
				int measure = SimilarityMeasure.SIMMEASURE_SHORT_STRING[j];
				float sim = Match2Values.execute(measure, value1, value2);
				System.out.println(SimilarityMeasure.measureToString(measure) + ": " + sim);
				assertTrue(sim>=0 && sim<=1);
				assertEquals(simValues[i][0][j], sim);
			}	
			for (int j = 0; j < SimilarityMeasure.SIMMEASURE_LONG_STRING.length; j++) {	
				int measure = SimilarityMeasure.SIMMEASURE_LONG_STRING[j];
				float sim = Match2Values.execute(measure, value1, value2);
				System.out.println("\t"+ SimilarityMeasure.measureToString(measure) + ": " + sim);
				assertTrue(sim>=0 && sim<=1);
				assertEquals(simValues[i][1][j], sim);
			}	
		}
	}
	
	
	
}
