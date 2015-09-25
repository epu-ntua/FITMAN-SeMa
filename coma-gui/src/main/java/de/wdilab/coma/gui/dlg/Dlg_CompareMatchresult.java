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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.TextFrame;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;

/**
 * Dlg_CompareMatchresult extends Dlg and offers the possibility to compare one 
 * matchresults (intended) with one or all matchresults (with the same source and target,
 * including itself).
 * 
 * @author Sabine Massmann
 */
public class Dlg_CompareMatchresult extends Dlg {
	protected Container mainPanel;
	protected JButton compareBtn, cancelBtn;
	int stratStart;
	int stratHeight;
	// Matcher
	JComboBox intendedMatchresults;
	JComboBox testMatchresults;
	Controller controller;
	JRadioButtonMenuItem allMatchresults, singleMatchresult;
	ArrayList matchresultList;
	JPanel panel;	
	MatchResult intendedResult;
	
	/*
	 * Constructor of Dlg_NewMatcher
	 */
	public Dlg_CompareMatchresult(JFrame _parent, final Controller _controller, MatchResult _result) {
		super(_parent, GUIConstants.COMPARE_MATCHRESULTS);
		controller = _controller;
		intendedResult = _result;
		//		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		controller.setStatus(GUIConstants.COMPARE_MATCHRESULT_WITH);
		// Handle escape key to close the dialog
		// http://forum.java.sun.com/thread.jsp?thread=462776&forum=57&message=2669506
		javax.swing.KeyStroke escape = javax.swing.KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0, false);
		javax.swing.Action escapeAction = new javax.swing.AbstractAction() {
			public void actionPerformed(ActionEvent _event) {
				dispose();
				_controller.setStatus(GUIConstants.COMPARE_MATCHRESULTS_CANCELED);
			}
		};
		getRootPane()
				.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(escape, GUIConstants.ESCAPE);
		getRootPane().getActionMap().put(GUIConstants.ESCAPE, escapeAction);
		// end escape key support
		init();
	}

	/*
	 * returns a JPanel containing three buttons: Save (DB), Save (File), Cancel
	 * (and add to these button action/ key listener)
	 */
	protected JPanel getButton() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		//		new JPanel(new GridLayout(1, 2));
		compareBtn = new JButton(GUIConstants.BUTTON_COMPARE);
		compareBtn.addActionListener(new ActionListener() {
			/* if the ok button was pressed close dialog */
			public void actionPerformed(ActionEvent _event) {
				dispose();
				MatchResult intended = (MatchResult) intendedMatchresults.getSelectedItem();
				boolean error;
				ArrayList<Object> items = new ArrayList<Object>();
				if (singleMatchresult.isSelected()) {
					MatchResult test = (MatchResult) testMatchresults.getSelectedItem();
					items.add(test);
				} else {					
					for (int i = 0; i < testMatchresults.getItemCount(); i++) {
						items.add(testMatchresults.getItemAt(i));
					}					
				}
				error = calculateAndShowResult(intended, items);
				if (!error) {
					controller
							.setStatus(GUIConstants.COMPARE_MATCHRESULTS_DONE);
				}
			}
		});
		cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
		cancelBtn.addActionListener(new ActionListener() {
			/* if the ok button was pressed close dialog */
			public void actionPerformed(ActionEvent _event) {
				dispose();
				controller
						.setStatus(GUIConstants.COMPARE_MATCHRESULTS_CANCELED);
			}
		});
		// Compare Button
		panel.add(compareBtn);
		//	Cancel Button
		panel.add(cancelBtn);
		return panel;
	}

	boolean calculateAndShowResult(MatchResult _intendedResult, ArrayList _items) {
//		ArrayList list = new ArrayList();
//		Graph sourceGraph1 = _intendedResult.getSourceGraph();
//		Graph targetGraph1 = _intendedResult.getTargetGraph();
//		for (int i = 0; i < _items.size(); i++) {
//			MatchResult testResult = (MatchResult)_items.get(i);
//			Graph sourceGraph2 = testResult.getSourceGraph();
//			Graph targetGraph2 = testResult.getTargetGraph();
//			if (sourceGraph1.equals(sourceGraph2)
//					&& targetGraph1.equals(targetGraph2)) {
//				list.add(testResult);
//			} else if (sourceGraph1.equals(targetGraph2)
//					&& targetGraph1.equals(sourceGraph2)) {
//				list.add(testResult);
//			}
//		}
//		if (!controller.checkMatchResults(list)) {
//			controller.setStatus(GUIConstants.DIFFERENT_SOURCETARGET);
//			return true;
//		}
		TextFrame wnd = new TextFrame(_intendedResult, _items,
				GUIConstants.COMPARE_MATCHRESULTS, MainWindow.DIM_LARGE2);
		wnd.setLocation(controller.getDialogPosition());
//		wnd.setSize(MainWindow.DIM_LARGE2);
		controller.setStatus(GUIConstants.COMPARE_MATCHRESULTS_DONE);
		wnd.setVisible(true);
		return false;
	}

	/*
	 * initialize this Dialog and add all needed Components (depending on the
	 * given default values or values of an existing matcher config)
	 */
	protected void init() {
		GridBagLayout gbl;
		GridBagConstraints gbc;
		gbl = new GridBagLayout();
		mainPanel.setLayout(gbl);
		gbc = new GridBagConstraints();
		int start = 0;
		int height = 1;
		// Intended Matchresult Label
		gbc = MainWindow.makegbc(0, start, 1, height);
		JLabel label = new JLabel(GUIConstants.INT_MATCHRESULT, SwingConstants.LEFT);
		label.setFont(MainWindow.FONT12);
		gbl.setConstraints(label, gbc);
		mainPanel.add(label);
		// Intended Matchresult
		gbc = MainWindow.makegbc(1, start, 2, height);
		intendedMatchresults = getMatchresultsBox(null);
		if (intendedResult!=null){
			intendedMatchresults.setSelectedItem(intendedResult);
		}
		intendedMatchresults.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent _event) {
				Object item = intendedMatchresults.getSelectedItem();
				Source source = ((MatchResult)item).getSourceGraph().getSource();
				Source target = ((MatchResult)item).getTargetGraph().getSource();
				testMatchresults.removeAllItems();
				for (int i = 0; i < matchresultList.size(); i++) {
					MatchResult current = (MatchResult) matchresultList.get(i);
					Source sourceCurrent = current.getSourceGraph().getSource();
					Source targetCurrent = current.getTargetGraph().getSource();
					if ( (source.equals(sourceCurrent) && target.equals(targetCurrent))
							|| (source.equals(targetCurrent) && target.equals(sourceCurrent))){
						testMatchresults.addItem(current);
					}
				}
			}
			
		});
		gbl.setConstraints(intendedMatchresults, gbc);
		mainPanel.add(intendedMatchresults);
		start += height;
		// Test Matchresult Label
		gbc = MainWindow.makegbc(0, start, 1, height);
		label = new JLabel(GUIConstants.TEST_MATCHRESULT, SwingConstants.LEFT);
		label.setFont(MainWindow.FONT12);
		gbl.setConstraints(label, gbc);
		mainPanel.add(label);
		// Test Matchresult
		panel = new JPanel(new BorderLayout());
		ButtonGroup bg = new ButtonGroup();
		singleMatchresult = new JRadioButtonMenuItem(GUIConstants.EMPTY, false);
		bg.add(singleMatchresult);
		panel.add(singleMatchresult, BorderLayout.WEST);
		JPanel panel2 = new JPanel(new BorderLayout());
		allMatchresults = new JRadioButtonMenuItem(GUIConstants.EMPTY, true);
		JLabel labelAll = new JLabel("All Matchresults in the Workspace (with same source and target)");
		labelAll.setBorder(new EmptyBorder(new Insets(11, 11, 11, 11)));
		panel2.add(allMatchresults, BorderLayout.WEST);
		panel2.add(labelAll, BorderLayout.CENTER);
		bg.add(allMatchresults);
		labelAll.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent _event) {
				allMatchresults.setSelected(true);
			}
		});
		
		//		panel.add(allMatchresults, BorderLayout.WEST);
		gbc = MainWindow.makegbc(1, start, 2, height);
		testMatchresults = getMatchresultsBox(intendedMatchresults.getSelectedItem());
		//		gbl.setConstraints(testMatchresults, gbc);
		//		mainPanel.add(testMatchresults);
		panel.add(testMatchresults, BorderLayout.CENTER);
		testMatchresults.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent _event) {
				singleMatchresult.setSelected(true);
			}
		});
		panel.add(panel2, BorderLayout.SOUTH);
		gbl.setConstraints(panel, gbc);
		mainPanel.add(panel);
		start += height;
		// Get Panel with all 2 Buttons and add it
		JPanel button = getButton();
		gbc = MainWindow.makegbc(0, start, 2, height);
		gbl.setConstraints(button, gbc);
		mainPanel.add(button);
		pack();
	}

	/*
	 * return a JComboBox that contains all possible matchresults
	 */
	private JComboBox getMatchresultsBox(Object _intended) {
		ArrayList<MatchResult> data = new ArrayList<MatchResult>();
		if (_intended==null) {
			matchresultList = controller.getManagementPane()
					.getAllWorkMatchresults();
			for (int i = 0; i < matchresultList.size(); i++) {
				MatchResult current = (MatchResult) matchresultList.get(i);
				data.add(current);
			}
		} else if (_intended instanceof MatchResult) {
			Source source = ((MatchResult)_intended).getSourceGraph().getSource();
			Source target = ((MatchResult)_intended).getTargetGraph().getSource();
			for (int i = 0; i < matchresultList.size(); i++) {
				MatchResult current = (MatchResult) matchresultList.get(i);
				Source sourceCurrent = current.getSourceGraph().getSource();
				Source targetCurrent = current.getTargetGraph().getSource();
				if ( (source.equals(sourceCurrent) && target.equals(targetCurrent))
						|| (source.equals(targetCurrent) && target.equals(sourceCurrent))){
					data.add(current);
				}
			}
		}
		JComboBox combo = new JComboBox(data.toArray());
		if (_intended instanceof MatchResult){
			int index = data.indexOf(_intended);
			if (index==0){
				combo.setSelectedIndex(1);
			} else {
				combo.setSelectedIndex(0);
			}
		}
		combo.setRenderer(new ListCellRenderer(){

			public Component getListCellRendererComponent(JList _list, Object _value, int _index, boolean _isSelected, boolean _cellHasFocus) {
				JLabel textLabel = new JLabel();
				textLabel.setOpaque(true);
				textLabel.setBackground(MainWindow.GLOBAL_BACKGROUND);
				textLabel.setForeground(MainWindow.FOREGROUND);
				if (_value instanceof MatchResult){
					textLabel.setText(((MatchResult)_value).getName());
				}
				if (_isSelected) {
					textLabel.setBackground(MainWindow.SELECTED_BACKGROUND);
				} 
				return textLabel;
			}
			
		});
		combo.setBackground(MainWindow.GLOBAL_BACKGROUND);
		combo.setMaximumRowCount(MainWindow.MAX_ROW);
		return combo;
	}
}