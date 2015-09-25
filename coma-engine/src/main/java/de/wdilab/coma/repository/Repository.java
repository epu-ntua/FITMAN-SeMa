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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * Repository contains function to create and delete the whole repository (=several tables)
 * and to insert or retrieve basic information. For more detailed reading access DataAccess 
 * is used and for writing acces DataImport. 
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class Repository {
	// static
	public static final String DB_MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	
	// Status of import
	public static final String STATUS_IMPORT_STARTED = "IMPORT_STARTED";
	public static final String STATUS_IMPORT_DONE = "IMPORT_DONE";

	public static final String TABLE_SOURCE = "source";
	static final String TABLE_SOURCE_REL = "source_rel";
	public static final String TABLE_OBJECT = "object";
	static final String TABLE_OBJECT_REL = "object_rel";
	static final String TABLE_WORKFLOW = "workflow";
	static final String TABLE_INSTANCES = "instances_";
	
	public static final String SRC_ABBREV = "ABBREVIATION";
	public static final String SRC_SYNONYM = "SYNONYM";
	
	static final int UNDEF = -1;
	
	static public final int INSTANCES_MAX_PER_ELEMENT = 1000;
	
	// non-static
	Connection connection = null;
	Statement statement = null;

	boolean queryStatus = true;
	String queryMessage = null;
	
	//
	PreparedStatement insertSourcePstmt, insertSourceRelPstmt,
			insertObjectPstmt, insertObjectRelPstmt, insertWorkflowPstmt,
            // Instances
            insertInstancesPstmt,updateInstancesPstmt;

	public Repository() {
		connectToDatabase();
	}

	public Repository(Connection connection) {
		this.connection = connection;
		connectToDatabase();
	}
	
	public void connectToDatabase() {
		try {
			if ((connection == null || (connection!=null && connection.isClosed()))
					&&  statement == null ) {
//                    System.out.println("hello  1");
					Class.forName(DB_MYSQL_DRIVER);
					// connection = DriverManager.getConnection(DB_MYSQL_URL,
					// DB_USER, DB_PASSWORD);
//                    System.out.println("hello  2");
					if (System.getProperty("comaUrl")!=null){
//                        System.out.println("hello  3");
						connection = DriverManager.getConnection(
								System.getProperty("comaUrl"),
								System.getProperty("comaUser"),
								System.getProperty("comaPwd"));
//                        connection = DriverManager.getConnection(
//                                "jdbc:mysql://localhost/testfuck2",
//                                "fakeuser",
//                                "fakepassword");
//                        System.out.println("hello  4");
						statement = connection.createStatement();
//                        System.out.println("hello  5");
					}
			} else if (connection != null &&  statement == null){
//                System.out.println("hello  6");
				statement = connection.createStatement();
//                System.out.println("hello  7");
			}
		} catch (SQLException e) {
			System.out.println("connectToDatabase(): " + e.getMessage());
            System.out.println("sql error "+System.getProperty("comaUrl") +"  "+System.getProperty("comaUser")+" "+System.getProperty("comaPwd")) ;
		} catch (Exception e) {
			System.out.println("connectToDatabase(): " + e.getMessage());
            e.printStackTrace();
		}
	}

	public Connection getConnection(){
		return connection;
	}
	
	public void closeDatabaseConnection() {
		try {
			if (statement != null)
				statement.close();
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			System.out.println("closeDatabaseConnection()" + e.getMessage());
		}
	}
	
	public void executeQuery(String query, boolean verbose) {
		    //System.out.println("Executing query: " + query);
		queryStatus = true;
		if (query!=null) {
			try { statement.execute(query); }
			catch(SQLException e) {
				queryStatus = false;
		        queryMessage = e.getMessage();
		        if (verbose) System.out.println("executeQuery(): " + query + " with ERROR " + queryMessage);
			}
		}
	}
	
	public void executeQuery(String query) {
		executeQuery(query, true);
	}

	public void executeBatch(String[] queries, boolean verbose) {
		queryStatus = true;
		for (int i=0; i<queries.length; i++) {
			executeQuery(queries[i], verbose);
			if (! queryStatus) break;
		}
	}
	
	public void executeBatch(String[] queries) {
		executeBatch(queries, true);
	}
	
	
	public void createRepositorySchema() {
		for (int i=0; i<MySQL.CREATE_QUERIES.length; i++){
			executeQuery(MySQL.CREATE_QUERIES[i]);
		}
	}
	
	public void createInstancesTable(int id){
		System.out.print("Create new instances schema ("+id+") ... ");
		String query = MySQL.createInstancesQuery1 + id + MySQL.createInstancesQuery2;
		executeQuery(query);	  
	}
	
	public void dropRepositorySchema() {
		for (int i=0; i<MySQL.DROP_QUERIES.length; i++){
			executeQuery(MySQL.DROP_QUERIES[i]);
		}

		deleteMatchResultDBs();
		
		deleteInstanceTables();
	}
	
	public void emptyWorkflow() {
		executeQuery("TRUNCATE " + TABLE_WORKFLOW);
	}

	boolean getLastQueryStatus() {
		return queryStatus;
	}

	String getLastQueryMessage() {
		return queryMessage;
	}
	
	
	  public void prepareInstancesStatement(int id){
		  try {
			insertInstancesPstmt = connection.prepareStatement(
					MySQL.insertInstancesQuery1 + id + MySQL.insertInstancesQuery2,Statement.RETURN_GENERATED_KEYS);
			updateInstancesPstmt = connection.prepareStatement(
					MySQL.updateInstancesQuery1 + id + MySQL.updateInstancesQuery2,Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			System.out.println("Repository.prepareInstancesStatement: SQLException");
		}
	  }
	  
	  public void closeInstancesStatement(){
		  try {
			insertInstancesPstmt.close();
			updateInstancesPstmt.close();
		} catch (SQLException e) {
			System.out.println("Repository.prepareInstancesStatement: SQLException");
		}
	  }
	  //--------------------------------------------------------------------------//
	  // Retrieval routines                                                       //
	  //--------------------------------------------------------------------------//
	  
	  
	  // assumption:  only one source has this name and provider!!!
	  public int getSourceId(String sourceName, String provider) {
		    int id = Source.UNDEF;
		    String query = MySQL.SELECT +"source_id FROM " + TABLE_SOURCE + 
		    	" WHERE name='" + sourceName + "' AND provider='"+ provider + "' ";
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      if (rs.next()){
		        id = rs.getInt(1);
		      } else {   // url instead of provider
		    	  query = MySQL.SELECT +"source_id FROM " + TABLE_SOURCE + 
			    	" WHERE name='" + sourceName + "' AND url LIKE '%"+ provider + "%'";
		    	  rs = statement.executeQuery(query);
		    	  if (rs.next()){
				        id = rs.getInt(1);
		    	  } else {   // only name
			    	  query = MySQL.SELECT +"source_id FROM " + TABLE_SOURCE + 
				    	" WHERE name='" + sourceName + "'";
			    	  rs = statement.executeQuery(query);
			    	  if (rs.next()){ // take first source
					        id = rs.getInt(1);
			    	  }
			      }
		      }
		    }
		    catch (SQLException e) {
		      System.out.println("getSourceId(): " + e.getMessage());
		    }
		    return id;
	  }
	  
	  // assumption:  only one source has this provider!!!
	  public int getSourceId(String provider) {
		    int id = Source.UNDEF;
		    String query = MySQL.SELECT +"source_id FROM " + TABLE_SOURCE + 
		    	" WHERE provider='"+ provider + "' ";
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      if (rs.next())
		        id = rs.getInt(1);
		    }
		    catch (SQLException e) {
		      System.out.println("getSourceId(): " + e.getMessage());
		    }
		    return id;
	  }
	  

	  public int getSourceRelId(int source1Id, int source2Id, int type, String name) {
	    int id = SourceRelationship.UNDEF;
	    String query = MySQL.SELECT +"sourcerel_id FROM " + TABLE_SOURCE_REL + " " +
	        "WHERE source1_id=" + source1Id + " AND source2_id=" + source2Id;
	    if (name!=null){
	    	query += " AND name='" + name + "'";
	    }
	    try {
	      ResultSet rs = statement.executeQuery(query);
	      if (rs.next()) id = rs.getInt(1);
	    }
	    catch (SQLException e) { System.out.println("getSourceRelId(): " + e.getMessage());  }
	    return id;
	  }
	  
	  public HashSet<Integer> getSourceRelId(int sourceId) {
		  if (sourceId==Source.UNDEF){
			  return null;
		  }
		  HashSet<Integer> list = new HashSet<Integer>();
		    String query = MySQL.SELECT +"sourcerel_id FROM " + TABLE_SOURCE_REL + " " +
		        "WHERE (source1_id=" + sourceId + " OR source2_id=" + sourceId + ") AND status='"+STATUS_IMPORT_DONE+"'";
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      while (rs.next()){
		    	  int id = rs.getInt(1);
		    	  list.add(id);
		      }
		      if (list.isEmpty()) return null;
		    }
		    catch (SQLException e) { System.out.println("getSourceRelId(): " + e.getMessage());  }
		    return list;
	  }
	  
	  public Integer getSuperSource(int sourceId) {
		  ArrayList<Integer> source_ids = getSource1Id(sourceId, SourceRelationship.REL_CONTAINS);
		  
		    if (source_ids==null) return UNDEF;
		    
		    
		    ArrayList<Integer> undef = getSourceID_Undef();
		    if (undef!=null){
		    	source_ids.removeAll(undef);
		    	if (source_ids.isEmpty()) return UNDEF;
		    }
		    
		    if (source_ids.size()>1){
		    	System.out.println("only one super source expected");
		    }
		    return source_ids.get(0);
	  }
	  
	  public ArrayList<Integer> getSubSources(int sourceId) {
		  return getSource2Id(sourceId, SourceRelationship.REL_CONTAINS);
	  }
	  
	  
	  public ArrayList<Integer> getSource2Id(int sourceId1, int sourcerel_type) {
		  if (sourceId1==Source.UNDEF){
			  return null;
		  }
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  String query = MySQL.SELECT +"source2_id FROM " + TABLE_SOURCE_REL + " " +
		  "WHERE source1_id=" + sourceId1 + " AND type="+sourcerel_type+" AND status='"+STATUS_IMPORT_DONE+"'";
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  while (rs.next()){
				  int id = rs.getInt(1);
				  list.add(id);
			  }
			  if (list.isEmpty()) return null;
		  }
		  catch (SQLException e) { System.out.println("getSource2Id(): " + e.getMessage());  }
		  return list;
	  }
	  
	  public ArrayList<Integer> getSource1Id(int sourceId2, int sourcerel_type) {
		  if (sourceId2==Source.UNDEF){
			  return null;
		  }
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  String query = MySQL.SELECT +"source1_id FROM " + TABLE_SOURCE_REL + " " +
		  "WHERE source2_id=" + sourceId2 + " AND type="+ sourcerel_type+" AND status='"+STATUS_IMPORT_DONE+"'";
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  while (rs.next()){
				  int id = rs.getInt(1);
				  list.add(id);
			  }
			  if (list.isEmpty()) return null;
		  }
		  
		  catch (SQLException e) { System.out.println("getSource2Id(): " + e.getMessage());  }
		  return list;
	  }
	  
	  public ArrayList<Integer> getSourceID_Undef() {
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  String query = MySQL.SELECT +"source_id FROM " + TABLE_SOURCE + " " +
		  "WHERE type='"+Source.typeToString(-1)+"'";
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  while (rs.next()){
				  int id = rs.getInt(1);
				  list.add(id);
			  }
			  if (list.isEmpty()) return null;
		  }
		  
		  catch (SQLException e) { System.out.println("getSource2Id(): " + e.getMessage());  }
		  return list;
	  }
	  
	  public ArrayList<Integer> getSourceIdsWithUrl(String url) {
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  String query = MySQL.SELECT +"source_id FROM " + TABLE_SOURCE + " WHERE  status='"+STATUS_IMPORT_DONE+"' AND ";
		  if (url==null){
			  query += "url IS NULL";
		  } else {
			//in case source contains multiple namespaces
			  query += "url LIKE '%"+url+"%'";
		  }
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  while (rs.next()){
				  int id = rs.getInt(1);
				  list.add(id);
			  }
			  if (list.isEmpty()) return null;
		  }
		  catch (SQLException e) { System.out.println("getSourcesWithUrl(): " + e.getMessage());  }
		  return list;
	  }
	  
	  public ArrayList<Integer> getSourceIdsWithProvider(String provider) {
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  String query = MySQL.SELECT +"source_id FROM " + TABLE_SOURCE + " WHERE  status='"+STATUS_IMPORT_DONE+"' AND ";
		  if (provider==null){
			  query += "provider IS NULL";
		  } else {
			//in case source contains multiple namespaces
			  query += "provider='"+provider+"'";
		  }
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  while (rs.next()){
				  int id = rs.getInt(1);
				  list.add(id);
			  }
			  if (list.isEmpty()) return null;
		  }
		  catch (SQLException e) { System.out.println("getSourceIdsWithProvider(): " + e.getMessage());  }
		  return list;
	  }
	  
	  public ArrayList<Integer> getSourceIdsWithNameAndUrl(String name, String url) {
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  String query = MySQL.SELECT +"source_id FROM " + TABLE_SOURCE + " WHERE  status='"+
		  		STATUS_IMPORT_DONE+"' AND name='"+name+"' AND ";		  
		  if (url==null){
			  query += "url IS NULL";
		  } else {
			  url = url.replace("\\", "/");
			//in case source contains multiple namespaces
			  query += "url LIKE '%"+url+"%'";
		  }
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  while (rs.next()){
				  int id = rs.getInt(1);
				  list.add(id);
			  }
			  if (list.isEmpty()) return null;
		  }
		  catch (SQLException e) { System.out.println("getSourcesWithUrl(): " + e.getMessage());  }
		  return list;
	  }
	  
	  public ArrayList<Integer> getSourceIdsWithName(String name) {
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  String query = MySQL.SELECT +"source_id FROM " + TABLE_SOURCE + " WHERE  status='"+
		  		STATUS_IMPORT_DONE+"' AND name='"+name+"' ";		  
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  while (rs.next()){
				  int id = rs.getInt(1);
				  list.add(id);
			  }
			  if (list.isEmpty()) return null;
		  }
		  catch (SQLException e) { System.out.println("getSourceIdsWithName(): " + e.getMessage());  }
		  return list;
	  }
	  
	  
	  public int getSourceRelId_ISA(int sourceId1, int sourceId2) {
		  if (sourceId1==Source.UNDEF || sourceId2==Source.UNDEF){
			  return SourceRelationship.UNDEF;
		  }
		    String query = MySQL.SELECT +"sourcerel_id FROM " + TABLE_SOURCE_REL + " " +
		        "WHERE source1_id=" + sourceId1 + " AND source2_id=" + sourceId2 + " AND type="+
		        SourceRelationship.REL_IS_A+" AND status='"+STATUS_IMPORT_DONE+"'";
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      if (rs.next()){
		    	  return rs.getInt(1);
		      }
		    }
		    catch (SQLException e) { System.out.println("getSourceRelId(): " + e.getMessage());  }
		    return SourceRelationship.UNDEF;
	  }
	  
	  public HashSet<Integer> getSourceRelIds(int sourceId1, int sourceId2, int sourcerel_type) {
		  if (sourceId1==Source.UNDEF || sourceId2==Source.UNDEF){
			  return null;
		  }
		  HashSet<Integer> ids = new HashSet<Integer>();
		    String query = MySQL.SELECT +"sourcerel_id FROM " + TABLE_SOURCE_REL + " " +
		        "WHERE source1_id=" + sourceId1 + " AND source2_id=" + sourceId2 + " AND type="+
		        sourcerel_type+" AND status='"+STATUS_IMPORT_DONE+"'";
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      while (rs.next()){
		    	  ids.add(rs.getInt(1));
		      }
		    }
		    catch (SQLException e) { System.out.println("getSourceRelId(): " + e.getMessage());  }
		    if (ids.isEmpty()) return null;
		    return ids;
	  }
	  
	  public HashMap<Integer,HashSet<Integer>> getSourceRelSourceIds(int sourcerel_type) {
		  HashMap<Integer,HashSet<Integer>> ids = new HashMap<Integer,HashSet<Integer>>();
		    String query = MySQL.SELECT +"source1_id, source2_id FROM " + TABLE_SOURCE_REL + " " +
		        "WHERE type="+sourcerel_type+" AND status='"+STATUS_IMPORT_DONE+"'";
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      while (rs.next()){
		    	  Integer id1 = rs.getInt(1);
		    	  HashSet<Integer> id1Rel = ids.get(id1);
		    	  if (id1Rel==null) id1Rel = new HashSet<Integer>();
		    	  id1Rel.add( rs.getInt(2));
		    	  ids.put(id1, id1Rel);
		      }
		    }
		    catch (SQLException e) { System.out.println("getSourceRelId(): " + e.getMessage());  }
		    if (ids.isEmpty()) return null;
		    return ids;
	  }
	  
	  public HashSet<Integer> getSourceRelIds(int sourcerel_type) {
		  HashSet<Integer> ids = new HashSet<Integer>();
		    String query = MySQL.SELECT +"sourcerel_id FROM " + TABLE_SOURCE_REL + " " +
		        "WHERE type="+sourcerel_type+" AND status='"+STATUS_IMPORT_DONE+"'";
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      while (rs.next()){
		    	  Integer id1 = rs.getInt(1);
		    	  ids.add(id1);
		      }
		    }
		    catch (SQLException e) { System.out.println("getSourceRelId(): " + e.getMessage());  }
		    if (ids.isEmpty()) return null;
		    return ids;
	  }
	  
	  public int getSourceRelIdWithName(int sourceId1, int sourceId2, String name) {
		  if (sourceId1==Source.UNDEF || sourceId2==Source.UNDEF){
			  return SourceRelationship.UNDEF;
		  }
		    String query = MySQL.SELECT +"sourcerel_id FROM " + TABLE_SOURCE_REL + " " +
		        "WHERE source1_id=" + sourceId1 + " AND source2_id=" + sourceId2 + 
		        " AND name='"+name+"' AND status='"+STATUS_IMPORT_DONE+"'";
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      if (rs.next()){
		    	  return rs.getInt(1);
		      }
		    }
		    catch (SQLException e) { System.out.println("getSourceRelId(): " + e.getMessage());  }
		    return SourceRelationship.UNDEF;
	  }
	  
	  
	  public HashSet<Integer> getSourceRelIds(int sourceId1, int sourceId2) {
		  if (sourceId1==Source.UNDEF || sourceId2==Source.UNDEF){
			  return null;
		  }
		  HashSet<Integer> relIds = new HashSet<Integer>();
		    String query = MySQL.SELECT +"sourcerel_id FROM " + TABLE_SOURCE_REL + " " +
		        "WHERE ((source1_id=" + sourceId1 + " AND source2_id=" + sourceId2 + ") " +
		        "OR (source1_id=" + sourceId2 + " AND source2_id=" + sourceId1 + ")) " +
		        		" AND status='"+STATUS_IMPORT_DONE+"'";
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      while (rs.next()){
		    	  relIds.add(rs.getInt(1));
		      }
		    }
		    catch (SQLException e) { System.out.println("getSourceRelId(): " + e.getMessage());  }
		    if (relIds.isEmpty()) return null;
		    return relIds;
	  }
	  
	  
	  public int getObjectId(int sourceId, String acc, int kind) {
		  if (sourceId==Source.UNDEF){
			  return Element.UNDEF;
		  }
		  int id = Element.UNDEF;
		  String query = MySQL.SELECT +"object_id FROM " + TABLE_OBJECT + 
		  	" WHERE source_id = " + sourceId + " AND accession = '" + acc + "'" + " AND kind = " + kind;
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  if (rs.next()) id = rs.getInt(1);
		  }
		  catch (SQLException e) { System.out.println("getObjectId(): " + e.getMessage());  }
		  return id;
	  }
	  
	  public int getObjectIdNotKind(int sourceId, String acc, int kind) {
		  if (sourceId==Source.UNDEF){
			  return Element.UNDEF;
		  }
		  int id = Element.UNDEF;
		  String query = MySQL.SELECT +"object_id FROM " + TABLE_OBJECT + 
		  	" WHERE source_id = " + sourceId + " AND accession = '" + acc + "'" + " AND kind != " + kind;
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  if (rs.next()) id = rs.getInt(1);
		  }
		  catch (SQLException e) { System.out.println("getObjectId(): " + e.getMessage());  }
		  return id;
	  }
	  
	  public int getObjectIdEndingNotKind(int sourceId, String acc, int kind) {
		  if (sourceId==Source.UNDEF){
			  return Element.UNDEF;
		  }
		  int id = Element.UNDEF;
		  String query = MySQL.SELECT +"object_id FROM " + TABLE_OBJECT + 
		  	" WHERE source_id = " + sourceId + " AND accession Like '%" + acc + "'" + " AND kind != " + kind;
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  if (rs.next()) id = rs.getInt(1);
		  }
		  catch (SQLException e) { System.out.println("getObjectId(): " + e.getMessage());  }
		  return id;
	  }
	  
	  public boolean sourceHasPaths(int sourceId) {
		  if (sourceId==Source.UNDEF){
			  return false;
		  }
		  String query = MySQL.SELECT +"count(*) FROM " + TABLE_OBJECT + 
		  	" WHERE source_id = " + sourceId + " AND kind = " + Element.KIND_ELEMPATH;
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  if (rs.next()){
				  int count = rs.getInt(1);
				  if (count>0){
					  return true;
				  }
			  }
		  }
		  catch (SQLException e) { System.out.println("getObjectId(): " + e.getMessage());  }
		  return false;
	  }
	  
	  public HashSet<Integer> getObjectIdEndWithAcc(int sourceId, String acc) {
		  if (sourceId==Source.UNDEF){
			  return null;
		  }
		  HashSet<Integer> ids = new HashSet<Integer>();
		  String query = MySQL.SELECT +"object_id FROM " + TABLE_OBJECT + 
		  	" WHERE source_id = " + sourceId + " AND accession LIKE '%" + acc + "'";
		  try {
			  ResultSet rs = statement.executeQuery(query);

			  while (rs.next()){
				 int id = rs.getInt(1);
				 ids.add(id);
			  }
			  if (ids.isEmpty()) return null;
		  }
		  catch (SQLException e) { System.out.println("getObjectId(): " + e.getMessage());  }
		  return ids;
	  }
	  
	  public HashSet<Integer> getObjectIds(int sourceId) {
		  if (sourceId==Source.UNDEF){
			  return null;
		  }
		  HashSet<Integer> ids = new HashSet<Integer>();
		  String query = MySQL.SELECT +"object_id FROM " + TABLE_OBJECT + 
		  	" WHERE source_id = " + sourceId ;
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  while (rs.next()){
				 int id = rs.getInt(1);
				 ids.add(id);
			  }
			  if (ids.isEmpty()) return null;
		  }
		  catch (SQLException e) { System.out.println("getObjectId(): " + e.getMessage());  }
		  return ids;
	  }
	  
	  public int getObjectRelCnt(int sourceRelId) {
		  if (sourceRelId==SourceRelationship.UNDEF){
			  return 0;
		  }
		  int size = 0;
		  String query = MySQL.SELECT +"COUNT(*) FROM " + TABLE_OBJECT_REL + 
		  	" WHERE sourcerel_id = " + sourceRelId ;
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  if (rs.next()){
				 size = rs.getInt(1);
			  }
		  }
		  catch (SQLException e) { System.out.println("getObjectId(): " + e.getMessage());  }
		  return size;
	  }
	  
	  public int getSourceRelCount(int sourcerel_id) {
		  String query = MySQL.SELECT +" count(*) " +
		  	"FROM " + TABLE_OBJECT_REL + " WHERE sourcerel_id="+sourcerel_id;
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  while (rs.next()){
				  return rs.getInt(1);
			  }
		  }
		  catch (SQLException e) { System.out.println("getSourceRelCount(): " + e.getMessage());  }
		  return -1;
	  }
	  
	  public boolean existObjectRel(int sourceRelId, int obj1Id, int obj2Id) {
		  String query = MySQL.SELECT +"* FROM " + TABLE_OBJECT_REL + 
		  	" WHERE sourcerel_id = " + sourceRelId + " AND object1_id = " + obj1Id + " AND object2_id = " + obj2Id ;
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  if (rs.next()) return true;
		  }
		  catch (SQLException e) { System.out.println("existObjectRel(): " + e.getMessage());  }
		  return false;
	  }	  
	  
	  public boolean existWorkflowVariable(String name) {
		  String query = MySQL.SELECT +"* FROM " + TABLE_WORKFLOW + 
		  	" WHERE name = '" + name + "'" ;
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  if (rs.next()) return true;
		  }
		  catch (SQLException e) { System.out.println("existObjectRel(): " + e.getMessage());  }
		  return false;
	  }	  
	  
	  public HashSet<Source> getSources() {
		    String query = MySQL.SELECT +"* FROM " + TABLE_SOURCE + " WHERE status='"+STATUS_IMPORT_DONE+"'";
		    HashSet<Source> sources = new HashSet<Source>();
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      while (rs.next()) {
		        int id = rs.getInt("source_id");
		        String name = rs.getString("name");
		        String type = rs.getString("type");
		        String url = rs.getString("url");
		        String provider = rs.getString("provider");
		        String date = rs.getString("date");
		        // additional: author, domain, version
		        String author = rs.getString("author");
		        String domain = rs.getString("domain");
		        String version = rs.getString("version");
		        String comment = rs.getString("comment");
		        Source source = new Source(id, name, type, url, provider, date, author, domain, version, comment);
		        sources.add(source);
		      }
		    }
		    catch (SQLException e) { 
		    	System.out.println("getSources(): " + e.getMessage()); 
		    	if (e.getMessage().contains("doesn't exist")){
		    		createRepositorySchema();
		    		System.out.println("create repository schema");
		    	}
		    }
		    if (sources.isEmpty()) return null;
		    return sources;
		  }
	  
	  
		/**
		 * @return SourceRelationship (all existing)
		 */
		public HashSet<SourceRelationship> getSourceRels() {
			HashSet<SourceRelationship> sourceRels = new HashSet<SourceRelationship>();

			String query = MySQL.SELECT +"* FROM " + TABLE_SOURCE_REL;
			try {
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {
					int sourcerel_id = rs.getInt("sourcerel_id");
					int source1Id = rs.getInt("source1_id");
					int source2Id = rs.getInt("source2_id");
					int type = rs.getInt("type");
					String name = rs.getString("name");
					String comment = rs.getString("comment");
					String provider = rs.getString("provider");
					int preprocessing = rs.getInt("preprocessing");
					String date = rs.getString("date");
					String status = rs.getString("status");
					SourceRelationship sourcerel = new SourceRelationship(sourcerel_id, source1Id, source2Id,
							type, name, comment, provider, preprocessing, date, status);	
					sourceRels.add(sourcerel);
				}
			}
			catch (SQLException e) { 
				System.out.println("getSourceRelationship(): " + e.getMessage()); 
			}
		    if (sourceRels.isEmpty()) return null;
			return sourceRels;
		}
	  
	  public String toSQLString(List list) {
		  if (list==null || list.isEmpty()) return null;
		  StringBuffer sb = new StringBuffer();
		  int listSize = list.size();
		  sb.append("(");
		  for (int i=0; i<listSize-1; i++){
			  sb.append("'").append(list.get(i)).append("',");
		  }
		  sb.append("'").append(list.get(listSize-1)).append("')");
		  return sb.toString();
	  }
	  
	  public String intToSQLString(List<Integer> list) {
		  if (list==null || list.isEmpty()) return null;
		  StringBuffer sb = new StringBuffer();
		  int listSize = list.size();
		  sb.append("(");
		  for (int i=0; i<listSize-1; i++){
			  sb.append(list.get(i)).append(",");
		  }
		  sb.append(list.get(listSize-1)).append(")");
		  return sb.toString();
	  }
	  
	  public String toSQLString(Set list) {
		  if (list==null || list.isEmpty()) return null;
		  StringBuffer sb = new StringBuffer();
		  sb.append("(");
		  for (Object object : list) {
			  sb.append("'").append(object).append("',");
		  }
		  sb.deleteCharAt(sb.lastIndexOf(","));
		  sb.append(")");
		  return sb.toString();
	  }

	  public String toSQLIDString(Set<Element> list) {
		  if (list==null || list.isEmpty()) return null;
		  StringBuffer sb = new StringBuffer();
		  sb.append("(");
		  for (Element element : list) {
			  sb.append("'").append(element.getId()).append("',");
		  }
		  sb.deleteCharAt(sb.lastIndexOf(","));
		  sb.append(")");
		  return sb.toString();
	  }
	  
	  public boolean isInstancesTableEmpty(int id){
//		    System.out.print("Is Instance table  ("+id+") ? ");
		    String query = MySQL.SELECT + " * FROM " + TABLE_INSTANCES + id;
			ResultSet resultSet;
			try {
				resultSet = statement.executeQuery(query);
				if  (resultSet.next()) {
			    	return false;
			    }
			} catch (SQLException e) {
				System.out.println("Repository.existInstanceTable: SQLException");
			}    
			return true;
	  }
	  
	  public boolean existInstancesTable(int id){
		    String database = System.getProperty("comaUrl");
		    if (database.indexOf("?")>-1){
		    	database = database.substring(0,database.indexOf("?"));
		    }
		    database=database.substring(database.lastIndexOf("/")+1);
		    String query = MySQL.SELECT + "* FROM " + MySQL.INFO_TABLE + " WHERE table_schema='"+database
		    +"' AND table_name='"+TABLE_INSTANCES+id+"'";
//		    executeQuery(query);	    
			ResultSet resultSet;
			try {
				resultSet = statement.executeQuery(query);
				if  (resultSet.next()) {
			    	return true;
			    }
			} catch (SQLException e) {
				System.out.println("Repository.existInstanceTable: SQLException");
			}  
			return false;
	  }
	  
	  //--------------------------------------------------------------------------//
	  // Delete routines                                                          //
	  //--------------------------------------------------------------------------//
	  public void deleteSource(int id) {
		  if (id==UNDEF){
			  return;
		  }
		  String[] queries = new String[2];
		  queries[0] = "DELETE FROM " + TABLE_OBJECT + " WHERE source_id=" + id;
		  queries[1] = "DELETE FROM " + TABLE_SOURCE + " WHERE source_id=" + id;
		  executeBatch(queries);
	  }
	  
	  public void deleteSourceRel(int id) {
		  String[] queries = new String[2];
		  queries[0] = "DELETE FROM " + TABLE_OBJECT_REL + " WHERE sourcerel_id=" + id;
		  queries[1] = "DELETE FROM " + TABLE_SOURCE_REL + " WHERE sourcerel_id=" + id;
		  executeBatch(queries);
	  }
	  
	  public void deleteSourceWithSourceRel(int id) {
		  String[] queries = new String[2];
		  queries[0] = "DELETE FROM " + TABLE_OBJECT + " WHERE source_id=" + id;
		  queries[1] = "DELETE FROM " + TABLE_SOURCE + " WHERE source_id=" + id;
		  executeBatch(queries);
		  
		  ArrayList<Integer> list2 = getSubSources(id);
		  if (list2!=null)
			  for (Integer source_id : list2) {
				  deleteSource(source_id);
			  }
		  int supersource_id =getSuperSource(id);
		  
		  HashSet<Integer> list = getSourceRelId(id);
		  for (Integer rel_id : list) {
			  deleteSourceRel(rel_id);
		  }
		  
		  list2 = getSubSources(supersource_id);
		  if (list2==null){
			  deleteSource(supersource_id);
		  }
	  }
	  
	  public void deleteInstances(int id){
		  if (existInstancesTable(id)){	  
			   String query= MySQL.DROP_TABLE + TABLE_INSTANCES + id;
			   executeQuery(query);
		  }
	  }
	  
	  public void deleteMatchResultDBs(){
			// delete MatchResult tmp tables	
			String query = "SHOW TABLES LIKE 'MatchResult_%'";
			try {
				ResultSet rs = statement.executeQuery(query);
				ArrayList<String> list = new ArrayList<String>();
				while (rs.next()) {
					String value = rs.getString(1);
					list.add(value);
				}
				for (String value : list) {
					query = "DROP TABLE IF EXISTS " +  value;
					statement.execute(query);
				}
				query = "SHOW TABLES LIKE 'MatchResult_%'";
				 rs = statement.executeQuery(query);
				list = new ArrayList<String>();
				 while (rs.next()) {
					 String value = rs.getString(1);
					 list.add(value);
				 }
				 for (String value : list) {
					 query = "DROP VIEW IF EXISTS " +  value;
					 statement.execute(query);
				 }
			}
			catch (SQLException e) { 
				System.out.println("dropRepositorySchema(): " + e.getMessage()); 
			}
	  }

	  void deleteInstanceTables(){
		    String database = System.getProperty("comaUrl");
		    database=database.substring(database.lastIndexOf("/")+1);
		    if (database.contains("?")){
		    	 database=database.substring(0, database.indexOf("?"));
		    }
		    String query = "select table_name from INFORMATION_SCHEMA.TABLES where table_schema='"+database+"' and table_name" +
//		    		">'" +TABLE_INSTANCES+"' and table_name<'"+TABLE_INSTANCES+"A'";
		    	" LIKE '" +TABLE_INSTANCES+"%'";
//		    String query = "show tables";
		    ResultSet resultSet;
			try {
				ArrayList<String> tables = new ArrayList<String>();
				resultSet = statement.executeQuery(query);
			    while (resultSet.next()){
			    	  tables.add(resultSet.getString("table_name"));
			    }
				String[] queries = new String[tables.size()];
			    for (int j=0; j<tables.size(); j++){
			    	  queries[j] =  MySQL.DROP_TABLE +  tables.get(j);
			    }
				executeBatch(queries);
			} catch (SQLException e) {
				e.printStackTrace();
			}  
	  }
	  
	  public void deleteWorkflowVariable(String name) {
		  if (name==null){
			  return;
		  }
		  String query = "DELETE FROM " + TABLE_WORKFLOW + " WHERE name='" + name + "'";
		  try {
			statement.execute(query);
		} catch (SQLException e) {
			System.out.println("Repository.deleteWorkflowVariable() Error deleting variable " 
					+ name + " " + e.getMessage());
		}
	  }


    //morfoula
    public ResultSet performSelectQuery(String queryString){
        try {
            ResultSet rs = statement.executeQuery(queryString);
            return rs;
        }
        catch (SQLException e) {
            System.out.println("getSourceRelationship(): " + e.getMessage());
            return null;
        }
    }

    public ResultSet getAttributeNames(String queryString){
        try {
            ResultSet rs = statement.executeQuery(queryString);
            return rs;
        }
        catch (SQLException e) {
            System.out.println("getSourceRelationship(): " + e.getMessage());
            return null;
        }
    }
	  
}
