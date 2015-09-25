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

package de.wdilab.coma.center.reuse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.tree.Tree;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.matching.Combination;
import de.wdilab.coma.matching.Constants;
import de.wdilab.coma.matching.validation.TreeToWorkflow;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This class determines different possible reuse paths that allow it to reuse existing mappings
 * to calculate one or several new mappings.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class MappingReuse {
	
	public static final int REUSEW = Constants.WORK_CNT + 50;
	public static final int REUSEW_DEFAULT = Constants.WORK_CNT + 51;	 
	
	public static final Integer[] REUSE = {
		REUSEW
//		, REUSEW_DEFAULT
	};
	public static final List<Integer> REUSE_LIST = Arrays.asList(REUSE);
	
	public final static boolean DEFAULT_EXACT = true, DEFAULT_PIVOT = false;
	public final static int DEFAULT_TOPK = 3, DEFAULT_MAXPATH = 2, DEFAULT_COMPOSITION = MatchResult.COMPOSITION_AVERAGE, DEFAULT_COMBINATION= Combination.COM_MAX;
	
	int id = -1;
	static boolean verbose = false;
	Manager manager = null;
	DataAccess accessor = null;
	ArrayList<Source> allSources =  null;
	boolean exact = true;
	int topKPaths=0, maxPathLen=0, combination = 0, composition = 0;
	int pivotSchema = Source.UNDEF;	
	boolean usePivot = false;
	String name = null;
	
	public MappingReuse(int id){
		switch (id) {
		case REUSEW:
			this.id= id;
			topKPaths=4;
			maxPathLen=2; 
			combination =Combination.COM_MAX;
			composition = MatchResult.COMPOSITION_AVERAGE;
			exact = true;
			usePivot = false;
			
			name = "ReuseW";
			break;

		default:
			this.id= REUSEW_DEFAULT;
			topKPaths=DEFAULT_TOPK;
			maxPathLen=DEFAULT_MAXPATH; 
			combination =DEFAULT_COMBINATION;
			composition =DEFAULT_COMPOSITION;
			exact = DEFAULT_EXACT;
			usePivot = DEFAULT_PIVOT;	
			
			name = "ReuseDefaultW";
			break;
		}
		
	}
	
	
	public MappingReuse(int topKPaths, int maxPathLen, boolean exact, 
			int composition, int combination, boolean usePivot){
		this.topKPaths = topKPaths;
		this.exact = exact;
		this.maxPathLen = maxPathLen;
		this.usePivot = usePivot;
		this.composition = composition;
		this.combination = combination;
	}
	
	public MappingReuse(Manager manager, String config){
		if (config.contains(",")){
			System.err.println("MappingReuse given config not valid " + config);
			return;
		}
		config = config.replace("(", "").replace(")", "").replace(" ", "");
		String[] parts = config.split(",");
		if (parts.length!=6){
			System.err.println("MappingReuse given config not valid " + config);
			return;
		}		
		topKPaths = Integer.valueOf(parts[0]);
		maxPathLen = Integer.valueOf(parts[1]);
		exact = Boolean.valueOf(parts[2]);
		composition = Integer.valueOf(parts[3]);
		combination = Integer.valueOf(parts[4]);
		usePivot = Boolean.valueOf(parts[5]);
		// TODO select pivot separately
	}
	
	public int getTopKPaths(){ return topKPaths; }
	public int getMaxPathLength(){ return maxPathLen; }
	public boolean getExact(){ return exact; }
	public int getComposition(){ return composition; }
	public int getCombination(){ return combination; }
	public boolean usePivot(){ return usePivot; }
	public String getName() { return name; }
	
	public static void insertDefaults(DataImport importer){
		for (int i = 0; i < REUSE.length; i++) {
			MappingReuse r = new MappingReuse(REUSE[i]);
			importer.insertWorkflowVariable("$" + r.getName(), r.toString());
		}
	}
	
	public MatchResult executeReuse(Manager manager, Graph sourceGraph, Graph targetGraph) {
		// the source or target ID can be also determined with the help of the match objects
		// because every Element has a source_id
		this.manager = manager;
		this.accessor = manager.getAccessor();
		
		Source source = sourceGraph.getSource();
		Source target = targetGraph.getSource();
//		MatcherConfig matcherConfig = _manager.getMatcherConfig(_matcher);
		int sourceId = source.getId();

//		float[][] simMatrix=null;
//		ArrayList relPaths = null;
		// TODO: fix bug -> doesn't work for not Feedback
//		if (nodematcher==MatcherLibrary.MATCH_SYS_FEEDBACK){
//			ArrayList<Float> pathSimsComplete = new ArrayList<Float>();
//			ArrayList<Float> pathSimsIncomplete = new ArrayList<Float>();
//			ArrayList relPathsComplete = calculateCompleteMappingPaths(manager,
//					source, target, vertexPaths, srValues, topK, pathSimsComplete,
//					pivotschema, exact, length);
//			ArrayList relPathsIncomplete = calculateIncompleteMappingPaths(
//					manager, source, target, vertexPaths, srValues, topK,
//					pathSimsIncomplete, pivotschema, exact, length);
//			relPaths = getTopKMappingPaths(topK, relPathsComplete,
//					relPathsIncomplete, pathSimsComplete, pathSimsIncomplete);
//		} else {
//			ArrayList<Float> pathSims = new ArrayList<Float>();
//			relPaths = calculateMappingPaths(manager,
//					source, target, vertexPaths, srValues, topK, pathSims,
//					pivotschema, exact, length);
//		}
			
		ArrayList<GraphPath<Integer, DefaultEdge>> vertexPaths  = new ArrayList<GraphPath<Integer,DefaultEdge>>();
		HashMap<Object, Float> srValues  = new HashMap<Object, Float>();
		ArrayList<Float> pathSims = new ArrayList<Float>();
		ArrayList relPaths =  calculateCompleteMappingPaths(manager, source, target, vertexPaths,
				srValues, pathSims);
			
		if (relPaths == null) {
			return null;
		}

		int size = relPaths.size();

		MatchResult[] results = new MatchResult[size];
		for (int i = 0; i < size; i++) {
			ArrayList mappingPath = (ArrayList) relPaths.get(i);
//			System.out.println(mappingPath);
			if (mappingPath.size() == 1) {
				// load selected matchresult (length 1 = one direct matchresult)
				results[i] = manager.loadMatchResult((SourceRelationship) mappingPath.get(0));
			} else {
				// calculate mapping path
				results[i] = calculateMappingPath(manager, mappingPath, sourceId);
			}
			if (!results[i].getSourceGraph().equals(sourceGraph)){
				// assumption: transpose
				results[i] = MatchResult.transpose(results[i]);
			}
		}
		Combination c = new Combination(combination);
		MatchResult  result = c.combine(results);
		return result;
	}

	
	static ArrayList<GraphPath<Integer, DefaultEdge>> findPathsWithExactLength(SimpleGraph<Integer, DefaultEdge> sourceRelGraph, 
			Integer source, Integer target, int minLength, int maxLength){
		// number of ranking paths between the start vertex and an end vertex. -> set really high to get all
		int nPaths = 50;
		// length =  nMaxHops maximum number of edges of the calculated paths
		KShortestPaths<Integer, DefaultEdge> k = new KShortestPaths<Integer, DefaultEdge>(sourceRelGraph, source, nPaths, maxLength);
		List<GraphPath<Integer, DefaultEdge>> paths = k.getPaths(target);
		if (paths==null || paths.isEmpty()) return null;
		if (paths.equals(nPaths)){
			nPaths=500;
			k = new KShortestPaths<Integer, DefaultEdge>(sourceRelGraph, source, nPaths, maxLength);
			paths = k.getPaths(target);
		}
		ArrayList<GraphPath<Integer, DefaultEdge>> result = new ArrayList<GraphPath<Integer,DefaultEdge>>();
		for (int i = 0; i < paths.size(); i++) {
			GraphPath<Integer, DefaultEdge> path = paths.get(i);
			int pathLength = path.getEdgeList().size();
			if (pathLength>=minLength && pathLength<=maxLength){
				result.add(path);
			}
		}
		if (result.isEmpty()) return null;
		return result;
	}
	
	
	public ArrayList calculateCompleteMappingPaths(Manager manager,
			Source source, Source target, ArrayList<GraphPath<Integer, DefaultEdge>> vertexPaths, 
			HashMap<Object, Float> srValues, ArrayList<Float> pathSims) {
		this.manager = manager;
		this.accessor = manager.getAccessor();
		allSources = manager.getAllSources();
		int minPathLen = 1;
		if (exact) {
			minPathLen = maxPathLen;
		}
		// Determine source Paths with len
		// Complete Paths
		SimpleGraph<Integer, DefaultEdge> sourceRelGraph = buildSourceGraph();
		
		ArrayList paths = findPathsWithExactLength(sourceRelGraph, source.getId(), target.getId(), minPathLen, maxPathLen);
		if (paths != null) {
			vertexPaths.addAll(paths);
		}
		if (pivotSchema != Source.UNDEF) {
			ArrayList<GraphPath<Integer, DefaultEdge>> vertexPathsPivot = new ArrayList<GraphPath<Integer,DefaultEdge>>();;
			for (GraphPath<Integer, DefaultEdge> graphPathImpl : vertexPaths) {
				if (Graphs.getPathVertexList(graphPathImpl).contains(pivotSchema)){
					vertexPathsPivot.add(graphPathImpl);
				}
			}
			if (vertexPathsPivot.isEmpty()) return null;
			vertexPaths = vertexPathsPivot;
		}
		ArrayList sourcePaths = identifySourcePaths(source, target, vertexPaths);
		if (sourcePaths == null) {
			System.out
					.println("computeConstReuseSimMatrix(): No Source Paths found");
		} else if (verbose) {
			System.out.println("All Source Paths: " + sourcePaths.size());
			for (int i = 0; i < sourcePaths.size(); i++)
				System.out.println(sourcePaths.get(i));
		}
		// Filter source paths with pivot
		ArrayList<Object> filteredSourcePaths = new ArrayList<Object>();
//		if (pivot != null) {
//			for (int i = 0; i < sourcePaths.size(); i++) {
//				ArrayList sourcePath = (ArrayList) sourcePaths.get(i);
//				if (sourcePath.contains(pivot))
//					filteredSourcePaths.add(sourcePath);
//			}
//		} else {
			filteredSourcePaths = sourcePaths;
//		}
		if (filteredSourcePaths == null || filteredSourcePaths.isEmpty()) {
			System.out
					.println("computeConstReuseSimMatrix(): No Filtered Source Paths");
			return null;
		}	
		if (verbose) {
			System.out.println("Filtered Source Paths: "
					+ filteredSourcePaths.size());
			for (int i = 0; i < filteredSourcePaths.size(); i++)
				System.out.println(filteredSourcePaths.get(i));
		}
		// Determine rel paths from source paths
		ArrayList relPaths = buildRelPathsFromSourcePaths(filteredSourcePaths);
		if (relPaths == null) {
			System.out.println("computeConstReuseSimMatrix(): No Rel Paths");
			return null;
		}
		if (verbose) {
			System.out.println("All Rel Paths: " + relPaths.size());
			for (int i = 0; i < relPaths.size(); i++)
				System.out.println(relPaths.get(i));
		}
		computeMappingStatistics();
		// Determine previousMappings for the rel path
		for (int i = 0; i < relPaths.size(); i++) {
			ArrayList relPath = (ArrayList) relPaths.get(i);
			// Determine previousMappings for the rel path
			float sim = 0;
			for (int j = 0; j < relPath.size(); j++) {
				SourceRelationship rel = (SourceRelationship) relPath.get(j);
				if (srValues.containsKey(rel)) {
					Float value = srValues.get(rel);
					sim += value.floatValue();
				} else {
					float value = getSimSourceRelationship(rel);
					sim += value;
					srValues.put(rel, new Float(value));
				}
			}
			sim = sim / relPath.size();
			sim =(float) Math.round (sim*100)/100;
			Float value = new Float(sim);
			pathSims.add(value);
			srValues.put(relPath, value);
		}
		if (verbose) {
			for (int i = 0; i < relPaths.size(); i++) {
				System.out.println("Unsorted: " + pathSims.get(i) + ": "
						+ relPaths.get(i));
			}
		}
		return getTopKMappingPaths(topKPaths, relPaths, pathSims);
	}
	
	
	
	  //Build sourceGraph with only inter-source relationships
	  public SimpleGraph<Integer, DefaultEdge> buildSourceGraph() {
//		  SimpleWeightedGraph<Source, DefaultWeightedEdge> sourceRelGraph = new SimpleWeightedGraph<Source, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		  SimpleGraph<Integer, DefaultEdge> sourceRelGraph = new SimpleWeightedGraph<Integer, DefaultEdge>(DefaultEdge.class);
//	    pathFinder = new GMPathFinder(sourceRelGraph);
	    
//	    if (allSources==null) return null;
////	    double weight = 1.0;
//	    for (Source source1 : allSources) {
//	    	for (Source source2 : allSources) {
//	        //look up directly in sourceRelMatrix for source structure, i.e. source1=source2
//	        //to avoid cycle in graph, do not consider intra-source relationships
//	        if (source1.equals(source2)) continue;
//
//	        //System.out.println("Examining source pair (" + i + "," + j + "): " + source1 + ", " + source2);
//	        HashSet<Integer> sourceRels = accessor.getSourceRelIds(source1.getId(), source2.getId(), SourceRelationship.REL_MATCHRESULT);
//	        if (sourceRels!=null) {
//	        	// thus there exists at least one mapping between them...
//	          //System.out.println("==>Adding relationships between " + source1 + " and " + source2);
//	        	if (!sourceRelGraph.containsEdge(source1, source2)){
//		        	 sourceRelGraph.addVertex(source1);
//		        	 sourceRelGraph.addVertex(source2);
//		        	 sourceRelGraph.addEdge(source1, source2);
//	        	}
//	          }
//	      }
//	    }
		 HashMap<Integer, HashSet<Integer>> relIds = accessor.getSourceRelSourceIds(SourceRelationship.REL_MATCHRESULT);
		  for (Integer srcId : relIds.keySet()) {
			  HashSet<Integer> trgIds = relIds.get(srcId);
			  for (Integer trgId : trgIds) {
		        	if (!sourceRelGraph.containsEdge(srcId, trgId)){
		        	 sourceRelGraph.addVertex(srcId);
		        	 sourceRelGraph.addVertex(trgId);
		        	 sourceRelGraph.addEdge(srcId, trgId);
	        	}
			}
		}
		  
	    if (verbose) System.out.println("Build sourceGraph: " + allSources.size() + " sources");
	    if (sourceRelGraph.vertexSet().isEmpty() || sourceRelGraph.edgeSet().isEmpty()) return null;
	    return sourceRelGraph;
	  }
	
	  //Only retain sourcePaths with different substitutedSourcePath or different path types
	  //sourcePaths = paths identified using methods from PathFinder, i.e. paths of vertices
	  public ArrayList<List<Integer>> identifySourcePaths(Source source, Source target, ArrayList<GraphPath<Integer, DefaultEdge>> vertexPaths) {
	    if (vertexPaths==null || vertexPaths.isEmpty()) return null;
	    ArrayList<List<Integer>> basicSourcePaths = new ArrayList<List<Integer>>();
	    ArrayList<ArrayList<Integer>> substitutedSourcePaths = new ArrayList<ArrayList<Integer>>();
	    //for each substituted source path, store all the types of the rel paths
	    ArrayList<ArrayList<ArrayList<String>>> relPathTypeLists = new ArrayList<ArrayList<ArrayList<String>>>();
	    for (int i=0; i<vertexPaths.size(); i++) {
//	      ArrayList<Source> sourcePath = GMGraphUtil.simplePathToObjectList(vertexPaths.get(i));
	    	List<Integer> sourcePath = Graphs.getPathVertexList(vertexPaths.get(i));
	      ArrayList<Integer> substitutedSourcePath = substituteSourcePath(sourcePath);
	      int ind = substitutedSourcePaths.indexOf(substitutedSourcePath);
	      if (ind!=-1) {
	        //source path belongs to an already encountered substituted source path
	        //only add if source path generates new rel path types
	        boolean newPath = false;
	        ArrayList<ArrayList<String>> relPathTypes = relPathTypeLists.get(ind);
	        ArrayList<ArrayList<SourceRelationship>> relPaths = buildRelPathsFromSourcePath(sourcePath);
	        for (int k=0; k<relPaths.size(); k++) {
	          ArrayList<SourceRelationship> relPath = relPaths.get(k);
	          ArrayList<String> relPathType = getRelPathType(relPath);
	          if (! relPathTypes.contains(relPathType)) {
	            //remember new path type
	            newPath = true;
	            relPathTypes.add(relPathType);
	          }
	        }
	        if (newPath) {
	          basicSourcePaths.add(sourcePath);
	          //System.out.println("NEW REL PATH TYPE");
	          //System.out.println(" - SourcePath : " + sourcePath);
	          //System.out.println(" - Substituted: " + substitutedSourcePath);
	          //System.out.println(" - Reltypes:    " + relPathTypes);
	        }
	      }
	      else {
	        //if a new substituted source path is accountered
	        //remember paths
	        basicSourcePaths.add(sourcePath);
	        substitutedSourcePaths.add(substitutedSourcePath);
	        //remember new path types
	        ArrayList<ArrayList<SourceRelationship>> relPaths = buildRelPathsFromSourcePath(sourcePath);
	        if (relPaths!=null){
		        ArrayList<ArrayList<String>> relPathTypes = new ArrayList<ArrayList<String>>();
		        for (int k=0; k<relPaths.size(); k++) {
		          ArrayList<SourceRelationship> relPath = relPaths.get(k);
		          ArrayList<String> relPathType = getRelPathType(relPath);
		          relPathTypes.add(relPathType);
		        }
		        relPathTypeLists.add(relPathTypes);
		        //System.out.println("NEW SUBSTITUTED PATH");
		        //System.out.println(" - SourcePath : " + sourcePath);
		        //System.out.println(" - Substituted: " + substitutedSourcePath);
		        //System.out.println(" - Reltypes:    " + relPathTypes);
	        }
	      }
	    }
	    if (basicSourcePaths.isEmpty()) return null;
	    return basicSourcePaths;
	  }
	
	  //Substitute sources in a sourcePath with their parent sources
	  public ArrayList<Integer> substituteSourcePath(List<Integer> sourcePath) {
		    if (sourcePath==null) return null;
		    ArrayList<Integer> substitutedSourcePath = new ArrayList<Integer>();
		    Integer source = sourcePath.get(0);
		    substitutedSourcePath.add(source);
		    for (int i=1; i<sourcePath.size()-1; i++) {
		    	Integer currentSource = sourcePath.get(i);
		    	Integer parentSource = accessor.getSuperSource(currentSource);
		    	if (parentSource!=null && parentSource!=Source.UNDEF) substitutedSourcePath.add(parentSource);
		    	else substitutedSourcePath.add(currentSource);
		    }
		    Integer target = sourcePath.get(sourcePath.size()-1);
		    substitutedSourcePath.add(target);
		    if (substitutedSourcePath.isEmpty()) return null;
		    return substitutedSourcePath;
	  }
	  
	  //Generate all possible sourceRelPaths from a sourcePath
	  // - sourcePath is ArrayList of Sources found by shortest path in sourceRelGraph
	  // - sourceRelPaths is ArrayList of ArrayLists of SourceRelationships
	  public ArrayList<ArrayList<SourceRelationship>> buildRelPathsFromSourcePath(List<Integer> sourcePath) {
	    //System.out.println(" -Processing Source Path: " + sourcePath);
	    if (sourcePath==null) return null;
	    Iterator<Integer> iterator = sourcePath.iterator();
	    Integer source1=null, source2=null;
	    ArrayList<ArrayList<SourceRelationship>> relPaths = new ArrayList<ArrayList<SourceRelationship>>();
	    while (iterator.hasNext()) {
	      if (source1==null) {
	        source1 = iterator.next();
	        continue;
	      }
	      source2 = iterator.next();
	      HashSet<Integer> directRels = accessor.getSourceRelIds(source1, source2);
	      //System.out.println("----DirectRels for " + source1 + " and " + source2 + " => " + directRels);
	      if (directRels==null) {
	        //System.out.println("buildRelPathsFromSourcePath(): No rel path found between " + source1 + " and " + source2);
	        return null;
	      }

	      if (relPaths.isEmpty()) {
	    	  for (Integer directRel : directRels) {
	    		  ArrayList<SourceRelationship> relPath = new ArrayList<SourceRelationship>();
	    		  relPath.add(manager.getSourceRel(directRel));
	    		  relPaths.add(relPath);
	        }
	      }
	      else {
	        ArrayList<ArrayList<SourceRelationship>> newRelPaths = new ArrayList<ArrayList<SourceRelationship>>(relPaths.size()*directRels.size());
	        for (int i=0; i<relPaths.size(); i++) {
	          ArrayList<SourceRelationship> relPath = relPaths.get(i);
	          for (Integer directRel : directRels) {
	            ArrayList<SourceRelationship> newRelPath = new ArrayList<SourceRelationship>(relPath);
	            newRelPath.add(manager.getSourceRel(directRel));
	            newRelPaths.add(newRelPath);
	          }
	        }
	        relPaths = newRelPaths;
	      }

	      source1=source2;
	    }
	    if (relPaths.isEmpty()) return null;
	    return relPaths;
	  }
	  
	  
	  public ArrayList<String> getRelPathType(ArrayList<SourceRelationship> relPath) {
		    if (relPath == null || relPath.isEmpty()) return null;
		    ArrayList<String> type = new ArrayList<String>();
		    for (int i=0; i<relPath.size(); i++) {
		      SourceRelationship sourceRel = relPath.get(i);
		      type.add(SourceRelationship.typeToString(sourceRel.getType()));
		    }
		    if (type.isEmpty()) return null;
		    return type;
	  }
	  
	  //build RelPaths for multiple sourcePaths
	  public ArrayList<ArrayList> buildRelPathsFromSourcePaths(ArrayList sourcePaths) {
	    if (sourcePaths==null || sourcePaths.isEmpty()) return null;
	    ArrayList<ArrayList> allRelPaths = new ArrayList<ArrayList>();
	    for (int i=0; i<sourcePaths.size(); i++) {
	      ArrayList<Integer> sourcePath = (ArrayList<Integer>)sourcePaths.get(i);
	      ArrayList<ArrayList<SourceRelationship>> relPaths = buildRelPathsFromSourcePath(sourcePath);
	      if (relPaths!=null) allRelPaths.addAll(relPaths);
	    }
	    if (allRelPaths.isEmpty()) return null;
	    return allRelPaths;
	  }

	   public void computeMappingStatistics() {
//		    if (statisticsComputed) return;

		    ArrayList<String> columns = new ArrayList<String>();
		    ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
		    accessor.getSourceStatistics(columns, rows);
		    for (int i=0; i<rows.size(); i++) {
		      ArrayList row = rows.get(i);
		      int id = Integer.parseInt((String)row.get(0));
//		      String name = (String)row.get(1);
		      int objCount = Integer.parseInt((String)row.get(2));
		      Source source =  manager.getSource(id);
		      source.setObjectCount(objCount);
		    }

		    columns.clear();
		    rows.clear();
		    accessor.getSourceRelStatistics(columns, rows);

		    for (int i=0; i<rows.size(); i++) {
		      ArrayList row = rows.get(i);
		      int id = Integer.parseInt((String)row.get(0));
		      int objRelCount = Integer.parseInt((String)row.get(1));
		      int obj1Count = Integer.parseInt((String)row.get(2));
		      int obj2Count = Integer.parseInt((String)row.get(3));
		      if (id>-1){
		      	SourceRelationship sourceRel = manager.getSourceRel(id);
		      	if (sourceRel!=null){
			      	sourceRel.setObjectRelCount(objRelCount);
			//      Source source1 = getSource(sourceRel.getSource1Id());
			//      Source source2 = getSource(sourceRel.getSource2Id());
			      	sourceRel.setObject1Count(obj1Count);
			      	sourceRel.setObject2Count(obj2Count);
		      	}
		      } else{
		    	  // no object relations -> empty match result
		      }
		    }
//		    statisticsComputed = true;
		  }

	   
		  float getSimSourceRelationship(SourceRelationship _rel){
//				SchemaGraph sourceGraph = _manager.loadSchemaGraph(
//						_manager.getSource(rel.getSource1Id()), true, false);
//				SchemaGraph targetGraph = _manager.loadSchemaGraph(
//						_manager.getSource(rel.getSource2Id()), true, false);
//				MatchResult mapping = _manager.loadMatchResult(
//						sourceGraph, targetGraph, rel.getType());
//				float value = CombinationLibrary.computeSetSimilarity(
//						mapping.getSimMatrix(),	CombinationLibrary.COM_SET_AVERAGE);
			  
			  //calculate similarity depending on correspondence count					
			  	float value = _rel.getObjectRelCount();
			  	int object1Count = manager.getSource(_rel.getSourceId()).getObjectCount();
			  	int object2Count = manager.getSource(_rel.getTargetId()).getObjectCount();
			  	// Dice
//				value = (value/object1Count + value/object2Count)/2;
			  	// Min
			  	value = value/Math.min(object1Count,object2Count);		
//				float value = (float) 0.5;
				return value;
		  }
	   
			public static ArrayList<Object> getTopKMappingPaths(int topKPaths,
					ArrayList<Object> relPaths, ArrayList<Float> pathSims) {
				// True=>asceding, lowest sim first, false=>descending, highest sim
				// first
				sort(relPaths, pathSims, false);
				if (verbose) {
					for (int i = 0; i < relPaths.size(); i++) {
						if (verbose)
							System.out.println("Sorted: " + pathSims.get(i) + ": "
									+ relPaths.get(i));
					}
				}
				// Take only topKPaths from the sorted list
				if (topKPaths > 0 && topKPaths < relPaths.size()) {
					ArrayList<Object> relPathsHelp = new ArrayList<Object>(relPaths.subList(0,
							topKPaths));
					relPaths.clear();
					relPaths.addAll(relPathsHelp);
					if (verbose) {
						System.out.println("Top Mapping Paths: " + relPaths.size());
						for (int i = 0; i < relPaths.size(); i++)
							System.out.println(relPaths.get(i));
					}
				}
				return relPaths;
			}
			
		    // Sort a list descending to a score list (of float)
		    public static void sort(List<Object> objects, List<Float> scores, boolean asc) {
		        if (objects == null || scores == null)
		            return;
		        for (int i = 0; i < objects.size(); i++) {
		            Object object1 = objects.get(i);
		            Float score1 = scores.get(i);
		            for (int j = i + 1; j < objects.size(); j++) {
		                Object object2 = objects.get(j);
		                Float score2 = scores.get(j);
		                boolean swap = false;
		                if (asc)
		                    swap = (score1.compareTo(score2) > 0);
		                else
		                    swap = (score1.compareTo(score2) < 0);
		                if (swap) {
		                    Object tmpObj = object1;
		                    Float tmpSco = score1;
		                    object1 = object2;
		                    score1 = score2;
		                    object2 = tmpObj;
		                    score2 = tmpSco;
		                    objects.set(i, object1);
		                    scores.set(i, score1);
		                    objects.set(j, object2);
		                    scores.set(j, score2);
		                }
		            }
		        }
		    }

			/*
			 * given two mappings S-A/A-S compose with A-T/T-A
			 */
			public MatchResult calculateMappingPath(Manager manager, ArrayList _mappingPath, int _sourceId) {
				if (_mappingPath == null) {
					return null;
				}
				this.manager = manager;
				String matchinfo = "";
				int sourceId = _sourceId;
//				String sourceName = _sourceName;
				ArrayList<MatchResult> path = new ArrayList<MatchResult>();
				for (int i = 0; i < _mappingPath.size(); i++) {
					Object current = _mappingPath.get(i);
					MatchResult result = null;
					if (current instanceof SourceRelationship) {
						result = manager.loadMatchResult((SourceRelationship)current);
//					} else if (current instanceof ArrayList) {
//					//TODO	result = getMatchResult(manager, (ArrayList) current, _nodeMatcher, _contextMatcher);
					} else if (current instanceof MatchResult) {
						result = (MatchResult) current;
					}
					if (result != null) {
//						System.out.println("MatchCount: " + result.getMatchCount());
						if (current instanceof SourceRelationship) {
							if (_sourceId==((SourceRelationship) current)
										.getTargetId()) {
							// AS => we have to transpose to SA
							String name = result.getName();
							result = MatchResult.transpose(result);
							result.setName(name);
							}
							_sourceId = result.getTargetGraph().getSource().getId();
						}
//						sourceName = result.getTargetGraph().getSource().getName();
						path.add(result);
						if (i < (_mappingPath.size() - 1)) {
							matchinfo += result.getName() +  "|";
						} else {
							matchinfo += result.getName();
						}
					} else {
						return null;
					}
				}
				_sourceId=sourceId;
				MatchResult newResult = composeMappings(_sourceId, path, composition);
				if (newResult == null || newResult.getMatchCount() < 1) {
					return null;
				}
				newResult.setMatchInfo(matchinfo);
				if (_mappingPath.size() == 2) {
					MatchResult[] mrs = new MatchResult[2];
					for (int i = 0; i < path.size(); i++) {
						mrs[i] = path.get(i);
					}
					newResult.setUserObject(mrs);
				}
				return newResult;
			}		    
		    
	
			static MatchResult composeMappings( int _sourceId, ArrayList _mappingPath, int _aggregation) {
				// Compose
				MatchResult mapping_1 = (MatchResult) _mappingPath.get(0);
				Graph sourceModel = mapping_1.getSourceGraph();
				int state = sourceModel.getPreprocessing();
//				mapping_1 = MatchResult.transformMatchResult(mapping_1,
//						state);
				if (_sourceId==mapping_1.getTargetGraph().getSource().getId()){
					mapping_1 = MatchResult.transpose(mapping_1);
				}
				for (int k = 1; k < _mappingPath.size(); k++) {
					MatchResult mapping_2 = (MatchResult) _mappingPath.get(k);
					mapping_2 = MatchResult.transformMatchResult(mapping_2,
							state);
//					mapping_2 = ReuseMatcher.recreateMatchResult(mapping_2, mapping_1.getTargetGraph(),
//							null);
					if (mapping_1.getTargetGraph().getSource().getId()==mapping_2.getTargetGraph().getSource().getId()){
						mapping_2 = MatchResult.transpose(mapping_2);
					} 

					if (_aggregation==MatchResult.COMPOSITION_AVERAGE){
						mapping_1 = MatchResult.compose(mapping_1, mapping_2, MatchResult.COMPOSITION_SUM);	
					} else {
						mapping_1 = MatchResult.compose(mapping_1, mapping_2, _aggregation);
					}
//					ArrayList middlePaths = mapping_1.getTrgObjects();
////					mapping_1 = mapping_1.restrict(null, middlePaths);
//					mapping_2 = mapping_2.restrict(middlePaths, null);
//					//			SchemaGraph targetModel_1 = mapping_1.getTargetGraph();
//					float[][] simMatrix_1 = mapping_1.getSimMatrix();
					Graph targetModel_2 = mapping_2.getTargetGraph();
//					float[][] simMatrix_2 = mapping_2.getSimMatrix();
//					// Determine current source, intermediate model and target model
					Graph currSrcModel = sourceModel;
					Graph currTarModel = targetModel_2;
//					ArrayList currSrcObjects = mapping_1.getSrcObjects();
//					ArrayList currTarObjects = mapping_2.getTrgObjects();
//					int x1 = simMatrix_1.length;
//					int y1 = simMatrix_1[0].length;
//					int x2 = simMatrix_2.length;
//					int y2 = simMatrix_2[0].length;
//					System.out.println("simMatrix_1: " + x1 + " x " + y1);
//					System.out.println("simMatrix_2: " + x2 + " x " + y2);
//					float[][] compSimMatrix = null;
////					if (x1 > Graph.CHOP_SIZE  || x2 >  Graph.CHOP_SIZE || y2 >  Graph.CHOP_SIZE){
////					// TODO	compSimMatrix = composeChopped(simMatrix_1,
////								simMatrix_2, _aggregation);
////					} else {
//						compSimMatrix = Combination.compose(simMatrix_1, simMatrix_2, _aggregation);
//						System.out.println("compSimMatrix: " + compSimMatrix.length + " x " + compSimMatrix[0].length );
////					}
//					mapping_1 = new MatchResult(currSrcObjects, currTarObjects,
//							compSimMatrix);
					mapping_1.setGraphs(currSrcModel, currTarModel);
				}
				if (_aggregation==MatchResult.COMPOSITION_AVERAGE){
					// calculating avg value
					int divide = _mappingPath.size();					
					mapping_1.divideBy(divide);
				}
				return mapping_1;
			}
	   
			// Name	(int topk, int maxPathLen, boolean exact, int compose, int combination, boolean pivot)			
			public String toString(){
				String text = 
					"("+ 
				topKPaths+ "," + maxPathLen + "," + exact + "," +
				MatchResult.compositionToString(composition) + "," + Combination.combinationToString(combination)
				+ "," +usePivot
				 +  ")"
				;
				return text;
			}
			
			
		    public static MappingReuse buildWorkflow( Tree tree) {
		    	// Creating the tree using recursion:
		    	int cnt = tree.getChildCount();
		    	if( cnt > 0) {   // We have at least one child, otherwise atomic level
		    		 // Iterate the children
		    		for( int j=0; j < cnt; j++) {   
			    		Tree subtree = tree.getChild(j);   // The subtree of the j-th child
			    		String name = tree.getChild(j).toString();   // The child's name
			    		// Print the name, but only if it is not a comma, brace etc. (we don't need those).	    		
			    		if( !name.equals( ";") && !name.equals( "(") && !name.equals( ")") && !name.equals( ",")) {
			    			System.out.println( name);  
			    		} 
			    		// After printing the node name call this method again to display the children.
			    		if (name.equals("reuse")){
			    			MappingReuse reuse = getWorkflow(subtree);
			    			System.out.println(reuse.toString());
			    			return reuse;
			    		} else{
			    			MappingReuse reuse =  buildWorkflow( subtree);
			    			if (reuse!=null) return reuse;
			    		}
		    		}
		    	}
		    	return null;
		    }
			
		    
		    static MappingReuse getWorkflow(Tree tree){
		    	TreeToWorkflow.removeNotNeededChildren(tree); // remove (;,)
		    	int cnt = tree.getChildCount();
		    	if( cnt > 0) {   // We have at least one child, otherwise atomic level
		    		
//		    		int topKPaths, boolean exact, int maxPathLen, int pivotSchema, int composition, int combination
		    		int topKPaths;
					int maxPathLen;
					boolean exact;
					int composition;
					int combination;
					boolean pivotSchema;
					try {
						topKPaths = Integer.valueOf(tree.getChild(0).toString());
						maxPathLen = Integer.valueOf(tree.getChild(1).toString());
						exact = Boolean.valueOf(tree.getChild(2).toString());
						composition = MatchResult.stringToComposition(tree.getChild(3).toString());
						combination = Combination.stringToCombination(tree.getChild(4).toString());
						pivotSchema = Boolean.valueOf(tree.getChild(5).toString());
			    		MappingReuse reuse = new MappingReuse(topKPaths, maxPathLen, 
			    				exact, composition, combination, pivotSchema);
			    		return reuse;
					} catch (NumberFormatException e) {
						System.out.println("MappingReuse.getWorkflow() error " + e.getMessage());
					}
		    	}
		    	return null;
		    }
			
}
