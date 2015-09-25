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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;


/**
 * GraphPathImpl contains the implementation for a GraphPath (jgrapht)
 * beside several constructors it allows adding elements whereas 
 * needed edges are automatically added
 * 
 * @author Sabine Massmann
 */
// alternative to Path
public class Path extends AbstractNode implements GraphPath {
	/** edge list that connect the vertices of the path */
    private List<Edge> edgeList;
    /** vertice list that appear in the path */
    private HashSet<Element> vertices;
    /** start vertex - often a root */
    private Element startVertex;
    /** end vertex - an inner or leaf, is the same as vertex if the path only contains one node */
    private Element endVertex;
    /** the graph that contains this path, needed e.g. to provide EdgeFactory */
    private Graph graph;	
    /** id of the path */
    private int id = -1;
    
    //~ Constructors -----------------------------------------------------------

    /**
     * @param graph
     * Constructor of an empty path
     */
    public Path(Graph graph){
    	this.graph = graph;
        startVertex = null;
        endVertex = null;
        edgeList = new ArrayList<Edge>();
        vertices = new HashSet<Element>();
    }
    
    /**
     * @param graph
     * @param startVertex
     * Constructor creating a path with only the given vertex
     */
    public Path(Graph graph, Element startVertex){
    	this.graph = graph;
    	this.startVertex = startVertex;
    	endVertex = startVertex;
    	edgeList = new ArrayList<Edge>();
    	vertices = new HashSet<Element>();
    	vertices.add(startVertex);
    }
    
    /**
     * @param path
     * Constructor that copies the given path
     */
    public Path(Path path){
    	this.graph = path.getGraph();
        this.startVertex = path.getFirstElement();
        this.endVertex = path.getLastElement();
        this.edgeList = new ArrayList<Edge>(path.getEdgeList());
        this.vertices = new HashSet<Element>(path.getVertices());
    }
    
    
    // not used in this graphpath implementation
	public double getWeight() { return 0; }
	
	public Graph getGraph() { return graph; }
	
	public HashSet<Element> getVertices() { return vertices; }
	
	public Object getStartVertex() { return startVertex; }
	public Element getFirstElement(){ return startVertex; }
	
	public Object getEndVertex() {	return endVertex; }
	public Element getLastElement(){ return endVertex; }
	
	public List<Edge> getEdgeList() { return edgeList; }
	
	public int getId() { return id; }
	
	/**
	 * @param element
	 * add new last element and add edge from previous last element to this one
	 */
	public void add(Element element){
		if (startVertex==null){
			startVertex = element;
		} else if (endVertex==null){
			Edge edge = (Edge) graph.getEdgeFactory().createEdge(startVertex, element);
			edgeList.add(edge);	
		} else {
			Edge edge = (Edge) graph.getEdgeFactory().createEdge(endVertex, element);
			edgeList.add(edge);
		}
		endVertex = element;
    	vertices.add(element);
	}
	
	/**
	 * @param element
	 * add new first element and add edge from this element to the previous first element
	 */
	public void addFirst(Element element){
    	vertices.add(element);
		if (startVertex==null){
			startVertex = element;
			endVertex = element;
			return;
		}
		// create new edge from new first element to the old one
		EdgeFactory factory = graph.getEdgeFactory();
		Edge edge = (Edge) factory.createEdge(element, startVertex);
		edgeList.add(0,edge);
		startVertex = element;

	}
	
	
	/**
	 * @param source
	 * @return element that is the next for the given element, returns null if last element
	 */
	public Element getNext(Element source){
		for (int i = 0; i < edgeList.size(); i++) {
			Edge current = edgeList.get(i);
			if (current.getSource().equals(source)){
				// Assumption: no loops with an element several times in an edge 
				return current.getTarget();
			}
		}
		return null; 
	}
	
	public void addEdge(Edge edge){
		if (edge==null) return;
		Element source = edge.getSource();
		if (startVertex==null || startVertex.equals(source)){
			add(source);
		}
		Element target = edge.getTarget();
		if (endVertex==null || endVertex.equals(target)){
			add(target);
		}
		// otherwise don't change start or end node
		vertices.add(source);
		vertices.add(target);
		Edge newEdge = (Edge) graph.getEdgeFactory().createEdge(source, target);
		edgeList.add(newEdge);
	}
	
	
	public Element get(int position){
		// negative position not valid
		if (position<0) return null;
		if (position==0) return getFirstElement();
		int size = size();
		// large/equal size not valid
		if (position>=size) return null;
		// size-1 is last element (because counting from 0)
		if (position==(size-1)) return getLastElement();
		Edge edge = edgeList.get(position);
		// error
		if (edge==null) return null;
		return edge.getSource();
	}
	
	
	/**
	 * @return size thus length of the path
	 */
	public int size(){
		// no start vertex means length 0
		if (startVertex==null) return 0;
		return vertices.size();
	}
	
	/**
	 * @return true if no start vertex
	 */
	public boolean isEmpty(){
		if (size()>0){
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		if (startVertex==null){
			return null;
		}
		String text = toNameString();
		return text;
	}
	
	/**
	 * @return string that concats the elements of this path by a "."
	 */
	public String toNameString(){
		if (startVertex==null){
			return null;
		}
		String text = startVertex.getName();
		if (endVertex==null){
			return text;
		}
		for (int i = 0; i < edgeList.size(); i++) {
			Edge edge = edgeList.get(i);
			text+= "." + edge.getTarget().getName();
		}
		return text;
	}
	
	/**
	 * @return string that concats the elements of this path by a "."
	 */
	public String toNameSynString(){
		if (startVertex==null){
			return null;
		}
		String text = startVertex.getNameSyn();
		if (endVertex==null){
			return text;
		}
		for (int i = 0; i < edgeList.size(); i++) {
			Edge edge = edgeList.get(i);
			text+= "." + edge.getTarget().getNameSyn();
		}
		return text;
	}
	
	/**
	 * @return string that concats the element ids of this path by a "."
	 */
	public String toIdString(){
		if (startVertex==null){
			return null;
		}
		String text = ""+startVertex.getId();
		if (endVertex==null){
			return text;
		}
		for (int i = 0; i < edgeList.size(); i++) {
			Edge edge = edgeList.get(i);
			text+= "." + edge.getTarget().getId();
		}
		return text;
	}
	
	public int setId(){
		if (startVertex==null){
			return -1;
		}
		if (endVertex==null){
			id = ((de.wdilab.coma.structure.Graph)graph).getSource().getId()+startVertex.getId();
		}
		id = ((de.wdilab.coma.structure.Graph)graph).getSource().getId();
		for (int i = 0; i < edgeList.size(); i++) {
			Edge edge = edgeList.get(i);
			id+= edge.getTarget().getId();
		}
		return id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object){
		if (!(object instanceof Path)) return false;
		Path current = (Path) object;
		if (current.getEdgeList().size()!=edgeList.size()) return false;
		if (!current.getStartVertex().equals(this.getStartVertex())) return false;
		for (int i = 0; i < edgeList.size(); i++) {
			Edge edge1 = edgeList.get(i);
			Edge edge2 = current.getEdgeList().get(i);
			Element target1 = edge1.getTarget();
			Element target2 = edge2.getTarget();
			if (!target1.equals(target2)) return false;
		}
		return true;		
	}

	
	public boolean containsAll(Object object){
		if (object instanceof Path){
			return containsAll((Path) object);
		} else if (object instanceof Element){
			return ((Path)object).getVertices().contains(object);
		} 
		return false;
	}
	
	public boolean containsAll(Path path){
		HashSet<Element> vertices1 = getVertices();
		HashSet<Element> vertices2 = path.getVertices();
		if (vertices1==null){
			if (vertices2==null){
				// both null
				return true;
			} else{
				// one null the other not
				return false;
			}
		} else if (vertices2==null){
			// one null the other not
			return false;
		}
		// compare the nodes
		return vertices1.containsAll(vertices2);	
	}
	
	public Path getSubPath(Element startElement){
		// element null or not in this path
		if (startElement==null || !vertices.contains(startElement)) return null;
		// given element equals start element - return whole path
		if (startVertex.equals(startElement)) return new Path(this);
		// given element equals end element - return path with only this element
		if (endVertex.equals(startElement)) return new Path(graph, endVertex);
		// go through all the edge
		int beginPosition = -1;
		for (int i = 0; i < edgeList.size(); i++) {
			Edge edge = edgeList.get(i);
			if (edge.getSource().equals(startElement)){
				// the first edge that contains the element
				beginPosition = i;
				break;
			}
		}	
		// create new sub path containing the edges beginning with "the first" 
		Path subPath = new Path(graph);
		for (int i = beginPosition; i < edgeList.size(); i++) {
			Edge edge = edgeList.get(i);
			subPath.addEdge(edge);
		}	
		
		return subPath;
	}
	
	public void appendAtEnd(Path path){
		// assumption this endVertex is the startVertex of the given path
		if (!endVertex.equals(path.getFirstElement())) return;
		
		vertices.addAll(path.getVertices());
		edgeList.addAll(path.getEdgeList());
		endVertex=path.getLastElement();
	}


	
//	public List getAllVertices(){
//		if (allVertices==null){
//			// optimization - if called several time this is already calculated
//			allVertices = Graphs.getPathVertexList(this);
//		}
//		return allVertices;
//	}
	
	
	/*
	 * OVERWRITTEN METHODS FROM CLASS AbstractNode.java (COMA MAPPING). The class GraphPathImpl
	 * is now an AbstractNode, overwriting the methods used for the coma mapping process. 
	 * Patrick, Mar 14
	 */
	
	
	@Override
	public String getUniqueRepresentation() {   // e.g. Student.Name.FirstName
		return toString();   
	}

	
	
	@Override
	public String getComment() {   // Temporarily not needed in the coma.mapping package.
		return null;	
	}

	
	
	@Override
	public String getDataType() {   // Temporarily not needed in the coma.mapping package.
		return null;	
	}

	
	
	@Override
	public String getElement() {    // e.g. FirstName

		if( startVertex==null){
			return null;
		}
		
		String text = startVertex.getName();
		if( endVertex==null){
			return text;
		}
		
		for( int i = 0; i < edgeList.size(); i++) {
			Edge edge = edgeList.get(i);
			text = edge.getTarget().getName();
		}
		
		return text;	
		
	}

	
	
	@Override
	public int getDepth() {   // e.g. 2 for Student.Name.FirstName

		if( startVertex==null) {   
			return -1;
		}
		
		if( endVertex==null) {
			return 0;
		}
		
		return edgeList.size();
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if (id==-1)
		return setId();
		return id;
	}
	
	void removeLast(){
		if (endVertex!=null){
			if (edgeList.size()==1){
				// only one edge
				edgeList.remove(0);
				endVertex=startVertex;
			} else if (edgeList.size()>1) {
				// several edges
				Edge lastEdge = edgeList.get(edgeList.size()-1);
				endVertex = lastEdge.getSource();
				edgeList.remove(lastEdge);
			}
		}
	}
	
	/**
	 * @return copy of this path without last node (and if existing without edge to it)
	 */
	public Path getPathWithoutLastNode(){
		Path path = new Path(this);
		path.removeLast();
		return path;
	}
	
}