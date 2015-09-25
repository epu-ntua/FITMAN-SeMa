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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.TextFrame;

/**
 * This dialog shows the definition of one workflow variable.
 * 
 * @author Sabine Massmann
 */
public class Dlg_WorkflowVariables extends Dlg {
	Controller controller;
	String[][] data; // variables
	JTable table; // variables table

	// http://www.rgagnon.com/javadetails/java-0219.html
	class ActionJList extends MouseAdapter {
		JList list;

		public ActionJList() {
			super();
		}

		public ActionJList(JList _list) {
			list = _list;
		}

		public void mouseClicked(MouseEvent _event) {
			if (table != null && !_event.getSource().equals(table)) {
				table.clearSelection();
			}
			
			if (_event.getClickCount() == 2 ) {
				setVisible(false);
				
				if (table.getSelectedRow() >= 0) {
					String name = data[table.getSelectedRow()][0];
					String value = data[table.getSelectedRow()][1];
					TextFrame frame = new TextFrame(controller, name, value, GUIConstants.CONFIGURE_WORKFLOW, 
							TextFrame.WORKFLOW, MainWindow.DIM_INFO);
					frame.setLocation(controller.getDialogPosition());
					frame.setVisible(true);
					frame.setDlgParent(Dlg_WorkflowVariables.this);
				}
				
				controller.setStatus(GUIConstants.CONFIGURE_NA);
			}
		}
	}

	/*
	 * Constructor of Dlg_ExistingMatcher
	 */
	public Dlg_WorkflowVariables(JFrame _parent, final Controller _controller) {
		super(_parent, GUIConstants.EX_WORKFLOWVARIABLES);
		controller = _controller;
		HashMap<String, String>  workflowVariables= _controller.getAccessor().getWorkflowVariables();
		if (workflowVariables==null){
			// ERROR - no matcher
//			return;
			// import default
			_controller.importDefaultVariables();
			workflowVariables= _controller.getAccessor().getWorkflowVariables();
		}
		init(workflowVariables);
	}
	
	
	private JScrollPane getScrollPane(HashMap<String, String>  workflowVariables){
		if (workflowVariables.isEmpty()){
			return null;
		}
		data = new String[workflowVariables.size()][2];
		
		ArrayList<String> variables = new ArrayList<String>(workflowVariables.keySet());
		 Collections.sort(variables);
		
		String[] colHeads = {"name", "value"};
		// get for all variables the value to put it into a table
		for (int i = 0; i < variables.size(); i++) {
				data[i][0] = variables.get(i);
				data[i][1] = workflowVariables.get(data[i][0]);		
		}
		// crate table column model
		DefaultTableColumnModel cm = new DefaultTableColumnModel();
		for (int i = 0; i < colHeads.length; ++i) {
			TableColumn col = new TableColumn(i, i == 0 ? 50 : 250);
			col.setHeaderValue(colHeads[i]);
			cm.addColumn(col);
		}
		// crate table model
		TableModel tm = new TableModel(data);
		// Tabelle erzeugen und ContentPane fuellen
		table = new JTable(tm, cm);
		table.setDefaultRenderer(Object.class,
				new ColoredTableCellRenderer());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionInterval(0, 0);
		table.addMouseListener(new ActionJList()); // david
		// create a scrollpane containing this table
		JScrollPane pane = new JScrollPane(table);
		return pane;
	}
	
	private void init(HashMap<String, String>  workflowVariables) {
		getContentPane().setLayout(new BorderLayout());

		// get all pre-defined workflow variables
		JScrollPane pane = getScrollPane(workflowVariables);
		
		getContentPane().add(pane, BorderLayout.CENTER);
		JPanel button = getButton();
		getContentPane().add(button, BorderLayout.SOUTH);
		pack();
	}

	JPanel getButton() {
		// create an OK Button and add listener
		JButton configureBtn = new JButton(GUIConstants.BUTTON_CONFIGURE);
		configureBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button was pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				setVisible(false);
				if (table.getSelectedRow() >= 0) {
					String name = data[table.getSelectedRow()][0];
					String value = data[table.getSelectedRow()][0];
					TextFrame frame = new TextFrame(controller, name, value, GUIConstants.CONFIGURE_WORKFLOW,
							TextFrame.WORKFLOW, MainWindow.DIM_INFO);
					frame.setLocation(controller.getDialogPosition());
					frame.setVisible(true);
				}
				controller.setStatus(GUIConstants.CONFIGURE_NA);
			}
		});
		// create an Cancel Button and add listener
		JButton cancelBtn = new JButton(GUIConstants.BUTTON_DONE);
		cancelBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button was pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				controller
						.setStatus(GUIConstants.EX_VARIABLES_CLOSED);
				setVisible(false);
			}
		});
		// create a panel
		JPanel button = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		// rename.setLayout(new GridLayout(1, 2));
		// add both buttons to the panel
		button.add(configureBtn);
		button.add(cancelBtn);
		return button;
	}

	/*
	 * TableModel extends AbstractTableModel
	 */
	private class TableModel extends AbstractTableModel {
		private Object[][] tableData;

		/*
		 * Constructor of TableModel
		 */
		public TableModel(Object[][] _data) {
			super();
			tableData = _data;
		}

		/*
		 * returns the number of rows that the given data has
		 */
		public int getRowCount() {
			return tableData.length;
		}

		/*
		 * returns the number of columns that the given data has
		 */
		public int getColumnCount() {
			return tableData[0].length;
		}

		/*
		 * return the value at a given row + column
		 */
		public Object getValueAt(int _row, int _column) {
			Object current = tableData[_row][_column];
			if (current instanceof String) {
				return current;
			}
//			if (current instanceof MatcherConfig) {
//				return ((MatcherConfig) current).getName();
//			}
			return current.toString();
		}
	}
	/*
	 * ColoredTableCellRenderer implements TableCellRenderer
	 */
	private class ColoredTableCellRenderer implements TableCellRenderer {
		/*
		 * returns the component as it shall be shown for a given value of the
		 * table depending of its place (row, column) and its state (selected,
		 * focused)
		 */
		public Component getTableCellRendererComponent(JTable _givenTable,
				Object _value, boolean _isSelected, boolean _hasFocus,
				int _row, int _column) {
			// Label erzeugen
			JLabel label = new JLabel((String) _value);
			label.setOpaque(true);
			Border b = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			label.setBorder(b);
			label.setFont(_givenTable.getFont());
			label.setForeground(_givenTable.getForeground());
			label.setBackground(MainWindow.GLOBAL_BACKGROUND);
			if (_hasFocus) {
				label.setBackground(MainWindow.SELECTED_BACKGROUND);
				label.setBorder(BorderFactory.createLineBorder(
						MainWindow.BORDER, 1));
			} else if (_isSelected) {
				label.setBackground(MainWindow.SELECTED_BACKGROUND);
			}
			// Angezeigte Spalte in Modellspalte umwandeln
			_column = _givenTable.convertColumnIndexToModel(_column);
			if (_column == 0) {
				label.setFont(MainWindow.FONT12_BOLD);
			} else {
				label.setFont(MainWindow.FONT11);
			}
			return label;
		}
	}
}