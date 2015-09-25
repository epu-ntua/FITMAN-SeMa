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

import junit.framework.TestCase;
import de.wdilab.coma.center.Manager;
import de.wdilab.coma.insert.metadata.OWLParser_V3;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.TRepository;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;

/**
 * @author Sabine Massmann
 */
public class TRDFAlignmentParser extends TestCase {

	// coma-project\coma-engine\resources\Sources\
	static	String directory = (new File ("")).getAbsolutePath() + "\\resources\\Sources\\";
	
	static String[][] data3= {
		{"anatomy/mouse_2010.owl", "anatomy/nci_2010.owl", "anatomy/reference_2010.rdf", "1520"},
		{"anatomy/mouse_2011.owl", "anatomy/human_2011.owl", "anatomy/reference_2011.rdf", "1516"},
	};
	
	public static void testWithoutDatabase(){
		for (int i = 0; i < data3.length; i++) {
			boolean dbInsert = false;
			OWLParser_V3 par = new OWLParser_V3(dbInsert);
			String srcFile = directory+ data3[i][0];
			par.parseSingleSource(srcFile);
			Graph srcGraph = par.getGraph();
			String trgFile = directory+ data3[i][1];
			par.parseSingleSource(trgFile);
			Graph trgGraph = par.getGraph();
			
			RDFAlignmentParser parser = new RDFAlignmentParser(null, dbInsert);
			String alignFile = directory+data3[i][2];
			MatchResult result = parser.loadOWLAlignmentFile(null, alignFile, srcGraph, trgGraph);
			assertEquals(Integer.parseInt(data3[i][3]), result.getMatchCount());
		}
	}
	
	
	public static void testWithDatabase(){
		TRepository.setDatabaseProperties();
		boolean dbInsert = true;
		Manager manager = new Manager();
		
		DataImport importer = manager.getImporter();
		importer.dropRepositorySchema();
		importer.createRepositorySchema();
		for (int i = 0; i < data3.length; i++) {
			OWLParser_V3 par = new OWLParser_V3(dbInsert);
			String srcFile = (directory+ data3[i][0]).replace("\\", "/");
			int srcId = par.parseSingleSource(srcFile);
			String trgFile = (directory+ data3[i][1]).replace("\\", "/");
			int trgId = par.parseSingleSource(trgFile);
			
			manager.loadRepository();
			
			RDFAlignmentParser parser = new RDFAlignmentParser(manager, dbInsert);
			String alignFile = directory+data3[i][2];
			parser.loadOWLAlignmentFile(alignFile, null, srcFile, trgFile);
			MatchResult result = manager.loadMatchResult(srcId, trgId, parser.getMappingId());
			assertEquals(Integer.parseInt(data3[i][3]), result.getMatchCount());
		}

		manager.closeDatabaseConnection();
	}
	
}
