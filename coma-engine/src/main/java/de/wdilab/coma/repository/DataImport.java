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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Path;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This class grants writing access to the repository. This including inserting
 * as well as updating information regarding e.g. source, source relationships,
 * elements, correspondences between elements, instances, workflow variables.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class DataImport extends Repository{

//	private String session = null;
	  
	public DataImport(){
		super();
	    prepareAllStatements();
//	    session = String.valueOf(System.currentTimeMillis());
	}
	
	public DataImport(Connection connection){
		super(connection);
	    prepareAllStatements();
//	    session = String.valueOf(System.currentTimeMillis());
	}
	
	
	public void prepareAllStatements() {
		try {
			insertSourcePstmt = connection.prepareStatement(MySQL.INSERT_SOURCE, Statement.RETURN_GENERATED_KEYS);
			insertSourceRelPstmt = connection.prepareStatement(MySQL.INSERT_SOURCE_REL, Statement.RETURN_GENERATED_KEYS);
			insertObjectPstmt = connection.prepareStatement(MySQL.INSERT_OBJECT, Statement.RETURN_GENERATED_KEYS);
			insertObjectRelPstmt = connection.prepareStatement(MySQL.INSERT_OBJECT_REL);
			insertWorkflowPstmt = connection.prepareStatement(MySQL.INSERT_WORKFLOW);
		}
		catch (SQLException e) { System.out.println("prepareAllStatements(): " + e.getMessage()); }
	}
	
	public void closeStatements() {
		try {
			insertSourcePstmt.close();
			insertObjectPstmt.close();
			insertSourceRelPstmt.close();
			insertObjectRelPstmt.close();
			insertWorkflowPstmt.close();
		} catch (SQLException e) {
			System.out.println("closeAllStatements(): " + e.getMessage());
		}
	}
	

	  //--------------------------------------------------------------------------//
	  // Insert routines                                                          //
	  //--------------------------------------------------------------------------//
	  public int insertSource(String name, String type, String url, String provider, String date,
			   String author, String domain, String version, String comment) {
	    int id = getSourceId(name, provider);
	    if (id!=Source.UNDEF) return id;
	    try {
	      insertSourcePstmt.setString(1, name);
	      insertSourcePstmt.setString(2, type);
	      insertSourcePstmt.setString(3, url);
	      insertSourcePstmt.setString(4, provider);
	      insertSourcePstmt.setString(5, date);
	      // additional: author, domain, version
	      insertSourcePstmt.setString(6, author);
	      insertSourcePstmt.setString(7, domain);
	      insertSourcePstmt.setString(8, version);
	      insertSourcePstmt.setString(9, comment);
	      // state is updated separately 
	      insertSourcePstmt.executeUpdate();
	      ResultSet rs = insertSourcePstmt.getGeneratedKeys();
	      if (rs.next()) id = rs.getInt(1);
	    }
	    catch (SQLException e) { System.out.println("insertSource(): " + e.getMessage()); }
	    return id;
	  }
	  
	  public int insertSource(String name, String type, String url, String provider, String date) {
	    int id = getSourceId(name, provider);
	    if (id!=Source.UNDEF) return id;
	    try {
	      insertSourcePstmt.setString(1, name);
	      insertSourcePstmt.setString(2, type);
	      insertSourcePstmt.setString(3, url);
	      insertSourcePstmt.setString(4, provider);
	      insertSourcePstmt.setString(5, date);
	      // additional: author, domain, version
	      insertSourcePstmt.setString(6, null);
	      insertSourcePstmt.setString(7, null);
	      insertSourcePstmt.setString(8, null);
	      insertSourcePstmt.setString(9, null);
	      // state is updated separately 
	      insertSourcePstmt.executeUpdate();
	      ResultSet rs = insertSourcePstmt.getGeneratedKeys();
	      if (rs.next()) id = rs.getInt(1);
	    }
	    catch (SQLException e) { System.out.println("insertSource(): " + e.getMessage()); }
	    return id;
	  }
	  

	  public int insertSourceRel(int source1Id, int source2Id, int type, String name, 
			  String comment, String provider, int preprocessing, String date) {
	    int id = SourceRelationship.UNDEF;
	    id = getSourceRelId(source1Id, source2Id, type, name);
	    if (id!=SourceRelationship.UNDEF) return id;
	    id = getSourceRelId(source2Id, source1Id, type, name);
	    if (id!=SourceRelationship.UNDEF) return id;
	    try {
	      insertSourceRelPstmt.setInt(1, source1Id);
	      insertSourceRelPstmt.setInt(2, source2Id);
	      insertSourceRelPstmt.setInt(3, type);
	      insertSourceRelPstmt.setString(4, name);
	      insertSourceRelPstmt.setString(5, comment);
	      insertSourceRelPstmt.setString(6, provider);
	      insertSourceRelPstmt.setInt(7, preprocessing);
	      insertSourceRelPstmt.setString(8, date);
	      insertSourceRelPstmt.executeUpdate();
	      ResultSet rs = insertSourceRelPstmt.getGeneratedKeys();
	      if (rs.next()) id = rs.getInt(1);
	    }
	    catch (SQLException e) { System.out.println("insertSourceRel(): " + e.getMessage()); }
	    return id;
	  }


	  public int insertObject(int sourceId, String accession, String name,
	                          String type, String typespace, int kind,
	                          String comment, String synonyms) {
		  if (accession== null || name==null){
			  return Element.UNDEF;
		  }
	    int id = getObjectId(sourceId, accession, kind);
	    if (id!=Element.UNDEF) {
	        //System.out.println("insertObject(): Existing obj: " + sourceId + ", " + accession + ", " + textRep);
	        return id;
	    }
	    try {
	      insertObjectPstmt.setInt(1, sourceId);
	      insertObjectPstmt.setString(2, accession);
	      insertObjectPstmt.setString(3, name);
	      insertObjectPstmt.setString(4, type);
	      insertObjectPstmt.setString(5, typespace);
	      insertObjectPstmt.setInt(6, kind);
	      insertObjectPstmt.setString(7, comment);
	      insertObjectPstmt.setString(8, synonyms);
	      insertObjectPstmt.executeUpdate();
	      ResultSet rs = insertObjectPstmt.getGeneratedKeys();
	      if (rs.next()) id = rs.getInt(1);
	    }
	    catch (SQLException e) { 
	    	System.out.println("insertObject(): " + e.getMessage()); 
	    	}
	    return id;
	  }


	  public void insertObjectRel(int sourceRelId, int object1Id, int object2Id, float similarity,
	                            String type) {
		if (object1Id==Element.UNDEF || object2Id==Element.UNDEF){
			System.out.println("insertObjectRel(): Not defined object id: " + object1Id + " or " + object2Id);
			return;
		}
	    if (existObjectRel(sourceRelId, object1Id, object2Id)) {
	        //System.out.println("insertObjectRel(): Existing rel: " + sourceRelId + ": " + object1Id + " -> " + object2Id);
	        return;
	    }
	    if (existObjectRel(sourceRelId, object1Id, object2Id)) {
	        //System.out.println("insertObjectRel(): Existing rel: " + sourceRelId + ": " + object1Id + " -> " + object2Id);
	        return;
	    }
	    try {
	      insertObjectRelPstmt.setInt(1, sourceRelId);
	      insertObjectRelPstmt.setInt(2, object1Id);
	      insertObjectRelPstmt.setInt(3, object2Id);
	      insertObjectRelPstmt.setFloat(4, similarity);
	      insertObjectRelPstmt.setString(5, type);
	      insertObjectRelPstmt.executeUpdate();
//	      ResultSet rs = insertObjectRelPstmt.getGeneratedKeys();
//	      if (rs.next()) id = rs.getInt(1);
	    }
	    catch (SQLException e) { System.out.println("insertObjectRel(): " + e.getMessage()); }
	  }
	  

	  public void insertObjectRel(int sourceRelId, String resulttable) {
		if (resulttable==null){
			System.out.println("insertObjectRel(): Not defined resulttable: " + resulttable);
			return;
		}
		  String query =MySQL.INSERT_INTO + TABLE_OBJECT_REL  
		  + "(sourcerel_Id, object1_id,object2_id,similarity)" 
		  + MySQL.SELECT + sourceRelId + " AS sourcerel_id, " 
		  + " srcId, trgId, sim FROM " + resulttable;
		  executeQuery(query);
	  }
	  
	  

	  public boolean insertWorkflowVariable(String name, String value) {
		if (name==null || value==null){
			System.out.println("insertWorkflowVariable(): problem name=" + name + " or value=" + value);
			return false;
		}
		if (existWorkflowVariable(name)){
			  String query = "UPDATE " + TABLE_WORKFLOW + " SET value= '" + value + "'" 
			  	+ " WHERE name='" + name+"'";
			  executeQuery(query);
			  return true;
		}
	    try {
	    	insertWorkflowPstmt.setString(1, name);
	    	insertWorkflowPstmt.setString(2, value);
	    	insertWorkflowPstmt.executeUpdate();
	    	return true;
	    }
	    catch (SQLException e) { System.out.println("insertWorkflowVariable(): " + e.getMessage()); }
	    return false;
	  }  
	  
	  public int insertInstance(String connect, int accession, int instanceid, String attribute, String value) {
		  int id =-1;
	   try {
	     insertInstancesPstmt.setString(1, connect);
	     insertInstancesPstmt.setInt(2, accession);
	     insertInstancesPstmt.setInt(3, instanceid);
	     insertInstancesPstmt.setString(4, attribute);
	     insertInstancesPstmt.setString(5, value);
	     insertInstancesPstmt.executeUpdate();
	     ResultSet rs = insertInstancesPstmt.getGeneratedKeys();
	     if (rs.next()) id = rs.getInt(1);
	   }
	   catch (SQLException e) { System.out.println("insertInstance(): " + e.getMessage()); }
	   return id;
	 }
	  	  
	  
	  
	  //--------------------------------------------------------------------------//
	  // Update routines                                                          //
	  //--------------------------------------------------------------------------//
	  
	  public void updateSource(int id, String author, String domain, String version, String comment) {
		  if (id==Source.UNDEF){
			  return;
		  }
		  if (author==null && domain==null && version==null && comment==null){
			  return;
		  }
		  String query = "UPDATE " + TABLE_SOURCE + " SET";
		  if (author!=null && !author.isEmpty()){
			  query+= " author= '" + author + "'";
		  }
		  if (domain!=null && !domain.isEmpty()){			  
				  query+= ", domain= '" + domain + "'";
		  }
		  if (version!=null && !version.isEmpty()){			 
				  query+= ", version= '" + version + "'";
		  }
		  if (comment!=null && !comment.isEmpty()){
				  query+= ", comment= '" + comment + "'";
		  }
		  if (query.contains("SET, ")){
			  query = query.replace("SET, ", "SET ");
		  }
		  query+= " WHERE source_id=" + id;
		  executeQuery(query);
	  }
	  
	  public void updateSourceUrl(int id, String url) {
		  if (id==Source.UNDEF || url==null){
			  return;
		  }
		  String query = "UPDATE " + TABLE_SOURCE + " SET url= '" + url + "'" 
		  	+ " WHERE source_id=" + id;
		  executeQuery(query);
	  }
	  
	  public void updateSourceDomain(int id, String domain) {
		  if (id==Source.UNDEF || domain==null){
			  return;
		  }
		  String query = "UPDATE " + TABLE_SOURCE + " SET domain= '" + domain + "'" 
		  	+ " WHERE source_id=" + id;
		  executeQuery(query);
	  }
	  
	  public void updateSource(int id, String status) {
		  if (id==Source.UNDEF || status==null){
			  return;
		  }
		  String query = "UPDATE " + TABLE_SOURCE + " SET status= '" + status + "'" 
		  	+ " WHERE source_id=" + id;
		  executeQuery(query);
	  }
	  
	  public void updateSourceRel(int id, String status) {
		  if (id==SourceRelationship.UNDEF || status==null){
			  return;
		  }
		  String query = "UPDATE " + TABLE_SOURCE_REL + " SET status= '" + status + "'" 
		  	+ " WHERE sourcerel_id=" + id;
		  executeQuery(query);
	  }
	  
	  public void updateObject(int sourceId, int elementId, String accession, String name,
	            String type, String typespace, int kind, String comment, String synonyms) {
		  if (sourceId==Source.UNDEF || sourceId == Element.UNDEF){
			  return;
		  }
		  if (accession==null && name==null && type==null && typespace==null
				  && kind==-1 && comment==null && synonyms==null){
			  return;
		  }
		  String query = "UPDATE " + TABLE_OBJECT + " SET";
		  if (accession!=null && !accession.isEmpty()){
			  query+= getString(accession, "accession");
		  }
		  if (name!=null && !name.isEmpty()){			  
			  query+= "," + getString(name, "name");
		  }
		  if (type!=null && !type.isEmpty()){
			  query+= "," + getString(type, "type");				  
		  }
		  if (typespace!=null && !typespace.isEmpty()){
			  query+= "," + getString(typespace, "typespace");			  
		  }
		  if (kind!=Element.UNDEF){
			  query+= ", kind=" + kind;
		  }
		  if (comment!=null && !comment.isEmpty()){
			  query+= "," + getString(comment, "comment");			
		  }
		  if (synonyms!=null && !synonyms.isEmpty()){
			  query+= "," + getString(synonyms, "synonyms");		
		  }
		  if (query.contains("SET, ")){
			  query = query.replace("SET, ", "SET ");
		  }
		  query+= " WHERE source_id=" + sourceId + " AND object_id=" + elementId;
		  executeQuery(query);
	  }
	  
	  String getString(String value, String attribute){
		  if (value!=null && !value.isEmpty()){
			  String query = " " + attribute + "= ";
			  if (!value.contains("'")){ // no apostrophe in value				  
				  query+= "'" + value + "'";
			  } else if (value.indexOf('"')<0){
				  query+= '"' + value + '"'; // no quotes in value
			  } else {			
				  // apostrophe and quotes in value -> replace apostrophe with acute (accent) 
				  value= value.replace("'", "´");
				  query+= "'" + value + "'";
			  }
			  return query;
		  }
		  return null; 
	  }
	  
	  
	  public boolean updateWorkflowVariable(String name, String value) {
			if (name==null || value==null){
				System.out.println("insertWorkflowVariable(): problem name=" + name + " or value=" + value);
				return false;
			}
			if (existWorkflowVariable(name)){
				  String query = "UPDATE " + TABLE_WORKFLOW + " SET value= '" + value + "'" 
				  	+ " WHERE name='" + name+"'";
				  executeQuery(query);
				  return true;
			}
			return false;
	  }
	  
	  public void updateInstance(int instanceid, int id) {
		   try {
		     updateInstancesPstmt.setInt(1, instanceid);
		     updateInstancesPstmt.setInt(2, id);
		     updateInstancesPstmt.executeUpdate();
		   }
		   catch (SQLException e) { System.out.println("updateInstance(): " + e.getMessage()); }
	  }
	  

		// Save match result to repository, always save in GRAPH_STATE_SIMPLIFIED
		public boolean saveMatchResult(MatchResult matchResult) {
			if (matchResult == null)
				return false;
			boolean verbose = false;
			if (verbose)
				System.out.println("saveMatchResult(): Save mapping: "
						+ matchResult.getName());
			Graph sourceGraph = matchResult.getSourceGraph();
			Graph targetGraph = matchResult.getTargetGraph();
			int graphState = sourceGraph.getPreprocessing();
//			if (!(Source.TYPE_ONTOLOGY==sourceGraph.getSource().getType()) 
//					&& !(Source.TYPE_ONTOLOGY==targetGraph.getSource().getType())){
//				// Transform to Simplified state
//				MatchResult tmpMatchResult = MatchResult.transformMatchResult(matchResult, Graph.GRAPH_STATE_SIMPLIFIED);
//				if (tmpMatchResult == null ) {
//					// failed because e.g. really large xml schemas do not have a simplified state
//					System.out.println("saveMatchResult(): Transformation to SIMPLIFIED failed, therefore use REDUCED");
//					matchResult = MatchResult.transformMatchResult(matchResult,	Graph.GRAPH_STATE_REDUCED);
//				} else {
//					matchResult = tmpMatchResult;
//				}	
//				sourceGraph = matchResult.getSourceGraph();
//				targetGraph = matchResult.getTargetGraph();
//			}
//			if (matchResult == null) {
//				System.out
//						.println("saveMatchResult(): Transformation to REDUCED failed");
//				return false;
//			}
			if (sourceGraph == null || targetGraph == null) {
				System.out
						.println("saveMatchResult(): No source/target graph specified");
				return false;
			}
			Source source1 = sourceGraph.getSource();
			Source source2 = targetGraph.getSource();
			if (source1 == null || source2 == null) {
				System.out
						.println("saveMatchResult(): No source/target schema specified");
				return false;
			}
			int source1Id = source1.getId();
			int source2Id = source2.getId();
			// TODO Import not yet saved schemas before mapping
//			if (source1Id == Source.UNDEF) {
//				if (verbose)
//					System.out.println(" - Save source schema " + source1);
//				saveSchemaGraph(sourceGraph);
//				source1 = sourceGraph.getSource();
//				source1Id = source1.getId();
//			}
//			if (source2Id == Source.UNDEF) {
//				if (verbose)
//					System.out.println(" - Save target schema " + source2);
//				saveSchemaGraph(targetGraph);
//				source2 = targetGraph.getSource();
//				source2Id = source2.getId();
//			}
			String resultName = matchResult.getName();
//			String evidence = matchResult.getEvidence();
			String matchInfo = matchResult.getMatchInfo();
			String provider = matchResult.getProvider();
//			String matcherName = matchResult.getMatcherName();
//			MatcherConfig matcherConfig = matchResult.getMatcherConfig();
			int mappingId = getSourceRelId(source1Id, source2Id, 
					SourceRelationship.REL_MATCHRESULT, resultName);
			if (mappingId == -1) {
				mappingId = insertSourceRel(source1Id, source2Id,
						SourceRelationship.REL_MATCHRESULT, resultName, 
						matchInfo,provider,graphState, new java.util.Date().toString());
			} else {
				System.out
						.println("saveMatchResult(): Error saving already existing match result "
								+ resultName + " with id " + mappingId);
				return false;
			}
			updateSourceRel(mappingId, STATUS_IMPORT_STARTED);
						
				String type = null;
				String typespace = null;
				String comment = null;
				ArrayList aObjects = matchResult.getSrcMatchObjects();
				ArrayList bObjects = matchResult.getTrgMatchObjects();			
				for (int i = 0; i < aObjects.size(); i++) {
					Object aObj = aObjects.get(i);
					for (int j = 0; j < bObjects.size(); j++) {
						Object bObj = bObjects.get(j);
						float sim = matchResult.getSimilarity(aObj, bObj);
						if (sim > 0) {
							if (aObj instanceof Element
									&& bObj instanceof Element) { // node match
								// result
								Element aElem = (Element) aObj;
								Element bElem = (Element) bObj;
								insertObjectRel(mappingId, aElem.getId(),bElem.getId(), 
										sim ,null);
							} else if (aObj instanceof Path
									&& bObj instanceof Path) { // path match result
								Path aPath = (Path) aObj;
								Path bPath = (Path) bObj;
								String aName = aPath.toNameString();							
								String aAcc = aPath.toIdString();
								String bName = bPath.toNameString();							
								String bAcc = bPath.toIdString();
								int kind = Element.KIND_ELEMPATH;
								int aId = insertObject(source1Id, aAcc, aName,
										type, typespace, kind, comment,	null);
								int bId = insertObject(source2Id, bAcc, bName,
										type, typespace, kind, comment,	null);
								insertObjectRel(mappingId, aId, bId, sim, null);
							} else {
								if (verbose)
									System.out.println(" - Ignore incompatible objs "
											+ aObj + " and " + bObj);
							}
						}
					}
				}
			
			updateSourceRel(mappingId, STATUS_IMPORT_DONE);
			return true;
		}


}
