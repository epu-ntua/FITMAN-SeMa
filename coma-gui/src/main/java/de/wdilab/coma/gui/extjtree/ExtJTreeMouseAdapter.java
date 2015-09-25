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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.tree.DefaultMutableTreeNode;

import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.MainWindowContentPane;
import de.wdilab.coma.gui.view.MatchresultView2;
import de.wdilab.coma.gui.view.MatchresultView3;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Path;

/**
 * This adapter class is used to initiate certain reaction when a mouse button
 * is pressed and released. It is used to show correspondences if existing when 
 * a single node is selected and remove them if it is "de-selected".
 * 
 * @author Sabine Massmann
 */
public class ExtJTreeMouseAdapter extends MouseAdapter {
	private ExtJTree tree;
	private MatchresultView2 view;
	private int selectedRow;

	/*
	 * Constructor for ExtJTreeMouseAdapter
	 */
	public ExtJTreeMouseAdapter(ExtJTree _tree, MatchresultView2 _view) {
		tree = _tree;
		view = _view;
		selectedRow = -1;
	}

	//	private void showOnlyFragment(MouseEvent _event){
	//		Point p = _event.getPoint();
	//		TreePath path = tree.getPathForLocation((int) p.getX(), (int)
	// p.getY());
	//		DefaultMutableTreeNode root = (DefaultMutableTreeNode)
	// path.getLastPathComponent();
	//		ExtJTree newTree = new ExtJTree(root, view, MainWindow.SOURCE);
	//		newTree.setRootVisible(true);
	//		newTree.setToolTipText(GUIConstants.EMPTY);
	//		DefaultTreeCellRenderer renderer = new ExtJTreeCellRenderer(
	//				MainWindow.SOURCE, view);
	//		renderer.setOpenIcon(null);
	//		renderer.setClosedIcon(null);
	//		renderer.setLeafIcon(null);
	//		newTree.setCellRenderer(renderer);
	//		newTree.expandAll();
	//		System.out.println(newTree.getRootNode().toString());
	//		System.out.println(newTree.getPathForRow(0));
	//		System.out.println(newTree.getPathForRow(1));
	//		System.out.println(newTree.toString());
	//		view.setSourceTree(newTree);
	//	}
	/*
	 * if mouse was pressed and Shift is NOT down => either select choosen
	 * element or unselect it (if already selected)
	 */
	public void mousePressed(MouseEvent _event) {
		//		if (_event.getButton()!=MouseEvent.BUTTON1){
		//			showOnlyFragment(_event);
		//			return;
		//		}
		if(_event.getButton()!=MouseEvent.BUTTON1){
			// do nothing 
			return;
		}
		Point p = _event.getPoint();
		int row = tree.getRowForLocation((int) p.getX(), (int) p.getY());
		//		System.out.println(_event.getButton());
		if (_event.isControlDown()) {
			if (selectedRow == row) {
				if (tree.getLastSelectedPathComponent() != null) {
					int[] all = tree.getSelectionRows();
					if (all.length > 0) {
						selectedRow = all[0];
						tree.setLeadSelectionPath(tree
								.getPathForRow(selectedRow));
					} else {
						selectedRow = -1;
						//					selectedRow = thisTree.getLeadSelectionRow();
					}
				}
			} else {
				selectedRow = row;
			}
			view.setLastSelectedTree(tree.getTreeKind());
		} else {
			if (selectedRow == row) {
				tree.setSelectionNull();
				switch (tree.getTreeKind()) {
				case ExtJTree.SOURCE :
					if (view.getLastSelectedTree() == ExtJTree.SOURCE) {
						if (view.getTargetTree()
								.getLastSelectedPathComponent() != null) {
							view.setLastSelectedTree(ExtJTree.TARGET);
						} else {
							view.setLastSelectedTree(MainWindow.NONE);
						}
					}
					break;
				case ExtJTree.MIDDLE :
					if (view.getLastSelectedTree() == ExtJTree.MIDDLE) {
						if (((MatchresultView3) view).getMiddleTree()
								.getLastSelectedPathComponent() != null) {
							view.setLastSelectedTree(ExtJTree.MIDDLE);
						} else {
							view.setLastSelectedTree(MainWindow.NONE);
						}
					}
					break;
				case ExtJTree.TARGET :
					if (view.getLastSelectedTree() == ExtJTree.TARGET) {
						if (view.getSourceTree()
								.getLastSelectedPathComponent() != null) {
							view.setLastSelectedTree(ExtJTree.SOURCE);
						} else {
							view.setLastSelectedTree(MainWindow.NONE);
						}
					}
					break;
				}
			} else {
				view.setLastSelectedTree(tree.getTreeKind());
				selectedRow = row;
			}
			if (tree.getTreeKind()==ExtJTree.SOURCE && view instanceof MatchresultView3){
				ExtJTree trgTree = ((MatchresultView3)view).getTargetTree();
			
				if (tree.getSelectionPath()==null){
					if ( trgTree.getCellRenderer() instanceof ExtJTreeCellRenderer)
						((ExtJTreeCellRenderer) trgTree.getCellRenderer()).setNotRelevantTrgPaths((Element)null);
				} else {
					DefaultMutableTreeNode source = (DefaultMutableTreeNode) tree
					.getSelectionPath().getLastPathComponent();
					if ( source.getUserObject() instanceof Path){
						Path path = (Path) source.getUserObject();

						if ( trgTree.getCellRenderer() instanceof ExtJTreeCellRenderer)
							((ExtJTreeCellRenderer) trgTree.getCellRenderer()).setNotRelevantTrgPaths(path.getLastElement());				
					}
				}
			}
		}
		view.setChanged(true);
	}

	/*
	 * sets the selected Component to null and the selectedRow -1
	 */
	protected void setSelectionNull() {
		tree.clearSelection();
		selectedRow = -1;
	}

	/*
	 * if mouse is released draw the current lines between (selected) source and
	 * (selected) target
	 */
	public void mouseReleased(MouseEvent _event) {
		if (_event.getButton() != MouseEvent.BUTTON1) {
			return;
		}
		if (view.getController().getGUIMatchresult().containsMatchResult()) {
			MainWindowContentPane pane = view.getController().getMainWindow().getNewContentPane();
			if (view.getLastSelectedTree() == MainWindow.NONE) {
				view.getController().setStatus(GUIConstants.SELECT_A_NODE);
				pane.enableArrows(false);
			} else {
				view.getController().setStatus(GUIConstants.DESELECT_NODES);
				pane.enableArrows(true);
			}
		}
	}

	/*
	 * set the selected row to the given row
	 */
	public void setSelectedRow(int _row) {
		selectedRow = _row;
	}
}