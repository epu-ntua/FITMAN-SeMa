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

package de.wdilab.coma.structure.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import de.wdilab.coma.structure.Edge;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;
import de.wdilab.coma.structure.Path;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.Statistics;

/**
 * This class contains useful functions for using graphs e.g.
 * - add one graph onto another
 * - using the nodes and paths to calculate basic statistics
 * - transform graph components from one preprocessing state to another
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class GraphUtil {
	
	  //---------------------------------------------------------------------------//
	  // Graph operations                                                          //
	  //---------------------------------------------------------------------------//
	  //Add a subgraph to a graph, retaining all vertices and edges from the subgraph
	  public static void addGraph(DirectedGraphImpl graph, DirectedGraphImpl subGraph) {
	    if (graph==null || subGraph==null) return;
	    HashMap<String, Element> vertices = new HashMap<String, Element>();
		for (Iterator<Element> iterator = graph.getElementSet().iterator(); iterator.hasNext();) {
			Element element = iterator.next();
			String label = element.getName();
			vertices.put(label, element);
		}
	    
	    //Adding all vertices
		for (Iterator<Element> iterator = subGraph.getElementSet().iterator(); iterator.hasNext();) {
			Element element = iterator.next();
	      if (! graph.containsVertex(element)) {
	        try {
	        	graph.addVertex(element);
	        }
	        catch (Exception e) { System.out.println("addGraph(): Error adding vertex " + element); }
	      }
	    }

	    //Adding all edges
	    for (Iterator<Edge> iterator = subGraph.getEdgeSet().iterator(); iterator.hasNext();) {
	    	Edge edge =iterator.next();
	      if (! graph.containsEdge(edge)) {
	        try {
	          graph.addEdge(edge.getSource(), edge.getTarget(), edge);
	        }
	        catch (Exception e) { System.out.println("addGraph: Error adding edge " + edge);  }
	      }
	    }
	  }
	  

	  
	  //source content and preprocessing
	  public static boolean checkTypePreprocessing(int type, int preprocessing) {
	      boolean verbose = false;
	      if (verbose) System.out.print("checkTypePreprocessing(): " + Source.typeToString(type) + " and " + Graph.preprocessingToString(preprocessing));
	      if (type==Source.UNDEF) return false;
//	      else if (content.equals(Source.SRC_CONT_ONTOLOGY) && preprocessing==GRAPH_STATE_SIMPLIFIED) {
//	          if (verbose) System.out.println(" -> NO");
//	          return false;
//	      }
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
	  
	   static public void computeStatistics(Graph graph) {
		     Iterator iterator = graph.getElementIterator();
		     while (iterator.hasNext()) {
		       Element elem = (Element)iterator.next();
		       Statistics statistics = Statistics.computeStatistics(graph, elem);
		       elem.setStatistics(statistics);
		     }
	   }
	   

	  
	  static public MatchResult transformComponents(Graph graph, ArrayList currentComps, int targetState) {
		  if (graph==null) return null;
		  int preprocessing = graph.getPreprocessing();
		    if (currentComps==null || targetState==preprocessing) return null;
		    //System.out.println("Transform:  " + stateToString(currentState) + " -> " + stateToString(targetState));
		    //System.out.println("Components: " + currentComps);

		    MatchResult matchresult = null;
		    ArrayList comps = currentComps;
		    if (targetState<preprocessing) {
		      for (int i=preprocessing; i>targetState; i--) {
		        //System.out.println("PreviousState: " + stateToString(i));
		        MatchResult previousMatchresult = transformComponentsToPreviousState(graph, comps);
		        if (matchresult!=null) {
		          matchresult = MatchResult.compose(matchresult, previousMatchresult, MatchResult.COMPOSITION_MAX);
		        }
		        else matchresult = previousMatchresult;
		        graph = graph.getPreviousGraph();
		        comps = matchresult.getTrgObjects(); // what are these 2 lines for? //david
		      }
		    }
		    else {
		      for (int i=preprocessing; i<targetState; i++) {
		        //System.out.println("NextState: " + stateToString(i));
		        MatchResult nextMapping = transformComponentsToNextState(graph, comps);
		        if (matchresult!=null) {
		          matchresult = MatchResult.compose(matchresult, nextMapping, MatchResult.COMPOSITION_MAX);
		        }
		        else matchresult = nextMapping;
		        graph = graph.getNextGraph();
		        comps = matchresult.getTrgObjects();
		      }
		    }
		    if (matchresult!=null){
			    matchresult.setSourceGraph(graph);
			    matchresult.setTargetGraph(graph.getGraph(targetState));
			    matchresult.setMatchInfo(MatchResult.operationToString(MatchResult.OP_TRANSFORM));
			    return matchresult;
		    }
		    return null;
		  }
 	  
	  //Get correspondences between components in the current rep to the previous one
	  static public MatchResult transformComponentsToPreviousState(Graph graph, ArrayList currentComps) {
	    if (currentComps==null) return null;
	    MatchResult mapping = new MatchResultArray();

	    //long start = System.currentTimeMillis();
	    for (int i=0; i<currentComps.size(); i++) {
	      Object currentComp = currentComps.get(i);
	      ArrayList previousComps = getPreviousStateComponents(graph, currentComp);
	      if (previousComps!=null) {
	        for (int j=0; j<previousComps.size(); j++) {
	          Object previousComp = previousComps.get(j);
	          mapping.append(currentComp, previousComp, (float)1.0);
	        }
	      }
	    }
	    //long end = System.currentTimeMillis();
	    //System.out.println("TransfromToPrevState: " + currentComps.size() + ": " + (end-start));

	    mapping.setSourceGraph(graph);
	    mapping.setTargetGraph(graph.getPreviousGraph());
	    mapping.setMatchInfo(MatchResult.operationToString(MatchResult.OP_TRANSFORM));
	    return mapping;
	  }
	  
	  //Get correspondences between components in the current rep to the next one
	  static public MatchResult transformComponentsToNextState(Graph graph, ArrayList currentComps) {
	    if (currentComps==null) return null;
	    MatchResult mapping = new MatchResultArray();

	    //long start = System.currentTimeMillis();
	    for (int i=0; i<currentComps.size(); i++) {
	      Object currentComp = currentComps.get(i);
	      ArrayList nextComps = getNextStateComponents(graph, currentComp);
	      if (nextComps!=null) {
	        for (int j=0; j<nextComps.size(); j++) {
	          Object nextComp = nextComps.get(j);
	          mapping.append(currentComp, nextComp, (float)1.0);
	        }
	      }
	    }
	    //long end = System.currentTimeMillis();
	    //System.out.println("TransfromToNextState: " + currentComps.size() + ": " + (end-start));

	    mapping.setSourceGraph(graph);
	    mapping.setTargetGraph(graph.getNextGraph());
	    mapping.setMatchInfo(MatchResult.operationToString(MatchResult.OP_TRANSFORM));
	    return mapping;
	  }
	  
	  //Get the corresponding component(s) in the previous representation for a comp of the current graph
	  static public ArrayList getPreviousStateComponents(Graph graph, Object currentComp) {
	    if (currentComp==null) return null;
	    Graph previousGraph = graph.getPreviousGraph();
	    ArrayList<Object> previousComps = new ArrayList<Object>();
	    if (previousGraph==null) {
	      System.out.println("getPreviousStateComponents(): No previous graph set!");
	      return null;
	    }
	    int preprocessing = graph.getPreprocessing();
	    if (preprocessing==Graph.PREP_LOADED) {
	      //no more previous
	      previousComps.add(currentComp);
	    }
	    else if (preprocessing==Graph.PREP_RESOLVED) {
	      //transform resolved to loaded component
	       if (currentComp instanceof Element) {
	    	   Element currentVertex = (Element)currentComp;
	        if (previousGraph.containsVertex(currentVertex))
	          previousComps.add(currentVertex);
	      }
	      else {
	    	  Path currentPath = (Path)currentComp;
	        Element resolvedLast = currentPath.getLastElement();
	        if (previousGraph.containsVertex(resolvedLast)) {
	          ArrayList<Path> loadedPaths = previousGraph.getUpRootPaths(resolvedLast);
	          for (int i=0; i<loadedPaths.size(); i++) {
	            Path loadedPath = loadedPaths.get(i);
	            if (previousGraph.isPathValid(loadedPath) && currentPath.containsAll(loadedPath))
	              previousComps.add(loadedPath);
	          }
	        }
	      }
	    }
	    else if (preprocessing==Graph.PREP_REDUCED) {
	      //transform reduced to resolved component
	      if (currentComp instanceof Element) {
	    	  Element currentVertex = (Element)currentComp;
	        if (previousGraph.containsVertex(currentVertex))
	          previousComps.add(currentVertex);
	      }
	      else {
	    	  Path currentPath = (Path)currentComp;
	        Element reducedFirst = currentPath.getFirstElement();
	        Element reducedLast = currentPath.getLastElement();
	        if (previousGraph.containsVertex(reducedFirst) && previousGraph.containsVertex(reducedLast)) {
	          ArrayList<Path> resolvedPaths = previousGraph.getPaths(reducedFirst, reducedLast);
	          for (int j=0; j<resolvedPaths.size(); j++) {
	        	  Path resolvedPath = resolvedPaths.get(j);
	            if (previousGraph.isPathValid(resolvedPath) && resolvedPath.containsAll(currentPath)) {
	              previousComps.add(resolvedPath);
	            }
	          }
	        }
	      }
	    }
	    else if (preprocessing==Graph.PREP_SIMPLIFIED) {
		      Graph simGraph = previousGraph.getIntraSimGraph();
	      //transform simplified to reduced component
	      if (previousGraph.getIntraMapping()==null || simGraph==null) {
	        System.out.println("getPreviousStateComponents(): No intraMapping computed!");
	        return null;
	      }

	      if (currentComp instanceof Element) {
	    	  Element currentVertex = (Element)currentComp;
	        Collection connectedSet = null;
	        try {
	        	connectedSet = simGraph.getConnectedSet(currentVertex);
	        }
	        catch (Exception e) {}
	        ArrayList<Element> matchVertices = null;
	        if (connectedSet==null) matchVertices = new ArrayList<Element>();
	        else matchVertices = new ArrayList<Element>(connectedSet);
	        if (! matchVertices.contains(currentVertex)) matchVertices.add(currentVertex);
	        for (int i=0; i<matchVertices.size(); i++) {
	        	Element vertex = matchVertices.get(i);
	          if (previousGraph.containsVertex(vertex)) previousComps.add(vertex);
	        }
	      }
	      else {
	        ArrayList currentPath = (ArrayList)currentComp;
	        for (int i=0; i< currentPath.size(); i++) {
	        	Element vertex = (Element)currentPath.get(i);
	          Collection connectedSet = null;
	          try {
	        	  connectedSet = simGraph.getConnectedSet(vertex);
	          }
	          catch (Exception e) {}

	          ArrayList matchVertices = null;
	          if (connectedSet==null) matchVertices = new ArrayList();
	          else matchVertices = new ArrayList(connectedSet);
	          if (! matchVertices.contains(vertex)) matchVertices.add(vertex);

	          ArrayList<Object> newValidPaths = new ArrayList<Object>();
	          if (previousComps.isEmpty()) {
	            for (int k=0; k<matchVertices.size(); k++) {
	            	Element matchVertex = (Element)matchVertices.get(k);
	            	Path newPath = new Path(previousGraph, matchVertex);
	              newValidPaths.add(newPath);
	            }
	          }
	          else {
	            for (int j=0; j<previousComps.size(); j++) {
	            	Path validPath = (Path)previousComps.get(j);
	              for (int k=0; k<matchVertices.size(); k++) {
	                Element matchVertex = (Element)matchVertices.get(k);
	                Path newPath = new Path(validPath);
	                newPath.add(matchVertex);

	                //check with the previousGraph
	                if (previousGraph.isPathValid(newPath))
	                  newValidPaths.add(newPath);
	              }
	            }
	          }
	          if (newValidPaths.isEmpty()) return null;
	          previousComps = newValidPaths;
	        }
	      }
	    }
	    if (previousComps.isEmpty()) return null;
	    return previousComps;
	  }
	  
	  
	  //Get the corresponding component(s) in the next representation for a comp of the current graph
	 static public ArrayList getNextStateComponents(Graph graph, Object currentComp) {
	    if (currentComp==null) return null;
	    Graph nextGraph = graph.getNextGraph();
	    if (nextGraph==null) {
	      System.out.println("getNextStateComponents(): No next graph set!");
	      return null;
	    }
	    int preprocessing = graph.getPreprocessing();
	    ArrayList<Object> nextComps = new ArrayList<Object>();
	    if (preprocessing==Graph.PREP_LOADED) {
	      //transform loaded to resolved component
	      if (currentComp instanceof Element) {
	    	  Element currentVertex = (Element)currentComp;
	        if (nextGraph.containsVertex(currentVertex))
	          nextComps.add(currentVertex);
	      }
	      else {
	    	  Path currentPath = (Path)currentComp;
	        Element loadedLast = currentPath.getLastElement();
	        if (nextGraph.containsVertex(loadedLast)) {
	          ArrayList<Path> resolvedPaths = nextGraph.getUpRootPaths(loadedLast);
	          for (int j=0; j<resolvedPaths.size(); j++) {
	        	  Path resolvedPath = resolvedPaths.get(j);
	            if (nextGraph.isPathValid(resolvedPath) && resolvedPath.containsAll(currentPath))
	              nextComps.add(resolvedPath);
	          }
	        }
	      }
	    }
	    else if (preprocessing==Graph.PREP_RESOLVED) {
	      //transform resolved to reduced component
	      if (currentComp instanceof Element) {
	    	  Element currentVertex = (Element)currentComp;
	        if (nextGraph.containsVertex(currentVertex))
	          nextComps.add(currentVertex);
	      }
	      else {
	    	  Path currentPath = (Path)currentComp;
	        Element resolvedFirst = currentPath.getFirstElement();
	        Element resolvedLast = currentPath.getLastElement();
	        if (nextGraph.containsVertex(resolvedFirst) && nextGraph.containsVertex(resolvedLast)) {
	          ArrayList<Path> reducedPaths = nextGraph.getPaths(resolvedFirst, resolvedLast);
	          for (int j=0; j<reducedPaths.size(); j++) {
	        	  Path reducedPath = reducedPaths.get(j);
	            if (nextGraph.isPathValid(reducedPath) && currentPath.containsAll(reducedPath)) {
	              nextComps.add(reducedPath);
	            }
	          }
	        }
	      }
	    }
	    else if (preprocessing==Graph.PREP_REDUCED) {
		      Graph simGraph = graph.getIntraSimGraph();
	      //transform reduced to simplified component
	      if (graph.getIntraMapping()==null || simGraph==null) {
	        System.out.println("getNextStateComponents(): No intraMapping computed!");
	        return null;
	      }


	      if (currentComp instanceof Element) {
	    	  Element currentVertex = (Element)currentComp;
	        Collection connectedSet = null;
	        try {
	        		connectedSet = simGraph.getConnectedSet(currentVertex);
	        }
	        catch (Exception e) {}
	        Element sharedVertex = null;
	        if (connectedSet==null) {
	          sharedVertex = currentVertex;
	        }
	        else {
	          Iterator iterator = connectedSet.iterator();
	          //Important !!!
	          //take vertex with lowest id as shared vertex for stability reasons
	          int sharedId = Integer.MAX_VALUE;
	          while (iterator.hasNext()) {
	            Element elem = (Element)iterator.next();
	            int id = elem.getId();
	            if (id<sharedId) {
	              sharedVertex = elem;
	              sharedId = id;
	            }
	          }
	        }
	        if (nextGraph.containsVertex(sharedVertex))
	          nextComps.add(sharedVertex);
	      }
	      else {
	    	  Path currentPath = (Path)currentComp;
	        Path simplifiedPath = new Path(nextGraph);
	        for (int i=0; i<currentPath.size(); i++) {
	        	Element vertex = currentPath.get(i);
	          Collection connectedSet = null;
	          try {
	        		connectedSet = simGraph.getConnectedSet(vertex);
	          }
	          catch (Exception e) {}
	          if (connectedSet==null) simplifiedPath.add(vertex);
	          else {
	            Iterator iterator = connectedSet.iterator();

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
	            simplifiedPath.add(sharedVertex);
	          }
	        }
	        //check with nextGraph
	        if (nextGraph.isPathValid(simplifiedPath))
	          nextComps.add(simplifiedPath);
	      }
	    }
	    else if (preprocessing==Graph.PREP_SIMPLIFIED) {
	      //no more next
	      nextComps.add(currentComp);
	    }
	    if (nextComps.isEmpty()) return null;
	    return nextComps;
	  }
	  
}
