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
import java.util.Iterator;

import de.wdilab.coma.matching.Combination;
import de.wdilab.coma.matching.ComplexMatcher;
import de.wdilab.coma.matching.Strategy;
import de.wdilab.coma.matching.Matcher;
import de.wdilab.coma.matching.Resolution;
import de.wdilab.coma.matching.Selection;
import de.wdilab.coma.matching.Workflow;
import de.wdilab.coma.matching.execution.ExecWorkflow;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.structure.Edge;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Path;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;
import de.wdilab.coma.structure.graph.DirectedGraphImpl;
import de.wdilab.coma.structure.graph.GraphUtil;

/**
 * This class calculates the different preprocessing states of a model. The given state is LOADED 
 * - from there the other ones are determined (RESOLVED, REDUCED, SIMPLIFIED). 
 * Be aware that not every model type supports all preprocessing states. 
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class GraphPreprocessing {
	boolean verbose = false;
	
	DataAccess accessor = null;
	ExecWorkflow exec = null;
	
	public GraphPreprocessing(DataAccess accessor, ExecWorkflow exec){
		this.accessor=accessor;
		this.exec = exec;
	}
	
	/**
	 * @param source
	 * @param loadForeignTypes
	 * @param preprocess
	 * @param loadInstances
	 * @return loaded graph including (if set true) foreign types, preprocessed states 
	 * and loaded instances
	 */
	public Graph loadGraph(Source source,
			boolean loadForeignTypes, boolean preprocess,
			boolean loadInstances){
		Graph graph = null;
		if (source == null)
			return null;
		
		// Create a copy of source to set in schema graph
		source = source.copy();
		if (verbose)
			System.out.println("loadGraph(): Load schema " + source);
		
		// assumption this graph is not loaded -> the above layer is responsible to catch that 
		
		// get all sub sources (sourcerelation type contains)
		ArrayList<Source> loadSources = accessor.getSubSources(source);
		if (loadSources == null) {
			// if no sub source add the source itself
			loadSources = new ArrayList<Source>();
			loadSources.add(source);
		}
		
		// load is-a relationships for the (sub) sources		
		ArrayList<SourceRelationship> loadStructRels = new ArrayList<SourceRelationship>();
		for (Source loadSource : loadSources) {
			int sourceid = loadSource.getId();
			SourceRelationship loadStructRel = accessor.getSourceRel(
					accessor.getSourceRelId_ISA(sourceid, sourceid));
			loadStructRels.add(loadStructRel);
		}
		if (verbose) {
			System.out.println(" - Load sources: " + loadSources);
			System.out.println(" - Load sourceRels: " + loadStructRels);
		}
		
		// First load schema graph
		graph = new Graph(source, accessor.loadCompositeStructure(
				loadSources, loadStructRels));
		if (verbose) {
			System.out.println("graph: " + graph.getElementCount() + " vertices " + graph.getEdgesCount() + " edges");
		}
		 // TODO accessor.loadIdConstraints
//		schemaGraph.setIdConstraints(accessor.loadIdConstraints(schema));

		// Important: a reference namespace may occur only once in the repository
		// as one independent source or in one source with other namespaces
		if (loadForeignTypes) {
			 accessor.loadForeignTypes(source, graph);
		}
		
		// check cycles and preprocess
		graph.checkGraphCycles();
		
		// Preprocess
		if (preprocess) {
			preprocessGraph(graph);
		}
		
//		// loadAndPropagateInstances
		graph =  accessor.loadAndPropagateInstances(loadInstances, graph); 
		
		 // computeStatistics
//		GraphUtil.computeStatistics(graph.getGraph(Graph.PREP_DEFAULT_ONTOLOGY));
//		GraphUtil.computeStatistics(graph);
		
		// If not yet paths saved & not an ontology
		if ( // CHANGED: save always
//				source.getType()!=Source.TYPE_ONTOLOGY &&
				! accessor.sourceHasPaths(source.getId())){
			Graph graphRed = graph.getGraph(Graph.PREP_REDUCED);
			if (graphRed==null) graphRed = graph.getGraph(Graph.PREP_RESOLVED);
			ArrayList<Path> paths = graphRed.getAllPaths();
			DataImport importer = new DataImport();
			int srcId = source.getId();
			for (Path graphPathImpl : paths) {
				String accession = graphPathImpl.toIdString();
				String name = graphPathImpl.toNameString();					
				importer.insertObject(srcId, accession, name, null, null, Element.KIND_ELEMPATH, null, null);
			}
		}
		return graph;		
	}
	
    //default preprocess with all methods
	  public Graph preprocessGraph(Graph schemaGraph) {
		  if (Source.TYPE_ONTOLOGY==schemaGraph.getSource().getType()){
			  return preprocessGraph(schemaGraph, Graph.PREP_DEFAULT_ONTOLOGY);
		  } else {
			  return preprocessGraph(schemaGraph, Graph.PREP_SIMPLIFIED);
		  }
	  }
	  
	  //Integrate all preprocessing methods, return the graph of the last representation
	  public Graph preprocessGraph(Graph graph, int targetState) {
	      if (graph==null) return null;
	      boolean verbose = false;
	      if (verbose) System.out.println("preprocessGraph(): Input schema graph " + graph.getSource());

	      int currentState = graph.getPreprocessing();
	      if (currentState>targetState) {
	        return graph.getGraph(targetState);
	      }
	      else if (currentState==targetState) {
	        return graph;
	      }
	      Graph prepGraph = graph;
	      if (currentState==Graph.PREP_LOADED && targetState>=Graph.PREP_RESOLVED) {
	          Graph prepGraphTmp = resolveGraph(prepGraph);        
	          if (prepGraphTmp!=null){
	          	prepGraph = prepGraphTmp;
	          	if (verbose) prepGraph.printGraphInfo();
	          	currentState = prepGraph.getPreprocessing();
	          }
	      }
	      if (currentState==Graph.PREP_RESOLVED && targetState>=Graph.PREP_REDUCED) {
	          prepGraph = reduceGraph(prepGraph);
	          if (verbose) prepGraph.printGraphInfo();
	          currentState = prepGraph.getPreprocessing();
	        }
	      if (currentState==Graph.PREP_REDUCED && targetState>=Graph.PREP_SIMPLIFIED) {
	          Graph simplGraph = simplifyGraph(prepGraph);
	          if (simplGraph!=null){
	          	prepGraph = simplGraph;
	  	        if (verbose) prepGraph.printGraphInfo();
	  	        currentState = prepGraph.getPreprocessing();
	          } 
	        }	      

	      GraphUtil.computeStatistics(prepGraph);
	      return prepGraph;
	  }
	  
	  
		/**
		 * @return this graph after executing preprocessing resolve
		 * preprocessing resolved - element and type references are resolved (both exist as node)
		 */
	  public static Graph resolveGraph(Graph graph) {
		     boolean verbose = false;	
		     if (verbose) System.out.println("resolveGraph(): ");
		     
		     //Check if already done once
		     Graph resolvedGraph = graph.getGraph(Graph.PREP_RESOLVED);     
		     if (resolvedGraph!=null) {
		       if (verbose) System.out.println("resolveGraph(): Found previously resolved prepresentation");
		       return resolvedGraph;
		     }
		     Source source = graph.getSource();
		     //not yet, check if preprocessing step allowed for this content
		     int type = source.getType();
		     if (! GraphUtil.checkTypePreprocessing(type, Graph.PREP_RESOLVED)) {
		         if (verbose) System.out.println("resolveGraph(): Resolve not needed!");
		         resolvedGraph = new Graph(source, graph);
		         resolvedGraph.setPreprocessing(Graph.PREP_RESOLVED);
		         resolvedGraph.setPreviousGraph(graph);
		         graph.setNextGraph(resolvedGraph);
		         return resolvedGraph;
		     }
		     
		     //required, check if the graph in the previous preprocessing
		     if (graph.getPreprocessing()!=Graph.PREP_LOADED) {
		       System.out.println("resolveGraph(): Require PREP_LOADED for this operation!");
		       return null;
		     }

		     resolvedGraph = new Graph();
		     ArrayList<Element> roots = graph.getRoots();
		     //System.out.println("Resolving graph roots: " + roots);
		     long start = System.currentTimeMillis();
		     if (roots!=null) {
		    	 ArrayList<Element> doneVertices = new ArrayList<Element>();
		       for (int i=0; i<roots.size(); i++) {
		    	   Element root = roots.get(i);
		         resolvedGraph = resolveGraph(graph, resolvedGraph, root, doneVertices);
		       }
		     }
		     long end = System.currentTimeMillis();
			 if (verbose)
					System.out.println("--> resolveGraph - loop: "
							+ (float) (end - start) / 1000);
			 
//		     if (source.getType().equals(Source.SRC_CONT_ONTOLOGY) && resolvedGraph.getVerticesCount() > MAX_SIZE_SIMPLIFIED
//		    		 && (resolvedGraph.getEdgesCount()>resolvedGraph.getVerticesCount())){
//		      	System.out.println("resolveGraph(): Ontology AND #Vertices>"+MAX_SIZE_SIMPLIFIED+" AND more edges than vertices ("+resolvedGraph.getEdgesCount()+" > "+resolvedGraph.getVerticesCount()+")! preprocessing RESOLVED aborted");
//		      	return null;
//		      }
			 
//			 System.out.println("getAllPaths: BEFORE "  + resolvedGraph.getAllPaths().size());
			 start = System.currentTimeMillis();
		     resolvedGraph.setSource(source);
		  // TODO resolvedGraph.setIdConstraints
//		     resolvedGraph.setIdConstraints(this.getIdConstraints()); 
		     resolvedGraph.checkGraphCycles();
		     end = System.currentTimeMillis();
			 if (verbose)
					System.out.println("--> resolveGraph - checkGraphCycles: "
							+ (float) (end - start) / 1000);
			 System.out.println("VertexSet: " + resolvedGraph.getElementSet().size()+ "  EdgeSet: " + 	 resolvedGraph.getEdgeSet().size());
//			 System.out.println("getAllPaths: AFTER "  + resolvedGraph.getAllPaths().size());
//			 resolvedGraph.printGraphInfo();
		     resolvedGraph.setPreprocessing(Graph.PREP_RESOLVED);
		     resolvedGraph.setPreviousGraph(graph);
		     graph.setNextGraph(resolvedGraph);
		     if (verbose) System.out.println("resolveGraph(): DONE!!!");
		     return resolvedGraph;
	  }
	
	  
	  /**
		 * @param resolvedGraph
		 * @param root
		 * @param doneVertices
		 * @return graph with resolved types of elements by means of links to the corresponding vertices
		 * The graph may contains shared elements if a type is referenced multiple times
		 */
		static Graph resolveGraph(Graph graph, Graph resolvedGraph, Element root, ArrayList<Element> doneVertices) {
		     if (graph==null || resolvedGraph==null || root==null) return resolvedGraph;
		     
		     boolean verbose = false;
		     
		     if (verbose) System.out.println("Resolving root: " + root);
		     if (doneVertices.contains(root)) {
		       if (verbose) System.out.println("Root: " + root + " already resolved");
		       return resolvedGraph;
		     }

		     DirectedGraphImpl subGraph = graph.getSubGraph(root);
//		     System.out.println("subGraph.getVerticesCount(): " + subGraph.getVerticesCount());
			 GraphUtil.addGraph(resolvedGraph, subGraph);

			 if (verbose)
				 System.out.println("--> getSubGraph + addGraph: ");
		     //identify and resolve subNodes
		     ArrayList<Element> subNodes = graph.getSubNodes(root);
		     if (subNodes==null) subNodes = new ArrayList<Element>();
		     subNodes.add(root);
		     for (int i=0; i<subNodes.size(); i++) {
		    	 Element vertex = subNodes.get(i);
		    	 Element type = graph.resolveVertexType(vertex);

		       //if no type found, do nothing
		       if (type!=null) {
		         if (resolvedGraph.containsVertex(type)) { //type already added to graph
		           Edge resolveEdge = resolvedGraph.getEdge(vertex, type);
		           if (resolveEdge==null) {
		             if (verbose) System.out.println(" - A: Adding resolution edge " + vertex + "->" + type);
		             try {
		               resolvedGraph.addEdge(vertex, type);
		             }
		             catch(Exception e) { System.out.println("resolveSubGraph(): Error adding resolving edge"); }
		           }
		         }
		         else {  //type is completely new
		           if (verbose) System.out.println(" - B: Adding resolution edge " + vertex + "->" + type);
		           try {
		             resolvedGraph.addEdge(vertex, type);
		           }
		           catch(Exception e) { System.out.println("resolveSubGraph(): Error adding resolving edge"); }

		           //Recursive resolve the type
		           resolvedGraph = resolveGraph(graph, resolvedGraph, type, doneVertices);
		         }
		       }
		     }
		     doneVertices.add(root);
		     if (verbose) System.out.println("Done root: " + root);
		     return resolvedGraph;
		   }

	  
		/**
		 * @return this graph after executing preprocessing reduce
		 * preprocessing reduce - the two nodes from element/type references are reduced to one
		 * Problem: type extension with attributes/elements of intermediate types!
		 */
		static public Graph reduceGraph(Graph graph) {
		     boolean verbose = false;
		     if (verbose) System.out.println("reduceGraph(): ");

		     //check if already done
		     Graph reducedGraph = graph.getGraph(Graph.PREP_REDUCED);
		     if (reducedGraph!=null) {
		       if (verbose) System.out.println("reduceGraph(): Found previously reduced prepresentation");
		       return reducedGraph;
		     }    
		     Source source = graph.getSource();
//		     if (source.getType().equals(Source.SRC_CONT_ONTOLOGY)) {
//		         nextGraph = copy();
//		    	 nextGraph.setPreprocessing(PREP_REDUCED);
//		    	 nextGraph.setPreviousGraph(this);
//		         return nextGraph;
//		     }
		     
		     //not yet, check if prepropcessing is allowed for source type
		     int type = source.getType();
		     if (! checkTypePreprocessing(type, Graph.PREP_REDUCED)) {
		         if (verbose) System.out.println("reduceGraph(): Reduce not needed!");
		         reducedGraph = new Graph(source, graph);
		         reducedGraph.setPreprocessing(Graph.PREP_REDUCED);
		         reducedGraph.setPreviousGraph(graph);
		         graph.setNextGraph(reducedGraph);
		         return reducedGraph;
		     }     

		     //required, check if graph in previous preprocessing 
		     if (graph.getPreprocessing()!=Graph.PREP_RESOLVED) {
		       System.out.println("reduceGraph(): Require PREP_RESOLVED for this operation!");
		       return null;
		     }     

		     reducedGraph = new Graph(source, graph);
		     Graph typeHierarchies = reducedGraph.findTypeHierarchies();
		     if (typeHierarchies==null) {
		         if (verbose) System.out.println("reduceGraph(): No type hierarchies detected!");
		         reducedGraph.setPreprocessing(Graph.PREP_REDUCED);
		         reducedGraph.setPreviousGraph(graph);
		         graph.setNextGraph(reducedGraph);
		         return reducedGraph;
		     }
		     
		     if (verbose) {
		       System.out.println("Type hierarchies detected: ");
		       typeHierarchies.print();
		     }
		     ArrayList<Element> roots = typeHierarchies.getRoots();
		     ArrayList<Element> removed = new ArrayList<Element>();
		     for (int i=0; i<roots.size(); i++) {
		    	 Element root = roots.get(i);
		    	 Element leaf = typeHierarchies.getLeaves(root).get(0); //only one leaf expected

		       if (verbose) System.out.println("Processing hierarchie: root " + root + " and leaf " + leaf);

		       ArrayList<Element> leafChildren = reducedGraph.getChildren(leaf);
		       ArrayList<Element> rootTypes = typeHierarchies.getSubNodes(root);

		       String leafType = leaf.getType();

		       //propagate type of leaf to root
		       if (verbose) System.out.println(" - Propagate type " + leafType + " from leaf to root");
		       root.setType(leafType); //!!! changed here !!!

		       if (leafChildren!=null) {
		         //attach all children of leaf to root
		         if (verbose) System.out.println(" - Add children of leaf to root: " + leafChildren);
		         for (int j=0; j<leafChildren.size(); j++) {
		        	 Element leafChild = leafChildren.get(j);
		           try {
		             reducedGraph.addEdge(root, leafChild);
		           }
		           catch (Exception e) { System.out.println("reduceResolvedGraph(): Adding edge " + e.getMessage()); }
		         }
		       }

		       //nodes to be removed later
		       if (verbose) System.out.println(" - Remove intermediate types: " + rootTypes);
		       rootTypes.removeAll(removed);
		       removed.addAll(rootTypes);
		     }

		     //remove all intermediate types of roots
		     for (int i=0; i<removed.size(); i++) {
		       Element vertex = removed.get(i);
		       try {
		         reducedGraph.removeElement(vertex);
		       }
		       catch (Exception e) { System.out.println("reduceGraph(): Removing types " + e.getMessage()); }
		     }
		     
		     //further remove all unconnected root, i.e. unused types 
		     roots = reducedGraph.getRoots();
		     for (int i=0; i<roots.size(); i++) {
		    	 Element element = roots.get(i);
		       if (reducedGraph.isLeaf(element)) {
		           if (verbose) System.out.println(" - Remove unconnected root: " + element);
		           try {
		               reducedGraph.removeElement(element);
		           }
		           catch (Exception e) { System.out.println("reduceGraph(): Removing unconnected root " + e.getMessage()); }
		       }
		     }
		     
		     reducedGraph.setSource(source);
		     reducedGraph.checkGraphCycles();
		     reducedGraph.setPreprocessing(Graph.PREP_REDUCED);
		     reducedGraph.setPreviousGraph(graph);
		     graph.setNextGraph(reducedGraph);
		     
		     if (verbose) System.out.println("reduceGraph(): DONE!!!");
		     return reducedGraph;
		  }
		
		  /**
		 * @param type
		 * @param preprocessing
		 * @return true if the given source type is available in the given preprocessing 
		 */
		public static boolean checkTypePreprocessing(int type, int preprocessing) {
		      boolean verbose = false;
		      if (verbose) System.out.print("checkTypePreprocessing(): " + type + " and " + Graph.preprocessingToString(preprocessing));
		      if (type==Source.UNDEF) return false;
//		      else if (type.equals(Source.SRC_CONT_ONTOLOGY) && preprocessing==PREP_SIMPLIFIED) {
//		          if (verbose) System.out.println(" -> NO");
//		          return false;
//		      }
		      //All schemas can be loaded or simplified
		      else if (preprocessing==Graph.PREP_LOADED || preprocessing==Graph.PREP_SIMPLIFIED) {
		          if (verbose) System.out.println(" -> OK");
		          return true;
		      }
		      //No other preprocessing for INTERN, i.e. saved imported schemas 
		      else if (type==Source.TYPE_INTERN) {
		          if (verbose) System.out.println(" -> NO");
		          return false;
		      }
		      //XDR, XSD, ODBC
		      if (verbose) System.out.println(" -> OK");
		      return true;
		  }
		

		/**
		 * @return this graph after executing preprocessing simplify
		 * preprocessing simplified - elements and fragments that appear several times are represented by the same node
		 */
		   public Graph simplifyGraph(Graph graph) {
			    boolean verbose = false;
			    if (verbose) System.out.println("simplifyGraph(): ");

			    //check if already done once
			    Graph simpleGraph = graph.getGraph(Graph.PREP_SIMPLIFIED);
			    if (simpleGraph!=null) {
			      if (verbose) System.out.println("simplifyGraph(): found previously simplified representation");
			      return simpleGraph;
			    }
			    Source source = graph.getSource();
//			    if (source.getType().equals(Source.SRC_CONT_ONTOLOGY)) {
//			        nextGraph = copy();
//			        nextGraph.setPreprocessing(PREP_SIMPLIFIED);
//			   	 	nextGraph.setPreviousGraph(this);
//			        return nextGraph;
//			    }
			    
			    //not yet, check if preprocessing is allowed for source type
			    int type = source.getType();
			    if (! checkTypePreprocessing(type, Graph.PREP_SIMPLIFIED)) {
			        if (verbose) System.out.println("simplifyGraph(): Simplify not needed!");
			        simpleGraph = new Graph(source, graph);
			        simpleGraph.setPreprocessing(Graph.PREP_SIMPLIFIED);
			        simpleGraph.setPreviousGraph(graph);
			        graph.setNextGraph(simpleGraph);
			        return simpleGraph;
			    }    

			    //Required, check if graph in the previous preprocessing
			    if (graph.getPreprocessing()!=Graph.PREP_REDUCED) {
			      System.out.println("simplifyGraph(): Require PREP_REDUCED for this operation!");
			      return null;
			    }
			    
//			    if (this.getVerticesCount()>MAX_SIZE_SIMPLIFIED){
//			    	System.out.println("simplifyGraph(): Graph too large (>"+MAX_SIZE_SIMPLIFIED+" vertices)! preprocessing SIMPLIFIED not possible");
//			    	return null;
//			    }
			    
			    //Match intra graph and set intraResult
			    graph.checkGraphCycles(); 
			    MatchResult intraMapping = matchIntra(graph);  //put intraMapping in the Reduced graph, i.e. this 
			    graph.setIntraMapping(intraMapping);
			    if (graph.getIntraMapping()==null) {
			        if (verbose) System.out.println("simplifyGraph(): No intra mapping computed!");
			        simpleGraph = new Graph(source, graph);
			        simpleGraph.setPreprocessing(Graph.PREP_SIMPLIFIED);
			        simpleGraph.setPreviousGraph(graph);
			        graph.setNextGraph(simpleGraph);
			        return simpleGraph;        
			    }
			    
//			    // remove correspondences if AA, AB, BA, BB -> remove either AB or BA because otherwise one node to much deleted
//			    ArrayList<Object> srcObjects = intraMapping.getSrcMatchObjects();
//			    for (Object srcObject : srcObjects) {
//					 ArrayList<Object> trgObjects = intraMapping.getTrgMatchObjects(srcObject);
//					 trgObjects.remove(srcObject);
//					 if (trgObjects.isEmpty()){
//						 continue;
//					 }
//					 // go for every AB
//					 for (Object trgObject : trgObjects) {
//						 if (intraMapping.getSimilarity(trgObject, srcObject)>0){
//							 // and find out if BA exists
//							if(trgObject.toString().compareTo(srcObject.toString())<0){
//								intraMapping.remove(srcObject, trgObject);
//							} else {
//								intraMapping.remove(trgObject, srcObject);
//							}
//						 }
//					 }
//				}

			    if (verbose) {
			      System.out.println("Intra MatchResult: ");
//			      intraMapping.print();
			    }
	
			    simpleGraph = new Graph(source, graph);
			    Graph intraSimGraph = intraMapping.toConnectedGraph(); //put intraSimGraph in the Reduced graph, i.e. this
			    graph.setIntraSimGraph(intraSimGraph);
			    Iterator iterator = intraSimGraph.getElementIterator();
			    while (iterator.hasNext()) {
			      Element vertex = (Element)iterator.next();
			      try {
			        simpleGraph.removeVertex(vertex);
			      }
			      catch (Exception e) { System.out.println("simplifyGraph(): Error removing vertex"); }
			    }
	
			    ArrayList connectedSets = new ArrayList(intraSimGraph.getConnectedSets());
			    ArrayList<Element> sharedVertices = new ArrayList<Element>();
	
			    for (int i=0; i<connectedSets.size(); i++) {
			      Collection set = (Collection)connectedSets.get(i);
			      iterator = set.iterator();
	
			      //Important !!!
			      //take vertex with lowest id as shared vertex for stability reasons
			      Element sharedVertex = null;
			      int sharedId = Integer.MAX_VALUE;
			      while (iterator.hasNext()) {
			        Element elem = (Element)iterator.next();
			        int id = elem.getId();
			        if (id<sharedId) {
			          sharedVertex = elem;
			          sharedId = id;
			        }
			      }
			      if (verbose) System.out.println("Simplify vertex set: " + set + " to " + sharedVertex);
			      try {
			        simpleGraph.addVertex(sharedVertex);
			      }
			      catch (Exception e) { System.out.println("simplifyGraph(): Error adding vertex"); }
			      sharedVertices.add(sharedVertex);
			    }
	
			    for (int i=0; i<connectedSets.size(); i++) {
			      Collection set = (Collection)connectedSets.get(i);
			      Element sharedVertex = sharedVertices.get(i);
			      if (verbose) System.out.println("Process set: " +  set + " with " + sharedVertex);
			      iterator = set.iterator();
			      while (iterator.hasNext()) {
			    	  Element vertex = (Element)iterator.next();
			        if (verbose) System.out.println("- Process vertex: " + vertex);
			        ArrayList parents = graph.getParents(vertex);
			        if (parents!=null) {
			          if (verbose) System.out.println("  - Adding edges to parents: " + parents);
			          for (int j=0; j<parents.size(); j++) {
			        	  Element parent = (Element)parents.get(j);
			            for (int k=0; k<connectedSets.size(); k++) {
			              Collection checkSet = (Collection)connectedSets.get(k);
			              if (checkSet.contains(parent)) {
			                if (verbose) System.out.println("  - Encountered simplified parent: " + parent);
			                parent = sharedVertices.get(k);
			              }
			            }
			            ArrayList addedParents = simpleGraph.getParents(sharedVertex);
			            if (addedParents==null || !addedParents.contains(parent)) {
			              try {
			                simpleGraph.addEdge(parent, sharedVertex);
			              }
			              catch (Exception e) { System.out.println("simplifyGraph(): Error adding parent edge"); }
			            }
			          }
			        }
	
			        ArrayList children = graph.getChildren(vertex);
			        if (children!=null) {
			          if (verbose) System.out.println("  - Adding edges to children: " + children);
			          for (int j=0; j<children.size(); j++) {
			        	  Element child = (Element)children.get(j);
			            for (int k=0; k<connectedSets.size(); k++) {
			              Collection checkSet = (Collection)connectedSets.get(k);
			              if (checkSet.contains(child)) {
			                if (verbose) System.out.println("  - Encountered simplified child: " + child);
			                child = sharedVertices.get(k);
			              }
			            }
			            ArrayList addedChildren = simpleGraph.getChildren(sharedVertex);
			            if (addedChildren==null || !addedChildren.contains(child)) {
			              try {
			                simpleGraph.addEdge(sharedVertex, child);
			              }
			              catch (Exception e) { System.out.println("simplifyGraph(): Error adding child edge"); }
			            }
			          }
			        }
			      }
			    }
			    simpleGraph.setSource(source);
			    simpleGraph.checkGraphCycles();
			    simpleGraph.setPreprocessing(Graph.PREP_SIMPLIFIED);
			    simpleGraph.setPreviousGraph(graph);
			    graph.setNextGraph(simpleGraph);
			    
			    if (verbose) System.out.println("simplifyGraph(): DONE: " +
			    		graph.vertexSet().size() + " => " +
			            simpleGraph.vertexSet().size());
			    return simpleGraph;
			  }
		
		   //Problem with statistics: when nodes have the same names and different sets of descandants
		   public MatchResult matchIntra(Graph graph) {
			  GraphUtil.computeStatistics(graph);

			  // initializing
////		     StructureResolution structResolution = new StructureResolution(this, getRoots(), getRoots());
////		     NameResolution nameResolution = new NameResolution();
////		     SemanticSimilarity semSimilarity = new SemanticSimilarity();
////		     ResolutionLibrary resolutionLibrary = new ResolutionLibrary(nameResolution, structResolution);
////		     SimilarityLibrary similarityLibrary = new SimilarityLibrary(nameResolution, semSimilarity
////		 			// not used: taxonomy
//////		     		, null
////		     		); //TODO? use taxonomyDistanceSim here as well?

			  // OLD Version
//			  make sure name only based on Nametokens not Synonyms or Abbreviations
//			  SYS_NAME.setConstMatchers( {STR_TRIGRAM})
//
//			  fragMatcher
//			  ConstMatchers({SYS_NAME, SYS_STATISTICS);
//			  Selection(CombinationLibrary.COM_SEL_SIMTHRESHOLD, 0, (float)0.0, 1); 
//
//			  leafMatcher
//			  ConstMatchers({SYS_NAME, SYS_DATATYPE});
//			  Selection(CombinationLibrary.COM_SEL_SIMTHRESHOLD, 0, (float)0.0, 1); 
	
			  // NEW Version
//			  Matcher NAMETOKEN already using RES3_NAMETOKEN and SIM_STR_TRIGRAM 
			  Matcher[] innerConstMatcher = new Matcher[2];
			  innerConstMatcher[0] = new Matcher(Matcher.NAME);
			  innerConstMatcher[1] = new Matcher(Matcher.STATISTICS);
			  ComplexMatcher innerMatcher = new ComplexMatcher(Resolution.RES2_SELFNODE, innerConstMatcher,
					  Combination.COM_WEIGHTED, new float[]{(float)0.7, (float)0.3}, Combination.SET_AVERAGE);
			  
			  Matcher[] leafConstMatcher = new Matcher[2];
			  leafConstMatcher[0] = new Matcher(Matcher.NAME);
			  leafConstMatcher[1] = new Matcher(Matcher.DATATYPE);
			  ComplexMatcher leafMatcher = new ComplexMatcher(Resolution.RES2_SELFNODE, leafConstMatcher,
					  Combination.COM_WEIGHTED, new float[]{(float)0.7, (float)0.3}, Combination.SET_AVERAGE);
			 
			  // OLD Version
//			//Match inner nodes
//			  ArrayList inners = getInners(); 
//			  MatchResult innerResult = innerMatcher.matchConstituentsComplete_1(inners);      
//			//Match leave
//			  ArrayList leaves = graph.getLeaves(); 
//			  MatchResult leafResult = leafMatcher.matchConstituentsComplete_1(leaves);
			  Selection selection = new Selection(Selection.DIR_BOTH, Selection.SEL_THRESHOLD, 1);
			  
			  // NEW Version
			 Workflow w = new Workflow();
			 w.setSource(graph);
			 w.setTarget(graph);
			  

			 
			 w.addBegin(new Strategy(Resolution.RES1_INNERNODES, innerMatcher, selection));
		     MatchResult[] innerResult = exec.execute(w);
//		     innerResult.print();
		     		     
		     w.clear();
		     w.addBegin(new Strategy(Resolution.RES1_LEAFNODES, leafMatcher, selection));
		     MatchResult[] leafResult = exec.execute(w);
//		     leafResult.print();
		     
		     MatchResult compResult = null;
		     if (innerResult!=null && innerResult.length==1 && leafResult!=null && leafResult.length==1){
		    	 compResult = MatchResult.merge(innerResult[0], leafResult[0]);
		     } else{
		    	 System.err.println("GraphPreprocessing.matchIntra results either null or more than 1 returned - both is not valid");
		     }
		     return compResult;
		  }
		  
}
