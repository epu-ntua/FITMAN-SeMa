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

package de.wdilab.coma.structure;

import junit.framework.TestCase;

/**
 * @author Sabine Massmann
 */
public class TEvaluationMeasure extends TestCase{

		public void testExampleMeasure() throws Exception {
			EvaluationMeasure m = new EvaluationMeasure(30, 20, 15);
			m.print();
			assertEquals(0.75, m.getPrecision(), 0.0001);
			assertEquals(0.5, m.getRecall(), 0.0001);
			assertEquals(0.6, m.getFmeasure(), 0.0001);
			
			m = new EvaluationMeasure(40, 10, 10);
			m.print();
			assertEquals(1, m.getPrecision(), 0.0001);
			assertEquals(0.25, m.getRecall(), 0.0001);
			assertEquals(0.4, m.getFmeasure(), 0.0001);
		}
		
		public void testMinMeasure() throws Exception {
			EvaluationMeasure m = new EvaluationMeasure(10, 0, 0);
			m.print();
			assertEquals(0, m.getPrecision(), 0.0001);
			assertEquals(0, m.getRecall(), 0.0001);
			assertEquals(0, m.getFmeasure(), 0.0001);
		}
		
		public void testMaxMeasure() throws Exception {
			EvaluationMeasure m = new EvaluationMeasure(10, 10, 10);
			m.print();
			assertEquals(1, m.getPrecision(), 0.0001);
			assertEquals(1, m.getRecall(), 0.0001);
			assertEquals(1, m.getFmeasure(), 0.0001);
		}
}
