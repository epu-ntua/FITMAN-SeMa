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

import javax.swing.tree.TreePath;

/**
 * This class connects a treepath with a similarity value. (It thus optimizes the 
 * display of similarity value without having to search for it in the match result.) 
 * 
 * @author Sabine Massmann
 */
public class Match {
	TreePath treePath;
	float simValue;

	public Match(TreePath _treePath, float _simValue) {
		treePath = _treePath;
		simValue = _simValue;
	}

	/**
	 * @return Returns the simValue.
	 */
	public float getSimValue() {
		return simValue;
	}

	/**
	 * @return Returns the treePath.
	 */
	public TreePath getTreePath() {
		return treePath;
	}
}