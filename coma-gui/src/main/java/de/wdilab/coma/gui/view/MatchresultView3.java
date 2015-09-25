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

package de.wdilab.coma.gui.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.DefaultScrollPane;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.ManagementPane;
import de.wdilab.coma.gui.extjtree.ExtJTree;
import de.wdilab.coma.gui.extjtree.ExtJTreeCellRenderer;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Path;

/**
 * MatchresultView extends JSplitPane, displays source + middle + target tree.
 * 
 * @author Sabine Massmann
 */
public class MatchresultView3 extends MatchresultView2 {
	//	private boolean debug = true;
	//	private Graph middle;
	private ExtJTree middleTree;
	private JScrollPane middleTreePane;
	private HashMap<Object, TreePath> middlePath2TreePath;
	private Hashtable middleUpRootPaths, middle2UpRootPaths;
	private JSplitPane leftSplitPane;
	final static double SPLIT = 0.67;
	protected ArrayList fragmentPairs2;
	protected Graph middle;
	int divLocationLeft=0, divLocationRight=0;
	
	/*
	 * Constructor of the MatchresultView
	 */
	public MatchresultView3(LinedMatchresultView2 _parent, Controller _controller) {
		super(_parent, _controller);
		leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		leftSplitPane.setResizeWeight(0.5);
		leftSplitPane.setLeftComponent(getLeftComponent());
		setLeftComponent(leftSplitPane);
		leftSplitPane.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("dividerLocation")){
					Integer newValue = (Integer) evt.getNewValue();
					Integer oldValue = (Integer) evt.getOldValue();
					if (newValue!=oldValue && !newValue.equals(divLocationLeft)){
						divLocationLeft = newValue;
						controller.getMatchresultView().setChanged(true);
					}
				}
				
//				divLocationLeft
			}
		});
		
		
		fragmentPairs2 = new ArrayList();
		middleTree = getMiddleTree(null);
		setMiddleTree(middleTree, null);
		middlePath2TreePath = new HashMap<Object, TreePath>();
		middleUpRootPaths = new Hashtable();
		middle2UpRootPaths = new Hashtable();
	}

	/* return middle tree */
	public ExtJTree getMiddleTree() {
		return middleTree;
	}

	public int getDividerLocationLeft() {
		return leftSplitPane.getDividerLocation();
	}

	public Graph getMiddleGraph(){
		return middle;
	}
	
	public int getDividerLocationRight() {
		return getDividerLocation();
	}

	/*
	 * create for the given schema graph a source tree and repaint the split
	 * pane
	 */
	public void setMiddleSchema(Graph _Graph) {
		//if (schema == null)
		//setSourceTree(getSourceTree(null), null);
		//else
		//setSourceTree(getSourceTree(schema), schema.getSource().getName(),
		// schema.getSource().getType());
		//		middle = _Graph;
		setMiddleTree(getMiddleTree(_Graph), _Graph);
	}
	
	public JSplitPane getLeftSplitPane(){
		return leftSplitPane;
	}

	/*
	 * set the source tree, create a new tree pane and add Listener
	 */
	public void setSourceTree(ExtJTree _sourceTree, Graph _Graph) {
		if (leftSplitPane == null) {
			super.setSourceTree(_sourceTree, _Graph);
			return;
		}
		sourceTree = _sourceTree;
		String name = GUIConstants.EMPTY;
//		String content = null;
		String tooltiptext = null;
		if (_Graph != null) {
			name = GUIConstants.EMPTY + _Graph.getSource().getName();
//			content = Source.typeToString(_Graph.getSource().getType());
			tooltiptext = GUIConstants.EMPTY + _Graph.getSource().getName()
					+ GUIConstants.COLON_SPACE2
					+ _Graph.getSource().getProvider();
//					+ GUIConstants.SPACE_SMALLER + _Graph.getSource().getUrl()
//					+ GUIConstants.BIGGER;
		}
		String labelText = name;
//		if (content != null) {
//			labelText = //GUIConstants.TARGETSCHEMA + GUIConstants.COLON_SPACE +
//			name + GUIConstants.BRACKET_LEFT + content + GUIConstants.BRACKET_RIGHT;
//		} else {
//			labelText = GUIConstants.SOURCESCHEMA + //GUIConstants.COLON_SPACE +
//					name;
//		}
		sourceTreePane = new DefaultScrollPane(_sourceTree, this);
		JPanel sourcePanel = new JPanel(new BorderLayout());
		JLabel sourceLabel = new JLabel(labelText, SwingConstants.CENTER);
		sourceLabel.setLayout(new BorderLayout());
//		JButton button = new JButton(new ImageIcon(GUIConstants.ICON_CLOSE));
		JButton button = new JButton(Controller.getImageIcon(GUIConstants.ICON_CLOSE));
		button.setToolTipText(GUIConstants.CLOSE_SCHEMA);
		button.setMargin(ManagementPane.BUTTON_INSETS);
		sourceLabel.add(button, BorderLayout.EAST);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				controller.closeSchema(true, false);
			}
		});
		sourceLabel.setToolTipText(tooltiptext);
		sourcePanel.add(sourceLabel, BorderLayout.NORTH);
		sourcePanel.add(sourceTreePane, BorderLayout.CENTER);
		//		setLeftComponent(sourceTreePane);
		leftSplitPane.setLeftComponent(sourcePanel);
		leftSplitPane.setDividerLocation(0.5);
	}

	/*
	 * start the creation of a source tree for a given schema graph, expand the
	 * tree and add CellRenderer (if schema graph is null create a default
	 * source tree)
	 */
	private ExtJTree getMiddleTree(Graph graph) {
		ExtJTree newTree = null;
		if (graph == null) {
			newTree = new ExtJTree(null, this, ExtJTree.MIDDLE, MainWindow.VIEW_UNDEF);
		} else {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			ArrayList roots = graph.getRoots();
			roots = sortNodesId(roots);
			if (roots != null) {
				for (int i = 0; i < roots.size(); i++) {
					buildTree(root, new Path(graph), (Element) roots.get(i),
							graph);
				}
			}
			newTree = new ExtJTree(root, this, ExtJTree.MIDDLE, MainWindow.VIEW_GRAPH);
			//			don't know why but we need this line to show the ToolTip of the
			// elements
			newTree.setToolTipText(GUIConstants.EMPTY);
			DefaultTreeCellRenderer renderer = new ExtJTreeCellRenderer(
					ExtJTree.MIDDLE, this);
			newTree.setCellRenderer(renderer);
			newTree.expandAll();
			buildPath2TreePath(newTree, middlePath2TreePath);
		}
		return newTree;
	}

	/*
	 * set the source tree, create a new tree pane and add Listener
	 */
	private void setMiddleTree(ExtJTree _middleTree, Graph _Graph) {
		middleTree = _middleTree;
		middle = _Graph;
		String name = GUIConstants.EMPTY;
//		String content = null;
		String tooltiptext = null;
		if (_Graph != null) {
			name = GUIConstants.EMPTY + _Graph.getSource().getName();
//			content = Source.typeToString(_Graph.getSource().getType());
			tooltiptext = GUIConstants.EMPTY + _Graph.getSource().getName()
					+ GUIConstants.COLON_SPACE2
					+ _Graph.getSource().getProvider();
//					+ GUIConstants.SPACE_SMALLER + _Graph.getSource().getUrl()
//					+ GUIConstants.BIGGER;
		}
		String labelText = name;
//		if (content != null) {
//			labelText = //GUIConstants.TARGETSCHEMA + GUIConstants.COLON_SPACE +
//			name + GUIConstants.BRACKET_LEFT + content + GUIConstants.BRACKET_RIGHT;
//		} else {
//			labelText = GUIConstants.MIDDLESCHEMA + //GUIConstants.COLON_SPACE +
//					name;
//		}
		middleTreePane = new DefaultScrollPane(_middleTree, this);
		JPanel middlePanel = new JPanel(new BorderLayout());
		JLabel middleLabel = new JLabel(labelText, SwingConstants.CENTER);
		middleLabel.setLayout(new BorderLayout());
		//		JButton button = new JButton(new ImageIcon(GUIConstants.ICON_CLOSE));
		//		button.setToolTipText(GUIConstants.CLOSE_SCHEMA);
		//		button.setMargin(ManagementPane.BUTTON_INSETS);
		//		middleLabel.add(button, BorderLayout.EAST);
		//		button.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent _event) {
		//				MatchresultView3.controller.closeSchema(true, false);
		//			}
		//		});
		middleLabel.setToolTipText(tooltiptext);
		middlePanel.add(middleLabel, BorderLayout.NORTH);
		middlePanel.add(middleTreePane, BorderLayout.CENTER);	
		leftSplitPane.setRightComponent(middlePanel);
		leftSplitPane.setDividerLocation(0.5);
		setDividerLocation(SPLIT);
		leftSplitPane.setDividerLocation(0.5);
		leftSplitPane.repaint();
	}

	public void setDividerLocation(double _d) {
		//		if (leftSplitPane != null) {
		//			leftSplitPane.setDividerLocation(d);
		//		}
		_d = 1 - (_d * 2) / 3;
		super.setDividerLocation(_d);
		//		if (leftSplitPane!=null){
		//			leftSplitPane.setDividerLocation(0.5);
		//		}
		//		super.setDividerLocation(SPLIT);
	}

//	/*
//	 * set whether the edit modus is on (true) or not (false)
//	 */
//	public void setEdit(boolean _state) {
//		edit = _state;
//		if (edit) {
//			sourceTree.getSelectionModel().setSelectionMode(
//					TreeSelectionModel.SINGLE_TREE_SELECTION);
//			targetTree.getSelectionModel().setSelectionMode(
//					TreeSelectionModel.SINGLE_TREE_SELECTION);
//			middleTree.getSelectionModel().setSelectionMode(
//					TreeSelectionModel.SINGLE_TREE_SELECTION);
//		} else {
//			sourceTree.getSelectionModel().setSelectionMode(
//					TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
//			targetTree.getSelectionModel().setSelectionMode(
//					TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
//			middleTree.getSelectionModel().setSelectionMode(
//					TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
//		}
//	}

	/*
	 * return the Hashtable with key: target node and value: all Root Paths to
	 * that node
	 */
	public Hashtable getMiddleUpRootPaths() {
		return middleUpRootPaths;
	}

	public Hashtable getMiddle2UpRootPaths() {
		return middle2UpRootPaths;
	}

	public ArrayList getFragmentPairs2() {
		return fragmentPairs2;
	}

	/*
	 * return source treepane
	 */
	public JScrollPane getMiddleTreePane() {
		return middleTreePane;
	}

	/*
	 * return the Hashmap with key: source path and value: corresponding tree
	 * path
	 */
	public HashMap<Object, TreePath> getMiddlePath2TreePath() {
		return middlePath2TreePath;
	}
}