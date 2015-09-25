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

import java.io.File;

import junit.framework.TestCase;
import de.wdilab.coma.insert.TestParser;
import de.wdilab.coma.insert.metadata.SQLParser;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.repository.TRepository;
import de.wdilab.coma.structure.Graph;

/**
 * @author Sabine Massmann
 */
public class TSQLParser extends TestCase {

	// coma-project\coma-engine\resources\Sources\SQL
	String directory = (new File ("")).getAbsolutePath() + "\\resources\\Sources\\SQL\\";
	
	static Object[][] data= {
			{"produktgruppe.sql", 4, 3},
			{"db-dump.sql", 20, 16},
			{"phpmyadmin 20100208 1846.sql", 50, 42}
	};
	
	public void test(){
		singleImportWithoutDB();
		
		TRepository.setDatabaseProperties();
		
		Repository rep = new Repository();
		rep.createRepositorySchema();
		
		singleImport();
		TestParser.testData(rep, directory, data);
		
		multipleImport();
		TestParser.testData(rep, directory, data);
	}
	
	public void singleImportWithoutDB(){
		SQLParser par = new SQLParser(false);
		for (int i = 0; i < data.length; i++) {		
			String file = directory+(String) data[i][0];
			par.parseSingleSource(file);
			Graph graph = par.getGraph();
			TestParser.testData(graph, data[i]);
		}
	}
	
	public void singleImport(){
		SQLParser par = new SQLParser(true);
		for (int i = 0; i < data.length; i++) {		
			String file = directory+(String) data[i][0];
			par.parseSingleSource(file);
		}
	}
	

	public void multipleImport(){
		SQLParser par = new SQLParser(true);
		String[] files = new String[data.length];
		for (int i = 0; i < data.length; i++) {		
			files[i] = directory+(String) data[i][0];			
		}
		par.parseMultipleSources(files);
	}
	
}
