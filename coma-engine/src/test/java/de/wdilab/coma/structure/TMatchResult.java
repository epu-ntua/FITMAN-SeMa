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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import junit.framework.TestCase;

/**
 * @author Sabine Massmann
 */
public class TMatchResult extends TestCase {
	static final long SEED = 1234567;
	static final float DELTA = (float) 0.00001;
	static final int SRC_SIZE = 100;
	static final int TRG_SIZE = 50;
	static final int CNT1 = 1220;
	static final int CNT2 = 659;
	static final int[] OP_CNT = { 332, 888, 1547 };
	static final int[] OP_CNT_SINGLE1 = { 9620 };
	static final int[] OP_CNT_SINGLE2 = { 5260 };
	
	static final int[] RESTR_CNT = {444, 211 };

	// MYSQL
	public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://localhost/coma-project?useUnicode=true&characterEncoding=UTF-8";
	public static final String DB_USER = "";
	public static final String DB_PASSWORD = "";

	Connection connection = null;
	Statement statement = null;

	protected void setUp() {
		if (connection == null || statement == null) {
			try {
				Class.forName(DB_DRIVER);
				connection = DriverManager.getConnection(DB_URL, DB_USER,
						DB_PASSWORD);
				statement = connection.createStatement();
			} catch (SQLException e) {
				System.out.println("setUp(): " + e.getMessage());
			} catch (Exception e) {
				System.out.println("setUp(): " + e.getMessage());
			}
		}
	}
	
	static void deleteMatchResultTables(Statement statement){
		try {
		// MYSQL
		String[] tableTypes = { "TABLE"};
		DatabaseMetaData metadata = statement.getConnection().getMetaData();
		ResultSet trs = metadata.getTables(null, "%", "%", tableTypes);
		// delete matchresult tables
        while (trs.next()) {
            String tableName = trs.getString("TABLE_NAME");
	        if (tableName.startsWith("matchresult")){
//	        	System.out.println(tableName);
	        	statement.execute("DROP TABLE " + tableName);
	        }
          }
		} catch (SQLException e) {
			System.out.println("TMatchResult.deleteMatchResultView() Error droping table " +  e.getMessage());
		}
	}
	
	static void deleteMatchResultView(Statement statement){
		try {
		// MYSQL
		String[] tableTypes = { "VIEW"};
		DatabaseMetaData metadata = statement.getConnection().getMetaData();
		ResultSet trs = metadata.getTables(null, "%", "%", tableTypes);
		// delete matchresult tables
        while (trs.next()) {
            String tableName = trs.getString("TABLE_NAME");
	        if (tableName.toLowerCase().contains("matchresult")){
//	        	System.out.println(tableName);
	        	statement.execute("DROP VIEW " + tableName);
	        }
          }
		} catch (SQLException e) {
			System.out.println("TMatchResult.deleteMatchResultView() Error deleting view " +  e.getMessage());
		}
	}

	protected void tearDown() {
			deleteMatchResultView(statement);
			deleteMatchResultTables(statement);
			
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				System.out.println("TMatchResult.tearDown() Error closing statement or connection " +  e.getMessage());
			}
			statement=null;
			connection=null;
	}

	public void testExampleMatchResult() {
		for (int i = 0; i < MatchResult.OP.length; i++) {
			assertEquals(MatchResult.OP[i],
					MatchResult.stringToOperation(MatchResult
							.operationToString(MatchResult.OP[i])));
		}

		MatchResult resultArrayEmpty = new MatchResultArray();

		assertEquals(0, resultArrayEmpty.getMatchCount());

		ArrayList<Object> srcObjects = new ArrayList<Object>();
		for (int i = 0; i < SRC_SIZE; i++) {
			srcObjects.add(new Element(i));
		}
		assertTrue(srcObjects.size() == SRC_SIZE);
		ArrayList<Object> trgObjects = new ArrayList<Object>();
		for (int i = 0; i < TRG_SIZE; i++) {
			trgObjects.add(new Element(i));
		}
		assertTrue(trgObjects.size() == TRG_SIZE);

		testExampleMatchResult(srcObjects, trgObjects, resultArrayEmpty,
				new int[] { 0 });

		System.out.println("Test Creation");

		Random r = new Random(SEED); // For reproducible testing
		float[][] simMatrix1 = new float[SRC_SIZE][TRG_SIZE];
		for (int i = 0; i < SRC_SIZE; i++) {
			for (int j = 0; j < TRG_SIZE; j++) {
				float sim = r.nextFloat();
				if (((i + j) % 2 == 0 && (i + j) % 3 == 0) || (i + j) % 11 == 0) {
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

		MatchResultArray resultArray1 = new MatchResultArray(srcObjects,
				trgObjects, simMatrix1);
		MatchResultArray resultArray2 = new MatchResultArray(srcObjects,
				trgObjects, simMatrix2);
		assertEquals(CNT1, resultArray1.getMatchCount());
		assertEquals(CNT2, resultArray2.getMatchCount());

		for (int i = 0; i < SRC_SIZE; i++) {
			Object srcObject = srcObjects.get(i);
			for (int j = 0; j < TRG_SIZE; j++) {
				Object trgObject = trgObjects.get(j);
				assertEquals(simMatrix1[i][j],
						resultArray1.getSimilarity(srcObject, trgObject));
				assertEquals(simMatrix2[i][j],
						resultArray2.getSimilarity(srcObject, trgObject));
			}
		}

		System.out.println("Test Operations with 2 MatchResults ");
		testExampleMatchResult(srcObjects, trgObjects, resultArray1,resultArray2);
		assertEquals(CNT1, resultArray1.getMatchCount());
		assertEquals(CNT2, resultArray2.getMatchCount());

		System.out.println("Test Operations with 1 MatchResult");
		testExampleMatchResult(srcObjects, trgObjects, resultArray1,
				OP_CNT_SINGLE1);
		testExampleMatchResult(srcObjects, trgObjects, resultArray2,
				OP_CNT_SINGLE2);
		
		System.out.println("Test Remove and Restriction");
		testExampleMatchResultRemove(srcObjects, trgObjects, resultArray1);
		testExampleMatchResultRemove(srcObjects, trgObjects, resultArray2);
		
	}
	

	
	void testExampleMatchResultRestrict(ArrayList<Object> srcObjects,
			ArrayList<Object> trgObjects, MatchResult resultArray,MatchResult resultDB, int restr_cnt){
		ArrayList<Object> srcObjectsPart = new ArrayList<Object>();
		for (int i = 0; i < SRC_SIZE; i++) {
			if (i%2==0){
				srcObjectsPart.add(srcObjects.get(i));
			}
		}
		ArrayList<Object> trgObjectsPart = new ArrayList<Object>();
		for (int j = 0; j < TRG_SIZE; j++) {
			if (j%2==0){
				trgObjectsPart.add(trgObjects.get(j));
			}
		}		
		
		MatchResult	resultTmpArray = MatchResult.restrict(resultArray, srcObjectsPart, trgObjectsPart);
		assertEquals(restr_cnt, resultTmpArray.getMatchCount());
		MatchResult	resultTmpDB = MatchResult.restrict(resultDB, srcObjectsPart, trgObjectsPart);
		assertEquals(restr_cnt, resultTmpDB.getMatchCount());
		equal(resultTmpArray, resultTmpDB);
	}

	
	void testExampleMatchResultRemove(ArrayList<Object> srcObjects,
			ArrayList<Object> trgObjects, MatchResult result){
		
		int cnt = result.getMatchCount();
		int removeCnt = cnt/10;
		MatchResult resultTmp = result.clone();
				
		for (int i = 0; i < SRC_SIZE; i++) {
			Object srcObject = srcObjects.get(i);
			for (int j = 0; j < TRG_SIZE; j++) {
				Object trgObject = trgObjects.get(j);				
				float sim = result.getSimilarity(srcObject, trgObject);
				if (sim>0){
					result.remove(srcObject, trgObject);
					removeCnt--;
					if (removeCnt==0){
						return;
					}
				}
			}
		}
		
		assertEquals(cnt-removeCnt, resultTmp.getMatchCount());
	}
	
	void equal(MatchResult resultArray, MatchResult resultDB) {
		ArrayList<Object> arraySrcMatch = resultArray.getSrcMatchObjects();
		ArrayList<Object> dbSrcMatch = resultDB.getSrcMatchObjects();
		ArrayList<Object> arrayTrgMatch = resultArray.getTrgMatchObjects();
		ArrayList<Object> dbTrgMatch = resultDB.getTrgMatchObjects();
		assertTrue(arraySrcMatch.containsAll(dbSrcMatch));
		assertTrue(dbSrcMatch.containsAll(arraySrcMatch));
		assertTrue(arrayTrgMatch.containsAll(dbTrgMatch));
		assertTrue(dbTrgMatch.containsAll(arrayTrgMatch));
	}

	void testExampleMatchResult(ArrayList<Object> srcObjects,
			ArrayList<Object> trgObjects, MatchResult result1,
			MatchResult result2) {
		for (int k = 0; k < MatchResult.OP_SAMEGRAPHS.length; k++) {
			MatchResult opResult = MatchResult.applyOperation(
					MatchResult.OP_SAMEGRAPHS[k], result1, result2);
			if (opResult == null) {
				return;
			}
			int cnt1 = opResult.getMatchCount();
			assertEquals(OP_CNT[k], cnt1);
			for (int i = 0; i < SRC_SIZE; i++) {
				Object srcObject = srcObjects.get(i);
				for (int j = 0; j < TRG_SIZE; j++) {
					Object trgObject = trgObjects.get(j);
					float sim1 = opResult.getSimilarity(srcObject, trgObject);
					switch (OP_CNT[k]) {
					case MatchResult.OP_DIFF:
						if (sim1 > 0) {
							assertEquals(
									result1.getSimilarity(srcObject, trgObject),
									sim1);
						}
						break;

					case MatchResult.OP_INTERSECT:
						if (sim1 > 0) {
							assertEquals(
									result1.getSimilarity(srcObject, trgObject),
									sim1);
							assertEquals(
									result2.getSimilarity(srcObject, trgObject),
									sim1);
						} else {
							assertTrue(result1.getSimilarity(srcObject,
									trgObject) == 0
									|| result2.getSimilarity(srcObject,
											trgObject) == 0);
						}
						break;
					case MatchResult.OP_MERGE:
						if (sim1 > 0) {
							assertTrue(result1.getSimilarity(srcObject,
									trgObject) == sim1
									|| result2.getSimilarity(srcObject,
											trgObject) == sim1);
						}
						break;
					}
				}
			}
		}

	}

	void testExampleMatchResult(ArrayList<Object> srcObjects,
			ArrayList<Object> trgObjects, MatchResult result, int[] op_cnt) {
		for (int k = 0; k < MatchResult.OP_SINGLEMR.length; k++) {
			MatchResult opResult = MatchResult.applyOperation(
					MatchResult.OP_SINGLEMR[k], result);
			if (opResult == null) {
				continue;
			}
			int cnt = opResult.getMatchCount();
			assertEquals(result.getMatchCount(), cnt);
			for (int i = 0; i < SRC_SIZE; i++) {
				Object srcObject = srcObjects.get(i);
				for (int j = 0; j < TRG_SIZE; j++) {
					Object trgObject = trgObjects.get(j);
					float sim1 = opResult.getSimilarity(trgObject, srcObject);
					switch (MatchResult.OP_SINGLEMR[k]) {
					case MatchResult.OP_TRANSPOSE:
						assertEquals(
								result.getSimilarity(srcObject, trgObject),
								sim1);
						ArrayList<Object> trgMatchObjects1 = result
								.getTrgMatchObjects(srcObject);
						ArrayList<Object> trgMatchObjects2 = opResult
								.getTrgMatchObjects(trgObject);
						ArrayList<Object> srcMatchObjects1 = result
								.getSrcMatchObjects(trgObject);
						ArrayList<Object> srcMatchObjects2 = opResult
								.getSrcMatchObjects(srcObject);
						if (trgMatchObjects1 == null) {
							assertEquals(trgMatchObjects1, srcMatchObjects2);
						} else {
							assertEquals(trgMatchObjects1.size(),
									srcMatchObjects2.size());
						}
						if (trgMatchObjects2 == null) {
							assertEquals(trgMatchObjects2, srcMatchObjects1);
						} else {
							assertEquals(trgMatchObjects2.size(),
									srcMatchObjects1.size());
						}
						break;
					}
				}
			}
			MatchResult composeResult = MatchResult.applyOperation(
					MatchResult.OP_COMPOSE, result, opResult);
			if (composeResult == null) {
				return;
			}
			assertEquals(op_cnt[k], composeResult.getMatchCount());
		}
	}

}
