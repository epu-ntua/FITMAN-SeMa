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
import de.wdilab.coma.insert.metadata.XSDParser;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.repository.TRepository;
import de.wdilab.coma.structure.Graph;

/**
 * @author Sabine Massmann
 */
public class TXSDParser extends TestCase {

	// coma-project\coma-engine\resources\Sources\
	String directory = (new File ("")).getAbsolutePath() + "\\resources\\Sources\\";
	static Object[][] data= {
			{"poxsd/po.xsd", 24, 18},
			{"BMECat/bmecat_newcat.xsd", 394, 205},
			{"BMECat/bmecat_price.xsd", 381, 193},
			{"BMECat/bmecat_product.xsd", 392, 203},
			{"OpenTrans/OpenTrans_INVOICE.xsd", 367, 188},
			{"OpenTrans/OpenTrans_ORDER.xsd", 348, 171},
			{"OpenTrans/OpenTrans_ORDERCHANGE.xsd", 344, 167},
			{"OpenTrans/OpenTrans_ORDERRESPONSE.xsd", 355, 177},
	};
	
	static Object[][] data2= {
			{"BMECatAll", 429, 233},
			{"OpenTransAll", 614, 395},
			// "Xcbl" Composite -> TODO: same schema
			{"XcblCore", 1063, 719},
			{"XcblOrder", 494, 410},
			{"XcblCatalog", 193, 142},
	};
	
	
	public void test(){
		singleImportWithoutDB();		
		
		compositeImportWithoutDB();
		
		TRepository.setDatabaseProperties();
		
		Repository rep = new Repository();
		rep.dropRepositorySchema();
		rep.createRepositorySchema();
		
		singleImport();
		TestParser.testData(rep, directory, data);
		
		multipleImport();
		TestParser.testData(rep, directory, data);

		compositeImport();
		TestParser.testData(rep, directory, data2);
	}
	
	public void singleImportWithoutDB(){
		XSDParser par = new XSDParser(false);
		for (int i = 0; i < data.length; i++) {		
			String file = directory+(String) data[i][0];
			par.parseSingleSource(file);
			Graph graph = par.getGraph();
			TestParser.testData(graph, data[i]);
		}
	}
	
	public void compositeImportWithoutDB(){
		XSDParser par = new XSDParser(false);
		for (int i = 0; i < data2.length; i++) {	
			par.parseCompositeSources(directory+data2[i][0], null);
			Graph graph = par.getGraph();
			TestParser.testData(graph, data2[i]);
		}
	}
	
	public void singleImport(){
		XSDParser par = new XSDParser(true);
		for (int i = 0; i < data.length; i++) {		
			String file = directory+(String) data[i][0];
			par.parseSingleSource(file);
		}
	}

	public void multipleImport(){
		XSDParser par = new XSDParser(true);
		String[] files = new String[data.length];
		for (int i = 0; i < data.length; i++) {		
			files[i] = directory+(String) data[i][0];			
		}
		par.parseMultipleSources(files);
	}
	
	public void compositeImport(){
		XSDParser par = new XSDParser(true);
		for (int i = 0; i < data2.length; i++) {	
			par.parseCompositeSources(directory+data2[i][0], null);
		}
	}
	
}
