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
 * Test for specific similarity matrices all different selections (maxN, delta, threshold, multiple)
 * and compare array and db results to ensure the same results for both MatchResult implementations
 * 
 * @author Sabine Massmann
 *
 */
public class TSelection extends TestCase {

	static final float DELTA = (float) 0.00001;
	static final int SRC_SIZE = 100;
	static final int TRG_SIZE = 50;
	static final int CNT1 = 1220;
	static final int CNT2 = 659;
	static final long SEED = 1234567;
	
	static final int NOSEL1 = 1220;
	static final int NOSEL2 = 659;	
	
	static final int[][][] SEL_MAXN = {
		// BOTH							// FORWARD						// BACKWARD						// SIMPLE				
		{{35, 87, 139, 190, 240}, 		{100, 200, 300, 400, 500},  	{50, 100, 150, 200, 250}, 		{115, 213, 311, 410, 510} },	// resultArray1
		{{36, 87, 140, 194, 247}, 		{100, 200, 300, 400, 500},  	{50, 100, 150, 200, 250}, 		{114, 213, 310, 406, 503} }		// resultArray2
	};
	static final int[][][] SEL_DELTA = {
		// BOTH							// FORWARD						// BACKWARD						// SIMPLE
		{{63, 93, 121, 142}, 			{117, 143, 162, 186}, 	 		{79, 107, 136, 154}, 			{133, 157, 177, 198} },			// resultArray1
		{{48, 55, 64, 71}, 				{109, 121, 129, 136}, 	 		{61, 68, 81, 85}, 				{122, 134, 146, 150} }			// resultArray2
	};
	static final int[][][] SEL_THRESHOLD = {
		// BOTH								// FORWARD					// BACKWARD						// SIMPLE
		{{1104, 966, 860, 723, 597},	{1104, 966, 860, 723, 597}, 	{1104, 966, 860, 723, 597}, 	{1104, 966, 860, 723, 597} },	// resultArray1
		{{587, 513, 452, 385, 318},		{587, 513, 452, 385, 318}, 		{587, 513, 452, 385, 318}, 		{587, 513, 452, 385, 318} } 	// resultArray2
	};

	static final int[][][] SEL_MULTIPLE1 = {
		{	// BOTH	
			{1220,1104,966,860,723,597,63,63,63,63,63,63,93,93,93,93,93,93,121,121,121,121,121,121,142,142,142,142,142,142,}, 	// n=0
			{35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,35,}, 						// n=1	
			{87,87,87,87,87,87,58,58,58,58,58,58,70,70,70,70,70,70,80,80,80,80,80,80,85,85,85,85,85,85,}, 						// n=2
			{139,139,139,139,139,139,62,62,62,62,62,62,84,84,84,84,84,84,102,102,102,102,102,102,116,116,116,116,116,116,},		// n=3
			{190,190,190,190,190,190,63,63,63,63,63,63,91,91,91,91,91,91,113,113,113,113,113,113,129,129,129,129,129,129,},		// n=4
			{240,240,240,240,240,240,63,63,63,63,63,63,92,92,92,92,92,92,120,120,120,120,120,120,136,136,136,136,136,136,}, 	// n=5
		},		
		{	// FORWARD	
			{1220,1104,966,860,723,597,117,117,117,117,117,117,143,143,143,143,143,143,162,162,162,162,162,162,186,186,186,186,186,186,}, 
			{100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,}, 
			{200,200,200,200,200,200,115,115,115,115,115,115,137,137,137,137,137,137,153,153,153,153,153,153,166,166,166,166,166,166,}, 
			{300,300,300,300,300,297,117,117,117,117,117,117,143,143,143,143,143,143,162,162,162,162,162,162,183,183,183,183,183,183,}, 
			{400,400,400,400,398,391,117,117,117,117,117,117,143,143,143,143,143,143,162,162,162,162,162,162,185,185,185,185,185,185,}, 
			{500,500,499,498,494,476,117,117,117,117,117,117,143,143,143,143,143,143,162,162,162,162,162,162,186,186,186,186,186,186,},
		},
		{	// BACKWARD		
			{1220,1104,966,860,723,597,79,79,79,79,79,79,107,107,107,107,107,107,136,136,136,136,136,136,154,154,154,154,154,154,},
			{50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,}, 
			{100,100,100,100,100,100,73,73,73,73,73,73,81,81,81,81,81,81,90,90,90,90,90,90,94,94,94,94,94,94,}, 
			{150,150,150,150,150,150,78,78,78,78,78,78,97,97,97,97,97,97,113,113,113,113,113,113,124,124,124,124,124,124,},
			{200,200,200,200,200,200,79,79,79,79,79,79,105,105,105,105,105,105,126,126,126,126,126,126,139,139,139,139,139,139,}, 
			{250,250,250,250,250,250,79,79,79,79,79,79,106,106,106,106,106,106,134,134,134,134,134,134,147,147,147,147,147,147,}, 
		},
		{	// SIMPLE
			{1220,1104,966,860,723,597,133,133,133,133,133,133,157,157,157,157,157,157,177,177,177,177,177,177,198,198,198,198,198,198,}, 
			{115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,115,}, 
			{213,213,213,213,213,213,130,130,130,130,130,130,148,148,148,148,148,148,163,163,163,163,163,163,175,175,175,175,175,175,}, 
			{311,311,311,311,311,308,133,133,133,133,133,133,156,156,156,156,156,156,173,173,173,173,173,173,191,191,191,191,191,191,},
			{410,410,410,410,408,401,133,133,133,133,133,133,157,157,157,157,157,157,175,175,175,175,175,175,195,195,195,195,195,195,}, 
			{510,510,509,508,504,486,133,133,133,133,133,133,157,157,157,157,157,157,176,176,176,176,176,176,197,197,197,197,197,197,}, 
		}
	};
	
	static final int[][][] SEL_MULTIPLE2 = {
		{
			{659,587,513,452,385,318,48,48,48,48,48,48,55,55,55,55,55,55,64,64,64,64,64,64,71,71,71,71,71,71,}, 
			{36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,36,},
			{87,87,87,87,87,87,48,48,48,48,48,48,53,53,53,53,53,53,60,60,60,60,60,60,65,65,65,65,65,65,}, 
			{140,140,140,140,140,139,48,48,48,48,48,48,55,55,55,55,55,55,62,62,62,62,62,62,69,69,69,69,69,69,}, 
			{194,194,194,194,193,190,48,48,48,48,48,48,55,55,55,55,55,55,63,63,63,63,63,63,70,70,70,70,70,70,}, 
			{247,247,247,247,245,237,48,48,48,48,48,48,55,55,55,55,55,55,64,64,64,64,64,64,71,71,71,71,71,71,}, 
		},
		{
			{659,587,513,452,385,318,109,109,109,109,109,109,121,121,121,121,121,121,129,129,129,129,129,129,136,136,136,136,136,135,}, 
			{100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,},
			{200,200,200,200,196,194,109,109,109,109,109,109,120,120,120,120,120,120,126,126,126,126,126,126,131,131,131,131,131,131,}, 
			{300,300,299,295,280,259,109,109,109,109,109,109,121,121,121,121,121,121,129,129,129,129,129,129,136,136,136,136,136,135,}, 
			{400,399,392,374,337,295,109,109,109,109,109,109,121,121,121,121,121,121,129,129,129,129,129,129,136,136,136,136,136,135,}, 
			{500,493,462,421,370,311,109,109,109,109,109,109,121,121,121,121,121,121,129,129,129,129,129,129,136,136,136,136,136,135,}, 
		},
		{
			{659,587,513,452,385,318,61,61,61,61,61,61,68,68,68,68,68,67,81,81,81,81,81,80,85,85,85,85,85,84,},
			{50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,}, 
			{100,100,100,100,100,99,61,61,61,61,61,61,66,66,66,66,66,65,74,74,74,74,74,73,77,77,77,77,77,76,}, 
			{150,150,150,150,150,148,61,61,61,61,61,61,68,68,68,68,68,67,78,78,78,78,78,77,81,81,81,81,81,80,}, 
			{200,200,200,200,199,195,61,61,61,61,61,61,68,68,68,68,68,67,80,80,80,80,80,79,84,84,84,84,84,83,}, 
			{250,250,250,250,248,240,61,61,61,61,61,61,68,68,68,68,68,67,81,81,81,81,81,80,85,85,85,85,85,84,}, 
		},
		{
			{659,587,513,452,385,318,122,122,122,122,122,122,134,134,134,134,134,133,146,146,146,146,146,145,150,150,150,150,150,148,},
			{114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,114,},
			{213,213,213,213,209,206,122,122,122,122,122,122,133,133,133,133,133,132,140,140,140,140,140,139,143,143,143,143,143,142,}, 
			{310,310,309,305,290,268,122,122,122,122,122,122,134,134,134,134,134,133,145,145,145,145,145,144,148,148,148,148,148,146,}, 
			{406,405,398,380,343,300,122,122,122,122,122,122,134,134,134,134,134,133,146,146,146,146,146,145,150,150,150,150,150,148,}, 
			{503,496,465,424,373,314,122,122,122,122,122,122,134,134,134,134,134,133,146,146,146,146,146,145,150,150,150,150,150,148,}, 
		}
	};

	public void testExampleSelection() {
		// test for correct creation -> look at TMatchResult
		MatchResultArray resultArrayNull = null;
		MatchResult resultArrayEmpty = new MatchResultArray();
		
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
		
		for (int i = 0; i < Selection.DIRECTION_IDS.length; i++) {
			for (int j = 0; j < Selection.SELECTION_IDS.length; j++) {
				int direction = Selection.DIRECTION_IDS[i];
				int selection = Selection.SELECTION_IDS[j];			
				System.out.println(Selection.directionToString(direction) + " "+ Selection.selectionToString(selection));				
				
				// default selN=0, selDelta=0, selThres=0
				Selection s = new Selection(direction, selection, 0, 0, 0);
				MatchResult resultArray = s.select(null);
				assertEquals(null, resultArray);
				
				resultArray = s.select(resultArrayNull);
				assertEquals(null, resultArray);

				resultArray = s.select(resultArrayEmpty);
				assertEquals(0, resultArray.getMatchCount());				
				
				resultArray = s.select(resultArray1);
				assertEquals(NOSEL1, resultArray.getMatchCount());
				
				resultArray = s.select(resultArray2);
				assertEquals(NOSEL2, resultArray.getMatchCount());
				
				if (selection== Selection.SEL_MAXN)
					for(int n=1;n<=5;n++){
						
						s = new Selection(direction, selection, n);
						resultArray = s.select(resultArray1);
						assertEquals(SEL_MAXN[0][i][n-1],resultArray.getMatchCount());
						
						// test Multiple with only n value for db
						s = new Selection(direction, Selection.SEL_MULTIPLE, n, 0, 0);
						MatchResult resultTmp = s.select(resultArray1);
						TCombination.testOfEqual(srcObjects, trgObjects, resultArray, resultTmp);									

						s = new Selection(direction, selection, n);
						resultArray = s.select(resultArray2);
						assertEquals(SEL_MAXN[1][i][n-1],resultArray.getMatchCount());
						
						// test Multiple with only n value for db
						s = new Selection(direction, Selection.SEL_MULTIPLE, n, 0, 0);
						resultTmp = s.select(resultArray2);
						TCombination.testOfEqual(srcObjects, trgObjects, resultArray, resultTmp);

					}
				
				if (selection== Selection.SEL_DELTA)
					for(float d=(float) 0.02;d<=0.1;d+=0.02){
						int tmp = (int)(d*50)-1;
						
						s = new Selection(direction, selection, d);
						resultArray = s.select(resultArray1);
						assertEquals(SEL_DELTA[0][i][tmp],resultArray.getMatchCount());
						
						// test Multiple with only d value for db
						s = new Selection(direction, Selection.SEL_MULTIPLE, 0, d, 0);
						MatchResult resultTmp = s.select(resultArray1);
						TCombination.testOfEqual(srcObjects, trgObjects, resultArray, resultTmp);
						
						s = new Selection(direction, selection, d);
						resultArray = s.select(resultArray2);
						assertEquals(SEL_DELTA[1][i][tmp],resultArray.getMatchCount());						
						
						// test Multiple with only d value for db
						s = new Selection(direction, Selection.SEL_MULTIPLE, 0, d, 0);
						resultTmp = s.select(resultArray2);
						TCombination.testOfEqual(srcObjects, trgObjects, resultArray, resultTmp);
					}
				
				if (selection== Selection.SEL_THRESHOLD)
					for(float t=(float)0.1;t<=0.5;t+=0.1){
						int tmp = (int)(t*10)-1;
						
						s = new Selection(direction, selection, t);
						resultArray = s.select(resultArray1);
						assertEquals(SEL_THRESHOLD[0][i][tmp],resultArray.getMatchCount());
						
						// test Multiple with only t value for db
						s = new Selection(direction, Selection.SEL_MULTIPLE, 0, 0, t);
						MatchResult resultTmp = s.select(resultArray1);
						TCombination.testOfEqual(srcObjects, trgObjects, resultArray, resultTmp);
						
						s = new Selection(direction, selection, t);
						resultArray = s.select(resultArray2);
						assertEquals(SEL_THRESHOLD[1][i][tmp],resultArray.getMatchCount());
											
						// test Multiple with only t value for db
						s = new Selection(direction, Selection.SEL_MULTIPLE, 0, 0, t);
						resultTmp = s.select(resultArray2);
						TCombination.testOfEqual(srcObjects, trgObjects, resultArray, resultTmp);				
					}
				
				if (selection== Selection.SEL_MULTIPLE){
					for(int n=0;n<=5;n++){
						for(float d= 0;d<=0.1;d+=0.02){
							int tmpd = (int)(d*50);
							for(float t=0;t<=0.5;t+=0.1){
								int tmp = (int)(t*10);
								tmp = tmpd*6+tmp;

								s = new Selection(direction, selection, n, d, t);
								resultArray = s.select(resultArray1);							
								assertEquals(SEL_MULTIPLE1[i][n][tmp],resultArray.getMatchCount());

								resultArray = s.select(resultArray2);
								assertEquals(SEL_MULTIPLE2[i][n][tmp],resultArray.getMatchCount());
							}							
						}
					}
				}
			}
		}
	}

}
