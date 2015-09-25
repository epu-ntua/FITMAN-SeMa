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

import java.util.ArrayList;
import java.util.Random;

import junit.framework.TestCase;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;

/**
 * Test some example matrixes for combination,
 * be aware that a lot of combinations don't support database matchresults (yet)
 * 
 * @author Sabine Massmann
 *
 */
public class TCombination extends TestCase {

	static final float DELTA = (float) 0.00001;
	static final int SRC_SIZE = 100;
	static final int TRG_SIZE = 50;
	static final int CNT1 = 1220;
	static final int CNT2 = 659;
	static final long SEED = 1234567;
	// TODO check the combination, especially the not traditional ones
	static final int[] COM = {1547, 1547, 1547, 332, 1547, 1547, 1547, 1547, 1547, 1547, 1547};
	
	static final int[] SEL_LARGER_05 = {
		838, // Max
		163, // Average
		163, // Weighted
		77,  // Min
		80,  //  NonLinear
		171, //  Harmony
		163, // Sigmoid
		163, // OpenII
		838, // OWA
		134, // OWA_MOST
		163 // Weighted2
	};

	public void testExampleCombination() {
		// test for correct creation -> look at TMatchResult
	
		ArrayList<Object> srcObjects = new ArrayList<Object>();
		for (int i = 0; i < SRC_SIZE; i++) {
			srcObjects.add(new Element(i));
		}
		ArrayList<Object> trgObjects = new ArrayList<Object>();
		for (int i = 0; i < TRG_SIZE; i++) {
			trgObjects.add(new Element(i));
		}
	
		Random r = new Random(SEED); // For reproducible testing
		float[][] simMatrix1 = new float[SRC_SIZE][TRG_SIZE];
		for (int i = 0; i < SRC_SIZE; i++) {
			for (int j = 0; j < TRG_SIZE; j++) {
				float sim = r.nextFloat();
				if (((i + j) % 2 == 0 && (i + j) % 3 == 0) || (i + j) % 11 == 0){
					simMatrix1[i][j] = sim;
				}
			}
		}
	
		 float[][] simMatrix2 = new float[SRC_SIZE][TRG_SIZE];
		 for (int i = 0; i < SRC_SIZE; i++) {
			for (int j = 0; j < TRG_SIZE; j++) {
				float sim = r.nextFloat();
				if (((i + j) % 2 == 0 && (i + j) % 9 == 0) || (i + j) % 13 == 0) {
					simMatrix2[i][j] = sim;
				}
			}
		}
		 
		MatchResultArray resultArray1 = new MatchResultArray(srcObjects, trgObjects, simMatrix1);
		MatchResultArray resultArray2 = new MatchResultArray(srcObjects, trgObjects, simMatrix2);

		
		Combination c = new Combination( Combination.COM_AVERAGE);
		MatchResult result = c.combine(null);
		assertEquals(null, result);

		Selection s = new Selection(Selection.DIR_SIMPLE, Selection.SEL_THRESHOLD, 0.5f);
		for (int k = 0; k < Combination.COM_IDS.length; k++) {
			c = new Combination( Combination.COM_IDS[k]);
			System.out.println(c.toString());
			
			MatchResult resultArray = c.combine(null);
			assertNull(resultArray);
			
			resultArray = c.combine(new MatchResult[]{resultArray1});
			assertEquals(resultArray1.getMatchCount(), resultArray.getMatchCount());
			
			resultArray = c.combine(new MatchResult[]{resultArray1, resultArray2});
			assertEquals(COM[k], resultArray.getMatchCount());

			// select values >= 0.5
			resultArray = s.select(resultArray);
			assertEquals(SEL_LARGER_05[k], resultArray.getMatchCount());
		}
	}
	
	static public void testOfEqual(ArrayList<Object> srcObjects, ArrayList<Object> trgObjects, MatchResult result1, MatchResult result2){
		for (int i = 0; i < SRC_SIZE; i++) {
			Object srcObject = srcObjects.get(i);
			for (int j = 0; j < TRG_SIZE; j++) {
				Object trgObject = trgObjects.get(j);
				assertEquals( result1.getSimilarity(srcObject, trgObject),result2.getSimilarity(srcObject, trgObject), DELTA);
			}
		}
		
	}
	
	static float[][] dataSetCombination1x1 =  {
		{0.6f},
	};
	
	static float[][] dataSetCombination2x2 =  {
		{0.6f, 1f},
		{0.8f, 0f},
	};
	
	static float[][] dataSetCombination3x2 =  {
		{0.6f, 1f},
		{0.8f, 0f},
		{0f,   0.3f},
	};
	
	
	static public void testSetCombination(){
		float threshold = 0.5f;
		
		float simAvg = Combination.computeSetSimilarity(dataSetCombination1x1, Combination.SET_AVERAGE, 0);
		float simDice = Combination.computeSetSimilarity(dataSetCombination1x1, Combination.SET_DICE, threshold);
		float simMin = Combination.computeSetSimilarity(dataSetCombination1x1, Combination.SET_MIN, threshold);
		float simMax = Combination.computeSetSimilarity(dataSetCombination1x1, Combination.SET_MAX, threshold);
		
		assertEquals(dataSetCombination1x1[0][0], simAvg);
		// all three 1 because value is larger than threshold (otherwise all 0)
		assertEquals(1.0f, simDice);
		assertEquals(1.0f, simMin);
		assertEquals(1.0f, simMax);

		simAvg = Combination.computeSetSimilarity(dataSetCombination2x2, Combination.SET_AVERAGE, 0);
		simDice = Combination.computeSetSimilarity(dataSetCombination2x2, Combination.SET_DICE, threshold);
		simMin = Combination.computeSetSimilarity(dataSetCombination2x2, Combination.SET_MIN, threshold);
		simMax = Combination.computeSetSimilarity(dataSetCombination2x2, Combination.SET_MAX, threshold);
		
		assertEquals(0.9f, simAvg);
		// all three 1 because for each i/j (=line/row) a value is larger than threshold (otherwise all 0)
		assertEquals(1.0f, simDice);
		assertEquals(1.0f, simMin);
		assertEquals(1.0f, simMax);

		
		simAvg = Combination.computeSetSimilarity(dataSetCombination3x2, Combination.SET_AVERAGE, 0);
		simDice = Combination.computeSetSimilarity(dataSetCombination3x2, Combination.SET_DICE, threshold);
		simMin = Combination.computeSetSimilarity(dataSetCombination3x2, Combination.SET_MIN, threshold);
		simMax = Combination.computeSetSimilarity(dataSetCombination3x2, Combination.SET_MAX, threshold);
		assertEquals(0.78f, simAvg);
		assertEquals(0.8f, simDice);
		assertEquals(1.0f, simMin); // use minimal size for dividing -> therefore higher value than dice
		assertEquals(0.6666667f, simMax);
	}
	
	
	static public void testSetCombination2(){
		
		float[][] simM1 = {{0.0f, 0.11111111f}, {0.16666667f, 0.0f}};
		
		float simAvg = Combination.computeSetSimilarity(simM1, Combination.SET_AVERAGE, 0);
		assertEquals(0.1388889f, simAvg);
		
		float[][] simM2 = {{0.21052632f, 0.0f}, {0.0f, 0.0f}};
		simAvg = Combination.computeSetSimilarity(simM2, Combination.SET_AVERAGE, 0);
		assertEquals(0.10526316f, simAvg);
	
		float[][] simM3 = {{0.410184f, 0.4461245f, 0.44580072f}};
		simAvg = Combination.computeSetSimilarity(simM3, Combination.SET_AVERAGE, 0);
		assertEquals(0.43705845f, simAvg);		  
		 
	}
	
}
