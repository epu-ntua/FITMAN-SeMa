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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.Strings;

/**
 * This dialog shows all available default schemas /
 * matchresults to the user and let him choose which to import default: all selected
 * 
 * @author Sabine Massmann
 */
public class Dlg_ImportDefault extends Dlg {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	public static final int EXAMPLE_ALL = 1;	
	//----------------------------------------------
	Controller controller;
	int mode;
	JCheckBoxMenuItem[] items;
	int size;

	/*
	 * Constructor of Dlg_NewMatcher
	 */
	public Dlg_ImportDefault(JFrame _parent, final Controller _controller,
			boolean _schema, String _titel, int _mode) {
		super(_parent, _titel);
		controller = _controller;
		//		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		mode = _mode;
		init();
	}

	/*
	 * initialize this Dialog and add all needed Components (depending on the
	 * given default values or values of an existing matcher config)
	 */
	protected void init() {
		Container mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		mainPanel.setLayout(gbl);
		int start = 0;
		int height = 1;
		switch (mode) {
		case EXAMPLE_ALL:
			size =  Strings.DEFAULT_ALL_SHORT.length;
			items = new JCheckBoxMenuItem[size];
			for (int i = 0; i < size; i++) {
				items[i] = new JCheckBoxMenuItem(
						Strings.DEFAULT_ALL_SHORT[i], false);
				items[i].setFont(MainWindow.FONT12_BOLD);
				gbc = MainWindow.makegbc(0, start, 2, height);
				gbl.setConstraints(items[i], gbc);
				mainPanel.add(items[i]);
				start += height;
				JLabel label = new JLabel("      " + Strings.DEFAULT_ALL_EXPLANATION[i]);
				label.setFont(MainWindow.FONT11);
				gbc = MainWindow.makegbc(0, start, 2, height);
				gbl.setConstraints(label, gbc);
				mainPanel.add(label);				
				start += height;
			}
			break;
		}

			// Separator
			height = 1;
			gbc = MainWindow.makegbc(0, start, 2, height);
			JSeparator sep = new JSeparator();
			gbl.setConstraints(sep, gbc);
			mainPanel.add(sep);
			start += height;
			// JLabel
			height = 1;
			gbc = MainWindow.makegbc(0, start, 2, height);
			JLabel label = new JLabel(GUIConstants.LAST_SOME_MIN);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			start += height;

		// Get Panel with all 3 Buttons and add it
		JPanel button = getButton();
		gbc = MainWindow.makegbc(0, start, 2, height);
		gbl.setConstraints(button, gbc);
		mainPanel.add(button);
		pack();
	}

	/*
	 * returns a JPanel containing three buttons: Save (DB), Save (File), Cancel
	 * (and add to these button action/ key listener)
	 */
	protected JPanel getButton() {
		JButton importBtn, cancelBtn;
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		//		new JPanel(new GridLayout(1, 3));
		importBtn = new JButton(GUIConstants.BUTTON_IMPORT);
		importBtn.setToolTipText(GUIConstants.DEFAULT_STRATEGY);
		importBtn.addActionListener(new ActionListener() {
			/* if the ok button was pressed close dialog */
			public void actionPerformed(ActionEvent _event) {
				setVisible(false);
				boolean[] returnValue = new boolean[size];
				for (int i = 0; i < size; i++) {
					returnValue[i] = items[i].getState();
				}
				switch (mode) {
				case EXAMPLE_ALL:
					controller.importDefaultAll(returnValue);
					break;
				default:
					break;
				}
			}
		});
		cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
		cancelBtn.addActionListener(new ActionListener() {
			/* if the ok button was pressed close dialog */
			public void actionPerformed(ActionEvent _event) {
				controller.setStatus(GUIConstants.IMPORT_DATA_ABORTED);
				setVisible(false);
			}
		});
		// Import Button
		panel.add(importBtn);
		//	Cancel Button
		panel.add(cancelBtn);
		return panel;
	}
}