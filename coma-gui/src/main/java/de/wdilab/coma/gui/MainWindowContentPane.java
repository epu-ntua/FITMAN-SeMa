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

package de.wdilab.coma.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import de.wdilab.coma.gui.view.LinedMatchresultView2;
import de.wdilab.coma.gui.view.LinedMatchresultView3;
import de.wdilab.coma.gui.view.MatchresultView2;
import de.wdilab.coma.gui.view.MatchresultView3;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;

/**
 * MainWindowContentPane extends JPanel - creates the content pane with tool
 * bar, scrollbar (source + target tree), status line, line pane.
 * It allows the switch from 2splitpane to 3splitpane and back.
 * 
 * @author Sabine Massmann
 */
public class MainWindowContentPane extends JPanel {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	static final int STATUSLINE_HEIGHT = 20;
	static final int LABELS_HEIGHT = 16;
	static final int PROGRESSBAR_WIDTH = 150;
	static final int SCROLLBAR_HEIGHT = 16;
	//----------------------------------------------
	private LinedMatchresultView2 lmw;
	private LinedMatchresultView2 lmw2;
	private LinedMatchresultView3 lmw3;
	JSplitPane main_Split;
	Controller controller;
	private StatusLine status_line;
	private ManagementPane managementPane;
	// Button Insets
//	float DIV_LOCATION_MATCHRESULT = (float) 0.5;
	int DIV_LOCATION_MAINSPLIT = 210; //312;  240 - one icon more
	int oldWidth;
	JLabel matchresultLabel;
	JPanel rightComponent;
	boolean changeTo3SplitPane = false;
	boolean changeTo2SplitPane = false;
	JButton arrowUp=null, arrowDown=null;
	
	/*
	 * Constructor of MainWindowContentPane
	 */
	public MainWindowContentPane(Controller _controller, int _width, int _height) {
		//		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setLayout(new BorderLayout());
		controller = _controller;
		if (!Controller.SIMPLE_GUI && !Controller.WORKING_GUI){
			DIV_LOCATION_MAINSPLIT = 312;
		}
		setPreferredSize(new Dimension(_width, _height));
		//Create and set up the layered pane.
		// StatusLine
		status_line = new StatusLine(GUIConstants.NO_ERRORS);
		status_line.setBorder(BorderFactory.createLoweredBevelBorder());
		status_line.setOpaque(true);
		add(status_line, BorderLayout.SOUTH);
		setProgressBar(false);
		//		ManagementPane
		managementPane = new ManagementPane(_controller);
		// SplitPane
		lmw2 = new LinedMatchresultView2(_controller);
		lmw = lmw2;
		//				lmw = new LinedMatchresultView3(_controller);
		rightComponent = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new BorderLayout()); 

		matchresultLabel = new JLabel(GUIConstants.MATCHRESULT_BIG + "    ", SwingConstants.CENTER);
		matchresultLabel.setFont(MainWindow.FONT12_BOLD);
		matchresultLabel.setToolTipText(matchresultLabel.getText());
		
		JPanel helpPanel =new JPanel(new FlowLayout()); 
		
		arrowDown = new JButton(Controller.getImageIcon(GUIConstants.ICON_ARROWDOWN));
		arrowDown.setToolTipText(GUIConstants.SHOW_NEXT_LINE_DOWN);
		arrowDown.setMargin(ManagementPane.BUTTON_INSETS);
		arrowDown.setEnabled(false);
		arrowDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
//				lmw.showNextLine(true);
			}
		});
		
		helpPanel.add(arrowDown);
		
		arrowUp = new JButton(Controller.getImageIcon(GUIConstants.ICON_ARROWUP));
		arrowUp.setToolTipText(GUIConstants.SHOW_NEXT_LINE_UP);
		arrowUp.setMargin(ManagementPane.BUTTON_INSETS);
		arrowUp.setEnabled(false);
		arrowUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
//				lmw.showNextLine(false);
			}
		});
		
		helpPanel.add(arrowUp);
		
		helpPanel.add(matchresultLabel);
		panel.add(helpPanel, BorderLayout.CENTER);
//		panel.add(matchresultLabel, BorderLayout.CENTER);
		
		JPanel panelLine = new JPanel(new BorderLayout()); 
		JLabel line = new JLabel(Controller.getImageIcon(GUIConstants.ICON_LINES));
		JLabel number1 = new JLabel("1.0 ", SwingConstants.CENTER);
		JLabel number0 = new JLabel(" 0.0", SwingConstants.CENTER);
		panelLine.add(line, BorderLayout.CENTER);
		panelLine.add(number1, BorderLayout.WEST);
		panelLine.add(number0, BorderLayout.EAST);
		panel.add(panelLine, BorderLayout.WEST);
		

		
		
		JButton button = new JButton(Controller.getImageIcon(GUIConstants.ICON_CLOSE));
		button.setToolTipText(GUIConstants.CLEAN_INFO);
		button.setMargin(ManagementPane.BUTTON_INSETS);
		panel.add(button, BorderLayout.EAST);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				controller.cleanMatchresultLines();
			}
		});
		
		
		rightComponent.add(panel, BorderLayout.NORTH);
//		rightComponent.add(matchresultLabel, BorderLayout.NORTH);
		rightComponent.add(lmw, BorderLayout.CENTER);
		//		main_Split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		//				managementPane, lmw);
		main_Split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				managementPane, rightComponent);
		main_Split.setOpaque(true);
		main_Split.setBounds(0, 0, _width, _height
				- status_line.getHeight());
		main_Split.setDividerLocation(DIV_LOCATION_MAINSPLIT);
		main_Split.setOneTouchExpandable(true);
		add(main_Split, BorderLayout.CENTER);
		oldWidth = _width;
	}

	public void changeTo3SplitPane() {
		MatchResult mr = controller.getGUIMatchresult().getMatchResult();
		changeTo3SplitPane(mr);
	}
	
	public void changeTo3SplitPane(MatchResult _mr) {
		changeTo3SplitPane = true;
//		controller.enableEdit(false);
		if (lmw3 == null) {
			lmw3 = new LinedMatchresultView3(controller);
		}
		lmw2.getMatchresultView().getSourceTree().setSelectionNull();
		lmw2.getMatchresultView().getTargetTree().setSelectionNull();
		lmw = lmw3;
		rightComponent.remove(lmw2);
		rightComponent.add(lmw, BorderLayout.CENTER);
		if (_mr == null) {
			main_Split.setRightComponent(rightComponent);
			remove(main_Split);
			add(main_Split, BorderLayout.CENTER);
			return;
		}
		if (_mr.getUserObject() instanceof MatchResult[]){		
			MatchResult[] results = (MatchResult[]) _mr.getUserObject();
			main_Split.setRightComponent(rightComponent);
			remove(main_Split);
			add(main_Split, BorderLayout.CENTER);
			controller.loadSourceSchema(controller.getGUIMatchresult()
					.getSourceGraph());
			controller.loadTargetSchema(controller.getGUIMatchresult()
					.getTargetGraph());
			Graph middle = results[0].getTargetGraph();
			if (middle != null) {
				controller.loadMiddleGraph(middle);
			}
		} 
		controller.setNewMatchResult(_mr, true);
		changeTo3SplitPane = false;
		main_Split.setDividerLocation(DIV_LOCATION_MAINSPLIT);
		int width = lmw2.getWidth()/3;
		lmw.getMatchresultView().setDividerLocation(2*width);
		((MatchresultView3)lmw3.getMatchresultView()).getLeftSplitPane().setDividerLocation(width);
	}

	public void changeTo2SplitPane() {
		if (changeTo2SplitPane){
			return;
		}
		changeTo2SplitPane = true;
		if (lmw3 != null) {
			
			Object mw = lmw3.getMatchresultView();
			if (mw instanceof MatchresultView3){
				((MatchresultView3)lmw3.getMatchresultView()).getMiddleTree().setSelectionNull();
			}
			lmw3.getMatchresultView().getSourceTree().setSelectionNull();
			lmw3.getMatchresultView().getTargetTree().setSelectionNull();
//			controller.enableEdit(true);
			lmw = lmw2;
			rightComponent.remove(lmw3);
			rightComponent.add(lmw, BorderLayout.CENTER);
			MatchResult mr = controller.getGUIMatchresult().getMatchResult();
			remove(main_Split);
			main_Split.setRightComponent(rightComponent);
			add(main_Split, BorderLayout.CENTER);
			controller.loadSourceSchema(controller.getGUIMatchresult()
					.getSourceGraph());
			controller.loadTargetSchema(controller.getGUIMatchresult()
					.getTargetGraph());
			controller.setNewMatchResult(mr, true);
		}
		changeTo2SplitPane = false;
		main_Split.setDividerLocation(DIV_LOCATION_MAINSPLIT);
		lmw.getMatchresultView().setDividerLocation(0.5);
	}

	public void changeTo2SplitPane(MatchResult _mr) {
		if (changeTo2SplitPane){
			return;
		}
		changeTo2SplitPane = true;
		if (lmw3 != null) {
			lmw3.getMatchresultView().getSourceTree().setSelectionNull();
			lmw3.getMatchresultView().getTargetTree().setSelectionNull();
//			controller.enableEdit(true);
			lmw = lmw2;
			rightComponent.remove(lmw3);
			rightComponent.add(lmw, BorderLayout.CENTER);
			remove(main_Split);
			main_Split.setRightComponent(rightComponent);
			add(main_Split, BorderLayout.CENTER);
//			controller.loadSourceSchema(controller.getMatchresult()
//					.getSourceGraph());
//			controller.loadTargetSchema(controller.getMatchresult()
//					.getTargetGraph());
			controller.setNewMatchResult(_mr, true);
		}
		changeTo2SplitPane = false;
	}
	
	public LinedMatchresultView2 getLMW2(){
		return lmw2;
	}
	
	public boolean is2SplitPane() {
		if (lmw.equals(lmw2)) {
			return true;
		}
		return false;
	}

	public void enableArrows(boolean enable){
		arrowDown.setEnabled(enable);
		arrowUp.setEnabled(enable);
	}
	
	public void setMatchresultLabel(MatchResult _result) {
		String labelText = GUIConstants.EMPTY;
		if (_result == null) {
			labelText = GUIConstants.MATCHRESULT_BIG;

		} else {
			labelText = _result.getName();
		}
		matchresultLabel.setToolTipText(labelText);
		matchresultLabel.setText(labelText + "    ");
		enableArrows(false);
	}

	/*
	 * add a new MatchresultTab with the given result, if select = true the new
	 * created Tab will be selected (and thus shown)
	 */
	public void addMatchResult(MatchResult _result, boolean _select) {
		//		_result = Manager.transformMatchResult(_result,
		// controller.getPreprocessing());
		if (_result == null || _result.getMatchCount() == 0) {
			controller.setStatus(GUIConstants.NO_RESULTS);
			return;
		}
		// check if the Graph are the same as current loaded
		// if not - create Match Result with "old" Graphs
		//		Graph targetNew =
		// _result.getTargetGraph().getGraph(Graph.GRAPH_STATE_SIMPLIFIED);
		//		if (sourceOld.equals(sourceNew) && targetOld.equals(targetNew)){
		//			System.out.println("same Graphs");
		//		} else {
		//			System.out.println("different Graph(s)");
		//  create Match Result with "old" Graphs
		// WORKAROUND: because after loading repository the graphs are not the
		// same
//		if (controller.isUpdatedRepository()
//				&& controller.getMatchresult().getSourceGraph() != null
//				&& controller.getMatchresult().getTargetGraph() != null) {
//			Graph sourceOld = controller.getMatchresult()
//					.getSourceGraph();
////					.getGraph(Graph.GRAPH_STATE_SIMPLIFIED);
//			//		Graph sourceNew =
//			// _result.getSourceGraph().getGraph(Graph.GRAPH_STATE_SIMPLIFIED);
//			Graph targetOld = controller.getMatchresult()
//					.getTargetGraph();
////					.getGraph(Graph.GRAPH_STATE_SIMPLIFIED);
//			_result = ReuseStrategy.recreateMatchResult(_result, sourceOld,
//					targetOld);
//		}
		//		}
		managementPane.setSelectedTab(ManagementPane.WORKSPACE);
		managementPane.addMatchResult(_result, _select);
	}

	/*
	 * return ManagementPane
	 */
	public ManagementPane getManagementPane() {
		return managementPane;
	}

	/*
	 * set the size of the matchresult view, status line and line component to the
	 * given with + height
	 */
	public void setNewSize(int _newWidth, int _newHeight) {
		//		System.out.println("setNewSize");
		if (_newHeight < 100) {
			_newHeight = 100;
		}
		//		layeredPane.setPreferredSize(new Dimension(_newWidth,
		// _newHeight));
		if (_newWidth != oldWidth) {
			//			DIV_LOCATION_MAINSPLIT = (float) main_Split
			//					.getDividerLocation()
			//					/ oldWidth;
			DIV_LOCATION_MAINSPLIT = main_Split.getDividerLocation();
			//			lmw.setDefaultDividerLocation();
			main_Split.setDividerLocation(main_Split
					.getDividerLocation());
			//			System.out.println(locationMatchresultSplit+ " "
			//					+ main_Split.getDividerLocation() + " "
			//					+ newWidth);
		} else {
			main_Split.setDividerLocation(main_Split
					.getDividerLocation());
		}
		oldWidth = _newWidth;
	}

	/*
	 * set text for status line
	 */
	public void setStatus(String _text) {
		status_line.setText(_text);
	}

	/*
	 * set a new Controller
	 */
	public void setController(Controller _controller) {
		controller = _controller;
		managementPane.setController(_controller);
	}

	/*
	 * set the progress bar active = show it, running animation with text
	 */
	public void setProgressBar(boolean _active) {
		if (_active) {
			if ((controller.getMainWindow() != null)
					&& (controller.getMainWindow().getUnlock() != null)) {
				controller.getMainWindow().getUnlock().setEnabled(true);
			}
			status_line.setStringVisible(true);
			status_line.getProgressBar().setIndeterminate(true);
		} else {
			if ((controller.getMainWindow() != null)
					&& (controller.getMainWindow().getUnlock() != null)) {
				controller.getMainWindow().getUnlock().setEnabled(false);
			}
			status_line.setStringVisible(false);
			status_line.getProgressBar().setIndeterminate(false);
		}
	}

	/*
	 * return the matchresult view
	 */
	public MatchresultView2 getMatchresultView() {
		return lmw.getMatchresultView();
	}

	public void setNewMatchResult(MatchResult _result) {
		setMatchresultLabel(_result);
		if (lmw == lmw2) {
			lmw.getLinesComponent().setNewMatchResult(_result);
		} else {
			((LinedMatchresultView3) lmw).getLinesComponent().setNewMatchResult(
					_result);
		}
	}

	/**
	 * @return Returns the result.
	 */
	public MatchResult getResult() {
		if (lmw.equals(lmw2)) {
			return lmw.getLinesComponent().getResult();
		}
		return ((LinedMatchresultView3) lmw).getLinesComponent().getResult();
	}

	/*
	 *  
	 */
	public void unlockGUI() {
		controller.getManagementPane().setMenuStateDB(false);
		controller.getManagementPane().setMenuStateRun(false);
		controller.getMainWindow().getNewContentPane().setProgressBar(
				false);
	}

	/**
	 * @return Returns the changeTo3SplitPane.
	 */
	public boolean isChangeTo3SplitPane() {
		return changeTo3SplitPane;
	}
	
}