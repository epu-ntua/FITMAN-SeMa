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

package de.wdilab.coma.insert.instance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;

/**
 * Import instances of an odbc connection. Assumption is that the odbc 
 * connection used to import the metadata also contains the instance data.
 * 
 * @author Sabine Massmann
 */

//Import relational metadata from a RDBMS through ODBC
public class InstanceODBCParser {
	DataImport importer = null;
	protected Statement statement=null;
	private Connection dbConnection;
	public static final String DRIVER_NAME = "sun.jdbc.odbc.JdbcOdbcDriver";
	public static final String DB_URL = "jdbc:odbc:";

  public InstanceODBCParser(DataImport importer, String odbcEntry, String userName, String userPass) {  
//	  DB_URL + odbcEntry + ":" + schemaName  --> = provider of a ODBC schema 
	  try {
		  Class.forName(DRIVER_NAME); 
//		  dbConnection = DriverManager.getConnection(System.getProperty("comaUrl"),System.getProperty("comaUser"),System.getProperty("comaPwd"));
		  dbConnection = DriverManager.getConnection(DB_URL + odbcEntry, userName, userPass);
		  statement  = dbConnection.createStatement();
	} catch (SQLException e) {
		System.out.println("InstanceODBCParser Constructor: Error " + e.getMessage());
	} catch (ClassNotFoundException e) {
		System.out.println("InstanceODBCParser Constructor: Error " + e.getMessage());
	}
	  this.importer = importer;
  }

  
  public void parseInstancesForSchema(Graph schemaGraph){
	  int id = schemaGraph.getSource().getId();
//	  importer.createInstancesTable(id);
	  importer.prepareInstancesStatement(id);
	  ArrayList<Element> inners = schemaGraph.getInners();
	  for (int i = 0; i < inners.size(); i++) {
		  Element inner = inners.get(i); // table name
		ArrayList<Element> children = schemaGraph.getChildren(inner); // attributes
		if (children.isEmpty()){
			continue;
		}
		parseInstancesForTable(inner, children);
	}
  }
  
  private void parseInstancesForTable(Element table, ArrayList<Element> attributes){
	  String tableName = table.getName();
	  String attributeList = null;
	  for (int i = 0; i < attributes.size(); i++) {
		  Element element =  attributes.get(i);
		String attributeName = element.getName();
		if (i==0){
			attributeList = attributeName;
		} else {
			attributeList += ", " + attributeName;
		}
	  }
	  String url=null;
	try {
		url = statement.getConnection().getMetaData().getURL();
		if (url!=null && url.contains(":")) {
			  url = url.substring(url.lastIndexOf(":")+1);
		  }
	} catch (SQLException e1) {
	}
	
	  String query = "SELECT "+attributeList+" FROM ";
	  try {		   

		  if (attributeList!=null && attributeList.contains("table")){
			  if (url!=null) {
				  query = "SELECT * FROM " + url + "." + tableName;
			  } else {
				  query = "SELECT * FROM " + tableName;
			  }
		  } else if (url!=null) {
			  query += url + "." + tableName;
		  } else {
			  query += tableName;
		  }
		  ResultSet rs = statement.executeQuery(query);
		  int connect = 1;
		  while (rs.next() &&  connect< Repository.INSTANCES_MAX_PER_ELEMENT){
			  String connectString = connect + "";
			  for (int i = 0; i < attributes.size(); i++) {
				  Element element = attributes.get(i);
				  String value = rs.getString(element.getName());
				  saveInstanceToRepository(element, value, connectString, importer);
			  }
			  connect++;
		  } 
	  }
	  catch (SQLException e) { 
		  
		  System.out.println("parseInstancesForTable(): " + e.getMessage());
		  System.out.println(query);
	  }
	}
  
  
	private static void saveInstanceToRepository(Element element,
			String elementText, String connect, DataImport importer) {
		if (elementText!=null && elementText.length() > 0) {
			element.addInstance(elementText);
			int id = -1;
			String attributeName = null;
			id = importer.insertInstance(connect, element.getId(), id,
					attributeName, elementText);
			importer.updateInstance(id, id);
		}
	}
	
//	public static void main(String[] args) {
//		System.setProperty("comaUrl", "jdbc:mysql://localhost/coma?autoReconnect=true");
//		System.setProperty("comaUser", "");
//		System.setProperty("comaPwd", "");
//		int graphstate = Graph.PREP_REDUCED;
//		Manager manager = new Manager();
//		manager.loadRepository();
//		int id = 3;
//		Graph schemaGraph = manager.loadSchemaGraph(manager.getSource(id), true).getGraph(graphstate);
//		String odbcEntry="temp8";
//		String userName="";
//		String userPass="";		
//		
//		manager.parseInstances(schemaGraph, true, odbcEntry, userName, userPass);
////		InstanceODBCParser parser = new InstanceODBCParser(importer, odbcEntry, userName, userPass);
////		parser.parseInstancesForSchema(schemaGraph);
//		
//		System.out.println("DONE.");
//	}	


}