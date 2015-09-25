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

package de.wdilab.coma.structure;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.jgrapht.Graphs;
import org.jgrapht.alg.CycleDetector;

import de.wdilab.coma.structure.graph.DirectedGraphImpl;
import de.wdilab.coma.structure.graph.ElementComparator;
import de.wdilab.coma.structure.graph.GraphUtil;

/**
 * Graph contains not just the elements (nodes) and edges (hierarchy) 
 * but also functions to determine special kinds of nodes e.g. parents, children 
 * or types e.g. simple type, complex types
 * a from the repository loaded graph has also to be preprocessed to get e.g. the resolved version
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class Graph extends DirectedGraphImpl{
	// constants
	 boolean verbose = false;
	
	// preprocessing loaded - represents information as directly loaded from file
	public static final int PREP_LOADED = 0;
	// preprocessing resolved - element and type references are resolved (both exist as node)
	public static final int PREP_RESOLVED = PREP_LOADED + 1; // 1
	// preprocessing reduce - the two nodes from element/type references are reduced to one
	public static final int PREP_REDUCED = PREP_RESOLVED + 1; // 2
	// preprocessing simplified - elements and fragments that appear several times are represented by the same node
	public static final int PREP_SIMPLIFIED = PREP_REDUCED + 1; // 3

	// default
	public static final int PREP_DEFAULT_ONTOLOGY = PREP_RESOLVED; // PREP_LOADED PREP_RESOLVED
	public static final int PREP_DEFAULT_XML_REL = PREP_SIMPLIFIED; // PREP_SIMPLIFIED
	
	static final int ALL_ELEMENTS = 1;
	static final int ALL_SIMPLETYPE = 2;
	static final int ALL_COMPLEXTYPE = 3;
	static final int GLOBAL_ELEMENTS = 4;
	static final int GLOBAL_SIMPLETYPE = 5;
	static final int GLOBAL_COMPLEXTYPE = 6;
	
    // For schema analysis
    int frequentCharSize = 10;
	
    float hasInstances=Integer.MIN_VALUE;
    float averageInstanceCnt=Integer.MIN_VALUE;
    
	// variables
	private Source source;
	// current preprocessing of the graph
	private int preprocessing = PREP_LOADED;
	// graph with the next "lower" preprocessing, only null if preprocessing loaded
	private Graph previousGraph = null;
	// graph with the next "higher" preprocessing, null if preprocessing simplified or not calculataed
	private Graph nextGraph = null;
	
	//intraMapping, produced by simplifyGraph, captures correspondences between
	//nodes of this representation, i.e. reduced
	private MatchResult intraMapping = null;
	private Graph intraSimGraph = null;
	  
	ArrayList elementNames=null;
	
	// constructors
	/**
	 * Constructor that only creates objects (no nodes or edges yet)
	 */
	public Graph() {
		super();
	}
	
	/**
	 * @param source
	 * @param directedGraph
	 * Constructor setting the given source and adding given graph
	 */
	public Graph(Source source, DirectedGraphImpl directedGraph) {
		super();
		this.source = source;
		GraphUtil.addGraph(this, directedGraph);
//		if (directedGraph instanceof Graph){
//			this.setIdConstraints(((Graph)directedGraph).getIdConstraints());
//		}
	}
	  
	// simple getters
	public Source getSource() { return source; }
	public int getPreprocessing() { return preprocessing; }
	public Graph getPreviousGraph() { return previousGraph; }
	public Graph getNextGraph() { return nextGraph; }
	public Graph getIntraSimGraph() { return intraSimGraph; }
	public MatchResult getIntraMapping() { return intraMapping;	}  
	
	
	// simple setters
	public void setSource(Source source) {	this.source = source; }	
	public void setPreprocessing(int preprocessing) { this.preprocessing = preprocessing; }
	public void setPreviousGraph(Graph previousGraph) { this.previousGraph = previousGraph; }
	public void setNextGraph(Graph nextGraph){ this.nextGraph = nextGraph; }
	public void setIntraMapping(MatchResult intraMapping){ this.intraMapping = intraMapping; }
	public void setIntraSimGraph(Graph intraSimGraph) { this.intraSimGraph = intraSimGraph; }

	
	
	/**
	 * @param preprocessing
	 * @return string representation for the given preprocessing id
	 */
	public static String preprocessingToString(int preprocessing) {
		switch (preprocessing) {
			case PREP_LOADED : return "LOADED";
			case PREP_RESOLVED : return "RESOLVED";
			case PREP_REDUCED : return "REDUCED";
			case PREP_SIMPLIFIED : return "SIMPLIFIED";
			default : return "LOADED";
		}
	}

	/**
	 * @param preprocessingStr
	 * @return preprocessing id for the given preprocessing string
	 */
	public static int stringToPreprocessing(String preprocessingStr) {
		if (preprocessingStr == null) return PREP_LOADED;
		if (preprocessingStr.equals("LOADED")) return PREP_LOADED;
		if (preprocessingStr.equals("RESOLVED")) return PREP_RESOLVED;
		if (preprocessingStr.equals("REDUCED")) return PREP_REDUCED;
		if (preprocessingStr.equals("SIMPLIFIED")) return PREP_SIMPLIFIED;
		return PREP_LOADED;
	}
	

	public boolean equals(Object object){
		if (object==null) return false;
		if (!(object instanceof Graph)) return false;
		return ((Graph)object).getSource().equals(source);
	}
	 
	
	/**
	 * @param kind
	 * @return list of elements of the given kind e.g. all simple type, global complex types
	 */
	private ArrayList<Object> getElementsOfKind(int kind){
	    if (preprocessing!=PREP_LOADED) {
	        System.out.println("getAllElements(): Warning: Not LOADED preprocessing!");
	      }
	      ArrayList<Object> elements = new ArrayList<Object>();
	      Iterator iterator = null;
	        switch (kind) {
			case ALL_ELEMENTS:
			case ALL_SIMPLETYPE:
			case ALL_COMPLEXTYPE:
				iterator = getElementIterator();
				break;
			case GLOBAL_ELEMENTS:
			case GLOBAL_SIMPLETYPE:
			case GLOBAL_COMPLEXTYPE:
				iterator = getRoots().iterator();
				break;
			default:
					return null;
	        }
//	        System.out.println();
	        
	      while (iterator.hasNext()) {
//	    	  System.out.print(".");
	        Element elem = (Element)iterator.next();
	        int elemType = elem.getKind();
	        switch (kind) {
			case ALL_ELEMENTS:
		        if (elemType!=Element.UNDEF && (elemType==Element.KIND_ELEMENT || elemType==Element.KIND_GLOBELEM)){
			          elements.add(elem);
		        }
				break;
			case ALL_SIMPLETYPE:
				if (elemType!=Element.UNDEF && (elemType==Element.KIND_ELEMTYPE || elemType==Element.KIND_GLOBTYPE)) {
					Element resolveType = resolveVertexType(elem);
					if (resolveType==null) elements.add(elem);  //seems to be atomic type
				}
				break;
			case ALL_COMPLEXTYPE:
				if (elemType!=Element.UNDEF && (elemType==Element.KIND_ELEMTYPE || elemType==Element.KIND_GLOBTYPE)) {
					if (isInner(elem)) elements.add(elem);
				}
				break;
			case GLOBAL_ELEMENTS:
				if (elemType!=Element.UNDEF && elemType==Element.KIND_GLOBELEM){
			           elements.add(elem);
				}
				break;
			case GLOBAL_SIMPLETYPE:
				if (elemType!=Element.UNDEF && elemType==Element.KIND_GLOBTYPE) {
					Element resolveType = resolveVertexType(elem);
					if (resolveType==null) elements.add(elem); //seems to be atomic type
				}
				break;
			case GLOBAL_COMPLEXTYPE:
				if (elemType!=Element.UNDEF && elemType==Element.KIND_GLOBTYPE) {
					if (isInner(elem)) elements.add(elem);
				}
				break;
			}
	      }
	      if (elements.isEmpty()) return null;
	      return elements;
	}
	
	
	public ArrayList<Object> getAllNodes(){
		return new ArrayList(vertexSet());
	}
	
	public ArrayList<Object> getAllElements(){
		return getElementsOfKind(ALL_ELEMENTS);
	}
	public ArrayList<Object> getAllSimpleTypes(){
		return getElementsOfKind(ALL_SIMPLETYPE);
	}
	public ArrayList<Object> getAllComplexTypes(){
		return getElementsOfKind(ALL_COMPLEXTYPE);
	}
	public ArrayList<Object> getGlobalElements(){
		return getElementsOfKind(GLOBAL_ELEMENTS);
	}
	public ArrayList<Object> getGlobalSimpleTypes(){
		return getElementsOfKind(GLOBAL_SIMPLETYPE);
	}
	public ArrayList<Object> getGlobalComplexTypes(){
		return getElementsOfKind(GLOBAL_COMPLEXTYPE);
	}

	/**
	 * print graph information to system output
	 */
	public void printGraphInfo() {
		printGraphInfo(System.out);
	}
	
	void printGraphInfo(PrintStream out) {
		out.println(getGraphInfo());
	}
	
	/**
	 * @return a statistical information, like number of all nodes, roots, inners, leaves and shared,
	 * average number of parents, children, siblings, and number of all paths, inner and leaf paths  
	 */
	public String getGraphInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n" + source + "|" + preprocessingToString(preprocessing));
		buffer.append("\n" + " - All Nodes: " + getElementSet().size());
		ArrayList roots = getRoots();
		buffer.append("\n" + " - Roots: " + (roots!=null?roots.size():0));
		ArrayList inners = getInners();
		buffer.append("\n" + " - Inners: " + (inners!=null?inners.size():0));
		ArrayList leaves = getLeaves();
		buffer.append("\n" + " - Leaves: " + (leaves!=null?leaves.size():0));
		ArrayList shared = getShared();
		buffer.append("\n" + " - Shared: " + (shared!=null?shared.size():0));

		float hasComments =0;
//        int instanceCnt = 0 , indirectCnt = 0,indirectInstancesCnt = 0, indirectOrDirectCnt = 0;
//        int instanceDirectSimple = 0, instanceDirectComplex = 0;
		int maxParentCnt=0, maxChildCnt=0,maxSiblingCnt=0;
		int parentCnt=0, childCnt=0, siblingsCnt=0;	     

		Iterator iterator = getElementIterator();
		while (iterator.hasNext()) {
			Element element=(Element)iterator.next();
			if (element.getComment()!=null && element.getComment().length()>0){
				hasComments++;
			}
			
            /* if (el.getDirectInstancesSimple().size()>0){
            hasInstances++;
            instanceDirectSimple ++;
            instanceCnt += el.getDirectInstancesSimple().size();
//  	   allInstances.addAll(el.getDirectInstancesSimple());
        }
        if (el.getDirectInstancesComplex().size()>0){
            if (el.getDirectInstancesSimple().isEmpty()) hasInstances++;
            instanceDirectComplex++;
            instanceCnt += el.getDirectInstancesComplexMaxSize();
        }
        if (el.getDirectInstancesSimple().size()>0 || el.getDirectInstancesComplex().size()>0
                || el.getIndirectInstancesSimple().size()>0 || el.getIndirectInstancesComplex().size()>0){
            indirectOrDirectCnt++;
        } else {
//  	   // no instances at all
//  	   System.out.println(el.getAccession());
        }
        if (el.getIndirectInstancesSimple().size()>0 ){
            indirectCnt++;
            indirectInstancesCnt += el.getIndirectInstancesSimple().size();
        }
        if (el.getIndirectInstancesSimple().size()>0 || el.getIndirectInstancesComplex().size()>0){
            if (el.getIndirectInstancesSimple().isEmpty())indirectCnt++;
            indirectInstancesCnt += el.getIndirectInstancesComplexMaxSize();
        }    */ // TODO util the implementation of instance of the element
			
			ArrayList parents = getParents(element);
			ArrayList children = getChildren(element);
			ArrayList siblings = getSiblings(element);
			if (siblings!=null) {
				if (siblings.size()>maxSiblingCnt) maxSiblingCnt = siblings.size();
				siblingsCnt += siblings.size();
//				System.out.println(element.getName() + "\t" + siblings.size());
			}
			if (parents!=null) {
				if (parents.size()>maxParentCnt) maxParentCnt = parents.size();
				parentCnt += parents.size();
			}
			if (children!=null) {
				if (children.size()>maxChildCnt) maxChildCnt = children.size();
				childCnt += children.size();
			}
		}
	     
		hasComments = hasComments/getElementCount();

		float averageParentCnt=0, averageChildCnt=0, averageSiblingCnt=0;
		
		int vertexCnt =  getElementCount();
	     if (parentCnt>0 && roots!=null) {
	    	 averageParentCnt = (float)parentCnt/(vertexCnt-roots.size());
	    	 buffer.append("\n" + " - parent Avg: " + averageParentCnt);
	     } else {
	    	 buffer.append("\n" + " - parent Avg: " + "NAN");
	     }
	     if (inners!=null) {
	    	 averageChildCnt= (float)childCnt/inners.size();
	    	 buffer.append("\n" + " - child Avg: " + averageChildCnt);
	     } else {
	    	 buffer.append("\n" + " - child Avg: " + "NAN");
	     } 
	     averageSiblingCnt = (float)siblingsCnt/(vertexCnt);
//	     buffer.append("\n" + " - sibling Cnt: " + siblingsCnt);
	     buffer.append("\n" + " - sibling Avg: " + averageSiblingCnt);
		
	     
	     int maxPathLen=Integer.MIN_VALUE;
	     int pathCnt = 0;
	     int innerPathCnt = 0;
	     int leafPathCnt = 0;
	     int pathLenCnt = 0;
	     if (roots!=null)
		     for (int i=0; i<roots.size(); i++) {
		       Element root = (Element)roots.get(i);
		       // BIG QUESTION - HOW TO USE PATH/GRAPHPATH
		       // how to generate it from a graph/split them into...
		       ArrayList<Path> paths = pathBreadthFirstTraversalDown(root);
		       if (paths!=null) {
		         pathCnt += paths.size();
		         for (int j=0; j<paths.size(); j++) {
		        	 Path path = paths.get(j);
		           if (path.size()>maxPathLen) maxPathLen = path.size();
		           pathLenCnt += path.size();
		           Element last = path.getLastElement();
		           if (isInner(last)) innerPathCnt++;
		           else leafPathCnt++;
		         }
		       }
		     }
//	     out.println(" - Max path length: " + maxPathLen + "; Avg: " + (float)pathLenCnt/pathCnt);
	     buffer.append("\n" + " - path Avg: " + (float)pathLenCnt/pathCnt);
	     buffer.append("\n" + " - All path count: " + pathCnt);
	     buffer.append("\n" + " - Inner path count: " + innerPathCnt);
	     buffer.append("\n" + " - Leaf path count: " + leafPathCnt);
	     buffer.append("\n" + " - Comments Avg: " + hasComments);
	     
//	     if (instanceCnt>0){
//	    	 buffer.append("\n" + " - Nodes with direct / indirect / indirect+direct Instances : " + (int) hasInstances + " / " + indirectCnt + " / " + indirectOrDirectCnt);
//	    	 buffer.append("\n" + " - Nodes with direct simple / complex Instances : " + instanceDirectSimple + " / " + instanceDirectComplex);
//	    	 buffer.append("\n" + " - Instances (with Duplicates) : " + instanceCnt );
//	    	 averageInstanceCnt = instanceCnt/hasInstances;
//	    	 hasInstances = hasInstances/getElementCount();
//	    	 buffer.append("\n" + " - Nodes Having Instances: " + hasInstances);
//	    	 buffer.append("\n" + " - Instances Avg: " +averageInstanceCnt);
//	     }
		
		return buffer.toString();
	}	
	
	
	/**
	 * print schema information to system output
	 */
	public void printSchemaInfo() {
		printSchemaInfo(System.out);
	}
	   
	/**
	 * @param out 
	 * print information regarding the type of elements (global/all) elements/simple types/complex types 
	 */
	public void printSchemaInfo(PrintStream out) {
		if (preprocessing!=PREP_LOADED) {
			System.out.println("printLoadedSchemaInfo(): Warning: Not LOADED preprocessing!");
		}
		ArrayList globalElements = getGlobalElements();
		ArrayList allElements = getAllElements();
		ArrayList globalSimpleTypes = getGlobalSimpleTypes();
		ArrayList allSimpleTypes = getAllSimpleTypes();
		ArrayList globalComplexTypes = getGlobalComplexTypes();
		ArrayList allComplexTypes = getAllComplexTypes();

		out.println(source + "|" + preprocessingToString(preprocessing));
		out.println(" - All components: " + getElementSet().size() + " vertices");
		out.println(" - Global elements: " + (globalElements!=null?globalElements.size():0));
		out.println(" - All elements: " + (allElements!=null?allElements.size():0));
		out.println(" - Global simpleTypes: " + (globalSimpleTypes!=null?globalSimpleTypes.size():0));
		out.println(" - All simpleTypes: " + (allSimpleTypes!=null?allSimpleTypes.size():0));
		out.println(" - Global complexTypes: " + (globalComplexTypes!=null?globalComplexTypes.size():0));
		out.println(" - All complexTypes: " + (allComplexTypes!=null?allComplexTypes.size():0));
	}
	
	
	  /**
	 * @param path
	 * @return true if the given path is a valid path in this graph (thus all vertices exists and are connected by (at least) the same edges)
	 */
	public boolean isPathValid(Path path) {
		    if (path==null) return false;
		    //System.out.println("Check path: " + GMGraphUtil.getObjectsFromVertices(path));
		    Element parent = path.getFirstElement();
		    while(parent!=null){
		    	Element next = path.getNext(parent);
		    	if (next==null){
		    		break;
		    	}
		    	if (! containsVertex(next)) return false;
		    	ArrayList children = getChildren(parent);
		    	if (children==null || !children.contains(next))
		    		return false;
		    	parent = next;
		    }
		    return true;
	  }

	
	  /**
	 * @param name
	 * @return a list of elements having the given name
	 */
	public ArrayList<Element> getElementsWithName(String name) {
	    if (name==null) return null;
	    Iterator<Element> iterator = getElementIterator();
	    ArrayList<Element> vertices = new ArrayList<Element>();
	    while (iterator.hasNext()) {
	    	Element elem = iterator.next();
	    	String eName = elem.getName();
	    	if (eName!=null && eName.equals(name)) {
	    		vertices.add(elem);
	      }
	    }
	    if (vertices.isEmpty()) return null;
	    return vertices;
	  }
	  
	  /**
	 * @param acc
	 * @return a list of elements having the given accession
	 */
	public ArrayList<Element> getElementsWithAccession(String acc) {
		    if (acc==null) return null;
		    Iterator<Element> iterator = getElementIterator();
		    ArrayList<Element> vertices = new ArrayList<Element>();
		    ArrayList<Element> verticesEnd = new ArrayList<Element>();
		    while (iterator.hasNext()) {
		    	Element elem = iterator.next();
		    	String eAcc = elem.getAccession();
		    	if (eAcc==null) continue;
		    	if (eAcc.equals(acc)) {
		    		vertices.add(elem);
		    	}
		    	if (eAcc.endsWith(acc)) {
		    		verticesEnd.add(elem);
		    	}
		    }
		    if (vertices.isEmpty()) vertices.addAll(verticesEnd);
		    
		    if (vertices.isEmpty()) return null;
		    return vertices;
	}
	
	  /**
	 * @param acc
	 * @return a list of elements having the given accession
	 */
	static public Path getPathWithString(String string, ArrayList<Path> paths) {
		    if (string==null) return null;
		    Iterator<Path> iterator = paths.iterator();
		    while (iterator.hasNext()) {
		    	Path path = iterator.next();
		    	String ur = path.getUniqueRepresentation();
		    	if (ur!=null && ur.equals(string)) {
		    		return path;
		      }
		    }
		    return null;
	}

	
	  /**
	 * @param idStr
	 * @return a path that corresponds to the given id string representation
	 * returns null if no such valid path exists in the graph
	 */
	public Path idStringToPath(String idStr) {
		    if (idStr==null) return null;
		    StringTokenizer tokens = new StringTokenizer(idStr, ".");
		    Path path = new Path(this);
		    while (tokens.hasMoreTokens()) {
		      String token = tokens.nextToken();
		      Element vertex=null;
			try {
				vertex = getElementWithId(Integer.parseInt(token));
			} catch (NumberFormatException e) {
				// Do nothing - not valid id string - return null
//				e.printStackTrace();
			}
		      if (vertex==null) {
		         //System.out.println("idStringToPath(): No vertex with id " + token + " found");         
		      }
		      else path.add(vertex);      
		    }
		    if (path.isEmpty()) return null;
		    return path;
	  }
	  
	  //Return one valid path
	  /**
	 * @param nameStr
	 * @return a valid path from the graph that is represented by the given
	 * input string, if input empty or no such path can be found than
	 * return null 
	 */
	public Path nameStringToPath(String nameStr) {
	    if (nameStr==null) return null;
	    StringTokenizer tokens = new StringTokenizer(nameStr, ".");
	    ArrayList<Path> validPaths = new ArrayList<Path>();
	    while (tokens.hasMoreTokens()) {
	      String token = tokens.nextToken();
	      ArrayList vertices = getElementsWithName(token);
	      if (vertices==null) return null;

	      ArrayList<Path> newValidPaths = new ArrayList<Path>();
	      if (validPaths.isEmpty()) {
	        for (int i=0; i<vertices.size(); i++) {
	          Element vertex = (Element)vertices.get(i);
	          Path newPath = new Path(this,vertex);
	          newValidPaths.add(newPath);
	         }
	      }
	      else {
	        for (int i=0; i<validPaths.size(); i++) {
	        	Path validPath = validPaths.get(i);
	          for (int j=0; j<vertices.size(); j++) {
	        	  Element vertex = (Element)vertices.get(j);
	            Path newPath = new Path(validPath);
	            newPath.add(vertex);

	            //check path with the graph structure
	            if (isPathValid(newPath)) newValidPaths.add(newPath);
	          }
	        }
	      }
	      if(newValidPaths.isEmpty()) return null;
	      validPaths = newValidPaths;
	    }
	    if (validPaths.isEmpty()) return null;
	    //only one valid path expected
	    return validPaths.get(0);
	  }
	
	  //All paths from an upperVertex to a lowerVertex
	  public ArrayList<Path> getPaths(Element upperVertex, Element lowerVertex) {
	    if (lowerVertex==null || upperVertex==null) return null;
	    ArrayList<Path> upPaths = new ArrayList<Path>();
	    ArrayList<Path> paths = pathDepthFirstTraversalUp(lowerVertex);
	    if (paths!=null) {
	      for (int i=0; i<paths.size(); i++) {
	    	  Path path = paths.get(i);
	    	  Path subPath = path.getSubPath(upperVertex);
	    	  if (subPath!=null && !upPaths.contains(subPath)) {
	    		  upPaths.add(subPath);
	        }
	      }
	    }
	    if (upPaths.isEmpty()) return null;
	    return upPaths;
	  }
	
	
	/**
	 * check for cycles and if there exists one or more resolve them 
	 * by deleting edge that are part of the cycle 
	 */
	public void checkGraphCycles(){
		CycleDetector<Element, Edge> detector = new CycleDetector<Element, Edge>(this); 
		 Set<Element> set = detector.findCycles();
		 if (set==null || set.isEmpty()){
//			 if (verbose) System.out.println("checkGraphCycles(): no cycles in " + this.getSource().getName());
			 return;
		 }
//		 if (verbose)
//			 System.out.println("checkGraphCycles(): cycles in " + this.getSource().getName());
		
		while (set!=null && !set.isEmpty()){
			// only for ontologies and xml schemas 
			ArrayList<Element> list = new ArrayList<Element>(set);
		    Collections.sort(list,new ElementComparator());
			Element element = list.get(list.size()-1);
			Set<Element> cycle = detector.findCyclesContainingVertex(element);
			if (cycle.size()==1){
				// self loop, remove edge to resolve cycle
				removeEdge(element, element);
			} else {
				ArrayList<Element> parents = getParents(element);
				if (parents!=null){
					// only look at the elements that are part of the cycle
					parents.retainAll(list);
					if (parents.size()==1){
						// only one parent - remove edge to hopefully resolve cycle
						removeEdge(parents.get(0), element);
						set = detector.findCycles();
						continue;
					}
				}
				ArrayList<Element> children = getChildren(element);
				if (children!=null && (parents==null || parents.size()>1)){
					// only look at the elements that are part of the cycle				
					children.retainAll(list);
					if (children.size()==1){
						// only one child - remove edge to hopefully resolve cycle
						removeEdge(element, children.get(0));
						set = detector.findCycles();
						continue;
					}					
					if (parents!=null && parents.size()>1 ){
						// several parents and several children, calculate overlap
						children.retainAll(parents);
							// take first - remove edge to hopefully resolve cycle
							removeEdge(element, children.get(0));
							set = detector.findCycles();
							continue;
					}
				}				
				System.out.println("Error: Cycle not solved");
			}
			// detect if still cycles exist
			set = detector.findCycles();
		}
//		 System.out.println();
	}
	

	 /**
	 * @return all types which are not resolvable in the current schema graph
	 * Problem: Resolution in XSD only with global components => check in Load operation
	 */
	public HashMap<String, HashSet<String>> getUnresolvableTypes() {  		  
		  HashMap<String, HashSet<String>> newTypespaces = new HashMap<String, HashSet<String>>();		  
	    //Determine all element names, types and namespaces
	    Iterator<Element> iterator = getElementIterator();
	    HashSet<String> names = new HashSet<String>();
	    ArrayList<String> types = new ArrayList<String>();
	    ArrayList<String> typespaces = new ArrayList<String>();
	    while (iterator.hasNext()) {
	    	Element elem = iterator.next();
	      String name = elem.getName();
	      String type = elem.getType();
	      String typespace = elem.getTypespace();
	      if (! (name==null || names.contains(name) || name.equals(type)))
	        names.add(name);
	      if (! (type==null || types.contains(type))) {
	        types.add(type);
	        typespaces.add(typespace);
	      }
	    }
	    System.out.println("types " + types.size() + "  typespaces " + typespaces.size());
	    //Identify new types by comparing against name
	    //Sort according to namespaces of the types
	    for (int i=0; i<types.size(); i++) {
	      String type = types.get(i);
	      String typespace = typespaces.get(i);
	      if (! names.contains(type)) {
	        //new type	    	  
	    	  HashSet<String>  nspaceTypes = newTypespaces.get(typespace);
	    	  if (nspaceTypes==null){
	    		  nspaceTypes = new HashSet<String>();
	    		  newTypespaces.put(typespace, nspaceTypes);
	    	  }	    	  
	    	  nspaceTypes.add(type);
	      }
	    }
	    if (verbose)
		    for (Iterator<String> iterator2 = newTypespaces.keySet().iterator(); iterator2.hasNext();) {
				String typespace =  iterator2.next();
				HashSet<String> typenames = newTypespaces.get(typespace);
				System.out.println(typespace + " " + typenames.size() );    	
			}	    
	    return newTypespaces;
	  }
	  
	  /**
	 * @param prep
	 * @return graph representation in the given preprocessing
	 */
	public Graph getGraph(int prep) {
	    if (prep == preprocessing) return this;
	    if (prep>preprocessing) {
	    	Graph next = getNextGraph();
	      while (next!=null && next.getPreprocessing()!=prep)
	        next = next.getNextGraph();
	      return next;
	    }
	    Graph prev = getPreviousGraph();
	    while (prev!=null && prev.getPreprocessing()!=prep)
	      prev = prev.getPreviousGraph();
	    return prev;
	  }
	  

	  

	  
	/**
	 * @param element
	 * @return Find the node representing a type of vertex, look in direct children
	 * assuming the referenced type has been resolved to the referencing node
	 */
	public Element findVertexType(Element element) {
	    if (element==null) return null;
	    ArrayList<Element> children = getChildren(element);
	    if (children==null) return null;
	    String typeName = element.getType();
	    if (typeName==null) return null;
	    for (int i=0; i<children.size(); i++) {
	    	Element child = children.get(i);
	      String childName = child.getName();
	      if (childName!=null && childName.equals(typeName)) return child;
	    }
	    return null;
	  }
	  
	  /**
	 * @return graph containing the type hierarchies
	 */
	public Graph findTypeHierarchies() {
		     Iterator<Element> iterator = getElementIterator();
		     Graph typeHierarchies = new Graph();
		     while (iterator.hasNext()) {
		       Element element = iterator.next();
		       if (! typeHierarchies.containsVertex(element)) {
		    	   Element type = findVertexType(element);
		         if (type!=null) {
		           try {
		             typeHierarchies.addVertex(element);
		           }
		           catch (Exception e) { System.out.println("findTypeHierarchies(): Adding vertex " + e.getMessage()); }
		           while (type!=null) {
		             try {
		               if (! typeHierarchies.containsVertex(type))
		                 typeHierarchies.addVertex(type);
		               Edge edge = getEdge(element, type);
		               if (! typeHierarchies.containsEdge(edge))
		                 typeHierarchies.addEdge(element, type);
		             }
		             catch (Exception e) { System.out.println("findTypeHierarchies(): Adding types " + e.getMessage()); }
		             element = type;
		             type = findVertexType(element);
		           }
		         }
		       }
		     }
		     if (typeHierarchies.getElementCount()==0) return null;
		     return typeHierarchies;
		   }



	   
	   

	   

	  /**
	 * @param element
	 * @return Element that resolves the type of the given element, find in the upper part of the 
	 * graph thelowest vertex, which has the name as the type of the vertex to be resolved
	 */
	public Element resolveVertexType(Element element) {
	    if (element==null) return null;
	    String type = element.getType();
	    if (type==null) return null;
	    
	    int sourceId = element.getSourceId();
	    String typespace = element.getTypespace();

	    boolean verbose = false;
	    //String textRep = elem.getTextRep();
	    //if (textRep.equals("Language") || textRep.equals("CorrespondenceLanguage"))
	    //  verbose=true;

	    if (verbose) System.out.println("resolveVertexType(): Process vertex " + element + " with typespace " + typespace);
	    if (verbose) System.out.println(" - Find resolve candidates (1): Siblings of ascendants with name = type");
	    //Identify all possible candidates by traversing up the tree
	    //Only siblings of the parents of the vertex can be used for resolution
	    ArrayList<Element> resolveCands = new ArrayList<Element>();
	    ArrayList siblings = null;
	    ArrayList parents = getParents(element);
	    Element parent = element;
	    do {
	      siblings = getSiblings(parent);
	      //if (verbose) System.out.println(" - Check siblings: " + GMGraphUtil.getObjectsFromVertices(siblings) + " for parent: " + parent.getObject());
	      if (siblings!=null) {
	        for (int i=0; i<siblings.size(); i++) {
	          Element siblingElem = (Element)siblings.get(i);
	          String siblingName = siblingElem.getName();
	          String siblingType = siblingElem.getType();
	          if (siblingName==null){
	           	  // something is wrong with the import
	        	  System.out.print("resolveVertexType(): siblingName = null");
	        	  continue;
	          }  
	          //check name=type
	          if (siblingName.equals(type) &&
	              (siblingType==null || !siblingName.equals(siblingType))) {  //avoid unamed element referencing other element
	            resolveCands.add(siblingElem);
	          }
	        }
	      }
	      if (parents!=null) {
	        parent = (Element)parents.get(0);
	        parents = getParents(parent);
	      }
	      else parent = null;
	    }
	    while (parent!=null);

	    //Already traversed up to schema roots, only find further candidates if none found yet 
	    if (resolveCands.isEmpty()) {
	      if (verbose) System.out.println(" - Find resolve candidates (2): Take all vertices with name=type");
	      //return null;
	      
	      //next attempt: search for all vertices with name equal the type
	      Iterator iterator = getElementIterator();
	      while (iterator.hasNext()) {
	    	  Element v = (Element)iterator.next();
	          if (type.equals(v.getName())) resolveCands.add(v);
	      }
	    }    
	    
	    //Really no resolve candidates
	    if (resolveCands.isEmpty()) {
	        if (verbose) System.out.println(" - No resolve candidates after both steps");
	        return null;
	    }
	    
	    Element resolveElem = null;
//	    String resolveNamespace;
	    int resolveSourceId;    
	    if (verbose) {
	      System.out.println(" - Possible candidates: " + resolveCands);
	    }
	    for (int i=0; i<resolveCands.size(); i++) {
	      resolveElem = resolveCands.get(i);
//	      resolveNamespace = resolveElem.getNamespace();
	      resolveSourceId = resolveElem.getSourceId();
	      if (verbose) System.out.println(" - Check candidat " + resolveElem + " of source " + resolveSourceId /* + ":" + resolveNamespace*/);
	      if (typespace!=null) {
	        //Only candidate with namespace=typespace
//	        if (resolveNamespace!=null && resolveNamespace.equals(typespace)) {
	          if (verbose) System.out.println(" - Take candidate " + resolveElem /* +" found in namespace " + resolveNamespace*/);
	          return resolveElem;
//	        }
	      }
	      else {
	        //No typespace is given, only candidate in the same source
	        if (sourceId==resolveSourceId) {
	          if (verbose) System.out.println(" - Take candidate " + resolveElem + " found in same source " + sourceId);
	          return resolveElem;
	        }
	      }
	    }
	    resolveElem = resolveCands.get(0);
//	    resolveNamespace = resolveElem.getNamespace();
	    resolveSourceId = resolveElem.getSourceId();
	    if (verbose) System.out.println(" - Take first candidate " + resolveElem + " found in source " + resolveSourceId /* + ":" + resolveNamespace*/);
	    return resolveElem;
	  }


    /**
     * @return a function to extract the element names
     */
    public ArrayList getElementNames()
    {
	 if (elementNames!=null) return elementNames;
	   ArrayList elementNames = new ArrayList<String>();
	   Iterator iterator = getElementIterator();
       while (iterator.hasNext()) {
           Element element=(Element) iterator.next();
           if (element!=null && element.getName()!=null){
        	   elementNames.add(element.getName());
           }
       }
	   return elementNames;
   }


    /**
     * @return a list with the most frequent characters of element names
     */
    public ArrayList<Character> frequentCharacters(){
            ArrayList<Character> characters = new ArrayList<Character>();
//    	a = 97, b = 98, ..., z = 122
            int[] count = new int[200];
            for (Iterator iterator = this.getElementIterator(); iterator.hasNext();) {
                Element element=(Element)iterator.next();
                String textRep = element.getName();
//    		 char[] chars = textRep.toLowerCase().replaceAll("[^a-zA-Z]", "").toCharArray();
                char[] chars = textRep.toLowerCase().replaceAll("[^a-z0-9]", "").toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    int value = chars[i];
                    count[value]=count[value]+1;
                }
            }
            for (int j = 0; j < frequentCharSize; j++) {
                int maxValue=0;
                int maxIndex =0;
                for (int i = 0; i < count.length; i++) {
                    if (count[i]>maxValue){
                        maxValue = count[i];
                        maxIndex=i;
                    }
                }
                char c = (char) maxIndex;
                characters.add(new Character(c));
                count[maxIndex]=0;
            }
            return characters;
        }

        /**
         * @return string that contains number of nodes having a type / number of nodes, the number of 
         * datatypes and the actual list of datatypes 
         */
        public String analyseDatatypes(){
            HashMap<String, Integer> countTypes = new HashMap<String, Integer>();
            int all = 0;
            for (Iterator iterator = this.getElementIterator(); iterator.hasNext();) {
               Element element = (Element) iterator.next();
                String type = element.getType();
                if (type==null){
                    continue;
                }
                all++;
                Integer count = countTypes.get(type);
                if (count==null){
                    count = new Integer(1);
                } else {
                    count = new Integer(count.intValue()+1);
                }
                countTypes.put(type, count);
            }
            return all + "/" + this.getElementCount() + ", " + countTypes.size() + " Types " + countTypes.toString();
        }

    /**
     * @return a list containing the most frequent starting character of element names
     */
    public ArrayList<Character> frequentStartingCharacters(){
    	ArrayList<Character> characters = new ArrayList<Character>();
//    	a = 97, b = 98, ..., z = 122
		int[] count = new int[200];
    	for (Iterator iterator = this.getElementIterator(); iterator.hasNext();) {
    		Element element = (Element) iterator.next();

    		String textRep = element.getName();
//    		char[] chars = textRep.toLowerCase().replaceAll("[^a-zA-Z]", "").toCharArray();
    		String[] chars = textRep.toLowerCase().replaceAll("[^a-z ]", "").split(" ");
    		if (chars==null || chars.length==0 || (chars.length==1 &&  chars[0].isEmpty())){
    			continue;
    		}
    		for (int i = 0; i < chars.length; i++) {
    			char c = chars[i].charAt(0);
				int value = c;
				count[value]=count[value]+1;
			}
		}
    	for (int j = 0; j < frequentCharSize; j++) {
    		int maxValue=0;
    		int maxIndex =0;
	    	for (int i = 0; i < count.length; i++) {
	    		if (count[i]>maxValue){
	    			maxValue = count[i];
	    			maxIndex=i;
	    		}
	    	}
	    	if(maxValue>0){
	    		char c = (char) maxIndex;
	    		characters.add(new Character(c));
	    		count[maxIndex]=0;
	    	}
    	}
    	if (characters.isEmpty()){
    		return null;
    	}
    	return characters;
    }
    
    
      
	public void clearInstances(){
		hasInstances=Integer.MIN_VALUE;
		averageInstanceCnt=Integer.MIN_VALUE;
        Iterator<Element> it= getElementIterator();
		while (it.hasNext()) {
		       Element el=it.next();
		       el.clearInstances();
		}
	}

	   public float getHasInstances(){ 
		   if (hasInstances!=Integer.MIN_VALUE) return hasInstances;
		     Iterator iterator = getElementIterator();
		     hasInstances=0;
		     int instanceCnt=0;
			while (iterator.hasNext()) {
		       Element el=(Element)iterator.next();
		       if (el.getDirectInstancesSimple().size()>0){
		    	   hasInstances++;
		    	   instanceCnt += el.getDirectInstancesSimple().size();
		       } else  if (el.getDirectInstancesComplex().size()>0){
		    	   hasInstances++;
		    	   instanceCnt += el.getDirectInstancesComplexMaxSize();
		       }
		     }
		     averageInstanceCnt = instanceCnt/hasInstances;
		     hasInstances = hasInstances/getElementCount();
		   return hasInstances;
	   }
	
	public float getAverageInstanceCnt(){ 
		if (averageInstanceCnt!=Integer.MIN_VALUE) return averageInstanceCnt;
		getHasInstances();
		return averageInstanceCnt;
	}
    
	public ArrayList<HashSet<Element>> getConnectedSets(){
		ArrayList<HashSet<Element>> sets = new ArrayList<HashSet<Element>>();
		HashSet<Element> vertices = new HashSet<Element>(vertexSet());
		while(!vertices.isEmpty()){
			Element vertex = vertices.iterator().next();
			HashSet<Element> set = new HashSet<Element>();
			List<Element> adjacents = Graphs.neighborListOf(this, vertex);
			if (adjacents!=null && !adjacents.isEmpty()) {
				// if adjacents nodes add them to the set
				set.addAll(adjacents);
			} else {
				// the set contains at minimum the node itself
				set.add(vertex);
			}
	        // adding the new set to the set of connected sets
			sets.add(set);
	        // removing elements from the set that still have to be visited
			vertices.removeAll(set);
		}
	 return sets;
	}

	public HashSet<Element> getConnectedSet(Element vertex){
		HashSet<Element> set = new HashSet<Element>();
		List<Element> adjacents = Graphs.neighborListOf(this, vertex);
		if (adjacents!=null && !adjacents.isEmpty()) {
			// if adjacents nodes add them to the set
			set.addAll(adjacents);
		} else {
			// the set contains at minimum the node itself
			set.add(vertex);
		}
	 return set;
	}

	

}