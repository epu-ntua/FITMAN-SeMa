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

package de.wdilab.coma.gui.dlg;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.SimpleJTree;

/**
 * Show hierarchy from Graph as JTree
 * 
 * @author David Aumueller, Sabine Massmann
 * 
 * TODO clean output/toString() enable matcher configuration from
 *         within matcher hierarchy?
 */
public class Dlg_ShowHierarchy extends Dlg {
	Controller controller;

	public Dlg_ShowHierarchy(Controller _controller,
			SimpleDirectedGraph<String, DefaultEdge> _graph, String _title) {
		super(null, _title + " Display");
		controller = _controller;
		init(_title, _graph);
		pack(); // vs showDlg()/show()
	}

	void init(String _title, SimpleDirectedGraph<String, DefaultEdge> _graph) {
		Container cp = getContentPane();
		DefaultMutableTreeNode root;
		root = new DefaultMutableTreeNode(_title);
		toJTree(_graph, root);
		SimpleJTree tree = new SimpleJTree(root, controller, false);
		tree.setRootVisible(true);
//		tree.expandAll();
		tree.expandAll(2);
		//JTree einfuegen
		cp.add(new JScrollPane(tree), BorderLayout.CENTER);
		// Handle escape key to close the dialog
		// http://forum.java.sun.com/thread.jsp?thread=462776&forum=57&message=2669506
		KeyStroke escape = KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			public void actionPerformed(ActionEvent _event) {
				dispose();
			}
		};
		getRootPane()
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(escape, GUIConstants.ESCAPE);
		getRootPane().getActionMap().put(GUIConstants.ESCAPE, escapeAction);
	}
	
	  /**
	   * construct JTree of Graph as Hierarchy
	   * @param topRoot Node to attach Matchers to
	   * @author david
	   */
	  public void toJTree(SimpleDirectedGraph<String, DefaultEdge> graph, DefaultMutableTreeNode topRoot) {
	  	//DefaultMutableTreeNode re;
	    ArrayList<String> roots = getRoots(graph);
	    if (roots!=null) {
	      for (int i=0; i<roots.size(); i++) {
	        //toString((VertexImpl)roots.get(i), sb);
//	      	String rootName = "";
//	      		Object o = roots.get(i)).getObject();
//	      		if (o instanceof app.Element) {
//	      			rootName = ((app.Element)o).getTextRep();
//	      		} else if (o instanceof MatcherConfig){
//	      			rootName = ((MatcherConfig)o).toShortString();
//	      		} else{
//	      			rootName = o.toString();
//	      		}
	      	String rootName = roots.get(i);
	      	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootName);
	      	topRoot.add(rootNode);
	      	toJTree(graph, roots.get(i), rootNode);
	      }
	    }
	    else System.err.println("no roots in matcher hierarchy");
	    //else sb.append("Vertices: " + getVertexSet().toString() + "\nEdges: " + getEdgeSet().toString());
	    //return ;
	  }
	  
	 public static ArrayList<String> getRoots(SimpleDirectedGraph<String, DefaultEdge> graph){
		 Set<String> vertices = graph.vertexSet();
		 ArrayList<String> roots = new ArrayList<String>();
		 for (String vertex : vertices) {
			Set<DefaultEdge> incoming = graph.incomingEdgesOf(vertex);
			if (incoming==null || incoming.isEmpty()){
				roots.add(vertex);
			}
		}
		 if (roots.isEmpty()) return null;
		 return roots;
	  }
	  
	  
	  /**
	   * construct JTree of Graph as Hierarchy - recursive part
	   * @param vertex Vertex to get descendants from
	   * @param node Node to attach descendants to
	   * @return node Node with attached descendants
	   * @author david
	   */
	  private DefaultMutableTreeNode toJTree(SimpleDirectedGraph<String, DefaultEdge> graph, String vertex, DefaultMutableTreeNode node) {
	    //if (root == null) return null;

	    //for (int i=0; i<depth; i++) pad += "   ";
	    //DefaultMutableTreeNode child = new DefaultMutableTreeNode(((VertexImpl)root).getObject());
	    //node.add(child);
//	    ArrayList<String> children = getChildren(vert);
		  Set<DefaultEdge> children = graph.outgoingEdgesOf(vertex);
	    if (children != null)
//	      for (int i=0; i<children.size(); i++) {
//	      	String childName = ""; //((VertexImpl)children.get(i)).getObject().toString();
//		  		Object o = ((VertexImpl)children.get(i)).getObject();
//		  		if (o instanceof app.Element) {
//		  			childName = ((app.Element)o).getTextRep();
//	      		} else if (o instanceof MatcherConfig){
//	      			childName = ((MatcherConfig)o).toShortString();
//		  		} else childName = o.toString();
	    	
	    	for (DefaultEdge child : children) {
	    		String childName =graph.getEdgeTarget(child);
	    		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childName);
	    		node.add(childNode);
	    		toJTree(graph, childName, childNode);
	      }
	    return node;
	  }
	  
	
}