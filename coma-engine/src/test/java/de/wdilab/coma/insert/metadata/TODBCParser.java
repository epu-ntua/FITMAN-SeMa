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

package de.wdilab.coma.insert.metadata;

import junit.framework.TestCase;
import de.wdilab.coma.insert.TestParser;
import de.wdilab.coma.insert.metadata.ODBCParser;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.repository.TRepository;
import de.wdilab.coma.structure.Graph;

/**
 * @author Sabine Massmann
 */
public class TODBCParser extends TestCase {

	static Object[][] data= {
		// To Do: Create connnections first - otherwise negative test
		
		// MySQL ODBC 3.51 Driver
			{"coma-project", 42, 37},
			
		// MySQL ODBC 5.1 Driver
			{"coma-project2", 42, 37},

	};
	
	
	public void test(){
		TRepository.setDatabaseProperties();
		
//		singleImportWithoutDB();
		
		Repository rep = new Repository();
		rep.dropRepositorySchema();
		rep.createRepositorySchema();
		
		singleImport();
		// Problem: in debug it is working but in run mode
		// Problem behind it: odbc driver works (currently) only with 32bit java
		// with 64bit java the following problem occurs
		// [Microsoft][ODBC Driver Manager] Ungueltige Zeichenfolgen- oder Pufferlaenge
		TestParser.testData(rep, "", data);
		
		singleImportWithoutDB();
		
//		testMultipleImport(rep, data);
	}
	
	public void singleImportWithoutDB(){
		ODBCParser par = new ODBCParser(false);
		for (int i = 0; i < data.length; i++) {		
			par.parseSingleSource((String)data[i][0], "root", "", (String)data[i][0]);
			Graph graph = par.getGraph();
			graph.print();
			TestParser.testData(graph, data[i]);
		}
	}
	
	public void singleImport(){
		ODBCParser par = new ODBCParser(true);
		for (int i = 0; i < data.length; i++) {		
			par.parseSingleSource((String)data[i][0], "root", "", (String)data[i][0]);
		}
	}
	

//	public void testMultipleImport(Repository rep, Object[][] data){
//		ODBCParser par = new ODBCParser(true);
//		String[] files = new String[data.length];
//		for (int i = 0; i < data.length; i++) {		
//			files[i]  = (String)data[i][0];
//		}
//		par.parseMultipleSources(files, "", "");
//		TestParser.testData(rep, "", data);
//	}
	
}
