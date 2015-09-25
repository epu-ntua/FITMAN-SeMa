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
import de.wdilab.coma.insert.metadata.XDRParser;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.repository.TRepository;
import de.wdilab.coma.structure.Graph;

/**
 * @author Sabine Massmann
 */
public class TXDRParser extends TestCase {
	
	// coma-project\coma-engine\resources\Sources\
	String directory = (new File ("")).getAbsolutePath() + "\\resources\\Sources\\";
	
	static Object[][] data= {
			// small schemas
			{"PO_xdr/Apertum.xdr", 183, 101},
			{"PO_xdr/CIDXPOSCHEMA.xdr", 69, 52},
			{"PO_xdr/Excel.xdr", 72, 63},
			{"PO_xdr/Noris.xdr", 96, 50},
			{"PO_xdr/Paragon.xdr", 148, 136},	

			// large schemas
			{"Xcbl35/Xcbl35_ChangeOrder.xdr", 1527, 856}, 
			{"Xcbl35/Xcbl35_Invoice.xdr", 1821, 1047}, 
			{"Xcbl35/Xcbl35_Order.xdr", 1457, 813}, 
			{"Xcbl35/Xcbl35_OrderResponse.xdr", 1648, 933}, 
			{"Xcbl35/Xcbl35_ProductCatalog.xdr", 431, 243}
	};
	
	static Object[] data2= {"PO_xdr", "Xcbl35"};
		
	public void test(){
		
		singleImportWithoutDB();
		
		TRepository.setDatabaseProperties();
		
		Repository rep = new Repository();
		rep.dropRepositorySchema();
		rep.createRepositorySchema();
		
		singleImport();
		TestParser.testData(rep, directory, data);
		
		multipleImport();
		TestParser.testData(rep, directory, data);
		
		multipleImport2();
		TestParser.testData(rep, directory, data);
	}
	
	public void singleImportWithoutDB(){
		XDRParser par = new XDRParser(false);
		for (int i = 0; i < data.length; i++) {		
			String file = directory+(String) data[i][0];
			par.parseSingleSource(file);
			Graph graph = par.getGraph();
			TestParser.testData(graph, data[i]);
		}
	}
	
	public void singleImport(){
		XDRParser par = new XDRParser(true);
		for (int i = 0; i < data.length; i++) {		
			String file = directory+(String) data[i][0];
			par.parseSingleSource(file);
		}
	}

	public void multipleImport(){
		XDRParser par = new XDRParser(true);
		String[] files = new String[data.length];
		for (int i = 0; i < data.length; i++) {		
			files[i] = directory+(String) data[i][0];			
		}
		par.parseMultipleSources(files);
	}
	
	public void multipleImport2(){
		XDRParser par = new XDRParser(true);
		for (int i = 0; i < data2.length; i++) {		
			par.parseMultipleSources(directory+(String) data2[i]);	
		}
		
	}
	
}
