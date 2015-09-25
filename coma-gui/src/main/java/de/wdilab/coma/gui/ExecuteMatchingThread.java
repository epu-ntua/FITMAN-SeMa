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

import java.util.ArrayList;

import org.antlr.runtime.tree.Tree;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.gui.dlg.StepByStepFragmentMatching;
import de.wdilab.coma.gui.view.MatchresultView2;
import de.wdilab.coma.center.reuse.MappingReuse;
import de.wdilab.coma.matching.Workflow;
import de.wdilab.coma.matching.execution.ExecWorkflow;
import de.wdilab.coma.matching.validation.TreeToWorkflow;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;

import javax.swing.JOptionPane;

/**
 * ExecuteMatchingThread is a Thread, that runs the Matching (while the GUI
 * still can be used)
 * 
 * @author Sabine Massmann
 */
public class ExecuteMatchingThread extends Thread {
	// ----------------------------------------------
	// STATIC FINAL
	// ----------------------------------------------
	static final int NORMAL = 0;
	static final int STEPBYSTEP = 1;
//	static final int ReuseStrategy_EXTENDED = 4;
//	static final int ReuseStrategy = 5;
//	static final int TAXONOMY = 6;
//	static final int MATCHFRAGMENT = 7;
//	static final int COMBINEDReuseStrategy = 8;
//	// ----------------------------------------------
	boolean debug = false;
	Controller controller = null;
	int state = NORMAL;
	StepByStepFragmentMatching step = null;
//	// MatchResult[] results = null;
	Graph sourceGraph = null, targetGraph = null;
//	Source source = null, target = null;
	ArrayList innersSource = null, innersTarget = null;
	MatchResult selected = null;
//	DlgALL_Reuse dlg_reuse = null;
	Manager manager = null;
//	MatchResult onlyMatchResult = null;
	String workflow = null;
//	int matcherOrg = 0;
//	int ReuseStrategyID;
//
	/*
	 * Constructor of ExecuteMatchingThread
	 */
	public ExecuteMatchingThread(MainWindow _mainWindow,
			Controller _controller, int _state, String workflow) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		this.workflow = workflow;
	}
//
//	/*
//	 * Constructor of ExecuteMatchingThread
//	 */
//	public ExecuteMatchingThread(Manager _manager, Source _source,
//			Source _target, int _matcher) {
//		// super(_mainWindow);
//		super();
//		manager = _manager;
//		state = ReuseStrategy_EXTENDED;
//		source = _source;
//		target = _target;
//		ReuseStrategyID = _matcher;
//	}
//
//	/*
//	 * Constructor of ExecuteMatchingThread
//	 */
//	public ExecuteMatchingThread(Manager _manager, Graph _source,
//			Graph _target, int _matcher) {
//		// super(_mainWindow);
//		super();
//		manager = _manager;
//		state = ReuseStrategy_EXTENDED;
//		sourceGraph = _source;
//		targetGraph = _target;
//		ReuseStrategyID = _matcher;
//	}
//
//	/*
//	 * Constructor of ExecuteMatchingThread
//	 */
//	public ExecuteMatchingThread(MainWindow _mainWindow,
//			Controller _controller, int _state, DlgALL_Reuse _dlg_reuse,
//			Source _source, Source _target) {
//		super(_mainWindow);
//		controller = _controller;
//		state = _state;
//		dlg_reuse = _dlg_reuse;
//		source = _source;
//		target = _target;
//	}
//
	/*
	 * Constructor of ExecuteMatchingThread
	 */
	public ExecuteMatchingThread(MainWindow _mainWindow,
			Controller _controller, int _state, StepByStepFragmentMatching _step) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		sourceGraph = _step.getSource();
		targetGraph = _step.getTarget();
		// innersSource = step.getInnersSource();
		innersSource = _step.getAllInnersSource();
		// innersTarget = step.getInnersTarget();
		innersTarget = _step.getAllInnersTarget();
		selected = _step.getSelected();
		step = _step;
	}

	/*
	 * to start the Thread call start()
	 */
	public void run() {
		// TODO add missing match execution
		switch (state) {
		case NORMAL:
//		case MATCHFRAGMENT:
			executeMatching(workflow);
			break;
//		case TAXONOMY:
//			executeMatching();
//			controller.setStrategy(strategyOrg);
//			controller.setMatcherAllContext(matcherOrg);
//			break;
		case STEPBYSTEP:
			executeFragmentMatching();
			controller.getMatchresultView().setChanged(true);
			break;
//		case ReuseStrategy_EXTENDED:
//			executeOnlyMatching();
//			break;
//		case ReuseStrategy:
//			executeReuse();
//			break;
//		case COMBINEDReuseStrategy:
//			executeCombinedReuse();
//			break;
		}
	}

////	private void executeOnlyMatching() {
////		if ((source == null) && (sourceGraph == null)) {
////			System.out.println("Error: executeOnlyMatching() - no source");
////			return;
////		}
////		if (target == null && (targetGraph == null)) {
////			System.out.println("Error: executeOnlyMatching() - no target");
////		}
////		if (sourceGraph == null) {
////			sourceGraph = manager.loadGraph(source, true);
////		}
////		if (targetGraph == null) {
////			targetGraph = manager.loadGraph(target, true);
////		}
////		if (Source.TYPE_ONTOLOGY==sourceGraph.getSource().getType() || 
////				Source.TYPE_ONTOLOGY==targetGraph.getSource().getType()){
////			// Always save in Resolved representation - for ontologies
////			sourceGraph = sourceGraph.getGraph(Graph.GRAPH_STATE_DEFAULT_ONTOLOGY);
////			targetGraph = targetGraph.getGraph(Graph.GRAPH_STATE_DEFAULT_ONTOLOGY);
////		} else {
////			// Always save in Simplified representation - for xml schemas and relational
////			sourceGraph = sourceGraph.getGraph(Graph.GRAPH_STATE_DEFAULT_XML_REL);
////			targetGraph = targetGraph.getGraph(Graph.GRAPH_STATE_DEFAULT_XML_REL);
////			if (sourceGraph==null || targetGraph==null){				
////				System.out.println("saveGraph(): Transformation to SIMPLIFIED failed, therefore use REDUCED");
////				sourceGraph = sourceGraph.getGraph(Graph.GRAPH_STATE_REDUCED);
////				targetGraph = targetGraph.getGraph(Graph.GRAPH_STATE_REDUCED);
////			}
////		}
//		
////		ArrayList inputSource = sourceGraph.getRoots();
////		ArrayList inputTarget = targetGraph.getRoots();
//		manager.prepareMatch(sourceGraph, targetGraph);
//		// MatcherConfig matcherConfig = controller.getManager()
//		// .getMatcherConfig(controller.getReuseStrategy());
//		// controller.getManager().loadManager();
//		// GenericMatcher matcher =
//		// controller.getManager().getMatcher(matcherConfig, true);
//		MatcherConfig reuseConfig = manager
//				.getMatcherConfig(ReuseStrategyID);
//		// GenericMatcher ReuseStrategy =
//		// controller.getManager().getMatcher(reuseConfig, true);
////		int strategy = reuseConfig.getStrategy();
//		String info = reuseConfig.toString();
//		// TODO:
//		MatcherConfig contextMatcher = manager.getMatcherConfig(reuseConfig.getContextmatcher());
//		onlyMatchResult = manager.matchAllContextDirect(sourceGraph.getAllPaths(),
//				targetGraph.getAllPaths(), contextMatcher);
////		if (strategy == Manager.MATCH_STRAT_ALLCONT) {
////			MatcherConfig config = manager.getMatcherConfig(reuseConfig
////					.getNodematcher());
////			if (state == MATCHFRAGMENT) {
////				onlyMatchResult = manager.matchAllContextGlobal(
////						inputSource, inputTarget, config);
////			} else {
////				onlyMatchResult = manager.matchAllContext(inputSource,
////						inputTarget, config);
////			}
////			info = config.getName();
////		} else if (strategy == Manager.MATCH_STRAT_FILTCONT) {
////			MatcherConfig nodeMatcher = controller.getManager()
////					.getMatcherConfig(reuseConfig.getNodematcher());
////			MatcherConfig contextMatcher = controller.getManager()
////					.getMatcherConfig(reuseConfig.getNodematcher());
////			if (state == MATCHFRAGMENT) {
////				onlyMatchResult = manager.matchFilteredContextGlobal(
////						inputSource, inputTarget, nodeMatcher, contextMatcher);
////			} else {
////				onlyMatchResult = manager.matchFilteredContext(
////						inputSource, inputTarget, nodeMatcher, contextMatcher);
////			}
////			info = nodeMatcher.getName() + GUIConstants.STROKE
////					+ contextMatcher.getName();
////		}
//		if (onlyMatchResult != null) {
//			onlyMatchResult.setMatchInfo(info);
//		}
//		// onlyMatchResult = manager.matchAllContext(inputSource,
//		// inputTarget, config);
//	}
//
//	private void executeReuse() {
//		controller.getManagementPane().setMenuStateRun(true);
//		manager = controller.getManager();
//		MatcherConfig matcherConfig = manager
//				.getMatcherConfig(controller.getReuseStrategy());
//		Graph sourceGraph = controller.getMatchresult().getSourceGraph();
//		sourceGraph = sourceGraph.getGraph(controller.getPreprocessing());
//		Graph targetGraph = controller.getMatchresult().getTargetGraph();
//		targetGraph = targetGraph.getGraph(controller.getPreprocessing());
//		manager.prepareMatch(sourceGraph, targetGraph);
//		GenericMatcher ReuseStrategy = manager.getMatcher(matcherConfig,
//				true);
//		MatchResult result = ((ReuseStrategy) ReuseStrategy).matchReuse(
//				ReuseStrategy.getId(), sourceGraph, targetGraph);
//		controller.getMainWindow().getNewContentPane().addMatchResult(
//				result, true);
//		// controller.setNewMatchResult(result, true);
//		controller.getManagementPane().setMenuStateRun(false);
//	}
//
//	private void executeCombinedReuse() {
//		controller.getManagementPane().setMenuStateRun(true);
//		manager = controller.getManager();
////		MatcherConfig matcherConfig = manager
////				.getMatcherConfig(controller.getCombinedReuseStrategy());
//		sourceGraph = controller.getMatchresult().getSourceGraph();
//		sourceGraph = sourceGraph.getGraph(controller
//				.getPreprocessing());
//		targetGraph = controller.getMatchresult().getTargetGraph();
//		targetGraph = targetGraph.getGraph(controller
//				.getPreprocessing());
////		GenericMatcher ReuseStrategy = manager.getMatcher(matcherConfig,
////				true);
////		ArrayList sources = controller.getManager().getSourcesOfUrl(
////				sourceGraph.getSource().getUrl());
////		ArrayList targets = controller.getManager().getSourcesOfUrl(
////				targetGraph.getSource().getUrl());
//		Source source = sourceGraph.getSource();
//		Source target = targetGraph.getSource();
////		String sourceName = source.getName();
//		HashMap<Object, Float> srValues = controller.getSourceRelationshipValues();
//		if (srValues == null) {
//			srValues = new HashMap<Object, Float>();
//		}
//		ArrayList<SimplePath> vertexPaths = new ArrayList<SimplePath>();
//		ArrayList<Float> pathSimsComplete = new ArrayList<Float>();
//		ArrayList<Float> pathSimsIncomplete = new ArrayList<Float>();
//		int topK = 0; // we want all possible matchresult paths
//		int length = 2; // of length 2
//		boolean exact = true; // only the given length
//		int pivotschema = Source.UNDEF; // no pivotschema given
//		ArrayList relPathsComplete = ReuseStrategy
//				.calculateCompleteMatchresultPaths(controller
//						.getManager(), source, target, vertexPaths,
//						srValues, topK, pathSimsComplete, pivotschema, exact,
//						length);
//		ArrayList relPathsIncomplete = ReuseStrategy
//				.calculateIncompleteMatchresultPaths(controller
//						.getManager(), source, target, vertexPaths,
//						srValues, topK, pathSimsIncomplete, pivotschema, exact,
//						length);
//		controller.setSourceRelationshipValues(srValues);
//		ArrayList relPaths = new ArrayList();
//		if (relPathsComplete != null) {
//			relPaths.addAll((ArrayList) relPathsComplete.clone());
//		}
//		if (relPathsIncomplete != null) {
//			relPaths.addAll((ArrayList) relPathsIncomplete.clone());
//		}
//		if (relPaths.size() == 0) {
//			controller.setStatus(GUIConstants.COMBINEDREUSE_ABORTED);
//			return;
//		}
//		ArrayList<Float> pathSims = new ArrayList<Float>();
//		HashMap<Object, Float> simValues = controller.getSimValues();
//		if (simValues == null) {
//			simValues = new HashMap<Object, Float>();
//		}
//		// sort matchresult paths with the similarity of name, url and provider
//		relPaths = ReuseStrategy.sortPathsBySourceSim(source, controller
//				.getManager(), relPaths, pathSims, simValues);
//		controller.setSimValues(simValues);
//		ArrayList help = (ArrayList) relPaths.clone();
//		if (relPathsIncomplete != null) {
//			relPaths.removeAll(relPathsIncomplete);
//		}
//		relPathsComplete = relPaths;
//		if (relPathsComplete != null) {
//			help.removeAll(relPathsComplete);
//		}
//		relPathsIncomplete = help;
//		DlgALL_Reuse dlg2 = new DlgALL_Reuse(controller, sourceGraph,
//				targetGraph, relPathsComplete, relPathsIncomplete, true);
//		dlg2.showDlg(controller.getDialogPosition(), MainWindow.DIM_REUSE);
//		controller.getManagementPane().setMenuStateRun(false);
//	}
//
//	public MatchResult getOnlyMatchResult() {
//		return onlyMatchResult;
//	}
//
//	/*
//	 * set the status bar, restrict the menu, run Matching, extend the menu,
//	 * update the status bar
//	 */
//	private void executeMatching() {
//		controller.setStatus(GUIConstants.EX_STARTED);
//		controller.getManagementPane().setMenuStateRun(true);
//		executeMatching(controller.getStrategy());
//		controller.getManagementPane().setMenuStateRun(false);
//	}
//
	/*
	 * set the status bar, restrict the menu, run Matching, extend the menu,
	 * update the status bar
	 */
	private void executeFragmentMatching() {
		if ((sourceGraph == null) || (targetGraph == null)
				|| (innersSource == null) || (innersTarget == null)) {
			return;
		}
		controller.setStatus(GUIConstants.EX_STARTED);
		controller.getManagementPane().setMenuStateRun(true);
		executeFragmentMatching(sourceGraph, targetGraph,
				innersSource, innersTarget);
		controller.getManagementPane().setMenuStateRun(false);
	}

	/*
	 * run the Matching with the given options (preprocessing, strategie)
	 */
	private void executeMatching(String workflow) {
		if (workflow == null) {
			return;
		}
		DataAccess accessor = controller.getAccessor();
		manager = controller.getManager();
		MatchresultView2 matchresultView = controller.getMatchresultView();
		Graph sourceGraph, targetGraph;
		switch (state) {
		case NORMAL:
//		case TAXONOMY:
//		case MATCHFRAGMENT:
			sourceGraph = controller.getGUIMatchresult().getSourceGraph();
			targetGraph = controller.getGUIMatchresult().getTargetGraph();
			break;
		default:
			return;
		}		
		
//		String info = null;
		System.out.println("Execute " + workflow);
		String value = accessor.getWorkflowVariable(workflow);
		System.out.println(value);
		String workflowValue = accessor.getWorkflowVariableWithoutVariables(workflow);
		System.out.println("build tree from: " + workflowValue);
		Tree tree = TreeToWorkflow.getTree(workflowValue);
//		System.out.println(tree.toString());
//		System.out.println(tree.toStringTree());
		Workflow w = TreeToWorkflow.buildWorkflow(tree);
		MatchResult[] results = null;
		if (w==null) {
			MappingReuse reuse = MappingReuse.buildWorkflow(tree);
			results = new MatchResult[1];
			results[0] = reuse.executeReuse(manager, sourceGraph, targetGraph);
		} else {
			w.setSource(sourceGraph.getGraph(controller.getPreprocessing()));
			w.setTarget(targetGraph.getGraph(controller.getPreprocessing()));
			
			// get the source and target schema graph depending on selected
			// preprocessing option
			sourceGraph = sourceGraph.getGraph(controller.getPreprocessing());
			targetGraph = targetGraph.getGraph(controller.getPreprocessing());
			// execute matching depending on the selected option
			// Input for the matching process
			// needed for showing the selected fragments 
			// and if shared at all places
			ArrayList inputSource = null;
			ArrayList inputTarget = null;
			if (controller.getView()==MainWindow.VIEW_GRAPH){
				 inputSource = MatchresultView2.getSelectedFragmentPaths(matchresultView.getSourceTree());
				 inputTarget = MatchresultView2.getSelectedFragmentPaths(matchresultView.getTargetTree());
			} else {
				 inputSource = MatchresultView2.getSelectedFragments(matchresultView.getSourceTree());
				 inputTarget = MatchresultView2.getSelectedFragments(matchresultView.getTargetTree());
			}
	//		int fragSel = controller.getFragmentIdentification();
	
			if ((inputSource != null) && (inputTarget != null) && 
					!inputSource.isEmpty() && !inputTarget.isEmpty() ) {
	//			if (workflow.equals(GUIConstants.STRAT_ALLCONTEXT)
	//					|| workflow.equals(GUIConstants.STRAT_FILTEREDCONTEXT)) {
					String question = GUIConstants.USE_SEL_FRAG
							+ GUIConstants.PRESS_NO_TO_USE_ALL;
					int res = JOptionPane.showConfirmDialog(controller
							.getMainWindow(), question, GUIConstants.QUESTION,
							JOptionPane.YES_NO_OPTION);
	//				if (res == JOptionPane.NO_OPTION) {
	//					controller.getMainWindow().clearMatchresultView();
	//					inputSource = sourceGraph.getRoots();
	//					inputTarget = targetGraph.getRoots();
	//				} else
					if (res == JOptionPane.YES_OPTION) {
	//					state = MATCHFRAGMENT;
						w.setSourceSelected(inputSource);
						w.setTargetSelected(inputTarget);
						controller.getMainWindow().clearMatchresultView();
					}
	//			} else	if (fragSel != Manager.FRAG_STRAT_USER) {
	//				String question = GUIConstants.USE_SEL_FRAG
	//						+ GUIConstants.PRESS_NO_TO_USE_FRAG;
	//				int res = JOptionPane.showConfirmDialog(controller
	//						.getMainWindow(), question, GUIConstants.QUESTION,
	//						JOptionPane.YES_NO_OPTION);
	//				if (res == JOptionPane.NO_OPTION) {
	//					controller.getMainWindow().clearMatchresultView();
	//					inputSource = null;
	//					inputTarget = null;
	//				} else {
	//					fragSel = Manager.FRAG_STRAT_USER;
	//				}
	//				controller.getMainWindow().clearMatchresultView();
	//			}
			}
			 ExecWorkflow exec = new ExecWorkflow(controller.getAccessor());
			results = exec.execute(w);
		}
		if (results!=null){
			if (results.length==1){
				results[0].setMatchInfo(value);
				results[0].setName("MatchResult");
			} else {
				for (int i = 0; i < results.length; i++) {
					results[i].setMatchInfo(value);
					results[i].setName("MatchResult"+i);
				}
			}
		}
//		if ((inputSource == null) && (inputTarget == null)) {
//			if (_strategie.equals(GUIConstants.STRAT_ALLCONTEXT)
//					|| _strategie.equals(GUIConstants.STRAT_FILTEREDCONTEXT)) {
//				inputSource = sourceGraph.getRootElements();
//				inputTarget = targetGraph.getRootElements();
//			} else if (_strategie.equals(GUIConstants.STRAT_FRAGMENT)) {
//				if (fragSel == Manager.FRAG_STRAT_USER) {
//					JOptionPane.showMessageDialog(controller
//							.getMainWindow(), GUIConstants.SELECT_FRAG
//							+ GUIConstants.SRC_SCHEMA + GUIConstants.OR
//							+ GUIConstants.TRG_SCHEMA + GUIConstants.LINEBREAK
//							+ GUIConstants.CHANGE_FRAG_SEL, GUIConstants.INFORMATION,
//							JOptionPane.INFORMATION_MESSAGE);
//					controller.setStatus(GUIConstants.NO_RESULTS);
//					return;
//				}
//				inputSource = Manager
//						.determineFragments(sourceGraph, fragSel);
//				inputTarget = Manager
//						.determineFragments(targetGraph, fragSel);
//			}
//		}
//		if (inputSource == null && (inputTarget != null)) {
//			String question = GUIConstants.SEL_TRG_FRAG_AGAINST_WHOLE
//					+ GUIConstants.PRESS_NO_TO_USE_ALL;
//			int res = JOptionPane.showConfirmDialog(controller
//					.getMainWindow(), question, GUIConstants.QUESTION,
//					JOptionPane.YES_NO_OPTION);
//			if (res == JOptionPane.YES_OPTION) {
//				if (_strategie.equals(GUIConstants.STRAT_FRAGMENT)
//						&& !(fragSel == Manager.FRAG_STRAT_USER)) {
//					inputSource = Manager
//							.determineFragments(sourceGraph, fragSel);
//					fragSel = Manager.FRAG_STRAT_USER;
//				} else {
//					state = MATCHFRAGMENT;
//					inputSource = sourceGraph.getRootElements();
//				}
//				controller.getMatchresultView().getTargetTree()
//						.setSelectionNull();
//				controller.getMatchresultView().setLastSelectedTree(
//						MainWindow.NONE);
//			} else if (res == JOptionPane.NO_OPTION) {
//				if (_strategie.equals(GUIConstants.STRAT_FRAGMENT)) {
//					inputSource = Manager
//							.determineFragments(sourceGraph, fragSel);
//					inputTarget = Manager
//							.determineFragments(targetGraph, fragSel);
//				} else {
//					inputSource = sourceGraph.getRootElements();
//					inputTarget = targetGraph.getRootElements();
//				}
//				controller.getMainWindow().clearMatchresultView();
//				// controller.getMainWindow().repaint();
//			}
//		}
//		if (inputTarget == null && (inputSource != null)) {
//			String question = GUIConstants.SEL_SRC_FRAG_AGAINST_WHOLE
//					+ GUIConstants.PRESS_NO_TO_USE_ALL;
//			int res = JOptionPane.showConfirmDialog(controller
//					.getMainWindow(), question, GUIConstants.QUESTION,
//					JOptionPane.YES_NO_OPTION);
//			if (res == JOptionPane.YES_OPTION) {
//				if (_strategie.equals(GUIConstants.STRAT_FRAGMENT)
//						&& !(fragSel == Manager.FRAG_STRAT_USER)) {
//					inputTarget = Manager
//							.determineFragments(targetGraph, fragSel);
//					fragSel = Manager.FRAG_STRAT_USER;
//				} else {
//					state = MATCHFRAGMENT;
//					inputTarget = targetGraph.getRootElements();
//				}
//				controller.getMatchresultView().getSourceTree()
//						.setSelectionNull();
//				controller.getMatchresultView().setLastSelectedTree(
//						MainWindow.NONE);
//			} else if (res == JOptionPane.NO_OPTION) {
//				if (_strategie.equals(GUIConstants.STRAT_FRAGMENT)) {
//					inputSource = Manager
//							.determineFragments(sourceGraph, fragSel);
//					inputTarget = Manager
//							.determineFragments(targetGraph, fragSel);
//				} else {
//					inputSource = sourceGraph.getRootElements();
//					inputTarget = targetGraph.getRootElements();
//				}
//				controller.getMainWindow().clearMatchresultView();
//				// controller.getMainWindow().repaint();
//			}
//		}


		
		
//		if (_strategie.equals(GUIConstants.STRAT_ALLCONTEXT)) {
//			MatcherConfig config = controller.getManager()
//					.getMatcherConfig(controller.getMatcherAllContext());
//		    if (config.getBaseMatcher() == Manager.UNDEF){
//		    	// Usermatcher
//		    	result =  manager.matchUserMatcher(sourceGraph, targetGraph, config);
//		    } else {
//				if (state == MATCHFRAGMENT) {
//					result = manager.matchAllContextGlobal(inputSource,
//							inputTarget, config);
//				} else {
//					result = manager.matchAllContext(inputSource, inputTarget,
//							config);
//				}
//		    }
//			info = config.getName();
//		} else if (_strategie.equals(GUIConstants.STRAT_FILTEREDCONTEXT)) {
//			MatcherConfig nodeMatcher = controller.getManager()
//					.getMatcherConfig(
//							controller.getNodeMatcherFilteredContext());
//			MatcherConfig contextMatcher = controller.getManager()
//					.getMatcherConfig(
//							controller.getContextMatcherFilteredContext());
//			if (state == MATCHFRAGMENT) {
//				result = manager.matchFilteredContextGlobal(inputSource,
//						inputTarget, nodeMatcher, contextMatcher);
//			} else {
//				result = manager.matchFilteredContext(inputSource, inputTarget,
//						nodeMatcher, contextMatcher);
//			}
//			info = nodeMatcher.getName() + GUIConstants.STROKE
//					+ contextMatcher.getName();
//		} else if (_strategie.equals(GUIConstants.STRAT_FRAGMENT)) {
//			int matchStrat = controller.getFragmentStrategy();
//			MatcherConfig nodeMatcher = null;
//			MatcherConfig contextMatcher = null;
//			if (matchStrat == Manager.MATCH_STRAT_ALLCONT) {
//				contextMatcher = controller.getManager()
//						.getMatcherConfig(
//								controller.getMatcherAllContext());
//				nodeMatcher = contextMatcher;
//			} else /*if (matchStrat == Manager.MATCH_STRAT_FILTCONT)*/ {
//				nodeMatcher = controller
//						.getManager()
//						.getMatcherConfig(
//								controller.getNodeMatcherFilteredContext());
//				contextMatcher = controller.getManager()
//						.getMatcherConfig(
//								controller
//										.getContextMatcherFilteredContext());
//			}
//			result = manager.matchFrag(inputSource, inputTarget, fragSel,
//					matchStrat, nodeMatcher, contextMatcher);
//			info = Manager.fragStratToString(fragSel) + GUIConstants.STROKE
//					+ nodeMatcher.getName() + GUIConstants.STROKE
//					+ contextMatcher.getName() + GUIConstants.STROKE
//					+ contextMatcher.getName() + GUIConstants.STROKE + matchStrat;
//		} else if (_strategie.equals(GUIConstants.STRAT_NODES)) {
//			MatcherConfig nodeMatcher = controller
//			.getManager()
//			.getMatcherConfig(
//					controller.getMatcherNodes());
//			ArrayList nodesSrc = new ArrayList();
//			Iterator verticesIt = sourceGraph.getVertexSet().iterator();
//			while (verticesIt.hasNext()){
//				nodesSrc.add(verticesIt.next());				
//			}
//			nodesSrc = MatchresultView2.sortNodesNames(nodesSrc);
//			ArrayList nodesTrg = new ArrayList();
//			verticesIt = targetGraph.getVertexSet().iterator();
//			while (verticesIt.hasNext()){
//				nodesTrg.add(verticesIt.next());				
//			}
//			nodesTrg = MatchresultView2.sortNodesNames(nodesTrg);
//			
//			result = controller.getManager().matchNodesDirect(nodesSrc, nodesTrg, nodeMatcher);
//			info = "Node Matching: " + nodeMatcher.getName();
//		}
		if (results != null) {
			for (int i = 0; i < results.length; i++) {
				MatchResult current = results[i];
				if (current.getMatchCount() ==0){
					continue;
				}
//				if (info != null) {
//					current.setMatchInfo(info);
//				}
				current.setGraphs(sourceGraph, targetGraph);
				if (state == NORMAL 
//						|| state == TAXONOMY
//						|| state == MATCHFRAGMENT
						) {
					controller.getMainWindow().getNewContentPane()
							.addMatchResult(current, true);
					controller.setStatus(GUIConstants.MATCHING_DONE);
				}
				if (debug)
					current.print();
				if (state == NORMAL 
//						|| state == TAXONOMY
//						|| state == MATCHFRAGMENT
						) {
					controller.setNewMatchResult(current, true);
				}
			}
			

		} else {
			if (state == NORMAL 
//					|| state == TAXONOMY
//					|| state == MATCHFRAGMENT
					) {
				controller.setNewMatchResult(null);
				controller.setStatus(GUIConstants.NO_RESULTS);
			}
		}
	}
//
	/*
	 * run the Matching with the given options (preprocessing, strategie)
	 */
	private void executeFragmentMatching(Graph _sourceGraph,
			Graph _targetGraph, ArrayList _innersSource,
			ArrayList _innersTarget) {
		manager = controller.getManager();
//		manager.prepareMatch(_sourceGraph, _targetGraph);
		// execute matching depending on the selected option
		MatchResult result = null;
//		int fragSel = controller.getFragmentIdentification();
		String matchStrat = controller.getFragmentStrategy();
//		MatcherConfig nodeMatcher = null;
//		MatcherConfig contextMatcher = null;
//		if (matchStrat == Manager.MATCH_STRAT_ALLCONT) {
//			contextMatcher = controller.getManager()
//					.getMatcherConfig(controller.getMatcherAllContext());
//			nodeMatcher = contextMatcher;
//		} else if (matchStrat == Manager.MATCH_STRAT_FILTCONT) {
//			nodeMatcher = controller.getManager().getMatcherConfig(
//					controller.getNodeMatcherFilteredContext());
//			contextMatcher = controller.getManager()
//					.getMatcherConfig(
//							controller.getContextMatcherFilteredContext());
//		}
		
		String value = controller.getAccessor().getWorkflowVariableFull(matchStrat);
		Workflow w =  TreeToWorkflow.buildWorkflow(value);
		w.setSource(sourceGraph);
		w.setTarget(targetGraph);
		w.setSourceSelected(_innersSource);
		w.setTargetSelected(_innersTarget);
		w.setSelected(selected);
		ExecWorkflow exec = new ExecWorkflow();
		MatchResult[] results = exec.execute(w);
		if (results==null){
			System.err.println("ExecuteMatchingThread.executeFragmentMatching results unexpected null");
		} else {
			if (results.length>1){
				System.err.println("ExecuteMatchingThread.executeFragmentMatching results unexpected more than one, only first one used");
			}
			result = results[0];
		}
		if (result != null) {
			result.setMatchInfo(Strings.STRAT_FRAGMENT + GUIConstants.COLON
					+ controller.getFragmentRootStrategy() + GUIConstants.COMMA_SPACE 
					+ controller.getFragmentStrategy());
			controller.getMainWindow().getNewContentPane().addMatchResult(
					result, true);
			String matchresultName = controller.getManagementPane()
					.getSelectedWorkMatchresult().getName();
			controller.setStatus(GUIConstants.MATCHING_DONE);
			step.setMatchresultName(matchresultName);
		} else {
			controller.setStatus(GUIConstants.NO_RESULTS);
			step.setMatchresultName(null);
		}
		controller.setNewMatchResult(result, true);
	}
//
//	/**
//	 *            The matcherOrg to set.
//	 */
//	public void setMatcherOrg(int _matcherOrg) {
//		matcherOrg = _matcherOrg;
//	}
//
//	/**
//	 *            The strategyOrg to set.
//	 */
//	public void setStrategyOrg(String _strategyOrg) {
//		strategyOrg = _strategyOrg;
//	}
}