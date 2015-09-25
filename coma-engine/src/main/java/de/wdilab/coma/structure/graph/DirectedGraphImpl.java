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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;

import de.wdilab.coma.structure.Edge;
import de.wdilab.coma.structure.EdgeFactoryImpl;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Path;

/**
 * This class extends DefaultDirectedGraph. A default directed graph is a 
 * non-simple directed graph in which multiple edges between any two
 * vertices are not permitted, but loops are.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class DirectedGraphImpl extends DefaultDirectedGraph<Element, Edge>{

	final static int ROOT = 1;
	final static int INNER = 2;
	final static int LEAF = 3;
	final static int SHARED = 4;
	
	
	final static int CHILDREN = 5;
	final static int PARENTS = 6;
	final static int SIBLINGS = 7;
	
	public DirectedGraphImpl(){
//		super( Edge.class);
		super(new EdgeFactoryImpl());
	}
	
	@SuppressWarnings("unchecked")
	public DirectedGraphImpl(Class edgeClass) {
		super(edgeClass);
	}

	
    // simple getter
	public Set<Element> getElementSet(){	return vertexSet();	}	
	
	public Set<Edge> getEdgeSet() { return edgeSet(); }
	
	
	public Iterator<Element> getElementIterator(){
		return getElementSet().iterator();
	}
	
	public int getElementCount(){
		return vertexSet().size();
	}
	
	public int getPathCount(){
		return getAllPaths().size();
	}
	
	public int getEdgesCount(){
		return edgeSet().size();
	}
	
	public void removeElement(Element element){
		removeVertex(element);
	}
	
	public int getInnerNodesCount(){
		ArrayList<Element> nodes = getInners();
		if (nodes==null) return 0;
		return nodes.size();
	}
	
	public int getLeafNodesCount(){
		ArrayList<Element> nodes = getLeaves();
		if (nodes==null) return 0;
		return nodes.size();
	}
	
	public int getSharedNodesCount(){
		ArrayList<Element> nodes = getShared();
		if (nodes==null) return 0;
		return nodes.size();
	}
	
	public int getRootNodesCount(){
		ArrayList<Element> nodes = getRoots();
		if (nodes==null) return 0;
		return nodes.size();
	}
	
	public int getInnerPathsCount(){
		ArrayList<Path> paths = getInnerPaths();
		if (paths==null) return 0;
		return paths.size();
	}
	
	public int getLeafPathsCount(){
		ArrayList<Path> paths = getLeafPaths();
		if (paths==null) return 0;
		return paths.size();
	}
	
	public int getChildrenCount(){
		int cnt=0;
		Iterator iterator = vertexSet().iterator();
		while (iterator.hasNext()) {
			Element el=(Element)iterator.next();
			ArrayList children = getChildren(el);
			if (children!=null) {
				cnt += children.size();
			}
		}
		return cnt;
	}
	
	public int getParentsCount(){
		int cnt=0;
		Iterator iterator = vertexSet().iterator();
		while (iterator.hasNext()) {
			Element el=(Element)iterator.next();
			ArrayList parents = getParents(el);
			if (parents!=null) {
				cnt += parents.size();
			}
		}
		return cnt;
	}
	
	public int getSiblingsCount(){
		int cnt=0;
		Iterator iterator = vertexSet().iterator();
		while (iterator.hasNext()) {
			Element el=(Element)iterator.next();
			ArrayList siblings = getSiblings(el);
			if (siblings!=null) {
				cnt += siblings.size();
			}
		}
		return cnt;
	}
	

	
	/**
	 * @return all the paths that exist in the graph beginning with one of the root nodes
	 */
	public ArrayList<Path> getAllPaths(){
	    ArrayList<Element> roots = getRoots();
	    ArrayList<Path> paths = new ArrayList<Path>();
	    if (roots!=null) {
	      for (int i=0; i<roots.size(); i++) {
	    	  Element root = roots.get(i);
	    	  ArrayList<Path> newPaths = pathBreadthFirstTraversalDown(root);
	    	  if (newPaths!=null) paths.addAll(newPaths);
	      }
	    }
	    if (paths.isEmpty()) return null;
	    return paths;
	}
	 
	/**
	 * @return all the paths beginning with a root node and ending with an inner node
	 * (an inner node is a node that has one or more children)
	 */
	public ArrayList<Path> getInnerPaths(){
	    ArrayList<Element> roots = getRoots();
	    ArrayList<Path> paths = new ArrayList<Path>();
	    if (roots!=null) {
	      for (int i=0; i<roots.size(); i++) {
	    	  Element root = roots.get(i);
	    	  ArrayList<Path> newPaths = getDownInnerPaths(root);
	    	  if (newPaths!=null) paths.addAll(newPaths);
	      }
	    }
	    if (paths.isEmpty()) return null;
	    return paths;
	}
	
	/**
	 * @param element
	 * @return all up paths ending at a root node
	 */
	public ArrayList<Path> getUpRootPaths(Element element){
		if (element==null) return null;
	    ArrayList<Path> rootPaths = new ArrayList<Path>();
	    ArrayList<Path> allPaths = pathDepthFirstTraversalUp(element);
	    if (allPaths!=null) {
	    	for (int i=0; i<allPaths.size(); i++) {
	    		Path path = allPaths.get(i);
	        	Element first = path.getFirstElement();
	        	if (isRoot(first)) rootPaths.add(path);
	    	}
	    }
	    if (rootPaths.isEmpty()) return null;
	    return rootPaths;
	}

	/**
	 * @return all the paths beginning with a root node and ending with a leaf node
	 * (a leaf node is a node that has no children)
	 */
	public ArrayList<Path> getLeafPaths(){
	    ArrayList<Element> roots = getRoots();
	    ArrayList<Path> paths = new ArrayList<Path>();
	    if (roots!=null) {
	      for (int i=0; i<roots.size(); i++) {
	    	  Element root = roots.get(i);
	        ArrayList<Path> newPaths = getDownLeafPaths(root);
	        if (newPaths!=null) paths.addAll(newPaths);
	      }
	    }
	    if (paths.isEmpty()) return null;
	    return paths;
	}
	
	/**
	 * @return all the paths beginning with a root node and ending with a leaf node
	 * (a leaf node is a node that has no children)
	 */
	public ArrayList<Path> getSharedPaths(){
	    ArrayList<Element> roots = getRoots();
	    ArrayList<Path> paths = new ArrayList<Path>();
	    if (roots!=null) {
	      for (int i=0; i<roots.size(); i++) {
	    	  Element root = roots.get(i);
	        ArrayList<Path> newPaths = getDownSharedPaths(root);
	        if (newPaths!=null) paths.addAll(newPaths);
	      }
	    }
	    if (paths.isEmpty()) return null;
	    return paths;
	}
	
	
	/**
	 * @return all the paths containing (only) a root node
	 * (a root node is a node that has no parents)
	 */
	public ArrayList<Object> getRootPaths(){
	    ArrayList<Element> roots = getRoots();
	    ArrayList<Object> paths = new ArrayList<Object>();
	    if (roots!=null) {
	      for (int i=0; i<roots.size(); i++) {
	    	  Element root = roots.get(i);
	    	  Path path = new Path(this, root);
	    	  paths.add(path);
	      }
	    }
	    if (paths.isEmpty()) return null;
	    return paths;
	}
	
	  
	public Element getElementWithId(int id) {
		if (id==Element.UNDEF) return null;
		Iterator<Element> iterator = getElementSet().iterator();
		while (iterator.hasNext()) {
			Element element = iterator.next();
			if (element.getId()==id) return element;
		}
		return null;
	}
	
	  //---------------------------------------------------------------------------//
	  // Roots/Leaves/Children/Parents/Super/Sub                                   //
	  //---------------------------------------------------------------------------//
	  /**
	 * @param element
	 * @return true if the given element is a root (thus doesn't have parents)
	 * note: if graph is cycle, no roots can be detected
	 */
	  public boolean isRoot(Element element) {
	    if (element==null) return false;
	    int fanIn =  inDegreeOf(element);	    
	    if (fanIn==0) return true;
	    return false;
	  }
	  
	  /**
	 * @param element
	 * @return true if the given element is an inner element (thus has children)
	 */
	  public boolean isInner(Element element) {
		    if (element==null) return false;
		    int fanOut =  outDegreeOf(element);
		    if (fanOut>0) return true;
		    return false;
	  }
	  
	  /**
	 * @param element
	 * @return true if the given element is a leaf (thus doesn't have children)
	 */
	  public boolean isLeaf(Element element) {
		    if (element==null) return false;
		    int fanOut =  outDegreeOf(element);
		    if (fanOut==0) return true;
		    return false;
	  }
	
	  /**
	 * @param element
	 * @return true if the given element is shared (thus has 2 ore more parents)
	 */
	public boolean isShared(Element element) {
		    if (element==null) return false;
		    int fanIn =  inDegreeOf(element);	    
		    if (fanIn>1) return true;
		    return false;
	  }	
	
	  /**
	  * @paramkind
	  * @return all elements of a specific type (root, innter, leaf, shared) 
	  */
	  ArrayList<Element> getElementsOfType(int type) {
		  Iterator<Element> iterator = getElementSet().iterator();
		  ArrayList<Element> nodes = new ArrayList<Element>();
		  while (iterator.hasNext()) {
			  Element v = iterator.next();
			  switch (type) {
			  case ROOT:
				  if (isRoot(v)) nodes.add(v);
				  break;
			  case INNER:
				  if (isInner(v)) nodes.add(v);
				  break;
			  case LEAF:
				  if (isLeaf(v)) nodes.add(v);
				  break;
			  case SHARED:
				  if (isShared(v)) nodes.add(v);
				  break;
			  }
		  }
		  if (nodes.isEmpty()) return null;
		  Collections.sort(nodes, new ElementComparator());
		  return nodes;	  
	  }
	
	  /**
	 * @return all elements that are a root (thus have not parents)
	 */
	public ArrayList<Element> getRoots() {
		  return getElementsOfType(ROOT);
	  }
	  
	  /**
	 * @return all elements that are an inner element (thus have children)
	 */
	  public ArrayList<Element> getInners() {
		  return getElementsOfType(INNER);
	  }
	  
	  /**
	 * @return all elements that are a leaf element (thus don't have children)
	 */
	  public ArrayList<Element> getLeaves() {
		  return getElementsOfType(LEAF);
	  }
	  
	  public ArrayList<Element> traverse(Element root){
		  ArrayList<Element> list = new ArrayList<Element>();
		  traverse(list, root);
		  return list;
	  }
	  
	  public ArrayList<Element> traverse(ArrayList<Element> list, Element root){
		  list.add(root);
		  ArrayList<Element> children = getChildren(root);
		  if (children!=null){
			  for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				Element element = (Element) iterator.next();
				traverse(list, element);
			}
		  }
		  return list;
	  }
	  
	  
	  //All leaves subordinate to a given node
	  public ArrayList<Element> getLeaves(Element root) {
	    Iterator iterator = traverse(root).iterator();
	    ArrayList<Element> leaves = new ArrayList<Element>();
	    while (iterator.hasNext()) {
	    	Element v = (Element)iterator.next();
	      if (isLeaf(v) && !v.equals(root) && !leaves.contains(v)) leaves.add(v);
	    }
	    if (leaves.isEmpty()) return null;
	    Collections.sort(leaves, new ElementComparator());
	    return leaves;
	  }
	  
	  //Inner nodes subordinate to a given node
	  public ArrayList<Element> getInners(Element root) {
	    Iterator iterator = traverse(root).iterator();
	    ArrayList<Element> inners = new ArrayList<Element>();
	    while (iterator.hasNext()) {
	    	Element v = (Element)iterator.next();
	      if (isInner(v) && !v.equals(root) && !inners.contains(v)) inners.add(v);
	    }
	    if (inners.isEmpty()) return null;
	    Collections.sort(inners, new ElementComparator());
	    return inners;
	  }
	  
	  /**
	 * @return all elements that are shared (thus have 2 ore more parents)
	 */
	  /**
	 * @return
	 */
	public ArrayList<Element> getShared() {
		  return getElementsOfType(SHARED);
	  }
	  
	  /**
	 * @param element
	 * @return all direct children of a node
	 */
	public ArrayList<Element> getChildren(Element element) {
		  return getElementsOfKind(element, CHILDREN);
	  }
	  
	  /**
	 * @param element
	 * @return all direct parents of a node
	 */
	  public ArrayList<Element> getParents(Element element) {
		  return getElementsOfKind(element, PARENTS);
	  }
	  
	  public ArrayList<Element> getParents(ArrayList<Element> children) {
		    if (children==null) return null;
		    ArrayList<Element> parents = new ArrayList<Element>();
		    for (int i=0; i<children.size(); i++) {
		      Element v = children.get(i);
		      ArrayList<Element> vParents = getParents(v);
		      if (vParents!=null) {
		        vParents.removeAll(parents);
		        parents.addAll(vParents);
		      }
		    }
		    if (parents.isEmpty()) return null;
		    Collections.sort(parents, new ElementComparator());
		    return parents;
	  }
	 
	  /**
	  * @param element
	  * @return all siblings of a node (children of the parents of the given element)
	  * if element is root return all other roots
	  */
	  public ArrayList<Element> getSiblings(Element element) {
		  return getElementsOfKind(element, SIBLINGS);
	  }
	  
	  /**
	 * @param element
	 * @param kind
	 * @return elements related in the given kind to the given element
	 */
	ArrayList<Element> getElementsOfKind(Element element, int kind) {
	    if (element==null) return null;
	    Set<Edge> edges = null; 
	    ArrayList<Element> nodes = new ArrayList<Element>();
	    
    	switch (kind) {
    	case PARENTS:
    		edges = incomingEdgesOf(element);
    		break;
    	case CHILDREN:
    		edges = outgoingEdgesOf(element);
    		break;
    	case SIBLINGS:
    		ArrayList<Element> parents = getParents(element);
    	    if (parents!=null) {
    	    	// get parent and from parents the children
    	    	edges = new HashSet<Edge>();
    	    	for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
					Element element2 = (Element) iterator.next();
					Set<Edge> tmp = outgoingEdgesOf(element2);
					if (tmp!=null) edges.addAll(tmp);
				}
    	    } else {
    	    	//  this element is a root only other roots are their siblings
    	    	nodes = getRoots();
    	    	nodes.remove(element);
    	    	if (nodes.isEmpty()) return null;
    		    Collections.sort(nodes,new ElementComparator());
    	    	return nodes;
    	    }    		
    		break;
    	}
	    
    	// if there are no edges there are no related nodes
	    if (edges==null || edges.isEmpty()) return null;

	    for (Iterator<Edge> iterator = edges.iterator(); iterator.hasNext();) {
			Edge edge = iterator.next();
	    	switch (kind) {
	    	case PARENTS:
	    		Element source = edge.getSource();
	    		if (!nodes.contains(source)) nodes.add(source);
	    		break;
	    	case CHILDREN:
	    	case SIBLINGS:
	    		Element target = edge.getTarget();
	    		if (!nodes.contains(target)) nodes.add(target);
	    		break;
	    	}	
		}
	    // remove element itself (assumption no self circles, thus it is not a parent or child of itself)
	    nodes.remove(element);
	    if (nodes.isEmpty()) return null;
	    Collections.sort(nodes,new ElementComparator());
	    return nodes;
	  }
	
	  /**
	 * @param element
	 * @return all downpaths with an inner ending from an element
	 */
	public ArrayList<Path> getDownPaths(Element element) {
	    if (element==null) return null;
	    ArrayList<Path> allPaths = pathDepthFirstTraversalDown(element);
	    if (allPaths==null || allPaths.isEmpty()) return null;
	    return allPaths;
	  }
	  
	  /**
	 * @param element
	 * @return all downpaths with an inner ending from an element
	 */
	public ArrayList<Path> getDownInnerPaths(Element element) {
	    if (element==null) return null;
	    ArrayList<Path> innerPaths = new ArrayList<Path>();
	    ArrayList<Path> allPaths = pathDepthFirstTraversalDown(element);
	    if (allPaths!=null) {
	      for (int i=0; i<allPaths.size(); i++) {
	        Path path = allPaths.get(i);
	        Element last = path.getLastElement();
	        if (isInner(last)) innerPaths.add(path);
	      }
	    }
	    if (innerPaths.isEmpty()) return null;
	    return innerPaths;
	  }
	  
	  /**
	 * @param element
	 * @return all downpaths with an leaf ending for the given element
	 */
	public ArrayList<Path> getDownLeafPaths(Element element) {
	    if (element==null) return null;
	    ArrayList<Path> leafPaths = new ArrayList<Path>();
	    ArrayList<Path> allPaths = pathDepthFirstTraversalDown(element);
	    if (allPaths!=null) {
	      for (int i=0; i<allPaths.size(); i++) {
	    	  Path path = allPaths.get(i);
	    	  Element last = path.getLastElement();
	        if (isLeaf(last)) leafPaths.add(path);
	      }
	    }
	    if (leafPaths.isEmpty()) return null;
	    return leafPaths;
	  }
	
	  /**
	 * @param element
	 * @return all downpaths with an shared ending for the given element
	 */
	public ArrayList<Path> getDownSharedPaths(Element element) {
	    if (element==null) return null;
	    ArrayList<Path> leafPaths = new ArrayList<Path>();
	    ArrayList<Path> allPaths = pathDepthFirstTraversalDown(element);
	    if (allPaths!=null) {
	      for (int i=0; i<allPaths.size(); i++) {
	    	  Path path = allPaths.get(i);
	    	  Element last = path.getLastElement();
	        if (isShared(last)) leafPaths.add(path);
	      }
	    }
	    if (leafPaths.isEmpty()) return null;
	    return leafPaths;
	  }
	
	   /**
	 * @param parent
	 * @return all nodes subordinate to a given node, i.e. excluding the node itself
	 */
	public ArrayList<Element> getSubNodes(Element parent) {
		ArrayList<Element> subNodes = breadthFirstTraverseDown(parent);
		if (subNodes==null){
			return null;
		}
		subNodes.remove(parent);
		if (subNodes.isEmpty()) return null;
		Collections.sort(subNodes, new ElementComparator());
		return subNodes;
	  }
	  
	  
	/**
	 * @param currentSubGraph
	 * @param root
	 * add to the subgraph the children of the given element and 
	 * follow them iterative to get all their children
	 */
	void getSubGraph(DirectedGraphImpl currentSubGraph, Element root) {
		if (currentSubGraph==null || root==null) return;
		ArrayList<Element> children = getChildren(root);
		if (children!=null) {
			for (int i=0; i<children.size(); i++) {
				Element child = children.get(i);
		        Edge edge = getEdge(root, child);		        
		        try {
		        	currentSubGraph.addVertex(child);
		        	currentSubGraph.addEdge(root, child);
		        }
		        catch (Exception e) {
		        	System.out.println("getSubGraph(): Error with child/edge " + child + "/" + edge);
		        }
		        getSubGraph(currentSubGraph, child);
			}
		}
	  }
	  
	  /**
	 * @param root
	 * @return the subgraph of this graph with the given element as root
	 */
	public DirectedGraphImpl getSubGraph(Element root) {
		  if (root==null) return null;
		  DirectedGraphImpl subGraph = new DirectedGraphImpl();
		  try {
			  subGraph.addVertex(root);
		  }
		  catch (Exception e) {
			  System.out.println("getSubGraph(): Error with root " + root + ": " + e.getMessage());
		  }
		  getSubGraph(subGraph, root);
		  return subGraph;
	  }
	
	  //---------------------------------------------------------------------------//
	  // Printout methods                                                          //
	  //---------------------------------------------------------------------------//
	  //note if graph is cycle, no roots can be detected
	  void print(Element root, int depth, PrintStream out) {
	    if (root == null) return;
	    String pad = "";
	    for (int i=0; i<depth; i++) pad += "   ";
	    //out.println(pad + "-" + root.getLabel() + "[" + ((VertexImpl)root).getObject() + "]");
	    out.println(pad + "-" + root);
	    ArrayList<Element> children = getChildren(root);
	    if (children != null)
	      for (int i=0; i<children.size(); i++)
	        print(children.get(i), depth+1, out);
	  }
	
	  public void print(Element root, PrintStream out) {
		    print(root, 0, out);
	  }
	  
	  public void print() {
		    ArrayList<Element> roots = getRoots();
		    if (roots!=null) {
		      for (int i=0; i<roots.size(); i++)
		        print(roots.get(i), System.out);
		    }
	  }
	//---------------------------------------------------------------------------//
	// Traversal methods                                                         //
	//---------------------------------------------------------------------------//
	
	//Traverse down the directed edges		  	  
	public ArrayList<Path> pathDepthFirstTraversalDown(Element node) {
		if (node == null) return null;
		ArrayList<Path> visitedList = new ArrayList<Path>();
		Path path = new Path(this, node);
		
		pathDepthFirstTraversalDown(node, visitedList, path);
		visitedList.add(path);
		return visitedList;
	}	  

	  
	//Path depth first traversal
	void pathDepthFirstTraversalDown(Element node, ArrayList<Path> visitedList, Path pathPrefix) {
		if (node == null) return;
		ArrayList<Element> children = getChildren(node);
		if (children != null) {
			for (int i=0; i<children.size(); i++) {
				Element child = children.get(i);
				Path childPath = new Path(pathPrefix);
				childPath.add(child);
				pathDepthFirstTraversalDown(child, visitedList, childPath);
				visitedList.add(childPath);
			}
		}
	}
		  
	  		  
	public ArrayList<Element> breadthFirstTraverseDown(Element element) {
		if (element==null) return null;
		ArrayList<Element> visited = new ArrayList<Element>();
		ArrayList<Element> seen = new ArrayList<Element>();
			   
		breadthFirstTraverseDown(element, seen, visited);
		if (visited.isEmpty()) return null;
		return visited;
	}
		  
	public ArrayList<Element> breadthFirstTraverseDown(Element element, ArrayList<Element> seen, ArrayList<Element> visited) {
		if (element==null) return visited;
		if (seen.contains(element)){
			return visited;
		}			    
		seen.add(element);

		Set<Edge> edges = outgoingEdgesOf(element);			    
		for (Edge edge : edges) {
			Element child = edge.getTarget();
			//visited element
			if (!visited.contains(child)){
				visited.add(child);
			}
			breadthFirstTraverseDown(child, seen, visited);
		}
		return visited;
	}
		  
	public ArrayList<Path> pathBreadthFirstTraversalDown(Element node) {
		if (node==null) return null;
		ArrayList<Path> visitedList = new ArrayList<Path>();
		Path path = new Path(this, node);
		pathBreadthFirstTraversalDown(node, visitedList, path);
		visitedList.add(path);
		return visitedList;
	}

	//path breadth first traversal
	void pathBreadthFirstTraversalDown(Element node, ArrayList<Path> visitedList, Path pathPrefix) {
		if (node==null) return;
		ArrayList<Element> children = getChildren(node);
		if (children != null) {
			for (int i=0; i<children.size(); i++) {
				Element child = children.get(i);
				Path childPath = new Path(pathPrefix);
				childPath.add(child);
				visitedList.add(childPath);
			}
			for (int i=0; i<children.size(); i++) {
				Element child = children.get(i);
				Path childPath = new Path(pathPrefix);
				childPath.add(child);
				pathBreadthFirstTraversalDown(child, visitedList, childPath);
			}
		}
	}
	
	public ArrayList<Path> pathDepthFirstTraversalUp(Element node) {
		if (node == null) return null;
		ArrayList<Path> visitedList = new ArrayList<Path>();
		Path path = new Path(this, node);
		pathDepthFirstTraversalUp(node, visitedList, path);
		visitedList.add(path);
		return visitedList;
	}
	
	void pathDepthFirstTraversalUp(Element node, ArrayList<Path> visitedList, Path pathPrefix) {
		if (node == null) return;
		ArrayList<Element> parents = getParents(node);
		if (parents != null) {
			for (int i=0; i<parents.size(); i++) {
				Element parent = parents.get(i);
				Path parentPath = new Path(pathPrefix);
				parentPath.addFirst(parent);
				pathDepthFirstTraversalUp(parent, visitedList, parentPath);
				visitedList.add(parentPath);
			}
		}
	}
	
    public Edge addEdge(Element sourceVertex, Element targetVertex){
    	addVertex(sourceVertex);
    	addVertex(targetVertex);
    	return super.addEdge(sourceVertex, targetVertex);
    }
    
    public Edge addEdge(Element sourceVertex, Element targetVertex, String type){
    	addVertex(sourceVertex);
    	addVertex(targetVertex);
    	Edge edge = super.addEdge(sourceVertex, targetVertex);
    	edge.setType(type);
    	return edge;
    }

}
