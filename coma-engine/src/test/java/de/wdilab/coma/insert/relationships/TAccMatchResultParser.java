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
import de.wdilab.coma.structure.SourceRelationship;

/**
 * @author Sabine Massmann
 */
public class TAccMatchResultParser extends TestCase {

	// coma-project\coma-engine\resources\Sources\
	String directory = (new File ("")).getAbsolutePath() + "\\resources\\Sources\\";
	
	static String[][] data= {
			// Anatomy
		{"anatomy/mouse_2010.owl","mouse_2010"},
		{"anatomy/nci_2010.owl","nci_2010"}
	};
	
	static String[][] data2= {
		{"anatomy/partial_2010.txt", "987"},
		{"anatomy/pm_2010.txt","1520"}
	};
	
	public void testWithDatabase(){
		TRepository.setDatabaseProperties();
		
		DataImport importer = new DataImport();
		importer.dropRepositorySchema();
		importer.createRepositorySchema();
		OWLParser_V3 par = new OWLParser_V3(true);
		Graph[] graphs = new Graph[data.length];
		Manager manager = new Manager();
		for (int i = 0; i < data.length; i++) {		
			String file = directory+ data[i][0];
			int id = par.parseSingleSource(file, data[i][1]);
			graphs[i]= manager.loadGraph(id);
		}
		par.closeAll();
		AccMatchResultParser parser = new AccMatchResultParser(importer, false);
		int graphState = Graph.PREP_LOADED;
		for (int i = 0; i < data2.length; i++) {
			MatchResult result =  parser.loadMatchResultFile(directory+data2[i][0],  graphs[0], graphs[1], graphState);
			assertEquals((int)Integer.valueOf(data2[i][1]), result.getMatchCount());
		}
		System.out.println();
		
		 parser = new AccMatchResultParser(importer, true);
		for (int i = 0; i < data2.length; i++) {
			parser.loadMatchResultFile(directory+data2[i][0],  graphs[0], graphs[1], graphState);
			int id = parser.getMappingId();
			manager.loadRepository();
			SourceRelationship sr = manager.getSourceRel(id);
			MatchResult result = manager.loadMatchResult(graphs[0], graphs[1],sr);
			assertEquals((int)Integer.valueOf(data2[i][1]), result.getMatchCount());
		}
		
		importer.closeDatabaseConnection();
	}
	
	public void testWithoutDatabase(){
		TRepository.setDatabaseProperties();
		
		OWLParser_V3 par = new OWLParser_V3(false);
		Graph[] graphs = new Graph[data.length];
		for (int i = 0; i < data.length; i++) {		
			String file = directory+ data[i][0];
			par.parseSingleSource(file, data[i][1]);
			graphs[i]= par.getGraph();
		}
		
		AccMatchResultParser parser = new AccMatchResultParser(null, false);
		for (int i = 0; i < data2.length; i++) {
			MatchResult result =  parser.loadMatchResultFile(directory+data2[i][0], graphs[0], graphs[1]);
			assertEquals((int)Integer.valueOf(data2[i][1]), result.getMatchCount());
		}
	}
	
	
}
