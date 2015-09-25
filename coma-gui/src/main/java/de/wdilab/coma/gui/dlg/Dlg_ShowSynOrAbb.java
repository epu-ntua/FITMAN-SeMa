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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.insert.metadata.ListParser;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Source;

/**
 * This dialog shows existing synonym or abbreviation pairs. It allows to 
 * add an entry or remove one/all. 
 * 
 * @author Sabine Massmann
 */
public class Dlg_ShowSynOrAbb extends Dlg {
	private Container mainPanel;
	Controller controller;
	boolean synonyms;
	JTable table;
	String[][] data;
	ArrayList<String> addData, removeData;
	JScrollPane pane;

	/*
	 * Constructor of Dlg_ExistingMatcher
	 */
	public Dlg_ShowSynOrAbb(JFrame _parent, String _titel,
			Controller _controller, boolean _synonyms) {
		super(_parent, _titel);
		ArrayList<ArrayList<String>> synPairList;
		ArrayList<String> abbrevList, fullFormList, wordList;
		controller = _controller;
		synonyms = _synonyms;
		addData = new ArrayList<String>();
		removeData = new ArrayList<String>();
		//		setResizable(false);
		// get all existing matcher in the database
		if (_synonyms) {
			synPairList = new ArrayList<ArrayList<String>>();
			wordList = new ArrayList<String>();
			_controller.getAccessor().loadSynonyms(Repository.SRC_SYNONYM, 
					synPairList, wordList);
			data = new String[synPairList.size()][2];
			if (synPairList.size() > 0) {
				for (int i = 0; i < synPairList.size(); i++) {
					data[i][0] = ((ArrayList) synPairList.get(i)).get(0)
							.toString();
					data[i][1] = ((ArrayList) synPairList.get(i)).get(1)
							.toString();
				}
			}
		} else {
			abbrevList = new ArrayList<String>();
			fullFormList = new ArrayList<String>();
			_controller.getAccessor().loadList(Repository.SRC_ABBREV, 
					abbrevList, fullFormList);
			data = new String[abbrevList.size()][2];
			if (abbrevList.size() > 0) {
				for (int i = 0; i < abbrevList.size(); i++) {
					data[i][0] = abbrevList.get(i).toString();
					data[i][1] = fullFormList.get(i).toString();
				}
			}
		}
		getContentPane().setLayout(new BorderLayout());
		mainPanel = new JPanel(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		newPane();
		mainPanel.add(getButtons(), BorderLayout.SOUTH);
		pack();
	}

	/*
	 * (remove old plane and) create a new pane containing the data sets and add
	 * it to the current dialog
	 */
	void newPane() {
		if (pane != null) {
			mainPanel.remove(pane);
		}
		//create table model
		TableModel tm = new TableModel(data);
		//create table column model
		DefaultTableColumnModel cm = new DefaultTableColumnModel();
		for (int i = 0; i < 2; ++i) {
			TableColumn col = new TableColumn(i, 60);
			cm.addColumn(col);
		}
		//Tabelle erzeugen und ContentPane fuellen
		table = new JTable(tm, cm);
		table.setDefaultRenderer(Object.class,
				new ColoredTableCellRenderer());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (data != null && data.length > 0) {
			table.setRowSelectionInterval(0, 0);
		}
		// create a scrollpane containing this table
		Dimension dim;
		if (pane != null) {
			dim = pane.getSize();
		} else {
			dim = MainWindow.DIM_MEDIUM;
		}
		pane = new JScrollPane(table);
		pane.setPreferredSize(dim);
		mainPanel.add(pane, BorderLayout.CENTER);
		mainPanel.repaint();
	}

	/*
	 * remove a pair of word from current list of either synonyms or
	 * abbreviation remove pair also to the current table
	 */
	void removeData(String _data1, String _data2) {
		String newString = _data1 + GUIConstants.COMMA_SPACE + _data2;
		String newStringUpper = newString.toUpperCase();
		if (removeData.contains(newStringUpper)) {
			// if already contained, don't add it
			return;
		}
		//remove pair from table
		String[][] newData = new String[data.length - 1][2];
		int j = 0;
		for (int i = 0; i < data.length; i++) {
			if (!data[i][0].equals(_data1)
					|| !data[i][1].equals(_data2)) {
				newData[j][0] = data[i][0];
				newData[j][1] = data[i][1];
				j++;
			}
		}
		data = newData;
		newPane();
		pack();
		if (addData.contains(newString)) {
			// case: first add, then delete a pair -> remove from add, don't add
			// to delete
			addData.remove(newString);
			return;
		}
		removeData.add(newStringUpper);
	}

	/*
	 * add a pair of word to either synonyms or abbreviation add pair also to
	 * the current table
	 */
	void addData(String _data1, String _data2) {
		String newString = _data1 + GUIConstants.COMMA_SPACE + _data2;
		String newStringUpper = newString.toUpperCase();
		if (addData.contains(newString)) {
			// if already contained, don't add it
			return;
		}
		// add pair to table
		String[][] newData = new String[data.length + 1][2];
		for (int i = 0; i < data.length; i++) {
			newData[i][0] = data[i][0];
			newData[i][1] = data[i][1];
		}
		newData[data.length][0] = _data1;
		newData[data.length][1] = _data2;
		data = newData;
		newPane();
		pack();
		if (removeData.contains(newStringUpper)) {
			// case: first delete, then add a pair -> remove from delete, don't
			// add to add
			removeData.remove(newStringUpper);
			return;
		}
		addData.add(newString);
	}

	/*
	 * change the abbreviation/synonyms file by adding and deleting word pairs
	 * import changed file to repository
	 */
	public void changeFile() {
		if (removeData.size() == 0 && addData.size() == 0) {
			return;
		}
		String fileName;
		int sourceID;
		if (synonyms) {
			fileName = controller.getFileSyn();
			sourceID = controller.getAccessor().getSourceIdsWithNameAndUrl(Repository.SRC_SYNONYM, null).get(0);
		} else {
			fileName = controller.getFileAbb();
			sourceID = controller.getAccessor().getSourceIdsWithNameAndUrl(Repository.SRC_ABBREV,null).get(0);
		}
		File file = new File(GUIConstants.DOT, fileName);
		//	System.out.println("length file: " + file.length());
		// change the needed File
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader f;
		BufferedWriter f2;
		String line;
		try {
			// 1. read original file
			f = new BufferedReader(new FileReader(file));
			while ((line = f.readLine()) != null) {
				list.add(line);
			}
			f.close();
		} catch (IOException e) {
			if (synonyms) {
				controller.setStatus(GUIConstants.ERROR_SYN);
			} else {
				controller.setStatus(GUIConstants.ERROR_ABB);
			}
		}
		try {
			f2 = new BufferedWriter(new FileWriter(file));
			// 2. rewrite file
			if (list.size() > 0)
				for (int i = 0; i < list.size(); i++) {
					line = list.get(i);
					// System.out.println(line);
					// BUT: don't write a pair, that shall be deleted
					if (!removeData.contains(line.toUpperCase())) {
						f2.write(line);
						f2.newLine();
					}
					//					else {
					//						// System.out.println("remove: " + line);
					//					}
				}
			// 3. AND add all pairs that shall be added
			if (addData.size() > 0) {
				for (int i = 0; i < addData.size(); i++) {
					//					System.out.println("add: " + (String) addData.get(i));
					f2.write(( addData.get(i)).toCharArray());
					f2.newLine();
				}
			}
			f2.close();
		} catch (IOException e) {
			if (synonyms) {
				controller.setStatus(GUIConstants.ERROR_SYN);
			} else {
				controller.setStatus(GUIConstants.ERROR_ABB);
			}
		}
		// delete old Abbreviaton/Synonym data sets
		controller.getAccessor().deleteSource(sourceID);
		Source schema = controller.getManager().getSource(sourceID);
		HashSet<Integer> sourceRelIds = controller.getAccessor().getSourceRelId(schema.getId());
		if (sourceRelIds != null) {
			for (Integer rel_id : sourceRelIds) {
				controller.getAccessor().deleteSourceRel(rel_id);
			}
		}
		/*
		 * controller.getAccessor().dropViewTables(false);
		 * controller.getAccessor().deleteSourcePaths();
		 * controller.updateAllOld(false, false);
		 */
		// import changed file to repository
		if (synonyms) {
			ListParser parser = new ListParser(true);
			parser.parseSynonym(fileName);
		} else {
			ListParser parser = new ListParser(true);
			parser.parseAbbreviation(fileName);
		}
		// load the Respository new to the GUI
		//controller.updateAllOld(true, true);
		controller.updateAll(true); //parse and import
	}

	/*
	 * return a JPanel with 3 buttons- ok, add, delete
	 */
	private JPanel getButtons() {
		JButton 
//		addBtn, deleteBtn, 
		okBtn;
//		// create an Add Button and add listener
//		addBtn = new JButton(GUIConstants.BUTTON_ADD);
//		addBtn.addActionListener(new ActionListener() {
//			/*
//			 * if the add button was pressed open a new dialog for entering 2
//			 * words
//			 */
//			public void actionPerformed(ActionEvent _event) {
//				Dlg_AddSynOrAbb dialog = new Dlg_AddSynOrAbb(
//						Dlg_ShowSynOrAbb.this, GUIConstants.ABB,
//						synonyms);
//				dialog.setLocation(controller
//						.getDialogPosition());
//				dialog.setVisible(true);
//			}
//		});
//		// create an Add Button and add listener
//		deleteBtn = new JButton(GUIConstants.BUTTON_DELETE);
//		deleteBtn.addActionListener(new ActionListener() {
//			/*
//			 * if the delete button was pressed close dialog
//			 */
//			public void actionPerformed(ActionEvent _event) {
//				if (table != null
//						&& data != null
//						&& data.length > 0) {
//					int selected = table.getSelectedRow();
//					String word1 = data[selected][0];
//					String word2 = data[selected][1];
//					// delete the selected
//					int res = JOptionPane.showConfirmDialog(
//							controller.getMainWindow(),
//							GUIConstants.DEL_QUESTION1 + word1
//									+ GUIConstants.DEL_QUESTION2 + word2
//									+ GUIConstants.DEL_QUESTION3
//									+ GUIConstants.OK_CONTINUE, GUIConstants.DELETE,
//							JOptionPane.OK_CANCEL_OPTION);
//					if (res != JOptionPane.OK_OPTION) {
//						return;
//					}
//					removeData(word1, word2);
//				}
//			}
//		});
		// create an Cancel Button and add listener
		okBtn = new JButton(GUIConstants.BUTTON_OK);
		okBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button was pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				dispose();
				changeFile();
			}
		});
		// create a panel
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.setLayout(new GridLayout(1, 2));
		// add all buttons to the panel
		panel.add(okBtn);
//		panel.add(addBtn);
//		panel.add(deleteBtn);
		return panel;
	}

	/*
	 * separate small dialog for entering two words that shall be add to the
	 * repository either as a pair of synonyms or abbreviations
	 */
	class Dlg_AddSynOrAbb extends JDialog {
		boolean synomyms;
		private JButton addBtn2;
		private JButton cancelBtn;
		JTextField word1;
		JTextField word2;

		/*
		 * Constructor of Dlg_ExistingMatcher
		 */
		public Dlg_AddSynOrAbb(final Dlg_ShowSynOrAbb _parent, String _titel,
				boolean _synonyms) {
			super(_parent, _titel);
			synomyms = _synonyms;
			getContentPane().setLayout(new GridLayout(3, 2));
			if (_synonyms) {
				// 2 words -> synonym
				getContentPane().add(new JLabel(GUIConstants.WORD1));
			} else {
				// 2 words -> abbreviation, full form
				getContentPane().add(new JLabel(GUIConstants.ABBREVIATION_SMALL));
			}
			word1 = new JTextField(20);
			getContentPane().add(word1);
			if (_synonyms) {
				// 2 words -> synonym
				getContentPane().add(new JLabel(GUIConstants.WORD2));
			} else {
				// 2 words -> abbreviation, full form
				getContentPane().add(new JLabel(GUIConstants.FULL_FORM));
			}
			word2 = new JTextField(20);
			getContentPane().add(word2);
			addBtn2 = new JButton(GUIConstants.BUTTON_ADD);
			addBtn2.addActionListener(new ActionListener() {
				/*
				 * if the add button was pressed close dialog
				 */
				public void actionPerformed(ActionEvent _event) {
					// check if both fields are not empty
					String word1Text = word1.getText();
					String word2Text = word2.getText();
					if (word1Text.equals(GUIConstants.EMPTY)
							|| word2Text.equals(GUIConstants.EMPTY)) {
						JOptionPane.showMessageDialog(Dlg_ShowSynOrAbb.this,
								GUIConstants.WORD_NOT_VALID, GUIConstants.INFORMATION,
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					String newString = word1Text + GUIConstants.COMMA_SPACE
							+ word2Text;
					if (addData.contains(newString)) {
						// inform user when pair already exists (in
						// addData-List)
						JOptionPane.showMessageDialog(Dlg_ShowSynOrAbb.this,
								GUIConstants.PAIR_EXISTS, GUIConstants.INFORMATION,
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					int contained = -1;
					for (int i = 0; i < data.length; i++) {
						if (data[i][0].equalsIgnoreCase(word1Text)
								&& data[i][1].equalsIgnoreCase(word2Text)) {
							contained = i;
							break;
						}
					}
					if (contained > -1) {
						// inform user when pair already exists (in
						// repository-list)
						JOptionPane
								.showMessageDialog(
										Dlg_ShowSynOrAbb.this,
										GUIConstants.PAIR_EXISTS
												+ data[contained][0]
												+ GUIConstants.COMMA_SPACE
												+ data[contained][1],
										GUIConstants.INFORMATION,
										JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					dispose();
					_parent.addData(word1Text, word2Text);
				}
			});
			// create an Cancel Button and add listener
			cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
			cancelBtn.addActionListener(new ActionListener() {
				/*
				 * if the cancel button was pressed close dialog
				 */
				public void actionPerformed(ActionEvent _event) {
					dispose();
				}
			});
			// add all buttons to the panel
			getContentPane().add(addBtn2);
			getContentPane().add(cancelBtn);
			pack();
		}
	}
	/*
	 * TableModel extends AbstractTableModel
	 */
	private class TableModel extends AbstractTableModel {
		private String[][] givenData;

		/*
		 * Constructor of TableModel
		 */
		public TableModel(String[][] _data) {
			super();
			givenData = _data;
		}

		public void setData(String[][] _data) {
			givenData = _data;
		}

		/*
		 * returns the number of rows that the given data has
		 */
		public int getRowCount() {
			return givenData.length;
		}

		/*
		 * returns the number of columns that the given data has
		 */
		public int getColumnCount() {
			return givenData[0].length;
		}

		/*
		 * return the value at a given row + column
		 */
		public Object getValueAt(int _row, int _column) {
			return givenData[_row][_column];
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
		public Component getTableCellRendererComponent(JTable _table,
				Object _value, boolean _isSelected, boolean _hasFocus,
				int _row, int _column) {
			//Label erzeugen
			JLabel label = new JLabel((String) _value);
			label.setOpaque(true);
			Border b = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			label.setBorder(b);
			label.setFont(_table.getFont());
			label.setForeground(_table.getForeground());
			label.setBackground(MainWindow.GLOBAL_BACKGROUND);
			if (_hasFocus) {
				label.setBackground(MainWindow.SELECTED_BACKGROUND);
				label.setBorder(BorderFactory.createLineBorder(
						MainWindow.BORDER, 1));
			} else if (_isSelected) {
				label.setBackground(MainWindow.SELECTED_BACKGROUND);
			}
			//Angezeigte Spalte in Modellspalte umwandeln
			_column = _table.convertColumnIndexToModel(_column);
			return label;
		}
	}
}