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
import java.util.HashSet;

import javax.swing.ListSelectionModel;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.gui.dlg.Dlg_ChooseFromList;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * SaveSchemaToDBThread is a Thread, that saves a Schema to the database (while
 * the GUI still can be used)
 * 
 * @author Sabine Massmann
 */
public class LoadFromDBThread extends Thread {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	static final int STATE_LOAD_SRC_SCHEMA = 0;
	static final int STATE_LOAD_TRG_SCHEMA = 1;
	static final int STATE_LOAD_MATCHRESULT_DB = 2;
	static final int STATE_LOAD_MATCHRESULT_FILE = 3;
	static final int STATE_EXPORT_FILE = 4;
	// not used: taxonomy
//	static final int STATE_TAXONOMY_CHOOSE = 5;
	static final int STATE_LOAD_MIDDLE_SCHEMA = 6;
	static final int STATE_LOAD_MATCHRESULTS_DB = 7;
	//----------------------------------------------
	boolean debug = true;
	Controller controller = null;
	int state = -1;
	// for STATE_LOAD_SRC_SCHEMA/ STATE_LOAD_TRG_SCHEMA
//	String schemaName = null;
	// for STATE_LOAD_MATCHRESULT_DB
	Source source = null;
	SourceRelationship sr = null;
	// for STATE_LOAD_MATCHRESULT_FILE
	// for STATE_LOAD_MATCHRESULTS_DB
	ArrayList list = null;
	// for export
	String textTree = null;
	// for restricted matchresults
	boolean restrict = false;
	ArrayList sourceList, targetList;

	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public LoadFromDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, Source _source) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		source = _source;
	}

	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public LoadFromDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, SourceRelationship _source) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		sr = _source;
	}

	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public LoadFromDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, ArrayList _list) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		list = _list;
	}

	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public LoadFromDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, SourceRelationship _source, boolean _restrict,
			ArrayList _sourceList, ArrayList _targetList) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		sr = _source;
		restrict = _restrict;
		sourceList = _sourceList;
		targetList = _targetList;
	}

	/*
	 * to start the Thread call start()
	 */
	public void run() {
		controller.getManagementPane().setMenuStateDB(true);
		switch (state) {
			case STATE_LOAD_SRC_SCHEMA :
				loadSourceSchemaFromDB();
				// if there is a target schema loaded and no match result yet
				if ((controller.getGUIMatchresult().getTargetGraph() != null)
						&& (!controller.getGUIMatchresult().containsMatchResult())) {
					controller.setStatus(GUIConstants.MATCH_HINT);
				}
				break;
			case STATE_LOAD_TRG_SCHEMA :
				loadTargetSchemaFromDB();
				// if there is a source schema loaded and no match result yet
				if ((controller.getGUIMatchresult().getSourceGraph() != null)
						&& (!controller.getGUIMatchresult().containsMatchResult())) {
					controller.setStatus(GUIConstants.MATCH_HINT);
				}
				break;
			case STATE_LOAD_MIDDLE_SCHEMA :
				loadMiddleSchemaFromDB();
				break;
			case STATE_LOAD_MATCHRESULT_DB :
				loadMatchresultDB();
				break;
			case STATE_LOAD_MATCHRESULTS_DB :
				loadMatchresultsDB();
				break;
			case STATE_LOAD_MATCHRESULT_FILE :
				loadMatchresultFile();
				break;
			case STATE_EXPORT_FILE :
//				exportSchemaFromDB();
				break;
				// not used: taxonomy
//			case STATE_TAXONOMY_CHOOSE :
//				selectTaxonomy();
//				break;
		}
		controller.getMatchresultView().setDividerLocation(0.5);
		controller.getManagementPane().setMenuStateDB(false);
	}

	/*
	 * return all Matchresults (SourceRelationships that are not internal)
	 */
	public static ArrayList<SourceRelationship> getAllDBMatchresults(Controller _controller) {
		// none Internal
		ArrayList<SourceRelationship> rels = _controller.getManager().getAllSourceRels();
		ArrayList<SourceRelationship> selectedRel = new ArrayList<SourceRelationship>();
		if (rels != null) {
			for (int i = 0; i < rels.size(); i++) {
				SourceRelationship sr =  rels.get(i);
				if (!SourceRelationship.ALL_RELLIST.contains(sr.getType())
						&& !Strings.INTERNAL_SIM.equals(sr.getComment())) {
					Sort.addSortedSourceRelationship(selectedRel, sr);
				}
			}
		}
		if (selectedRel.size() == 0) {
			return null;
		}
		return selectedRel;
	}

	public static ArrayList<Source> getAllSchemas(Controller _controller) {
		// none Internal
		ArrayList<Source> schemas = _controller.getManager().getAllSources();
		ArrayList<Source> selectedSchemas = new ArrayList<Source>();
		if (schemas != null) {
			for (int i = 0; i < schemas.size(); i++) {
				Source s =  schemas.get(i);
				// content = XSD or XDR (not ONTOLOGY
				//						if (!"ABBREVIATION"
				//							.equals(objectList.get(i).toString())
				//							&& !"SYNONYM".equals(objectList.get(i).toString()))
				int type = s.getType();
				String name = s.getName();
				// IS_A (not UNDEF)
				if (Source.TYPE_ONTOLOGY==type) {
					if (!Repository.SRC_ABBREV.equals(name)
							&& !Repository.SRC_SYNONYM.equals(name))
						Sort.addSortedSource(selectedSchemas, s);
//				} else if (SourceRelationship.REL_IS_A==type || SourceRelationship.REL_CONTAINS==type
				} else if (Source.TYPE_INTERN!=type
						&& Source.UNDEF!=type
						)
					Sort.addSortedSource(selectedSchemas, s);
			}
		}
		if (selectedSchemas.size() == 0)
			return null;
		return selectedSchemas;
	}


	/*
	 * return all Matchresults (SourceRelationships that are not internal)
	 */
	public static ArrayList<SourceRelationship> getAllDBMatchresultsInclInternal(Controller _controller) {
		// none Internal
		ArrayList rels = _controller.getManager().getAllSourceRels();
		ArrayList<SourceRelationship> selectedRel = new ArrayList<SourceRelationship>();
		if (rels != null) {
			for (int i = 0; i < rels.size(); i++) {
				SourceRelationship sr = (SourceRelationship) rels.get(i);
				if (!SourceRelationship.ALL_RELLIST.contains(sr.getType())) {
					//						&& !GUIConstants.INTERNAL.equals(sr.getComment()))
					Sort.addSortedSourceRelationship(selectedRel, sr);
				}
			}
		}
		if (selectedRel.size() == 0) {
			return null;
		}
		return selectedRel;
	}

	/*
	 * return all Matchresults that are internal for a special source and target
	 * (SourceRelationships that are not internal)
	 */
	public static ArrayList getInternalDBMatchresults(Controller _controller,
			Source _source, Source _target) {
		HashSet<SourceRelationship> rels = _controller.getManager().getAccessor().getSourceRels(_source.getId(), _target.getId());
		ArrayList<SourceRelationship> selectedRel = new ArrayList<SourceRelationship>();
		if (rels != null){
			for (SourceRelationship sr : rels) {
				if (!SourceRelationship.ALL_RELLIST.contains(sr.getType())
				// only Internal
						&& Strings.INTERNAL_SIM.equals(sr.getComment()))
					selectedRel.add(sr);
			}
		}
		if (selectedRel.size() == 0) {
			return null;
		}
		return selectedRel;
	}

	/*
	 * return all Matchresults that are internal for a special source and target
	 * (SourceRelationships that are not internal)
	 */
	public static SourceRelationship getInternalSimDBMatchresult(
			Manager _manager, Source _source, Source _target) {
		HashSet<SourceRelationship> rels = _manager.getAccessor().getSourceRels(_source.getId(), _target.getId());
		if (rels != null)
			for (SourceRelationship sr : rels) {
				if (!SourceRelationship.ALL_RELLIST.contains(sr.getType())
				// only Internal
						&& Strings.INTERNAL_SIM.equals(sr.getComment())
						&& sr.getType()==SourceRelationship.REL_INTERNAL)
					return sr;
			}
		return null;
	}

	/*
	 * load a matchresult from the db
	 */
	private void loadMatchresultsDB() {
		if (list == null || list.size() < 1) {
			return;
		}
		SourceRelationship sourceRelationship = null;
		MatchResult res = null;
		Graph sourceGraph = null, targetGraph = null;
//		int lowestState = controller.getPreprocessing();
		
		for (int i = 0; i < list.size(); i++) {
			sourceRelationship = (SourceRelationship) list.get(i);
			Source source = controller.getManager().getSource(
					sourceRelationship.getSourceId());
			Source target = controller.getManager().getSource(
					sourceRelationship.getTargetId());
			sourceGraph = controller.getManager().loadGraph(
					source, true);
			targetGraph = controller.getManager().loadGraph(
					target, true);
//			res = controller.getManager().loadMatchResult(
//					sourceGraph, targetGraph, sourceRelationship.getType());
//			if (res.getMatcherName() == null) {
//				res.setMatcherName(GUIConstants.OP_LOADED_DB);
//			}
			MatchResult resTransformed = null;
//			MatchResult.transformMatchResult(res, controller
//					.getPreprocessing());
			if (i != (list.size() - 1)) {
				if (resTransformed!=null){
					controller.getMainWindow().getNewContentPane()
					.addMatchResult(resTransformed, false);
				} else {
					controller.setPreprocessing(res.getSourceGraph().getPreprocessing());
					controller.getMainWindow().getNewContentPane()
							.addMatchResult(res, false);					
				}
			}
		}		
		// set the state of the last loaded Matchresult
		// (might be higher (before Ontology, now XML/Relational) or lower (other way around)
		if (sourceRelationship!=null){
			controller.setPreprocessing(sourceRelationship.getPreprocessing());
		}
//		controller.setPreprocessing(sr.getState());
		//		String source1 = null;
		//		if (controller.getMatchresult().containsSource()) {
		//			source1 = controller.getMatchresult().getSourceName();
		//		}
		//		String source2 = null;
		//		if (controller.getMatchresult().containsTarget()) {
		//			source2 = controller.getMatchresult().getTargetName();
		//		}
		//		String newSource1 = sr.getSource1Name();
		//		String newSource2 = sr.getSource2Name();
		//		boolean transpose = false;
		//		if ((source1 != null) && (source2 != null) &&
		// !source1.equals(source2)
		//				&& source1.equals(newSource2) && source2.equals(newSource1)) {
		//			transpose = true;
		//		}
		//		// just load source schema, when the current one is a different
		// schema
		//		if (((source1 == null) || !source1.equals(newSource1)) && !transpose)
		// {
		controller.loadSourceGraph(sourceGraph, false);
		//		}
		//		// just load target schema, when the current one is a different
		// schema
		//		if (((source2 == null) || !source2.equals(newSource2))) {
		controller.loadTargetGraph(targetGraph, false);
		//		}
		//		if (transpose) {
		//			res = MatchResult.transpose(res);
		//		}
		controller.getMainWindow().getNewContentPane().addMatchResult(res,
				true);
		controller.setStatus(GUIConstants.MATCHRESULT_LOADED_DB);
	}

	/*
	 * load a matchresult from the db
	 */
	private void loadMatchresultDB() {
		if (sr == null) {
			return;
		}
		Source source1 = null;
		if (controller.getGUIMatchresult().containsSource()) {
			source1 = controller.getGUIMatchresult().getSourceSource();
		}
		Source source2 = null;
		if (controller.getGUIMatchresult().containsTarget()) {
			source2 = controller.getGUIMatchresult().getTargetSource();
		}
		Source newSource1 = controller.getManager().getSource(sr.getSourceId());
		Source newSource2 = controller.getManager().getSource(sr.getTargetId());
		boolean transpose = false;
		if ((source1 != null) && (source2 != null) && !source1.equals(source2)
				&& source1.equals(newSource2) && source2.equals(newSource1)) {
			transpose = true;
		}
		// just load source schema, when the current one is a different schema
		if (((source1 == null) || !source1.equals(newSource1)) && !transpose) {
			controller.loadSourceSchema(newSource1);
		}
		// just load target schema, when the current one is a different schema
		if (((source2 == null) || !source2.equals(newSource2)) && !transpose) {
			controller.loadTargetSchema(newSource2);
		}
		// wait until both Schemas are loaded
		while (controller.getLoadSchema() != null
				&& controller.getLoadSchema().isAlive()) {
			// nothing
			//	;
			try {
				Thread.sleep(Controller.WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		MatchResult res = null;
		Graph source1Graph = controller.getManager().loadGraph(newSource1,true);
		Graph source2Graph = controller.getManager().loadGraph(newSource2,true);
		if (transpose) {
			res = controller.getManager().loadMatchResult(
					source1Graph, source2Graph, sr.getType(), sr.getName());
			res = MatchResult.transpose(res);
		} else {
			res = controller.getManager().loadMatchResult(
					source1Graph, source2Graph, sr.getType(), sr.getName());
		}
		if (restrict) {
			// restrict left side and/or right side
			res = MatchResult.restrict(res, sourceList, targetList);
			if (res.getMatchCount() == 0) {
				controller.setStatus(GUIConstants.NO_RESULTS);
				return;
			}
		}
		//			MatchResult res =
		//				manager.loadMatchResult(
		//					matchresult.getSimplifiedSourceSchema(),
		//					matchresult.getSimplifiedTargetSchema(),
		//					sr.getType());
		//			matchresult.setMatchResult(res);
//		if (res.getMatcherName() == null) {
//			res.setMatcherName(GUIConstants.OP_LOADED_DB);
//		}
//		// old
//		res = MatchResult.transformMatchResult(res, controller
//				.getPreprocessing());
		// new
		controller.setPreprocessing(sr.getPreprocessing());
//		res = MatchResult.transformMatchResult(res, sr.getPreprocessing());
		controller.getMainWindow().getNewContentPane().addMatchResult(res,
				true);
		controller.setStatus(GUIConstants.MATCHRESULT_LOADED_DB);
	}

	/*
	 * load a matchresult from a file
	 */
	private void loadMatchresultFile() {
		if (list == null) {
			return;
		}
		Source source1 = null;
		if (controller.getGUIMatchresult().containsSource()) {
			source1 = controller.getGUIMatchresult().getSourceSource();
		}
		Source source2 = null;
		if (controller.getGUIMatchresult().containsTarget()) {
			source2 = controller.getGUIMatchresult().getTargetSource();
		}
		if (list.size() == 1) {
			MatchResult res = (MatchResult) list.get(0);
			loadMatchresultFile(res, source1, source2);
		} else {
			Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(controller
					.getMainWindow(), GUIConstants.CHOOSE_MATCHRESULT, list,
					MainWindow.DIM_SMALL, Dlg_ChooseFromList.MATCHRESULT_FILE,
					ListSelectionModel.SINGLE_SELECTION);
			dialog.setLocation(controller.getDialogPosition());
			MatchResult res = (MatchResult) dialog.showDialog();
			loadMatchresultFile(res, source1, source2);
		}
		controller.setStatus(GUIConstants.MATCHRESULT_LOADED_FILE);
	}

	/*
	 * given the Match Result from the file - load (if necessary) source/target
	 * schema, modify result to the current preprocessing
	 */
	private void loadMatchresultFile(MatchResult _result, Source _source1,
			Source _source2) {
//		if (_result.getMatcherName() == null) {
//			_result.setMatcherName(GUIConstants.OP_LOADED_FILE);
//		}
		Source newSource1 = null;
		if (_result.getSourceGraph() != null) {
			newSource1 = _result.getSourceGraph().getSource();
		}
		Source newSource2 = null;
		if (_result.getTargetGraph() != null) {
			newSource2 = _result.getTargetGraph().getSource();
		}
		boolean transpose = false;
		if ((_source1 != null) && (_source2 != null)
				&& !_source1.equals(_source2) && _source1.equals(newSource2)
				&& _source2.equals(newSource1)) {
			transpose = true;
		}
		// just load source schema, when the current one is a different
		// schema
		if (((_source1 == null) || !_source1.equals(newSource1)) && !transpose) {
			controller.loadSourceSchema(newSource1);
		}
		// just load target schema, when the current one is a different
		// schema
		if (((_source2 == null) || !_source2.equals(newSource2)) && !transpose) {
			controller.loadTargetSchema(newSource2);
		}
		// wait until both Schemas are loaded
		while (controller.getLoadSchema() != null
				&& controller.getLoadSchema().isAlive()) {
			// nothing
			//	;
			try {
				Thread.sleep(Controller.WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//		// old
//		_result = MatchResult.transformMatchResult(_result, controller
//				.getPreprocessing());
//		// new
//		controller.setPreprocessing(sr.getState());
//		res = MatchResult.transformMatchResult(res, sr.getState());
		

		if (transpose) {
			_result = MatchResult.transpose(_result);
		}
		//		controller.setNewMatchResult(_result, true);
		controller.getMainWindow().getNewContentPane().addMatchResult(
				_result, true);
		// controller.setPreprocessing(GUIConstants.PREP_REDUCED);
		// controller.getMainWindow().setPreprocessing(GUIConstants.PREP_REDUCED);
	}

	/*
	 * load a source schema from the database to the GUI
	 */
	private void loadSourceSchemaFromDB() {
		if (debug) {
			System.out.println(source.toString());
		}
		long start = System.currentTimeMillis();
//		Source source = controller.getManager().getSource(
//				schemaName);
//		if (debug) {
//			System.out
//					.println("controller.getManager().getSource(schemaName) done: "
//							+ source.toString());
//		}
		
		//Maßmann
        //if (Source.TYPE_ONTOLOGY==source.getType() ) {
		
		//hung 
		if (Source.TYPE_ONTOLOGY==source.getType()) {
			controller.setPreprocessing(Graph.PREP_DEFAULT_ONTOLOGY);
//			controller.getMainWindow().setPreprocessing(
//					Graph.GRAPH_STATE_DEFAULT_ONTOLOGY);
		}
		Graph Graph = controller.getManager()
				.loadGraph(source, true);
		long end = System.currentTimeMillis();
		if (debug) {
			System.out
					.println("controller.getManager().loadGraph(source, true): done "
							+ (float) (end - start) / 1000 + " s");
		}
		start = System.currentTimeMillis();
		controller.loadSourceGraph(Graph, false);
//		Graph.printGraphInfo();
		end = System.currentTimeMillis();
		if (debug) {
			System.out.println("controller.loadSourceGraph(Graph): done "
					+ (float) (end - start) / 1000 + " s");
		}
		controller.setStatus(GUIConstants.SRC_SCHEMA_LOADED);
		controller.getGUIMatchresult().setMatchResult(null);
	}

	/*
	 * load a source schema from the database to the GUI
	 */
	private void loadMiddleSchemaFromDB() {
		if (debug) {
			System.out.println(source.toString());
		}
		long start = System.currentTimeMillis();
//		Source source = controller.getManager().getSource(
//				schemaName);
		if (debug) {
			System.out
					.println("controller.getManager().getSource(schemaName) done: "
							+ source.toString());
		}
		
		//hung
		if (Source.TYPE_ONTOLOGY==source.getType()){
			controller.setPreprocessing(Graph.PREP_DEFAULT_ONTOLOGY);
//			controller.getMainWindow().setPreprocessing(
//					Graph.GRAPH_STATE_DEFAULT_ONTOLOGY);
		}
		Graph Graph = controller.getManager()
				.loadGraph(source, true);
		long end = System.currentTimeMillis();
		if (debug) {
			System.out
					.println("controller.getManager().loadGraph(source, true): done "
							+ (float) (end - start) / 1000 + " s");
		}
		start = System.currentTimeMillis();
		controller.loadMiddleGraph(Graph);
		end = System.currentTimeMillis();
		if (debug) {
			System.out.println("controller.loadSourceGraph(Graph): done "
					+ (float) (end - start) / 1000 + " s");
		}
//		if (Graph.getSource().getType()==Source.TYPE_ONTOLOGY
//				&& controller.getPreprocessing()> Graph.getPreprocessing()){
		
		//hung
		if ((Graph.getSource().getType()==Source.TYPE_ONTOLOGY)
				&& controller.getPreprocessing()> Graph.getPreprocessing()){
			controller.setPreprocessing(Graph.getPreprocessing());
		}
		controller.setStatus(GUIConstants.MIDDLE_SCHEMA_LOADED);
		//		controller.getMatchresult().setMatchResult(null);
	}

	/*
	 * load a target schema from the database to the GUI
	 */
	private void loadTargetSchemaFromDB() {
		if (debug) {
			System.out.println(source.toString());
		}
		long start = System.currentTimeMillis();
//		Source source = controller.getManager().getSource(
//				schemaName);
		
		//Maßmann
		//if (Source.TYPE_ONTOLOGY==source.getType()) {
	
		//hung
		if (Source.TYPE_ONTOLOGY==source.getType()){
			controller.setPreprocessing(Graph.PREP_DEFAULT_ONTOLOGY);
//			controller.getMainWindow().setPreprocessing(
//					Graph.GRAPH_STATE_DEFAULT_ONTOLOGY);
		}
		Graph Graph = controller.getManager()
				.loadGraph(source, true);
		long end = System.currentTimeMillis();
		if (debug) {
			System.out
					.println("controller.getManager().loadGraph(source, true): done "
							+ (float) (end - start) / 1000 + " s");
		}
		start = System.currentTimeMillis();
		controller.loadTargetGraph(Graph, false);
//		Graph.printGraphInfo();
		end = System.currentTimeMillis();
		if (debug) {
			System.out.println("controller.loadTargetGraph(Graph): done "
					+ (float) (end - start) / 1000 + " s");
		}
		controller.setStatus(GUIConstants.TRG_SCHEMA_LOADED);
		controller.getGUIMatchresult().setMatchResult(null);
	}

//	/**
//	 * load a schema from the database for export
//	 * 
//	 * @author david
//	 */
//	private void exportSchemaFromDB() {
//		System.out.println("LoadFromDBThread: exportSchemaFromDB...");
//		//System.out.println(schemaName);
////		Source source = controller.getManager().getSource(
////				schemaName);
//		// ask for preprocessing ?
//		if (Source.TYPE_ONTOLOGY==source.getType()) {
//			controller.setPreprocessing(Graph.GRAPH_STATE_DEFAULT_ONTOLOGY);
////			controller.getMainWindow().setPreprocessing(
////					Graph.GRAPH_STATE_DEFAULT_ONTOLOGY);
//		}
//		Graph Graph = controller.getManager()
//				.loadGraph(source, true);
//		//or return schema now to call export somewhere else?
//		//String output =
//		textTree = Graph.getTextTree();
//	}

	public String getTextTree() {
		return textTree;
	}
	
	// not used: taxonomy

//	/**
//	 * calculate distanceSim and store MatchResult in DB
//	 * 
//	 * @author david
//	 */
//	private void selectTaxonomy() {
//		// ask for preprocessing ?
//		//		if (Source.SRC_CONT_ONTOLOGY.equals(source.getType())) {
//		//			controller.setPreprocessing(Graph.GRAPH_STATE_LOADED);
//		//			controller.getMainWindow().setPreprocessing(
//		//					Graph.GRAPH_STATE_LOADED);
//		//		}
//		Graph Graph = controller.getManager()
//				.loadGraph(source, true);
//		MatchResult mr = Graph.getDistanceSimMapVertex();
//		// temporarily give to controller for global access within gui
//		controller.getManager().setTaxonomyDistanceSim(mr);
//		controller.setTaxonomy(Graph);
//		/*
//		 * // store into db
//		 * controller.getManager().setDistanceSimResult(mr); // is it ok
//		 * to // go via // controller?
//		 * controller.getManager().saveMatchResult(mr);
//		 * controller.updateAll();
//		 */
//	}
}