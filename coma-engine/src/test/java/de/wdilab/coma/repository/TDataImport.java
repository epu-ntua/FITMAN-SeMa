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

import java.util.HashSet;

import junit.framework.TestCase;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * @author Sabine Massmann
 */
public class TDataImport extends TestCase {

	public void testRepository(){
		
		TRepository.setDatabaseProperties();
		
		DataImport dataImport = new DataImport();
		dataImport.dropRepositorySchema();		
		dataImport.createRepositorySchema();		
		dataImport.prepareAllStatements();
		
		String[][] source={
				{"Apertum", "XDR", "APERTUM_PO_1", "Sources/Po_xdr/Apertum.xdr", "Tue Aug 24 15:12:30 CEST 2010", null, "PurchaseOrder", null, null, null},
				{"Excel", "XDR", "PurchaseOrder.biz", "Sources/Po_xdr/Excel.xdr", "Tue Aug 24 15:13:30 CEST 2010", null, "PurchaseOrder", null, null, null},
				};
		
		String[][] object={
//				accession, name, type, typespace, kind, comment, synonyms
				// Apertum
				{"1.4", "Supplier", "Supplier", "APERTUM_PO_1", Element.kindToString(Element.KIND_ELEMENT), null, null},
				{"20", "Supplier", null, null,  Element.kindToString(Element.KIND_GLOBTYPE), "Supplier", null},
				// Excel
				{"1.6", "DeliverTo", "DeliverTo", "PurchaseOrder.biz",  Element.kindToString(Element.KIND_ELEMENT), null, null},
				{"44", "DeliverTo", null, null,  Element.kindToString(Element.KIND_GLOBTYPE), null, null},
		};
		
		testInsert(dataImport, source, object);
		
		
		testDelete1(dataImport, source, object);
		
		testDelete2(dataImport, source, object);
				
		dataImport.closeDatabaseConnection();
		
	}
	
	
	public void testInsert(DataImport dataImport, 	String[][] source, String[][] object){
		// **Insert Sources**
		int source_id1 = dataImport.insertSource(source[0][0], source[0][1], source[0][2], source[0][3], source[0][4], source[0][5], source[0][6], source[0][7], source[0][8]);
		// already inserted - so get the same id back
		int tmp = dataImport.insertSource(source[0][0], source[0][1], source[0][2], source[0][3], source[0][4], source[0][5], source[0][6], source[0][7], source[0][8]);
		assertEquals(source_id1, tmp);
		
		int source_id2 = dataImport.insertSource(source[1][0], source[1][1], source[1][2], source[1][3], source[1][4], source[1][5], source[1][6], source[1][7], source[1][8]);
		// already inserted - so get the same id back
		tmp = dataImport.insertSource(source[1][0], source[1][1], source[1][2], source[1][3], source[1][4], source[1][5], source[1][6], source[1][7], source[1][8]);
		assertEquals(source_id2, tmp);

		// **Insert SourceRelationship**
		int sourcerel_id1 = dataImport.insertSourceRel(source_id1, source_id1, SourceRelationship.REL_IS_A, null, null, source[0][3], Graph.PREP_LOADED, source[0][4]);
		dataImport.updateSourceRel(sourcerel_id1, Repository.STATUS_IMPORT_DONE);
		// already inserted - so get the same id back
		tmp = dataImport.insertSourceRel(source_id1, source_id1, SourceRelationship.REL_IS_A, null, null, source[0][3], Graph.PREP_LOADED, source[0][4]);
		assertEquals(sourcerel_id1, tmp);
		
		int sourcerel_id2 = dataImport.insertSourceRel(source_id2, source_id2, SourceRelationship.REL_IS_A, null, null, source[1][3], Graph.PREP_LOADED, source[1][4]);
		dataImport.updateSourceRel(sourcerel_id2, Repository.STATUS_IMPORT_DONE);
		// already inserted - so get the same id back
		tmp = dataImport.insertSourceRel(source_id2, source_id2, SourceRelationship.REL_IS_A, null, null, source[1][3], Graph.PREP_LOADED, source[1][4]);
		assertEquals(sourcerel_id2, tmp);
		
		int sourcerel_id3 = dataImport.insertSourceRel(source_id1, source_id2, SourceRelationship.REL_MATCHRESULT, "Apertum_Excel", "Test_Mapping", null,Graph.PREP_RESOLVED, "Tue Aug 24 15:14:30 CEST 2010");
		dataImport.updateSourceRel(sourcerel_id3, Repository.STATUS_IMPORT_DONE);
		// already inserted - so get the same id back
		tmp = dataImport.insertSourceRel(source_id1, source_id2, SourceRelationship.REL_MATCHRESULT, "Apertum_Excel", "Test_Mapping", null,Graph.PREP_RESOLVED, "Tue Aug 24 15:14:30 CEST 2010");
		assertEquals(sourcerel_id3, tmp);
		
		
		source_id1 = dataImport.getSourceId(source[0][0], source[0][3]);
		source_id2 = dataImport.getSourceId(source[1][0], source[1][3]);
		HashSet<Integer> list = dataImport.getSourceRelId(source_id1);
		assertEquals(2, list.size());
		list = dataImport.getSourceRelId(source_id2);
		assertEquals(2, list.size());
		
		// **Insert Object**
		int object_id1 = dataImport.insertObject(sourcerel_id1, object[0][0], object[0][1], object[0][2], object[0][3],  Element.stringToKind(object[0][4]), object[0][5], object[0][6]);
		tmp = dataImport.insertObject(sourcerel_id1, object[0][0], object[0][1], object[0][2], object[0][3], Element.stringToKind(object[0][4]), object[0][5], object[0][6]);
		assertEquals(object_id1, tmp);
		
		int object_id2 = dataImport.insertObject(sourcerel_id1, object[1][0], object[1][1], object[1][2], object[1][3], Element.stringToKind(object[1][4]), object[1][5], object[1][6]);
		tmp = dataImport.insertObject(sourcerel_id1, object[1][0], object[1][1], object[1][2], object[1][3], Element.stringToKind(object[1][4]), object[1][5], object[1][6]);
		assertEquals(object_id2, tmp);
		
		int object_id3 = dataImport.insertObject(sourcerel_id2, object[2][0], object[2][1], object[2][2], object[2][3], Element.stringToKind(object[2][4]), object[2][5], object[2][6]);
		tmp = dataImport.insertObject(sourcerel_id2, object[2][0], object[2][1], object[2][2], object[2][3], Element.stringToKind(object[2][4]), object[2][5], object[2][6]);
		assertEquals(object_id3, tmp);
		
		int object_id4 = dataImport.insertObject(sourcerel_id2, object[3][0], object[3][1], object[3][2], object[3][3], Element.stringToKind(object[3][4]), object[3][5], object[3][6]);
		tmp = dataImport.insertObject(sourcerel_id2, object[3][0], object[3][1], object[3][2], object[3][3], Element.stringToKind(object[3][4]), object[3][5], object[3][6]);
		assertEquals(object_id4, tmp);
		
		// **Insert ObjectRelationship**
		dataImport.insertObjectRel(sourcerel_id1, object_id1, object_id2, -1, null);
		dataImport.insertObjectRel(sourcerel_id2, object_id3, object_id4, -1, null);
		dataImport.insertObjectRel(sourcerel_id3, object_id1, object_id3, (float) 0.5, "equal");
		dataImport.insertObjectRel(sourcerel_id3, object_id2, object_id4, (float) 0.5, "equal");
	}
	
	public void testDelete1(DataImport dataImport, String[][] source, String[][] object){
		int source_id1 = dataImport.getSourceId(source[0][0], source[0][3]);
		int source_id2 = dataImport.getSourceId(source[1][0], source[1][3]);

		dataImport.deleteSource(source_id1);
		int tmp = dataImport.getSourceId(source[0][0], source[0][3]);
		assertEquals(Source.UNDEF, tmp);
		
		
		HashSet<Integer> list = dataImport.getSourceRelId(source_id1);		
		for (Integer id : list) {
			dataImport.deleteSourceRel(id);
		}
		list = dataImport.getSourceRelId(source_id1);
		assertNull(list);
		
		dataImport.deleteSource(source_id2);
		tmp = dataImport.getSourceId(source[1][0], source[1][3]);
		assertEquals(Source.UNDEF, tmp);

		list = dataImport.getSourceRelId(source_id2);		
		for (Integer id : list) {
			dataImport.deleteSourceRel(id);
		}
		list = dataImport.getSourceRelId(source_id1);
		assertNull(list);
		
	}
	
	public void testDelete2(DataImport dataImport, String[][] source, String[][] object){
		testInsert(dataImport, source, object);
		int source_id1 = dataImport.getSourceId(source[0][0], source[0][3]);
		dataImport.deleteSourceWithSourceRel(source_id1);
		
		source_id1 = dataImport.getSourceId(source[0][0], source[0][3]);
		assertEquals(Source.UNDEF, source_id1);
		HashSet<Integer> list = dataImport.getSourceRelId(source_id1);
		assertNull(list);
		
		testInsert(dataImport, source, object);
		int source_id2 = dataImport.getSourceId(source[0][0], source[0][3]);
		dataImport.deleteSourceWithSourceRel(source_id2);
		
		source_id2 = dataImport.getSourceId(source[0][0], source[0][3]);
		assertEquals(Source.UNDEF, source_id2);
		list = dataImport.getSourceRelId(source_id2);
		assertNull(list);
	}
	
	
}
