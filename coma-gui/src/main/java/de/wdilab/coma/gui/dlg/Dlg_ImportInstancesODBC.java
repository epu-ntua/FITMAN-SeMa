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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Source;

/**
 * This dialog enables the user to import instances for an odbc model.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class Dlg_ImportInstancesODBC extends JDialog {
	Controller controller = null;
	private JButton cancelButton;
	private JButton importButton;
	private JPanel pane;
	JTextField odbcEntry;
	JTextField userName;
	// JTextField userPass;
	JPasswordField userPass;
	JComboBox schemaName;

	public Dlg_ImportInstancesODBC() {
		buildGUI();
	}

	public Dlg_ImportInstancesODBC(JFrame _parent, final Controller _controller) {
		super(_parent);
		buildGUI();
		controller = _controller;
		_controller.setStatus(GUIConstants.SAVE_SCHEMA_DB);
	}

	private void buildGUI() {
		GridBagConstraints c = new GridBagConstraints();
		pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		pane.setBorder(new EmptyBorder(
				new Insets(11, 11, 12, 12)));
		// c.fill = c.VERTICAL;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 2;
		c.weighty = 2;
		odbcEntry = new JTextField();
		userName = new JTextField();
		// userPass = new JTextField();
		userPass = new JPasswordField(15);
		userPass.setEchoChar('*');
		schemaName = new JComboBox();
//		schemaName.setEditable(true);
		cancelButton = new JButton();
		importButton = new JButton();
		setTitle("Import ODBC Schema");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent _event) {
				setVisible(false);
			}
		});
		c.gridx = 0;
		c.gridy = 0;
		pane.add(new JLabel("ODBC Entry: "), c);
		c.gridx = 1;
		c.gridy = 0;
		pane.add(odbcEntry, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(new JLabel("User Name: "), c);
		c.gridx = 1;
		c.gridy = 1;
		pane.add(userName, c);
		c.gridx = 0;
		c.gridy = 2;
		pane.add(new JLabel("Password: "), c);
		c.gridx = 1;
		c.gridy = 2;
		pane.add(userPass, c);
		c.gridx = 0;
		c.gridy = 3;
		pane.add(new JLabel("Schema: "), c);
		c.gridx = 1;
		c.gridy = 3;
		pane.add(schemaName, c);
		schemaName
				.addPopupMenuListener(new PopupMenuListener() {
					public void popupMenuCanceled(
							PopupMenuEvent _event) {}

					public void popupMenuWillBecomeInvisible(
							PopupMenuEvent _event) {}

					public void popupMenuWillBecomeVisible(
							PopupMenuEvent _event) {
						schemaNamePopupMenuWillBecomeVisible(_event);
					}
				});
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(importButton, BorderLayout.CENTER);
		panel.add(cancelButton, BorderLayout.EAST);
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.insets = new Insets(5, 0, 0, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTH;
		pane.add(panel, c);
		// c.gridx = 0;
		// c.gridy = 4;
		importButton.setText("Import");
		importButton
				.addActionListener(new ActionListener() {
					public void actionPerformed(
							ActionEvent _event) {
						Source source = (Source) schemaName.getSelectedItem();
						if (source==null){
							JOptionPane.showMessageDialog(Dlg_ImportInstancesODBC.this,"No source selected! Please select a source schema.",
									"Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						importButtonActionPerformed(_event);
					}
				});
		// c.fill = GridBagConstraints.NONE;
		// c.anchor = GridBagConstraints.EAST;
		// pane.add(importButton, c);
		// c.anchor = GridBagConstraints.WEST;
		//        
		// c.gridx = 1;
		// c.gridy = 4;
		// pane.add(cancelButton, c);
		cancelButton.setText("Cancel");
		cancelButton
				.addActionListener(new ActionListener() {
					public void actionPerformed(
							ActionEvent _event) {
						cancelButtonActionPerformed(_event);
					}
				});
		getContentPane().add(pane, BorderLayout.CENTER);
		pack();
	}

	void schemaNamePopupMenuWillBecomeVisible(PopupMenuEvent e) {
		schemaName.removeAllItems();
		ArrayList sources = controller.getManager().getAllSources();
		for (int i = 0; i < sources.size(); i++) {
			Source source = (Source) sources.get(i);
			if (source.getType()==Source.TYPE_ODBC){
				schemaName.addItem(source);
			}
		}
	}

	void importButtonActionPerformed(ActionEvent _event) {
		setVisible(false);
		String entry = odbcEntry.getText();
		String user = userName.getText();
		char[] password = userPass.getPassword();
		String pass = new String(password);
		Source source = (Source) schemaName.getSelectedItem();
		Graph graph = controller.getManager().loadGraph(source, true, false);
		controller.parseInstances(graph, true, entry, user, pass);
	}

	void cancelButtonActionPerformed(ActionEvent _event) {
		controller.setStatus(GUIConstants.IMPORT_SCHEMAS_ABORTED);
		setVisible(false);
	}


	public static void main(String args[]) {
		new Dlg_ImportInstancesODBC().setVisible(true);
	}
}