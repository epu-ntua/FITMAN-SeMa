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
import java.util.HashMap;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.gui.dlg.DlgALL_Reuse;
import de.wdilab.coma.center.reuse.MappingReuse;
import de.wdilab.coma.matching.validation.TreeToWorkflow;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Source;

/**
 * This class starts the reuse wizard.
 * 
 * @author Sabine Massmann
 */
public class ReuseWizardThread extends Thread {
	// ----------------------------------------------
	// STATIC FINAL
	// ----------------------------------------------
	static final boolean SEL_NAME = true;
	static final boolean SEL_NAMESPACE = true;
	static final boolean SEL_SCHEMA = false;
	static final boolean timeCompose = false;
	static final boolean timeCombine = false;
	static final boolean timeMatcher = false;
	static int CANCEL = 0;
	static int LOAD = 1;
	static int NEXT = 2;
	static boolean onlyNotExisting = true;
	// ----------------------------------------------
	Controller controller;
	Source source, target;
	String sourceName, targetName;
	Graph sourceGraph, targetGraph;
	ArrayList allTmpResults;

	/*
	 * Constructor of AboutDialog
	 */
	public ReuseWizardThread(Controller _controller) {
		// super(parent, GUIConstants.INFORMATION);
		controller = _controller;
		allTmpResults = new ArrayList();
	}

	void startWizard(Manager _manager) {
		sourceName = source.getName();
		targetName = target.getName();
		HashMap<Object, Float> srValues = controller.getSourceRelationshipValues();
		ArrayList<GraphPath<Integer, DefaultEdge>> vertexPaths = new ArrayList<GraphPath<Integer, DefaultEdge>>();
		ArrayList<Float> pathSimsComplete = new ArrayList<Float>();
//		ArrayList<Float> pathSimsIncomplete = new ArrayList<Float>();
		
//		MatcherConfig config = _manager.getMatcherConfig(controller
//				.getReuseStrategy());
//		int topK=20;
//		if (config.getTopK()>topK){
//			topK=config.getTopK();
//		}
//		int length= 3;
//		if (config.getLength()>length){
//			length = config.getLength();
//		}
//		boolean exact=false;

		MappingReuse reuse = MappingReuse.buildWorkflow(TreeToWorkflow.getTree(_manager.getAccessor().getWorkflowVariable("$ReuseW")));
		ArrayList relPathsComplete = reuse			
		.calculateCompleteMappingPaths(controller.getManager(), source, target, vertexPaths, srValues, pathSimsComplete);	

//		ArrayList relPathsIncomplete = ReuseStrategy
//				.calculateIncompleteMatchresultPaths(_manager, source, target,
//						vertexPaths, srValues, topK, // config.getTopK(),
//						pathSimsIncomplete, config.getPivotschema(), exact, //config.getExact(),
//								 length //config.getLength()
//								 );
		controller.setSourceRelationshipValues(srValues);
		ArrayList relPaths = MappingReuse.getTopKMappingPaths(reuse.getTopKPaths(), // config.getTopK(),
				relPathsComplete
//				,relPathsIncomplete 
				,pathSimsComplete
//				,pathSimsIncomplete
				);
		if (relPaths == null) {
			controller.setStatus(GUIConstants.REUSE_ABORTED);
			return;
		}
		ArrayList help = (ArrayList) relPaths.clone();
//		if (relPathsIncomplete != null) {
//			relPaths.removeAll(relPathsIncomplete);
//		}
		relPathsComplete = relPaths;
		help.removeAll(relPathsComplete);
//		relPathsIncomplete = help;
		DlgALL_Reuse dlg2 = new DlgALL_Reuse(controller, sourceGraph,
				targetGraph, relPathsComplete, reuse
//				, relPathsIncomplete, false
				);
		dlg2.showDlg(controller.getDialogPosition(), MainWindow.DIM_REUSE);
	}

	/*
	 * return tmp-results
	 */
	public ArrayList getTmpResults() {
		return allTmpResults;
	}

	/*
	 * to start the Thread call start()
	 */
	public void run() {
		controller.setStatus(GUIConstants.REUSE_MATCHRESULTS);
		GUIMatchResult matchresult = controller.getGUIMatchresult();
		if (!matchresult.containsSource()) {
			// no soure schema given
			controller.setStatus(GUIConstants.NO_SRC_SCHEMA);
			return;
		} else if (!matchresult.containsTarget()) {
			// no target schema given
			controller.setStatus(GUIConstants.NO_TRG_SCHEMA);
			return;
		}
		ArrayList sourceRels = controller.getManager().getAllSourceRels();
		if (sourceRels == null) {
			// no matchresults exist
			controller.setStatus(GUIConstants.NO_MATCHRESULTS);
			return;
		}
		source = matchresult.getSourceSource();
		target = matchresult.getTargetSource();
		sourceGraph = matchresult.getSourceGraph();
		targetGraph = matchresult.getTargetGraph();
		controller.getManagementPane().setMenuStateDB(true);
		controller.getMainWindow().getNewContentPane()
				.setProgressBar(true);
		startWizard(controller.getManager());
		controller.getManagementPane().setMenuStateDB(false);
		controller.getMainWindow().getNewContentPane().setProgressBar(
				false);
	}
}