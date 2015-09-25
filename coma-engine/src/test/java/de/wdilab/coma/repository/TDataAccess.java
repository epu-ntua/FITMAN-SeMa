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

package de.wdilab.coma.repository;

import java.io.File;

import junit.framework.TestCase;
import de.wdilab.coma.center.TManager;
import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;
import de.wdilab.coma.structure.graph.DirectedGraphImpl;

/**
 * @author Sabine Massmann
 */
public class TDataAccess extends TestCase {

	static String directory = (new File ("")).getAbsolutePath() + "\\resources\\";
	static boolean loadForeignTypes=false;
	static boolean preprocess=true;
	static boolean loadInstances=true; 
	
	
	public void testRepository(){
		
		TRepository.setDatabaseProperties();

		DataAccess dataAccess = new DataAccess();
		dataAccess.dropRepositorySchema();		
		dataAccess.createRepositorySchema();
		
		boolean dbInsert = true;
		for (int i = 0; i <TManager.SCHEMAINFO.length; i++) {
//			String filename = "file:/" + directory + objectgraph[i][0];
			String filename = directory + TManager.SCHEMAINFO[i][0];
			InsertParser.parseSingleSource(filename, dbInsert);
		}
		System.out.println("Import Done.");

		
		for (int i = 0; i < TManager.SCHEMAINFO.length; i++) {
//			String provider = "file:/" + directory + objectgraph[i][0];
			String provider = ( directory +TManager.SCHEMAINFO[i][0]).replace("\\", "/");
			
			// for independent test insert parser here
			int source_id = dataAccess.getSourceId(provider);
			Source source = dataAccess.getSource(source_id);
			System.out.println(source);
			
			int sourcerel_id = dataAccess.getSourceRelId_ISA(source_id, source_id);
			SourceRelationship sourcerel = dataAccess.getSourceRel(sourcerel_id);
			DirectedGraphImpl graph = dataAccess.loadStructure(source, sourcerel);
			
			assertEquals(TManager.SCHEMAINFO[i][1], graph.getElementCount());
			assertEquals(TManager.SCHEMAINFO[i][2], graph.getEdgesCount());
		}
		
		
		dataAccess.closeDatabaseConnection();		
	}
	



}
