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

package de.wdilab.coma.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This class contains a simple tree used to display the hierachy of the matching variables 
 * (Dlg_ShowHierarchy) and the reuse possibilities (DlgALL_Reuse).
 * 
 * @author Sabine Massmann
 */
public class SimpleJTree extends JTree {
	private DefaultMutableTreeNode rootNode;
	Controller controller;
	boolean combinedReuse = false;

	
	public SimpleJTree(DefaultMutableTreeNode _rootNode,
			Controller _controller) {
		super(_rootNode);
		//		if (_combinedReuse){
		//			setSelectionModel(TreeSelectionModel.SINGLE_TREE_SELECTION);
		TreeSelectionModel model = getSelectionModel();
		model.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setSelectionModel(model);
		//		}
		setCellRenderer(new SimpleJTreeCellRenderer(combinedReuse));
		rootNode = _rootNode;
		controller = _controller;
		setBackground(MainWindow.GLOBAL_BACKGROUND);
	}
	
	public SimpleJTree(DefaultMutableTreeNode _rootNode,
			Controller _controller, boolean _combinedReuse) {
		super(_rootNode);
		combinedReuse = _combinedReuse;
		//		if (_combinedReuse){
		//			setSelectionModel(TreeSelectionModel.SINGLE_TREE_SELECTION);
		TreeSelectionModel model = getSelectionModel();
		model.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setSelectionModel(model);
		//		}
		setCellRenderer(new SimpleJTreeCellRenderer(_combinedReuse));
		rootNode = _rootNode;
		controller = _controller;
		setBackground(MainWindow.GLOBAL_BACKGROUND);
	}

	public void expandAll() {
		expandAll(-1);
	}

	/*
	 * expand all nodes of the tree (GUI)
	 */
	public void expandAll(int _length) {
		expandAll(this, rootNode, new TreePath(rootNode), _length, 0);
	}

	/*
	 * expand all nodes of the current tree node (GUI)
	 */
	public void expandAll(SimpleJTree _tree, DefaultMutableTreeNode _rootNode,
			TreePath _rootPath, int _length, int _currentLength) {
		if ((_length > -1) && (_length == _currentLength)) {
			return;
		}
		_tree.expandPath(_rootPath);
		for (int r = 0; r < _rootNode.getChildCount(); r++) {
			DefaultMutableTreeNode tn = (DefaultMutableTreeNode) _rootNode
					.getChildAt(r);
			expandAll(_tree, tn, _rootPath.pathByAddingChild(tn), _length,
					_currentLength + 1);
		}
	}

	class SimpleJTreeCellRenderer extends DefaultTreeCellRenderer {
		boolean combReuse = false;

		public SimpleJTreeCellRenderer(boolean _combinedReuse) {
			super();
			combReuse = _combinedReuse;
		}

		/*
		 * get for an object the component, that will be shown in the tree
		 */
		public Component getTreeCellRendererComponent(JTree _tree,
				Object _value, boolean _selected, boolean _expanded,
				boolean _leaf, int _row, boolean _hasFocus) {
			JLabel label = new JLabel();
			HashMap srValues = null;
			if (controller != null) {
				if (combReuse) {
					srValues = controller.getSimValues();
				} else {
					srValues = controller.getSourceRelationshipValues();
				}
			}
			Object userObject = ((DefaultMutableTreeNode) _value)
					.getUserObject();
			if (userObject instanceof SourceRelationship) {
				// an already existing matchresult (repository)
				SourceRelationship sr = (SourceRelationship) ((DefaultMutableTreeNode) _value)
						.getUserObject();
				String value = "";
				if (srValues!=null){
					Float f = (Float) srValues.get(sr);
					value = f.toString();
					if (value.length() > 4) {
						value = value.substring(0, 5);
					}
				}
				label.setText(value + GUIConstants.COMMA_SPACE + sr.getType()
						+ GUIConstants.BRACKET_LEFT + controller.getManager().getSourceName(sr)
						+ GUIConstants.COMMA_SPACE + controller.getManager().getTargetName(sr)
						+ GUIConstants.BRACKET_RIGHT);
				if (sr.getComment() != null) {
					label.setText(label.getText() + GUIConstants.COMMA_SPACE
							+ sr.getComment());
				}
			} else if (srValues != null && srValues.containsKey(userObject)) {
				String sim = srValues.get(userObject).toString();
				if (sim.length() > 4) {
					sim = sim.substring(0, 5);
				}
				String text;
				if (userObject instanceof ArrayList) {
					// matchresultpath
					text = GUIConstants.VIA;
					ArrayList<String> sources = new ArrayList<String>();
					boolean matchTask = false;
					for (int i = 0; i < ((ArrayList) userObject).size(); i++) {
						if (((ArrayList) userObject).get(i) instanceof SourceRelationship) {
							SourceRelationship sr = (SourceRelationship) ((ArrayList) userObject)
									.get(i);
							if (sources.contains(controller.getManager().getSourceName(sr))) {
								text = text.concat(controller.getManager().getSourceName(sr)
										+ GUIConstants.COMMA_SPACE);
							} else {
								sources.add(controller.getManager().getSourceName(sr));
							}
							if (sources.contains(controller.getManager().getTargetName(sr))) {
								text = text.concat(controller.getManager().getTargetName(sr)
										+ GUIConstants.COMMA_SPACE);
							} else {
								sources.add(controller.getManager().getTargetName(sr));
							}
						} else if (((ArrayList) userObject).get(i) instanceof ArrayList) {
							ArrayList sourceList = (ArrayList) ((ArrayList) userObject)
									.get(i);
							for (int j = 0; j < sourceList.size(); j++) {
								Source source = (Source) sourceList.get(j);
								if (sources.contains(source.getName())) {
									text = text.concat(source.getName()
											+ GUIConstants.COMMA_SPACE);
								} else {
									sources.add(source.getName());
								}
							}
						} else if (((ArrayList) userObject).get(i) instanceof Source) {
							text = userObject.toString();
							matchTask = true;
							label.setForeground(MainWindow.BORDER);
							continue;
						}
					}
					if (!matchTask) {
						text = text.substring(0, text.length() - 2);
					}
				} else {
					text = userObject.toString();
				}
				label.setText(sim + GUIConstants.COMMA_SPACE + text);
			} else if (userObject instanceof ArrayList) {
				Object o = ((ArrayList) userObject).get(0);
				if ((o != null) && (o instanceof Source)) {
					label.setText(GUIConstants.Number_0_0 + GUIConstants.COMMA_SPACE
							+ userObject);
					label.setForeground(MainWindow.BORDER);
				} else {
					label.setText(_value.toString());
				}
			} else {
				label.setText(_value.toString());
			}
			label.setOpaque(true);
			label.setFont(MainWindow.FONT13);
			if (_selected) {
				label.setBackground(MainWindow.SELECTED_BACKGROUND);
			} else {
				label.setBackground(MainWindow.GLOBAL_BACKGROUND);
			}
			label.setToolTipText(label.getText());
			return label;
		}
	}
}