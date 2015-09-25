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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;

/**
 * This dialog offers to create, delete or change a domain.
 * 
 * @author Sabine Massmann
 */
public class Dlg_Domain extends Dlg {
	private Container mainPanel;
	Controller controller;
	JTextField domain  = new JTextField();
	JComboBox combo = null;
	JList schemaList = null;
	/*
	 * Constructor of Dlg_ExistingMatcher
	 */
	public Dlg_Domain(JFrame _parent, String _title, Controller _controller) {
		super(_parent, _title);
		controller = _controller;
		getContentPane().setLayout(new BorderLayout());
		mainPanel = new JPanel(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		if(_title.equals(GUIConstants.DEL_DOMAIN)){
			ArrayList domains = controller.getManagementPane().getAllDomains();
//			domains.remove(GUIConstants.SHOW_ALL_HTML);
//			domains.remove(GUIConstants.NO_DOMAIN_HTML);
			domains.remove(GUIConstants.SHOW_ALL_NORMAL);
			domains.remove(GUIConstants.NO_DOMAIN_NORMAL);
			combo = new JComboBox(domains.toArray());
			combo.setMaximumRowCount(MainWindow.MAX_ROW);
			combo.setBackground(MainWindow.GLOBAL_BACKGROUND);
			mainPanel.add(combo, BorderLayout.CENTER);
			mainPanel.add(getButtonsDelete(), BorderLayout.SOUTH);
		} else if(_title.equals(GUIConstants.CREATE_DOMAIN)){
			mainPanel.add(domain, BorderLayout.CENTER);
			mainPanel.add(getButtonsCreate(), BorderLayout.SOUTH);
		} else if(_title.equals(GUIConstants.CHANGE_DOMAIN)){
			ArrayList schemas = controller.getManagementPane().getRepSchemas();			
			schemaList = new JList(schemas.toArray());
			schemaList.setBackground(MainWindow.GLOBAL_BACKGROUND);
			schemaList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			JScrollPane schemaPane = new JScrollPane(schemaList);
			mainPanel.add(schemaPane, BorderLayout.NORTH);
			ArrayList domains = controller.getManagementPane().getAllDomains();
//			domains.remove(GUIConstants.SHOW_ALL_HTML); // --> no real domain
			domains.remove(GUIConstants.SHOW_ALL_NORMAL); // --> no real domain
			// change the html representation to normal...
//			domains.set(domains.indexOf(GUIConstants.NO_DOMAIN_HTML), GUIConstants.NO_DOMAIN_NORMAL);
			combo = new JComboBox(domains.toArray());
			combo.setMaximumRowCount(MainWindow.MAX_ROW);
			combo.setBackground(MainWindow.GLOBAL_BACKGROUND);
			mainPanel.add(combo, BorderLayout.CENTER);
			mainPanel.add(getButtonsChange(), BorderLayout.SOUTH);
		}
		pack();
	}

	/*
	 * return a JPanel with 3 buttons- ok, add, delete
	 */
	private JPanel getButtonsDelete() {
		JButton deleteBtn, cancelBtn;
		// create an Add Button and add listener
		deleteBtn = new JButton(GUIConstants.BUTTON_DELETE);
		deleteBtn.addActionListener(new ActionListener() {
			/*
			 * if the add button was pressed open a new dialog for entering 2
			 * words
			 */
			public void actionPerformed(ActionEvent _event) {
				String domainName = combo.getSelectedItem().toString();
				if (controller.getManagementPane().domainContainSchemas(domainName)){
					  JOptionPane.showMessageDialog(Dlg_Domain.this, "Domain is not empty. Please move all schemas before deleting domain.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
				}
				controller.getManagementPane().deleteDomain(domainName);				
				setVisible(false);
			}
		});
		// create an Cancel Button and add listener
		cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
		cancelBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button was pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				setVisible(false);
			}
		});
		// create a panel
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.setLayout(new GridLayout(1, 2));
		// add all buttons to the panel
		panel.add(deleteBtn);
		panel.add(cancelBtn);
		return panel;
	}
	
	/*
	 * return a JPanel with 3 buttons- ok, add, delete
	 */
	private JPanel getButtonsCreate() {
		JButton createBtn, cancelBtn;
		// create an Add Button and add listener
		createBtn = new JButton(GUIConstants.BUTTON_CREATE);
		createBtn.addActionListener(new ActionListener() {
			/*
			 * if the add button was pressed open a new dialog for entering 2
			 * words
			 */
			public void actionPerformed(ActionEvent _event) {
				String domainName = domain.getText();
				if (domainName.length()==0){
					  JOptionPane.showMessageDialog(Dlg_Domain.this, "Domain name is empty. Please type at least one character!", "Error", JOptionPane.ERROR_MESSAGE);
						return;
				}
				if (controller.getManagementPane().containsDomain(domainName)){
				    JOptionPane.showMessageDialog(Dlg_Domain.this, "Domain already exists. Please use another name!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				controller.getManagementPane().addDomain(domainName);				
				setVisible(false);
			    JOptionPane.showMessageDialog(Dlg_Domain.this, "Domain \""+domainName+"\" has been created. You have to move schemas to this domain to save it to the repository ", "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		// create an Cancel Button and add listener
		cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
		cancelBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button was pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				setVisible(false);
			}
		});
		// create a panel
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.setLayout(new GridLayout(1, 2));
		// add all buttons to the panel
		panel.add(createBtn);
		panel.add(cancelBtn);
		return panel;
	}
	
	/*
	 * return a JPanel with 3 buttons- ok, add, delete
	 */
	private JPanel getButtonsChange() {
		JButton changeBtn, cancelBtn;
		// create an Add Button and add listener
		changeBtn = new JButton(GUIConstants.BUTTON_CHANGE);
		changeBtn.addActionListener(new ActionListener() {
			/*
			 * if the add button was pressed open a new dialog for entering 2
			 * words
			 */
			public void actionPerformed(ActionEvent _event) {
				String domainName = combo.getSelectedItem().toString();
				Object[] selected = schemaList.getSelectedValues();
				if (selected==null || selected.length==0){
				    JOptionPane.showMessageDialog(Dlg_Domain.this, "No schemas have been selected!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				controller.changeDomain(domainName,selected);				
				setVisible(false);
//			    JOptionPane.showMessageDialog(Dlg_Domain.this, "Domain \""+domainName+"\" has been created. You have to move schemas to this domain to save it to the repository ", "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		// create an Cancel Button and add listener
		cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
		cancelBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button was pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				setVisible(false);
			}
		});
		// create a panel
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.setLayout(new GridLayout(1, 2));
		// add all buttons to the panel
		panel.add(changeBtn);
		panel.add(cancelBtn);
		return panel;
	}
}