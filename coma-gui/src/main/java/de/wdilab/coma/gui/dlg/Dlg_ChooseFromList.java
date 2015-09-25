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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This dialog shows a list of Objects to choose from.
 * 
 * @author Sabine Massmann  
 */
public class Dlg_ChooseFromList extends JDialog {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	public static final int SCHEMA = 0;
	public static final int MATCHRESULT_DB = 1;
	public static final int MATCHRESULT_FILE = 2;
	public static final int VARIABLE = 3;
	public static final int MATCHRESULT_TMP = 4;
	public static final int MATCHRESULTPAIR = 5;
	public static final int DELETE_MATCHRESULT_DB = 6;
	public static final int DELETE_SCHEMA_DB = 7;
	public static final int SAVE_MATCHRESULT_FILE = 8;
	//----------------------------------------------
	private JList list;
	private ArrayList objectList = null;
	boolean ok = false;
	private int kind = -1;

	//	 http://www.rgagnon.com/javadetails/java-0219.html
	class ActionJList extends MouseAdapter {
		protected JList givenList;

		public ActionJList(JList _list) {
			givenList = _list;
		}

		public void mouseClicked(MouseEvent _event) {
			if (_event.getClickCount() == 2) {
				int index = givenList.locationToIndex(_event.getPoint());
				givenList.ensureIndexIsVisible(index);
				ok = true;
				setVisible(false);
			}
		}
	}

	/*
	 * Constructor of Dlg_ChooseFromList
	 */
	public Dlg_ChooseFromList(JFrame _parent, String _titel,
			ArrayList _objects, Dimension _dim, int _kind, int _selectionModel) {
		super(_parent, _titel);
		// Handle escape key to close the dialog
		// http://forum.java.sun.com/thread.jsp?thread=462776&forum=57&message=2669506
		javax.swing.KeyStroke escape = javax.swing.KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0, false);
		javax.swing.Action escapeAction = new javax.swing.AbstractAction() {
			public void actionPerformed(ActionEvent _event) {
				dispose();
			}
		};
		getRootPane()
				.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(escape, GUIConstants.ESCAPE);
		getRootPane().getActionMap().put(GUIConstants.ESCAPE, escapeAction);
		// end escape key support
		objectList = _objects;
		kind = _kind;
		DefaultListModel listModel = new DefaultListModel();
		// kind 0=Schema, 1=Matchresult (DB), 2=Matchresult(File)
		switch (_kind) {
			case SCHEMA :
			case DELETE_SCHEMA_DB :
			case MATCHRESULT_DB :
			case DELETE_MATCHRESULT_DB :
			case SAVE_MATCHRESULT_FILE :
			case VARIABLE :
				if ((objectList != null) && (objectList.size() > 0)) {
					for (int i = 0; i < objectList.size(); i++) {
						listModel.addElement(objectList.get(i));
					}
				}
				break;
			case MATCHRESULT_FILE :
				if ((objectList != null) && (objectList.size() > 0)) {
					for (int i = 0; i < objectList.size(); i++) {
						MatchResult mr = (MatchResult) objectList.get(i);
						String source = mr.getSourceGraph().getSource()
								.getName();
						String target = mr.getTargetGraph().getSource()
								.getName();
//						String matcher = mr.getMatcherName();
						String matcher = mr.getMatchInfo();
						listModel.addElement(source + GUIConstants.COLON_SPACE2
								+ target + GUIConstants.BRACKET_LEFT + matcher
								+ GUIConstants.BRACKET_RIGHT);
					}
				}
				break;
			case MATCHRESULT_TMP :
				if ((objectList != null) && (objectList.size() > 0)) {
					for (int i = 0; i < objectList.size(); i++) {
						Object o = objectList.get(i);
						//						if (o instanceof SourceRelationship)
						//							listModel.addElement(o.toString() + " (DB)");
						//						else {
						Object[] os = (Object[]) o;
						// MatchResult
						MatchResult m = (MatchResult) os[0];
						String source = m.getSourceGraph().getSource()
								.getName();
						String target = m.getTargetGraph().getSource()
								.getName();
//						String matcher;
//						if (m.getMatcherName() != null) {
//							matcher = m.getMatcherName();
//						} else if (m.getMatcherConfig() != null) {
//							matcher = m.getMatcherConfig().getName();
//						} else {
//							matcher = GUIConstants.DASH_SPACE;
//						}
						// Matchresult Name in Temporay Matchresult Pane
						String matchresult = (String) os[1];
//						listModel.addElement(source + GUIConstants.DASH_SPACE
//								+ target + GUIConstants.COLON_SPACE2 + matcher
//								+ GUIConstants.BRACKET_LEFT + matchresult
//								+ GUIConstants.BRACKET_RIGHT);
						listModel.addElement(matchresult + GUIConstants.COLON_SPACE2
								+ GUIConstants.BRACKET_LEFT + source 
								+ GUIConstants.DASH_SPACE
								+ target + GUIConstants.BRACKET_RIGHT);
						//						}
					}
				}
				break;
			case MATCHRESULTPAIR :
				if ((objectList != null) && (objectList.size() > 0)) {
					for (int i = 0; i < objectList.size(); i++) {
						ArrayList v = (ArrayList) objectList.get(i);
						SourceRelationship left = (SourceRelationship) v.get(0);
						SourceRelationship right = (SourceRelationship) v
								.get(1);
						listModel.addElement(left.toString() + GUIConstants.ARROW1
								+ right.toString());
					}
				}
				break;
		}
		//Create the list and put it in a scroll pane
		list = new JList(listModel);
		list.addMouseListener(new ActionJList(list)); //david
		list.setBackground(MainWindow.GLOBAL_BACKGROUND);
		list.setSelectionMode(_selectionModel);
		//        list.setSelectedIndex(0);
		JScrollPane listScrollPane = new JScrollPane(list);
		JButton okBtn = new JButton(GUIConstants.BUTTON_OK);
		okBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button was pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				ok = true;
				setVisible(false);
			}
		});
		JButton cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
		cancelBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button was pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				ok = false;
				setVisible(false);
			}
		});
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.add(okBtn);
		buttonPane.add(cancelBtn);
		Container contentPane = getContentPane();
		contentPane.add(listScrollPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		//		pack();
		setSize(_dim);
	}

	/*
	 * show this dialog, wait for the user to make a choice and return choosen
	 * object
	 */
	public Object showDialog() {
		Object returnValue = null;        
        setModal(true);
		setVisible(true);
		dispose();
		if (ok && (list.getSelectedValue() != null)) {
			switch (kind) {
				case SCHEMA :
				case MATCHRESULT_DB :
					returnValue = list.getSelectedValue();
					break;
				case MATCHRESULT_FILE :
				case MATCHRESULTPAIR :
					returnValue = objectList.get(list
							.getSelectedIndex());
					break;
				case MATCHRESULT_TMP :
				case DELETE_MATCHRESULT_DB :
				case DELETE_SCHEMA_DB :
				case SAVE_MATCHRESULT_FILE :
				case VARIABLE :
					returnValue = getListElements();
					break;
			}
		}
		//System.out.println(returnValue);
		return returnValue;
	}

	public void selectSchema(String _schema) {
		if (_schema != null) {
			DefaultListModel listModel = (DefaultListModel) list.getModel();
			int index = -1;
			if ((listModel != null) && (listModel.size() > 0)) {
				for (int i = 0; i < listModel.size(); i++) {
					Object o = listModel.get(i);
					if (o instanceof Source
							&& ((Source) o).getName().equals(_schema)) {
						index = i;
					}
				}
			}
			list.setSelectedIndex(index);
		}
	}

	public void addInfoText(String _info1, String _info2, String _info3) {
		Container contentPane = getContentPane();
		JPanel panel = new JPanel(new BorderLayout());
		if (_info1 != null) {
			JLabel label1 = new JLabel(_info1);
			panel.add(label1, BorderLayout.NORTH);
		}
		if (_info2 != null) {
			JLabel label2 = new JLabel(_info2);
			panel.add(label2, BorderLayout.CENTER);
		}
		if (_info3 != null) {
			JLabel label3 = new JLabel(_info3);
			panel.add(label3, BorderLayout.SOUTH);
		}
		contentPane.add(panel, BorderLayout.NORTH);
	}

	/*
	 * returns all selected Elements in the List in an ArrayList except the case
	 * there is only one selected item
	 */
	private Object getListElements() {
		int[] indices = list.getSelectedIndices();
		if (indices == null) {
			return null;
		}
		ArrayList objects = new ArrayList();
		if (indices.length == 1) {
			return objectList.get(indices[0]);
		}
		for (int i = 0; i < indices.length; i++) {
			objects.add(objectList.get(indices[i]));
		}
		return objects;
	}

	public boolean isOk() {
		return ok;
	}
}