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

package de.wdilab.coma.insert;

import java.util.HashSet;
import java.util.Iterator;

import junit.framework.TestCase;
import de.wdilab.coma.insert.metadata.ODBCParser;
import de.wdilab.coma.insert.metadata.TCSVParser;
import de.wdilab.coma.insert.metadata.TListParser;
import de.wdilab.coma.insert.metadata.TODBCParser;
import de.wdilab.coma.insert.metadata.TOWLParserV3;
import de.wdilab.coma.insert.metadata.TSQLParser;
import de.wdilab.coma.insert.metadata.TXDRParser;
import de.wdilab.coma.insert.metadata.TXSDParser;
import de.wdilab.coma.insert.relationships.TAccMatchResultParser;
import de.wdilab.coma.insert.relationships.TMatchResultParser;
import de.wdilab.coma.insert.relationships.TRDFAlignmentParser;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.repository.TRepository;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * @author Sabine Massmann
 */
public class TestParser extends TestCase {


	
	public void testParser(){
		
		TRepository.setDatabaseProperties();
		
		Repository rep = new Repository();
		rep.dropRepositorySchema();		
		rep.createRepositorySchema();		
		rep.closeDatabaseConnection();
		
		TCSVParser csv = new TCSVParser();
		csv.test();
		
		TListParser list= new TListParser();
		list.test();
		
		TODBCParser odbc= new TODBCParser();
		odbc.test();
		 
		TOWLParserV3 owl= new TOWLParserV3();
		owl.test();
		
		TSQLParser sql= new TSQLParser();
		sql.test();
		
		TXDRParser xdr= new TXDRParser();
		xdr.test();		
		
		TXSDParser xsd= new TXSDParser();
		xsd.test();
		
		
		TAccMatchResultParser mrAcc = new TAccMatchResultParser();
		mrAcc.testWithDatabase();
		mrAcc.testWithoutDatabase();

		TMatchResultParser mr = new TMatchResultParser();
		mr.testWebdirectorySmall();
		mr.testWebdirectoryLarge();
		mr.testPO();
		
		TRDFAlignmentParser rdf = new TRDFAlignmentParser();
		rdf.testWithDatabase();
		rdf.testWithoutDatabase();

	}
	
	
	public static void testData(Repository rep, String directory, Object[][] data){
		for (int i = 0; i < data.length; i++) {		
			String file = directory+(String) data[i][0];	
			String odbc = ODBCParser.DB_URL + data[i][0] + ":" + data[i][0];
			HashSet<Source> sources = rep.getSources();
			for (Iterator<Source> iterator = sources.iterator(); iterator.hasNext();) {
				Source source = iterator.next();
				if (source.getProvider()!=null && (source.getProvider().equals(file)||source.getProvider().equals(odbc))){			
					int sourceId = source.getId();
					int rel = rep.getSourceRelId_ISA(sourceId, sourceId);
					assertTrue(rel!=SourceRelationship.UNDEF);
					HashSet<Integer> list = rep.getObjectIds(sourceId);
					assertEquals(data[i][1], list.size());
					Integer sourcerel_id = rep.getSourceRelId_ISA(sourceId, sourceId);
					int size = rep.getSourceRelCount(sourcerel_id);
					assertEquals(data[i][2], size);
					rep.deleteSourceWithSourceRel(sourceId);
				}
			}
		}
	}
	
	public static void testData(Graph graph, Object[] data){
		graph = graph.getGraph(Graph.PREP_LOADED);
		int nodeCnt = graph.getElementCount();
		assertEquals(data[1], nodeCnt);
		int edgeCnt = graph.getEdgesCount();
		assertEquals(data[2], edgeCnt);
	}
	
}
