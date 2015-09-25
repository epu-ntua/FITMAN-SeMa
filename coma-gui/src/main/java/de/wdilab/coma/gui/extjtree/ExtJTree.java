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

package de.wdilab.coma.gui.extjtree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JTree;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.view.MatchresultView2;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Path;

/**
 * This class is used to display source, middle and target model in a tree kind. It has one or
 * multiple roots and each of them can have one or more child nodes and so on.
 * 
 * @author Sabine Massmann
 */
public class ExtJTree extends JTree {
	
	//
	/**
	 *  kind of a tree can be left=source, middle, right=target
	 */
	public static  final int SOURCE = 0,
							 TARGET = 1,
							 MIDDLE = 2;
	/** restrict the number of expanded element because the display has limits
	25000 works for one schema, 12500 works for two big schemas */	
	static final int MAX_EXPAND = 2500; // 2500, 10000, 12500
	/** Matchresult View - needed to propagte changes and 
	 * get informations about the second tree in the view */
	private MatchresultView2 matchresultView;
	/** the root node of this tree */
	private DefaultMutableTreeNode root;
	/** the kind of this tree (SOURCE, MIDDLE, TARGET) */
	private int treeKind;
	/** the kind of this tree (MainWindow.VIEW_GRAPH/VIEW_NODES) */
	private int viewKind;
	/** the mouse adapter (ExtJTreeMouseAdapter) */
	private ExtJTreeMouseAdapter mouseAdapter;
	/** the key adapter (ExtJTreeKeyAdapter) */
	private ExtJTreeKeyAdapter keyAdapter;

	/**
	 * Returns a ExtJTree with the specified DefaultMutableTreeNode as its root,
	 * matchresultView a MatchresultView2 object and the tree kind (SOURCE, MIDDLE, TARGET)
	 */
	public ExtJTree(DefaultMutableTreeNode _rootNode,
			MatchresultView2 _matchresultView, int _treeKind, int _viewKind) {
		super(_rootNode);
		setRootVisible(false);
		matchresultView = _matchresultView;
		root = _rootNode;
		treeKind = _treeKind;
		viewKind = _viewKind;
		getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		mouseAdapter = new ExtJTreeMouseAdapter(this, matchresultView);
		addMouseListener(mouseAdapter);
		keyAdapter = new ExtJTreeKeyAdapter(this, matchresultView);
		addKeyListener(keyAdapter);
		setBackground(MainWindow.GLOBAL_BACKGROUND);
	}

	/** returns the tree kind (SOURCE, MIDDLE, TARGET) */
	protected int getTreeKind() {
		return treeKind;
	}
	
	/** returns the view kind (MainWindow.VIEW_GRAPH/VIEW_NODES) */
	public int getViewKind() {
		return viewKind;
	}


	/**
	 * Returns the next treepath to the given row that start with the given
	 * prefix use the given prefix (here: only Forward!))
	 */
	public TreePath getNextMatch(String _prefix, int _startingRow,
			Position.Bias _bias) {
		if (_prefix == null) {
			return null;
		}
		int maxRow = getRowCount();
		for (int i = _startingRow; i < maxRow; i++) {
			TreePath current = getPathForRow(i);
			if (current.getLastPathComponent() != null) {
				Path path = (Path) ((DefaultMutableTreeNode) current
						.getLastPathComponent()).getUserObject();
				if (path != null) {
					Element element = path.getLastElement();
					if ((element.getName() != null)
							&& element.getName().toLowerCase().startsWith(
									_prefix.toLowerCase())) {
						scrollPathToVisible(current);
						return current;
					}
				}
			}
		}
		return null;
	}

	/*
	 * returns the root node of this tree
	 */
	protected DefaultMutableTreeNode getRootNode() {
		return root;
	}

	/*
	 * expand all nodes of the tree (GUI)
	 */
	public void expandAll() {
		//		 for counting the number of expanding
		count = 0;
		TreePath rootPath = new TreePath(root);
		for (int r = 0; r < root.getChildCount(); r++) {
			DefaultMutableTreeNode tn = (DefaultMutableTreeNode) root.getChildAt(r);
			this.expandPath(rootPath.pathByAddingChild(tn));
			count++;
		}		
		if (count < MAX_EXPAND){
			expandAll(this, root, new TreePath(root));
		}
	}

	int count = 0;
	
	/*
	 * expand all nodes of the current tree node (GUI)
	 */
	public void expandAll(ExtJTree _tree, DefaultMutableTreeNode _rootNode,
			TreePath _rootPath) {	
		if (count >= MAX_EXPAND){
			return;
		}
		_tree.expandPath(_rootPath);
		count++;
		if (count < MAX_EXPAND) {
			for (int r = 0; r < _rootNode.getChildCount(); r++) {
				DefaultMutableTreeNode tn = (DefaultMutableTreeNode) _rootNode
						.getChildAt(r);
				expandAll(_tree, tn, _rootPath.pathByAddingChild(tn));
			}
//		} else {
//			System.out.println("_count");
//			switch (getTreeKind()) {
//			case SOURCE :
//				matchresultView.getController().getMainWindow()
//						.enableUnfoldSrc(false);
//				break;
//			case MIDDLE :
//				// nothing
//				break;
//			case TARGET :
//				matchresultView.getController().getMainWindow()
//						.enableUnfoldTrg(false);
//				break;
//			}
		}
	}

	public void expandAll(TreePath _rootPath) {
		//		 for counting the number of expanding
		count = 0;
		expandAll(this, (DefaultMutableTreeNode) _rootPath
				.getLastPathComponent(), _rootPath);
	}

	/*
	 * return the ExtJTreeMouseAdapter of this tree
	 */
	public ExtJTreeMouseAdapter getExtJTreeMouseAdapter() {
		return mouseAdapter;
	}
	
	/*
	 * sets the selected Component to null and the selectedRow -1
	 */
	public void setSelectionNull() {
		if (mouseAdapter != null) {
			mouseAdapter.setSelectionNull();
		}
	}

	/*
	 * get for a path (of a node in the schema graph) the row in the tree
	 * representation
	 */
	static HashSet<TreePath> findTreePath(ArrayList _path, ExtJTree _tree,
			HashMap<Object, TreePath> _path2TreePath) {
		//		boolean write = false;
		if (_tree != null) {
			TreePath treePath = _path2TreePath.get(_path);
			if (treePath != null) {
				HashSet<TreePath> treePaths = new HashSet<TreePath>();
				treePaths.add(treePath);
				return treePaths;
			}
			return getTreePath(_path, _tree, _path2TreePath);
		}
		return null;
	}

	public static HashSet<TreePath> getTreePath(Object _path, ExtJTree _tree,
			HashMap<Object, TreePath> _path2TreePath) {
		int rows = _tree.getRowCount();
		HashSet<TreePath> treePaths = new HashSet<TreePath>();
		for (int i = 0; i < rows; i++) {
			TreePath treePath = _tree.getPathForRow(i);
			Object userObject = ((DefaultMutableTreeNode) treePath
				.getLastPathComponent()).getUserObject();
			if ((userObject != null) && userObject.equals(_path)) {
				_path2TreePath.put(userObject, treePath);
				treePaths.add(treePath);
			} else if (userObject!=null && _path instanceof Element 
					&& userObject instanceof Path){
				if (((Path)userObject).getLastElement().equals(_path)){
					// get a path that ends on the given element 
					// restriction: only first path returned
					_path2TreePath.put(userObject, treePath);
					treePaths.add(treePath);
				}
			}
		}
		if (treePaths.isEmpty()) return null;
		return treePaths;
	}
	
	
	
    public HashMap<Integer, DefaultMutableTreeNode> getMatches(String string) {
   	int max = getRowCount();
		if (string == null) {
		  return null;
		}
		string = string.toLowerCase();
		HashMap<Integer,DefaultMutableTreeNode> nodes = new HashMap<Integer,DefaultMutableTreeNode>(); 
		for (int i = 0; i < max; i++) {
		   TreePath current = getPathForRow(i);
			if (current.getLastPathComponent() != null) {
				DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) current.getLastPathComponent();
				Object object = lastPathComponent.getUserObject();
				if (object!=null){
					Element element = null;
					if (object instanceof Path){
						Path path = (Path) object;
						element = path.getLastElement();
					} else if (object instanceof Element){
						element = (Element) object;
					} else {
//						System.out.println("ExtJTree.getMatches unexpected class " + object.getClass().getCanonicalName());
					}
					if (element!=null){
						String textRep = element.getName();
						if (textRep != null && textRep.toLowerCase().contains(string)) {
							nodes.put(new Integer(i), lastPathComponent);
						}
					}
				}
			}
		}
		if (nodes.isEmpty()){
			return null;
		}
		return nodes;
   }
}