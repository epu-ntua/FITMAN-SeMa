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

package de.wdilab.coma.insert.relationships;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import junit.framework.TestCase;
import de.wdilab.coma.insert.metadata.OWLParser_V3;
import de.wdilab.coma.insert.metadata.XDRParser;
import de.wdilab.coma.insert.relationships.MatchResultParser;
import de.wdilab.coma.center.Manager;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.TRepository;
import de.wdilab.coma.structure.MatchResult;

/**
 * @author Sabine Massmann
 */
public class TMatchResultParser extends TestCase {

	// coma-project\coma-engine\resources\Sources\
	String directory = (new File ("")).getAbsolutePath() + "\\resources\\Sources\\";
	
	static String[][] data_small= {
		//webdirectory
		{"webdirectories/dmoz.Freizeit.owl","dmoz_Freizeit"},
		{"webdirectories/Google.Freizeit.owl","Google_Freizeit"},
		{"webdirectories/Google.Lebensmittel.owl","Google_Lebensmittel"},
		{"webdirectories/web.Lebensmittel.owl","web_Lebensmittel"},
	};
	
	static String[][] data_large= {
		//webdirectory
		{"webdirectories/dmoz.Freizeit.owl","dmoz_Freizeit"},
		{"webdirectories/Google.Freizeit.owl","Google_Freizeit"},
		{"webdirectories/Google.Lebensmittel.owl","Google_Lebensmittel"},
		{"webdirectories/web.Lebensmittel.owl","web_Lebensmittel"},

		{"webdirectory/dmoz.owl","webdirectory_dmoz_owl"},
		{"webdirectory/google.owl","webdirectory_google_owl"},
		{"webdirectory/web.owl","webdirectory_web_owl"},
		{"webdirectory/yahoo.small.owl","webdirectory_yahoo_small_owl"},
	};
	
	static String dataMR1_small= "Mappings/mappings-WebDir.txt";
	static int[] sizeMR1_small = {67,32};
	static String dataMR1_large= "webdirectory/webdirectory_Mappings.small.txt";
	static int[] sizeMR1_large = {729,218,356,211,340,197};
	
	static String data2= "PO_xdr";
	static String dataMR2= "Mappings/mappings-PO.txt";
	static int[] sizeMR2 = {65,32,49,54,50,60,79,45,85,66};

	DataImport importer = null;
	Manager manager = null;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		TRepository.setDatabaseProperties();
		manager = new Manager();
		// webdirectories small
		importer = new DataImport();
		importer.dropRepositorySchema();
		importer.createRepositorySchema();
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		importer.closeDatabaseConnection();
		manager.getAccessor().closeDatabaseConnection();
		manager.getImporter().closeDatabaseConnection();
	}
	
	public void testWebdirectorySmall(){
		OWLParser_V3 par = new OWLParser_V3(true);
		for (int i = 0; i < data_small.length; i++) {		
			String file = directory+ data_small[i][0];
			par.parseSingleSource(file, data_small[i][1]);
		}
		
		manager.loadRepository();
		MatchResultParser parser = new MatchResultParser(manager);
		
		ArrayList<MatchResult> results = parser.loadMatchResultFile(directory+dataMR1_small);

		assertEquals(sizeMR1_small.length, results.size());
	
		DataAccess access = manager.getAccessor();
		for (int i = 0; i < results.size(); i++) {
			MatchResult current = results.get(i);
			assertEquals(sizeMR1_small[i], current.getMatchCount());
//			System.out.println(current.getMatchCount());
			importer.saveMatchResult(current);
			
			HashSet<Integer> ids = access.getSourceRelIds(current.getSourceGraph().getSource().getId(), current.getTargetGraph().getSource().getId());
			MatchResult resultLoaded1 = access.loadMatchResult(current.getSourceGraph(), current.getTargetGraph(), ids.iterator().next());
			assertEquals(current.getMatchCount(), resultLoaded1.getMatchCount());
		}	
	}

	public void testWebdirectoryLarge(){
		// webdirectories large
		OWLParser_V3 par = new OWLParser_V3(true);
		for (int i = 0; i < data_large.length; i++) {		
			String file = directory+ data_large[i][0];
			par.parseSingleSource(file, data_large[i][1]);
		}
		
		manager.loadRepository();
		MatchResultParser parser = new MatchResultParser(manager);
		
		ArrayList<MatchResult> results = parser.loadMatchResultFile(directory+dataMR1_large);

		assertEquals(sizeMR1_large.length, results.size());
		
		DataAccess access = manager.getAccessor();
		
		for (int i = 0; i < results.size(); i++) {
			MatchResult current = results.get(i);
//			assertEquals(sizeMR1[i], current.getMatchCount());
			System.out.println(current.getMatchCount());
			importer.saveMatchResult(current);
			
			HashSet<Integer> ids = access.getSourceRelIds(current.getSourceGraph().getSource().getId(), current.getTargetGraph().getSource().getId());
			MatchResult resultLoaded1 = access.loadMatchResult(current.getSourceGraph(), current.getTargetGraph(), ids.iterator().next());
			assertEquals(current.getMatchCount(), resultLoaded1.getMatchCount());
		}	
		importer.closeDatabaseConnection();
	}
	
	public void testPO(){
		XDRParser par = new XDRParser(true);
		par.parseMultipleSources(directory+ data2);
		
		manager.loadRepository();
		MatchResultParser parser = new MatchResultParser(manager);
		// PO 
		ArrayList<MatchResult> results = parser.loadMatchResultFile(directory+dataMR2);
		assertEquals(10, results.size());
					

		for (int i = 0; i < results.size(); i++) {
			MatchResult current = results.get(i);
			assertEquals(sizeMR2[i], current.getMatchCount());
//			System.out.println(current.getMatchCount());
			importer.saveMatchResult(current);
		}	
	}
	
}
