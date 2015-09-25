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

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Dialog class including classes for showing the dialog at a certain location 
 * and dimension (size).
 * 
 * @author Sabine Massmann
 */
public class Dlg extends JDialog {
	/**
	 * Constructor, parent and title
	 */
	public Dlg(JFrame _parent, String _title) {
		super(_parent, _title);
		setModal(true);
	}

	public void showDlg(Point _position) {
		setLocation(_position);
		setVisible(true);
	}

	public void showDlg(Point _position, Dimension _dim) {
		setLocation(_position);
		setSize(_dim);
		setVisible(true);
	}
}
