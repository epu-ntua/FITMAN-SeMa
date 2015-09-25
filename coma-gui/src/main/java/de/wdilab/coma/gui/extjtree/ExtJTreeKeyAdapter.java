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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.view.MatchresultView2;

/**
 * This adapter class is used to start certain reaction after a key is released. 
 * Pressing the key does not have an (additional) effect.
 * 
 * @author Sabine Massmann
 */
public class ExtJTreeKeyAdapter extends KeyAdapter {
	private ExtJTree tree;
	private MatchresultView2 view;

	/*
	 * Constructor for ExtJTreeMouseAdapter
	 */
	public ExtJTreeKeyAdapter(ExtJTree _tree, MatchresultView2 _view) {
		tree = _tree;
		view = _view;
	}

	public void keyReleased(KeyEvent _event) {
		// 3.
//		System.out.println("keyReleased");
		if (tree.getLeadSelectionRow() > 0) {
				tree.getExtJTreeMouseAdapter().setSelectedRow(
						tree.getLeadSelectionRow());
				view.controller.getMatchresultView().setChanged(true);
			view.setLastSelectedTree(tree.getTreeKind());
			if (view.getController().getGUIMatchresult().containsMatchResult()) {
				if (view.getLastSelectedTree() == MainWindow.NONE) {
					view.getController().setStatus(GUIConstants.SELECT_A_NODE);
				} else {
					view.getController().setStatus(GUIConstants.DESELECT_NODES);
				}
			}
		}
		//		view.getController().getMainWindow().repaint();
		//		System.out.println(e);
	}
}