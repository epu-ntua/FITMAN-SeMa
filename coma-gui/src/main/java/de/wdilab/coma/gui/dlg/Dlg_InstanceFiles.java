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
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;

/**
 * This dialog offers the change to import instances for an 
 * xsd or csv file.
 * 
 * @author Sabine Massmann
 */
public class Dlg_InstanceFiles extends Dlg {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	static final int BUTTON_START = 0;
	static final int ITEM_START1 = 1;
	static final int ITEM_START2 = 2;
	static final int ITEM_START3 = 3;
	//----------------------------------------------
	Controller controller;
	GridBagLayout gbl;
	GridBagConstraints gbc;

	ButtonGroup bg;
	JPanel buttons;
	JComboBox schema;
	JTextField instanceFile;
	boolean ok = false;
	
	/*
	 * Constructor of Dlg_NewMatcher
	 */
	public Dlg_InstanceFiles(JFrame _parent, Controller _controller, ArrayList _schemas) {
		super(_parent, GUIConstants.IMPORT_INSTANCES);
		controller = _controller;
		//		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		init(_schemas);
	}

	
	/*
	 * return a JComboBox that contains all possible fragment identification
	 * strategies
	 */
	protected JComboBox getSchemaBox(ArrayList _schemas) {
		ArrayList<Object> list = new ArrayList<Object>();
		if (_schemas!=null){
			list.addAll(_schemas);
		}
		list.add(0, GUIConstants.CHOOSE_NONE);
		JComboBox combo = new JComboBox(list.toArray());
		combo.setBackground(MainWindow.GLOBAL_BACKGROUND);
		combo.setMaximumRowCount(MainWindow.MAX_ROW);
		return combo;
	}
	
	/*
	 * initialize this Dialog and add all needed Components (depending on the
	 * given default values or values of an existing matcher config)
	 */
	protected void init(ArrayList _schemas) {
		Container mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		gbl = new GridBagLayout();
		mainPanel.setLayout(gbl);
		gbc = null;
		int start = 0;
		int height = 1;
				
		// Schema Label
		gbc = MainWindow.makegbc(ITEM_START1, start, 1, height);
		JLabel label = new JLabel("Schema: ", SwingConstants.LEFT);
		gbl.setConstraints(label, gbc);
		label.setFont(MainWindow.FONT11);
		mainPanel.add(label);
		// Schema Box
		gbc = MainWindow.makegbc(ITEM_START2, start, 1, height);
		schema = getSchemaBox(_schemas);
		gbl.setConstraints(schema, gbc);
		mainPanel.add(schema);
		
		start += height;
		
		// Schema Label
		gbc = MainWindow.makegbc(ITEM_START1, start, 1, height);
		label = new JLabel("Instance File: ", SwingConstants.LEFT);
		gbl.setConstraints(label, gbc);
		label.setFont(MainWindow.FONT11);
		mainPanel.add(label);
		// Text Field for Location and Name of Instance File
		gbc = MainWindow.makegbc(ITEM_START2, start, 1, height);
		instanceFile = new JTextField(GUIConstants.EMPTY, 20);
		gbl.setConstraints(instanceFile, gbc);
		instanceFile.setFont(MainWindow.FONT11);
		mainPanel.add(instanceFile);
		gbc = MainWindow.makegbc(ITEM_START3, start, 1, height);
		JButton searchBtn = new JButton(GUIConstants.BUTTON_SEARCH);
		searchBtn.addActionListener(new ActionListener() {
			/* if the ok button was pressed close dialog */
			public void actionPerformed(ActionEvent _event) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(controller.getDefaultDirectory());
				int returnVal = chooser.showSaveDialog(Dlg_InstanceFiles.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					controller.setDefaultDirectory(chooser.getCurrentDirectory());
					String _name = chooser.getSelectedFile().toString();
					instanceFile.setText(_name);
				}
			}
		});
		gbl.setConstraints(searchBtn, gbc);
		mainPanel.add(searchBtn);
		
		start += height;
		
		// Separator
		height = 1;
		gbc = MainWindow.makegbc(BUTTON_START, start, 4, height);
		JSeparator sep = new JSeparator();
		gbl.setConstraints(sep, gbc);
		mainPanel.add(sep);
		start += height;
		
		// Get Panel with all 3 Buttons and add it
		buttons = getButton();
		gbc = MainWindow.makegbc(BUTTON_START, start, 4, height);
		gbl.setConstraints(buttons, gbc);
		mainPanel.add(buttons);
		pack();
	}
	
	/*
	 * returns a JPanel containing three buttons: Save (DB), Save (File), Cancel
	 * (and add to these button action/ key listener)
	 */
	protected JPanel getButton() {
		JButton importBtn, cancelBtn;
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		importBtn = new JButton(GUIConstants.BUTTON_IMPORT);
		importBtn.addActionListener(new ActionListener() {
			/* if the ok button was pressed close dialog */
			public void actionPerformed(ActionEvent _event) {
				ok = true;
				setVisible(false);
				controller
						.setStatus(GUIConstants.CONF_STRAT_DONE);
//				updateValues();
			}
		});
		cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
		cancelBtn.addActionListener(new ActionListener() {
			/* if the ok button was pressed close dialog */
			public void actionPerformed(ActionEvent _event) {
				ok = false;
				setVisible(false);
				controller
						.setStatus(GUIConstants.OPERATION_CANCELED);
			}
		});
		// Import Button
		panel.add(importBtn);
		//	Cancel Button
		panel.add(cancelBtn);
		return panel;
	}

	/*
	 * show this dialog, wait for the user to make a choice and return choosen
	 * object
	 */
	public ArrayList showDialog() {
		ArrayList returnValue = null;        
        setModal(true);
		setVisible(true);
		dispose();
		Object selectedSchema = schema.getSelectedItem();
		String file = instanceFile.getText(); 
		if (ok && selectedSchema!=null && file!= null && !file.equals(GUIConstants.EMPTY)) {
			returnValue = new ArrayList();
			returnValue.add(selectedSchema);
			returnValue.add(file);
		}
		//System.out.println(returnValue);
		return returnValue;
	}
	
}