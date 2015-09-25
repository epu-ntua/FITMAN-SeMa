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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import de.wdilab.coma.matching.execution.ExecWorkflow;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Edge;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Path;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This class lists all current sources and source relationships in the repository. 
 * It manages the loading of selected sources to graphs and source relationships to
 * match results. 
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class Manager {

	public static final String DIR_TMP = "D:/tmp/";
	

	
	DataAccess accessor = null;
	DataImport importer = null;
	HashMap<Integer, Graph> loadedGraphs;	
	HashMap<Integer, Source> allSources;
	HashMap<Integer, SourceRelationship> allSourceRels;
	ExecWorkflow exec = new ExecWorkflow();
	
	
	public Manager() {
	    accessor = new DataAccess();
	    if (accessor.getConnection()!=null){
	    	importer = new DataImport(accessor.getConnection());
	    }
		loadedGraphs = new HashMap<Integer, Graph>();
		allSources = new HashMap<Integer, Source>();
		allSourceRels = new HashMap<Integer, SourceRelationship>();		
	}
	
	public DataAccess getAccessor(){ return accessor; }
	public DataImport getImporter(){ return importer; }
	public ExecWorkflow getExecWorkflow(){ return exec; }
	public ExecWorkflow getExecWorkflow(boolean useSynAbb){
		if (useSynAbb){
			exec.loadSynAbb(accessor);
		} else {
			exec.removeSynAbb();
		}
		return exec; 
	}

	public void loadRepository(){
	    HashSet<Source> sources = accessor.getSources();
	    if (sources==null) return;
	    for (Source source : sources) {
	    	allSources.put(source.getId(), source);
		}
	    
	    HashSet<SourceRelationship> sourceRels = accessor.getSourceRels();
	    if (sourceRels==null) return;
	    for (SourceRelationship sourceRel : sourceRels) {
	    	allSourceRels.put(sourceRel.getId(), sourceRel);
		}
	}
	
	public void closeDatabaseConnection() {
		accessor.closeDatabaseConnection();
		importer.closeDatabaseConnection();
	}
	
	public Source getSource(int id) {
		if (allSources.isEmpty()){
			return null;
		}
		Source source = allSources.get(id);
		if (source==null){
			// maybe not loaded yet
			source = accessor.getSource(id);
			if (source!=null){
				allSources.put(source.getId(), source);
			}
		}
		return source;
	}
	
	  
	public SourceRelationship getSourceRel(int id) {
		if (allSourceRels.isEmpty()){
			return null;
		}
		SourceRelationship sourceRel = allSourceRels.get(id);
		if (sourceRel==null){
			// maybe not loaded yet
			sourceRel = accessor.getSourceRel(id);
			if (sourceRel!=null){
				allSourceRels.put(sourceRel.getId(), sourceRel);
			}
		}
		return sourceRel;
	}
	
	public String getSourceName(SourceRelationship sourceRel){
		if (sourceRel==null) return null;
		int id = sourceRel.getSourceId();
		Source source = getSource(id);
		if (source!=null){
			return source.getName();
		}
		return null;
	}
	
	public String getTargetName(SourceRelationship sourceRel){
		if (sourceRel==null) return null;
		int id = sourceRel.getTargetId();
		Source source = getSource(id);
		if (source!=null){
			return source.getName();
		}
		return null;
	}
	
	  //---------------------------------------------------------------------------//
	  // Retrieve/Search source, mappings and paths                                //
	  //---------------------------------------------------------------------------//
	  public ArrayList<Source> getAllSources() {
	      if (allSources!=null)
	          return new ArrayList<Source>(allSources.values());
	      return null;
	  }

	  public HashMap<Integer, Graph> getLoadedGraphs() {
	      return loadedGraphs;
	  }
	  
	
	  public ArrayList<SourceRelationship> getAllSourceRels() {
	      if (allSourceRels!=null)
	          return new ArrayList<SourceRelationship>(allSourceRels.values());
	      return null;
	  }
	  
		public ArrayList<Source> getSourcesWithNameAndUrl(String name, String url) {
			return accessor.getSourcesWithNameAndUrl(name, url);
		}
		
		public ArrayList<Source> getSourcesWithName(String name) {
			return accessor.getSourcesWithName(name);
		}

		public ArrayList<Integer> getSourceIdsWithNameAndUrl(String name, String url) {
			return accessor.getSourceIdsWithNameAndUrl(name, url);
		}
	
	//---------------------------------------------------------------------------//
	  
	public Graph loadGraph(int schemaId) {
		Source schema = accessor.getSource(schemaId);
		boolean loadForeignTypes = true;
		boolean preprocess = true;
		boolean loadInstances = true;
		return loadGraph(schema, loadForeignTypes, preprocess, loadInstances);
	}
		
		
	public Graph loadGraph(Source schema, boolean loadForeignTypes) {
		boolean preprocess = true;
		boolean loadInstances = true;
		return loadGraph(schema, loadForeignTypes, preprocess, loadInstances);
	}
	
	public Graph loadGraph(Source schema, boolean loadForeignTypes,
			boolean loadInstances) {
		boolean preprocess = true;
		return loadGraph(schema, loadForeignTypes, preprocess, loadInstances);
	}
	
	public Graph loadGraph(Source source, boolean loadForeignTypes, boolean preprocess, boolean loadInstances) {
		if (source==null) return null;
		Graph graph = loadedGraphs.get(source.getId());
		if (graph!=null){
			return graph;
		}
		GraphPreprocessing prep = new GraphPreprocessing(accessor, exec);
		graph = prep.loadGraph(source, loadForeignTypes, preprocess, loadInstances);
		if (graph!=null){
			loadedGraphs.put(source.getId(), graph);
		}
		return graph;
	}
	
	
	public void addGraph(Graph graph){
		if (!loadedGraphs.containsValue(graph)){
			loadedGraphs.put(graph.getSource().getId(), graph);
		}
	}
	
	public Graph getGraphWithProvider(String provider){
		if (provider==null){
			return null;
		}
		Collection<Graph> graphs = loadedGraphs.values();
		for (Graph graph : graphs) {
			if (provider.equals(graph.getSource().getProvider())) return graph;
		}		
		return null;
	}
	
	public void deleteGraphsFromMemory(MatchResult result){
		loadedGraphs.remove(result.getSourceGraph().getSource().getId());
		loadedGraphs.remove(result.getTargetGraph().getSource().getId());
	}
	
	public MatchResult loadMatchResult(int schemaId1,
			int schemaId2,String resultName) {
		int simRelId = accessor.getSourceRelIdWithName(schemaId1, schemaId2, resultName);
		
		Graph schemaGraph1 = loadGraph(schemaId1);
		Graph schemaGraph2 = loadGraph(schemaId2);
		SourceRelationship simRel = accessor.getSourceRel(simRelId);
		return loadMatchResult(schemaGraph1, schemaGraph2, simRel);
	}
	
	public MatchResult loadMatchResult(SourceRelationship simRel) {
		Graph schemaGraph1 = loadGraph(simRel.getSourceId());
		Graph schemaGraph2 = loadGraph(simRel.getTargetId());
		return loadMatchResult(schemaGraph1, schemaGraph2, simRel);
	}
	
	
	public MatchResult loadMatchResult(int schemaId1,
			int schemaId2, int simRelId) {
		Graph schemaGraph1 = loadGraph(schemaId1);
		Graph schemaGraph2 = loadGraph(schemaId2);
		SourceRelationship simRel = accessor.getSourceRel(simRelId);
		return loadMatchResult(schemaGraph1, schemaGraph2, simRel);
	}
	
	// Load match result from repository, expect GRAPH_STATE_SIMPLIFIED results
	// in repository
	public MatchResult loadMatchResult(Graph schemaGraph1,
			Graph schemaGraph2, SourceRelationship simRel) {

		if (simRel==null){
			return null;
		}
		int state = simRel.getPreprocessing();
		if (state<0){
			state=Graph.PREP_REDUCED;
		}
		Graph schemaGraph1Tmp = schemaGraph1.getGraph(state);
		Graph schemaGraph2Tmp = schemaGraph2.getGraph(state);
		
		if (state!=Graph.PREP_REDUCED && schemaGraph1Tmp==null || schemaGraph2Tmp==null){
			System.out.println("loadMatchResult(): Transformation to SIMPLIFIED failed, therefore use REDUCED");
			schemaGraph1Tmp = schemaGraph1.getGraph(Graph.PREP_REDUCED);
			schemaGraph2Tmp = schemaGraph2.getGraph(Graph.PREP_REDUCED);
		} 
		if (schemaGraph1Tmp==null || schemaGraph2Tmp==null) {
			return null;
		}
		schemaGraph1 = schemaGraph1Tmp;
		schemaGraph2 = schemaGraph2Tmp;		
		
		MatchResult matchResult = accessor.loadMatchResult(schemaGraph1, schemaGraph2, simRel);
//		System.out.println(matchResult.getMatchCount());
//		matchResult = matchResult.trim();
		matchResult.setGraphs(schemaGraph1, schemaGraph2);		
//		matchResult.setEvidence(simRel.getEvidence());
		matchResult.setMatchInfo(simRel.getComment());
//		String provider = simRel.getProvider();
//		MatcherConfig config = MatcherConfig.parseConfig(provider);
//		if (config != null) {
//			matchResult.setMatcherName(config.getName());
//			matchResult.setMatcherConfig(config);
//		} else
//			matchResult.setMatcherName(provider);
		return matchResult;
	}
	
	
	// Load match result from repository, expect GRAPH_STATE_SIMPLIFIED results
	// in repository
	public MatchResult loadMatchResult(Graph schemaGraph1,
			Graph schemaGraph2, int type, String resultName) {
		boolean verbose = false;
		Source source1 = schemaGraph1.getSource();
		Source source2 = schemaGraph2.getSource();
		if (source1 == null || source2 == null) {
			System.out
					.println("loadMatchResult(): No sources are set in input schema graphs");
			return null;
		}
		if (verbose)
			System.out.println("loadMatchResult(): " + source1 + "<->"
					+ source2 + ": " + resultName);
		int simRelId = accessor.getSourceRelId(source1.getId(), source2.getId(), type, resultName);
		if (simRelId == SourceRelationship.UNDEF) {
			System.out.println("loadMatchResult(): No sourcerel found for "
					+ source1 + "<->" + source2 + ": " + resultName);
			return null;
		}
		SourceRelationship simRel = accessor.getSourceRel(simRelId);
		return loadMatchResult(schemaGraph1, schemaGraph2, simRel);
	}

	
	public boolean existSourceWithName(String name) {
		return accessor.existSourceWithName(name);
	}
	

	public boolean saveMatchResult(MatchResult matchResult){
		return saveMatchResult(matchResult, SourceRelationship.REL_MATCHRESULT);
	}
	
	public boolean saveMatchResult(MatchResult matchResult, int reltype){
		if (matchResult == null)
			return false;
		boolean verbose = false;
		if (verbose)
			System.out.println("saveMatchResult(): Save mapping: "
					+ matchResult.getName());
		Graph sourceGraph = matchResult.getSourceGraph();
		Graph targetGraph = matchResult.getTargetGraph();
		if (Source.TYPE_ONTOLOGY!=sourceGraph.getSource().getType() 
				&& Source.TYPE_ONTOLOGY!=targetGraph.getSource().getType()){
			// Transform to Simplified state
			MatchResult tmpMatchResult = MatchResult.transformMatchResult(matchResult, Graph.PREP_SIMPLIFIED);
			if (tmpMatchResult == null ) {
				// failed because e.g. really large xml schemas do not have a simplified state
				System.out.println("saveMatchResult(): Transformation to SIMPLIFIED failed, therefore use REDUCED");
				matchResult = MatchResult.transformMatchResult(matchResult,	Graph.PREP_REDUCED);
			} else {
				matchResult = tmpMatchResult;
			}	
			if (matchResult == null) {
				System.out
						.println("saveMatchResult(): Transformation to REDUCED failed");
				return false;
			}
			sourceGraph = matchResult.getSourceGraph();
			targetGraph = matchResult.getTargetGraph();
		}
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
		if (source1Id == Source.UNDEF) {
			if (verbose)
				System.out.println(" - Save source schema " + source1);
			saveSchemaGraph(sourceGraph);
			source1 = sourceGraph.getSource();
			source1Id = source1.getId();
		}
		if (source2Id == Source.UNDEF) {
			if (verbose)
				System.out.println(" - Save target schema " + source2);
			saveSchemaGraph(targetGraph);
			source2 = targetGraph.getSource();
			source2Id = source2.getId();
		}
		String date = new Date().toString();
		String resultName = matchResult.getName();
//		String evidence = matchResult.getEvidence();
		String matchInfo = matchResult.getMatchInfo();
//		MatcherConfig matcherConfig = matchResult.getMatcherConfig();
		ArrayList aObjects = matchResult.getSrcObjects();
		ArrayList bObjects = matchResult.getTrgObjects();
		int mappingId = importer.getSourceRelIdWithName(source1Id, source2Id, resultName);
		if (mappingId == -1) {
			mappingId = importer.insertSourceRel(source1Id, source2Id,
					reltype, resultName, matchInfo, null,
					sourceGraph.getPreprocessing(), date);
		} else {
			System.out
					.println("saveMatchResult(): Error saving already existing match result "
							+ resultName + " with id " + mappingId);
			return false;
		}
		String type = null;
		String typespace = null;
		String comment = null;
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
						if (verbose)
							System.out.println(" - Insert association: "
									+ aElem.getId() + ":" + aElem + "<->"
									+ bElem.getId() + ":" + bElem + ":"
									+ sim);
						importer.insertObjectRel(mappingId, aElem.getId(),
								bElem.getId(), sim, null);
					} else if (aObj instanceof Path
							&& bObj instanceof Path) { // path match result
						String aAcc = ((Path)aObj).toIdString();
						String aName = ((Path)aObj).toNameString();
						String bAcc = ((Path)bObj).toIdString();
						String bName = ((Path)bObj).toNameString();

						int kind = Element.KIND_ELEMPATH;
						int aId = importer.insertObject(source1Id, aAcc, 
								aName, type, typespace, kind, comment, null);
						if (verbose)
							System.out.println(" - Insert AObj: " + aAcc + ":"
									+ aName + " => " + aId);
						int bId = importer.insertObject(source2Id, bAcc, bName,
								type, typespace, kind, comment,null);
						if (verbose)
							System.out.println(" - Insert BObj: " + bAcc + ":"
									+ bName + " => " + bId);
						importer.insertObjectRel(mappingId, aId, bId, sim, null);
						if (verbose)
							System.out.println(" + Insert association: " + aId
									+ "<->" + bId + ": " +sim);
					} else {
						if (verbose)
							System.out.println(" - Ignore incompatible objs "
									+ aObj + " and " + bObj);
					}
				}
			}
		}
		importer.updateSourceRel(mappingId, Repository.STATUS_IMPORT_DONE);
		return true;
	}
	
	// Save a schema graph under a new source id and new accessions for all
	// elements
	public boolean saveSchemaGraph(Graph schemaGraph) {
		if (schemaGraph == null)
			return false;
		boolean verbose = false;
		if (verbose)
			System.out.println("saveSchemaGraph(): Save schema graph "
					+ schemaGraph.getSource());
		
		Graph saveGraph =null;
		if (Source.TYPE_ONTOLOGY==schemaGraph.getSource().getType()){
			// Always save in Resolved representation - for ontologies
			saveGraph = schemaGraph.getGraph(Graph.PREP_DEFAULT_ONTOLOGY);
		} else {
			// Always save in Simplified representation - for xml schemas and relational
			saveGraph = schemaGraph.getGraph(Graph.PREP_DEFAULT_XML_REL);
			if (saveGraph==null){				
				System.out.println("saveSchemaGraph(): Transformation to SIMPLIFIED failed, therefore use REDUCED");
				saveGraph = schemaGraph.getGraph(Graph.PREP_REDUCED);
			}
		}

		// make a deep copy for generating new ids and accessions or changes
		// apply directly to input graph
		// saveGraph = saveGraph.copy();
		Source source = saveGraph.getSource();

		String sourceName = source.getName(); // Expect a unique ( name +
		// provider)
		String sourceProvider = source.getProvider();
		int sourceId = importer.getSourceId(sourceName, sourceProvider);
		if (sourceId != -1) {
			System.out
					.println("saveSchemaGraph(): Error saving already existing schema graph "
							+ sourceName
							+ " with provider "
							+ source.getProvider() + " with id " + sourceId);
			return false;
		}
		// Reset source description
		source.setId(Source.UNDEF); // require a new id
		source.setType(Source.TYPE_INTERN); // INTERN format
		source.setUrl(sourceName); // itself a namespace

		// Insert source
		sourceId = importer.insertSource(sourceName, Source.typeToString(source.getType()), 
				source.getUrl(), sourceProvider, source.getDate(), source.getAuthor(), 
				source.getDomain(), source.getVersion(), source.getComment());
		source.setId(sourceId);
		importer.updateSource(sourceId, Repository.STATUS_IMPORT_STARTED);
		// Insert structure mapping
		int mappingId = importer.getSourceRelId_ISA(sourceId, sourceId);
		if (mappingId == -1) {
			mappingId = importer.insertSourceRel(sourceId, sourceId,
					SourceRelationship.REL_IS_A, null,
					null, null, Graph.PREP_LOADED, null);
		} else {
			System.out
					.println("saveSchemaGraph(): Error saving already existing structure mapping "
							+ mappingId);
			return false;
		}
		if (verbose)
			System.out.println(" - Create source: " + sourceId + ", "
					+ sourceName + " with IS_A rel " + mappingId);

		// Save object information
		Iterator<Element> vertices = saveGraph.getElementIterator();
		int objCnt = 0;
		while (vertices.hasNext()) {
			objCnt++;
			Element elem = vertices.next();
//			String acc = String.valueOf(objCnt); // Important: new accession
			String acc = elem.getAccession(); // old accession
			String textRep = elem.getName();
			String type = elem.getType();
			String typespace = sourceName;
			int kind = Element.KIND_ELEMENT;
			String comment = elem.getComment();
			String synonym = elem.getSynonym();
			int id = importer.insertObject(sourceId, acc, textRep, type,
					typespace, kind, comment, synonym);
			// update element and vertex
			elem.setId(id);
			elem.setAccession(acc);
			elem.setKind(kind);
			if (verbose)
				System.out.println(" - Insert Obj: " + objCnt + " (" + acc
						+ ":" + textRep + ") => " + id);
		}
		// Save structure information
		float evidence = -1;
		Iterator edges = saveGraph.getEdgeSet().iterator();
		int edgeCnt = 0;
		while (edges.hasNext()) {
			Edge edge = (Edge) edges.next();
			Element aElem = edge.getSource();
			Element bElem = edge.getTarget();

			int aId = importer.getObjectId(sourceId, aElem.getAccession(), Element.KIND_ELEMENT);
			int bId = importer.getObjectId(sourceId, bElem.getAccession(), Element.KIND_ELEMENT);
			// Structure relationship: bElem IS_A aElem

			edgeCnt++;
			importer.insertObjectRel(mappingId, bId, aId, evidence, null);
			if (verbose)
				System.out.println(" - Insert Rel: " + edgeCnt + " (" + bId
						+ "->" + aId + ")");
		}
		
		importer.updateSource(sourceId, Repository.STATUS_IMPORT_DONE);
		return true;
	}
	
}
