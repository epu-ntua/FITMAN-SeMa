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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.Strings;
import de.wdilab.coma.gui.view.MatchresultView2;
import de.wdilab.coma.matching.Resolution;
import de.wdilab.coma.matching.Workflow;
import de.wdilab.coma.matching.execution.ExecWorkflow;
import de.wdilab.coma.matching.validation.TreeToWorkflow;
//import de.wdilab.coma.gui.dlg.StepByStepCombinedReuse.Dlg_StepByStep3;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;

/**
 * StepByStepFragmentMatching = coordinates the fragment matching with 4 Steps
 * 
 * @author Sabine Massmann
 */
public class StepByStepFragmentMatching {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	// button values
	public static final int CANCEL = 0;
	public static final int CONTINUE = 1;
	public static final int BACK = 2;
	public static final int STOP = 3;
	public static final boolean ACTIVE = true;
	public static final boolean STOPPED = false;
	//----------------------------------------------
	Controller controller;
	// position of the dialogs
	Point position = new Point(200, 100);
	boolean matchingAborted = false;
	// all chosen fragments of source and target
	ArrayList allInnersSource = null;
	ArrayList allInnersTarget = null;
	MatchResult selected = null;
	// all fragments of source/target, that found a fragment partner for
	// matching
	ArrayList innersSource = null;
	ArrayList innersTarget = null;
	// source and target Graphs
	Graph source = null;
	Graph target = null;
	// fragment pairs
	MatchResult fragmentPairs = null;
	// result of the fragmentroot matching
//	MatchResult[] globalResults = null;
	Dlg_StepByStep3 dlg3;
	boolean active = STOPPED;

	/*
	 * Constructor of StepByStepFragmentMatching
	 */
	public StepByStepFragmentMatching(Controller _controller) {
		controller = _controller;
		innersSource = new ArrayList();
		innersTarget = new ArrayList();
	}

	/*
	 * @return Returns the fragmentPairs.
	 */
	public MatchResult getFragmentPairs() {
		return fragmentPairs;
	}

	/*
	 * The fragmentPairs to set.
	 */
	public void setFragmentPairs(MatchResult _fragmentPairs) {
		fragmentPairs = _fragmentPairs;
		if (_fragmentPairs == null) {
			controller.getMatchresultView().deleteSuggestedFragments();
		}
	}

	/*
	 * start the "Step By Step" -fragment matching
	 */
	public void start() {
		if (!controller.getGUIMatchresult().containsSource()) {
			controller.setStatus(GUIConstants.NO_SRC_SCHEMA);
			return;
		}
		if (!controller.getGUIMatchresult().containsTarget()) {
			controller.setStatus(GUIConstants.NO_TRG_SCHEMA);
			return;
		}
		active = ACTIVE;
		position = controller.getDialogPosition();
		step1();
	}

	/*
	 * first step: open a dialog (not child of themainwindow-frame!) where the
	 * fragment strategy can be modified including the fragment identification
	 * strategy -> user manually select a fragment of interest - themself or
	 * choosing a strategy (shared, root, inner) these fragments have a
	 * different color (with cancel, continue)
	 */
	void step1() {
		matchingAborted = false;
		Dlg_StepByStep1 wnd = new Dlg_StepByStep1();
		wnd.setLocation(position);
		wnd.setVisible(true);
		wnd.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent _event) {
				controller
						.setStatus(GUIConstants.STEPBYSTEP__FRAGMENTMATCHING_CANCELED);
				setFragmentPairs(null);
			}

			public void windowClosed(WindowEvent _event) {
				Object currentSource = _event.getSource();
				if (currentSource instanceof Dlg_StepByStep1) {
					position = ((Dlg_StepByStep1) currentSource).getLocation();
					controller.setDialogPosition(position);
					int state = ((Dlg_StepByStep1) currentSource).getState();
					if (state == CONTINUE) {
						step2();
					}
				}
			}
		});
	}

	/*
	 * second step: show all fragment pairs that were found (still orange, with
	 * a line between them) and count involved fragments for source/target (with
	 * back, cancel, continue=match)
	 */
	void step2() {
		// TO DO Sabine: fragments should be deselected or selected!!! from user
		Dlg_StepByStep2 wnd = new Dlg_StepByStep2();
		wnd.setLocation(position);
		wnd.setVisible(true);
		wnd.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent _event) {
				controller
						.setStatus(GUIConstants.STEPBYSTEP__FRAGMENTMATCHING_CANCELED);
				setFragmentPairs(null);
			}

			public void windowClosed(WindowEvent _event) {
				Object currentSource = _event.getSource();
				if (currentSource instanceof Dlg_StepByStep2) {
					position = ((Dlg_StepByStep2) currentSource).getLocation();
					controller.setDialogPosition(position);
					int state = ((Dlg_StepByStep2) currentSource).getState();
					if (state == CONTINUE) {
						step3();
					} else if (state == BACK) {
						step1();
					}
				}
			}
		});
	}

	/*
	 * third step: run matching => stop button (to abort matching)
	 */
	void step3() {
		controller.executeFragmentMatching(this);
		dlg3 = new Dlg_StepByStep3();
		dlg3.setLocation(position);
		dlg3.setVisible(true);
		dlg3.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent _event) {
			//matchingAborted = true;
			//controller.stopMatching();
			}

			public void windowClosed(WindowEvent _event) {
				Object currentSource = _event.getSource();
				if (currentSource instanceof Dlg_StepByStep3) {
					position = ((Dlg_StepByStep3) currentSource).getLocation();
					controller.setDialogPosition(position);
					int state = ((Dlg_StepByStep3) currentSource).getState();
					if ((state == STOP) && !matchingAborted) {
						matchingAborted = true;
						controller.stopMatching();
						step4(null);
					}
				}
			}
		});
	}

	/*
	 * fourth step: clear selection in both trees (if there are any) open a
	 * dialog with the result- MatchresultTabName (with continue (start over again),
	 * cancel)
	 */
	void step4(String _matchresultName) {
		controller.getMainWindow().clearMatchresultView();
		controller.getStepFragmentMatching().setActive(StepByStepFragmentMatching.STOPPED);
		//		controller.getMatchresultView().setChanged(true);
		Dlg_StepByStep4 wnd = new Dlg_StepByStep4(_matchresultName);
		wnd.setLocation(position);
		wnd.setVisible(true);
		wnd.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent _event) {
				Object currentSource = _event.getSource();
				if (currentSource instanceof Dlg_StepByStep4) {
					position = ((Dlg_StepByStep4) currentSource).getLocation();
					controller.setDialogPosition(position);
					int state = ((Dlg_StepByStep4) currentSource).getState();
					if (state == CONTINUE) {
						step1();
					}
				}
			}
		});
	}

	/*
	 * @return
	 */
	public ArrayList getInnersSource() {
		return innersSource;
	}

	/*
	 * @return
	 */
	public ArrayList getInnersTarget() {
		return innersTarget;
	}

//	/*
//	 * @return Returns the globalResults.
//	 */
//	public MatchResult[] getGlobalResults() {
//		return globalResults;
//	}
//
//	/*
//	 * The globalResults to set.
//	 */
//	public void setGlobalResults(MatchResult[] _globalResults) {
//		globalResults = _globalResults;
//	}

	public void setInnersSource(ArrayList _list) {
		innersSource = _list;
	}

	public void setInnersTarget(ArrayList _list) {
		innersTarget = _list;
	}

	public void addInnersSource(ArrayList _list) {
		if (innersSource != null) {
			innersSource.addAll(_list);
		} else {
			innersSource = _list;
		}
	}

	public void addInnersTarget(ArrayList _list) {
		if (innersTarget != null) {
			innersTarget.addAll(_list);
		} else {
			innersTarget = _list;
		}
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point _point) {
		position = _point;
	}

	public Graph getSource() {
		return source;
	}

	public Graph getTarget() {
		return target;
	}

	public void setSource(Graph _graph) {
		source = _graph;
	}

	public void setTarget(Graph _graph) {
		target = _graph;
	}

	/*
	 * set the Matchresult Name
	 */
	public void setMatchresultName(String _matchresultName) {
		disposeDlg3();
		step4(_matchresultName);
	}

	public boolean isMatchingAborted() {
		return matchingAborted;
	}

	/*
	 * Returns the allInnersSource.
	 */
	public ArrayList getAllInnersSource() {
		return allInnersSource;
	}

	/*
	 * The allInnersSource to set.
	 */
	public void setAllInnersSource(ArrayList _allInnersSource) {
		allInnersSource = _allInnersSource;
	}

	/*
	 * Returns the allInnersTarget.
	 */
	public ArrayList getAllInnersTarget() {
		return allInnersTarget;
	}
	
	/*
	 * The allInnersSource to set.
	 */
	public void setSelected(MatchResult result) {
		selected = result;
	}

	/*
	 * Returns the allInnersTarget.
	 */
	public MatchResult getSelected() {
		return selected;
	}

	/*
	 * The allInnersTarget to set.
	 */
	public void setAllInnersTarget(ArrayList _allInnersTarget) {
		allInnersTarget = _allInnersTarget;
	}

	/*
	 * Returns the dlg3.
	 */
	public void disposeDlg3() {
		if (dlg3 != null) {
			dlg3.dispose();
			setFragmentPairs(null);
		}
	}

	/*
	 * @return Returns the dlg3.
	 */
	public Dlg_StepByStep3 getDlg3() {
		return dlg3;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean _active) {
		active = _active;
	}

	/*
	 * Dlg_StepByStep1 extends JFrame
	 */
	class Dlg_StepByStep1 extends JFrame {
		int stratStart;
		int stratHeight;
		JLabel comaLabel;
		JLabel comaContextLabel;
		JLabel graphLabel;
		JLabel graphContextLabel;
		JLabel graphNodeLabel;
		JPanel button;
		// Matcher
		JComboBox comaMatcher;
		JComboBox fragmentRoot;
//		JComboBox nodeContextMatcher;
		JComboBox fragmentStrategy;
		JComboBox fragmentIdentification;
		GridBagLayout gbl;
		GridBagConstraints gbc;
		int state = -1;

		/*
		 * Constructor of Dlg_NewMatcher
		 */
		public Dlg_StepByStep1() {
			super(GUIConstants.STEP1);
			//		setResizable(false);
			controller.getStepFragmentMatching().setActive(StepByStepFragmentMatching.ACTIVE);
			getContentPane().setLayout(new BorderLayout());
			init();
		}

		/*
		 * if the fragment identification = user select then check whether there
		 * are fragments selected (one should be at least) - if there are only
		 * fragments selected in one schema find the default ones for the other
		 * schema (inners, leaves, both)
		 */
		boolean checkFragments() {
			String selected = (String) fragmentIdentification
					.getSelectedItem();
			int resValue = Resolution.stringToResolution(selected);
			if (resValue != Resolution.RES1_USER) {
				return true;
			}
			Graph sourceGraph = controller.getGUIMatchresult().getSourceGraph();
			Graph targetGraph = controller.getGUIMatchresult().getTargetGraph();
			ArrayList sourceFragments = null;
			ArrayList targetFragments = null;
			// get the source and target schema graph depending on selected
			// preprocessing option
			sourceGraph = sourceGraph.getGraph(controller.getPreprocessing());
			targetGraph = sourceGraph.getGraph(controller.getPreprocessing());
			sourceFragments = MatchresultView2.getSelectedFragments(
					controller.getMatchresultView().getSourceTree());
			targetFragments = MatchresultView2.getSelectedFragments(
					controller.getMatchresultView().getTargetTree());
			if ((sourceFragments == null) && (targetFragments == null)) {
				JOptionPane.showMessageDialog(this, GUIConstants.SELECT_FRAG
						+ GUIConstants.SRC_SCHEMA + GUIConstants.OR + GUIConstants.TRG_SCHEMA
						+ GUIConstants.LINEBREAK + GUIConstants.CHANGE_FRAG_SEL2,
						GUIConstants.INFORMATION, JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			// check if selected Fragments only Inners or only Leaves
			// take then Inners or Leaves from the other schema - otherwise take
			// both
			if (targetFragments == null && sourceFragments!=null) {
				boolean onlyLeaves = true;
				boolean onlyInners = true;
				ArrayList sourceInners = sourceGraph.getInners();
				ArrayList sourceLeaves = sourceGraph.getLeaves();
				for (int i = 0; i < sourceFragments.size(); i++) {
					if (sourceLeaves.contains(sourceFragments.get(i))) {
						onlyInners = false;
					}
					if (sourceInners.contains(sourceFragments.get(i))) {
						onlyLeaves = false;
					}
				}
				if (onlyLeaves) {
					targetFragments = targetGraph.getLeaves();
				} else if (onlyInners) {
					targetFragments = targetGraph.getInners();
				} else {
					targetFragments = targetGraph.getInners();
					targetFragments.addAll(targetGraph.getLeaves());
				}
			}
			if (sourceFragments == null && targetFragments!=null) {
				boolean onlyLeaves = true;
				boolean onlyInners = true;
				ArrayList targetInners = targetGraph.getInners();
				ArrayList targetLeaves = targetGraph.getLeaves();
				for (int i = 0; i < targetFragments.size(); i++) {
					if (targetInners.contains(targetFragments.get(i))) {
						onlyLeaves = false;
					}
					if (targetLeaves.contains(targetFragments.get(i))) {
						onlyInners = false;
					}
				}
				if (onlyLeaves) {
					sourceFragments = sourceGraph.getLeaves();
				} else if (onlyInners) {
					sourceFragments = sourceGraph.getInners();
				} else {
					sourceFragments = sourceGraph.getInners();
					sourceFragments.addAll(sourceGraph.getLeaves());
				}
			}
			setAllInnersSource(sourceFragments);
			setAllInnersTarget(targetFragments);
			setSource(sourceGraph);
			setTarget(targetGraph);
			return true;
		}

		/*
		 * returns a JPanel containing three buttons: Save (DB), Save (File),
		 * Cancel (and add to these button action/ key listener)
		 */
		protected JPanel getButton() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			JButton continueBtn = new JButton(GUIConstants.BUTTON_CONTINUE);
			continueBtn.addActionListener(new ActionListener() {
				/* if the ok button was pressed close dialog */
				public void actionPerformed(ActionEvent _event) {
					boolean okay = checkFragments();
					if (okay) {
						setPosition(getLocation());
						Dlg_StepByStep1.this.state = StepByStepFragmentMatching.CONTINUE;
						dispose();
						updateValues();
					}
				}
			});
			JButton cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
			cancelBtn.addActionListener(new ActionListener() {
				/* if the ok button was pressed close dialog */
				public void actionPerformed(ActionEvent _event) {
					controller.setStatus(GUIConstants.STEPBYSTEP__FRAGMENTMATCHING_CANCELED);
					Dlg_StepByStep1.this.state = StepByStepFragmentMatching.CANCEL;
					controller.getMatchresultView().deleteSuggestedFragments();
					dispose();
					setActive(StepByStepFragmentMatching.STOPPED);
				}
			});
			// Continue Button
			panel.add(continueBtn);
			//	Cancel Button
			panel.add(cancelBtn);
			return panel;
		}

		/*
		 * initialize this Dialog and add all needed Components (depending on
		 * the given default values or values of an existing matcher config)
		 */
		protected void init() {
			Container mainPanel = new JPanel();
			getContentPane().add(mainPanel, BorderLayout.CENTER);
			gbl = new GridBagLayout();
			mainPanel.setLayout(gbl);
			gbc = new GridBagConstraints();
			int start = 0;
			int height = 1;
			// Fragment Label
			gbc = MainWindow.makegbc(0, start, 2, height);
			JLabel label = new JLabel(Strings.STRAT_FRAGMENT,
					SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			start += height;
			// Fragment Label
			gbc = MainWindow.makegbc(0, start, 1, height);
			label = new JLabel(GUIConstants.FRAGMENT_ID, SwingConstants.LEFT);
			gbl.setConstraints(label, gbc);
			//		Font f = label.getFont();
			label.setFont(MainWindow.FONT11);
			mainPanel.add(label);
			// Fragment Identification
			gbc = MainWindow.makegbc(1, start, 1, height);
			fragmentIdentification = getIdentificationBox();
			// a Listener (everytime something changes in the Fragment
			// identification strategy
			//  -> show/hide automatic selected Fragments)
			fragmentIdentification.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent _event) {
					if (_event.getActionCommand().equals(GUIConstants.BOX_CHANGED)) {
						Graph sourceGraph = controller.getGUIMatchresult()
								.getSourceGraph();
						Graph targetGraph = controller.getGUIMatchresult()
								.getTargetGraph();
						if ((sourceGraph != null) && (targetGraph != null)) {
							sourceGraph = sourceGraph.getGraph(controller
									.getPreprocessing());
							targetGraph = targetGraph.getGraph(controller
									.getPreprocessing());
							ArrayList innersSource = null;
							ArrayList innersTarget = null;
							String selected = (String) ((JComboBox) _event
									.getSource()).getSelectedItem();
							int resValue = Resolution.stringToResolution(selected);
//							innersSource = Manager.determineFragments(sourceGraph,resValue);
//							innersTarget = Manager.determineFragments(targetGraph,resValue);

							if (resValue==Resolution.RES1_USER){
								// do nothing because from user selected nodes
								innersSource = 
								MatchresultView2.getSelectedFragmentPaths(controller.getMatchresultView().getSourceTree());
								innersTarget = 
									MatchresultView2.getSelectedFragmentPaths(controller.getMatchresultView().getTargetTree());								
							} else {
								Resolution r = new Resolution(resValue);
								innersSource = r.getResolution1(sourceGraph);
								innersTarget = r.getResolution1(targetGraph);
							}
							// scann for suggested Fragments
							if (innersSource != null) {
								controller
										.getMatchresultView()
										.scanForSuggestedFragments(
												innersSource, sourceGraph, true);
								controller.getMatchresultView()
										.scanForSuggestedFragments(
												innersTarget, targetGraph,
												false);
								setAllInnersSource(innersSource);
								setAllInnersTarget(innersTarget);								
								setSource(sourceGraph);
								setTarget(targetGraph);
							} else {
								setFragmentPairs(null);
							}
							controller.getMainWindow().repaint();
							controller.getMatchresultView().repaint();
						}
					}
				}
			});
			// the selected Value is the default Value of Common
			String selected = Resolution.resolutionToString(controller
					.getFragmentIdentification());
			if (selected != null) {
				fragmentIdentification.setSelectedItem(selected);
			}
			gbl.setConstraints(fragmentIdentification, gbc);
			mainPanel.add(fragmentIdentification);
			start += height;
			// Matching Fragment Roots Label
			gbc = MainWindow.makegbc(0, start, 1, height);
			label = new JLabel("Match Fragment Roots", SwingConstants.LEFT);
			gbl.setConstraints(label, gbc);
			label.setFont(MainWindow.FONT11);
			mainPanel.add(label);
			// Fragment Node
			gbc = MainWindow.makegbc(1, start, 1, height);
			fragmentRoot = getNodeMatchStrategyBox();
			// the selected Value is the default Value of Common
			selected = controller.getFragmentStrategy();
//			selected = Manager.matchStratToString(controller
//					.getFragmentStrategy());
			if (selected != null) {
				fragmentRoot.setSelectedItem(selected);
			}
			gbl.setConstraints(fragmentRoot, gbc);
			mainPanel.add(fragmentRoot);
			
			start += height;
			// Matching Fragments Label
			gbc = MainWindow.makegbc(0, start, 1, height);
			label = new JLabel("Match Similar Fragment", SwingConstants.LEFT);
			gbl.setConstraints(label, gbc);
			label.setFont(MainWindow.FONT11);
			mainPanel.add(label);
			// Fragment Node
			gbc = MainWindow.makegbc(1, start, 1, height);
			fragmentStrategy = getFragmentMatchStrategyBox();
			// the selected Value is the default Value of Common
			selected = controller.getFragmentStrategy();
//			selected = Manager.matchStratToString(controller
//					.getFragmentStrategy());
			if (selected != null) {
				fragmentStrategy.setSelectedItem(selected);
			}
			gbl.setConstraints(fragmentStrategy, gbc);
			mainPanel.add(fragmentStrategy);
//			fragmentStrategy.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent _event) {
//					if (_event.getActionCommand().equals(GUIConstants.BOX_CHANGED)) {
//						Component[] cs = getContentPane().getComponents();
//						JPanel panel = null;
//						if (cs != null && cs[0] instanceof JPanel) {
//							panel = (JPanel) cs[0];
//						}
//						setStrat(Dlg_StepByStep1.this.stratStart,
//								Dlg_StepByStep1.this.stratHeight, panel);
//					}
//				}
//			});
			start += height;
			// Separator
			height = 1;
			gbc = MainWindow.makegbc(0, start, 2, height);
			JSeparator sep = new JSeparator();
			gbl.setConstraints(sep, gbc);
			mainPanel.add(sep);
			start += height;
			stratStart = start;
			stratHeight = height;
			setStrat(start, height, mainPanel);
			controller.getMatchresultView().setChanged(true);
			pack();
		}

		/*
		 * return a JComboBox that contains all possible fragment identification
		 * strategies
		 */
		protected JComboBox getIdentificationBox() {
			ArrayList<String> data = new ArrayList<String>();
			for (int i = 0; i < Resolution.RES1_FRAGSEL.length; i++) {
				data.add(Resolution.resolutionToString(Resolution.RES1_FRAGSEL[i]));
			}
			JComboBox combo = new JComboBox(data.toArray());
			combo.setBackground(MainWindow.GLOBAL_BACKGROUND);
			combo.setMaximumRowCount(MainWindow.MAX_ROW);
			return combo;
		}
		
		/*
		 * return a JComboBox that contains all possible strategys for fragment
		 * matching (path, node)
		 */
		protected JComboBox getNodeMatchStrategyBox() {
			ArrayList<String> data = new ArrayList<String>();
			
			HashMap<String, String> workflows = controller.getAccessor().getWorkflowVariablesWithValue("(SelfNode");
			for (String workflow : workflows.keySet()) {
				data.add(workflow);
			}
			workflows = controller.getAccessor().getWorkflowVariablesWithValue("(SelfPath");
			for (String workflow : workflows.keySet()) {
				data.add(workflow);
			}
			JComboBox combo = new JComboBox(data.toArray());
			combo.setBackground(MainWindow.GLOBAL_BACKGROUND);
			combo.setMaximumRowCount(MainWindow.MAX_ROW);
			return combo;
		}
		
		/*
		 * return a JComboBox that contains all possible strategys for fragment
		 * matching (path, node)
		 */
		protected JComboBox getFragmentMatchStrategyBox() {
			ArrayList<String> data = new ArrayList<String>();
			HashMap<String, String> workflows = controller.getAccessor().getWorkflowVariablesWithType("W");
			for (String workflow : workflows.keySet()) {
				if (!workflow.toLowerCase().contains("reuse")
//						&& !workflow.toLowerCase().contains("fragment")
						){
					data.add(workflow);
				}
			}
			JComboBox combo = new JComboBox(data.toArray());
			combo.setBackground(MainWindow.GLOBAL_BACKGROUND);
			combo.setMaximumRowCount(MainWindow.MAX_ROW);
			return combo;
		}
		
		/*
		 * add the components for the chosen strategy (COMA or Graph) at the
		 * given coordinates (start, height)
		 */
		protected void setStrat(int _start, int _height, Container _mainPanel) {
			if (_mainPanel == null)
				return;
			// Get Panel with all 3 Buttons and add it
			if (button != null) {
				_mainPanel.remove(button);
			}
			button = getButton();
			gbc = MainWindow.makegbc(0, _start, 2, _height);
			gbl.setConstraints(button, gbc);
			_mainPanel.add(button);
			pack();
		}


		/*
		 * update the controller with the given strategy parameters (they maybe
		 * changed) and update the position for showing the following dialog
		 * (step 2)
		 */
		void updateValues() {
			//		FRAGMENT
			String fragment_Id = fragmentIdentification.getSelectedItem()
					.toString();
			int fragment_Identification = Resolution
					.stringToResolution(fragment_Id);
			controller.setFragmentIdentification(fragment_Identification);
			String selectedMatcher = fragmentStrategy.getSelectedItem()
					.toString();
			controller.setFragmentStrategy(selectedMatcher);
			selectedMatcher = fragmentRoot.getSelectedItem()
			.toString();
			controller.setFragmentRootStrategy(selectedMatcher);
		}

		/*
		 * @return state
		 */
		public int getState() {
			return state;
		}
	}
	/*
	 * Dlg_StepByStep2 extends JFrame
	 */
	class Dlg_StepByStep2 extends JFrame {
		JButton matchBtn;
		int state = -1;

		/*
		 * Constructor of Dlg_NewMatcher
		 */
		public Dlg_StepByStep2() {
			// find with matchFragmentRoots fragment pairs
			// show them in the panel (with line)
			super(GUIConstants.STEP2);
			//		setResizable(false);
			init();
		}

		/*
		 * returns a JPanel containing three buttons: Save (DB), Save (File),
		 * Cancel (and add to these button action/ key listener)
		 */
		protected JPanel getButton() {
			JButton cancelBtn, backBtn;
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			//		new JPanel(new GridLayout(1, 3));
			backBtn = new JButton(GUIConstants.BUTTON_BACK);
			backBtn.addActionListener(new ActionListener() {
				/* if the ok button was pressed close dialog */
				public void actionPerformed(ActionEvent _event) {
					setPosition(getLocation());
					Dlg_StepByStep2.this.state = StepByStepFragmentMatching.BACK;
					setFragmentPairs(null);
					dispose();
				}
			});
			matchBtn = new JButton(GUIConstants.BUTTON_MATCH);
			matchBtn.addActionListener(new ActionListener() {
				/* if the ok button was pressed close dialog */
				public void actionPerformed(ActionEvent _event) {
					setPosition(getLocation());
					Dlg_StepByStep2.this.state = StepByStepFragmentMatching.CONTINUE;
					dispose();
				}
			});
			cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
			cancelBtn.addActionListener(new ActionListener() {
				/* if the ok button was pressed close dialog */
				public void actionPerformed(ActionEvent _event) {
					controller.setStatus(GUIConstants.STEPBYSTEP__FRAGMENTMATCHING_CANCELED);
					Dlg_StepByStep2.this.state = StepByStepFragmentMatching.CANCEL;
					setFragmentPairs(null);
					dispose();
					setActive(StepByStepFragmentMatching.STOPPED);
				}
			});
			//	Back Button
			panel.add(backBtn);
			// Match Button
			panel.add(matchBtn);
			//	Cancel Button
			panel.add(cancelBtn);
			return panel;
		}

		/*
		 * find for the selected fragments fragment pairs by matching there
		 * roots show them and draw a line between them
		 */
		void setFragmentRoots() {
			setFragmentPairs(null);
//			ArrayList aRoots = getAllInnersSource();
//			ArrayList bRoots = getAllInnersTarget();
//			MatcherConfig nodeMatcher = null, contextMatcher = null;
			Graph sourceGraph = controller.getGUIMatchresult().getSourceGraph();
			Graph targetGraph = controller.getGUIMatchresult().getTargetGraph();
			if ((sourceGraph != null) && (targetGraph != null)) {
				sourceGraph = sourceGraph.getGraph(controller
						.getPreprocessing());
				targetGraph = targetGraph.getGraph(controller
						.getPreprocessing());
			} else {
				return;
			}
//			Manager manager = controller.getManager();
//			manager.prepareMatch(sourceGraph, targetGraph);
			int fragIdent = controller.getFragmentIdentification();
			String fragRoot = controller.getFragmentRootStrategy();
			// globalResults
			String value = controller.getAccessor().getWorkflowVariableFull(fragRoot);
			value = "((" + Resolution.resolutionToString(fragIdent)+";"+value+";(Both,MaxN(1))"+"))";
			
			Workflow w =  TreeToWorkflow.buildWorkflow(value);
			w.setSource(sourceGraph);
			w.setTarget(targetGraph);
			ExecWorkflow exec = new ExecWorkflow();
			MatchResult result = null;
			MatchResult[] results = exec.execute(w);
			if (results==null){
				System.err.println("StepByStepFragmentMatching.setFragmentRoots results unexpected null");
			} else {
				if (results.length>1){
					System.err.println("StepByStepFragmentMatching.setFragmentRoots results unexpected more than one, only first one used");
				}
				result = results[0];
			}
//			MatchResult[] globalResults = controller.getManager()
//					.matchFragmentRoots(aRoots, bRoots, matchStrat
////							, nodeMatcher, contextMatcher
//							);
//			MatchResult[] globalResults = new MatchResult[]{ result};
//			setGlobalResults(globalResults);
			setFragmentPairs(result);
			setSelected(result);
			//		System.out.println(globalResults);
			getInnersSource().clear();
			getInnersTarget().clear();
			if (fragmentPairs != null) {
					ArrayList innersSource = (ArrayList) fragmentPairs.getSrcObjects().clone();
					ArrayList innersTarget = (ArrayList) fragmentPairs.getTrgObjects().clone();
					controller.getMatchresultView().scanForSuggestedFragments(
							fragmentPairs, sourceGraph, false, innersSource,
							innersTarget);
					addInnersSource(innersSource);
					//				System.out.println(innersSource);
					addInnersTarget(innersTarget);
					//				System.out.println(innersTarget);
					setSource(sourceGraph);
					setTarget(targetGraph);

				//			all.print();
				setFragmentPairs(fragmentPairs);
			}
		}

		/*
		 * initialize this Dialog and add all needed Components (depending on
		 * the given default values or values of an existing matcher config)
		 */
		protected void init() {
			getContentPane().setLayout(new BorderLayout());
			JPanel mainPanel = new JPanel();
			getContentPane().add(mainPanel, BorderLayout.CENTER);
			setFragmentRoots();
			controller.getMatchresultView().setChanged(true);
			GridBagLayout gbl = new GridBagLayout();
			mainPanel.setLayout(gbl);
			GridBagConstraints gbc = new GridBagConstraints();
			int start = 0;
			int height = 1;
			boolean matchNotPossible = false;
			// Fragment Label
			gbc = MainWindow.makegbc(0, start, 1, height);
			JLabel label = new JLabel(GUIConstants.SRC_FRAGMENTS,
					SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			gbc = MainWindow.makegbc(1, start, 1, height);
			label = new JLabel(GUIConstants.EMPTY + getAllInnersSource().size(),
					SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			start += height;
			gbc = MainWindow.makegbc(0, start, 1, height);
			label = new JLabel(GUIConstants.SEL_SRC_FR, SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			gbc = MainWindow.makegbc(1, start, 1, height);
			label = new JLabel(GUIConstants.EMPTY + getInnersSource().size(),
					SwingConstants.LEFT);
			if (getInnersSource().size() == 0) {
				matchNotPossible = true;
			}
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			start += height;
			gbc = MainWindow.makegbc(0, start, 1, height);
			label = new JLabel(GUIConstants.TRG_FRAGMENTS, SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			gbc = MainWindow.makegbc(1, start, 1, height);
			label = new JLabel(GUIConstants.EMPTY + getAllInnersTarget().size(),
					SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			start += height;
			gbc = MainWindow.makegbc(0, start, 1, height);
			label = new JLabel(GUIConstants.SEL_TRG_FR, SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			gbc = MainWindow.makegbc(1, start, 1, height);
			label = new JLabel(GUIConstants.EMPTY + getInnersTarget().size(),
					SwingConstants.LEFT);
			if (getInnersTarget().size() == 0) {
				matchNotPossible = true;
			}
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			start += height;
			// Get Panel with all 3 Buttons and add it
			JPanel button = getButton();
			if (matchNotPossible) {
				matchBtn.setEnabled(false);
			}
			gbc = MainWindow.makegbc(0, start, 2, height);
			gbl.setConstraints(button, gbc);
			mainPanel.add(button);
			pack();
		}

		/*
		 * @return state
		 */
		public int getState() {
			return state;
		}
	}
	/*
	 * Dlg_StepByStep3 extends JFrame
	 */
	public class Dlg_StepByStep3 extends JFrame {
		protected JButton stopBtn;
		int state = -1;

		/*
		 * Constructor of Dlg_NewMatcher
		 */
		public Dlg_StepByStep3() {
			super(GUIConstants.STEP3);
			//		setResizable(false);
			getContentPane().setLayout(new BorderLayout());
			init();
		}

		/*
		 * returns a JPanel containing three buttons: Save (DB), Save (File),
		 * Cancel (and add to these button action/ key listener)
		 */
		protected JPanel getButton() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			//		new JPanel(new GridLayout(1, 3));
			stopBtn = new JButton(GUIConstants.BUTTON_STOP);
			stopBtn.addActionListener(new ActionListener() {
				/* if the ok button was pressed close dialog */
				public void actionPerformed(ActionEvent _event) {
					setPosition(getLocation());
					Dlg_StepByStep3.this.state = StepByStepFragmentMatching.STOP;
					controller.stopMatching();
					dispose();
					setFragmentPairs(null);
				}
			});
			//	Stop Button
			panel.add(stopBtn);
			return panel;
		}

		/*
		 * initialize this Dialog and add all needed Components (depending on
		 * the given default values or values of an existing matcher config)
		 */
		protected void init() {
			JPanel mainPanel = new JPanel();
			getContentPane().add(mainPanel, BorderLayout.CENTER);
			GridBagLayout gbl = new GridBagLayout();
			mainPanel.setLayout(gbl);
			GridBagConstraints gbc = new GridBagConstraints();
			int start = 0;
			int height = 1;
			// Fragment Label
			gbc = MainWindow.makegbc(0, start, 2, height);
			JLabel label = new JLabel(GUIConstants.EX_STARTED, SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			start += height;
			gbc = MainWindow.makegbc(0, start, 1, height);
			label = new JLabel(GUIConstants.SEL_SRC_FR, SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			gbc = MainWindow.makegbc(1, start, 1, height);
			label = new JLabel(GUIConstants.EMPTY + getInnersSource().size(),
					SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			start += height;
			gbc = MainWindow.makegbc(0, start, 1, height);
			label = new JLabel(GUIConstants.SEL_TRG_FR, SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			gbc = MainWindow.makegbc(1, start, 1, height);
			label = new JLabel(GUIConstants.EMPTY + getInnersTarget().size(),
					SwingConstants.LEFT);
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
		 * @return state
		 */
		public int getState() {
			return state;
		}

		/*
		 * The state to set.
		 */
		public void setState(int _state) {
			state = _state;
		}
	}
	/*
	 * Dlg_StepByStep4 extends JFrame
	 */
	class Dlg_StepByStep4 extends JFrame {
		int state = -1;
		String matchresultName;

		/*
		 * Constructor of Dlg_NewMatcher
		 */
		public Dlg_StepByStep4(String _matchresultName) {
			super(GUIConstants.STEP4);
			matchresultName = _matchresultName;
			//		setResizable(false);
			getContentPane().setLayout(new BorderLayout());
			init();
		}

		/*
		 * returns a JPanel containing three buttons: Save (DB), Save (File),
		 * Cancel (and add to these button action/ key listener)
		 */
		protected JPanel getButton() {
			JButton continueBtn, cancelBtn;
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			//		new JPanel(new GridLayout(1, 3));
			continueBtn = new JButton(GUIConstants.BUTTON_AGAIN);
			continueBtn.addActionListener(new ActionListener() {
				/* if the ok button was pressed close dialog */
				public void actionPerformed(ActionEvent _event) {
					setPosition(getLocation());
					Dlg_StepByStep4.this.state = StepByStepFragmentMatching.CONTINUE;
					setFragmentPairs(null);
					dispose();
				}
			});
			cancelBtn = new JButton(GUIConstants.BUTTON_DONE);
			cancelBtn.addActionListener(new ActionListener() {
				/* if the ok button was pressed close dialog */
				public void actionPerformed(ActionEvent _event) {
					controller.setStatus(GUIConstants.STEPBYSTEP__FRAGMENTMATCHING_CANCELED);
					Dlg_StepByStep4.this.state = StepByStepFragmentMatching.CANCEL;
					setFragmentPairs(null);
					dispose();
					setActive(StepByStepFragmentMatching.STOPPED);
				}
			});
			// Continue Button
			panel.add(continueBtn);
			//	Cancel Button
			panel.add(cancelBtn);
			return panel;
		}

		/*
		 * initialize this Dialog and add all needed Components (depending on
		 * the given default values or values of an existing matcher config)
		 */
		protected void init() {
			Container mainPanel = new JPanel();
			getContentPane().add(mainPanel, BorderLayout.CENTER);
			GridBagLayout gbl = new GridBagLayout();
			mainPanel.setLayout(gbl);
			GridBagConstraints gbc = new GridBagConstraints();
			int start = 0;
			int height = 1;
			String info1;
			if (matchresultName == null) {
				if (isMatchingAborted()) {
					info1 = GUIConstants.MATCHING_STOP;
				} else {
					info1 = GUIConstants.NO_RESULTS;
				}
			} else {
				info1 = matchresultName + GUIConstants.CREATED;
			}
			// Fragment Label
			gbc = MainWindow.makegbc(0, start, 1, height);
			JLabel label = new JLabel(info1, SwingConstants.LEFT);
			label.setFont(MainWindow.FONT14);
			gbl.setConstraints(label, gbc);
			mainPanel.add(label);
			start += height;
			gbc = MainWindow.makegbc(0, start, 1, height);
			label = new JLabel(GUIConstants.FM_CONTINUE, SwingConstants.LEFT);
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
		 * @return state
		 */
		public int getState() {
			return state;
		}
	}
}