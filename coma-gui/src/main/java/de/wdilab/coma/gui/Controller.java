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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import javax.swing.*;

import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.wdilab.coma.gui.extensions.CheckBoxList;
import de.wdilab.coma.gui.extensions.semantica.SASresponse;
import de.wdilab.coma.gui.extensions.semantica.SemanticRepoUtilities;
import de.wdilab.coma.gui.extensions.semantica.SemanticWorkspace;
import de.wdilab.coma.gui.extensions.semantica.TriplesExporter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.center.reuse.MappingReuse;
import de.wdilab.coma.export.relationships.MatchResultExport;
import de.wdilab.coma.gui.dlg.Dlg_About;
import de.wdilab.coma.gui.dlg.Dlg_ChooseFromList;
import de.wdilab.coma.gui.dlg.Dlg_CompareMatchresult;
import de.wdilab.coma.gui.dlg.Dlg_InstanceFiles;
import de.wdilab.coma.gui.dlg.Dlg_ShowHierarchy;
import de.wdilab.coma.gui.dlg.Dlg_ShowSynOrAbb;
import de.wdilab.coma.gui.dlg.Dlg_WorkflowVariables;
import de.wdilab.coma.gui.dlg.StepByStepFragmentMatching;
import de.wdilab.coma.gui.extjtree.ExtJTree;
import de.wdilab.coma.gui.view.MatchresultView2;
import de.wdilab.coma.gui.view.MatchresultView3;
import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.insert.instance.InstanceCSVParser;
import de.wdilab.coma.insert.instance.InstanceODBCParser;
import de.wdilab.coma.insert.instance.InstanceOWLParser_V3;
import de.wdilab.coma.insert.instance.InstanceXMLParser;
import de.wdilab.coma.insert.metadata.ListParser;
import de.wdilab.coma.insert.metadata.OWLParser_V3;
import de.wdilab.coma.insert.metadata.XDRParser;
import de.wdilab.coma.insert.metadata.XSDParser;
import de.wdilab.coma.insert.relationships.AccMatchResultParser;
import de.wdilab.coma.insert.relationships.MatchResultParser;
import de.wdilab.coma.insert.relationships.RDFAlignmentParser;
import de.wdilab.coma.matching.Resolution;
import de.wdilab.coma.matching.Workflow;
import de.wdilab.coma.matching.util.VariableGraph;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This class starts the GUI and controls the whole interacting with the database and is
 * responsible for the matching process
 *
 * @author Sabine Massmann
 */
public class Controller {
    //----------------------------------------------
    //	  STATIC FINAL
    //----------------------------------------------
    public static final boolean SIMPLE_GUI = false;
    public static final boolean WORKING_GUI = true;
    //----------------------------------------------
    public static  final boolean TEST_MODE = false;
    public static  final boolean CALCULATE_SCHEMA_SIM = false;
    // default options for configure strategy
    public static  final int DEFAULT_PREPROCESSING = Graph.PREP_SIMPLIFIED; // PREP_SIMPLIFIED PREP_REDUCED
    //	public static final String DEFAULT_STRATEGY = GUIConstants.STRAT_ALLCONTEXT;
//	public static  final int DEFAULT_ALLCONTEXT_MATCHER = Manager.MATCH_SYS_COMA_OPT;
    //	static final int DEFAULT_PATH_MATCHER = Manager.MATCH_SYS_REUSE;
//	public static  final int DEFAULT_FILTEREDCONTEXT_NODEMATCHER = Manager.MATCH_SYS_NODES;
//	public static  final int DEFAULT_FILTEREDCONTEXT_CONTEXTMATCHER = Manager.MATCH_SYS_CONTEXTS;
    public static  final int DEFAULT_FRAGMENT_IDENTIFICATION =
            Resolution.RES1_INNERNODES;
    //		Manager.FRAG_STRAT_SUBSCHEMA;
    public static  final String DEFAULT_FRAGMENTROOT_STRATEGY = "$NamePathS";
    public static  final String DEFAULT_FRAGMENT_STRATEGY = "$FragmentBasedW";
    //		Workflow.FRAGMENTBASED;
//		Manager.MATCH_STRAT_FILTCONT;
//	public static  final int DEFAULT_REUSE_MATCHER = Manager.MATCH_SYS_REUSE;
//	public static  final int DEFAULT_COMBINEDREUSE_MATCHER = Manager.MATCH_SYS_REUSE;
//	public static  final int DEFAULT_NODEMATCHER = Manager.MATCH_SYS_NAMETYPE;
    // Default Position for the window
    public static  final int DEFAULT_POSITION_X = 100, DEFAULT_POSITION_Y = 100;
    public static  final int WAIT = 500; // milliseconds
    public static  final int WAIT_SHORT = 50;
    //----------------------------------------------
    private MainWindow mainWindow;
    private GUIMatchResult guiMatchresult = null;
    private Manager manager = null;
    private ExecuteMatchingThread execute = null;
    private int preprocessing = DEFAULT_PREPROCESSING;
    private int view = MainWindow.VIEW_GRAPH;
    //	private String strategy = DEFAULT_STRATEGY, contextStrategy = GUIConstants.STRAT_ALLCONTEXT;
    int //	matcherAllContext = DEFAULT_ALLCONTEXT_MATCHER,
//			nodeMatcherFilteredContext = DEFAULT_FILTEREDCONTEXT_NODEMATCHER,
//			contextMatcherFilteredContext = DEFAULT_FILTEREDCONTEXT_CONTEXTMATCHER,
            fragmentIdentification = DEFAULT_FRAGMENT_IDENTIFICATION;
    String	fragmentRootStrategy = DEFAULT_FRAGMENTROOT_STRATEGY;
    String	fragmentStrategy = DEFAULT_FRAGMENT_STRATEGY;
    //			ReuseStrategy = DEFAULT_REUSE_MATCHER,
//			combinedReuseStrategy = DEFAULT_COMBINEDREUSE_MATCHER,
//			matcherNodes = DEFAULT_NODEMATCHER;
    Point position = new Point(Controller.DEFAULT_POSITION_X,
            Controller.DEFAULT_POSITION_Y);
    // Default directory
    File defaultDirectory = null;
    // Data access
    DataAccess accessor;
    DataImport importer;
    // Matching Step by Step
    StepByStepFragmentMatching stepFragmentMatching;
    // Thread for loading schema to the GUI
    LoadFromDBThread loadSchema = null;
    boolean updatedRepository = false; // since loading Schemas
    boolean loadNewSourceSchema = false, loadNewTargetSchema = false;
    HashMap<Object, Float> sourceRelationshipValues;
    HashMap<Object, Float> simValues;
    //	private StepByStepCombinedReuse stepCombinedReuse;
    Graph taxGraph = null;

    public String file_syn = Strings.FILE_SYN_DEFAULT;
    public String file_abb = Strings.FILE_ABB_DEFAULT;
//	Dlg_InstConf dlg_InstConf = null;


    public Controller(Manager manager, DataAccess accessor) {
//		setSystemProperties();
        this.manager = manager;
        this.accessor = accessor;
        importer = new DataImport();
        defaultDirectory = new File(GUIConstants.DOT);
    }

    /*
     * Constructor of Controller
     */
    public Controller() {
        Locale.setDefault(Locale.ENGLISH);
        setSystemProperties();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        manager = new Manager();
        sourceRelationshipValues = new HashMap<Object, Float>();
        manager.loadRepository();
//		manager.computeMatchresultStatistics();
        Connection connection = manager.getAccessor().getConnection();
        accessor = new DataAccess(connection);
        importer = new DataImport(connection);
        mainWindow = new MainWindow(this);
        setGUIMatchresult(new GUIMatchResult());
        defaultDirectory = new File(GUIConstants.DOT);
        File file = new File(Manager.DIR_TMP);
        String tmpFile = System.getProperty("tmpDir");
        if (tmpFile!=null){
            file=new File(tmpFile);
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++){
                String fileName = files[i].getName();
                if (fileName.indexOf('.') > -1
                        && (fileName.length() - fileName.indexOf('.')) == 14) {
                    // delete temporary match result files
//            		files[i].delete();
                } else if (
//            		 delete temporary files spicy uses 
                        fileName.equals("config2.properties")
                                || fileName.equals("config.properties")
                                || fileName.equals("script.xq")
                                || fileName.equals("matchresultTaskFile.xml")
//            		|| fileName.startsWith(Spicy.TRANSFORMED_DATA)
                        ) {
                    files[i].delete();
                }
            }
        }
        String synFile = System.getProperty("file_syn");
        if (synFile!=null){
            file_syn=synFile;
        }
        String abbFile = System.getProperty("file_abb");
        if (abbFile!=null){
            file_abb=abbFile;
        }
    }



    //
    public static void setSystemProperties(){
        try {
            // read in property file
            java.io.FileInputStream propFile = new java.io.FileInputStream(
                    Strings.PROPERTY_FILE);

            java.util.Properties p = new java.util.Properties(System.getProperties());
            p.load(propFile);


            // writing like this ...
            //java.util.Properties p = new
            // java.util.Properties(System.getProperties());
            //p.store(new java.io.FileOutputStream(GUIConstants.PROPERTY_FILE),
            // "COMA++ Java-Property-File");
            System.setProperties(p);
            // now anywhere use String propertyName =
            // System.getProperty("propertyName");
        } catch (java.io.FileNotFoundException fnfe) {
            //fnfe.printStackTrace(System.err);
            System.out.println("Property file " + Strings.PROPERTY_FILE
                    + " not found.");
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }

    static public ImageIcon getImageIcon(String _icon){
//		URL url;
//		try {
//			url = new URL (".//" +_icon);
//			if (url!=null){
//				return new ImageIcon(url);
//			} 
        return new ImageIcon(".//" +_icon);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		return null;
    }

    public void start() {
        if (mainWindow !=null){
            mainWindow.run();
        }
    }

//	void importUserMatcher(){
//		ImportUserMatcher importMatcher = new ImportUserMatcher();
//		importMatcher.importUserMatcher(this);	
//	}

    /*
     * set Matchresult to the given matchresult and the given name
     */
    private void setGUIMatchresult(GUIMatchResult _matchresult) {
        guiMatchresult = _matchresult;
        mainWindow.setTitle(GUIConstants.ABOUT1);
    }

//	/*
//	 * calculate the similarity of two given Graphs
//	 */
//	public static MatchResult calculateSchemaSimilarity(Graph _schema1,
//			Graph _schema2, Manager _manager) {
//		SourceRelationship sr = LoadFromDBThread.getInternalSimDBMatchresult(
//				_manager, _schema1.getSource(), _schema2.getSource());
//		if (sr != null) {
//			System.out.println(_schema1.getSource().getLabel()
//					+ GUIConstants.DASH_SPACE + _schema2.getSource().getLabel()
//					+ GUIConstants.COLON_SPACE + sr.getEvidence());
//			return null;
//		}
//		//		long start = System.currentTimeMillis();
//		ArrayList aNodes = new ArrayList();
//		ArrayList bNodes = new ArrayList();
//		aNodes.addAll(_schema1.getVertexSet());
//		bNodes.addAll(_schema2.getVertexSet());
//		MatcherConfig matcherConfig = _manager
//				.getMatcherConfig(Manager.MATCH_SYS_NODES);
//		_manager.prepareMatch(_schema1, _schema2);
//		GenericMatcher matcher = _manager.getMatcher(matcherConfig, true);
//		int constMatcherId = Manager.MATCH_SYS_NAME;
//		float[][] simMatrix = matcher
//				.computeConstituentSimilarityMatrixComplete_1(constMatcherId,
//						aNodes, bNodes);
//		int strategy = Combination.SET_AVERAGE;
//		float evidence = Combination.computeSetSimilarity(simMatrix,
//				strategy);
//		//		long end = System.currentTimeMillis();
//		MatchResult mr = new MatchResultArray(aNodes, bNodes, simMatrix);
//		mr.setGraphs(_schema1, _schema2);
//		mr.setName(GUIConstants.INTERNAL_SIM + GUIConstants.UNDERSCORE
//				+ _schema1.getSource() + GUIConstants.UNDERSCORE
//				+ _schema2.getSource());
//		mr.setMatchInfo(GUIConstants.INTERNAL_SIM);
//		mr.setEvidence(GUIConstants.EMPTY + evidence);
//		//		saveMatchresultToDB(mr);
//		System.out.println(_schema1.getSource().getLabel() + " - "
//				+ _schema2.getSource().getLabel()
//				+ "controller.calculateSchemaSimilarity(...): done ");
//		//		System.out.println(s1.getSource().getName() + " - "
//		//				+ s2.getSource().getName() + ": " + result);
//		//		System.out.println(":"+ result);
//		//		System.out
//		//				.println("controller.calculateSchemaSimilarity(...): done "
//		//						+ (float) (end - start) / 1000 + " s");
//		return mr;
//	}

    /*
     * close the given schema (source or target) if there are temporary matchresults
     * ask again before deleting them
     */
    public void closeSchema(boolean _source, boolean _ask) {
        ManagementPane pane = getManagementPane();
        if (_source) {
            if (!guiMatchresult.containsSource()) {
                setStatus(GUIConstants.NO_SCHEMA);
                return;
            }
            if (pane.countAllWorkMatchresults() > 0) {
                int res;
                if (_ask) {
                    res = JOptionPane.showConfirmDialog(mainWindow,
                            GUIConstants.QUESTION_CLOSE_SRC, GUIConstants.WARNING,
                            JOptionPane.OK_CANCEL_OPTION);
                } else {
                    res = JOptionPane.OK_OPTION;
                }
                if (res != JOptionPane.OK_OPTION) {
                    setStatus(GUIConstants.CLOSE_SRC_CANCELED);
                } else {
                    guiMatchresult.closeSource();
                    setNewMatchResult(null);
                    getMatchresultView().setSourceSchema(null);
                    setStatus(GUIConstants.CLOSE_SRC_M_DONE);
                }
            } else {
                guiMatchresult.closeSource();
                getMatchresultView().setSourceSchema(null);
                setStatus(GUIConstants.CLOSE_SRC_DONE);
            }
        } else {
            if (!guiMatchresult.containsTarget()) {
                setStatus(GUIConstants.NO_SCHEMA);
                return;
            }
            if (pane.countAllWorkMatchresults() > 0) {
                int res;
                if (_ask) {
                    res = JOptionPane.showConfirmDialog(mainWindow,
                            GUIConstants.QUESTION_CLOSE_TRG, GUIConstants.WARNING,
                            JOptionPane.OK_CANCEL_OPTION);
                } else {
                    res = JOptionPane.OK_OPTION;
                }
                if (res != JOptionPane.OK_OPTION) {
                    setStatus(GUIConstants.CLOSE_TRG_CANCELED);
                } else {
                    guiMatchresult.closeTarget();
                    setNewMatchResult(null);
                    getMatchresultView().setTargetSchema(null);
                    setStatus(GUIConstants.CLOSE_TRG_M_DONE);
                }
            } else {
                guiMatchresult.closeTarget();
                getMatchresultView().setTargetSchema(null);
                setStatus(GUIConstants.CLOSE_TRG_DONE);
            }
        }
    }

    /*
     * open a dialog for choosing a schema from it, load the chosen source
     * schema from the data base
     */
    public void loadSourceSchema() {
        //		if (debug) System.out.println(manager.getAllSources());
        ArrayList list = LoadFromDBThread.getAllSchemas(this);
        if (list == null) {
            setStatus(GUIConstants.NO_SCHEMA_IN_DB);
            return;
        }
        setStatus(GUIConstants.LOAD_SRC);
        Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                GUIConstants.LOAD_SRC, list, MainWindow.DIM_SMALL,
                Dlg_ChooseFromList.SCHEMA, ListSelectionModel.SINGLE_SELECTION);
        dialog.setLocation(position);
        Source source = (Source) dialog.showDialog();
        if (source != null) {
            loadSourceSchema(source);
        } else {
            setStatus(GUIConstants.NO_SRC_SCHEMA);
        }
    }

    /*
     * load the source schema given with his name from the data base
     */
    public void loadSourceSchema(Source _source) {
        if (_source != null) {
            if (loadSchema == null) {
                loadSchema = new LoadFromDBThread(mainWindow, this,
                        LoadFromDBThread.STATE_LOAD_SRC_SCHEMA, _source);
            } else {
                while (loadSchema.isAlive()) {
                    try {
                        Thread.sleep(WAIT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                loadSchema = new LoadFromDBThread(mainWindow, this,
                        LoadFromDBThread.STATE_LOAD_SRC_SCHEMA, _source);
            }
            loadSchema.start();
            loadNewSourceSchema = true;
            if (loadNewTargetSchema) {
                updatedRepository = false;
            }
        } else {
            setStatus(GUIConstants.NO_SRC_SCHEMA);
        }
    }

    /*
     * load the middle schema given with his name from the data base
     */
    public void loadMiddleSchema(Source _source) {
        if (_source != null) {
            if (loadSchema == null) {
                loadSchema = new LoadFromDBThread(mainWindow, this,
                        LoadFromDBThread.STATE_LOAD_MIDDLE_SCHEMA, _source);
            } else {
                while (loadSchema.isAlive()) {
                    try {
                        Thread.sleep(WAIT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                loadSchema = new LoadFromDBThread(mainWindow, this,
                        LoadFromDBThread.STATE_LOAD_MIDDLE_SCHEMA, _source);
            }
            loadSchema.start();
        } else {
            ((MatchresultView3) getMatchresultView()).setMiddleSchema(null);
            setStatus(GUIConstants.NO_MIDDLE_SCHEMA);
        }
    }

    public void swap() {
        if (!guiMatchresult.containsSource() && !guiMatchresult.containsTarget()) {
            setStatus(GUIConstants.SWAP_NOTHING);
            return;
        }
        getMatchresultView().swap();
        getManagementPane().swap();
        setNewMatchResult(getManagementPane().getSelectedWorkMatchresult());
        setStatus(GUIConstants.SWAP_DONE);
    }

    Graph loadSourceGraph(Graph _Graph, boolean _createUniqueName) {
        boolean changePreprocessing = true;
        return loadSourceGraph(_Graph, _createUniqueName, changePreprocessing);
    }
    /*
     * set the given Graph as source schema for the matchresult update the
     * matchresult view with the source (depending on selected preprocessing)
     */
    Graph loadSourceGraph(Graph _Graph, boolean _createUniqueName, boolean changePreprocessing) {
        if (_Graph == null) {
            return null;
        }
//		stopEdit();
        if (!mainWindow.getNewContentPane().is2SplitPane()
                && !mainWindow.getNewContentPane().isChangeTo3SplitPane()) {
            getManagementPane().setSplitPaneButtonEnabled(false);
        }
        guiMatchresult.setSourceGraph(_Graph);
        Graph currentPrep = _Graph.getGraph(preprocessing);
        if (currentPrep==null){
            int currentP = preprocessing -1;
            String text = "preprocessing " + Graph.preprocessingToString(preprocessing) + " not supported. Therefore use of " + Graph.preprocessingToString(currentP);
            setStatus(text);
            System.out.println(text);
            currentPrep = _Graph.getGraph(currentP);
            setPreprocessing(currentP);
        }
//		if (changePreprocessing && Source.SRC_CONT_ONTOLOGY.equals(currentPrep.getSource().getType())) {
        if (changePreprocessing && Source.TYPE_ONTOLOGY==currentPrep.getSource().getType()) {
            currentPrep = _Graph.getGraph(Graph.PREP_DEFAULT_ONTOLOGY);
            setPreprocessing(Graph.PREP_DEFAULT_ONTOLOGY);
        }
        getMatchresultView().setSourceSchema(currentPrep);
        getManagementPane().addSchemaToWorkspace(currentPrep, true,
                _createUniqueName);
        return currentPrep;
    }

    /*
     * set the given Graph as middle schema for the matchresult update the
     * matchresult view with the source (depending on selected preprocessing)
     */
    void loadMiddleGraph(Graph _Graph) {
        if (_Graph == null) {
            return;
        }
        if ((getMatchresultView() != null)
                && (getMatchresultView() instanceof MatchresultView3)) {
            //			matchresult.setOrgMiddleSchema(_Graph);
            Graph currentPrep = _Graph.getGraph(preprocessing);
            if (currentPrep==null){
                String problem = "Error with the preprocessing of the middle schema!";
                System.out.println(problem);
                setStatus(problem);
            }
//			if (currentPrep==null){
//				String text = "preprocessing " + Graph.stateToString(preprocessing) + " not supported. Therefore use of " + Graph.stateToString(preprocessing-1);
//				 setStatus(text);
//				 System.out.println(text);
//				 currentPrep = _Graph.getGraph(preprocessing-1);
//				 setPreprocessing(preprocessing-1);
//			}
//			if (Source.SRC_CONT_ONTOLOGY.equals(currentPrep.getSource().getType())) {
//				currentPrep = _Graph.getGraph(Graph.GRAPH_STATE_DEFAULT_ONTOLOGY);
//				setPreprocessing(Graph.GRAPH_STATE_DEFAULT_ONTOLOGY);
//			}
            ((MatchresultView3) getMatchresultView()).setMiddleSchema(currentPrep);
        }
    }

    /*
     * unfold the given tree (=expand all)
     */
    public void unfold(ExtJTree _tree) {
        _tree.expandAll();
        getMatchresultView().setChanged(true);
    }

    /*
     * unfold the selected fragment in the given tree
     */
    public void unfoldFragment(ExtJTree _tree) {
        TreePath path = _tree.getSelectionPath();
        if (path!=null){
            _tree.expandAll(path);
            getMatchresultView().setChanged(true);
        }
    }

    /*
     * unfold the selected fragment in the given tree
     */
    public void foldFragmentChildren(ExtJTree _tree) {
        TreePath path = _tree.getSelectionPath();
        if (path!=null){
//			_tree.collapsePath(path);
            int pathCountParent = path.getPathCount() +1 ;
            Enumeration<TreePath> descendants = _tree.getExpandedDescendants(path);
            for (;descendants.hasMoreElements();) {
                TreePath descendant = descendants.nextElement();
                if (descendant.equals(path)){
                    continue;
                }
                int pathCount = descendant.getPathCount();
                if (pathCount>pathCountParent){
                    continue;
                }
                _tree.collapsePath(descendant);
            }
            getMatchresultView().setChanged(true);
        }
    }

    /*
     * fold the given tree (=only from the second level)
     */
    public void fold(ExtJTree _tree) {
        int rows = _tree.getRowCount();
        if (rows > 0) {
            for (int i = 0; i < rows; i++) {
                _tree.collapseRow(i);
            }
        }
        getMatchresultView().setChanged(true);
    }

    /*
     * open a dialog for choosing a schema from it, load the chosen target
     * schema from the data base
     */
    public void loadTargetSchema() {
        ArrayList list = LoadFromDBThread.getAllSchemas(this);
        if (list == null) {
            setStatus(GUIConstants.NO_SCHEMA_IN_DB);
            return;
        }
        setStatus(GUIConstants.LOAD_TRG);
        Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                GUIConstants.LOAD_TRG, list, MainWindow.DIM_SMALL,
                Dlg_ChooseFromList.SCHEMA, ListSelectionModel.SINGLE_SELECTION);
        dialog.setLocation(position);
        Source source = (Source) dialog.showDialog();
        if (source != null) {
            loadTargetSchema(source);
        } else {
            setStatus(GUIConstants.NO_TRG_SCHEMA);
        }
    }

    /*
     * load the source schema given with his name from the data base
     */
    public void loadTargetSchema(Source _source) {
        if (_source != null) {
            if (loadSchema == null) {
                loadSchema = new LoadFromDBThread(mainWindow, this,
                        LoadFromDBThread.STATE_LOAD_TRG_SCHEMA, _source);
            } else {
                while (loadSchema.isAlive()) {
                    try {
                        Thread.sleep(WAIT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                loadSchema = new LoadFromDBThread(mainWindow, this,
                        LoadFromDBThread.STATE_LOAD_TRG_SCHEMA, _source);
            }
            loadSchema.start();
            loadNewTargetSchema = true;
            if (loadNewSourceSchema) {
                updatedRepository = false;
            }
        } else {
            setStatus(GUIConstants.NO_TRG_SCHEMA);
        }
    }

    void loadSourceSchema(Graph _Graph) {
        loadSourceGraph(_Graph, false);
        setStatus(GUIConstants.SRC_SCHEMA_LOADED);
        guiMatchresult.setMatchResult(null);
    }

    void loadTargetSchema(Graph _Graph) {
        loadTargetGraph(_Graph, false);
        setStatus(GUIConstants.TRG_SCHEMA_LOADED);
        guiMatchresult.setMatchResult(null);
    }


    Graph loadTargetGraph(Graph _Graph, boolean _createUniqueName) {
        boolean changePreprocessing = true;
        return loadTargetGraph(_Graph, _createUniqueName, changePreprocessing);
    }

    /*
     * set the given Graph as target schema for the matchresult update the
     * matchresult view with the source (depending on selected preprocessing)
     */
    Graph loadTargetGraph(Graph _Graph, boolean _createUniqueName, boolean changePreprocessing) {
        if (_Graph == null) {
            return null;
        }
//		stopEdit();
        guiMatchresult.setTargetGraph(_Graph);
        Graph currentPrep = _Graph.getGraph(preprocessing);
        if (currentPrep==null){
            String text = "preprocessing " + Graph.preprocessingToString(preprocessing) + " not supported. Therefore use of " + Graph.preprocessingToString(preprocessing-1);
            setStatus(text);
            System.out.println(text);
            currentPrep = _Graph.getGraph(preprocessing-1);
            setPreprocessing(preprocessing-1);
        }
        if (changePreprocessing && Source.TYPE_ONTOLOGY==currentPrep.getSource().getType()) {
            currentPrep = _Graph.getGraph(Graph.PREP_DEFAULT_ONTOLOGY);
            setPreprocessing(Graph.PREP_DEFAULT_ONTOLOGY);
        }
        if (!mainWindow.getNewContentPane().is2SplitPane()
                && !mainWindow.getNewContentPane().isChangeTo3SplitPane()) {
            getManagementPane().setSplitPaneButtonEnabled(false);
        }
        getMatchresultView().setTargetSchema(currentPrep);
        getManagementPane().addSchemaToWorkspace(currentPrep, true,
                _createUniqueName);
        return currentPrep;
    }

    /*
     * save a new schema into the data base
     */
    public void importSchemaInDB() {
        setStatus(GUIConstants.SAVE_SCHEMA_DB);
        SaveToDBThread saveSchema = new SaveToDBThread(mainWindow, this,
                SaveToDBThread.STATE_IMPORT_FILE);
        saveSchema.start();
    }

    /*
     * parse instances from an ontology file and import them into the repository
     */
    public String parseInstancesOrgFile(Object data, boolean showResult) {
        if (data==null){
            String titel = GUIConstants.CHOOSE_SCHEMA_PARSE_INST;
            ArrayList schemas = LoadFromDBThread.getAllSchemas(this);
            if (schemas==null){
                setStatus("Error: There are no ontologies or csv schemas or sql schemas in the repository.");
                return null;
            }
            ArrayList selected = new ArrayList();
            for (int i = 0; i < schemas.size(); i++) {
                Source source = (Source) schemas.get(i);
                if ((source.getType()==Source.TYPE_ONTOLOGY
                        || source.getType()==Source.TYPE_CSV
                        || source.getType()==Source.TYPE_SQL)
//						&& !accessor.existInstancesTable(source)
                        ){
                    selected.add(source);
                }
            }
            if (selected.isEmpty()){
                setStatus("Error: There are no ontologies or csv schemas or sql schemas (without instances) in the repository.");
                return null;
            }
            Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                    titel, selected,
                    MainWindow.DIM_SMALL, Dlg_ChooseFromList.DELETE_SCHEMA_DB,
                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            dialog.setLocation(position);
            data = dialog.showDialog();
            if (!dialog.isOk()){
                return null;
            }
        }
        String info = null;
        if (data instanceof ArrayList){
            info = loadInstances((ArrayList) data, false);
        } else if (data instanceof Source){
            ArrayList list = new ArrayList();
            list.add(data);
            info = loadInstances(list, false);
        } else {
            JOptionPane.showMessageDialog(mainWindow,GUIConstants.NO_SCHEMA_SEL,
                    GUIConstants.ERROR,JOptionPane.ERROR_MESSAGE);
            return "";
        }
        boolean sourceReloaded = false, targetReloaded = false;
        if (guiMatchresult!=null && guiMatchresult.getSourceGraph()!=null){
            if (data instanceof ArrayList && ((ArrayList)data).contains(guiMatchresult.getSourceGraph().getSource())){
                sourceReloaded = true;
            } else if (data instanceof Source && data.equals(guiMatchresult.getSourceGraph().getSource())) {
                sourceReloaded = true;
            }
            if (sourceReloaded){
                getMatchresultView().setSourceSchema(guiMatchresult.getSourceGraph());
            }
        }
        if (guiMatchresult!=null && guiMatchresult.getTargetGraph()!=null){
            if (data instanceof ArrayList && ((ArrayList)data).contains(guiMatchresult.getTargetGraph().getSource())){
                targetReloaded = true;
            } else if (data instanceof Source && data.equals(guiMatchresult.getTargetGraph().getSource())) {
                targetReloaded = true;
            }
            if (targetReloaded){
                getMatchresultView().setTargetSchema(guiMatchresult.getTargetGraph());
            }
        }
        if (guiMatchresult!=null && guiMatchresult.getMatchResult()!=null && (sourceReloaded || targetReloaded)){
            mainWindow.getNewContentPane().setNewMatchResult(guiMatchresult.getMatchResult());
        }
        if (guiMatchresult!=null && showResult && info!=null){
            TextFrame wnd = new TextFrame(GUIConstants.IMPORT_STAT + info, GUIConstants.INFORMATION,MainWindow.DIM_INFO);
            wnd.setLocation(position);
            wnd.setVisible(true);
        }
        return info;
    }

    /*
     * parse instances from an ontology file and import them into the repository
     */
    public String parseInstancesAddFile(ArrayList data, boolean showResult) {
        if (data==null) {
            ArrayList schemas = LoadFromDBThread.getAllSchemas(this);
            if (schemas==null){
                setStatus("Error: There are no xsd or csv-header schemas in the repository.");
                return null;
            }
            ArrayList xsdcsv = new ArrayList();
            for (int i = 0; i < schemas.size(); i++) {
                Source source = (Source) schemas.get(i);
                if ((source.getType()==Source.TYPE_XSD
                        || source.getType()==Source.TYPE_CSV)  // for separate CSV schema  instance files
//						&& !accessor.existInstancesTable(source)
                        ){
                    xsdcsv.add(source);
                }
            }
            if (xsdcsv.isEmpty()){
                setStatus("Error: There are no xsd or csv-header schemas (without instances) in the repository.");
                return null;
            }
            Dlg_InstanceFiles dialog = new Dlg_InstanceFiles(mainWindow,
                    this, xsdcsv);
            dialog.setLocation(position);
            data = dialog.showDialog();
        }
        if (data!=null){
            Source source = (Source) data.get(0);
            String instanceFile = (String) data.get(1);
            // schema has to be loaded first
            Graph graph = manager.loadGraph(source, true, false);
            for (int i = 1; i < data.size(); i++) {
                parseInstances(graph, instanceFile,(i==1 ? true : false));
                graph.printGraphInfo();
            }
            int instanceCnt = Math.round(graph.getElementCount()*graph.getAverageInstanceCnt()*graph.getHasInstances());
            String info = "\n"+graph.getSource().getName() + ": \t\t"
                    + Math.round(100 * graph.getHasInstances()) + "% , \t\t" + Math.round(graph.getAverageInstanceCnt()) + ", \t\t" + instanceCnt;
            if (showResult){
                TextFrame wnd = new TextFrame(GUIConstants.IMPORT_STAT + info, GUIConstants.INFORMATION,MainWindow.DIM_INFO);
                wnd.setLocation(position);
                wnd.setVisible(true);
            }
            return info.toString();
        }
        return null;
    }

    public void parseInstances(Graph schemaGraph, String instanceFile, boolean deleteOldInstances) {
        if (schemaGraph==null || schemaGraph.getSource()==null || instanceFile==null){
            return;
        }
        Source source = schemaGraph.getSource();
        beforeParseInstances(deleteOldInstances, schemaGraph);
        if (Source.TYPE_XSD==source.getType() || Source.TYPE_XDR==source.getType()) {
            InstanceXMLParser parser = new InstanceXMLParser(importer);
//			String file = schemaGraph.getSource().getProvider();
//			file = file.replace(".xsd", ".xml");
            schemaGraph = parser.parseInstances(schemaGraph, instanceFile);
            schemaGraph = afterParseInstances(schemaGraph);
        }
        // This would be the necessary code for choosing a separate instance file
        // for CSV schemas instead of using the file in the 'provider' field.
        //if (Source.SRC_CONT_CSV.equals(source.content)) {
        //	InstanceCSVParser parser = new InstanceCSVParser(importer);
        //	schemaGraph = parser.parseInstances(schemaGraph, instanceFile);
        //	schemaGraph = afterParseInstances(schemaGraph);
        //}
    }

    public void parseInstances(Graph schemaGraph, boolean deleteOldInstances) {
        if (schemaGraph==null || schemaGraph.getSource()==null){
            return;
        }
        Source source = schemaGraph.getSource();
        int sourceId = source.getId();
        beforeParseInstances(deleteOldInstances, schemaGraph);
        if (Source.TYPE_ONTOLOGY==source.getType()) {
            // owl - Instances are saved with their element.accession
            InstanceOWLParser_V3 parser = new InstanceOWLParser_V3(importer);
            schemaGraph = parser.parseInstances(source, schemaGraph.getGraph(Graph.PREP_DEFAULT_ONTOLOGY),null);
            if (importer.isInstancesTableEmpty(sourceId)){
                importer.deleteInstances(sourceId);
            }
            accessor.loadAndPropagateInstances(true, schemaGraph);
        } else if (Source.TYPE_CSV == source.getType()) {
            // csv - Instances are saved with their element.accession
            InstanceCSVParser parser = new InstanceCSVParser(importer);
            parser.parseInstances(schemaGraph.getGraph(Graph.PREP_RESOLVED));
            if (importer.isInstancesTableEmpty(sourceId)){
                importer.deleteInstances(sourceId);
            }
            accessor.loadAndPropagateInstances(true, schemaGraph);
        }
    }

    public void parseInstances(Graph schemaGraph, boolean deleteOldInstances,
                               String odbcEntry, String userName, String userPass) {
        if (schemaGraph==null || schemaGraph.getSource()==null || odbcEntry==null){
            return;
        }
        Source source = schemaGraph.getSource();
        beforeParseInstances(deleteOldInstances, schemaGraph);
        if (Source.TYPE_ODBC==source.getType()) {
            InstanceODBCParser parser = new InstanceODBCParser(importer, odbcEntry, userName, userPass);
            parser.parseInstancesForSchema(schemaGraph);
            schemaGraph = afterParseInstances(schemaGraph);
        }
    }

    private Graph afterParseInstances(Graph graph){
        int sourceId = graph.getSource().getId();
        if (importer.isInstancesTableEmpty(sourceId)){
            importer.deleteInstances(sourceId);
        } else {
            graph = accessor.propagateInstancesToParents(graph);
        }
        return graph;
    }

    private void beforeParseInstances( boolean deleteOldInstances, Graph graph){
        int sourceId = graph.getSource().getId();
        if (deleteOldInstances){
            //      // create instance table for database
            importer.deleteInstances(sourceId);
            importer.createInstancesTable(sourceId);
            // delete old instances in schema graph
            graph.clearInstances();
        } else if (!accessor.existInstancesTable(graph.getSource().getId())){
            importer.createInstancesTable(sourceId);
        }
    }


    /*
     * parse instances from an ontology file and import them into the repository
     */
    public String parseInstancesBatch(ArrayList data, boolean showResult) {
        if (data==null) {
            ArrayList schemas = LoadFromDBThread.getAllSchemas(this);
            if (schemas==null){
                setStatus("Error: There are no xsd or csv-header schemas in the repository.");
                return null;
            }
            ArrayList xsd = new ArrayList();
            for (int i = 0; i < schemas.size(); i++) {
                Source source = (Source) schemas.get(i);
                if (source.getType()==Source.TYPE_XSD){
                    xsd.add(source);
                }
            }
            if (xsd.isEmpty()){
                setStatus("Error: There are no xsd  schemas (without instances) in the repository.");
                return null;
            }
            String titel = GUIConstants.CHOOSE_SCHEMA_PARSE_INST;
            Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                    titel, xsd,
                    MainWindow.DIM_SMALL, Dlg_ChooseFromList.DELETE_SCHEMA_DB,
                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            dialog.setLocation(position);
            Object dataTmp = dialog.showDialog();
            if (dataTmp instanceof ArrayList){
                data = (ArrayList) dataTmp;
            } else if (dataTmp instanceof Source){
                data = new ArrayList();
                data.add(dataTmp);
            } else {
                JOptionPane.showMessageDialog(mainWindow,GUIConstants.NO_SCHEMA_SEL,
                        GUIConstants.ERROR,JOptionPane.ERROR_MESSAGE);
                return "";
            }
            if (!dialog.isOk()){
                return null;
            }
        }
//		if (data!=null){
//			String info = GUIConstants.EMPTY;
//			for (int i = 0; i < data.size(); i++) {
//				Source source = (Source) data.get(i);				
//				String instanceFile = source.getProvider().toLowerCase();
//				instanceFile = instanceFile.replace(".xsd", ".xml");
//				// schema has to be loaded first
//				Graph graph = manager.loadGraph(source, true, false);
//				manager.parseInstances(graph, instanceFile, true);
////				graph.printGraphInfo();
//				int instanceCnt = Math.round(graph.getVerticesCount()*graph.getAverageInstanceCnt()*graph.getHasInstances());
//				info += "\n"+graph.getSource().getLabel() + ": \t\t" 
//					+Math.round(100*graph.getHasInstances())+ "% , \t\t"
//					+Math.round(graph.getAverageInstanceCnt()) + ", \t\t" + instanceCnt;
//			}
//			if (showResult){
//				TextFrame wnd = new TextFrame(GUIConstants.IMPORT_STAT + info, GUIConstants.INFORMATION,MainWindow.DIM_INFO);
//				wnd.setLocation(position);
//				wnd.setVisible(true);
//			}
//			return info.toString();
//		}
        return null;
    }

    private String loadInstances(ArrayList schemas, boolean showResult){
//		ArrayList loadedSchemas = new ArrayList();
        if (mainWindow!=null){
            mainWindow.getNewContentPane().setProgressBar(true);
//			loadedSchemas = getManagementPane().getAllWorkSchemas();
        }
        HashMap<Integer, Graph> loadedGraphs = manager.getLoadedGraphs();
        StringBuffer info = new StringBuffer();
        for (int i = 0; i < schemas.size(); i++) {
            Source source = (Source) schemas.get(i);
            Graph loaded = loadedGraphs.get(new Integer(source.getId()));
//			for (int j = 0; j < loadedSchemas.size(); j++) {
//				Graph graph = (Graph) loadedSchemas.get(j);
//				if (graph.getSource().equals(source)){
//					loaded=graph;
//					break;
//				}
//			}
            Graph graph=null;
            if (loaded!=null){
                // schema already loaded to Workspace
                graph = loaded;
            } else {
                // schema has to be loaded first
                graph = manager.loadGraph(source, true, false);

            }
            boolean deleteOldInstances = true; // delete previous instances
            parseInstances(graph, deleteOldInstances);
            int instanceCnt = Math.round(graph.getElementCount()*graph.getAverageInstanceCnt()*graph.getHasInstances());
            if (instanceCnt>0){
                info.append("\n"+graph.getSource().getName() + ": \t"
                        + Math.round(100 * graph.getHasInstances()) + "% , \t\t\t" + Math.round(graph.getAverageInstanceCnt()) + ", \t\t" + instanceCnt);
            }
        }
        if (mainWindow!=null){
            mainWindow.getNewContentPane().setProgressBar(false);
            if (showResult){
                JOptionPane.showMessageDialog(mainWindow,GUIConstants.IMPORT_STAT + info.toString(),
                        GUIConstants.INFORMATION,JOptionPane.PLAIN_MESSAGE);
            }
        }
        return info.toString();
    }

    public void showInstances(Source _source, Element _element){
        TextFrame wnd = new TextFrame(_source, _element,
                GUIConstants.INFO_INST, MainWindow.DIM_LARGE);
        wnd.setLocation(position);
        wnd.setVisible(true);
    }


    /**
     * read in & save a new ontolgy into the data base
     *
     */
    public void importOWLInDB() {
        setStatus(GUIConstants.SAVE_SCHEMA_DB);
        SaveToDBThread saveSchema = new SaveToDBThread(mainWindow, this,
                SaveToDBThread.STATE_IMPORT_OWL_URI);
        saveSchema.start();
    }

    /**
     *
     * export the current schema to file (ascii tree representation)
     *
     * @author david
     */
    public void exportToFile() {
        String textTree = null;
        ArrayList list = LoadFromDBThread.getAllSchemas(this);
        if (list == null) {
            setStatus(GUIConstants.NO_SCHEMA_IN_DB);
            return;
        }
        setStatus(GUIConstants.SCHEMA_EXPORT);
        String titel = GUIConstants.SCHEMA_EXPORT;
        Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                titel, list, MainWindow.DIM_SMALL, Dlg_ChooseFromList.SCHEMA,
                ListSelectionModel.SINGLE_SELECTION);
        dialog.setLocation(position);
        Source source = (Source) dialog.showDialog();
        //loadSourceSchema(schemaName.toString());
        if (source != null) {
            if (loadSchema == null) {
                loadSchema = new LoadFromDBThread(mainWindow, this,
                        LoadFromDBThread.STATE_EXPORT_FILE, source);
            } else {
                while (loadSchema.isAlive()) {
                    // nothing
                    //	;
                    try {
                        Thread.sleep(WAIT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                loadSchema = new LoadFromDBThread(mainWindow, this,
                        LoadFromDBThread.STATE_EXPORT_FILE, source);
            }
            loadSchema.start();
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(defaultDirectory);
                int returnVal = chooser.showSaveDialog(mainWindow);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    defaultDirectory = chooser.getCurrentDirectory();
                    String _name = chooser.getSelectedFile().toString();
                    if (_name != null) {
                        try {
                            PrintStream out = new PrintStream(
                                    new FileOutputStream(_name, true), true);
                            //loadSchema. noch active?
                            while (loadSchema.isAlive()) {
                                // nothing
                                //System.out.print(".");
                                try {
                                    Thread.sleep(WAIT);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            textTree = loadSchema.getTextTree();
                            out.print(textTree);
                            out.close();
                        } catch (FileNotFoundException e) {
                            //
                        }
                        setStatus(GUIConstants.FILE_SAVED + _name);
                    } else
                        setStatus(GUIConstants.CANCELED);
                } else
                    setStatus(GUIConstants.CANCELED);
            } catch (Exception x) {
                setStatus(x.getMessage());
                setStatus(GUIConstants.MATCHRESULT_SAVED_FILE_ERROR);
                x.printStackTrace();
            }
        }
    }

    // not used: taxonomy
//	/**
//	 * choose a schema in DB for calculating distanceSimilarity (as
//	 * taxonomyMatch) upon <br/>and further store resulting MatchResult in DB
//	 * 
//	 * @author david
//	 */
//	public void selectTaxonomy() {
//		ArrayList list = LoadFromDBThread.getAllSchemas(this);
//		if (list == null) {
//			setStatus(GUIConstants.NO_SCHEMA_IN_DB);
//			return;
//		}
//		setStatus(GUIConstants.TAXONOMY_CHOOSE);
//		Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
//				GUIConstants.TAXONOMY_CHOOSE, list, MainWindow.DIM_SMALL,
//				Dlg_ChooseFromList.SCHEMA, ListSelectionModel.SINGLE_SELECTION);
//		dialog.setLocation(position);
//		Source source = (Source) dialog.showDialog();
//		//loadSourceSchema(schemaName.toString());
//		if (source != null) {
//			if (loadSchema == null) {
//				loadSchema = new LoadFromDBThread(mainWindow, this,
//						LoadFromDBThread.STATE_TAXONOMY_CHOOSE, source);
//			} else {
//				while (loadSchema.isAlive()) {
//					try {
//						Thread.sleep(WAIT);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				loadSchema = new LoadFromDBThread(mainWindow, this,
//						LoadFromDBThread.STATE_TAXONOMY_CHOOSE, source);
//			}
//			loadSchema.start();
//			// wait till distance sim map is calculated and display/show/view
//			// chosen taxonomy (tree)
//			while (loadSchema.isAlive()) {
//				// nothing
//				//	;
//				try {
//					Thread.sleep(WAIT);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			//now distanceSimMap should be ready in DB for taxMatch.
//			// now display it.
//			viewTaxonomy();
//		}
//	}
//
//	public void setTaxonomy(Graph _source) {
//		if (_source == null) {
//			return;
//		}
//		taxGraph = _source;
//	}
//	/**
//	 * choose a schema in DB for calculating distanceSimilarity (as
//	 * taxonomyMatch) upon <br/>and further store resulting MatchResult in DB
//	 * 
//	 * @author david
//	 */
//	public void setTaxonomy(Source _source) {
//		if (_source == null) {
//			return;
//		}
//		if (loadSchema == null) {
//			loadSchema = new LoadFromDBThread(mainWindow, this,
//					LoadFromDBThread.STATE_TAXONOMY_CHOOSE, _source);
//		} else {
//			while (loadSchema.isAlive()) {
//				try {
//					Thread.sleep(WAIT);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			loadSchema = new LoadFromDBThread(mainWindow, this,
//					LoadFromDBThread.STATE_TAXONOMY_CHOOSE, _source);
//		}
//		loadSchema.start();
//		// wait till distance sim map is calculated and display/show/view
//		// chosen taxonomy (tree)
//		while (loadSchema.isAlive()) {
//			// nothing
//			// ;
//			try {
//				Thread.sleep(WAIT);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		// now distanceSimMap should be ready in DB for taxMatch.
//	}
//
//	/**
//	 * show schema as selected taxonomy
//	 * 
//	 * @author david
//	 */
//	public void viewTaxonomy() {
//		//SimpleJTree
//		//Graph taxGraph =
//		// getManager().getTaxonomyDistanceSim().getSourceGraph();
//		Graph taxGraph = getTaxonomyChosen();
//		if (taxGraph!=null){
//			String name = taxGraph.getSource().getName();
//			//String name = getTaxonomyNameChosen();
//			Dlg_ShowHierarchy dmh = new Dlg_ShowHierarchy(this, taxGraph,
//					"Taxonomy [" + name + "]");
//			//		dmh.init(); // not necessary... called in the constructor
//			dmh.showDlg(getDialogPosition(), MainWindow.DIM_MEDIUM2);
//		}
//	}
//
//	public void viewTaxonomy(Source _source) {
//		Graph taxGraph = manager.loadGraph(_source, true);
//		String name = taxGraph.getSource().getName();
//		//String name = getTaxonomyNameChosen();
//		Dlg_ShowHierarchy dmh = new Dlg_ShowHierarchy(this, taxGraph,
//				"Taxonomy [" + name + "]");
//		//		dmh.init(); // not necessary... called in the constructor
//		dmh.showDlg(getDialogPosition(), MainWindow.DIM_MEDIUM2);
//	}
//
//	public void viewPivotSchema(Source _source) {
//		Graph graph = manager.loadGraph(_source, true);
//		String name = graph.getSource().getName();
//		Dlg_ShowHierarchy dmh = new Dlg_ShowHierarchy(this, graph.getGraph(this
//				.getPreprocessing()), "Pivot Schema  [" + name + "]");
//		//		dmh.init(); // not necessary... called in the constructor
//		dmh.showDlg(getDialogPosition(), MainWindow.DIM_MEDIUM2);
//	}
//
//	public Graph getTaxonomyChosen() {
////		Graph taxGraph = null;
////		//		try {
////		//			taxGraph = getManager().getTaxonomyDistanceSim()
////		//					.getSourceGraph();
////		//			//name = taxGraph.getSource().getName();
////		//		} catch (NullPointerException e) {
////		//			System.err.println("No taxonomy chosen yet.");
////		//		}
//		return (taxGraph);
//	}

    /*
     * show information about the source and target schema depending on the
     * given kind (loaded, resolved, reduced, simplified)
     */
    public void showSchemaInformations() {
        if (!guiMatchresult.containsSource() && !guiMatchresult.containsTarget()) {
            setStatus(GUIConstants.NO_SCHEMAS);
            return;
        }
        setStatus(GUIConstants.SCHEMA_INFO);
        TextFrame wnd = new TextFrame(this, GUIConstants.SCHEMA_INFO, MainWindow.DIM_LARGE);
//		wnd.setSize(MainWindow.DIM_LARGE);
        wnd.setLocation(position);
        wnd.setVisible(true);
    }

    /*
     * start the "Step By Step"- Fragment Matching
     */
    public void stepByStepFragmentMatching() {
        setStatus(GUIConstants.STEPBYSTEP_FRAGMENTMATCHING);
        stepFragmentMatching = new StepByStepFragmentMatching(this);
        stepFragmentMatching.start();
    }

//	/*
//	 * start the "Step By Step"- Combined Reuse
//	 */
//	public void stepByStepCombinedReuse() {
//		setStatus(GUIConstants.STEPBYSTEP_COMBINEDREUSE);
//		stepCombinedReuse = new StepByStepCombinedReuse(this);
//		stepCombinedReuse.start();
//	}

//	/*
//	 * open a Dialog where the user can configure the choosen Strategy (COMA,
//	 * Graph, Fragment)
//	 */
//	public void configureStrategy() {
//		setStatus(GUIConstants.CONFIGURE_STRATEGY);
//		Dlg_ConfigureStrategy dialog = new Dlg_ConfigureStrategy(
//				mainWindow, this);
//		dialog.showDlg(position);
//	}

    //	public void configureInstanceStrategy() {
//		setStatus(GUIConstants.CONFIGURE_STRATEGY);
//		Graph source  = getMatchresult().getSourceGraph();
//		Graph target  = getMatchresult().getTargetGraph();
//		if (source==null || target==null){
//			setStatus(GUIConstants.NO_SCHEMAS);
//			return;
//		}
//		if (dlg_InstConf==null){
//			dlg_InstConf = new Dlg_InstConf(	mainWindow, this, source, target);
//		} else if (!dlg_InstConf.getSource().equals(source) || !dlg_InstConf.getTarget().equals(target)){
//			dlg_InstConf = new Dlg_InstConf(	mainWindow, this, source, target);
//		}
//		if (dlg_InstConf.hasBothAttributes()){
//			dlg_InstConf.showDlg(position);
//		}
//	}
//	
//	public void saveInstanceStrategyToUsermatcher(){
//		DataAccess access = new DataAccess();
//		Statement statement = access.getStatement();
//		String value = dlg_InstConf.getConfig();
//		String query = "update usermatcher set info=\""+value+"\" where id=300";
//		try {
//			statement.executeUpdate(query);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//	
//		// Dung Phan 17:36 16.06.2009
//	public void configureMachineLearningStrategy() {
//		setStatus(GUIConstants.CONFIGURE_STRATEGY);
//		Graph source  = getMatchresult().getSourceGraph();
//		Graph target  = getMatchresult().getTargetGraph();
//		if (source==null || target==null){
//			setStatus(GUIConstants.NO_SCHEMAS);
//			return;
//		}
//		if (mlDialog==null){
//			mlDialog = new Dlg_MachineLearningConf(	mainWindow, this, source, target);
//		} else if (!mlDialog.getSource().equals(source) || !mlDialog.getTarget().equals(target)){
//			mlDialog = new Dlg_MachineLearningConf(	mainWindow, this, source, target);
//		}
//		mlDialog.showDlg(position);
//	}
	/*
	 * current Matchresult shall be deleted
	 */
    public void deleteMatchresultTemp() {
        if (getManagementPane().getAllWorkMatchresults().size() > 0) {
            getManagementPane().removeSelectedWorkMatchresult();
            //			String warning = GUIConstants.QUESTION_DELETE_MATCHRESULT + tab.getName()
            //					+ GUIConstants.QUESTION_MARK + GUIConstants.LINEBREAK
            //					+ GUIConstants.OK_CONTINUE;
            //			int res = JOptionPane.showConfirmDialog(mainWindow, warning,
            //					GUIConstants.WARNING, JOptionPane.OK_CANCEL_OPTION);
            //			if (res == JOptionPane.CANCEL_OPTION) {
            //				setStatus(GUIConstants.DEL_MATCHRESULT_ABORTED);
            //				return;
            //			} else {
            if (getManagementPane().countAllWorkMatchresults() == 0) {
                setNewMatchResult(null);
            }
            //			}
            setStatus(GUIConstants.DEL_MATCHRESULT_DONE);
        } else {
            setStatus(GUIConstants.NO_MATCHRESULT);
        }
    }

    /*
     * open a Dialog where the user can choose an existing Matcher, that shall
     * be deleted
     */
    public void deleteVariable() {
        setStatus(GUIConstants.DEL_VARIABLE);
        SimpleDirectedGraph<String, DefaultEdge> graph = VariableGraph.getVariableGraph(accessor);


        if ((graph != null) && graph.vertexSet().size()>0) {
            ArrayList<String> roots = Dlg_ShowHierarchy.getRoots(graph);

            Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                    GUIConstants.CHOOSE_VARIABLE, roots, MainWindow.DIM_SMALL,
                    Dlg_ChooseFromList.VARIABLE,
                    ListSelectionModel.SINGLE_SELECTION);
            dialog.setLocation(position);
            String config = (String) dialog.showDialog();
            if (config != null) {
                int res = JOptionPane.showConfirmDialog(mainWindow,
                        GUIConstants.QUESTION_DELETE_VARIABLE + config
                                + GUIConstants.QUESTION_MARK + GUIConstants.LINEBREAK
                                + GUIConstants.OK_CONTINUE, GUIConstants.WARNING,
                        JOptionPane.OK_CANCEL_OPTION);
                if (res != JOptionPane.OK_OPTION) {
                    setStatus(GUIConstants.DEL_VARIABLE_ABORTED);
                } else {
                    setStatus(GUIConstants.DEL_VARIABLE + config
                            + GUIConstants.DONE);
//					if (config.getBaseMatcher() == Manager.MATCH_GEN_REUSE) {
//						accessor.deleteReuseStrategy(config.getId());
//					} else  if (config.getBaseMatcher() == Manager.MATCH_GEN_INSTANCE) {
//						accessor.deleteInstanceMatcher(config.getId());
//					} else  if (config.getBaseMatcher() == Manager.STRAT_UNDEF) {
//						accessor.deleteUserMatcher(config.getId());
//					} else  {
//						accessor.deleteMatcher(config.getId());
//					}
                    config = config.substring(0, config.indexOf("="));
                    importer.deleteWorkflowVariable(config);
                    updateAll(false); //no parse or import of new sources
                }
            } else
                setStatus(GUIConstants.NO_MATCHER_CHOSEN);
        } else
            setStatus(GUIConstants.NO_MATCHER);
    }

    /*
     * open a Dialog where the user can choose an imported Schema, that shall be
     * deleted
     */
    public void deleteSchemaDB(Object _schemas) {
        setStatus(GUIConstants.DEL_SCHEMA_DB);
        //		mainWindow.getNewContentPane().setMenuStateDB(
        //				true);
        //		mainWindow.getNewContentPane().setProgressBar(true);
        if (_schemas == null) {
            String titel = GUIConstants.CHOOSE_SCHEMA_DEL;
            Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                    titel, LoadFromDBThread.getAllSchemas(this),
                    MainWindow.DIM_SMALL, Dlg_ChooseFromList.DELETE_SCHEMA_DB,
                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            dialog.setLocation(position);
            _schemas = dialog.showDialog();
        }
        if (_schemas != null) {
            if (_schemas instanceof Source) {
                Source schema = (Source) _schemas;
//				accessor.dropViewTables(schema.getId());
                //				String sourceName = matchresult.getSourceName();
                //				String targetName = matchresult.getTargetName();
                //				if ((sourceName != null) &&
                // schema.getName().equals(sourceName)) {
                //					closeSchema(true, false);
                //				}
                //				if ((targetName != null) &&
                // schema.getName().equals(targetName)) {
                //					closeSchema(false, false);
                //				}
                accessor.deleteSource(schema.getId());
                HashSet<SourceRelationship> matchresults2 = manager.getAccessor().getSourceRels(schema.getId());
                if (matchresults2 != null) {
                    for (SourceRelationship currentMatchresult : matchresults2) {
                        accessor.deleteSourceRel(currentMatchresult.getId());
                    }
//					accessor.deleteSourcePaths();
                }
            } else if (_schemas instanceof ArrayList) {
                //				DataImport importer = new DataImport();
                //				ArrayList masterSources = importer.importSourceInfo();
                for (int i = 0; i < ((ArrayList) _schemas).size(); i++) {
                    Source schema = (Source) ((ArrayList) _schemas).get(i);
//					accessor.dropViewTables(schema.getId());
                    //					String sourceName = matchresult.getSourceName();
                    //					String targetName = matchresult.getTargetName();
                    //					if ((sourceName != null)
                    //							&& schema.getName().equals(sourceName)) {
                    //						closeSchema(true, false);
                    //					}
                    //					if ((targetName != null)
                    //							&& schema.getName().equals(targetName)) {
                    //						closeSchema(false, false);
                    //					}
                    accessor.deleteSource(schema.getId());

                    HashSet<SourceRelationship> matchresults2 = manager.getAccessor().getSourceRels(schema.getId());
                    if (matchresults2 != null) {
                        for (SourceRelationship currentMatchresult : matchresults2) {
                            accessor.deleteSourceRel(currentMatchresult	.getId());
                        }
//						accessor.deleteSourcePaths();
                    }
                }
            }
			/*
			 * updateAllOld(true, true);
			 */
            updateAll(false); //no parse or import of new sources
            setStatus(GUIConstants.DEL_SCHEMA_DONE);
        } else
            setStatus(GUIConstants.DEL_SCHEMA_ABORTED);
        //		mainWindow.getNewContentPane().setMenuStateDB(
        //				false);
        //		mainWindow.getNewContentPane().setProgressBar(false);
    }

    /*
     * open a Dialog where the user can choose a Matchresult (DB), that shall be
     * deleted
     */
    public void deleteMatchresultDB() {
        setStatus(GUIConstants.DELETE_MATCHRESULT_DB);
        String titel = GUIConstants.CHOOSE_MATCHRESULT_DEL;
        ArrayList selectedRel = LoadFromDBThread.getAllDBMatchresults(this);
        if (selectedRel == null) {
            setStatus(GUIConstants.NO_MATCHRESULT_IN_DB);
        }
        Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                titel, selectedRel, MainWindow.DIM_SMALL,
                Dlg_ChooseFromList.DELETE_MATCHRESULT_DB,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        dialog.setLocation(position);
        Object o = dialog.showDialog();
        deleteMatchresultDB(o);
    }

    /*
     * delete given SourceRelationship(s)
     */
    public void deleteMatchresultDB(Object _matchresults) {
        if (_matchresults != null) {
            if (_matchresults instanceof SourceRelationship) {
                accessor.deleteSourceRel(((SourceRelationship) _matchresults).getId());
//				accessor.dropViewTables(false);
//				accessor.deleteSourcePaths();
                setStatus(GUIConstants.DEL_MATCHRESULT_DONE);
            } else if (_matchresults instanceof ArrayList) {
                for (int i = 0; i < ((ArrayList) _matchresults).size(); i++) {
                    SourceRelationship sr = (SourceRelationship) ((ArrayList) _matchresults).get(i);
                    accessor.deleteSourceRel(sr.getId());
                }
//				accessor.dropViewTables(false);
//				accessor.deleteSourcePaths();
                setStatus(GUIConstants.DEL_MATCHRESULT_DONE);
            }
            updateAll(false); //no parse or import of new sources
        } else
            setStatus(GUIConstants.DEL_MATCHRESULT_ABORTED);
    }

//	/*
//	 * create a new matcher (hybrid matcher = match strategie)
//	 */
//	public void createNewMatcher() {
//		setStatus(GUIConstants.CREATE_MATCHER);
//		Dlg_NewMatcher dialog = new Dlg_NewMatcher(mainWindow, this);
//		dialog.showDlg(position);
//	}
//
//	/*
//	 * create a new matcher (hybrid matcher = match strategie)
//	 */
//	public void createNewReuseStrategy() {
//		setStatus(GUIConstants.CREATE_MATCHER);
//		Dlg_NewReuseMatcher dialog = new Dlg_NewReuseMatcher(mainWindow,
//				this);
//		dialog.showDlg(position);
//	}
//
//	/*
//	 * create a new matcher (hybrid matcher = match strategie)
//	 */
//	public void createNewMatcher(MatcherConfig _config) {
//		setStatus(GUIConstants.CREATE_MATCHER);
//		Dlg_NewMatcher dialog = new Dlg_NewMatcher(mainWindow, this,
//				_config);
//		dialog.showDlg(position);
//	}
//
//	/*
//	 * create a new matcher (hybrid matcher = match strategie)
//	 */
//	public void createNewReuseStrategy(MatcherConfig _config) {
//		setStatus(GUIConstants.CREATE_MATCHER);
//		Dlg_NewReuseMatcher dialog = new Dlg_NewReuseMatcher(mainWindow,
//				this, _config);
//		dialog.setLocation(position);
//		dialog.setVisible(true);
//	}
//	
//	/*
//	 * create a new matcher (hybrid matcher = match strategie)
//	 */
//	public void createNewInstanceMatcher(MatcherConfig _config) {
//		setStatus(GUIConstants.CREATE_MATCHER);
//		Dlg_NewInstanceMatcher dialog = new Dlg_NewInstanceMatcher(mainWindow,
//				this, _config);
//		dialog.setLocation(position);
//		dialog.setVisible(true);
//	}
//
//	/*
//	 * create a new matcher (hybrid matcher = match strategie)
//	 */
//	public void createNewInstanceMatcher() {
//		setStatus(GUIConstants.CREATE_MATCHER);
//		Dlg_NewInstanceMatcher dialog = new Dlg_NewInstanceMatcher(mainWindow,
//				this);
//		dialog.showDlg(position);
//	}

//	/*
//	 * start the Matching process with the loaded source and target schema
//	 */
//	public void executeMatching() {
//		setStatus(GUIConstants.EXECUTE_MATCHING);
//		if (checkSchemas()){
//			execute = new ExecuteMatchingThread(mainWindow, this,
//					ExecuteMatchingThread.NORMAL);
//			execute.start();
//		}
//	}

    // not used: taxonomy
//	public void executeTaxonomyMatching(String _strategyOrg, int _matcherOrg) {
//		setStatus(GUIConstants.EXECUTE_MATCHING);
//		if (!getMatchresult().containsSource()) {
//			setStatus(GUIConstants.NO_SRC_SCHEMA);
//			return;
//		}
//		if (!getMatchresult().containsTarget()) {
//			setStatus(GUIConstants.NO_TRG_SCHEMA);
//			return;
//		}
//		setStrategy(GUIConstants.STRAT_ALLCONTEXT);
//		setMatcherAllContext(match.Manager.MATCH_SYS_TAXONOMY); //128
//		execute = new ExecuteMatchingThread(mainWindow, this,
//				ExecuteMatchingThread.TAXONOMY);
//		execute.setStrategyOrg(_strategyOrg);
//		execute.setMatcherOrg(_matcherOrg);
//		execute.start();
//	}
//
//	public void executeTaxonomyMatching(String _strategy) {
//		String strategyOrg = getStrategy();
//		int matcherOrg = getMatcherAllContext();
//		getManagementPane().setButtonRunTooltip(": " + _strategy);
//		/*
//		 * todo: - if taxonomy not loaded/chosen yet, open 'select tax'-dialog -
//		 * get a default taxonomy 'strategy', i.e. matcher config only using
//		 * this matcher
//		 */
//		if (getTaxonomyChosen() != null) {
//			// tax is loaded, engage!
//			executeTaxonomyMatching(strategyOrg, matcherOrg);
//		} else {
//			// no tax loaded. load!
//			selectTaxonomy();
//			// selectTaxonomy waits till simMap got calculated, so shud
//			// be safe to exectue:
//			executeTaxonomyMatching(strategyOrg, matcherOrg);
//		}
//	}


    private boolean checkSchemas(){
        if (!guiMatchresult.containsSource()) {
            setStatus(GUIConstants.NO_SRC_SCHEMA);
            return false;
        }
        if (!guiMatchresult.containsTarget()) {
            setStatus(GUIConstants.NO_TRG_SCHEMA);
            return false;
        }
        return true;
    }

    /*
     * start the Fragment Matching process with the loaded source and target
     * schema
     */
    public void executeFragmentMatching(StepByStepFragmentMatching _step) {
        setStatus(GUIConstants.EXECUTE_MATCHING);
        if (checkSchemas()){
            execute = new ExecuteMatchingThread(mainWindow, this,
                    ExecuteMatchingThread.STEPBYSTEP, _step);
            execute.start();
        }
    }

//	/*
//	 * start the Fragment Matching process with the loaded source and target
//	 * schema
//	 */
//	public void executeReuseMatching(boolean _combined) {
//		setStatus(GUIConstants.EXECUTE_MATCHING);
//		if (checkSchemas()){
//			if (_combined) {
//				execute = new ExecuteMatchingThread(mainWindow, this,
//						ExecuteMatchingThread.COMBINEDReuseStrategy);
//			} else {
//				execute = new ExecuteMatchingThread(mainWindow, this,
//						ExecuteMatchingThread.ReuseStrategy);
//			}
//			execute.start();
//		}
//	}

    /*
     * stop the Matching process
     */
    public void stopMatching() {
        if (execute != null) {
            //			Thread waiter = execute;
            //			execute = null;
            //			waiter.interrupt();
            execute.stop();
            setStatus(GUIConstants.MATCHING_STOP);
            //			execute = null;
            guiMatchresult.setMatchResult(null);
            getManagementPane().setMenuStateRun(false);
            mainWindow.getNewContentPane().setProgressBar(false);
            if ((stepFragmentMatching != null) && (stepFragmentMatching.getDlg3() != null)) {
                stepFragmentMatching.getDlg3().setState(StepByStepFragmentMatching.STOP);
                stepFragmentMatching.disposeDlg3();
            }
        }
    }

    /*
     * return ManagementPane
     */
    public ManagementPane getManagementPane() {
        if (mainWindow!=null){
            return mainWindow.getNewContentPane().getManagementPane();
        }
        return null;
    }

    /*
     * save the Matchresult to the data base (by saving the match results between
     * the loaded source and target schema) attention: the program has to be
     * stopped and started again to see this matchresult in the database!
     */
    public void saveMatchresultToDB() {
        MatchResult mr = getManagementPane().getSelectedWorkMatchresult();
        saveMatchresultToDB(mr);
    }

    /*
     * save the Matchresult to the data base (by saving the match results between
     * the loaded source and target schema) attention: the program has to be
     * stopped and started again to see this matchresult in the database!
     */
    public void saveMatchresultToDB(MatchResult _matchResult) {
        if (_matchResult != null) {
            SaveToDBThread importMatchresult = new SaveToDBThread(mainWindow,
                    this, SaveToDBThread.STATE_IMPORT_MATCHRESULT, _matchResult);
            importMatchresult.start();
        } else {
            setStatus(GUIConstants.NO_MATCHRESULT);
        }
    }

    public void saveSchemaToDB(Graph _graph) {
        if (_graph != null) {
            SaveToDBThread importSchema = new SaveToDBThread(mainWindow,
                    this, SaveToDBThread.STATE_IMPORT_SCHEMA, _graph);
            importSchema.start();
        } else {
            setStatus(GUIConstants.NO_WSCHEMA);
        }
    }

    public void saveMatchresultToDB(ArrayList _matchResults) {
        if (_matchResults != null) {
            boolean isSchema = false;
            SaveToDBThread importMatchresult = new SaveToDBThread(mainWindow,
                    this, SaveToDBThread.STATE_IMPORT_MATCHRESULTS, isSchema,
                    _matchResults);
            importMatchresult.start();
        } else {
            setStatus(GUIConstants.NO_MATCHRESULT);
        }
    }

    public void saveSchemaToDB(ArrayList _graphs) {
        if (_graphs != null) {
            boolean isSchema = true;
            SaveToDBThread importSchema = new SaveToDBThread(mainWindow,
                    this, SaveToDBThread.STATE_IMPORT_SCHEMAS, isSchema,
                    _graphs);
            importSchema.start();
        } else {
            setStatus(GUIConstants.NO_WSCHEMA);
        }
    }

    /*
     * show information about the match results
     */
    public void showResultsInRDFAlignment() {
        setStatus(GUIConstants.INFO_MR);
        if (guiMatchresult.containsMatchResult()) {
            TextFrame wnd = new TextFrame(guiMatchresult.getMatchResult(),
                    GUIConstants.RESULTS_RDF_ALIGNMENT, TextFrame.RDF, MainWindow.DIM_LARGE);
//			wnd.setSize(MainWindow.DIM_LARGE);
            wnd.setLocation(position);
            wnd.setVisible(true);
        } else {
            setStatus(GUIConstants.NO_MR);
        }
    }

//	public void showResultsinXQuery(){
//		Dlg_QueryDisplay dlg = new Dlg_QueryDisplay(mainWindow,this);
//		dlg.showDlg(position,MainWindow.DIM_EXISTING_MATCHER);
//	}

    /*
     * show information about the match results
     */
    public void showInfoMatchResults() {
        setStatus(GUIConstants.INFO_MR);
        if (guiMatchresult.containsMatchResult()) {
            //			if (!preprocessing.equals(GUIConstants.PREP_REDUCED))
            //				// matchResult =
            //				// manager.validateSimplifiedMatchResult(matchResult);
            //				result = Manager.transformMatchResult(result,
            //						Graph.GRAPH_STATE_REDUCED);
            TextFrame wnd = new TextFrame(guiMatchresult.getMatchResult(),
                    GUIConstants.INFO_MR, TextFrame.MATCHRESULT_INFO, MainWindow.DIM_MEDIUM);
//			wnd.setSize(MainWindow.DIM_LARGE);
            wnd.setLocation(position);
            wnd.setVisible(true);
        } else {
            setStatus(GUIConstants.NO_MR);
        }
    }

    /*
     * show the match results
     */
    public void showMatchResults() {
        setStatus(GUIConstants.INFO_MR);
        if (guiMatchresult.containsMatchResult()) {
            //			if (!preprocessing.equals(GUIConstants.PREP_REDUCED))
            //				// matchResult =
            //				// manager.validateSimplifiedMatchResult(matchResult);
            //				result = Manager.transformMatchResult(result,
            //						Graph.GRAPH_STATE_REDUCED);
            TextFrame wnd = new TextFrame(guiMatchresult.getMatchResult(),
                    GUIConstants.MATCHRESULT_CORRESP, TextFrame.MATCHRESULT, MainWindow.DIM_LARGE);
//			wnd.setSize(MainWindow.DIM_LARGE);
            wnd.setLocation(position);
            wnd.setVisible(true);
        } else {
            setStatus(GUIConstants.NO_MR);
        }
    }

    /*
     * show the about information
     */
    public void showAbout() {
        setStatus(GUIConstants.ABOUT);
        Dlg_About aboutDlg = new Dlg_About(mainWindow);
        aboutDlg.showDlg(position);
    }

    /*
     * set the status bar of the main window to the given String
     */
    public void setStatus(String _text) {
        mainWindow.getNewContentPane().setStatus(_text);
    }

//	/* set strategy */
//	public void setStrategy(String _strategy) {
//		if (strategy.equals(GUIConstants.STRAT_ALLCONTEXT) || strategy.equals(GUIConstants.STRAT_FILTEREDCONTEXT)){
//			contextStrategy = strategy;
//		}
//		strategy = _strategy;
//	}
//	
//	/* set strategy */
//	public void setLastContextStrategy(String _strategy) {
//		contextStrategy = _strategy;
//	}
//	
//	/* set strategy */
//	public String getLastContextStrategy() {
//		return contextStrategy;
//	}

    /*
     * set preprocessing to the given value, update all current matchresult tabs and
     * the schemas
     */
    public void setPreprocessing(int _preprocessing) {
        if (preprocessing == _preprocessing) {
            return;
        }
        // set new preprocessing
        preprocessing = _preprocessing;
        if (guiMatchresult.containsSource()) {
            Graph graph = loadSourceGraph(guiMatchresult.getSourceGraph(), false, false);
            if (graph.getPreprocessing()!=preprocessing){
                System.out.println("Transformation to "+Graph.preprocessingToString(preprocessing)+" failed, therefore use "+Graph.preprocessingToString(preprocessing-1));
                preprocessing = preprocessing-1;
            }
        }
        if (guiMatchresult.containsTarget()) {
            Graph graph = loadTargetGraph(guiMatchresult.getTargetGraph(), false, false);
            if (graph.getPreprocessing()!=preprocessing){
                System.out.println("Transformation to "+Graph.preprocessingToString(preprocessing)+" failed, therefore use "+Graph.preprocessingToString(preprocessing-1));
                preprocessing = preprocessing-1;
                loadSourceGraph(guiMatchresult.getSourceGraph(), false, false);
            }
        }
        getManagementPane().updateCurrentMatchresult();
        mainWindow.setPreprocessing(preprocessing);
    }

    public void setView(int _view){
        if (view == _view) {
            return;
        }
        // set new view
        view = _view;
        if (_view==MainWindow.VIEW_GRAPH){
            getMainWindow().graph.setSelected(true);
        } else {
            getMainWindow().nodes.setSelected(true);
        }
        getMatchresultView().setView(view);
        getManagementPane().updateCurrentMatchresult();
    }

//	/* return strategy */
//	public String getStrategy() {
//		return strategy;
//	}

    /* return preprocessing */
    public int getPreprocessing() {
        return preprocessing;
    }

    /* return preprocessing */
    public int getView() {
        return view;
    }

    /* return matchresult */
    public GUIMatchResult getGUIMatchresult() {
        return guiMatchresult;
    }


    /* return file synonyms */
    public String getFileSyn() {
        return file_syn;
    }

    /* return file abbreviations */
    public String getFileAbb() {
        return file_abb;
    }
    /* return matchresult */
    public MatchresultView2 getMatchresultView() {
        if (mainWindow == null || mainWindow.getNewContentPane() == null) {
            return null;
        }
        return mainWindow.getNewContentPane().getMatchresultView();
    }

    /* return mainWindow */
    public MainWindow getMainWindow() {
        return mainWindow;
    }

    /* return Manager */
    public Manager getManager() {
        return manager;
    }

    /*
     * open a dialog for choosing a matchresult (source + target schema, match
     * results) from it, load the chosen matchresult from the data base
     */
    public void loadMatchresultFromDB() {
        setStatus(GUIConstants.LOAD_MATCHRESULT_DB);
        ArrayList selectedRel = LoadFromDBThread.getAllDBMatchresults(this);
        if (selectedRel == null) {
            setStatus(GUIConstants.NO_MATCHRESULT_IN_DB);
            return;
        }
        Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                GUIConstants.LOAD_MATCHRESULT_DB, selectedRel, MainWindow.DIM_SMALL,
                Dlg_ChooseFromList.MATCHRESULT_DB,
                ListSelectionModel.SINGLE_SELECTION);
        dialog.setLocation(position);
        SourceRelationship sr = (SourceRelationship) dialog.showDialog();
        if (sr != null) {
            LoadFromDBThread loadMatchresult = new LoadFromDBThread(
                    mainWindow, this,
                    LoadFromDBThread.STATE_LOAD_MATCHRESULT_DB, sr);
            loadMatchresult.start();
        } else {
            setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);
        }
    }

    /*
     * load the chosen matchresult from the data base
     */
    public void loadMatchresultsFromDB(ArrayList _list) {
        if (_list != null && _list.size() > 0) {
            LoadFromDBThread loadMatchresult = new LoadFromDBThread(
                    mainWindow, this,
                    LoadFromDBThread.STATE_LOAD_MATCHRESULTS_DB, _list);
            loadMatchresult.start();
        }
    }

    /*
     * load the chosen matchresult from the data base
     */
    public void loadMatchresultFromDB(SourceRelationship _source) {
        if (_source != null) {
            LoadFromDBThread loadMatchresult = new LoadFromDBThread(
                    mainWindow, this,
                    LoadFromDBThread.STATE_LOAD_MATCHRESULT_DB, _source);
            loadMatchresult.start();
        }
    }

	/*
	 * set the moode for editing the current matchresult on or of (if mode is on and
	 * there is no current one, create a new, empty one)
	 */
//	public boolean editMatchresult(boolean _state, boolean _showStatusMessage) {
//		//		System.out.println("edit: " +state);
//		// unselect both trees
//		if (!matchresult.containsMatchResult()) {
//			if (_showStatusMessage) {
//				setStatus(GUIConstants.NO_MATCHRESULT);
//			}
//			return false;
//		}
//		if (_state) {
////			mainWindow.clearMatchresultView();
//			getMatchresultView().getSourceTree().setBackground(
//					MainWindow.EDIT_BACKGROUND);
//			getMatchresultView().getTargetTree().setBackground(
//					MainWindow.EDIT_BACKGROUND);
//			((ExtJTreeCellRenderer) getMatchresultView().getSourceTree()
//					.getCellRenderer())
//					.setGeneralBackground(MainWindow.EDIT_BACKGROUND);
//			((ExtJTreeCellRenderer) getMatchresultView().getTargetTree()
//					.getCellRenderer())
//					.setGeneralBackground(MainWindow.EDIT_BACKGROUND);
////			getMatchresultView().setLastSelectedTree(MainWindow.NONE);
//			getMatchresultView().setChanged(true);
//			if (_showStatusMessage) {
//				setStatus(GUIConstants.EDIT_ON);
//			}
//			//			if (matchresult.getMatchResult() == null) {
//			//				MatchResult newResult = new MatchResult();
//			//				newResult.setSourceGraph(matchresult.getOrgSourceSchema());
//			//				newResult.setTargetGraph(matchresult.getOrgTargetSchema());
//			//				// newResult.setMatcher(simplResult.getMatcher());
//			//				newResult.setMatcherName(MatchResult.MAP_OP_TRANSFORM);
//			//				// newResult.setMatcherConfig(simplResult.getMatcherConfig());
//			//				setNewMatchResult(newResult, false);
//			//				mainWindow.getNewContentPane().addTab(newResult, true);
//			//			}
//		} else {
//			if (_showStatusMessage) {
//				setStatus(GUIConstants.EDIT_OFF);
//			}
//			getMatchresultView().getSourceTree().setBackground(
//					MainWindow.GLOBAL_BACKGROUND);
//			getMatchresultView().getTargetTree().setBackground(
//					MainWindow.GLOBAL_BACKGROUND);
//			if (getMatchresultView().getSourceTree().getCellRenderer() instanceof ExtJTreeCellRenderer) {
//				((ExtJTreeCellRenderer) getMatchresultView().getSourceTree()
//						.getCellRenderer())
//						.setGeneralBackground(MainWindow.GLOBAL_BACKGROUND);
//			}
//			if (getMatchresultView().getTargetTree().getCellRenderer() instanceof ExtJTreeCellRenderer) {
//				((ExtJTreeCellRenderer) getMatchresultView().getTargetTree()
//						.getCellRenderer())
//						.setGeneralBackground(MainWindow.GLOBAL_BACKGROUND);
//			}
//		}
//		getMatchresultView().setEdit(_state);
//		return true;
//	}

    /*
     * reuse the whole Matchresults in the DB replace the task "Match A-B" with the
     * task "Match A-A' and Compose A-A'-B" (whereby A and A' are similar)
     */
    public void reuseMatchresult() {
        ReuseWizardThread reuseMatchresult = new ReuseWizardThread(this);
        reuseMatchresult.start();
    }
//	// use: schema management
//	public void rangeMatchresult(boolean _invert, MatchResult _result) {
//		if (_result == null) {
//			setStatus(GUIConstants.NO_MATCHRESULT);
//			return;
//		}
//		Graph graph;
//		if (_invert) {
//			graph = MatchResult.invertRange(_result);
//		} else {
//			graph = MatchResult.range(_result);
//		}
//		closeSchema(true, false);
//		closeSchema(false, false);
//		loadSourceGraph(graph, true, false);
//	}

//	public void domainMatchresult(boolean _invert, MatchResult _result) {
//		if (_result == null) {
//			setStatus(GUIConstants.NO_MATCHRESULT);
//			return;
//		}
//		Graph graph;
//		if (_invert) {
//			graph = MatchResult.invertDomain(_result);
//		} else {
//			graph = MatchResult.domain(_result);
//		}
//		closeSchema(true, false);
//		closeSchema(false, false);
//		loadSourceGraph(graph, true, false);
//	}

//	public void smergeMatchresult(MatchResult _result) {
//		if (_result == null) {
//			setStatus(GUIConstants.NO_MATCHRESULT);
//			return;
//		}
//		Graph graph = MatchResult.merge(_result);
//		closeSchema(true, false);
//		closeSchema(false, false);
//		loadSourceGraph(graph, true, false);
//	}

    /*
     * open a dialog for choosing a matchresult (same source + target schema, except
     * this one) from it, compare the current matchresult with that one
     */
    public void compareMatchresult(MatchResult _result) {
        ArrayList tmpMatchresults = getManagementPane().getAllWorkMatchresults();
        if (tmpMatchresults.size() == 0) {
            setStatus(GUIConstants.NO_MATCHRESULT);
            return;
        }
        setStatus(GUIConstants.CONFIGURE_STRATEGY);
        Dlg_CompareMatchresult dialog = new Dlg_CompareMatchresult(mainWindow,
                this, _result);
        dialog.showDlg(position);
    }

    /*
     * open a dialog for choosing a matchresult (same source + target schema, except
     * this one) from it, compare the current matchresult with that one
     */
    public void mergeMatchresult() {
        if (!guiMatchresult.containsMatchResult()) {
            setStatus(GUIConstants.NO_MATCHRESULT);
            return;
        }
        MatchResult current = guiMatchresult.getMatchResult();
        // Merge
        setStatus(GUIConstants.MERGE_MATCHRESULT);
        ArrayList<Object> selectedRel = new ArrayList<Object>();
        ArrayList tmpMatchresults = getManagementPane().getAllWorkMatchresults();

        if ((tmpMatchresults != null) && (tmpMatchresults.size() > 1)) {
            tmpMatchresults.remove(current);
            for (int i = 0; i < tmpMatchresults.size(); i++) {
                MatchResult currentMR = (MatchResult) tmpMatchresults.get(i);
                Object[] os = new Object[2];
                os[0] = currentMR;
                os[1] = currentMR.getName();
                selectedRel.add(os);
            }
        } else {
            setStatus(GUIConstants.NO_OTHER_MATCHRESULTS);
            return;
        }
        Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                GUIConstants.CHOOSE_MATCHRESULT, selectedRel, MainWindow.DIM_SMALL,
                Dlg_ChooseFromList.MATCHRESULT_TMP,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        dialog.setLocation(position);
        Object o = dialog.showDialog();
        MatchResult res;
        if (o != null) {
            MatchResult newResult = new MatchResultArray();
            String info;
            if (o instanceof ArrayList) {
//				info = current.getResultName();
                info = current.getName();
                for (int i = 0; i < ((ArrayList) o).size(); i++) {
                    Object[] os = (Object[]) ((ArrayList) o).get(i);
                    // MatchResult
                    res = (MatchResult) os[0];
                    // both Match Results are (most likely) simplified (if thats
                    // the current preprocessing)
                    info += GUIConstants.PLUS + os[1];
                    newResult = MatchResult.merge(newResult, res);
                }
            } else {
                Object[] os = (Object[]) o;
                // MatchResult
                res = (MatchResult) os[0];
                // both Match Results are (most likely) simplified (if thats the
                // current preprocessing)
//				info = current.getResultName() + GUIConstants.PLUS + os[1];
                info = current.getName() + GUIConstants.PLUS + os[1];
                newResult = MatchResult.merge(newResult, res);
            }
            //newResult.append(current);
            newResult = MatchResult.merge(newResult, current);
            newResult.setGraphs(current.getSourceGraph(), current.getTargetGraph());
//			newResult.setMatcherName(MatchResult.MAP_OP_MERGE);
            info = MatchResult.OP_MERGE + ":" + info;
            newResult.setMatchInfo(info);
            mainWindow.getNewContentPane().addMatchResult(newResult, true);
        } else
            setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);
    }

    //Ensure that all _matchResults have the same source, target, respectively
    //Required by Merge, Intersect, and actually, by Diff and Compare, too
    public boolean checkMatchResults(ArrayList _matchResults) {
        if (_matchResults == null || _matchResults.isEmpty())
            return false;
        Graph sourceGraph = null;
        Graph targetGraph = null;
        for (int i = 0; i < _matchResults.size(); i++) {
            MatchResult result = (MatchResult) _matchResults.get(i);
            if (sourceGraph == null || targetGraph == null) {
                sourceGraph = result.getSourceGraph();
                targetGraph = result.getTargetGraph();
            } else if (!(sourceGraph.getSource().equals(result.getSourceGraph().getSource()) && targetGraph.getSource().equals(result.getTargetGraph().getSource()))) {
                JOptionPane.showMessageDialog(getMainWindow(), GUIConstants.OPERATION_REQUIRES,
                        GUIConstants.INFORMATION, JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /*
     * create a new match result by merging all given match results with each
     * other
     */
    public void mergeMatchresult(ArrayList _matchResults) {
        if (_matchResults==null || _matchResults.size() < 2){
            setStatus(GUIConstants.NOT_ENOUGH_MATCHRESULT);
            return;
        }
        if (!checkMatchResults(_matchResults)) {
            setStatus(GUIConstants.DIFFERENT_SOURCETARGET);
            return;
        }
        // Merge
        MatchResult newResult = new MatchResultArray();
        MatchResult current = null;
        String info = GUIConstants.EMPTY;
        for (int i = 0; i < _matchResults.size(); i++) {
            // MatchResult
            current = (MatchResult) _matchResults.get(i);
            if (i > 0) {
//				info += GUIConstants.PLUS + current.getResultName();
                info += GUIConstants.PLUS + current.getName();
            } else {
//				info = current.getResultName();
                info = current.getName();
                newResult.setGraphs(current.getSourceGraph(), current.getTargetGraph());
            }
            // newResult.append(res);
            newResult = MatchResult.merge(newResult, current);
        }
//		newResult.setMatcherName(MatchResult.MAP_OP_MERGE);
//		newResult.setMatchInfo(info);
        newResult.setMatchInfo(MatchResult.operationToString(MatchResult.OP_MERGE) + ": " + info);
        mainWindow.getNewContentPane().addMatchResult(newResult, true);
        setStatus(GUIConstants.MERGE_DONE);
    }

    /*
     * create a new match result by intersecting all given match results with
     * each other
     */
    public void intersectMatchresult(ArrayList _matchResults) {
        if (_matchResults.size() < 2) {
            setStatus(GUIConstants.NOT_ENOUGH_MATCHRESULT);
            return;
        }
        if (!checkMatchResults(_matchResults)) {
            setStatus(GUIConstants.DIFFERENT_SOURCETARGET);
            return;
        }
        // Intersect
        MatchResult newResult = (MatchResult) _matchResults.get(0);
        String info = newResult.getName();
        for (int i = 1; i < _matchResults.size(); i++) {
            // MatchResult
            MatchResult current = (MatchResult) _matchResults.get(i);
            info += GUIConstants.EQUAL + current.getName();
//			newResult = current.intersect(newResult);
            newResult = MatchResult.intersect(current, newResult);
        }
        if (newResult==null){
            setStatus(GUIConstants.NO_RESULTS);
            return;
        }
//		newResult.setMatcherName(MatchResult.MAP_OP_INTERSECT);
//		newResult.setMatchInfo(info);
        newResult.setMatchInfo(MatchResult.operationToString(MatchResult.OP_INTERSECT) + ": " + info);
        mainWindow.getNewContentPane().addMatchResult(newResult, true);
        setStatus(GUIConstants.INTERSECT_DONE);
    }

    /*
     * duplicate the current matchresult
     */
    public void duplicateMatchresult() {
        if (!guiMatchresult.containsMatchResult()) {
            setStatus(GUIConstants.NO_MATCHRESULT);
            return;
        }
        MatchResult current = guiMatchresult.getMatchResult();
        MatchResult newResult = current.clone();
        newResult.setGraphs(current.getSourceGraph(), current.getTargetGraph());
//		newResult.setMatcherName(GUIConstants.DUPLICATE);
        newResult.setName(GUIConstants.COPY_OF + current.getName());
        mainWindow.getNewContentPane().addMatchResult(newResult, true);
        // Duplicate done
        setStatus(GUIConstants.DUPLICATE_DONE);
    }

    /*
     * open a dialog for choosing a matchresult (same source + target schema, except
     * this one) from it, intersect the current matchresult with that one
     */
    public void intersectMatchresult() {
        if (!guiMatchresult.containsMatchResult()) {
            setStatus(GUIConstants.NO_MATCHRESULT);
            return;
        }
        MatchResult current = guiMatchresult.getMatchResult();
        setStatus(GUIConstants.INTERSECT_MATCHRESULT);
        ArrayList<Object> selectedRel = new ArrayList<Object>();
        ArrayList tmpMatchresults = getManagementPane().getAllWorkMatchresults();
        if ((tmpMatchresults != null) && (tmpMatchresults.size() > 1)) {
            tmpMatchresults.remove(current);
            for (int i = 0; i < tmpMatchresults.size(); i++) {
                MatchResult currentMR = (MatchResult) tmpMatchresults.get(i);
                Object[] os = new Object[2];
                os[0] = currentMR;
                os[1] = currentMR.getName();
                selectedRel.add(os);
            }
        } else {
            setStatus(GUIConstants.NO_OTHER_MATCHRESULTS);
            return;
        }
        Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                GUIConstants.CHOOSE_MATCHRESULT, selectedRel, MainWindow.DIM_SMALL,
                Dlg_ChooseFromList.MATCHRESULT_TMP,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        dialog.setLocation(position);
        Object o = dialog.showDialog();
        MatchResult res;
        if (o != null) {
            MatchResult newResult = current;
            String info;
            if (o instanceof ArrayList) {
                info = current.getName();
                for (int i = 0; i < ((ArrayList) o).size(); i++) {
                    Object[] os = (Object[]) ((ArrayList) o).get(i);
                    // MatchResult
                    res = (MatchResult) os[0];
                    // both Match Results are (most likely) simplified (if thats
                    // the current preprocessing)
                    info += GUIConstants.EQUAL + os[1];
//					newResult = newResult.intersect(res);
                    newResult = MatchResult.intersect(newResult, res);
                }
            } else {
                Object[] os = (Object[]) o;
                // MatchResult
                res = (MatchResult) os[0];
//				newResult = newResult.intersect(res);
                newResult = MatchResult.intersect(newResult, res);
                // both Match Results are (most likely) simplified (if thats the
                // current preprocessing)
                info = current.getName() + GUIConstants.EQUAL + os[1];
            }
            if (newResult == null) {
                setStatus(GUIConstants.NO_RESULTS);
                return;
            }
            //			newResult.setSourceGraph(current.getSourceGraph());
            //			newResult.setTargetGraph(current.getTargetGraph());
//			newResult.setMatcherName(MatchResult.OP_INTERSECT);
//			newResult.setMatchInfo(info);
            newResult.setMatchInfo(MatchResult.operationToString(MatchResult.OP_INTERSECT)+ ": " + info);
            mainWindow.getNewContentPane().addMatchResult(newResult, true);
        } else
            setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);
    }

    /*
     * open a dialog for choosing a matchresult (same source + target schema, except
     * this one) from it, Remove the overlapping part from the current Matchresult
     * (between this and another Matchresult)
     */
    public void differenceMatchresult(MatchResult _result) {
        if (_result == null) {
            setStatus(GUIConstants.NO_MATCHRESULT);
            return;
        }
        setStatus(GUIConstants.DIFFERENCE_MATCHRESULT);
        ArrayList<Object> selectedRel = new ArrayList<Object>();
        ArrayList tmpMatchresults = getManagementPane().getAllWorkMatchresults();
        if ((tmpMatchresults != null) && (tmpMatchresults.size() > 1)) {
            tmpMatchresults.remove(_result);
            for (int i = 0; i < tmpMatchresults.size(); i++) {
                MatchResult currentMR = (MatchResult) tmpMatchresults.get(i);
                Object[] os = new Object[2];
                os[0] = currentMR;
                os[1] = currentMR.getName();
                selectedRel.add(os);
            }
        } else {
            setStatus(GUIConstants.NO_OTHER_MATCHRESULTS);
            return;
        }
        Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                GUIConstants.CHOOSE_MATCHRESULT, selectedRel, MainWindow.DIM_SMALL,
                Dlg_ChooseFromList.MATCHRESULT_TMP,
                ListSelectionModel.SINGLE_SELECTION);
        dialog.setLocation(position);
        Object o = dialog.showDialog();
        MatchResult res;
        if (o != null) {
            Object[] os = (Object[]) o;
            // MatchResult
            res = (MatchResult) os[0];
            // both Match Results are (most likely) simplified (if thats the
            // current preprocessing)
            MatchResult newResult =MatchResult.diff(_result, res);
            if (newResult == null) {
                setStatus(GUIConstants.NO_RESULTS);
            } else {
                String info = _result.getName() + GUIConstants.DASH_SPACE
                        + os[1];
                newResult.setMatchInfo(info);
            }
            mainWindow.getNewContentPane().addMatchResult(newResult, true);
        } else
            setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);
    }

    /*
     * save new matchresults from a file into the data base
     */
    public void importMatchresultInDB() {
        setStatus(GUIConstants.SAVE_MATCHRESULT_DB);
        SaveToDBThread saveMatchresult = new SaveToDBThread(mainWindow, this,
                SaveToDBThread.STATE_IMPORT_MATCHRESULTFILE);
        saveMatchresult.start();
    }

//	public void importAccessionMatchresultInDB(){
//		// Example Import
//		ArrayList<Integer> list = manager.getSourceIdsWithNameAndUrl("anatomy_mouse_2010_owl", "http://mouse.owl");
//		if (list==null){
//			return;
//		}
//		// assumption there is only one source like that
//		int srcId = list.get(0);
//		list = manager.getSourceIdsWithNameAndUrl("anatomy_nci_2010_owl", "http://human.owl");
//		if (list==null){
//			return;
//		}
//		// assumption there is only one source like that
//		int trgId = list.get(0);
//		
//		int graphState = Graph.GRAPH_STATE_RESOLVED;
//		
//		String fileName = InsertParser.SOURCE_DIR + "/anatomy/pm_2010.txt";
//		manager.importMatchResultFile(fileName,srcId,trgId, graphState);
//		
//		fileName = InsertParser.SOURCE_DIR + "/anatomy/partial_2010.txt";
//		manager.importMatchResultFile(fileName,srcId,trgId, graphState);
//		
//		updateAll(false);
//	}


//	/*
//	 * open a dialog for selecting from it a file that contains a matchresult , load
//	 * the chosen matchresult from tis file
//	 */
//	public void loadMatchresultFromFile() {
//		setStatus(GUIConstants.LOAD_MATCHRESULT_FILE);
//		try {
//			JFileChooser chooser = new JFileChooser();
//			chooser.setCurrentDirectory(defaultDirectory);
//			int returnVal = chooser.showOpenDialog(mainWindow);
//			if (returnVal == JFileChooser.APPROVE_OPTION) {
//				defaultDirectory = chooser.getCurrentDirectory();
//				String _name = chooser.getSelectedFile().toString();
//				if (_name != null) {
//					ArrayList list = manager.loadMatchResultFile(_name);
//					if ((list != null) && (list.size() > 0)) {
//						LoadFromDBThread loadMatchresult = new LoadFromDBThread(
//								mainWindow, this,
//								LoadFromDBThread.STATE_LOAD_MATCHRESULT_FILE, list);
//						loadMatchresult.start();
//					}
//				} else
//					setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);
//			} else
//				setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);
//		} catch (Exception x) {
//			System.out.println(x.getMessage());
//			setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);
//		}
//	}

    public void importDefaultMatchresults(boolean[] _matchresults) {
        importDefaultMatchresults(_matchresults, null);
    }


    public String parseDefaultMatchresults(boolean[] _matchresults, String directory, Manager manager){
        String status =GUIConstants.IMPORT_EXAMPLES + GUIConstants.DONE;
        if (directory==null){
            directory = InsertParser.SOURCE_DIR;
        }
        boolean dbInsert = true;
        String[] matchresultList = new String[7];
        if (_matchresults[0]) {
            matchresultList[0] = directory + Strings.MATCHRESULT_PO;
        }
        if (_matchresults[1]) {
            matchresultList[1] = directory + Strings.MATCHRESULT_PO_OP;
        }
        if (_matchresults[2]) {
            matchresultList[2] = directory + Strings.MATCHRESULT_OP_XCBL;
        }
        if (_matchresults[4]) {
            matchresultList[3] = directory + Strings.MATCHRESULT_WEBDIR;
        }
        if (_matchresults[5]) {
            matchresultList[4] = directory + Strings.MATCHRESULT_WEBDIR_LARGE;
        }
        if (_matchresults[6]) {
            matchresultList[5] = directory + Strings.MATCHRESULT_SPICY;
            matchresultList[6] = directory + Strings.MATCHRESULT_UNI;
        }
        loadMatchresultFromFile(matchresultList, manager);
        if (_matchresults[3]) {
//            for (int i = 0; i < OAEIConstants.ONTO_NUMBERS.length; i++) {
//            	String uri = OAEIConstants.BENCHMARK_URL
//                        + OAEIConstants.ONTO_NUMBERS[i] + "/refalign.rdf";
//                MatchResult result = manager.loadOWLAlignmentFile(uri, null, null);
//	            if (result.getMatchCount()>0){
//	                result.setResultName(OAEIConstants.ONTO_NUMBERS[0]+"-"+OAEIConstants.ONTO_NUMBERS[i]+ "_refalign");
            String dir = directory;
            if (dir==null){
                File file = new File("./");
                System.out.println(file.getAbsolutePath());
                dir = file.getAbsolutePath().replace("\\", "/");
                dir = dir.substring(0, dir.length()-2)+ GUIConstants.SLASH+ InsertParser.SOURCE_DIR+ GUIConstants.SLASH;
            }
            dir = "file:/" + dir + Strings.BENCHMARK_URL;
            RDFAlignmentParser parser = new RDFAlignmentParser(manager, dbInsert);
            for (int i = 0; i < Strings.localContestOntos.length; i++) {
                String uri =
//            		GUIConstants.BENCHMARK_URL
                        dir
                                + Strings.localContestOntos[i] + "/refalign.rdf";
//              MatchResult result = manager.loadOWLAlignmentFile(uri, null, null);

                String name = Strings.localContestOntos[0]+"-"+Strings.localContestOntos[i]+ "_refalign";
                parser.loadOWLAlignmentFile(uri, name, null, null);
            }
        }
        if (_matchresults[7]) {
            AccMatchResultParser parser = new AccMatchResultParser(importer, dbInsert);
            int graphState = Graph.PREP_LOADED;
            String fileSrc = directory+ Strings.DEFAULT_SCHEMAS[21][0];
            int srcId = importer.getSourceId(Strings.DEFAULT_SCHEMAS[21][1], fileSrc);
            String fileTrg = directory+ Strings.DEFAULT_SCHEMAS[22][0];
            int trgId = importer.getSourceId(Strings.DEFAULT_SCHEMAS[22][1], fileTrg);
            parser.loadMatchResultFile(directory+Strings.MATCHRESULT_ANATOMY,
                    srcId, trgId, graphState);
            parser.loadMatchResultFile(directory+Strings.MATCHRESULT_ANATOMY_PARTIAL,
                    srcId, trgId, graphState);
        }
        return status;
    }


    /*
     * import default Matchresults to database
     */
    public void importDefaultMatchresults(boolean[] _matchresults, String directory) {
        String status = parseDefaultMatchresults(_matchresults, directory, manager);

        updateAll(true); //no parse or import of new sources
        if (mainWindow!=null){
            getManagementPane().setSelectedTab(ManagementPane.REPOSITORY);
            setStatus(status);
        }
    }

    /*
     * given one or more filnames... load all of the matchresults to the DB ONLY FOR
     * RECOVER USE - NOT IN THE GUI
     */
    public void loadMatchresultFromFile(String[] _fileNames, Manager manager) {
        if (_fileNames == null) {
            return;
        }
        try {
            MatchResultParser parser = new MatchResultParser(manager);
            for (int i = 0; i < _fileNames.length; i++) {
                if (_fileNames[i] != null) {
                    System.out.println(_fileNames[i]);
//					ArrayList list = loadMatchResultFile(_fileNames[i]);
                    ArrayList<MatchResult> list = parser.loadMatchResultFile(_fileNames[i]);
                    //System.out.println(list);
                    if (list != null) {
                        for (int j = 0; j < list.size(); j++) {
                            MatchResult matchResult = list.get(j);
//							if (matchResult.getName() == null) {
//								String name = matchResult.getDescription();
//								name = name.replaceAll(GUIConstants.COLON,
//										GUIConstants.UNDERSCORE);
//								name = name.replaceAll(GUIConstants.DASH,
//										GUIConstants.UNDERSCORE);
//								name = name.replaceAll(GUIConstants.SMALLER,
//										GUIConstants.EMPTY);
//								name = name.replaceAll(GUIConstants.BIGGER,
//										GUIConstants.EMPTY);
//								name = name.replaceAll(GUIConstants.UNDERSCORE
//										+ GUIConstants.UNKNOWN, GUIConstants.EMPTY);
//								matchResult.setName(name);
//							}
                            importer.saveMatchResult(matchResult);
                        }
                    }
                }
            }
        } catch (Exception x) {
            System.out.println(x.getMessage());
//			setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);
        }
    }

    public void exportXSLTrightToLeft(boolean strict){
        setStatus("creating Right to Left XSLT");

        try {
            JFileChooser chooser = new JFileChooser();
            final FileFilter ftXSLT = new FileNameExtensionFilter("XSLT FILES (.xslt)", "xslt");
            chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
            chooser.addChoosableFileFilter(ftXSLT);
            chooser.setCurrentDirectory(defaultDirectory);
            int returnVal = chooser.showSaveDialog(mainWindow);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                defaultDirectory = chooser.getCurrentDirectory();
                String _name = chooser.getSelectedFile().toString();
                if (_name != null) {
                    if (guiMatchresult.containsMatchResult()) {

                        TextFrame wnd = new TextFrame(guiMatchresult.getMatchResult(),
                                _name+".xslt", TextFrame.RIGHT_TO_LEFT_XSLT, MainWindow.DIM_LARGE, _name+".xslt",this,strict);
                        wnd.setLocation(position);
                        wnd.setVisible(true);
                    } else {
                        setStatus(GUIConstants.NO_MR);

                    }
                    setStatus(GUIConstants.FILE_SAVED + _name+".xslt");
                } else
                    setStatus(GUIConstants.CANCELED);
            } else
                setStatus(GUIConstants.CANCELED);
        } catch (Exception x) {
            setStatus(x.getMessage());
            setStatus(GUIConstants.MATCHRESULT_SAVED_FILE_ERROR);
            x.printStackTrace();
        }
    }

    public void exportXSLTLeftToRight(boolean strict){
        setStatus("creating Left to Right XSLT");
        try {
            JFileChooser chooser = new JFileChooser();
            final FileFilter ftXSLT = new FileNameExtensionFilter("XSLT FILES (.xslt)", "xslt");
            chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
            chooser.addChoosableFileFilter(ftXSLT);
            chooser.setCurrentDirectory(defaultDirectory);
            int returnVal = chooser.showSaveDialog(mainWindow);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                defaultDirectory = chooser.getCurrentDirectory();
                String _name = chooser.getSelectedFile().toString();
                if (_name != null) {
                    if (guiMatchresult.containsMatchResult()) {

                        TextFrame wnd = new TextFrame(guiMatchresult.getMatchResult(),
                                _name+".xslt", TextFrame.LEFT_TO_RIGHT_XSLT, MainWindow.DIM_LARGE, _name+".xslt",this,strict);
                        wnd.setLocation(position);
                        wnd.setVisible(true);
                    } else {
                        setStatus(GUIConstants.NO_MR);

                    }
                    setStatus(GUIConstants.FILE_SAVED + _name+".xslt");
                } else
                    setStatus(GUIConstants.CANCELED);
            } else
                setStatus(GUIConstants.CANCELED);
        } catch (Exception x) {
            setStatus(x.getMessage());
            setStatus(GUIConstants.MATCHRESULT_SAVED_FILE_ERROR);
            x.printStackTrace();
        }
    }


    public void showGEontologyList(){
        setStatus("listing available ontologies");

        try {
            String[] ontologies = SemanticRepoUtilities.getOntologyList();
            String input = (String) JOptionPane.showInputDialog(null, "Select ontology to import",
                    "Import from Ontology Repository", JOptionPane.QUESTION_MESSAGE, null, // Use

                    ontologies, // Array of choices
                    ontologies[0]);
            if (input!=null) {

                try {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(defaultDirectory);
                    int returnVal = chooser.showSaveDialog(mainWindow);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        defaultDirectory = chooser.getCurrentDirectory();
                        String _name = chooser.getSelectedFile().toString();
                        if (_name != null) {
                            SemanticRepoUtilities.downloadOntology(input,_name);
                            setStatus("Ontology saved.");
                        } else
                            setStatus(GUIConstants.CANCELED);
                    } else
                        setStatus(GUIConstants.CANCELED);
                } catch (Exception x) {
                    setStatus(x.getMessage());
                    setStatus("Error saving ontology.");
                    x.printStackTrace();
                }

            }
            else
                setStatus("No ontology was selected");
        } catch (Exception x) {
            setStatus(x.getMessage());
            setStatus("Requested operation could not be completed.");
        }
    }

    public void exportTriplesLocally(){

        setStatus("Save matching to file");
        String titel = GUIConstants.CHOOSE_MATCHRESULT_SAVE;
        ArrayList selectedRel = LoadFromDBThread.getAllDBMatchresults(this);
        if (selectedRel == null) {
            setStatus(GUIConstants.NO_MATCHRESULT_IN_DB);
        }
        Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                titel, selectedRel, MainWindow.DIM_SMALL,
                Dlg_ChooseFromList.SAVE_MATCHRESULT_FILE,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        dialog.setLocation(position);
        Object obj = dialog.showDialog();
        if (obj != null) {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(defaultDirectory);
                int returnVal = chooser.showSaveDialog(mainWindow);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    defaultDirectory = chooser.getCurrentDirectory();
                    String _name = chooser.getSelectedFile().toString();
                    if (_name != null) {
                        if (obj instanceof SourceRelationship) {
                            SourceRelationship rel = (SourceRelationship) obj;
                            TriplesExporter texporter = new TriplesExporter(rel,Controller.this);
                            String context = texporter.createTriplets();
                            PrintWriter output =new PrintWriter(new FileWriter(_name));
                            output.append(context.toString());
                            output.close();
                        } else if (obj instanceof ArrayList) {
                            ArrayList rels = (ArrayList) obj;
                            SourceRelationship rel = (SourceRelationship) rels.get(0);
                            TriplesExporter texporter = new TriplesExporter(rel,Controller.this);
                            String context = texporter.createTriplets();
                            PrintWriter output =new PrintWriter(new FileWriter(_name));
                            output.append(context.toString());
                            output.close();
                        }


                    } else
                        setStatus(GUIConstants.MATCHRESULT_NOT_SAVED_NO_FILE);
                } else
                    setStatus(GUIConstants.MATCHRESULT_NOT_SAVED_NO_FILE);
            } catch (Exception x) {
                setStatus(x.getMessage());
                setStatus(GUIConstants.MATCHRESULT_SAVED_FILE_ERROR);
                x.printStackTrace();
            }
        } else
            setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);


        ///////////

    }

    public void uploadOntology(){
        JFileChooser chooser = new JFileChooser();
        final FileFilter ftOWL = new FileNameExtensionFilter("OWL FILES (.owl)", "owl");
        final FileFilter ftRDF = new FileNameExtensionFilter("RDF FILES (.rdf)", "rdf");
//        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.addChoosableFileFilter(ftOWL);
        chooser.addChoosableFileFilter(ftRDF);
        chooser.setCurrentDirectory(defaultDirectory);
        int returnVal = chooser.showOpenDialog(mainWindow);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            defaultDirectory = chooser.getCurrentDirectory();
            String _name = chooser.getSelectedFile().toString();
            if (_name != null) {
                setStatus(_name);
                String inputName = JOptionPane.showInputDialog(null,"Ontology name to be used in semantic repository:");
                SemanticRepoUtilities.uploadOntology(inputName,_name);
            } else
                setStatus(GUIConstants.CANCELED);
        } else
            setStatus(GUIConstants.CANCELED);
    }


    public void createWorkspace(){
        setStatus("Create Workspace");


        try {
            final JFrame insideFrame = new JFrame();
            insideFrame.setTitle("Create Workspace");
            JSplitPane splitPane;

            JLabel label1 = new JLabel("Select Ontologies");
            JLabel label2 = new JLabel("Select MatchResults");
            JLabel label3 = new JLabel("Select name for the workspace      ");
            JLabel descriptionLabel = new JLabel("Provide short description for workspace:");
            final JTextField descriptionText = new JTextField();
            final JTextField textField = new JTextField();
            textField.setEditable(true);
            JPanel panel1 = new JPanel(new BorderLayout());
            JPanel panel2 = new JPanel(new BorderLayout());
            JPanel panel3 = new JPanel(new BorderLayout());
            JPanel panel4 = new JPanel(new BorderLayout());
            JPanel panel6 = new JPanel(new BorderLayout());
            JPanel panel7 = new JPanel(new BorderLayout());
            JPanel descriptionPanel = new JPanel(new BorderLayout());

            ArrayList<SourceRelationship>  repMatchResults;
            repMatchResults=LoadFromDBThread.getAllDBMatchresults(this);

            final CheckBoxList list_RepSchemas=new CheckBoxList();
            list_RepSchemas.setListData(SemanticRepoUtilities.getOntologyListWithVersions());

            final CheckBoxList list_RepMatchResults=new CheckBoxList();
            if (repMatchResults==null)  {
                throw new Exception("There are no matching results saved in repository.");
            }

            list_RepMatchResults.setListData(repMatchResults.toArray());

            JScrollPane listScrollPane_RepMatchResults = new JScrollPane(list_RepMatchResults);
            JScrollPane listScrollPane_RepSchemas = new JScrollPane(list_RepSchemas);
//            listScrollPane_RepMatchResults.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR);
//            listScrollPane_RepMatchResults.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//            listScrollPane_RepSchemas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//            listScrollPane_RepSchemas.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


            JButton button = new JButton("Create Workspace");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (textField.getText().trim().length()!=0) {
                        if (descriptionText.getText().trim().length()!=0) {
                            String wsName = textField.getText().trim();
//                            SASresponse createdWS = SemanticRepoUtilities.createWorkspace(wsName,descriptionText.getText().trim());
                            SASresponse createdWS = new SASresponse(true);
                            if (createdWS.isSuccess()){
                                SemanticWorkspace createdWorkspace = new SemanticWorkspace(wsName);
                                JTextArea ar=new JTextArea("Workspace "+textField.getText()+" was created.");
                                ar.setOpaque(false);
                                ar.setEditable(false);
                                ar.setFont(ar.getFont().deriveFont(12f));
                                JOptionPane.showMessageDialog(insideFrame, ar, "Workspace Successfully Created", JOptionPane.PLAIN_MESSAGE);

                                int[] selectedOntologies = list_RepSchemas.getCheckedIndices();
                                for(int ind=0;ind<selectedOntologies.length;ind++){
//                                    createdWorkspace.loadOntology((String)list_RepSchemas.getModel().getElementAt(selectedOntologies[ind]));
                                    System.out.println(list_RepSchemas.getModel().getElementAt(selectedOntologies[ind]));
                                }
                                int[] selectedMatchResults = list_RepMatchResults.getCheckedIndices();
                                for(int ind=0;ind<selectedMatchResults.length;ind++){
                                    TriplesExporter texporter = new TriplesExporter((SourceRelationship) list_RepMatchResults.getModel().getElementAt(selectedMatchResults[ind]),Controller.this);
                                    String context = texporter.createTriplets();
                                    if (ind==0)
                                        createdWorkspace.createContext(context);
                                    else
                                        createdWorkspace.loadContext(context);
                                }
                                insideFrame.dispose();
                            }

                            else{
                                JTextArea ar=new JTextArea("There was an error creating the workspace. Please ensure there does not already exist a workspace with this name.");
                                ar.setOpaque(false);
                                ar.setEditable(false);
                                ar.setFont(ar.getFont().deriveFont(12f));
                                JOptionPane.showMessageDialog(insideFrame, ar, "Workspace was not created", JOptionPane.ERROR_MESSAGE);


                            }
                        }
                        else{
                            JTextArea ar=new JTextArea("Please provide a workspace description.");
                            ar.setOpaque(false);
                            ar.setEditable(false);
                            ar.setFont(ar.getFont().deriveFont(12f));
                            JOptionPane.showMessageDialog(insideFrame, ar, "Workspace was not created", JOptionPane.ERROR_MESSAGE);

                        }

                    }
                    else{
                        JTextArea ar=new JTextArea("Workspace name cannot be null.");
                        ar.setOpaque(false);
                        ar.setEditable(false);
                        ar.setFont(ar.getFont().deriveFont(12f));
                        JOptionPane.showMessageDialog(insideFrame, ar, "Workspace was not created", JOptionPane.ERROR_MESSAGE);

//                        insideFrame.dispose();
                    }
                }
            });

            panel1.add(label1,BorderLayout.NORTH);
            panel1.add(listScrollPane_RepSchemas,BorderLayout.CENTER);
            panel2.add(label2,BorderLayout.NORTH);
            panel2.add(listScrollPane_RepMatchResults,BorderLayout.CENTER);
            panel3.add(label3,BorderLayout.WEST);
            panel3.add(textField,BorderLayout.CENTER);
            descriptionPanel.add(descriptionLabel,BorderLayout.WEST);
            descriptionPanel.add(descriptionText,BorderLayout.CENTER);
            JPanel workspaceInfoPanel = new JPanel(new BorderLayout());
            workspaceInfoPanel.add(panel3,BorderLayout.CENTER);
            workspaceInfoPanel.add(descriptionPanel,BorderLayout.SOUTH);

            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panel1,panel2);
            splitPane.setDividerLocation(200);
            splitPane.setResizeWeight(0.5);
            panel4.add(splitPane, BorderLayout.CENTER);
            panel4.add(workspaceInfoPanel, BorderLayout.NORTH);
            panel7.add(button);
            panel6.add(panel4,BorderLayout.CENTER);
            panel6.add(panel7,BorderLayout.SOUTH);

            insideFrame.getContentPane().add(panel6, BorderLayout.CENTER);
            insideFrame.setLocation(position);
            insideFrame.setSize(400,400);
            insideFrame.setVisible(true);



        } catch (Exception x) {
            if ((x.getMessage().equals("There are no ontologies saved in repository."))||(x.getMessage().equals("There are no matching results saved in repository.")))
                JOptionPane.showMessageDialog(null,
                        x.getMessage(),
                        "Operation not completed",
                        JOptionPane.ERROR_MESSAGE);
            setStatus("Operation not completed.");
        }
    }


    public void existingWorkspaceQuery(){
        setStatus("Cross-Ontology queries");

        try {
            String[] workspaces = SemanticRepoUtilities.getWorkspaceList();
            String inputWSname = (String) JOptionPane.showInputDialog(null, "Select WorkSpace",
                    "Available Semantic Workspaces", JOptionPane.QUESTION_MESSAGE, null, // Use
                    // default
                    // icon
                    workspaces, // Array of choices
                    workspaces[0]);
            setStatus("Workspace "+inputWSname+ " was selected.");
            if (!inputWSname.equals("null"))
            try {
                String[] ontologiesWS;
                final SemanticWorkspace semWS = new SemanticWorkspace(inputWSname);
                ontologiesWS = semWS.getOntologyList();
                JList list_ontologies=new JList(ontologiesWS);
                JScrollPane listScrollPane_ontologies = new JScrollPane(list_ontologies);
                listScrollPane_ontologies.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                listScrollPane_ontologies.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                JLabel label = new JLabel("list of ontologies included in workspace");

                JFrame insideFrame = new JFrame();
                insideFrame.setTitle(inputWSname);
                JSplitPane splitPane2;
                JSplitPane splitPane3;

                JPanel panel2 = new JPanel(new BorderLayout());
                JPanel panel3 = new JPanel(new BorderLayout());
                JPanel panel4 = new JPanel(new BorderLayout());
                JPanel panel5 = new JPanel(new BorderLayout());
                JPanel panel6 = new JPanel(new BorderLayout());
                JPanel panel7 = new JPanel(new BorderLayout());
                JPanel panel8 = new JPanel(new BorderLayout());
                final JTextArea query = new JTextArea("Write your SPARQL query here");
                query.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        query.selectAll();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                final JTextArea result = new JTextArea("Query Result");
                JButton button = new JButton("Press here to execute query");
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
//                        result.setText("result of query will appear here");
                        result.setText(semWS.queryWorkspace(query.getText()));
                    }
                });
                panel5.add(button);
                JScrollPane queryScroll = new JScrollPane(query);
                panel2.add(queryScroll);
                JScrollPane resultScroll = new JScrollPane(result);
                panel3.add(resultScroll);
                panel7.add(listScrollPane_ontologies,BorderLayout.CENTER);
                panel7.add(label,BorderLayout.NORTH);
                splitPane2=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panel2,panel3);
//                splitPane2.setDividerLocation(350);
                splitPane2.setResizeWeight(0.5);
                panel4.add(splitPane2);
                splitPane3=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panel7,panel4);
                splitPane3.setResizeWeight(0.1);
                splitPane3.setDividerLocation(100);
                panel8.add(splitPane3);
                panel6.add(panel8,BorderLayout.CENTER);
                panel6.add(panel5, BorderLayout.SOUTH);
                insideFrame.getContentPane().add(panel6, BorderLayout.CENTER);
                insideFrame.setLocation(position);
                insideFrame.setSize(400, 400);
                insideFrame.setVisible(true);



            } catch (Exception x) {
                setStatus(x.getMessage());
                setStatus("Requested operation could not be completed.");
            }

        } catch (Exception x) {
            setStatus(x.getMessage());
            setStatus("Requested operation could not be completed.");

        }


    }

    public void configureWorkspace(){
        setStatus("configure workspace");

        try {
            String[] workspaces = SemanticRepoUtilities.getWorkspaceList();
            String inputWSname = (String) JOptionPane.showInputDialog(null, "Select WorkSpace",
                    "Available Semantic Workspaces", JOptionPane.QUESTION_MESSAGE, null, // Use
                    // default
                    // icon
                    workspaces, // Array of choices
                    workspaces[0]);
            setStatus("Workspace "+inputWSname+ " was selected.");
            if (!inputWSname.equals("null"))
                try {
                    String[] ontologiesWS;
                    final SemanticWorkspace semWS = new SemanticWorkspace(inputWSname);
                    ontologiesWS = semWS.getOntologyList();
                    JList list_ontologies=new JList(ontologiesWS);
                    JScrollPane listScrollPane_ontologies = new JScrollPane(list_ontologies);
                    listScrollPane_ontologies.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    listScrollPane_ontologies.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                    JLabel label = new JLabel("list of ontologies included in workspace");

                    final JFrame insideFrame = new JFrame();
                    insideFrame.setTitle("Create Workspace");
                    JSplitPane splitPane;

                    JLabel label1 = new JLabel("Select Ontologies");
                    JLabel label2 = new JLabel("Select MatchResults");
                    JLabel label3 = new JLabel("Select name for the workspace      ");
                    JLabel descriptionLabel = new JLabel("Provide short description for workspace:");
                    final JTextField descriptionText = new JTextField();
                    final JTextField textField = new JTextField();
                    textField.setEditable(true);
                    JPanel panel1 = new JPanel(new BorderLayout());
                    JPanel panel2 = new JPanel(new BorderLayout());
                    JPanel panel3 = new JPanel(new BorderLayout());
                    JPanel panel4 = new JPanel(new BorderLayout());
                    JPanel panel6 = new JPanel(new BorderLayout());
                    JPanel panel7 = new JPanel(new BorderLayout());
                    JPanel descriptionPanel = new JPanel(new BorderLayout());

                    ArrayList<SourceRelationship>  repMatchResults;
                    repMatchResults=LoadFromDBThread.getAllDBMatchresults(this);

                    final CheckBoxList list_RepSchemas=new CheckBoxList();
                    list_RepSchemas.setListData(SemanticRepoUtilities.getOntologyListWithVersions());

                    final CheckBoxList list_RepMatchResults=new CheckBoxList();
                    if (repMatchResults==null)  {
                        throw new Exception("There are no matching results saved in repository.");
                    }

                    list_RepMatchResults.setListData(repMatchResults.toArray());

                    JScrollPane listScrollPane_RepMatchResults = new JScrollPane(list_RepMatchResults);
                    JScrollPane listScrollPane_RepSchemas = new JScrollPane(list_RepSchemas);
//            listScrollPane_RepMatchResults.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR);
//            listScrollPane_RepMatchResults.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//            listScrollPane_RepSchemas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//            listScrollPane_RepSchemas.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


                    JButton button = new JButton("Create Workspace");
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (textField.getText().trim().length()!=0) {
                                if (descriptionText.getText().trim().length()!=0) {
                                    String wsName = textField.getText().trim();
//                            SASresponse createdWS = SemanticRepoUtilities.createWorkspace(wsName,descriptionText.getText().trim());
                                    SASresponse createdWS = new SASresponse(true);
                                    if (createdWS.isSuccess()){
                                        SemanticWorkspace createdWorkspace = new SemanticWorkspace(wsName);
                                        JTextArea ar=new JTextArea("Workspace "+textField.getText()+" was created.");
                                        ar.setOpaque(false);
                                        ar.setEditable(false);
                                        ar.setFont(ar.getFont().deriveFont(12f));
                                        JOptionPane.showMessageDialog(insideFrame, ar, "Workspace Successfully Created", JOptionPane.PLAIN_MESSAGE);

                                        int[] selectedOntologies = list_RepSchemas.getCheckedIndices();
                                        for(int ind=0;ind<selectedOntologies.length;ind++){
//                                    createdWorkspace.loadOntology((String)list_RepSchemas.getModel().getElementAt(selectedOntologies[ind]));
                                            System.out.println(list_RepSchemas.getModel().getElementAt(selectedOntologies[ind]));
                                        }
                                        int[] selectedMatchResults = list_RepMatchResults.getCheckedIndices();
                                        for(int ind=0;ind<selectedMatchResults.length;ind++){
                                            TriplesExporter texporter = new TriplesExporter((SourceRelationship) list_RepMatchResults.getModel().getElementAt(selectedMatchResults[ind]),Controller.this);
                                            String context = texporter.createTriplets();
                                            if (ind==0)
                                                createdWorkspace.createContext(context);
                                            else
                                                createdWorkspace.loadContext(context);
                                        }
                                        insideFrame.dispose();
                                    }

                                    else{
                                        JTextArea ar=new JTextArea("There was an error creating the workspace. Please ensure there does not already exist a workspace with this name.");
                                        ar.setOpaque(false);
                                        ar.setEditable(false);
                                        ar.setFont(ar.getFont().deriveFont(12f));
                                        JOptionPane.showMessageDialog(insideFrame, ar, "Workspace was not created", JOptionPane.ERROR_MESSAGE);


                                    }
                                }
                                else{
                                    JTextArea ar=new JTextArea("Please provide a workspace description.");
                                    ar.setOpaque(false);
                                    ar.setEditable(false);
                                    ar.setFont(ar.getFont().deriveFont(12f));
                                    JOptionPane.showMessageDialog(insideFrame, ar, "Workspace was not created", JOptionPane.ERROR_MESSAGE);

                                }

                            }
                            else{
                                JTextArea ar=new JTextArea("Workspace name cannot be null.");
                                ar.setOpaque(false);
                                ar.setEditable(false);
                                ar.setFont(ar.getFont().deriveFont(12f));
                                JOptionPane.showMessageDialog(insideFrame, ar, "Workspace was not created", JOptionPane.ERROR_MESSAGE);

//                        insideFrame.dispose();
                            }
                        }
                    });

                    panel1.add(label1,BorderLayout.NORTH);
                    panel1.add(listScrollPane_RepSchemas,BorderLayout.CENTER);
                    panel2.add(label2,BorderLayout.NORTH);
                    panel2.add(listScrollPane_RepMatchResults,BorderLayout.CENTER);
                    panel3.add(label3,BorderLayout.WEST);
                    panel3.add(textField,BorderLayout.CENTER);
                    descriptionPanel.add(descriptionLabel,BorderLayout.WEST);
                    descriptionPanel.add(descriptionText,BorderLayout.CENTER);
                    JPanel workspaceInfoPanel = new JPanel(new BorderLayout());
                    workspaceInfoPanel.add(panel3,BorderLayout.CENTER);
                    workspaceInfoPanel.add(descriptionPanel,BorderLayout.SOUTH);

                    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panel1,panel2);
                    splitPane.setDividerLocation(200);
                    splitPane.setResizeWeight(0.5);
                    panel4.add(splitPane, BorderLayout.CENTER);
                    panel4.add(workspaceInfoPanel, BorderLayout.NORTH);
                    panel7.add(button);
                    panel6.add(panel4,BorderLayout.CENTER);
                    panel6.add(panel7,BorderLayout.SOUTH);

                    insideFrame.getContentPane().add(panel6, BorderLayout.CENTER);
                    insideFrame.setLocation(position);
                    insideFrame.setSize(400,400);
                    insideFrame.setVisible(true);




                } catch (Exception x) {
                    setStatus(x.getMessage());
                    setStatus("Requested operation could not be completed.");
                }

        } catch (Exception x) {
            setStatus(x.getMessage());
            setStatus("Requested operation could not be completed.");

        }


    }

    public void saveMatchresultToFile() {
        setStatus(GUIConstants.SAVE_MATCHRESULT_FILE);
        String titel = GUIConstants.CHOOSE_MATCHRESULT_SAVE;
        ArrayList selectedRel = LoadFromDBThread.getAllDBMatchresults(this);
        if (selectedRel == null) {
            setStatus(GUIConstants.NO_MATCHRESULT_IN_DB);
        }
        Dlg_ChooseFromList dialog = new Dlg_ChooseFromList(mainWindow,
                titel, selectedRel, MainWindow.DIM_SMALL,
                Dlg_ChooseFromList.SAVE_MATCHRESULT_FILE,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        dialog.setLocation(position);
        Object obj = dialog.showDialog();
        if (obj != null) {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(defaultDirectory);
                int returnVal = chooser.showSaveDialog(mainWindow);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    defaultDirectory = chooser.getCurrentDirectory();
                    String _name = chooser.getSelectedFile().toString();
                    if (_name != null) {
                        MatchResultExport export = new MatchResultExport(manager);
                        if (obj instanceof SourceRelationship) {
                            SourceRelationship rel = (SourceRelationship) obj;
                            export.saveMatchResultFile(rel, _name);
                        } else if (obj instanceof ArrayList) {
                            ArrayList rels = (ArrayList) obj;
                            for (int i = 0; i < rels.size(); i++) {
                                SourceRelationship rel = (SourceRelationship) rels.get(i);
                                export.saveMatchResultFile(rel,	_name);
                            }
                        }
                        setStatus(GUIConstants.MATCHRESULT_SAVED_FILE + _name);
                    } else
                        setStatus(GUIConstants.MATCHRESULT_NOT_SAVED_NO_FILE);
                } else
                    setStatus(GUIConstants.MATCHRESULT_NOT_SAVED_NO_FILE);
            } catch (Exception x) {
                setStatus(x.getMessage());
                setStatus(GUIConstants.MATCHRESULT_SAVED_FILE_ERROR);
                x.printStackTrace();
            }
        } else
            setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);
    }

    /*
     * save the current Matchresult to a textfile (by saving the match results
     * between the loaded source and target schema)
     */
    public void saveMatchresultToFile(MatchResult _matchResult, boolean _question) {
        //MatchResult _matchResult = matchresult.getMatchResult();
        if (_matchResult != null) {
            //			if (!preprocessing.equals(GUIConstants.PREP_REDUCED))
            //				// matchResult =
            //				// manager.validateSimplifiedMatchResult(matchResult);
            //				matchResult = Manager.transformMatchResult(matchResult,
            //						Graph.GRAPH_STATE_REDUCED);
            setStatus(GUIConstants.SAVE_MATCHRESULT_FILE);
            try {
                if (_question) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(defaultDirectory);
                    int returnVal = chooser.showSaveDialog(mainWindow);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        defaultDirectory = chooser.getCurrentDirectory();
                        String _name = chooser.getSelectedFile().toString();
                        if (_name != null) {
                            MatchResultExport.saveMatchResultFile(_matchResult, _name);
                            setStatus(GUIConstants.MATCHRESULT_SAVED_FILE + _name);
                        } else {
                            setStatus(GUIConstants.MATCHRESULT_NOT_SAVED_NO_FILE);
                        }
                    } else {
                        setStatus(GUIConstants.MATCHRESULT_NOT_SAVED_NO_FILE);
                    }
                } else {
                    String _name = ".//Sources//Internal//"
                            + _matchResult.getName() + ".txt";
                    MatchResultExport.saveMatchResultFile(_matchResult, _name);
                }
            } catch (Exception x) {
                setStatus(x.getMessage());
                setStatus(GUIConstants.MATCHRESULT_SAVED_FILE_ERROR);
                x.printStackTrace();
            }
        } else
            setStatus(GUIConstants.NO_MATCHRESULT);
    }

    public void saveMatchresultToFile(ArrayList _matchResults) {
        if (!(_matchResults == null || _matchResults.isEmpty())) {
            //          if (!preprocessing.equals(GUIConstants.PREP_REDUCED))
            //              // matchResult =
            //              // manager.validateSimplifiedMatchResult(matchResult);
            //              matchResult = Manager.transformMatchResult(matchResult,
            //                      Graph.GRAPH_STATE_REDUCED);
            setStatus(GUIConstants.SAVE_MATCHRESULT_FILE);
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(defaultDirectory);
                int returnVal = chooser.showSaveDialog(mainWindow);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    defaultDirectory = chooser.getCurrentDirectory();
                    String _name = chooser.getSelectedFile().toString();
                    if (_name != null) {
                        for (int i = 0; i < _matchResults.size(); i++) {
//							MatchResult result = (MatchResult) _matchResults
//									.get(i);
//							manager.saveMatchResultFile(result, _name);
                        }
                        setStatus(GUIConstants.MATCHRESULT_SAVED_FILE + _name);
                    } else {
                        setStatus(GUIConstants.MATCHRESULT_NOT_SAVED_NO_FILE);
                    }
                } else {
                    setStatus(GUIConstants.MATCHRESULT_NOT_SAVED_NO_FILE);
                }
            } catch (Exception x) {
                setStatus(x.getMessage());
                setStatus(GUIConstants.MATCHRESULT_SAVED_FILE_ERROR);
                x.printStackTrace();
            }
        } else
            setStatus(GUIConstants.NO_MATCHRESULT);
    }

    /*
     * deletes the current used database and creates a new one (inclusive
     * Systemt Matcher) - all saved matchresults will be deleted
     */
    public void createNewDatabase(boolean _question) {
        int res;
        if (_question) {
            res = JOptionPane.showConfirmDialog(mainWindow,
                    GUIConstants.NEW_DB, GUIConstants.WARNING,
                    JOptionPane.OK_CANCEL_OPTION);
        } else {
            res = JOptionPane.OK_OPTION;
        }
        if (res != JOptionPane.OK_OPTION) {
            setStatus(GUIConstants.DEL_DB_ABORTED);
            return;
        }
        if (mainWindow!=null){
            closeSchema(true, false);
            closeSchema(false, false);
            mainWindow.getNewContentPane().setProgressBar(true);
        }
//		accessor.dropViewTables(false);
//		accessor.deleteSourcePaths();
        DataImport importer = new DataImport();
        importer.dropRepositorySchema();

        importer.createRepositorySchema();
        ListParser parser = new ListParser(true);
        parser.parseAbbreviation(file_abb);
        parser.parseSynonym(file_syn);
        //			manager.importAllSources();
//		manager.saveSystemMatcherLibrary();
        // load the Respository new
        // the loaded Schemas and created Matchresults remain in the
        // mainwindow
        //updateAllOld(true, true);
        importDefaultVariables();
        updateAll(true); //parse and import synonym info
        //			manager = new Manager();
        //			manager.loadRepository(true);
        //			mainWindow.setController(this);
        if (mainWindow!=null){
            mainWindow.getNewContentPane().setProgressBar(false);
            setStatus(GUIConstants.DEL_DB_DONE);
        }
    }



    /*
     * import all default workflow variables
     */
    public void importDefaultVariables() {
        if (mainWindow!=null){
            mainWindow.getNewContentPane().setProgressBar(true);
        }

        manager.getAccessor().emptyWorkflow();
        Workflow.insertDefaults(manager.getImporter());
        MappingReuse.insertDefaults(manager.getImporter());
//		manager.saveSystemMatcherLibrary();
//		manager.reloadManager();
        // load the Respository new, the loaded Schemas
        // and created Matchresults remain in the mainwindow
        updateAll(false); //no parse or import
        //			manager = new Manager();
        //			manager.loadRepository(true);
        //			mainWindow.setController(this);
        if (mainWindow!=null){
            mainWindow.getNewContentPane().setProgressBar(false);
            setStatus(GUIConstants.IMPORT_MATCHER_DONE);
        }
    }

    public void importDefaultSchemas(boolean[] _schemas) {
        importDefaultSchemas(_schemas, null);
    }


    public void parseDefaultSchemas(boolean[] _schemas, String directory){
        String domain = "PurchaseOrder";
        long start = System.currentTimeMillis();
        //XSD Schemas
        boolean dbInsert = true;
        XSDParser xsdParser = new XSDParser(dbInsert);
        XDRParser xdrParser = new XDRParser(dbInsert);
        OWLParser_V3 owlParser = new OWLParser_V3(dbInsert);

        File file = new File(".");
//		String dir = null; 
        String dir = directory;
        if (dir==null){
            System.out.println(file.getAbsolutePath());
            dir = file.getAbsolutePath().replace("\\", "/");
            dir = dir.substring(0, dir.length()-2)+ GUIConstants.BACKSLASH+ InsertParser.SOURCE_DIR+ GUIConstants.BACKSLASH;
        }
        dir = "file:/" + dir;

        if (_schemas[0]) {// "BMECat"
//			XSDParser.parseMultipleXSD(directory
//					+ GUIConstants.DEFAULT_SCHEMAS[0][0],
//					GUIConstants.DEFAULT_SCHEMAS[0][1],
//					null, domain, null, null);			
            xsdParser.parseMultipleSources(directory + Strings.DEFAULT_SCHEMAS[0][0],
                    null, null, domain, null, null);
//			XSDParser.parseCompositeXSD(directory
//					+ GUIConstants.DEFAULT_SCHEMAS[1][0],
//					GUIConstants.DEFAULT_SCHEMAS[1][1],
//					null, domain, null, null);
            xsdParser.parseCompositeSources(directory + Strings.DEFAULT_SCHEMAS[1][0],
                    Strings.DEFAULT_SCHEMAS[1][1],null, domain, null, null);
            //XSDParser.parseMultipleXSD(InsertParser.SOURCE_DIR +
            // GUIConstants.SLASH
            //		+ GUIConstants.DEFAULT_SCHEMAS[2][0], GUIConstants.DEFAULT_SCHEMAS[2][1]);
        }
        if (_schemas[1]) { // "OpenTrans"
//			XSDParser.parseMultipleXSD(directory
//					+ GUIConstants.DEFAULT_SCHEMAS[3][0],
//					GUIConstants.DEFAULT_SCHEMAS[3][1],
//					null, domain, null, null);
            xsdParser.parseMultipleSources(directory+ Strings.DEFAULT_SCHEMAS[3][0],
                    null, null, domain, null, null);
//			XSDParser.parseCompositeXSD(directory
//					+ GUIConstants.DEFAULT_SCHEMAS[4][0],
//					GUIConstants.DEFAULT_SCHEMAS[4][1],
//					null, domain, null, null);
            xsdParser.parseCompositeSources(directory + Strings.DEFAULT_SCHEMAS[4][0],
                    Strings.DEFAULT_SCHEMAS[4][1], null, domain, null, null);
            //XSDParser.parseMultipleXSD(InsertParser.SOURCE_DIR +
            // GUIConstants.SLASH
            //		+ GUIConstants.DEFAULT_SCHEMAS[5][0], GUIConstants.DEFAULT_SCHEMAS[5][1]);
        }
        if (_schemas[2]) { // "Xcbl"
//			XSDParser.parseCompositeXSD(directory
//					+ GUIConstants.DEFAULT_SCHEMAS[6][0],
//					GUIConstants.DEFAULT_SCHEMAS[6][1],
//					null, domain, null, null);
//			XSDParser.parseCompositeXSD(directory
//					+ GUIConstants.DEFAULT_SCHEMAS[7][0],
//					GUIConstants.DEFAULT_SCHEMAS[7][1],
//					null, domain, null, null);
//			XSDParser.parseCompositeXSD(directory
//					+ GUIConstants.DEFAULT_SCHEMAS[8][0],
//					GUIConstants.DEFAULT_SCHEMAS[8][1],
//					null, domain, null, null);
            xsdParser.parseCompositeSources(directory + Strings.DEFAULT_SCHEMAS[6][0],
                    Strings.DEFAULT_SCHEMAS[6][0], null, domain, null, null);
            xsdParser.parseCompositeSources(directory + Strings.DEFAULT_SCHEMAS[7][0],
                    Strings.DEFAULT_SCHEMAS[7][0], null, domain, null, null);
            xsdParser.parseCompositeSources(directory + Strings.DEFAULT_SCHEMAS[8][0],
                    Strings.DEFAULT_SCHEMAS[8][0], null, domain, null, null);
        }
        //XDR Schemas
        if (_schemas[3]) { // "Xcbl35"
//			XDRParser.parseMultipleXDR(directory
//					+ GUIConstants.DEFAULT_SCHEMAS[9][0],
//					GUIConstants.DEFAULT_SCHEMAS[9][1],
//					null, domain, null, null);
            xdrParser.parseMultipleSources(directory + Strings.DEFAULT_SCHEMAS[9][0],
                    null, null, domain, null, null);
        }
        if (_schemas[4]) {// "PO"
//			XDRParser.parseMultipleXDR(directory
//					+ GUIConstants.DEFAULT_SCHEMAS[10][0],
//					GUIConstants.DEFAULT_SCHEMAS[10][1],
//					null, domain, null, null);
            xdrParser.parseMultipleSources(directory + Strings.DEFAULT_SCHEMAS[10][0],
                    null, null, domain, null, null);
        }
        if (_schemas[5]){
            owlParser.setLabelsPreferred(false);
            //domain = "EON Ontology Alignment Contest";
            domain = "OAEI Benchmark";
            String author = "oaei";
            String version = Strings.BENCHMARK_YEAR;
//			String urls = "http://www.purl.org/net/ontology/order.owl,http://www.purl.org/net/ontology/beer, http://www.w3.org/2001/sw/WebOnt/guide-src/food.owl, http://www.w3.org/2001/sw/WebOnt/guide-src/wine.owl";
//			String urls = "http://co4.inrialpes.fr/align/Contest/101/onto.rdf, http://co4.inrialpes.fr/align/Contest/102/onto.rdf";
//			String urls = "http://co4.inrialpes.fr/align/Contest/101/onto.rdf, http://co4.inrialpes.fr/align/Contest/102/onto.rdf, http://co4.inrialpes.fr/align/Contest/103/onto.rdf, http://co4.inrialpes.fr/align/Contest/104/onto.rdf, http://co4.inrialpes.fr/align/Contest/201/onto.rdf, http://co4.inrialpes.fr/align/Contest/202/onto.rdf, http://co4.inrialpes.fr/align/Contest/204/onto.rdf, http://co4.inrialpes.fr/align/Contest/205/onto.rdf, http://co4.inrialpes.fr/align/Contest/206/onto2.rdf, http://co4.inrialpes.fr/align/Contest/221/onto.rdf, http://co4.inrialpes.fr/align/Contest/222/onto.rdf, http://co4.inrialpes.fr/align/Contest/223/onto.rdf, http://co4.inrialpes.fr/align/Contest/224/onto.rdf, http://co4.inrialpes.fr/align/Contest/225/onto.rdf, http://co4.inrialpes.fr/align/Contest/228/onto.rdf, http://co4.inrialpes.fr/align/Contest/230/onto.rdf, http://co4.inrialpes.fr/align/Contest/301/onto.rdf, http://co4.inrialpes.fr/align/Contest/302/onto.rdf, http://co4.inrialpes.fr/align/Contest/303/onto.rdf, http://co4.inrialpes.fr/align/Contest/304/onto.rdf";
//			String urls = "http://www.purl.org/net/ontology/order.owl,http://www.purl.org/net/ontology/beer";
//			String oneUrl[] = urls.split(",");
//			for (int i = 0; i < OAEIConstants.ONTO_NUMBERS.length; i++) {		
//            	String uri = OAEIConstants.BENCHMARK_URL
//                + OAEIConstants.ONTO_NUMBERS[i] + "/onto.rdf";
//			
//			String path = file.getAbsolutePath().replace('\\', '/');
//			path = path.substring(0, path.length()-2);
//
//			
            String dir2 = dir + Strings.BENCHMARK_URL;
            boolean labelsPreferred = false;
            for (int i = 0; i < Strings.localContestOntos.length; i++) {
//               	String uri = GUIConstants.BENCHMARK_URL
                String uri = dir2 + Strings.localContestOntos[i] + "/onto.rdf";
                String schema = Strings.BENCHMARK_YEAR + "_"
                        + Strings.localContestOntos[i] + "_onto";
                String comment = "Contest Reference Alignment";
//				OWLParserSabine.parseOWLOnto(uri,author, domain,version,null,null);
//            	OWLParser.parseOWLOnto(uri, null, false, false,
//            			// labelsPreferred!
//                false, false, author, domain, version, comment, null);
//            	owlParser.parse(uri, schema, false, null, labelsPreferred, 
//	        			false, false, author, domain, version, comment);
                owlParser.parseSingleSource(uri, schema, author, domain, version, comment);
            }
        }
        if (_schemas[6]){ // small web directory
            domain = "Web Directory";
            owlParser.setLabelsPreferred(true);
//			boolean labelsPreferred = true;
            for (int i = 11; i < 15; i++) {
                String uri = dir + Strings.DEFAULT_SCHEMAS[i][0];
//	        	OWLParser.parseOWLOnto(uri, null, false, labelsPreferred,false, false, null, domain, null, null, GUIConstants.DEFAULT_SCHEMAS[i][1]);
//	        	owlParser.parse(uri, Strings.DEFAULT_SCHEMAS[i][1], false, null, labelsPreferred, 
//	        			false, false, null, domain, null, null);
                owlParser.parseSingleSource(uri, Strings.DEFAULT_SCHEMAS[i][1], null, domain, null, null);
            }
        }
        if (_schemas[7]){ // large web directory
            domain = "Web Directory";
            owlParser.setLabelsPreferred(true);
//			boolean labelsPreferred = true;
            for (int i = 15; i < 19; i++) {
                String uri = dir + Strings.DEFAULT_SCHEMAS[i][0];
//	        	OWLParser.parseOWLOnto(uri, null, false, labelsPreferred,false, false, null, domain, null, null, GUIConstants.DEFAULT_SCHEMAS[i][1]);
//				owlParser.parse(uri, Strings.DEFAULT_SCHEMAS[i][1], false, null, labelsPreferred, 
//	        			false, false, null, domain, null, null);
                owlParser.parseSingleSource(uri, Strings.DEFAULT_SCHEMAS[i][1], null, domain, null, null);
            }
        }
        if (_schemas[8]) {// "other"
            domain = "Spicy";
//			XSDParser.parseMultipleXSD(SourceParser.SOURCE_DIR + GUIConstants.SLASH
//					+ GUIConstants.DEFAULT_SCHEMAS[19][0],
//					GUIConstants.DEFAULT_SCHEMAS[19][1],
//					null, domain, null, GUIConstants.SPICY_SHORTNAMES);

            xsdParser.parseMultipleSources(directory+ Strings.DEFAULT_SCHEMAS[19][0],
                    Strings.SPICY_SHORTNAMES, null, domain, null, null);

            domain = "University";
//			XSDParser.parseMultipleXSD(SourceParser.SOURCE_DIR + GUIConstants.SLASH
//					+ GUIConstants.DEFAULT_SCHEMAS[20][0],
//					GUIConstants.DEFAULT_SCHEMAS[20][1],
//					null, domain, null, GUIConstants.UNI_SHORTNAMES);

            xsdParser.parseMultipleSources(directory+ Strings.DEFAULT_SCHEMAS[20][0],
                    Strings.UNI_SHORTNAMES, null, domain, null, null);
        }
        if (_schemas[9]){
//			boolean labelsPreferred = true;
            owlParser.setLabelsPreferred(true);
            domain = "OAEI Anatomy";
            String author = "oaei";
            String version = "2010";
            String uri = dir + Strings.DEFAULT_SCHEMAS[21][0];
//        	owlParser.parse(uri, Strings.DEFAULT_SCHEMAS[21][1], false, null, labelsPreferred, 
//        			false, false, author, domain, version, null);
            owlParser.parseSingleSource(uri, Strings.DEFAULT_SCHEMAS[21][1], author, domain, version, null);

            uri = dir + Strings.DEFAULT_SCHEMAS[22][0];
//        	owlParser.parse(uri, Strings.DEFAULT_SCHEMAS[22][1], false, null, labelsPreferred, 
//        			false, false, author, domain, version, null);

            owlParser.parseSingleSource(uri, Strings.DEFAULT_SCHEMAS[22][1], author, domain, version, null);
        }
        long end = System.currentTimeMillis();
        System.out.println("**************** DONE  importDefaultSchemas / PARSE: elapsed time " + (float) (end-start)/1000 + " s");


    }


    /*
         * deletes the current used database and creates a new one (inclusive
         * Systemt Matcher) - all saved matchresults will be deleted
         */
    public void importDefaultSchemas(boolean[] _schemas, String directory) {
        if (mainWindow!=null){
            mainWindow.getNewContentPane().setProgressBar(true);
        }
        if (directory==null){
            directory = InsertParser.SOURCE_DIR + GUIConstants.SLASH;
        }
        //		accessor.dropViewTables(false);
        //		accessor.deleteSourcePaths();
        long start = System.currentTimeMillis();

        parseDefaultSchemas(_schemas, directory);

        updateAll(true); //parse and import
        //		if (calculateSchemaSim) {
        //			ArrayList results = calculateAndSaveSchemaSim();
        //			// saveMatchresultToDB(results);
        //		}
        long end = System.currentTimeMillis();
        System.out.println("**************** DONE  importDefaultSchemas / PARSE + IMPORT: elapsed time " + (float) (end-start)/1000 + " s");
        if (mainWindow!=null){
            mainWindow.getNewContentPane().setProgressBar(false);
            getManagementPane().setSelectedTab(ManagementPane.REPOSITORY);
            setStatus(GUIConstants.IMPORT_EXAMPLES + GUIConstants.DONE);
        }
    }

    public void importDefaultAll(boolean[] values) {
        importDefaultAll(values, null);
    }

    /*
     * deletes the current used database and creates a new one (inclusive
     * Systemt Matcher) - all saved matchresults will be deleted
     */
    public void importDefaultAll(boolean[] values, String directory) {

        boolean[] schemas  =  new boolean[Strings.DEFAULT_SCHEMAS_SHORT.length];
        boolean[] matchresults  =  new boolean[Strings.DEFAULT_MATCHRESULTS.length];
        boolean[] instances  =  new boolean[Strings.DEFAULT_INSTANCES.length];
        boolean somethingSelected = false;
        if (values[0]){ // PurchaseOrder small
            schemas[4] = true; // "PO"
            matchresults[0] = true; // MATCHRESULT_PO
            somethingSelected = true;
        }
        if (values[1]){ // PurchaseOrder medium/large
            schemas[0] = true; // "BMECat"
            schemas[1] = true; // "OpenTrans"
            schemas[2] = true; // "Xcbl"
            schemas[3] = true; // "Xcbl35"
//				if (values[0]){ 
            matchresults[1] = true; // MATCHRESULT_PO_OP
//				}
            matchresults[2] = true; // MATCHRESULT_OP_XCBL
            instances[0] = true; // "BMECat"
            instances[1] = true;// "OpenTrans"
            somethingSelected = true;
        }
        if (values[2]){ // OAEI Benchmark
            schemas[5] = true;
            matchresults[3] = true; // BENCHMARK_URL
            instances[2] = true;
            somethingSelected = true;
        }
        if (values[3]){ // Web Directories
            schemas[6] = true;
            matchresults[4] = true; // MATCHRESULT_WEBDIR
            instances[3] = true;
            somethingSelected = true;
        }
        if (values[4]){ // Web Directories large
            schemas[7] = true;
            matchresults[5] = true; // MATCHRESULT_WEBDIR
            instances[4] = true;
            somethingSelected = true;
        }
        if (values[5]){ // other (Spicy + University)
            schemas[8] = true;
            matchresults[6] = true;
            somethingSelected = true;
        }
        if (values[6]){ // OAEI Anatomy
            schemas[9] = true;
            matchresults[7] = true;
            somethingSelected = true;
        }
        if (somethingSelected){
            if (directory==null){
                directory = InsertParser.SOURCE_DIR+GUIConstants.BACKSLASH;
            }
            importDefaultSchemas(schemas, directory);
            importDefaultMatchresults(matchresults, directory);
//				importDefaultInstances(instances, directory);
        }
    }


    //		public void importDefaultInstances(boolean[] instances) {
//			importDefaultInstances(instances, null);
//		}
	/*
	 * deletes the current used database and creates a new one (inclusive
	 * Systemt Matcher) - all saved matchresults will be deleted
	 */
    public void importDefaultInstances(boolean[] instances, String directory) {
        if (mainWindow!=null){
            mainWindow.getNewContentPane().setProgressBar(true);
        }
        long start = System.currentTimeMillis();
        ArrayList data = new ArrayList();
        StringBuffer info = new StringBuffer();
        String directoryTmp = directory+ "Instances/";
//		String directory = "./Sources/Instances/";	
        if (instances[0]) {// "BMECat"
            data.clear();
            data.add(manager.getSourcesWithNameAndUrl("bmecat_newcat", "http://www.bmecat.org/XMLSchema/1.2/bmecat_new_catalog").get(0));
            data.add(directoryTmp+"bmecat_light_12.xml");
            info.append(parseInstancesAddFile(data, false));
        }
        if (instances[1]) {// "OpenTrans"

            data.clear();
            data.add(manager.getSourcesWithNameAndUrl("OpenTrans_INVOICE", "http://www.opentrans.org/XMLSchema/1.0").get(0));
            data.add(directoryTmp+"openTRANS_INVOICE_1_0_all_in_one_example.xml");
            info.append(parseInstancesAddFile(data, false));

            data.clear();
            data.add(manager.getSourcesWithNameAndUrl("OpenTrans_ORDER", "http://www.opentrans.org/XMLSchema/1.0").get(0));
            data.add(directoryTmp+"openTRANS_ORDER_1_0_all_in_one_example.xml");
            info.append(parseInstancesAddFile(data, false));

            data.clear();
            data.add(manager.getSourcesWithNameAndUrl("OpenTrans_ORDERCHANGE", "http://www.opentrans.org/XMLSchema/1.0").get(0));
            data.add(directoryTmp+"openTRANS_ORDERCHANGE_1_0_all_in_one_example.xml");
            info.append(parseInstancesAddFile(data, false));

            data.clear();
            data.add(manager.getSourcesWithNameAndUrl("OpenTrans_ORDERRESPONSE", "http://www.opentrans.org/XMLSchema/1.0").get(0));
            data.add(directoryTmp+"openTRANS_ORDERRESPONSE_1_0_all_in_one_example.xml");
            info.append(parseInstancesAddFile(data, false));

            data.clear();
            data.add(manager.getSourcesWithNameAndUrl("OpenTransAll", "http://www.opentrans.org/XMLSchema/1.0").get(0));
            data.add(directoryTmp+"openTRANS_DISPATCHNOTIFICATION_1_0_all_in_one_example.xml");
            data.add(directoryTmp+"openTRANS_INVOICE_1_0_all_in_one_example.xml");
            data.add(directoryTmp+"openTRANS_ORDER_1_0_all_in_one_example.xml");
            data.add(directoryTmp+"openTRANS_ORDERCHANGE_1_0_all_in_one_example.xml");
            data.add(directoryTmp+"openTRANS_ORDERRESPONSE_1_0_all_in_one_example.xml");
            data.add(directoryTmp+"openTRANS_QUOTATION_1_0_all_in_one_example.xml");
            data.add(directoryTmp+"openTRANS_RECEIPTACKNOWLEDGEMENT_1_0_all_in_one_example.xml");
            data.add(directoryTmp+"openTRANS_RFQ_1_0_all_in_one_example.xml");
            info.append(parseInstancesAddFile(data, false));
        }
        if (instances[2]) {// "OAEI Benchmark
            data.clear();
            for (int i = 0; i < Strings.localContestOntos.length; i++) {
                String provider = "file:/" + directory
                        + Strings.BENCHMARK_URL+ Strings.localContestOntos[i] + "/onto.rdf";
//    			data.add(manager.getSourceWithProvider(uri));
//            	String name = "oaei_"+GUIConstants.BENCHMARK_YEAR+"_benchmarks_"+GUIConstants.localContestOntos[i]+"_onto_rdf";
//            	String name = Strings.BENCHMARK_YEAR+"_benchmarks_"+Strings.localContestOntos[i]+"_onto_rdf";
//            	String name = Strings.BENCHMARK_YEAR+"_benchmark_"+Strings.localContestOntos[i]+"_onto";
                data.add(manager.getSource(accessor.getSourceWithProvider(provider)));
            }
            if (!data.isEmpty() && data.get(0)!=null){
                info.append(parseInstancesOrgFile(data, false));
            }
        }
        if (instances[3]) {// "Web Directories"
            data.clear();
            data.add(manager.getSourcesWithNameAndUrl("dmoz_Freizeit","file:/E:/workspace/COMA++/Sources/webdirectories/dmoz.Freizeit.owl").get(0));
            data.add(manager.getSourcesWithNameAndUrl("Google_Freizeit","file:/E:/workspace/COMA++/Sources/webdirectories/Google.Freizeit.owl").get(0));
            data.add(manager.getSourcesWithNameAndUrl("Google_Lebensmittel","file:/E:/workspace/COMA++/Sources/webdirectories/Google.Lebensmittel.owl").get(0));
            data.add(manager.getSourcesWithNameAndUrl("web_Lebensmittel","file:/E:/workspace/COMA++/Sources/webdirectories/web.Lebensmittel.owl").get(0));
            if (data.get(0)!=null){
                info.append(parseInstancesOrgFile(data, false));
            }
        }
        if (instances[4]) {// "Web Directories"
            data.clear();
            data.add(manager.getSourcesWithNameAndUrl("webdirectory_dmoz_owl","file:/E:/webdirectories/dmoz.owl").get(0));
            data.add(manager.getSourcesWithNameAndUrl("webdirectory_google_owl","file:/E:/webdirectories/google.owl").get(0));
            data.add(manager.getSourcesWithNameAndUrl("webdirectory_web_owl","file:/E:/webdirectories/web.owl").get(0));
            data.add(manager.getSourcesWithNameAndUrl("webdirectory_yahoo_small_owl","file:/E:/webdirectories/yahoo.owl").get(0));
            if (data.get(0)!=null){
                info.append(parseInstancesOrgFile(data, false));
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("**************** DONE  importDefaultInstances / PARSE + IMPORT: elapsed time " + (float) (end-start)/1000 + " s");
        if (mainWindow!=null){
            mainWindow.getNewContentPane().setProgressBar(false);
            if (info.length()>0){
                TextFrame wnd = new TextFrame(GUIConstants.IMPORT_STAT + info, GUIConstants.INFORMATION,MainWindow.DIM_INFO);
                wnd.setLocation(position);
                wnd.setVisible(true);
            }
        }
    }

    /*
     * calculate similarity of schemas (in repository) depending on names of
     * nodes (save not existing results in repository)
     */
    public ArrayList calculateAndSaveSchemaSim() {
        // calculate all possible similaritys
        //		long start = System.currentTimeMillis();
        ArrayList sources = LoadFromDBThread.getAllSchemas(this);
        int count = 0;
        if ((sources != null) && (sources.size() > 1)) {
            for (int i = 0; i < (sources.size() - 1); i++) {
                //					ArrayList results = new ArrayList();
                //					while (importMatchresult!=null &&
                //							importMatchresult.isAlive()){
                //						try {
                //							Thread.sleep(wait);
                //						} catch (InterruptedException e) {
                //							e.printStackTrace();
                //						}
                //					}
                Source source1 = (Source) sources.get(i);
                Graph s1 = manager.loadGraph(source1, true);
                if (Source.TYPE_ONTOLOGY==source1.getType()){
                    // Always save in Resolved representation - for ontologies
                    s1 = s1.getGraph(Graph.PREP_DEFAULT_ONTOLOGY);
                } else {
                    // Always save in Simplified representation - for xml schemas and relational
                    s1 = s1.getGraph(Graph.PREP_DEFAULT_XML_REL);
                    if (s1==null){
                        System.out.println("saveGraph(): Transformation to SIMPLIFIED failed, therefore use REDUCED");
//						s1 = s1.getGraph(Graph.GRAPH_STATE_REDUCED);
                    }
                }

                //					HashMap map = new HashMap();
                for (int j = (i + 1); j < sources.size(); j++) {
                    Source source2 = (Source) sources.get(j);
//					Graph s2;
//					//						if (map.containsKey(source2))
//					//							s2= (Graph) map.get(source2);
//					//						else {
//					s2 = manager.loadGraph(source2, true).getGraph(
//							Graph.GRAPH_STATE_SIMPLIFIED);

                    Graph s2 = manager.loadGraph(source2, true);
                    if (Source.TYPE_ONTOLOGY==source2.getType()){
                        // Always save in Resolved representation - for ontologies
                        s2 = s2.getGraph(Graph.PREP_DEFAULT_ONTOLOGY);
                    } else {
                        // Always save in Simplified representation - for xml schemas and relational
                        s2 = s2.getGraph(Graph.PREP_DEFAULT_XML_REL);
                        if (s1==null){
                            System.out.println("saveGraph(): Transformation to SIMPLIFIED failed, therefore use REDUCED");
                            s2 = s2.getGraph(Graph.PREP_REDUCED);
                        }
                    }

                    //							map.put(source2, s2);
                    //						}
                    //					System.out.println(s1.getSource().getName() + " -
                    // "+s2.getSource().getName());
                    count++;
                    System.out.print(count + ":   ");
//					MatchResult mr = calculateSchemaSimilarity(s1, s2,
//							manager);
//					if (mr != null) {
//						//						System.out.println(mr.getSrcObjects().size() + " - "
//						//								+ mr.getTrgObjects().size() + ": "
//						//								+ mr.getMatchCount());
//						//						manager.saveMatchResult(mr);
//						//							insertSourceRel(int source1Id, int source2Id,
//						//		                             String type, int sourceRel1Id, int sourceRel2Id,
//						// String evidence,
//						//		                             String comment, String provider, String date)
//						accessor.insertSourceRel(source1.getId(), source2
//								.getId(), GUIConstants.INTERNAL_SIM, -1, -1, mr
//								.getEvidence(), GUIConstants.INTERNAL_SIM, null,
//								null, -1);
//						//							results.add(mr);
//						//// saveMatchresultToFile(mr, false);
//					}
                }
                //					if (results.size() > 0) {
                //						saveMatchresultToDB(results);
                //// results.clear();
                //					}
            }
        }
        updateAll(false); //no parse or import
        //			long end = System.currentTimeMillis();
        //			System.out
        //					.println("========================================================================================");
        //			System.out
        //					.println("========================================================================================");
        //			System.out.println((end - start) / 1000);
        //			Time t = new Time(end - start);
        //			System.out.println(t.toGMTString());
        //			System.out
        //					.println("========================================================================================");
        //			System.out
        //					.println("========================================================================================");
        //			return results;
        return null;
    }


    public void deleteSynonyms(){
        // delete old Synonym data sets
        ArrayList<Integer> sourceIDs = accessor.getSourceIdsWithNameAndUrl(Repository.SRC_SYNONYM, null);
        if (sourceIDs!=null){
            for (int j=0; j<sourceIDs.size(); j++){
                //			int sourceID = accessor.getSourceId(ListParser.SRC_SYNONYM);
                int sourceID = sourceIDs.get(j);
                accessor.deleteSource(sourceID);
                Source schema =manager.getSource(sourceID);
                HashSet<Integer> matchresults2 = accessor.getSourceRelId(schema.getId());
                if (matchresults2 != null) {
                    for (Integer rel_id : matchresults2) {
                        accessor.deleteSourceRel(rel_id);
                    }
                }
            }
        }
    }

    public void deleteAbbreviations(){
        // delete old Abbreviaton data sets
        ArrayList<Integer> sourceIDs = accessor.getSourceIdsWithNameAndUrl(Repository.SRC_ABBREV, null);
        if (sourceIDs!=null){
            for (int j=0; j<sourceIDs.size(); j++){
//					int sourceID = accessor.getSourceId(ListParser.SRC_ABBREV);
                int sourceID = sourceIDs.get(j);
                accessor.deleteSource(sourceID);
                Source schema =manager.getSource(sourceID);
                HashSet<SourceRelationship> matchresults2 = manager.getAccessor().getSourceRels(schema.getId());
                if (matchresults2 != null) {
                    for (SourceRelationship matchresult : matchresults2) {
                        accessor.deleteSourceRel(matchresult.getId());
                    }
                }
            }
        }
    }

    /*
     * import default abbreviations and synonyms
     */
    public void setDefaultAbbreviationsAndSynonyms() {
        String fileAbb = new String (file_abb);
//		System.out.println(fileAbb);
        String fileSyn = new String(file_syn);
//		System.out.println(fileSyn);
        setAbbreviations(fileAbb);
        setSynonyms(fileSyn);
    }

    /*
     * remove all abbreviations and synonyms
     */
    public void removeAbbreviationsAndSynonyms() {
        deleteAbbreviations();
        deleteSynonyms();
        updateAll(true);
    }


    /*
     * set default abbreviations
     */
    public void setAbbreviations(String _fileAbb) {
        setStatus(GUIConstants.ABB);
        if (_fileAbb!=null){
            deleteAbbreviations();
            ListParser parser = new ListParser(true);
            parser.parseAbbreviation(_fileAbb);
            updateAll(true);
            setStatus(GUIConstants.ABB + GUIConstants.DONE);
        }
    }

    /*
     * set default synonyms
     */
    public void setSynonyms(String _fileSyn) {
        setStatus(GUIConstants.SYN);
        if (_fileSyn!=null){
            deleteSynonyms();
            ListParser parser = new ListParser(true);
            parser.parseSynonym(_fileSyn);
            updateAll(true);
            setStatus(GUIConstants.SYN + GUIConstants.DONE);
        }
    }

    /*
     * show all abbreviations (and delete/ add pairs)
     */
    public void showAbbreviations() {
        setStatus(GUIConstants.ABB);
        Dlg_ShowSynOrAbb dialog = new Dlg_ShowSynOrAbb(mainWindow,
                GUIConstants.ABB, this, false);
        dialog.showDlg(position);
        setStatus(GUIConstants.ABB + GUIConstants.DONE);
    }

    /*
     * show all synonyms (and delete/ add pairs)
     */
    public void showSynonyms() {
        setStatus(GUIConstants.SYN);
        Dlg_ShowSynOrAbb dialog = new Dlg_ShowSynOrAbb(mainWindow,
                GUIConstants.SYN, this, true);
        dialog.showDlg(position);
        setStatus(GUIConstants.SYN + GUIConstants.SPACE1 + GUIConstants.DONE);
    }

//	public void showInstances(Source _source, Element _element){
//		TextFrame wnd = new TextFrame(_source, _element,
//				GUIConstants.INFO_INST, MainWindow.DIM_LARGE);
//		wnd.setLocation(position);
//		wnd.setVisible(true);
//	}

    /*
     * set the schema manager to the given one
     */
    public void setManager(Manager _manager) {
        manager = _manager;
    }

    /*
     * returns the defaultDirectory
     */
    public File getDefaultDirectory() {
        return defaultDirectory;
    }


    /*
     * Open an dialog where all pre-defined workflow variable will be shown
     * (and there characteristics)
     */
    public void showWorkflowVariables() {
        setStatus(GUIConstants.SHOW_EX_WORKFLOWVARIABLES);
        Dlg_WorkflowVariables dialog = new Dlg_WorkflowVariables(mainWindow,
                this);
        dialog.showDlg(position, MainWindow.DIM_EXISTING_MATCHER);
    }


//	/*
//	 * Open an dialog where all existing matcher will be shown (an there
//	 * characteristics)
//	 */
//	public void showExistingMatcher() {
//		setStatus(GUIConstants.SHOW_EX_MATCHER);
//		Dlg_ExistingMatcher dialog = new Dlg_ExistingMatcher(mainWindow,
//				this);
//		dialog.showDlg(position, MainWindow.DIM_EXISTING_MATCHER);
//	}

    //	/*
//	 * Save a given matcher config (and inform the user with the help of the
//	 * status line) if update = true an already existing matcher will be updated
//	 */
//	public void saveMatcherConfigDB(MatcherConfig _config, boolean _update) {
//		manager.saveMatcherConfig(_config, _update);
//		//updateAllOld(true, false);
//		updateAll(false); //no parse or import
//		//				manager.addMatcherConfig(config,update);
//		if (_update) {
//			setStatus(GUIConstants.MATCHER_CONF_UPDATE);
//		} else {
//			setStatus(GUIConstants.MATCHER_CONF_DB);
//		}
//	}
//
	/*
	 * Save a given matcher config (and inform the user with the help of the
	 * status line) if update = true an already existing matcher will be updated
	 */
    public void saveReuseStrategyConfigDB(String _name,String _config, boolean _update) {
        if (_update){
            manager.getImporter().updateWorkflowVariable(_name, _config);
        } else {
            manager.getImporter().insertWorkflowVariable(_name, _config);
        }
        //updateAllOld(true, false);
        updateAll(false); //no parse or import
        //				manager.addMatcherConfig(config,update);
        if (_update) {
            setStatus(GUIConstants.MATCHER_CONF_UPDATE);
        } else {
            setStatus(GUIConstants.MATCHER_CONF_DB);
        }
    }
//	
//	/*
//	 * Save a given matcher config (and inform the user with the help of the
//	 * status line) if update = true an already existing matcher will be updated
//	 */
//	public void saveInstanceMatcherConfigDB(MatcherConfig _config, boolean _update) {
//		manager.saveInstanceMatcherConfig(_config, _update);
//		//updateAllOld(true, false);
//		updateAll(false); //no parse or import
//		//				manager.addMatcherConfig(config,update);
//		if (_update) {
//			setStatus(GUIConstants.MATCHER_CONF_UPDATE);
//		} else {
//			setStatus(GUIConstants.MATCHER_CONF_DB);
//		}
//	}

//	/*
//	 * Open a filechooser and let the user specify a file where the given
//	 * matcher config shall be saved in (and inform the user with the help of
//	 * the status line)
//	 */
//	public void saveMatcherConfigFile(MatcherConfig _config) {
//		if (_config == null) {
//			setStatus(GUIConstants.NO_MATCHER_CONF);
//		} else {
//			try {
//				JFileChooser chooser = new JFileChooser();
//				chooser.setCurrentDirectory(defaultDirectory);
//				int returnVal = chooser.showSaveDialog(mainWindow);
//				if (returnVal == JFileChooser.APPROVE_OPTION) {
//					defaultDirectory = chooser.getCurrentDirectory();
//					String _name = chooser.getSelectedFile().toString();
//					if (_name != null) {
//						manager.saveMatcherConfigFile(_config, _name);
//						setStatus(GUIConstants.MATCHER_CONF_FILE + _name);
//					} else {
//						setStatus(GUIConstants.MATCHER_CONF_NOT);
//					}
//				} else {
//					setStatus(GUIConstants.MATCHER_CONF_NOT);
//				}
//			} catch (Exception x) {
//				setStatus(GUIConstants.MATCHER_CONF_ERROR);
//			}
//		}
//	}

    /*
     * show the hierarchy of the matcher (= dependences between them)
     */
    public void showVariableHierarchy() {
        SimpleDirectedGraph<String, DefaultEdge> graph = VariableGraph.getVariableGraph(accessor);
        if ((graph != null) && (graph.vertexSet().size() > 0)) {
            setStatus(GUIConstants.VARIABLE_HIERARCHY);
            Dlg_ShowHierarchy mh = new Dlg_ShowHierarchy(this, graph,
                    GUIConstants.VARIABLE_HIERARCHY);
            mh.showDlg(getDialogPosition(), MainWindow.DIM_LARGE); //mh.show();
        } else {
            setStatus(GUIConstants.NO_VARIABLE_CONF);
        }
    }

    /*
     * return matcher for coma strategy
     */
//	public int getMatcherAllContext() {
//		return matcherAllContext;
//	}
//
	/*
	 * return fragment identification strategy for fragment strategy
	 */
    public int getFragmentIdentification() {
        return fragmentIdentification;
    }

    /*
     * return fragment matching strategy for fragment strategy
     */
    public String getFragmentStrategy() {
        return fragmentStrategy;
    }

    /*
     * return fragment matching strategy for fragment strategy
     */
    public String getFragmentRootStrategy() {
        return fragmentRootStrategy;
    }
//
//	/*
//	 * return context matcher for graph strategy
//	 */
//	public int getContextMatcherFilteredContext() {
//		return contextMatcherFilteredContext;
//	}

    /*
     * The defaultDirectory to set.
     */
    public void setDefaultDirectory(File _directory) {
        defaultDirectory = _directory;
    }

//	/*
//	 * return node matcher for graph strategy
//	 */
//	public int getNodeMatcherFilteredContext() {
//		return nodeMatcherFilteredContext;
//	}
//
//	/*
//	 * set matcher for coma strategy
//	 */
//	public void setMatcherAllContext(int _matcher) {
//		matcherAllContext = _matcher;
//	}
//
//	/*
//	 * return matcher for coma strategy
//	 */
//	public int getMatcherNodes() {
//		return matcherNodes;
//	}
//	
//	/*
//	 * set matcher for coma strategy
//	 */
//	public void setMatcherNodes(int _matcher) {
//		matcherNodes = _matcher;
//	}


    /*
     * set fragment identification strategy for fragment strategy
     */
    public void setFragmentIdentification(int _identification) {
        fragmentIdentification = _identification;
    }

    /*
     * set fragment matching strategy for fragment strategy
     */
    public void setFragmentStrategy(String _matcher) {
        fragmentStrategy = _matcher;
    }


    /*
     * set fragment matching strategy for fragment strategy
     */
    public void setFragmentRootStrategy(String _matcher) {
        fragmentRootStrategy = _matcher;
    }
//
//	/*
//	 * set context matcher for graph strategy
//	 */
//	public void setContextMatcherFilteredContext(int _matcher) {
//		contextMatcherFilteredContext = _matcher;
//	}
//
//	/*
//	 * set node matcher for graph strategy
//	 */
//	public void setNodeMatcherFilteredContext(int _matcher) {
//		nodeMatcherFilteredContext = _matcher;
//	}

//	public void stopEdit() {
//		if (getManagementPane().getEditState()) {
//			getManagementPane().setEditState(false);
//			mainWindow.setEditMatchresult(false);
//			editMatchresult(false, false);
//		}
//	}
//
//	public void enableEdit(boolean _enable) {
//		getManagementPane().setEditEnabled(_enable);
//		mainWindow.setEditEnabled(false);
//		//			editMatchresult(false, false);
//	}

    public void setNewMatchResult(MatchResult _result) {
//		boolean editModus = false;
        boolean changeViewAuto = false;
        setNewMatchResult(_result, /*editModus,*/ changeViewAuto);
    }

//	public void setNewMatchResult(MatchResult _result, boolean _changeViewAuto) {
//		boolean editModus = false;
//		setNewMatchResult(_result, editModus, _changeViewAuto);
//	}

    /*
     * set the MatchResult to the given one and update the GUI
     */
    public void setNewMatchResult(MatchResult _result/*, boolean _editModus*/, boolean _changeViewAuto) {
        boolean verbose = false;
//		if (!_editModus) {
//			stopEdit();
//		}
        long start=0, end=0;
        if (_result == null || _result.getMatchCount() < 1) {
            _result = null;
            getManagementPane().setSplitPaneButtonEnabled(false);
        } else {
//			_result = MatchResult.transformMatchResult(_result,
//					getPreprocessing());
//			if (_result==null){
//				return;
//			}
            Source source = guiMatchresult.getSourceSource();
            Source target = guiMatchresult.getTargetSource();

            start = System.currentTimeMillis();
            if ((source == null)
                    || !source.equals(_result.getSourceGraph().getSource())) {
                loadSourceGraph(_result.getSourceGraph(), false);
            }
            end = System.currentTimeMillis();
            if (verbose) System.out.println("setNewMatchResult.loadSourceGraph "  + (float) (end - start) / 1000);
            start = System.currentTimeMillis();
            if ((target == null)
                    || !target.equals(_result.getTargetGraph().getSource())) {
                loadTargetGraph(_result.getTargetGraph(), false);
            }
            end = System.currentTimeMillis();
            if (verbose) System.out.println("setNewMatchResult.loadTargetGraph "  + (float) (end - start) / 1000);
            start = System.currentTimeMillis();
            if (_result.getUserObject() != null){
//				&& (_result.getUserObject() instanceof MatchResult[])) {
                MatchresultView2 mw = mainWindow.getNewContentPane().getMatchresultView();
                Source middle = null;
                Graph middleGraph=null;
                if (_result.getUserObject() instanceof MatchResult[]){
                    MatchResult[] results = (MatchResult[]) _result.getUserObject();
                    middleGraph = results[0].getTargetGraph();
                }
                if (mw instanceof MatchresultView3){
                    Graph current = ((MatchresultView3)mw).getMiddleGraph();
                    if (current!=null){
                        middle = current.getSource();
                    }
                }
                if (middleGraph != null && (middleGraph.getSource()!=middle)) {
                    loadMiddleGraph(middleGraph);
                }
                getManagementPane().setSplitPaneButtonEnabled(true);
            } else {
                getManagementPane().setSplitPaneButtonEnabled(false);
            }
//			if (_changeViewAuto && _result.getMatchCount()==_result.getMatchCountVertex()){
//				setView(MainWindow.VIEW_NODES);
//			}
//			if (_changeViewAuto && _result.getMatchCount()==_result.getMatchCountPath()){
//				setView(MainWindow.VIEW_GRAPH);
//			}
        }
        guiMatchresult.setMatchResult(_result);
        end = System.currentTimeMillis();
        if (verbose) System.out.println("setNewMatchResult.matchresult.setMatchResult "  + (float) (end - start) / 1000);
        start = System.currentTimeMillis();
        getMatchresultView().setChanged(true);
        mainWindow.getNewContentPane().setNewMatchResult(_result);
        end = System.currentTimeMillis();
        if (verbose) System.out.println("setNewMatchResult.mainWindow.getNewContentPane().setNewMatchResult(_result) "  + (float) (end - start) / 1000);
        start = System.currentTimeMillis();
    }

    public void cleanMatchresultLines() {
        if (!mainWindow.getNewContentPane().is2SplitPane()) {
            getManagementPane().setSplitPaneButtonEnabled(false);
        }
        setNewMatchResult(null);
        //		mainWindow.getNewContentPane().setMatchresultLabel(null);
        //		mainWindow.getNewContentPane().getLinesComponent().cleanMatchresultLines();
        //		getMatchresultView().setChanged(true);
    }

    /*
     * @return Returns the step.
     */
    public StepByStepFragmentMatching getStepFragmentMatching() {
        return stepFragmentMatching;
    }

//	/*
//	 * @return Returns the step.
//	 */
//	public StepByStepCombinedReuse getStepCombinedReuse() {
//		return stepCombinedReuse;
//	}

    /*
     * update default position for dialog, etc
     */
    public void setDialogPosition(Point _point) {
        position = _point;
    }

    /*
     * @return Returns the point, where to locate dialogs,etc.
     */
    public Point getDialogPosition() {
        return position;
    }

    /*
     * @return Returns the loadSchema-Thread
     */
    public LoadFromDBThread getLoadSchema() {
        return loadSchema;
    }

    //_parsed: import external schemas and matchresults using parse tables
    //otherwise only clean view tables and delete source paths
    public void updateAll(boolean _parsed) {
//		DataImport importer = new DataImport();
//		if (_parsed) {
//			//import sources and objects
//			//      importer.dropViewTables(false);
//			ArrayList sources = importer.importSourceInfo();
//			importer.importSources(sources);
//			//import matchresults and associations
//			ArrayList matchresults = importer.importMatchresultInfo();
//			importer.importMatchresultsForMultipleSources(sources);
//			//update source/subsources structure info
//			importer.updateSources();
//			//Save all subsuming/subsumed relationships
//			manager.saveSubRelationships(sources, matchresults);
//			//Clean previous views of sources
//			//      accessor = new DataAccess();
//			accessor.dropViewTables(sources);
//			//invalidate all existing source paths
//			importer.deleteSourcePaths();
//			updatedRepository = true;
//		} else {
//			//Clean previous views of sources
//			//      accessor = new DataAccess();
//			accessor.dropViewTables(false);
//			//invalidate all existing source paths
//			importer.deleteSourcePaths();
//		}

//		manager.updateAll(_parsed);
        // load the Respository new
        // the loaded Schemas and created Matchresults remain in the
        // mainwindow
//		MatchResult taxMR = manager.getTaxonomyDistanceSim();
        setManager(new Manager());
//		manager.setTaxonomyDistanceSim(taxMR);
        manager.loadRepository();
//		manager.computeMatchresultStatistics();
        if (mainWindow!=null) {
            mainWindow.setController(this);
        }
        updatedRepository = true;
    }

    /**
     * @return Returns the accessor.
     */
    public DataAccess getAccessor() {
        return accessor;
    }

    public boolean isUpdatedRepository() {
        return updatedRepository;
    }

    public HashMap<Object, Float> getSourceRelationshipValues() {
        return sourceRelationshipValues;
    }

    public void setSourceRelationshipValues(HashMap<Object, Float> _sourceRelationshipValues) {
        sourceRelationshipValues = _sourceRelationshipValues;
    }

//	public int getReuseStrategy() {
//		return ReuseStrategy;
//	}
//
//	public void setReuseStrategy(int _ReuseStrategy) {
//		ReuseStrategy = _ReuseStrategy;
//	}
//
//	public int getCombinedReuseStrategy() {
//		return combinedReuseStrategy;
//	}
//
//	public void setCombinedReuseStrategy(int _combinedReuseStrategy) {
//		combinedReuseStrategy = _combinedReuseStrategy;
//	}

    /**
     * @return Returns the simValues.
     */
    public HashMap<Object, Float> getSimValues() {
        return simValues;
    }

    /**
     *            The simValues to set.
     */
    public void setSimValues(HashMap<Object, Float> _simValues) {
        simValues = _simValues;
    }

//	public void createPartMatchResult(int _part){
//		MatchResult current = getMatchresult().getMatchResult();
//		if (current==null){
//			setStatus(GUIConstants.NO_MATCHRESULT);
//			return;
//		}
//		MatchResult resultNew = MatchResult.getPart(current,_part);
////		setNewMatchResult(resultNew,true);		
//		getMainWindow().getNewContentPane().addMatchResult(resultNew, true);
//	}

    public void changeDomain(String domainName, Object[] selected) {
        if (domainName==null || selected==null){
            return;
        }
        for (int i = 0; i < selected.length; i++) {
            Source current = (Source)selected[i];
            String domain = current.getDomain();
            if (domain!=null && domain.equals(domainName)){
                continue;
            }
            if (domainName.equals(GUIConstants.NO_DOMAIN_NORMAL)){
                current.setDomain(null);
                manager.getImporter().updateSourceDomain(current.getId(), null);
            } else {
                current.setDomain(domainName);
                manager.getImporter().updateSourceDomain(current.getId(), domainName);
            }
        }
        getManagementPane().selectDomain(domainName);
    }

    public void executeMatching(String workflow){
        setStatus(GUIConstants.EXECUTE_MATCHING);
        if (checkSchemas()){
            execute = new ExecuteMatchingThread(mainWindow, this,
                    ExecuteMatchingThread.NORMAL, workflow);
            execute.start();
        }

//		Workflow w = null;
//		if (workflow.contains("AllContextW")){
//			w = new Workflow(Workflow.ALLCONTEXT);
//		} else if (workflow.contains("FilteredContextW")){
//			w = new Workflow(Workflow.FILTEREDCONTEXT);
//		} else if (workflow.contains("FragmentBasedW")){
//			w = new Workflow(Workflow.FRAGMENTBASED);
//		} else {
//			return;
//		}


    }


}