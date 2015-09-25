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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;
import de.wdilab.coma.structure.graph.DirectedGraphImpl;
import de.wdilab.coma.structure.graph.GraphUtil;

/**
 * This class grants reading access to the information stored in the repository.
 * It includes getting the list of all sources and source relationships stored 
 * there as well as the possibility to load them.
 *  
 * @author Hong Hai Do, Sabine Massmann
 */
public class DataAccess extends Repository{

	// constants
	 static final boolean verbose = false;
	
	public DataAccess(){
		super();
	}
	
	public DataAccess(Connection connection){
		super(connection);
	}
	
	
	/**
	 * @param sourceId
	 * @return Source for the given source id
	 */
	public Source getSource(int sourceId) {
		if (sourceId==Source.UNDEF){
			return null;
		}
		Source source = null;
		String query = MySQL.SELECT +"* FROM " + TABLE_SOURCE+ " WHERE source_id=" + sourceId;
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
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
		        source = new Source(sourceId, name, type, url, provider, date, author, domain, version, comment);
			}
		}
		catch (SQLException e) { 
			System.out.println("getSource(): " + e.getMessage()); 
		}
		return source;
	}
	
	/**
	 * @param sourcerelId
	 * @return SourceRelationship for the given relationship id
	 */
	public SourceRelationship getSourceRel(int sourcerelId) {
		if (sourcerelId==SourceRelationship.UNDEF){
			return null;
		}
		SourceRelationship sourcerel = null;
		String query = MySQL.SELECT +"* FROM " + TABLE_SOURCE_REL+ " WHERE sourcerel_id=" + sourcerelId;
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
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
				sourcerel = new SourceRelationship(sourcerel_id, source1Id, source2Id,
						type, name, comment, provider, preprocessing, date, status);			
			}
		}
		catch (SQLException e) { 
			System.out.println("getSourceRelationship(): " + e.getMessage()); 
		}
		return sourcerel;
	}
	
	 public int getSourceRelIdWithName(Source source1, Source source2, String name) {
		 if (source1==null || source2==null){
			 return SourceRelationship.UNDEF;
		 }
		 return getSourceRelIdWithName(source1.getId(), source2.getId(), name);
	 }
	
	  public HashSet<SourceRelationship> getSourceRels(int sourceId) {
		  HashSet<Integer> relIds = getSourceRelId(sourceId);
		  if (relIds==null){
			  return null;
		  }
		  HashSet<SourceRelationship> rels = new HashSet<SourceRelationship>();
		  for (int relId : relIds) {
			SourceRelationship rel = getSourceRel(relId);
			if (rel!=null) rels.add(rel);
		  }
		  return rels;
	  }
	  
	  public HashSet<SourceRelationship> getSourceRels(int sourceId1, int sourceId2) {
		  HashSet<Integer> relIds = getSourceRelIds(sourceId1, sourceId2);
		  if (relIds==null){
			  return null;
		  }
		  HashSet<SourceRelationship> rels = new HashSet<SourceRelationship>();
		  for (int relId : relIds) {
			SourceRelationship rel = getSourceRel(relId);
			if (rel!=null) rels.add(rel);
		  }
		  return rels;
	  }
	
	
	  /**
	 * @param source
	 * @return List of sub sources (as Source objects) for the given sources
	 * returns null if no contain relationships given 
	 */
	public ArrayList<Source> getSubSources(Source source) {
		    if (source==null) return null;
		    int sourceId = source.getId();
		    ArrayList<Integer> sourcerel_Ids = getSubSources(sourceId);
		    if (sourcerel_Ids==null) return null;
		    
		    ArrayList<Source> subSources = new ArrayList<Source>();
		    for (int sourcerel_Id : sourcerel_Ids) {
				Source current = getSource(sourcerel_Id);
				subSources.add(current);
			}
		    return subSources;
	}
	
	/**
	 * @param name
	 * @return true if a source with the given names exists
	 * otherwise return false
	 */
	public boolean existSourceWithName(String name) {
		if (name==null){
			return false;
		}
		String query = MySQL.SELECT +"* FROM " + TABLE_SOURCE+ " WHERE name='" + name + "'";
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
				return true;
			}
		}
		catch (SQLException e) { 
			System.out.println("existSourceWithName(): " + e.getMessage()); 
		}
		return false;
	}
	
	
	public Source getSuperSource(Source source) {
	    if (source==null) return null;
	    int sourceId = source.getId();
	    Integer source_Id = getSuperSource(sourceId);
	    return getSource(source_Id);
	}
	
	public int getSourceWithProvider(String provider) {
		provider=provider.replace("\\", "/");
		  String query = MySQL.SELECT +"source_id FROM " + TABLE_SOURCE + " WHERE  status='"
		  +STATUS_IMPORT_DONE+"' AND " +  "provider='"+provider+"'";;
		  try {
			  ResultSet rs = statement.executeQuery(query);
			  if (rs.next()){
				  return rs.getInt(1);
			  }
		  }
		  catch (SQLException e) { System.out.println("getSourcesWithUrl(): " + e.getMessage());  }
		  return Source.UNDEF;
	}
	
	
	public ArrayList<Source> getSourcesWithUrl(String url) {
		// allow url=null
		ArrayList<Integer> list = getSourceIdsWithUrl(url);
		if (list==null){
			return null;
		}
		ArrayList<Source> foundSources = new ArrayList<Source>();
		
		for (int i=0; i<list.size(); i++) {
			int sourceId = list.get(i);
			Source source = getSource(sourceId);     
			foundSources.add(source);
		} 
		return foundSources;
	}
	
	public ArrayList<Source> getSourcesWithNameAndUrl(String name, String url) {
		// allow url=null
		ArrayList<Integer> list = getSourceIdsWithNameAndUrl(name, url);
		if (list==null){
			return null;
		}
		ArrayList<Source> foundSources = new ArrayList<Source>();
		
		for (int i=0; i<list.size(); i++) {
			int sourceId = list.get(i);
			Source source = getSource(sourceId);     
			foundSources.add(source);
		} 
		if (foundSources.isEmpty()) return null;
		return foundSources;
	}
	
	public ArrayList<Source> getSourcesWithName(String name) {
		// allow url=null
		ArrayList<Integer> list = getSourceIdsWithName(name);
		if (list==null){
			return null;
		}
		ArrayList<Source> foundSources = new ArrayList<Source>();
		
		for (int i=0; i<list.size(); i++) {
			int sourceId = list.get(i);
			Source source = getSource(sourceId);     
			foundSources.add(source);
		} 
		if (foundSources.isEmpty()) return null;
		return foundSources;
	}
	
	public ArrayList<Source> getSourcesUrl(String url) {
		// allow url=null
		ArrayList<Integer> list = getSourceIdsWithUrl(url);
		if (list==null){
			return null;
		}
		ArrayList<Source> foundSources = new ArrayList<Source>();
		
		for (int i=0; i<list.size(); i++) {
			int sourceId = list.get(i);
			Source source = getSource(sourceId);     
			foundSources.add(source);
		} 
		if (foundSources.isEmpty()) return null;
		return foundSources;
	}
	
	public ArrayList<Source> getSiblingSources(Source source) {
		if (source == null)
			return null;
		Source superSource = getSuperSource(source);
		if (superSource == null)
			return null;
		ArrayList<Source> siblingSources = getSubSources(superSource);
		siblingSources.remove(source);
		if (siblingSources.isEmpty())
			return null;
		return siblingSources;
	}
	
	
	/**
	 * @param rs
	 * @return create an Element instance containing the information of the current dataset of the result
	 */
	Element getElement(ResultSet rs){
   	 // load element
		try {
			int object_id = rs.getInt("object_id");
			int source_id = rs.getInt("source_id");
			String accession = rs.getString("accession");
			String name = rs.getString("name");
			String type = rs.getString("type");
			String typespace = rs.getString("typespace");
			int kind = rs.getInt("kind");
			String comment = rs.getString("comment");
			String synonyms = rs.getString("synonyms");
        
			Element object = new Element(object_id, source_id, 
					name, accession,  type, typespace, kind, comment, synonyms);
			return object;
		} catch (SQLException e) {
			System.out.println("DataAccess.getElement() Error loading information : " + e.getMessage());
		}
		return null;
	}
	
	
	
	/**
	 * @return HashMap of all pre-defined workflow variables (name and value)
	 */
	public HashMap<String, String> getWorkflowVariables() {
		HashMap<String, String> list = new HashMap<String, String>();
		String query = MySQL.SELECT +"* FROM " + TABLE_WORKFLOW;
		try {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String name = rs.getString("name");
				String value = rs.getString("value");
				list.put(name, value);			
			}
		}
		catch (SQLException e) { 
			System.out.println("getWorkflowVariables(): " + e.getMessage()); 
		}
		if (list.isEmpty()) return null;
		return list;
	}
	
	/**
	 * @return HashMap of all pre-defined workflow variables of a specific type 
	 * (indicated by the last Character e.g. W for Workflow)
	 */
	public HashMap<String, String> getWorkflowVariablesWithType(String type) {
		HashMap<String, String> list = new HashMap<String, String>();
		String query = MySQL.SELECT +"* FROM " + TABLE_WORKFLOW + " WHERE name LIKE '%"+type+"'";
		try {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String name = rs.getString("name");
				String value = rs.getString("value");
				list.put(name, value);			
			}
		}
		catch (SQLException e) { 
			System.out.println("getWorkflowVariablesWithType(): " + e.getMessage()); 
		}
		if (list.isEmpty()) return null;
		return list;
	}
	
	/**
	 * @return HashMap of all pre-defined workflow variables of a specific type 
	 * (indicated by the last Character e.g. W for Workflow)
	 */
	public HashMap<String, String> getWorkflowVariablesWithValue(String type) {
		HashMap<String, String> list = new HashMap<String, String>();
		String query = MySQL.SELECT +"* FROM " + TABLE_WORKFLOW + " WHERE value LIKE '%"+type+"%'";
		try {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String name = rs.getString("name");
				String value = rs.getString("value");
				list.put(name, value);			
			}
		}
		catch (SQLException e) { 
			System.out.println("getWorkflowVariablesWithType(): " + e.getMessage()); 
		}
		if (list.isEmpty()) return null;
		return list;
	}
	
	/**
	 * @return String return value for workflow variable with the given name
	 */
	public String getWorkflowVariable(String name) {		
		String query = MySQL.SELECT +"* FROM " + TABLE_WORKFLOW + " WHERE name='"+name+"'";
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
				String value = rs.getString("value");
				return value;	
			}
		}
		catch (SQLException e) { 
			System.out.println("getWorkflowVariable(): " + e.getMessage()); 
		}
		return null;
	}
	
	/**
	 * @return String return value for workflow variable with the given name
	 */
	public String getWorkflowVariableFull(String name) {		
		String query = MySQL.SELECT +"* FROM " + TABLE_WORKFLOW + " WHERE name='"+name+"'";
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
				String value = rs.getString("value");
				return replaceVariableNames(value);	
			}
		}
		catch (SQLException e) { 
			System.out.println("getWorkflowVariable(): " + e.getMessage()); 
		}
		return null;
	}
	
	/**
	 * @return String return value for workflow variable with the given name
	 */
	public String replaceVariableNames(String value) {		
		while (value.contains("$")){
			String nameTmp = value.substring(value.indexOf("$"));
			if (nameTmp.contains(";")){
				nameTmp=nameTmp.substring(0, nameTmp.indexOf(";"));
			}
			if (nameTmp.contains(",")){
				nameTmp=nameTmp.substring(0, nameTmp.indexOf(","));
			}
			if (nameTmp.contains("(")){
				nameTmp=nameTmp.substring(0, nameTmp.indexOf("("));
			}
			if (nameTmp.contains(")")){
				nameTmp=nameTmp.substring(0, nameTmp.indexOf(")"));
			}	
			// there should only be the name left
			String valueTmp = getWorkflowVariable(nameTmp);
			if (valueTmp==null){
				System.out.println("DataAccess.replaceVariableNames() Error getting value for " + nameTmp);
			} else {
				value = value.replace(nameTmp, valueTmp);
			}
		}
		return value;
	}
	
	
	/**
	 * @return String return value for workflow variable with the given name
	 */
	public String getWorkflowVariableWithoutVariables(String name) {		
		String query = MySQL.SELECT +"* FROM " + TABLE_WORKFLOW + " WHERE name='"+name+"'";
		try {
			ResultSet rs = statement.executeQuery(query);
			if (rs.next()) {
				String value = rs.getString("value");
				
				while(value.contains("$")){
					String variable = value.substring(value.indexOf("$"));
					if (variable.contains(";")){
						variable = variable.substring(0, variable.indexOf(";"));
					}
					if (variable.contains(",")){
						variable = variable.substring(0, variable.indexOf(","));
					}
					if (variable.contains(")")){
						variable = variable.substring(0, variable.indexOf(")"));
					}
					String variableValue = getWorkflowVariable(variable);
					value = value.replace(variable, variableValue);
				}
				
				return value;	
			}
		}
		catch (SQLException e) { 
			System.out.println("getWorkflowVariable(): " + e.getMessage()); 
		}
		return null;
	}
	  
	public void loadSynonyms(String name, ArrayList<ArrayList<String>> synPairList, 
			ArrayList<String> wordList) {
		ArrayList<Integer> result = getSourceIdsWithNameAndUrl(name, null);
		if (result==null){
			return;
		}
		int srcid = result.get(0);
		int relid = getSourceRelId_ISA(srcid, srcid);
		String query = "SELECT distinct o1.name, o2.name"
			+" FROM "+TABLE_OBJECT+" o1, "+TABLE_OBJECT_REL+" r,  "+TABLE_OBJECT+" o2"
			+" WHERE o1.object_id=r.object1_id and o2.object_id=r.object2_id and sourcerel_id="+relid
			+" ORDER BY o2.name, o1.name";		
		try {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String word1 = rs.getString(1);
				String word2 = rs.getString(2);
				ArrayList<String> syn = new ArrayList<String>();
				syn.add(word1);
				syn.add(word2);
				synPairList.add(syn);
				if (!wordList.contains(word1))
					wordList.add(word1);
				if (!wordList.contains(word2))
					wordList.add(word2);		
			}
		}
		catch (SQLException e) { 
			System.out.println("getSourceRelationship(): " + e.getMessage()); 
		}
		// if (relView!=null) accessor.dropView(relView.getViewName());
	}
	
	
	/**
	 * load the abbreviations+fullform or words+synonyms into two separate lists
	 * 
	 * @param name of the source
	 * @param leftList for abbreviation it is the short form, for synonyms the word to be replaced
	 * @param rightList for abbreviation it is the full form, for synonyms the synonym used for replacement
	 */
	public void loadList(String name, ArrayList<String> leftList, ArrayList<String> rightList) {
		ArrayList<Integer> result = getSourceIdsWithNameAndUrl(name, null);
		if (result==null){
			return;
		}
		int srcid = result.get(0);
		int relid = getSourceRelId_ISA(srcid, srcid);
		String query = "SELECT distinct o1.name, o2.name"
			+" FROM "+TABLE_OBJECT+" o1, "+TABLE_OBJECT_REL+" r,  "+TABLE_OBJECT+" o2"
			+" WHERE o1.object_id=r.object1_id and o2.object_id=r.object2_id and sourcerel_id="+relid
			+" ORDER BY o2.name, o1.name";		
		try {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String word1 = rs.getString(1).toLowerCase();
				String word2 = rs.getString(2).toLowerCase();
				leftList.add(word1);
				rightList.add(word2);
			}
		}
		catch (SQLException e) { 
			System.out.println("getSourceRelationship(): " + e.getMessage()); 
		}
		// if (relView!=null) accessor.dropView(relView.getViewName());
	}

	
		// functions

	  //---------------------------------------------------------------------------//
	  // Loading mappings into directed or weighted graphs                         //
	  //---------------------------------------------------------------------------//

	  /**
	 * @param source
	 * @param intraRel
	 * @return graph with the loaded nodes and edges
	 * 
	 * Load structure of a source into a DirectedGraphImpl graph object
	 * first get vertices (node) and then edges (relation/hierarchy)
	 */
	DirectedGraphImpl loadStructure(Source source, SourceRelationship intraRel) {
	    if (source==null) return null;
	    boolean verbose= false;
	    //build object graph
	    DirectedGraphImpl objectGraph = new DirectedGraphImpl();
	    //Extract objects from a source which may have sub-sources
	    ArrayList<Source> subSources = getSubSources(source);
	    String query = MySQL.SELECT + " * FROM " + TABLE_OBJECT +
        //Excluding path objects, but allow empty kind information	       
	    " WHERE (kind!='"+Element.KIND_ELEMPATH+"' OR kind IS NULL) AND " ;
	    if (subSources==null){
	    	// only use source
	    	query += "source_id="+source.getId();
	    } else {
	    	// use all the sub sources
	    	for (int i = 0; i < subSources.size(); i++) {
	    		Source subSource = subSources.get(i);
				if (i==0){
					query += "( source_id="+subSource.getId();
				} else {
					query += " OR source_id="+subSource.getId();
				}
			}
			query += ")";
	    }
	    
		HashMap<Integer, Element> vertices = new HashMap<Integer, Element>();
	    //load all the objects
	    long start = System.currentTimeMillis();
	    try {
	        ResultSet rs = statement.executeQuery(query);
	        while (rs.next()) {
	        	 // load element
	        	 Element elem = getElement(rs);
	            try {
	            	objectGraph.addVertex(elem);
	                vertices.put(elem.getId(), elem);
	            } catch (Exception e) { 
	            	System.out.println("loadStructure(): ERROR: adding element"); 
	            }	            
	        }
	      }
	      catch (SQLException e) {
	        queryStatus = false;
	        queryMessage = e.getMessage();
	        if (verbose) System.out.println("executeQuery(): " + query + " with ERROR " + queryMessage);
	      }
	    
	    long end = System.currentTimeMillis();
	    //System.out.println("loadStructure(): " + objView + ":" + rows.size() + " objects " + columns);
	    if (verbose) System.out.println("------>  new Elements: " + (float) (end - start) / 1000 + " s");
	    
	    //if there are no vertices, nothing to do more
	    if (objectGraph.getElementSet().isEmpty()) {
	      System.out.println("loadStructure(): No vertices loaded!");
	      return null;
	    }
	    
	    //if there is no intraRel given, nothing to do more
	    if (intraRel==null) {
	      System.out.println("loadStructure(): No structure relationship given!");
	      return objectGraph;
	    }
	    
	    query = MySQL.SELECT + " object1_id, object2_id, type FROM " + TABLE_OBJECT_REL +	       
	    " WHERE sourcerel_id="+intraRel.getId();
	    //load all the object relationships
	    start = System.currentTimeMillis();
	    try {
	        ResultSet rs = statement.executeQuery(query);
	        while (rs.next()) {
	        	 // load element
	            int id1 = rs.getInt("object1_id");
	            int id2 = rs.getInt("object2_id");
	            String type = rs.getString("type");
	            Element element1 = vertices.get(id1);
	            Element element2 = vertices.get(id2);
	            // object2 IS_A object1 => vertex1 -> vertex2
	            if (element1!=null && element2!=null) {
	            	try {
	            		objectGraph.addEdge(element1, element2, type);
	            	}
	            	catch (Exception e) { 
	            		System.out.println("loadStructure(): ERROR: adding edge"); 
	            		System.out.println(e);
	            		}
	            }	            
	        }
	      }
	      catch (SQLException e) {
	        queryStatus = false;
	        queryMessage = e.getMessage();
	        if (verbose) System.out.println("executeQuery(): " + query + " with ERROR " + queryMessage);
	      }
	    
	      end = System.currentTimeMillis();
	      if (verbose){
	    	  System.out.println("------>  addEdges : " + (float) (end - start) / 1000 + " s");
	    	  System.out.println("------>  objectGraph : " + objectGraph.getElementCount() + " vertices "+ 
	    			  objectGraph.getEdgesCount() + " edges");
	      }

	    return objectGraph;
	  }	
		
	
		/**
		 * @param sources
		 * @param intraRels
		 * @return a graph containing all the nodes and edges that are contained in the sources and 
		 * intra relationships -> the information is combined from all of them
		 */
		public DirectedGraphImpl loadCompositeStructure(ArrayList<Source> sources, ArrayList<SourceRelationship> intraRels) {
			if (sources==null) return null;
			DirectedGraphImpl compositeGraph = null;
			boolean verbose = false;
			for (int i=0; i<sources.size(); i++) {
				Source source = sources.get(i);
				SourceRelationship intraRel = null;
				if (intraRels!=null) intraRel = intraRels.get(i);
				long start = System.currentTimeMillis();
				DirectedGraphImpl subGraph = loadStructure(source, intraRel);      
				long end = System.currentTimeMillis();
				if (verbose) System.out.println("----> loadStructure: " +  i + " "
						+ (float) (end - start) / 1000 + " s");
				start = System.currentTimeMillis();
				if (subGraph!=null) {
					if (compositeGraph == null){
						compositeGraph = subGraph;
					} else {
						GraphUtil.addGraph(compositeGraph, subGraph);
					}
					end = System.currentTimeMillis();
			        if (verbose)  System.out.println("----> GMGraphUtil.addGraph(compositeGraph, subGraph);: " +  i + " "
			  				+ (float) (end - start) / 1000 + " s");
				}
			}
			return compositeGraph;
		}
		
//		/**
//		 * @param source
//		 * @param loadForeignTypes
//		 * @param preprocess
//		 * @param loadInstances
//		 * @return loaded graph including (if set true) foreign types, preprocessed states 
//		 * and loaded instances
//		 */
//		public Graph loadGraph(int sourceId,
//				boolean loadForeignTypes, boolean preprocess,
//				boolean loadInstances){
//			return loadGraph(getSource(sourceId), loadForeignTypes, preprocess, loadInstances);
//		}
		

		

		public void loadForeignTypes(Source source, Graph graph){
			long start = System.currentTimeMillis();
			// sources to resolve object types, if no sources with namespace found
			// either subsources of input schema if input schema is a parent source
			// or sibling sources of input schema if input schema is a child source
			ArrayList<Source> resolveSources = getSubSources(source);
			if (resolveSources == null) {
				resolveSources = getSiblingSources(source);
				if (resolveSources == null) {
					resolveSources = new ArrayList<Source>();
					resolveSources.add(source); // only this input schema
				}
			}

			// Only resolve global components from other sources
			ArrayList<Integer> resolveKinds = new ArrayList<Integer>();
			resolveKinds.add(Element.KIND_GLOBELEM);
			resolveKinds.add(Element.KIND_GLOBTYPE);

			// Determine locally unresolvable types
			 HashMap<String, HashSet<String>> typespaces = getUnresolvableTypes(source.getId());
//			 HashMap<String, HashSet<String>> typespaces = graph.getUnresolvableTypes();
//			 System.out.println(typespaces);
			 
			long end = System.currentTimeMillis();
				if (verbose)
						System.out.println("--> getUnresolvableTypes ["+typespaces.size()+"]: " + (float) (end - start) / 1000);
			if (typespaces.isEmpty()){
				return;
			}
				
			for (Iterator<String> iterator = typespaces.keySet().iterator(); iterator.hasNext();) {
				String typespace = iterator.next();
				// types to be retrieved from typespace
				HashSet<String> typeNames = typespaces.get(typespace);
				
				// use ArrayList - because contain uses equals() ... HashSet doesn't
				ArrayList<Element> doneElems = new ArrayList<Element>();
				
				loadSubGraph(resolveSources, graph, typespace, typeNames, doneElems, resolveKinds);
			}			
//			System.out.println("loadForeignTypes(): graph with " + graph.getVerticesCount() + 
//					" vertices and "+ graph.getEdgesCount() + " edges" );
		}

		HashMap<String, HashSet<String>> getUnresolvableTypes(int id) {
		    String query = MySQL.SELECT + " DISTINCT o.type, o.typespace FROM " + TABLE_OBJECT + " AS o, " + TABLE_SOURCE + " AS s "+ 
		    // only get the object where the typespace is not equal to or in the source typespace(s)
		    " WHERE o.source_id="+id +" AND s.source_id="+id + " AND  s.url!=o.typespace and INSTR(s.url,o.typespace)=0";

	        HashMap<String, HashSet<String>> typespaces = new HashMap<String, HashSet<String>>();
		    try {
		        ResultSet rs = statement.executeQuery(query);
		        while (rs.next()) {
		        	 // load info
		        	String typespace = rs.getString("typespace");
		        	HashSet<String> typeNames = typespaces.get(typespace);		        	
		            if (typeNames==null){
		            	typeNames = new HashSet<String>();
		            	typespaces.put(typespace, typeNames);
		            }
		        	String type = rs.getString("type");
		        	typeNames.add(type);
		        }
		      }
		      catch (SQLException e) {
		        queryStatus = false;
		        queryMessage = e.getMessage();
		        if (verbose) System.out.println("executeQuery(): " + query + " with ERROR " + queryMessage);
		      }
//		      System.out.println(typespaces);
		      return typespaces; 
		}
		
		
		// Load objects specified by names from particular sources and recursively load required types
		// resolveKinds specify the kinds of objects which can be used for type resolution
		Graph loadSubGraph(ArrayList<Source> sources, HashSet<String> names, ArrayList<Element> doneElems, ArrayList<Integer> resolveKinds) {
			if (sources == null)
				return null;
			ArrayList<Integer> sourceIds = new ArrayList<Integer>();
			for (int j = 0; j < sources.size(); j++) {
				Source source = sources.get(j);
				sourceIds.add(new Integer(source.getId()));
			}
			ArrayList<Element> elements = getElements(sourceIds, names, resolveKinds);
			if (elements == null) {
				if (verbose)
					System.out
							.println("loadSubGraph(): No objects retrieved -> return");
				return null;
			}
			if (verbose)
				System.out
						.println("loadSubGraph(): elements " + elements.size());
			
			Graph subGraph = new Graph();
//			System.out.println("names: " + names.size());
			for (int j = 0; j < elements.size(); j++) {
				Element elem = elements.get(j);
				if (!doneElems.contains(elem)) {
					String elemName = elem.getName();
					// check once more in case database returns case-insensitive results
					if (names == null || names.contains(elemName)) {
						DirectedGraphImpl elemGraph = loadSubStructure(elem);
//						System.out.println(elemName + " " + elemGraph.toString());
//						System.out.println(elemName + " " + elemGraph.getVerticesCount() + " " +  elemGraph.getEdgesCount());
						GraphUtil.addGraph(subGraph, elemGraph);
						doneElems.addAll(elemGraph.getElementSet());
					}
				}
			}
//			System.out.println("names: " + names.size() + " subGraph: " + subGraph.getVerticesCount() + " vertices " +  subGraph.getEdgesCount() + " edges");
//			System.out.println("doneElems: " + doneElems.size());
//			System.out.println("doneElems: " + doneElems.toString().replace(",","\n"));
			// Determine locally unresolvable types
			 HashMap<String, HashSet<String>> typespaces = subGraph.getUnresolvableTypes();			
 
//			 System.out.println("typespaces: " + typespaces.keySet().size());
//			 System.out.println("typenames: " + typespaces.toString().replace(",","\n"));
//			 System.out.println();
				
				
			 for (Iterator<String> iterator = typespaces.keySet().iterator(); iterator.hasNext();) {
			String typespace = iterator.next();
			// types to be retrieved from typespace
			HashSet<String> typeNames = typespaces.get(typespace);
			loadSubGraph(sources, subGraph, typespace, typeNames, doneElems, resolveKinds);
			
			}
			if (verbose)
				System.out.println("loadSubGraph(): DONE components " + names+ " and sources " + sources);
			return subGraph;
		}
		
		//Loading structure for a particular element into a single directedGraph
		DirectedGraphImpl loadSubStructure(Element elem) {
			if (elem==null) return null;
			 //System.out.println("Load subStructure for elem: " + elem);
		    int sourceId = elem.getSourceId();  
		    int structRelId = getSourceRelId_ISA(sourceId, sourceId);
		    SourceRelationship structRel = getSourceRel(structRelId);
		    
		    DirectedGraphImpl objectGraph = new DirectedGraphImpl();
		    
		    // get the element were the elem.accession is part of the accession as well 
		    // -> thus subelements in loaded state
		    
		    String query = MySQL.SELECT +" * " + "FROM " + TABLE_OBJECT + 
		       " WHERE source_id= " + sourceId + " AND accession like '"+elem.getAccession()+"%' " +
		       		" AND (kind!='"+Element.KIND_ELEMPATH+"' OR kind IS NULL)";

//		    System.out.println("loadSubStructure(): Query: " + query);
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      while (rs.next()) {
		    	  Element current = getElement(rs);
		    	  objectGraph.addVertex(current);
		      }
		    }
		    catch (SQLException e) { System.out.println("loadSubStructure(): " + e.getMessage()); }

		    if (objectGraph.getElementSet().isEmpty()) {
		        return null;
		    }
//		    System.out.println("objectGraph.getVerticesCount()): " + objectGraph.getVerticesCount());
		    //no structRel given
		    if (structRel==null) return objectGraph;
		    String idStr = toSQLIDString(objectGraph.getElementSet());
		    
		    query = MySQL.SELECT + " object1_id, object2_id FROM " + TABLE_OBJECT_REL +	       
		    " WHERE sourcerel_id="+structRelId +" AND (object1_id IN "+idStr+" OR object2_id IN "+idStr+" )";
		    
		    //load all the object relationships
		    try {
		        ResultSet rs = statement.executeQuery(query);
		        while (rs.next()) {
		        	 // load element
		            int id1 = rs.getInt("object1_id");
		            int id2 = rs.getInt("object2_id");
		            Element element1 = objectGraph.getElementWithId(id1);
		            Element element2 = objectGraph.getElementWithId(id2);
		            // object2 IS_A object1 => element1 -> element2
		            if (element1!=null && element2!=null) {
		            	try {
		            		objectGraph.addEdge(element1, element2);
		            	}
		            	catch (Exception e) { System.out.println("loadSubStructure(): ERROR: adding edge"); }
		            }	            
		        }
		      }
		      catch (SQLException e) {
		        queryStatus = false;
		        queryMessage = e.getMessage();
		        if (verbose) System.out.println("executeQuery(): " + query + " with ERROR " + queryMessage);
		   }
//		      System.out.println("objectGraph.getEdgesCount()): " + objectGraph.getEdgesCount());
		    return objectGraph;
		}
		
		  //Get objects of given sources, names, namespaces and kinds, sourceIds at least required
		  ArrayList<Element> getElements(ArrayList<Integer> sourceIds,  HashSet<String> names, ArrayList<Integer> kinds) {
		    if (sourceIds==null) return null;
		    String sourceIdStr = toSQLString(sourceIds);
		    String textRepStr = toSQLString(names);
		    String kindStr = intToSQLString(kinds);
		    ArrayList<Element> objects = new ArrayList<Element>();
		    String query = MySQL.SELECT +" * " + "FROM " + TABLE_OBJECT + " WHERE source_id IN " + sourceIdStr;
		    if (textRepStr!=null) query += " AND name IN " + textRepStr;
		    if (kindStr!=null) query += " AND kind IN " + kindStr;
//		    query+=" order by object_id";
		    
//		    System.out.println("getObjectsOfKinds(): Query: " + query);
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      while (rs.next()) {
		    	  Element elem = getElement(rs);
		    	  objects.add(elem);
		      }
		    }
		    catch (SQLException e) { System.out.println("getObjects(): " + e.getMessage()); }
		    if (objects.isEmpty()) return null;
		    return objects;
		  }
		  
	void loadSubGraph(ArrayList<Source> sources, Graph subGraph, String typespace, HashSet<String> typeNames, 
			ArrayList<Element> doneElems, ArrayList<Integer> resolveKinds){

		if (verbose)
			System.out.println(" + Processing namespace: " + typespace);

			// Identify sources to load required objects
			// srcs with url=currentTypespace
			ArrayList<Source> urlSources = getSourcesWithUrl(typespace); 
			if (urlSources == null)
				urlSources = new ArrayList<Source>();
			ArrayList<Source> resolveWithUrlSources = new ArrayList<Source> (urlSources);
			resolveWithUrlSources.retainAll(sources);
			
			// srcs without url
			ArrayList<Source> nourlSources = getSourcesWithUrl(null); 
			if (nourlSources == null)
				nourlSources = new ArrayList<Source>();
			ArrayList<Source>  resolveWithoutUrlSources = new ArrayList<Source> (nourlSources);
			resolveWithoutUrlSources.retainAll(sources);

			Graph nextGraph = null;
			ArrayList<String> currentNamespaces = new ArrayList<String>();
			currentNamespaces.add(typespace);

			if (urlSources.isEmpty()) {
				// Use source without namespace
				if (verbose)
					System.out.println(" - No sources with NS, use all resolve sources "+ sources);
				nextGraph = loadSubGraph(sources, typeNames, doneElems, resolveKinds);
			} else {
				// Use source with namespace
				if (verbose)
					System.out.println(" - Found sources with NS, use source with namespace "+ urlSources);
				nextGraph = loadSubGraph(urlSources, typeNames, doneElems, resolveKinds);
			}
			if (nextGraph!=null && verbose){
				System.out.println(" nextGraph vertices "+ nextGraph.getElementCount());
			}
			GraphUtil.addGraph(subGraph, nextGraph);

			if (verbose)
				System.out.println(" + Done namespace: " + typespace);
	}
	
//	public MatchResult loadMatchResult(int schemaGraph1Id,
//			int schemaGraph2Id, int sourcerelId) {
//		SourceRelationship simRel = getSourceRel(sourcerelId);
//		Graph schemaGraph1=loadGraph(schemaGraph1Id, true,  true, false);
//		Graph schemaGraph2=loadGraph(schemaGraph2Id, true,  true, false);
//		return loadMatchResult(schemaGraph1, schemaGraph2, simRel);
//	}
	
	public MatchResult loadMatchResult(Graph schemaGraph1,
			Graph schemaGraph2, int sourcerelId) {
		SourceRelationship simRel = getSourceRel(sourcerelId);
		return loadMatchResult(schemaGraph1, schemaGraph2, simRel);
	}

	// Load match result from repository, expect GRAPH_STATE_SIMPLIFIED results
		// in repository
	public MatchResult loadMatchResult(Graph schemaGraph1,
			Graph schemaGraph2, SourceRelationship sourcerel) {
		// assumption: graphs have correct preprocessing			
//		int srcId = schemaGraph1.getSource().getId();
//		int trgId = schemaGraph2.getSource().getId();
//		ArrayList  aPaths = schemaGraph1.getAllPaths();
//		ArrayList  bPaths = schemaGraph2.getAllPaths();
		MatchResult matchResult = null;	
//		HashMap<String, GraphPathImpl> srcPaths = new HashMap<String, GraphPathImpl>();
//		HashMap<String, GraphPathImpl> trgPaths = new HashMap<String, GraphPathImpl>();
//		boolean path = true;
			
//		boolean useArray = true;
//		if (schemaGraph1.getAllElements().size()>20000 || schemaGraph2.getAllElements().size()>20000){
//			useArray = false;
//		}
		
//		String query = MySQL.SELECT + " object1_id, object2_id, similarity FROM " + TABLE_OBJECT_REL +	       
//		" WHERE sourcerel_id="+simRel.getId();
		
		String query = MySQL.SELECT + "  r.object1_id, o1.kind AS o1_kind, o1.accession AS o1_accession, " +
				" r.object2_id, o2.kind AS o2_kind, o2.accession AS o2_accession, similarity"
			+" FROM "+TABLE_OBJECT+" o1, "+TABLE_OBJECT_REL+" r,  "+TABLE_OBJECT+" o2"
			+ " WHERE sourcerel_id="+sourcerel.getId() + " AND o1.object_id=r.object1_id AND o2.object_id=r.object2_id";	
		    
		    //load all the object relationships
		    try {
		        ResultSet rs = statement.executeQuery(query);
		        while (rs.next()) {
		        	 // load element
		        	int id1 = rs.getInt("object1_id");
		        	int id2 = rs.getInt("object2_id");	
		        	int kind1 = rs.getInt("o1_kind");		        
		        	int kind2 = rs.getInt("o2_kind");
		        	float sim = rs.getFloat("similarity");
//				String aAcc = (String) row.get(0);
//				String bAcc = (String) row.get(1);
				// Check if aAcc and bAcc are valid id paths
//				GraphPathImpl aPath = srcPaths.get(aAcc);
//				if (aPath ==null){
//					aPath = schemaGraph1.idStringToPath(aAcc);
//					srcPaths.put(aAcc, aPath);
//				}
//				GraphPathImpl bPath = trgPaths.get(bAcc);
//				if (bPath ==null){	
//					bPath = schemaGraph2.idStringToPath(bAcc);
//					trgPaths.put(bAcc, bPath);
//				}
//				if (aPath != null && bPath != null) {
//	//				if (verbose)
//	//					System.out.println(" + Append match: " + aPath + "<->"
//	//							+ bPath + ": " + sim);
//					if (matchResult==null){
//						matchResult = new MatchResultArray(aPaths, bPaths);
//					}
//					matchResult.append(aPath, bPath, sim);
//				} else{
//					int aId = getObjectId(srcId, aAcc);
//					int bId = getObjectId(trgId, bAcc);
		            // TODO PROBLEM: At the moment only node results supported
		            // TODO ADD: paths of graphs as objects into repository 

		        	Object aObject = null;
		        	Object bObject = null;
		        	
		        	if (kind1==Element.KIND_ELEMPATH && kind2==Element.KIND_ELEMPATH ){
			        	String accession1 = rs.getString("o1_accession");
			        	String accession2 = rs.getString("o2_accession");
		        	
			        	aObject = schemaGraph1.idStringToPath(accession1);
			        	bObject = schemaGraph2.idStringToPath(accession2);
		        		
						if (matchResult==null){
							ArrayList<Object>  aVertices = new ArrayList<Object>(schemaGraph1.getAllPaths());
							ArrayList<Object>  bVertices = new ArrayList<Object>(schemaGraph2.getAllPaths());
//							if (useArray){
								matchResult = new MatchResultArray(aVertices, bVertices);
//							} else {
//								matchResult = new MatchResultDB(aVertices, bVertices, statement);
//							}
						}
			        	
		        	} else if (kind1!=Element.KIND_ELEMPATH && kind2!=Element.KIND_ELEMPATH ){	        	
							// Check if aAcc and bAcc are simply node accessions
			        	aObject = schemaGraph1.getElementWithId(id1);
			        	bObject = schemaGraph2.getElementWithId(id2);
						if (matchResult==null){
							ArrayList<Object>  aVertices = new ArrayList<Object>(schemaGraph1.getElementSet());
							ArrayList<Object>  bVertices = new ArrayList<Object>(schemaGraph2.getElementSet());
							matchResult = new MatchResultArray(aVertices, bVertices);
						}
		        	} else {
		        		matchResult = new MatchResultArray();
		        	}
					if (aObject != null && bObject != null) {
						// TODO WHY?
//						if (path){
//							ArrayList  aVertices = new ArrayList(schemaGraph1.getElementSet());
//							ArrayList  bVertices = new ArrayList(schemaGraph2.getElementSet());
//							if (matchResult!=null){
//								MatchResult matchResult2 = new MatchResultArray(aVertices, bVertices);
//								matchResult = MatchResult.applyOperation(MatchResult.OP_MERGE, matchResult, matchResult2);
//							} else {
//								matchResult = new MatchResultArray(aVertices, bVertices);
//							}
//							path = false;
//						}
						matchResult.append(aObject, bObject, sim);
					} else {
						if (verbose)
							System.out.println(" - Ignore invalid graph components "
									+ id1 + " and " + id2);
					}
		        }
				if (matchResult!=null) {
					matchResult.setProvider(sourcerel.getProvider());
					matchResult.setName(sourcerel.getName());
					matchResult.setMatchInfo(sourcerel.getComment());
				}
			} catch (SQLException e) { System.out.println("getObjects(): " + e.getMessage()); }
			return matchResult;
		}
	
	
	  public ResultSet getInstancesOfElementId(int sourceId, int elementid) {
		    String query = MySQL.SELECT +"attribute, value" +
		        " FROM " + TABLE_INSTANCES +sourceId+ " WHERE elementid = '" + elementid + "'";
		    try {
		      ResultSet rs = statement.executeQuery(query);
		      return rs;
		    }
		    catch (SQLException e) { System.out.println("getInstancesOfElementId(): " + e.getMessage());   }
		    return null;
	  }
	
	  public boolean loadInstances(Source source, Set vertices){
		  if (vertices==null || vertices.isEmpty()){
			  return false;
		  }
		  if (source.getType()!=Source.UNDEF && existInstancesTable(source.getId())){
			  System.out.println("vertices " + vertices.size());
			  int count=0;
				  for (Iterator iter = vertices.iterator(); iter.hasNext();) {
					Element element = (Element) iter.next();
					ResultSet rs = getInstancesOfElementId(source.getId(), element.getId());
					if (rs!=null){
						try {
							while (rs.next()){
							    String attribute = rs.getString(1);
							    String value = rs.getString(2);
							    element.addInstance(attribute, value);
							}
						} catch (SQLException e) {
							System.out.println("DataAccess.loadInstances: SQLException");
							e.printStackTrace();
						}
					}
					if (element.hasAllInstancesSimple() || element.hasAllInstancesComplex()){
						count++;
					}
				}
				  System.out.println("DataAccess.loadInstances  count: " +count);
			  return true;
		  }
		  return false;
	  }
	
		public Graph loadAndPropagateInstances(boolean loadInstances, Graph schemaGraph){
			if (loadInstances && loadInstances(schemaGraph.getSource(), schemaGraph.getElementSet())){
				int currentState = schemaGraph.getPreprocessing();
				return propagateInstancesToParents(schemaGraph.getGraph(Graph.PREP_RESOLVED)).getGraph(currentState);
			}
			return schemaGraph;
		}
		
		public Graph propagateInstancesToParents(Graph schemaGraph) {
			int orgState = schemaGraph.getPreprocessing();
			schemaGraph = schemaGraph.getGraph(Graph.PREP_DEFAULT_ONTOLOGY);
			ArrayList<Element> leaves = schemaGraph.getLeaves();
			ArrayList<Element> alreadyDone = new ArrayList<Element>();		
			ArrayList<Element> parents = schemaGraph.getParents(leaves);
			while (parents != null && parents.size() > 0) {
				for (Element parent : parents) {
					// nur wenn parent keine eigenen Instanzen hat
//					if (parentElement.getInstances().size() != 0)
//						continue;
//					ArrayList<String> instances = new ArrayList<String>();
					// behalte Instanzen des Parent bei
//					 ArrayList<String> instances = parentElement.getInstances();
					ArrayList<Element> children = schemaGraph.getChildren(parent);
					ArrayList<String> instancesSimple = new ArrayList<String>();
					HashMap<String, ArrayList<String>> instancesComplex = new HashMap<String, ArrayList<String>>();
					for (Element child : children){
						instancesSimple.addAll(child.getDirectInstancesSimple());
//						instancesSimple.addAll(((Element)  ((VertexImpl) child).getObject())
//								.getIndirectInstancesSimple());
						// XXX
						HashMap<String, ArrayList<String>> directInstancesComplex  = child
									.getDirectInstancesComplex();
//						HashMap<String, ArrayList<String>> indirectInstancesComplex  = ((Element) ((VertexImpl) child).getObject())
//						.getIndirectInstancesComplex();		
						HashSet<String> keys = new HashSet<String>(directInstancesComplex.keySet());
//						keys.addAll(indirectInstancesComplex.keySet());
						
				    	for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
							String attribute = (String) iterator.next();
							ArrayList<String> values =instancesComplex.get(attribute);
							if (values==null){
								values = new ArrayList<String>();
							}
							ArrayList<String> instances = directInstancesComplex.get(attribute);
							if (instances!=null) {
								values.addAll(instances);
							}
//							if (indirectInstancesComplex.containsKey(attribute)) {
//								values.addAll(indirectInstancesComplex.get(attribute));
//							}
							instancesComplex.put(attribute, values);
						}
					}
//					HashSet<String> instancesTmp = new HashSet<String>();
//					instancesTmp.addAll(instances);
//					instances.clear();
//					instances.addAll(instancesTmp);
//					parentElement.setInstances(instances);
					parent.setIndirectInstancesSimple(instancesSimple);
					parent.setIndirectInstancesComplex(instancesComplex);
				}
				alreadyDone.addAll(parents);
				parents = schemaGraph.getParents(parents);
				if (parents!=null) parents.removeAll(alreadyDone);
			}
			schemaGraph = schemaGraph.getGraph(orgState);
			return schemaGraph;
		}
	
		public void getSourceStatistics(ArrayList<String> columns, ArrayList<ArrayList<String>> rows) {
			    String query = MySQL.SELECT +"A.source_id, A.name, COUNT(*) AS object_count " +
			    "FROM " + TABLE_SOURCE + " A, " + TABLE_OBJECT + " B " +
			    "WHERE A.source_id = B.source_id GROUP BY A.source_id order by A.source_id";
			    executeQuery(query, columns, rows);
		}
		

		  public void getSourceRelStatistics(ArrayList<String> columns, ArrayList<ArrayList<String>> rows) {
		    String query = MySQL.SELECT +"A.sourcerel_id, count(*) AS objectrel_count, " +
		        "count(DISTINCT object1_id) AS object1_count, count(DISTINCT object2_id) AS object2_count " +
		        "FROM " + TABLE_OBJECT_REL + " A GROUP BY A.sourcerel_id";
		    executeQuery(query, columns, rows);
		  }
		
		  public void executeQuery(String query, ArrayList<String> columns, ArrayList<ArrayList<String>> rows, boolean verbose) {
			    if (query==null || columns==null || rows==null) return;
			    try {
			      ResultSet rs = statement.executeQuery(query);
			      ResultSetMetaData rsmd = rs.getMetaData();
			      int colCount = rsmd.getColumnCount();
			      for (int i=1; i<=colCount; i++) {
			        String colName = rsmd.getColumnName(i);
			        columns.add(colName);
			      }
			      while (rs.next()) {
			        ArrayList<String> row = new ArrayList<String>();
			        for (int i=1; i<=colCount; i++) {
			          String val = rs.getString(i);
			          row.add(val);
			        }
			        rows.add(row);
			      }
			    }
			    catch (SQLException e) {
			      queryStatus = false;
			      queryMessage = e.getMessage();
			      if (verbose) System.out.println("executeQuery(): " + query + " with ERROR " + queryMessage);
			    }
			  }
		  public void executeQuery(String query, ArrayList<String> columns, ArrayList<ArrayList<String>> rows) {
			  executeQuery(query, columns, rows, true);
		  }
		
}
