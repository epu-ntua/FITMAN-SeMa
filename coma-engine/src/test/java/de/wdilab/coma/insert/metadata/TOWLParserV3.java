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
import de.wdilab.coma.insert.metadata.OWLParser_V3;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.repository.TRepository;
import de.wdilab.coma.structure.Graph;

/**
 * @author Sabine Massmann
 */
public class TOWLParserV3 extends TestCase {

	// coma-project\coma-engine\resources\Sources\
	String directory = (new File ("")).getAbsolutePath() + "\\resources\\Sources\\";
	
	static Object[][] data= {
			// Purchaseorder
			{"Po_Owl/order.owl", 48, 49},  // in COMA++ 48, 41

			//OAEI
			{"oaei/2010/benchmarks/101/onto.rdf", 101, 124}, // in COMA++ 105, 116
			{"oaei/2010/benchmarks/103/onto.rdf", 101, 115}, // in COMA++ 105, 106
			{"oaei/2010/benchmarks/104/onto.rdf", 101, 105}, // in COMA++ 105, 95
			{"oaei/2010/benchmarks/201/onto.rdf", 101, 124}, // in COMA++ 105, 116
			{"oaei/2010/benchmarks/202/onto.rdf", 101, 124}, // in COMA++ 105, 116
			{"oaei/2010/benchmarks/203/onto.rdf", 101, 124}, // in COMA++ 105, 116
			{"oaei/2010/benchmarks/204/onto.rdf", 101, 124}, // in COMA++ 105, 116
			{"oaei/2010/benchmarks/301/onto.rdf", 55, 54}, // in COMA++ 55, 58
			{"oaei/2010/benchmarks/302/onto.rdf", 46, 46}, // in COMA++ 46, 41
			{"oaei/2010/benchmarks/303/onto.rdf", 126, 92}, // in COMA++ 126, 101
			{"oaei/2010/benchmarks/304/onto.rdf", 88, 139}, // in COMA++ 95, 105
		
			//webdirectory
			{"webdirectories/dmoz.Freizeit.owl", 71, 70},
			{"webdirectories/Google.Freizeit.owl", 67, 66},
			{"webdirectories/Google.Lebensmittel.owl", 59, 58},
			{"webdirectories/web.Lebensmittel.owl", 53, 52},
			// Anatomy
			{"anatomy/mouse_2010.owl", 2746, 3444},
			{"anatomy/nci_2010.owl", 3306, 5423}
	};
	
	public void test(){
		TRepository.setDatabaseProperties();
		
		Repository rep = new Repository();
		rep.dropRepositorySchema();
		rep.createRepositorySchema();

		singleImport();
		TestParser.testData(rep, directory, data);

		multipleImport();
		TestParser.testData(rep, directory, data);
	}
	
	
	public void test2(){
		singleImportWithoutDB();
	}
	
	public void singleImportWithoutDB(){
		OWLParser_V3 par = new OWLParser_V3(false);
		for (int i = 0; i < data.length; i++) {		
			String file = "file:/" + directory+(String) data[i][0];
			par.parseSingleSource(file);
			Graph graph = par.getGraph();		
			TestParser.testData(graph, data[i]);
		}
	}
	
	public void singleImport(){
		OWLParser_V3 par = new OWLParser_V3(true);
		for (int i = 0; i < data.length; i++) {		
			String file = "file:/" + directory+(String) data[i][0];
			par.parseSingleSource(file);
		}
	}

	public void multipleImport(){
		OWLParser_V3 par = new OWLParser_V3(true);
		String[] files = new String[data.length];
		for (int i = 0; i < data.length; i++) {		
			files[i] = directory+(String) data[i][0];			
		}
		par.parseMultipleSources(files);
	}
	
}
