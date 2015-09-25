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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.ExecuteMatchingThread;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.SimpleJTree;
import de.wdilab.coma.gui.extjtree.ExtJTree;
import de.wdilab.coma.matching.Combination;
import de.wdilab.coma.center.reuse.MappingReuse;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This dialogs show the possible reuse paths (including the existing matchresults)
 * for the user to choose from. The user can select one or several paths.
 * 
 * @author Sabine Massmann
 */
public class DlgALL_Reuse extends Dlg {
	Controller controller;
	SimpleJTree tree;
	boolean restrictResult = false;
	ArrayList sourceList = null, targetList = null;
	ArrayList[] reuseComplete = null
//	, reuseIncomplete1 = null,
//			reuseIncomplete2 = null
			;
	Graph sourceGraph, targetGraph;
	DefaultMutableTreeNode rootDirect, rootComplete, rootIncomplete;
	ArrayList<DefaultMutableTreeNode> rootCompletePaths = new ArrayList<DefaultMutableTreeNode>(),
			rootIncompletePaths = new ArrayList<DefaultMutableTreeNode>();
	ExecuteMatchingThread execute = null;
//	boolean combinedReuse = false;
	ArrayList matchresultPath = null;
	 MappingReuse reuse = null;

	/*
	 * Constructor of Dlg_ReuseMatchresult
	 */
	public DlgALL_Reuse(Controller _controller, Graph _sourceGraph,
			Graph _targetGraph, ArrayList _relPaths, MappingReuse reuse
//			, ArrayList _relPathsIncomplete
//			, boolean _combinedReuse
			) {
		super(_controller.getMainWindow(), GUIConstants.REUSE_FOR
				+ _sourceGraph.getSource().getName() + GUIConstants.COMMA_SPACE
				+ _targetGraph.getSource().getName() + GUIConstants.BRACKET_RIGHT);
		controller = _controller;
		sourceGraph = _sourceGraph;
		targetGraph = _targetGraph;
		this.reuse=reuse;
//		combinedReuse = _combinedReuse;
//		MatcherConfig matcherConfig = _controller.getManager()
//				.getMatcherConfig(_controller.getReuseStrategy());
		reuseComplete = getCompleteMatchresultPaths(_controller, _relPaths,
				reuse.getExact(), reuse.getMaxPathLength());
//		getIncompleteMatchresultPaths(_controller, _relPathsIncomplete,
//				matcherConfig.getExact(), matcherConfig.getLength());
		initTree(reuse.getExact(), reuse.getMaxPathLength());
	}

	private ArrayList[] getCompleteMatchresultPaths(Controller _controller,
			ArrayList _relPaths, boolean _isExact, int _length) {
		if (_relPaths == null) {
			return null;
		}
		ArrayList[] reuse = null;
		if (_isExact) {
			reuse = new ArrayList[1];
			reuse[0] = new ArrayList();
			for (int i = 0; i < _relPaths.size(); i++) {
				ArrayList current = (ArrayList) _relPaths.get(i);
				if (current.size() == _length) {
					reuse[0].add(current);
				}
			}
		} else {
			reuse = new ArrayList[_length];
			for (int i = 0; i < _length; i++) {
				reuse[i] = new ArrayList();
			}
			for (int i = 0; i < _relPaths.size(); i++) {
				ArrayList current = (ArrayList) _relPaths.get(i);
				if (current.size() <= _length) {
					reuse[current.size() - 1].add(current);
				}
			}
		}
		return reuse;
	}

//	private void getIncompleteMatchresultPaths(Controller _controller,
//			ArrayList _relPaths, boolean _isExact, int _length) {
//		if (_relPaths == null) {
//			return;
//		}
//		if (_isExact) {
//			reuseIncomplete1 = new ArrayList[1];
//			reuseIncomplete1[0] = new ArrayList();
//			reuseIncomplete2 = new ArrayList[1];
//			reuseIncomplete2[0] = new ArrayList();
//			for (int i = 0; i < _relPaths.size(); i++) {
//				ArrayList current = (ArrayList) _relPaths.get(i);
//				if (current.size() == _length) {
//					int matchTasks = ReuseStrategy.countMatchTasks(current);
//					switch (matchTasks) {
//						case 1 :
//							reuseIncomplete1[0].add(current);
//							break;
//						case 2 :
//							reuseIncomplete2[0].add(current);
//							break;
//					}
//				}
//			}
//		} else {
//			reuseIncomplete1 = new ArrayList[_length];
//			reuseIncomplete2 = new ArrayList[_length];
//			for (int i = 0; i < _length; i++) {
//				reuseIncomplete1[i] = new ArrayList();
//				reuseIncomplete2[i] = new ArrayList();
//			}
//			for (int i = 0; i < _relPaths.size(); i++) {
//				ArrayList current = (ArrayList) _relPaths.get(i);
//				if (current.size() <= _length) {
//					int matchTasks = ReuseStrategy.countMatchTasks(current);
//					switch (matchTasks) {
//						case 1 :
//							reuseIncomplete1[current.size() - 1].add(current);
//							break;
//						case 2 :
//							reuseIncomplete2[current.size() - 1].add(current);
//							break;
//					}
//				}
//			}
//		}
//	}

	void initTree(boolean _isExact, int _length) {
		Container cp = getContentPane();
		ExtJTree source = controller.getMatchresultView().getSourceTree();
		ExtJTree target = controller.getMatchresultView().getTargetTree();
		if (!source.isSelectionEmpty() || !target.isSelectionEmpty()) {
			JCheckBoxMenuItem fragment = new JCheckBoxMenuItem(
					GUIConstants.RESTRICT_RESULTS, false);
			fragment.setForeground(MainWindow.FOREGROUND_SPECIAL);
			fragment.setFont(MainWindow.FONT14);
			fragment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent _event) {
					restrictResult = ((JCheckBoxMenuItem) _event
							.getSource()).getState();
				}
			});
			cp.add(fragment, BorderLayout.NORTH);
			if (!source.isSelectionEmpty()) {
				sourceList = getObjects(source);
			}
			if (!target.isSelectionEmpty()) {
				targetList = getObjects(target);
			}
		}
		// addWindowListener(new WindowListener(true));
		// Einfaches TreeModel bauen
		DefaultMutableTreeNode root, child;
		root = new DefaultMutableTreeNode(GUIConstants.REUSE_MANUAL);
		if (_isExact) {
			if (_length == 1) {
				rootDirect = new DefaultMutableTreeNode(GUIConstants.DIRECT_RESULTS);
				for (int i = 0; i < reuseComplete[0].size(); i++) {
					// 1 existing matchresult
					ArrayList list = (ArrayList) reuseComplete[0].get(i);
					child = new DefaultMutableTreeNode(list.get(0));
					rootDirect.add(child);
				}
				root.add(rootDirect);
			} else {
				if (reuseComplete != null) {
					addCompleteMatchresultPathsNodes(root, reuseComplete[0],
							_length);
				}
//				if (reuseIncomplete1 != null) {
//					addIncompleteMatchresultPathsNodes(root,
//							reuseIncomplete1[0], _length, 1);
//				}
//				if (reuseIncomplete2 != null) {
//					addIncompleteMatchresultPathsNodes(root,
//							reuseIncomplete2[0], _length, 2);
//				}
			}
		} else {
			if (reuseComplete != null) {
				for (int currentLength = 1; currentLength <= _length; currentLength++) {
					ArrayList current = reuseComplete[currentLength - 1];
					if (current != null && current.size() > 0) {
						if (currentLength == 1) {
							rootDirect = new DefaultMutableTreeNode(
									GUIConstants.DIRECT_RESULTS);
							for (int i = 0; i < reuseComplete[currentLength - 1]
									.size(); i++) {
								// 1 existing matchresult
								ArrayList list = (ArrayList) reuseComplete[0]
										.get(i);
								child = new DefaultMutableTreeNode(list.get(0));
								rootDirect.add(child);
							}
							root.add(rootDirect);
						} else {
							addCompleteMatchresultPathsNodes(root,
									reuseComplete[currentLength - 1],
									currentLength);
						}
					}
				}
			}
//			if (reuseIncomplete1 != null) {
//				for (int currentLength = 1; currentLength <= _length; currentLength++) {
//					addIncompleteMatchresultPathsNodes(root,
//							reuseIncomplete1[currentLength - 1],
//							currentLength, 1);
//				}
//			}
//			if (reuseIncomplete2 != null) {
//				for (int currentLength = 1; currentLength <= _length; currentLength++) {
//					addIncompleteMatchresultPathsNodes(root,
//							reuseIncomplete2[currentLength - 1],
//							currentLength, 2);
//				}
//			}
		}
		// JTree erzeugen
		tree = new SimpleJTree(root, controller
//				, combinedReuse
				);
		tree.setRootVisible(false);
		tree.expandAll(3);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		// JTree einfuegen
		cp.add(new JScrollPane(tree), BorderLayout.CENTER);
		// Handle escape key to close the dialog
		// http://forum.java.sun.com/thread.jsp?thread=462776&forum=57&message=2669506
		KeyStroke escape = KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			public void actionPerformed(ActionEvent _event) {
				dispose();
			}
		};
		getRootPane()
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(escape, GUIConstants.ESCAPE);
		getRootPane().getActionMap().put(GUIConstants.ESCAPE, escapeAction);
		// end escape key support
		JPanel button = getButton();
		cp.add(button, BorderLayout.SOUTH);
	}

	void addCompleteMatchresultPathsNodes(DefaultMutableTreeNode _root,
			ArrayList _complete, int _length) {
		DefaultMutableTreeNode child, subchild;
		if (rootComplete == null) {
			rootComplete = new DefaultMutableTreeNode(GUIConstants.COMPLETE);
		}
		if (_complete != null && _complete.size() > 0) {
			String name = GUIConstants.HTML_B + _length + GUIConstants.B_HTML;
			DefaultMutableTreeNode rootCompleteExact = new DefaultMutableTreeNode(
					name);
			for (int i = 0; i < _complete.size(); i++) {
				ArrayList current = (ArrayList) _complete.get(i);
				child = new DefaultMutableTreeNode(current);
				for (int j = 0; j < current.size(); j++) {
					subchild = new DefaultMutableTreeNode(current.get(j));
					child.add(subchild);
				}
				rootCompleteExact.add(child);
			}
			if (rootCompleteExact.getChildCount() > 0) {
				rootComplete.add(rootCompleteExact);
				rootCompletePaths.add(rootCompleteExact);
				_root.add(rootComplete);
			}
		}
	}

	void addIncompleteMatchresultPathsNodes(DefaultMutableTreeNode _root,
			ArrayList _incomplete, int _length, int _matchTasks) {
		DefaultMutableTreeNode child, subchild;
		if (rootIncomplete == null) {
			rootIncomplete = new DefaultMutableTreeNode(GUIConstants.INCOMPLETE);
		}
		if (_incomplete != null && _incomplete.size() > 0) {
			String name = GUIConstants.HTML_B + _matchTasks + GUIConstants.MATCHTASKS
					+ (_length - _matchTasks) + GUIConstants.B_HTML;
			DefaultMutableTreeNode rootIncompleteExact = new DefaultMutableTreeNode(
					name);
			for (int i = 0; i < _incomplete.size(); i++) {
				ArrayList current = (ArrayList) _incomplete.get(i);
				child = new DefaultMutableTreeNode(current);
				for (int j = 0; j < current.size(); j++) {
					subchild = new DefaultMutableTreeNode(current.get(j));
					child.add(subchild);
				}
				rootIncompleteExact.add(child);
			}
			if (rootIncompleteExact.getChildCount() > 0) {
				rootIncomplete.add(rootIncompleteExact);
				rootIncompletePaths.add(rootIncompleteExact);
				_root.add(rootIncomplete);
			}
		}
	}

	/*
	 * returns for a given tree all selected Nodes and there children (ArrayList
	 * of VertexImpl for each Node)
	 */
	static ArrayList getObjects(ExtJTree _tree) {
		ArrayList<Object> all = new ArrayList<Object>();
		TreePath[] paths = _tree.getSelectionPaths();
		if (paths == null) {
			return null;
		}
		for (int i = 0; i < paths.length; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0]
					.getLastPathComponent();
			getObjects(node, all);
		}
		if (all.size() > 0) {
			return all;
		}
		return null;
	}

	/*
	 * adds for a given node and all children the path (ArrayList of VertexImpl
	 * for each Node)
	 */
	static void getObjects(DefaultMutableTreeNode _node, ArrayList<Object> _all) {
		Object current = _node.getUserObject();
		if (current != null) {
			_all.add(current);
		}
		if (_node.getChildCount() > 0) {
			for (int i = 0; i < _node.getChildCount(); i++) {
				getObjects((DefaultMutableTreeNode) _node.getChildAt(i), _all);
			}
		}
	}

	public static void loadMatchResults(Controller _controller,
			ArrayList _result, Graph _source1Graph,
			Graph _source2Graph) {
		if (_result == null || _result.size() == 0) {
			return;
		}
		int preprocessing = _controller.getPreprocessing();
		_source1Graph = _source1Graph.getGraph(preprocessing);
		_source2Graph = _source2Graph.getGraph(preprocessing);
		_controller.getMainWindow().clearMatchresultView();
		for (int i = 0; i < _result.size(); i++) {
			MatchResult current = (MatchResult) _result.get(i);
			current = MatchResult.transformMatchResult(current, preprocessing);
			_controller.getMainWindow().getNewContentPane().addMatchResult(
					current, true);
		}
	}

	void handleSelection() {
		TreePath[] paths = tree.getSelectionPaths();
		ArrayList<MatchResult> result = new ArrayList<MatchResult>();
		if (paths != null) {
			for (int i = 0; i < paths.length; i++) {
				TreePath path = paths[i];
				ArrayList<MatchResult>	list = handleReusePath(path);
				if (list != null) {
					result.addAll(list);
				}
			}
			Graph source1Graph = controller.getGUIMatchresult()
					.getSourceGraph();
			Graph source2Graph = controller.getGUIMatchresult()
					.getTargetGraph();
			if (result.size() == 0) {
				// no result;
//				if (!combinedReuse){
					controller.setStatus(GUIConstants.NO_RESULTS);
//				}
				return;
			} else if (result.size() > 1) {
				boolean combine = true;
				int value = JOptionPane.showConfirmDialog(this,
						GUIConstants.COMBINE_MATCHRESULTS, GUIConstants.QUESTION,
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (value == JOptionPane.YES_OPTION) {
					combine = true;
				} else if (value == JOptionPane.NO_OPTION) {
					combine = false;
				}
				if (combine) {
					combineAndLoadMatchResults(controller, result,
							source1Graph, source2Graph);
				} else {
					loadMatchResults(controller, result, source1Graph,
							source2Graph);
				}
			} else if (result.size() > 0) {
				// one single Result
				loadMatchResults(controller, result, source1Graph, source2Graph);
			}
			controller.setStatus(GUIConstants.REUSE_MATCHRESULTS_DONE);
		}
	}

	ArrayList<MatchResult> handleReusePath(TreePath _p1) {
		ArrayList<MatchResult> result = new ArrayList<MatchResult>();
		TreePath p2 = _p1.getParentPath();
		boolean debug = false;
		// Root
		// -> DIRECT A: p2(Root)!=null; p3==null
		// -> SourceRelationship B: p2(D/C/I)!=null; p3(Root)!=null; p3==null
		// -> COMPLETE A: p2(Root)!=null; p3==null
		// -> RootComplete B: p2(D/C/I)!=null; p3(Root)!=null; p3==null
		// -> Node of C: p2(RootC/I)!=null; p3(D/C/I)!=null; p3(Root)!=null;
		// p4==null
		// -> SourceRelationship (2,3,4) D: p2(NodeC/I)!=null;
		// p3(RootC/I)!=null; p3(D/C/I)!=null; p4(Root)!=null; p5==null
		// -> INCOMPLETE A: p2(Root)!=null; p3==null
		// -> RootIncomplete B: p2(D/C/I)!=null; p3(Root)!=null; p3==null
		// -> Node of C: p2(RootC/I)!=null; p3(D/C/I)!=null; p3(Root)!=null;
		// p4==null
		// -> SourceRelationship (1,2) D: p2(NodeC/I)!=null; p3(RootC/I)!=null;
		// p3(D/C/I)!=null; p4(Root)!=null; p5==null
		// -> MatchTask (1,2) D: p2(NodeC/I)!=null; p3(RootC/I)!=null;
		// p3(D/C/I)!=null; p4(Root)!=null; p5==null
		if (p2 != null) {
			TreePath p3 = p2.getParentPath();
			if (p3 != null) {
				TreePath p4 = p3.getParentPath();
				if (p4 != null) {
					TreePath p5 = p4.getParentPath();
					DefaultMutableTreeNode object;
					if (p5 != null) {
						// case D: SourceRelationship (I/C), MatchTask
						if (debug) {
							System.out.println("p2, p3, p4, p5!=null");
						}
						object = ((DefaultMutableTreeNode) p2
								.getLastPathComponent());
					} else {
						// case C: SourceRelationship (D), Node of I/C
						if (debug) {
							System.out.println("p2, p3, p4!=null, p5==null");
						}
						object = ((DefaultMutableTreeNode) _p1
								.getLastPathComponent());
					}
					//	Complete or Incomplete Path
					MatchResult res = calculateMatchresultPath(object, false);
					if (res != null) {
						result.add(res);
					}
				} else {
					// case B: RootDirect, RootComplete, RootIncomplete
					if (debug) {
						System.out.println("p2, p3!=null, p4==null");
					}
					DefaultMutableTreeNode object = ((DefaultMutableTreeNode) _p1
							.getLastPathComponent());
					DefaultMutableTreeNode parent = ((DefaultMutableTreeNode) p2
							.getLastPathComponent());
					if (parent.equals(rootDirect)) {
						MatchResult res = controller.getManager().loadMatchResult(
								(SourceRelationship) object.getUserObject());
						if (res != null) {
							result.add(res);
						}
					} else {
						// Complete or Incomplete Path
						for (int j = 0; j < object.getChildCount(); j++) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) object
									.getChildAt(j);
							MatchResult res = calculateMatchresultPath(node, false);
							if (res != null) {
								result.add(res);
							}
						}
					}
				}
			} else {
				// case A: RootDirect, RootComplete, RootIncomplete
				if (debug) {
					System.out.println("p2!=null, p3==null");
				}
				DefaultMutableTreeNode object = ((DefaultMutableTreeNode) _p1
						.getLastPathComponent());
				if (object.equals(rootDirect)) {
					// Match Direct
					for (int j = 0; j < object.getChildCount(); j++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) object
								.getChildAt(j);
						MatchResult res = controller.getManager().loadMatchResult(
								(SourceRelationship) node.getUserObject());
						if (res != null) {
							result.add(res);
						}
					}
				} else {
					// Complete or Incomplete Path
					for (int j = 0; j < object.getChildCount(); j++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) object
								.getChildAt(j);
						for (int k = 0; k < node.getChildCount(); k++) {
							DefaultMutableTreeNode nodeChild = (DefaultMutableTreeNode) node
									.getChildAt(k);
							MatchResult res = calculateMatchresultPath(nodeChild,
									false);
							if (res != null) {
								result.add(res);
							}
						}
					}
				}
			}
		} else {
			// Root, SHOULD NOT BE POSSIBLE
			if (debug) {
				System.out.println("p2==null");
			}
		}
		return result;
	}

	/*
	 * returns a JPanel containing three buttons: Save (DB), Save (File), Cancel
	 * (and add to these button action/ key listener)
	 */
	JPanel getButton() {
		JButton loadBtn, cancelBtn;
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		// new JPanel(new GridLayout(1, 3));
		loadBtn = new JButton(GUIConstants.BUTTON_LOAD);
		loadBtn.addActionListener(new ActionListener() {
			/* if the ok button was pressed close dialog */
			public void actionPerformed(ActionEvent _event) {
				if (tree.getSelectionCount() == 0) {
					// nothing selected
					dispose();
					controller
							.setStatus(GUIConstants.REUSE_NOTHING_SEL);
					return;
				}
				controller
						.setDialogPosition(getLocation());
				handleSelection();
				dispose();
			}
		});
		cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
		cancelBtn.addActionListener(new ActionListener() {
			/* if the ok button was pressed close dialog */
			public void actionPerformed(ActionEvent _event) {
				controller.setStatus(GUIConstants.REUSE_CANCELED);
				controller
						.setDialogPosition(getLocation());
				dispose();
			}
		});
		// Load Button
		panel.add(loadBtn);
		// Cancel Button
		panel.add(cancelBtn);
		return panel;
	}

	MatchResult calculateMatchresultPath(DefaultMutableTreeNode _node, boolean _load) {
		// step2, step3 => get children and perform compose
		MatchResult result = null;
		int children = _node.getChildCount();
		ArrayList<Object> matchresultPath = new ArrayList<Object>();
		for (int i = 0; i < children; i++) {
			matchresultPath.add(((DefaultMutableTreeNode) _node.getChildAt(i))
					.getUserObject());
		}
//		if (combinedReuse) {
//			MatcherConfig matcherConfig = controller
//					.getManager()
//					.getMatcherConfig(controller.getCombinedReuseStrategy());
//			controller.getManager().prepareMatch(
//					controller.getMatchresult().getSourceGraph(),
//					controller.getMatchresult().getTargetGraph());
//			GenericMatcher matcher = controller.getManager()
//					.getMatcher(matcherConfig, true);
//			result = ((ReuseStrategy) matcher).matchCombinedReuse(
//					controller.getManager(), sourceGraph,
//					targetGraph, matchresultPath);
//		} else {
			result = reuse.calculateMappingPath(controller
					.getManager(), matchresultPath, sourceGraph
					.getSource().getId()
//					, controller.getReuseStrategy()
					);
//			result = ReuseStrategy.recreateMatchResult(result, sourceGraph, targetGraph);
//		}
//		if (restrictResult) {
//			// restrict left side and/or right side
//			result = result.restrict(sourceList, targetList);
//		}
		// float[] values= result.compare(resultNEW);
		return result;
	}

	//
	//	static MatchResult composeMatchresults(ArrayList matchresultPath, int
	// aggregation) {
	//		// Compose
	//		MatchResult matchresult_1 = (MatchResult) matchresultPath.get(0);
	//		Graph sourceModel = matchresult_1.getSourceGraph();
	//		sourceModel = sourceModel.getGraph(Graph.GRAPH_STATE_SIMPLIFIED);
	//		matchresult_1 = MatchResult.transformMatchResult(matchresult_1,
	//				Graph.GRAPH_STATE_SIMPLIFIED);
	//
	//		for (int k = 1; k < matchresultPath.size(); k++) {
	//			MatchResult matchresult_2 = (MatchResult) matchresultPath.get(k);
	//			matchresult_2 = MatchResult.transformMatchResult(matchresult_2,
	//					Graph.GRAPH_STATE_SIMPLIFIED);
	//			ArrayList middlePaths = matchresult_1.getTrgObjects();
	//			matchresult_1 = matchresult_1.restrict(null, middlePaths);
	//			matchresult_2 = matchresult_2.restrict(middlePaths, null);
	//
	//			// Graph targetModel_1 = matchresult_1.getTargetGraph();
	//			float[][] simMatrix_1 = matchresult_1.getSimMatrix();
	//			Graph targetModel_2 = matchresult_2.getTargetGraph();
	//			float[][] simMatrix_2 = matchresult_2.getSimMatrix();
	//
	//			// Determine current source, intermediate model and target model
	//			Graph currSrcModel = sourceModel;
	//			Graph currTarModel = targetModel_2;
	//			ArrayList currSrcObjects = matchresult_1.getSrcObjects();
	//			ArrayList currTarObjects = matchresult_2.getTrgObjects();
	//
	//			float[][] compSimMatrix = Combination.compose(simMatrix_1,
	//					simMatrix_2, aggregation);
	//			matchresult_1 = new MatchResult(currSrcObjects, currTarObjects,
	//					compSimMatrix);
	//			matchresult_1.setSourceGraph(currSrcModel);
	//			matchresult_1.setTargetGraph(currTarModel);
	//		}
	//		return matchresult_1;
	//	}
	public static void combineAndLoadMatchResults(Controller _controller,
			ArrayList<MatchResult> _results, Graph _source1Graph,
			Graph _source2Graph) {
		int preprocessing = _controller.getPreprocessing();
		_source1Graph = _source1Graph.getGraph(preprocessing);
		_source2Graph = _source2Graph.getGraph(preprocessing);
		for (int i = 0; i < _results.size(); i++) {
			MatchResult res = _results.get(i);
			res = MatchResult.transformMatchResult(res, preprocessing);
			_results.set(i, res);
		}
		MatchResult newResult = combineMatchResults(_results, _source1Graph,
				_source2Graph, _controller.isUpdatedRepository());
		// _controller.setNewMatchResult(newResult, true);
		_controller.getMainWindow().getNewContentPane().addMatchResult(
				newResult, true);
		_controller.getMainWindow().clearMatchresultView();
	}

	public static MatchResult combineMatchResults(ArrayList _results,
			Graph _source1, Graph _source2,
			boolean _updatedRepository) {
		// Graph source1 = source1Graph
		// .getGraph(Graph.GRAPH_STATE_SIMPLIFIED);
		// Graph source2 = source2Graph
		// .getGraph(Graph.GRAPH_STATE_SIMPLIFIED);
//		ArrayList aPaths = _source1.getAllPaths();
//		ArrayList bPaths = _source2.getAllPaths();
//		float[][][] simCube = new float[_results.size()][][];
//		for (int i = 0; i < _results.size(); i++) {
//			MatchResult mr = (MatchResult) _results.get(i);
//			// ReuseStrategy.recreatePaths(aPaths, bPaths, mr.getSourceGraph(),
//			// mr.getTargetGraph());
////			if (_updatedRepository) {
////				// WORKAROUND: for updated Repository necessary
////				ReuseStrategy.recreateMatchResult(mr, _source1, _source2);
////			}
//			// System.out.println("combineMatchResults: MatchCount: " +
//			// mr.getMatchCount());
//			simCube[i] = ReuseStrategy.orderSimMatrix(mr, aPaths, bPaths);
//		}
//		simCube = ReuseStrategy.trimSimCube(simCube);
//		float[][] simMatrix = null;
//		float[] aggWeights = null;
//		int aggregation = Combination.COM_AVERAGE; 
		// Aggregation
		Combination c = new Combination(Combination.COM_AVERAGE);
		// To match result and return
		MatchResult[] resultsTmp = new MatchResult[_results.size()];
		for (int i = 0; i < resultsTmp.length; i++) {
			resultsTmp[i]=(MatchResult) _results.get(i);
		}
		MatchResult combined = c.combine(resultsTmp);
		combined.setGraphs(_source1, _source2);
		combined.setMatchInfo("COMBINED COMPOSE");
		// System.out.println("combineMatchResults: COMBINED MatchCount: " +
		// combined.getMatchCount());
		return combined;
	}

	/**
	 * @return Returns the matchresultPath.
	 */
	public ArrayList getMatchresultPath() {
		return matchresultPath;
	}
}