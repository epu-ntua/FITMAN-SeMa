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

package de.wdilab.coma.center;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;
import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Source;

/**
 * @author Sabine Massmann
 */
public class TManager extends TestCase {

	static String directory = (new File ("")).getAbsolutePath().replace("\\", "/") + "/resources/";
	static boolean loadForeignTypes=false;
	static boolean preprocess=true;
	static boolean loadInstances=true; 
	
	public static Object[][] SCHEMAINFO= {											// SchemaInfo	
		// SchemaInfo: globalElements / allElements / globalSimpleTypes / allSimpleTypes / globalComplexTypes / allComplexTypes
			// CSV 0-2
			{"Sources/CSV/car.csv",4, 3,								1, 4, 0, 0, 0, 0},
			{"Sources/CSV/dslr_table_1.csv",73, 72,						1, 73, 0, 0, 0, 0},
			{"Sources/CSV/users.csv",11, 10,							1, 11, 0, 0, 0, 0},
			// SQL 3-5
			{"Sources/SQL/produktgruppe.sql",4, 3,						1, 4, 0, 0, 0, 0},
			{"Sources/SQL/db-dump.sql",20, 16,							4, 20, 0, 0, 0, 0},
			{"Sources/SQL/phpmyadmin 20100208 1846.sql",50, 42,			8, 50, 0, 0, 0, 0},
			// List 6-7
			{"PO_abbrevs.txt",42, 22,									0, 42, 0, 0, 0, 0},
			{"PO_syns.txt",191, 104,									0, 191, 0, 0, 0, 0},				
			// XDR 8-17
			{"Sources/PO_xdr/Apertum.xdr",183, 101,						0, 101, 82, 82, 23, 23}, 
			{"Sources/PO_xdr/CIDXPOSCHEMA.xdr",69, 52,					0, 39, 17, 30, 7, 7}, 
			{"Sources/PO_xdr/Excel.xdr",72, 63,							0, 37, 9, 35, 9, 9}, 
			{"Sources/PO_xdr/Noris.xdr",96, 50,							0, 50, 46, 46, 8, 8}, 
			{"Sources/PO_xdr/Paragon.xdr",148, 136,						0, 74, 12, 74, 11, 11}, 
			{"Sources/Xcbl35/Xcbl35_ChangeOrder.xdr", 1527, 856,		0, 856, 671, 671, 296, 296},
			{"Sources/Xcbl35/Xcbl35_Invoice.xdr", 1821, 1047,			0, 1047, 774, 774, 381, 381},
			{"Sources/Xcbl35/Xcbl35_Order.xdr", 1457, 813,				0, 813, 644, 644, 281, 281},
			{"Sources/Xcbl35/Xcbl35_OrderResponse.xdr", 1648, 933,		0, 933, 715, 715, 316, 316}, 
			{"Sources/Xcbl35/Xcbl35_ProductCatalog.xdr", 431, 243,		0, 243, 188, 188, 71, 71},
			// XSD single 18-25
			{"Sources/poxsd/po.xsd",24, 18,								2, 20, 4, 4, 3, 3},
			{"Sources/BMECat/bmecat_newcat.xsd",394, 205,				163, 368, 24, 24, 2, 2},
			{"Sources/BMECat/bmecat_price.xsd",381, 193,				162, 355, 24, 24, 2, 2},
			{"Sources/BMECat/bmecat_product.xsd",392, 203,				163, 366, 24, 24, 2, 2},
			{"Sources/OpenTrans/OpenTrans_INVOICE.xsd",367, 188,		154, 342, 24, 24, 0, 0},
			{"Sources/OpenTrans/OpenTrans_ORDER.xsd",348, 171,			152, 323, 24, 24, 0, 0},
			{"Sources/OpenTrans/OpenTrans_ORDERCHANGE.xsd",344, 167,	152, 319, 24, 24, 0, 0},
			{"Sources/OpenTrans/OpenTrans_ORDERRESPONSE.xsd",355, 177,	153, 330, 24, 24, 0, 0},				
			 // XSD composite 26-30
			{"Sources/BMECatAll",429, 233,								170, 403, 24, 24, 2, 2},
			{"Sources/OpenTransAll",614, 395,							194, 589, 24, 24, 0, 0},
			{"Sources/XcblCore", 1063, 719,								0, 719, 340, 340, 216, 216},
			{"Sources/XcblOrder", 494, 410,								8, 418, 76, 76, 71, 71},		
			{"Sources/XcblCatalog", 193, 142,							1, 143, 50, 50, 38, 38},
			// OWL 31-35
			{"Sources/Po_Owl/order.owl", 48, 49,						0, 36, 0, 12, 0, 8},
			{"Sources/webdirectories/dmoz.Freizeit.owl", 71, 70,		0, 0, 0, 71, 0, 19},
			{"Sources/webdirectories/Google.Freizeit.owl", 67, 66,		0, 0, 0, 67, 0, 19},
			{"Sources/webdirectories/Google.Lebensmittel.owl", 59, 58,	0, 0, 0, 59, 0, 14},
			{"Sources/webdirectories/web.Lebensmittel.owl", 53, 52,		0, 0, 0, 53, 0, 6},
			{"Sources/anatomy/mouse_2010.owl", 2746, 3444,				0, 3, 0, 2743, 0, 915}, // OLD: 0, 95, 0, 2651, 0, 823
			{"Sources/anatomy/nci_2010.owl", 3306, 5423,				0, 2, 0, 3304, 0, 1071}, // OLD: 0, 247, 0, 3059, 0, 826
			// OWL 36-48
			{"Sources/oaei/2010/benchmarks/101/onto.rdf", 101, 132,		0, 66, 0, 35, 0, 25}, // OLD: 0, 74, 0, 31, 0, 22
			{"Sources/oaei/2010/benchmarks/103/onto.rdf", 101, 122,		0, 66, 0, 35, 0, 20}, // OLD: 0, 74, 0, 31, 0, 17
			{"Sources/oaei/2010/benchmarks/104/onto.rdf", 101, 111,		0, 66, 0, 35, 0, 20}, // OLD: 0, 74, 0, 31, 0, 17
			{"Sources/oaei/2010/benchmarks/201/onto.rdf", 101, 132,		0, 66, 0, 35, 0, 25}, // OLD: 0, 73, 0, 32, 0, 23
			{"Sources/oaei/2010/benchmarks/202/onto.rdf", 101, 132,		0, 66, 0, 35, 0, 25}, // OLD: 0, 73, 0, 32, 0, 23
			{"Sources/oaei/2010/benchmarks/203/onto.rdf", 101, 132,		0, 66, 0, 35, 0, 25}, // OLD: 0, 74, 0, 31, 0, 22
			{"Sources/oaei/2010/benchmarks/204/onto.rdf", 101, 132,		0, 66, 0, 35, 0, 25}, // OLD: 0, 73, 0, 32, 0, 23
			{"Sources/oaei/2010/benchmarks/301/onto.rdf", 55, 54,		0, 40, 0, 15, 0, 1}, // OLD: 0, 40, 0, 15, 0, 1
			{"Sources/oaei/2010/benchmarks/302/onto.rdf", 46, 46,		0, 31, 0, 15, 0, 2}, // OLD: 0, 31, 0, 15, 0, 2
			{"Sources/oaei/2010/benchmarks/303/onto.rdf", 126, 101,		0, 72, 0, 54, 0, 22}, // OLD: 0, 83, 0, 43, 0, 12
			{"Sources/oaei/2010/benchmarks/304/onto.rdf", 88, 139,		0, 49, 0, 39, 0, 26}, // OLD: 0, 57, 0, 38, 0, 25
	};
	
	
	Object[][] GRAPHINFO= {	// GraphInfo
			// CSV 0-2
			{"Sources/CSV/car.csv", 1, 1, 3, 0, 3, 3, 6, 4, 1, 3},
			{"Sources/CSV/dslr_table_1.csv", 1, 1, 72, 0, 72, 72, 5112, 73, 1, 72},
			{"Sources/CSV/users.csv", 1, 1, 10, 0, 10, 10, 90, 11, 1, 10},
			// SQL 3-5
			{"Sources/SQL/produktgruppe.sql", 1, 1, 3, 0, 3, 3, 6, 4, 1, 3},
			{"Sources/SQL/db-dump.sql", 4, 4, 16, 0, 16, 16, 82, 20, 4, 16},
			{"Sources/SQL/phpmyadmin 20100208 1846.sql", 8, 8, 42, 0, 42, 42, 254, 50, 8, 42},
			// List 6-7
			{"PO_abbrevs.txt", 22, 22, 20, 2, 22, 22, 462, 44, 22, 22},
			{"PO_syns.txt", 94, 101, 90, 6, 104, 104, 8748, 203, 106, 97},
			// XDR 8-17
			{"Sources/PO_xdr/Apertum.xdr", 82, 23, 160, 0, 101, 101, 7094, 183, 23, 160},
			{"Sources/PO_xdr/CIDXPOSCHEMA.xdr", 17, 7, 62, 0, 52, 52, 650, 69, 7, 62},
			{"Sources/PO_xdr/Excel.xdr", 9, 9, 63, 0, 63, 63, 712, 72, 9, 63},
			{"Sources/PO_xdr/Noris.xdr", 46, 8, 88, 0, 50, 50, 2440, 96, 8, 88},
			{"Sources/PO_xdr/Paragon.xdr", 12, 11, 137, 0, 136, 136, 2106, 148, 11, 137},
			{"Sources/Xcbl35/Xcbl35_ChangeOrder.xdr", 671, 296, 1231, 0, 856, 856, 455094, 1527, 296, 1231},
			{"Sources/Xcbl35/Xcbl35_Invoice.xdr", 774, 381, 1440, 0, 1047, 1047, 605034, 1821, 381, 1440},
			{"Sources/Xcbl35/Xcbl35_Order.xdr", 644, 281, 1176, 0, 813, 813, 419334, 1457, 281, 1176},
			{"Sources/Xcbl35/Xcbl35_OrderResponse.xdr", 715, 316, 1332, 0, 933, 933, 516734, 1648, 316, 1332},
			{"Sources/Xcbl35/Xcbl35_ProductCatalog.xdr", 188, 71, 360, 0, 243, 243, 37630, 431, 71, 360},
			// XSD single 18-25
			{"Sources/poxsd/po.xsd", 6, 4, 20, 0, 18, 18, 110, 24, 4, 20},
			{"Sources/BMECat/bmecat_newcat.xsd", 189, 55, 339, 0, 205, 205, 36818, 394, 55, 339},
			{"Sources/BMECat/bmecat_price.xsd", 188, 54, 327, 0, 193, 193, 36348, 381, 54, 327},
			{"Sources/BMECat/bmecat_product.xsd", 189, 55, 337, 0, 203, 203, 36800, 392, 55, 337},
			{"Sources/OpenTrans/OpenTrans_INVOICE.xsd", 179, 53, 314, 0, 188, 188, 33006, 367, 53, 314},
			{"Sources/OpenTrans/OpenTrans_ORDER.xsd", 177, 51, 297, 0, 171, 171, 32176, 348, 51, 297},
			{"Sources/OpenTrans/OpenTrans_ORDERCHANGE.xsd", 177, 51, 293, 0, 167, 167, 32122, 344, 51, 293},
			{"Sources/OpenTrans/OpenTrans_ORDERRESPONSE.xsd", 178, 52, 303, 0, 177, 177, 32566, 355, 52, 303},
			 // XSD composite 26-30
			{"Sources/BMECatAll", 196, 62, 367, 0, 233, 233, 39622, 429, 62, 367},
			{"Sources/OpenTransAll", 219, 93, 521, 0, 395, 395, 50720, 614, 93, 521},
			{"Sources/XcblCore", 344, 216, 847, 0, 719, 719, 121982, 1063, 216, 847},
			{"Sources/XcblOrder", 84, 71, 423, 0, 410, 410, 11224, 494, 71, 423},
			{"Sources/XcblCatalog", 51, 38, 155, 0, 142, 142, 3858, 193, 38, 155},
			// OWL 31-35
			{"Sources/Po_Owl/order.owl", 4, 16, 32, 4, 49, 49, 268, 69, 23, 46},
			{"Sources/webdirectories/dmoz.Freizeit.owl", 1, 19, 52, 0, 70, 70, 356, 71, 19, 52 },
			{"Sources/webdirectories/Google.Freizeit.owl", 1, 19, 48, 0, 66, 66, 334, 67, 19, 48},
			{"Sources/webdirectories/Google.Lebensmittel.owl", 1, 14, 45, 0, 58, 58, 394, 59, 14, 45},
			{"Sources/webdirectories/web.Lebensmittel.owl", 1, 6, 47, 0, 52, 52, 636, 53, 6, 47},
			{"Sources/anatomy/mouse_2010.owl", 10, 915, 1831, 634, 3444, 3444, 63020, 12362, 3584, 8778},
			{"Sources/anatomy/nci_2010.owl", 9, 1071, 2235, 1752, 5423, 5423, 107552, 39001, 10058, 28943},
			// OWL 36-48
			{"Sources/oaei/2010/benchmarks/101/onto.rdf", 13, 45, 56, 29, 124, 124, 1672, 313, 128, 185},
			{"Sources/oaei/2010/benchmarks/103/onto.rdf", 12, 40, 61, 20, 115, 115, 2008, 193, 78, 115},
			{"Sources/oaei/2010/benchmarks/104/onto.rdf", 22, 40, 61, 20, 105, 105, 1608, 193, 78, 115},
			{"Sources/oaei/2010/benchmarks/201/onto.rdf", 12, 45, 56, 28, 124, 124, 1650, 250, 101, 149},
			{"Sources/oaei/2010/benchmarks/202/onto.rdf", 12, 45, 56, 28, 124, 124, 1650, 250, 101, 149},
			{"Sources/oaei/2010/benchmarks/203/onto.rdf", 13, 45, 56, 29, 124, 124, 1672, 313, 128, 185},
			{"Sources/oaei/2010/benchmarks/204/onto.rdf", 13, 45, 56, 29, 124, 124, 1672, 313, 128, 185},
			{"Sources/oaei/2010/benchmarks/301/onto.rdf", 1, 1, 54, 0, 54, 54, 2862, 55, 1, 54},
			{"Sources/oaei/2010/benchmarks/302/onto.rdf", 2, 7, 39, 1, 46, 46, 886, 48, 7, 41},
			{"Sources/oaei/2010/benchmarks/303/onto.rdf", 77, 22, 104, 25, 92, 92, 6446, 770, 263, 507},
			{"Sources/oaei/2010/benchmarks/304/onto.rdf", 3, 64, 24, 30, 139, 139, 938, 293, 165, 128},

			};
	
	
	
	static public void setDatabaseProperties(){
		System.setProperty("comaUrl", "jdbc:mysql://localhost/coma-project?autoReconnect=true");
		System.setProperty("comaUser", "");
		System.setProperty("comaPwd", "");
	}

	
	public void testRepository(){
		
		setDatabaseProperties();

		Manager manager = new Manager();
		DataAccess dataAccess = manager.getAccessor();
		dataAccess.dropRepositorySchema();		
		dataAccess.createRepositorySchema();
		
		boolean dbInsert = true;
		for (int i = 0; i <TManager.SCHEMAINFO.length; i++) {
			String filename = directory + TManager.SCHEMAINFO[i][0];
			InsertParser.parseSingleSource(filename, dbInsert);
		}
		System.out.println("Import Done.");
		
		for (int i = 0; i < SCHEMAINFO.length; i++) {
			String provider = directory + SCHEMAINFO[i][0];
			int source_id = dataAccess.getSourceId(provider);
			Source source = dataAccess.getSource(source_id);
			System.out.println(source);
			
			Graph graph2 = manager.loadGraph(source, loadForeignTypes, preprocess, loadInstances);
			testData(graph2, SCHEMAINFO[i]);
			testData2(graph2, GRAPHINFO[i]);
		}
		
		dataAccess.closeDatabaseConnection();		
	}
	

	public static void testData(Graph graph, Object[] data){		
		ArrayList allElements = graph.getAllElements();
		ArrayList allSimpleTypes = graph.getAllSimpleTypes();
		ArrayList allComplexTypes = graph.getAllComplexTypes();
		ArrayList globalElements = graph.getGlobalElements();
		ArrayList globalSimpleTypes = graph.getGlobalSimpleTypes();
		ArrayList globalComplexTypes = graph.getGlobalComplexTypes();	
		
		int allElementsSize = (allElements!=null?allElements.size():0);
		int allSimpleTypesSize = (allSimpleTypes!=null?allSimpleTypes.size():0);
		int allComplexTypesSize = (allComplexTypes!=null?allComplexTypes.size():0);
		int globalElementsSize = (globalElements!=null?globalElements.size():0);
		int globalSimpleTypesSize = (globalSimpleTypes!=null?globalSimpleTypes.size():0);
		int globalComplexTypesSize = (globalComplexTypes!=null?globalComplexTypes.size():0);
		
//	graph2.printSchemaInfo();
		
		assertEquals(data[3],globalElementsSize);
		assertEquals(data[4],allElementsSize);
		assertEquals(data[5],globalSimpleTypesSize);
		assertEquals(data[6],allSimpleTypesSize);
		assertEquals(data[7],globalComplexTypesSize);
		assertEquals(data[8],allComplexTypesSize);
	}
	
	public static void testData2(Graph graph, Object[] data){		
		ArrayList roots = graph.getRoots();
		ArrayList inners = graph.getInners();
		ArrayList leaves = graph.getLeaves();
		ArrayList shared = graph.getShared();
		ArrayList allPaths = graph.getAllPaths();
		ArrayList innerPaths = graph.getInnerPaths();
		ArrayList leafPaths = graph.getLeafPaths();
		
		int rootsSize = (roots!=null?roots.size():0);
		int innersSize = (inners!=null?inners.size():0);
		int leavesSize = (leaves!=null?leaves.size():0);
		int sharedSize = (shared!=null?shared.size():0);
		int allPathsSize = (allPaths!=null?allPaths.size():0);
		int innerPathsSize = (innerPaths!=null?innerPaths.size():0);
		int leafPathsSize = (leafPaths!=null?leafPaths.size():0);
		
		int siblingsCnt=0, parentCnt=0, childCnt=0;
		
		Iterator iterator = graph.getElementIterator();
	     while (iterator.hasNext()) {
	         Element elem=(Element)iterator.next();
	         ArrayList parents = graph.getParents(elem);
	         ArrayList children = graph.getChildren(elem);
	         ArrayList siblings = graph.getSiblings(elem);
	         if (siblings!=null) {
	      	   siblingsCnt += siblings.size();
	         }
	         if (parents!=null) {
	           parentCnt += parents.size();
	         }
	         if (children!=null) {
	           childCnt += children.size();
	         }
	     }
		System.out.println(", " + rootsSize + ", " + innersSize
	   			+ ", " + leavesSize + ", " + sharedSize
	   	+ ", " + parentCnt + ", " + childCnt + ", " + siblingsCnt  
	   	+ ", " + allPathsSize + ", " + innerPathsSize + ", " + leafPathsSize + "}"
	   	);
		
		assertEquals(data[1],rootsSize);
		assertEquals(data[2],innersSize);
		assertEquals(data[3],leavesSize);
		assertEquals(data[4],sharedSize);
		assertEquals(data[5],parentCnt);
		assertEquals(data[6],childCnt);
		assertEquals(data[7],siblingsCnt);
		assertEquals(data[8],allPathsSize);
		assertEquals(data[9],innerPathsSize);
		assertEquals(data[10],leafPathsSize);
	}

}
