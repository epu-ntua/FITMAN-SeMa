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

/**
 * This class contains most of the strings that are used in the COMA++ GUI (except the ones 
 * used for debugging)
 * 
 * @author Sabine Massmann
 */
public class GUIConstants {
	//	// ************************************************************
	//	// short or no letters
	public static final String ARROW1 = "<-> ";
	public static final String AVERAGE = "Average";
	public static final String BACKSLASH = "\\";
	public static final String BIGGER = ">";
	public static final String BRACKET_LEFT = " (";
	public static final String BRACKET_RIGHT = ") ";
	public static final String COLON = ":";
	public static final String COLON_SPACE = ": ";
	public static final String COLON_SPACE2 = " : ";
	public static final String COMMA_SPACE = ", ";
	public static final String COMMA_STAR = ", *";
	public static final String DASH = "-";
	public static final String DASH_SPACE = " - ";
	public static final String DOT = ".";
	public static final String DOTS = "...";
	public static final String EMPTY = "";
	public static final String EQUAL = " = ";
	public static final String LINEBREAK = "\n";
	public static final String LINEBREAK2 = "\n\n";
	public static final String MAX = "Max";
	public static final String MIN = "Min";
	public static final String OR = " or ";
	public static final String PLUS = " + ";
	public static final String QUESTION_MARK = "?";
	public static final String SLASH = "/";
	public static final String SMALLER = "<";
	public static final String SPACE_SMALLER = " <";
	public static final String SPACE1 = " ";
	public static final String STAR = "*";
	public static final String STAR_LINE = ":\n*********************\n";
	public static final String STROKE = "|";
	public static final String TAB = "\t";
	public static final String TAB2 = "\t\t";
	public static final String TAB3 = "\t\t\t";
	public static final String UNDERSCORE = "_";
	public static final String UNKNOWN = "Unknown";
	public static final String VIA = "via: ";
	public static final String SHOW_ALL_NORMAL = "<show all>";
//	public static final String SHOW_ALL_HTML = "&lt;show all&gt;";
	public static final String NO_DOMAIN_NORMAL = "<other>";
//	public static final String NO_DOMAIN_HTML = "&lt;other&gt;";	
	// ************************************************************
	// statusline/dialogs/questions/warnings/tooltips...
	public static final String ABBREVIATION_SMALL = "abbreviation";
	public static final String BOX_CHANGED = "comboBoxChanged";
	public static final String CANCELED = "Action canceled.";
	public static final String CHANGE_FRAG_SEL = " or change the Fragment Identification (Match->Configure).";
	public static final String CHANGE_FRAG_SEL2 = " or change the Fragment Identification.";
	public static final String CHANGE_TO_ADVANCED = "Change to Advanced";
	public static final String CHANGE_TO_BASIC = "Change to Basic";
	public static final String CHOOSE = "<Choose>";
	// Dung Phan 21.06.2009 03:00
	public static final String CHOOSE_ATTRIBUTE_VALUES = "For Saving you have to choose attribute value.";
	public static final String CHOOSE_CONSTITUENTS = "For Saving you have to choose constituents strategy.";
	// Dung Phan 09.07.2009 18:17
	public static final Object CHOOSE_LEARNER_OPERATOR = "For Saving you have to choose learner.";
	
	public static final String CHOOSE_MATCHRESULT = "Choose a Matchresult";
	public static final String CHOOSE_MATCHRESULT_DEL = "Choose Matchresult(s) to delete";
	public static final String CHOOSE_MATCHRESULT_SAVE = "Choose Matchresult(s) to save";
	public static final String CHOOSE_VARIABLE = "Choose a Variable";
	// Dung Phan 22.06.2009 16:10
	public static final Object CHOOSE_MATCHERS = "For Saving you have to choose matcher.";
	public static final Object CHOOSE_MEASURES = "For Saving you have to choose measure.";
	
	public static final String CHOOSE_NONE = "<Choose/None>";
	// Dung Phan 21.06.2009 04:10
	public static final Object CHOOSE_TOKENIZERS = "For Saving you have to choose tokenizer.";;
	public static final String CHOOSE_SCHEMAS = "Choose Schema(s)";
	public static final String CHOOSE_SCHEMA_DEL = "Choose Schema(s) to delete";
	public static final String CHOOSE_SCHEMA_PARSE_INST = "Choose Schema(s) to parse for instances";
	public static final String CLEAN_INFO = "Clean the View from the current matchresult";
	public static final String CLOSE_SCHEMA = "Close this schema (including all temporary matchresults).";
	public static final String CLOSE_SRC_CANCELED = "Close Source Schema and temporary Matchresults was canceled.";
	public static final String CLOSE_SRC_DONE = "Close Source Schema done.";
	public static final String CLOSE_SRC_M_DONE = "Close Source Schema and temporary Matchresults done.";
	public static final String CLOSE_TRG_CANCELED = "Close Target Schema and temporary Matchresults was canceled.";
	public static final String CLOSE_TRG_DONE = "Close Target Schema done.";
	public static final String CLOSE_TRG_M_DONE = "Close Target Schema and temporary Matchresults done.";
	public static final String COMBINE_MATCHRESULTS = "Combine the selected matchresults to create one new matchresult? "
			+ GUIConstants.LINEBREAK + "(To load them seperated press NO)";
	public static final String COMBINE_STRATEGY = "Composition/Aggregation Strategy";
	public static final String COMBINEDREUSE_ABORTED = "Combined Reuse Matchresults aborted - for this match task at the moment not possible.";
	public static final String COMPARE_MATCHRESULTS = "Compare Matchresults";
	public static final String COMPARE_MATCHRESULTS_CANCELED = "Compare Matchresults canceled.";
	public static final String COMPARE_MATCHRESULTS_DONE = "Compare Matchresults done.";
	public static final String COMPARE_MATCHRESULT_WITH = "Compare: Quality of other (test) matchresults w.r.t the current (intended) matchresult";
	public static final String CONF_STRAT_DONE = "Configure Strategy done.";
	public static final String CONF_STRAT_CANCELED = "Configuring Strategy was canceled.";
	public static final String CONF_STRAT_INST_CANCELED = "Configuring instance-based Strategy was canceled.";
	public static final String CONFIGURE_CANCELED = "Configure was canceled.";
	public static final String CONFIGURE_MATCHER = "Configure a Matcher";
	public static final String CONFIGURE_WORKFLOW = "Configure a Workflow Variable";
	public static final String CONFIGURE_NA = "Configure wasn't available.";
	public static final String CONFIGURE_STRATEGY = "Configure Strategy";
	public static final String CONST = "Constituent Matcher";
	public static final String CONTEXT_MATCHER = "Context Matcher";
	public static final String COPY_OF = "Copy of ";
	public static final String CREATE_MATCHER = "Create a new Matcher";
	public static final String CREATE_NEW_DOMAIN = "Create a domain with the above name";
	public static final String CREATED = " has been created.";
	public static final String DEFAULT_STRATEGY = "change the values of this dialog to the default ones of the program";
	public static final String DEL_DB_ABORTED = "Deleting & Creating a new database was aborted.";
	public static final String DEL_DB_DONE = "Delete & Create New Database is done.";
	public static final String DEL_MATCHRESULT_ABORTED = "Delete Matchresult was aborted.";
	public static final String DEL_MATCHRESULT_DONE = "Delete Matchresult done.";
	public static final String DEL_VARIABLE_ABORTED = "Delete Variable was aborted.";
	public static final String DEL_QUESTION1 = "Do you really want to delete \"";
	public static final String DEL_QUESTION2 = "\" - \"";
	public static final String DEL_QUESTION3 = "\"?\n";
	public static final String DEL_SCHEMA_ABORTED = "Deleting a Schema has been aborted.";
	public static final String DEL_SCHEMA_DB = "Delete Schema from the Repository.";
	public static final String DEL_SCHEMA_DONE = "Delete Schema done.";
	public static final String DELETE = "Delete";
	public static final String DELETE_MATCHRESULT_DB = "Delete Matchresult from the Repository";
	public static final String DELETE_SCHEMA_MATCHRESULT_DB = "Delete selected Schema or Matchresult from Repository";
	public static final String DELETE_SCHEMA_MATCHRESULT_WS = "Delete selected Schema or Matchresult from Workspace";
	public static final String DESELECT_NODES = "Deselect all selected nodes to display all correspondences";
	public static final String DIFF_MATCHRESULTS_CANCELED = "Diff Matchresults canceled.";
	public static final String DIFFERENCE_MATCHRESULT = "Diff: Retain the distinct correspondences from the current Matchresult compared to another one";
	public static final String DIFFERENT_SOURCETARGET = "Select input matchresults between the same source and target schema, respectively";
	public static final String DOMAIN_TOOLTIP = "Domain: Retain matching part of the source schema";
	public static final String DONE = " done.";
	public static final String DUPLICATE = "DUPLICATE";
	public static final String DUPLICATE_DONE = "Duplicate selected Matchresult done.";
	public static final String EDIT_MATCHRESULT = "Edit the current Matchresult";
	public static final String EDIT_OFF = "Edit mode off.";
	public static final String EDIT_ON = "Edit mode on: Add and remove correspondences.";
	public static final String ERROR = "Error";
	public static final String ERROR_ABB = "Error changing abbreviations!";
	public static final String ERROR_SYN = "Error changing synonyms!";
	public static final String ESCAPE = "ESCAPE";
	public static final String EX_MATCHER_CLOSED = "Existing Matchers dialog was closed.";
	public static final String EX_STARTED = "The Matching Process is started...";
	public static final String EX_VARIABLES_CLOSED = "Existing Variables dialog was closed.";
	public static final String EXACT = "exact   ";
	public static final String EXECUTE_MATCHING = "Execute Matching Process";
	public static final String FILE_SAVED = "File successfully written: "; // +
																	// filename
	public static final String FLOAT_VALUE = "The specified weights have to be a float value.";
	public static final String FM_CONTINUE = "\nTo run the Fragment Matching again click Continue.";
	public static final String FRAGMENT_ID = "Fragment Identification";
	public static final String FULL_FORM = "full form";
	public static final String IMPORT_DATA_ABORTED = "Import data was aborted.";
	public static final String IMPORT_INSTANCES = "Import Instances";
	public static final String IMPORT_INSTANCES_ABORTED = "Import Instances was aborted.";
	public static final String IMPORT_MATCHRESULTS_ABORTED = "Import Matchresults was aborted.";
	public static final String IMPORT_MATCHER_DONE = "Import default matcher is done.";
	public static final String IMPORT_SCHEMAS_ABORTED = "Import Schemas was aborted.";
	public static final String IND_SCHEMA2 = "Does each file represents an independent schema? Press YES.\n"
			+ "Or do all files in the directory belong to a single schema? Press NO. \n"
			+ "If you want to abort press CANCEL.";
	public static final String IMPORT_STAT = "After Import\nSchema: \t% Nodes having instances, avg. inst. per node, # inst.\n";
	public static final String IND_XDR_SCHEMA1 = "There are 2 or more XDR-Files in the selected directory.\n";
	public static final String IND_XDR_SCHEMA3 = IND_XDR_SCHEMA1 + IND_SCHEMA2;
	public static final String IND_XSD_SCHEMA1 = "There are 2 or more XSD-Files in the selected directory.\n";
	public static final String IND_XSD_SCHEMA3 = IND_XSD_SCHEMA1 + IND_SCHEMA2;
	public static final String INFORMATION = "Information";
	public static final String INT_MATCHRESULT = "Intended Matchresult";
	public static final String INTERSECT_DONE = "Intersect the selected Matchresults done.";
	public static final String INTERSECT_MATCHRESULT = "Intersect: Find the overlapping correspondences from the current Matchresult with another";
	public static final String INVERTDOMAIN_TOOLTIP = "InvertDomain: Remove matching part of the source schema";
	public static final String INVERTRANGE_TOOLTIP = "InvertRange: Remove matching part of the target schema";
	public static final String LAST_SOME_MIN = "\n\n(This process can last some minutes!)";
	public static final String LENGTH = "Length";
	public static final String LENGTH_PATH = "Length of Matchresult Paths";
	public static final String LOAD_MATCHRESULT_DB = "Load a Matchresult from database.";
	public static final String LOAD_MATCHRESULT_FILE = "Load a Matchresult from textfile.";
	public static final String LOAD_SCHEMA_DB_ABORTED = "Loading Schema to the database was aborted.";
	public static final String MATCHRESULT_BIG = "MATCHRESULT";
	public static final String MATCHRESULT_DB = "Matchresult(s) saved to the database.";
	public static final String MATCHRESULT_DB_FAILED = "Error saving the Matchresult to the database.";
	public static final String MATCHRESULT_LOADED_DB = "The chosen Matchresult was loaded from database.";
	public static final String MATCHRESULT_LOADED_FILE = "The chosen Matchresult was loaded from file: ";
	public static final String MATCHRESULT_LOADED_FILE_ERROR = "Some of the matchresults could not be imported.";
	public static final String MATCHRESULT_LOADED_FILE_ERROR2 = "No matchresults could be imported. The file is maybe a wrong format!";
	public static final String MATCHRESULT_NOT_SAVED_NO_FILE = "Matchresult wasn't saved - no file name specified.";
	public static final String MATCHRESULT_SAVED_FILE = "The chosen Matchresult was saved to file: ";
	public static final String MATCHRESULT_SAVED_FILE_ERROR = "An Error occured - the Matchresult couldn't be saved.";
	public static final String MATCH_HINT = "Match complete schemas or select nodes from each schema to match between corresponding fragments";
	public static final String MATCH_STRATEGY = "Match Strategy";
	public static final String MATCHER_CONF_DB = "Matcher Configuration saved to database.";
	public static final String MATCHER_CONF_ERROR = "An Error accured - the Matcher Configuration couldn't be saved.";
	public static final String MATCHER_CONF_FILE = "Matcher Configuration saved to file ";
	public static final String MATCHER_CONF_NOT = "Matcher Configuration wasn't saved - no file name specified.";
	public static final String MATCHER_CONF_UPDATE = "The Matcher Configuration was updated at the database.";
	public static final String MATCHING_DONE = "Matching is done.";
	public static final String MATCHING_STOP = "Matching stopped!";
	public static final String MAXIMUM = "maximum";
	public static final String MERGE_DONE = "Merge the selected Matchresults done.";
	public static final String MERGE_MATCHRESULT = "MatchresultMerge: Merge the current Matchresult with another";
	public static final String MIDDLE_SCHEMA_LOADED = "An Ontology or Pivot Schema has been loaded.";
	public static final String MIDDLESCHEMA = "ONTOLOGY/PIVOT";
	public static final String NEW_DB = "Do you really want to delete the database and create a new one?\n"
			+ "Every saved schema and matchresult is going to be deleted during this process.\n\n"
			+ GUIConstants.OK_CONTINUE;
	public static final String NEW_INSTANCEMATCHER = "New InstanceMatcher" + DOTS;
	public static final String NEW_MATCHER_CANCELED = "Creating a New Matcher was canceled.";
	public static final String NEW_ReuseStrategy = "New ReuseStrategy" + DOTS;
	public static final String NO_COMBINEDREUSE = "Combined Reuse was not possible!";
	public static final String NO_ERRORS = "No errors.";
	public static final String NO_MATCHRESULT = "There is no (temporary) Matchresult.";
	public static final String NO_MATCHRESULT_CHOSEN = "No Matchresult(s) chosen.";
	public static final String NO_MATCHRESULT_SEL = "There is no matchresult selected!";
	public static final String NO_MATCHRESULTS = "There is no Matchresult in the Repository that could be used for Reuse.";
	public static final String NO_MATCHER = "There are no Matcher.";
	public static final String NO_MATCHER_CHOSEN = "No Matcher was chosen.";
	public static final String NO_VARIABLE_CONF = "There are no Workfow variables.";
	public static final String NO_MATCHRESULT_IN_DB = "There exists no Matchresult in the database, that could be loaded!";
	public static final String NO_MIDDLE_SCHEMA = "There is no Ontology or Pivot Schema loaded.";
	public static final String NO_MR = "There are no Matching Results.";
	public static final String NO_OTHER_MATCHRESULTS = "There are no other temporary Matchresults.";
	public static final String NO_RESULTS = "There were no results!";
	public static final String NO_SCHEMA = "No Schema loaded...";
	public static final String NO_SCHEMA_FILES = "The specified directory didn't include schema files.";
	public static final String NO_SCHEMA_IN_DB = "There exists no Schema in the database. Please import at least one Schema!";
	public static final String NO_SCHEMA_SEL = "There is no schema selected!";
	public static final String NO_SCHEMAS = "No Schemas loaded!";
	public static final String NO_SRC_SCHEMA = "There is no Source Schema loaded.";
	public static final String NO_TRG_SCHEMA = "There is no Target Schema loaded.";
	public static final String NO_WSCHEMA = "There is no (temporary) Schema.";
	public static final String NO_XSD_XSDR = "The specified directory didn't include files of the type XSD or XDR.";
	public static final String NODE_MATCHER = "Node Matcher";
	public static final String NODE_MATCHER_PLUS = "Node Matcher +";
	public static final String NOT_ENOUGH_MATCHRESULT = "Not enough Matchresult were chosen.";
	public static final String NOT_LOADED = "Schema wasn't loaded to database -";
	public static final String NOT_LOADED_NO_URI = NOT_LOADED + "no uri specified.";
	public static final String NOT_LOADED_NO_NAME = NOT_LOADED
			+ "no file name specified.";
	public static final String NOT_LOADED_ODBC_INVALID = NOT_LOADED
			+ "Odbc entry invalid.";
	public static final String NOT_LOADED_ODBC_SPECIFY = "Please specify a valid odbc entry and schema for import.";
	public static final String NOT_LOADED_WRONG_DIR = NOT_LOADED
			+ " the selected file/directory has to be in the directory 'Sources'.";
	public static final String NOTHING_SEL = "There is nothing selected!";
	public static final String OK_CONTINUE = "Press OK to Continue.";
	public static final String OP_LOADED_DB = "Loaded (DB)";
	public static final String OP_LOADED_FILE = "Loaded (File)";
	public static final String OPEN_AS_SOURCE = "Open as Source Schema";
	public static final String OPEN_AS_TARGET = "Open as Target Schema";
	public static final String OPEN_MATCHRESULT = "Open selected Matchresult";
	public static final String OPERATION_CANCELED = "Operation was canceled.";
	public static final String OPERATION_REQUIRES = "This operation requires input matchresults between the same source and target schema, respectively";
	public static final String PAIR_EXISTS = "This pair already exists: ";
	public static final String PIVOTSCHEMA_CHOOSE = "Select Pivotschema" + DOTS;
	public static final String PIVOTSCHEMA_VIEW = "Show selected Pivot Schema";
	public static final String PRESS_NO_TO_USE_ALL = "\n(Press No to match the whole schemas.)";
	public static final String PRESS_NO_TO_USE_FRAG = "\n(Press No to use the selected Fragment Identification.)";
	public static final String QUESTION = "Question";
	public static final String QUESTION_CLOSE_SRC = "Do you really want to close the Source Schema and all temporary Matchresults?"
			+ LINEBREAK + OK_CONTINUE;
	public static final String QUESTION_CLOSE_TRG = "Do you really want to close the Target Schema and all temporary Matchresults?"
			+ LINEBREAK + OK_CONTINUE;
	public static final String QUESTION_DELETE_VARIABLE = "Do you really want to delete the Variable: ";
	public static final String RANGE_TOOLTIP = "Range: Retain matching part of the target schema";
	public static final String RESTRICT_RESULTS = "Restrict Results to selected fragment(s)!";
	public static final String REUSE_ABORTED = "Reuse Matchresults aborted - for this match task at the moment not possible.";
	public static final String REUSE_CANCELED = "Reuse Matchresults was canceled.";
	public static final String REUSE_FOR = "Reuse for (";
	public static final String REUSE_INFO = "Solve current match task using existing results";
	public static final String REUSE_MATCHRESULTS = "Reuse Matchresults";
	public static final String REUSE_MATCHRESULTS_DONE = "Reuse Matchresults done";
	public static final String REUSE_NOTHING_SEL = "Reuse Matchresults aborted - there was nothing selected.";
	public static final String SAVE_MATCHRESULT_DB = "Save current Matchresult to the database";
	public static final String SAVE_SCHEMA = "Save current Schema to the database";
	public static final String SAVE_SCHEMA_DB = "Save Schema into DB";
	public static final String SAVE_SCHEMA_MATCHRESULT = "Save current Schema or Matchresult to the database";
	public static final String SAVE_MATCHRESULT_FILE = "Save current Matchresult to a textfile";
	public static final String SCHEMA_DB = "Schema saved to the database.";
	public static final String SCHEMA_DB_FAILED = "Error saving the Schema to the database.";
	public static final String SEE_ABOVE = "(Configure this Strategy => see above)";
	public static final String SEL_SRC_FR = "selected source fragments: ";
	public static final String SEL_SRC_FRAG_AGAINST_WHOLE = "Do you want to use the selected source Fragment(s) "
			+ "for the matching process and match it against the whole target schema?";
	public static final String SEL_TRG_FR = "selected target fragments: ";
	public static final String SEL_TRG_FRAG_AGAINST_WHOLE = "Do you want to use the selected target Fragment(s) "
			+ "for the matching process and match it against the whole source schema?";
	public static final String SELECT_A_NODE = "Select a node to display its correspondences";
	public static final String SELECT_SIM_CONST = "For Saving you have to select a similarity measure and/or constituent matcher.";
	public static final String SELECT_FRAG = "Please select a Fragment from";
	public static final String SELECT_ONE = "Information: For Combined Reuse please select only one matchresult path!";
	public static final String SHOW_EX_MATCHER = "Show Existing Matchers.";
	public static final String SHOW_EX_WORKFLOWVARIABLES = "Show Existing Workflow Variables.";
	public static final String SHOW_NEXT_LINE_DOWN = "Show next corresponding element for the selected one (down)";
	public static final String SHOW_NEXT_LINE_UP = "Show next corresponding element for the selected one (up)";
	public static final String SIM = "Similarity Measure";
	public static final String SIM_OR_CONST = "Similarity Measure OR Constituent Matcher";
	public static final String SMERGE_TOOLTIP = "SchemaMerge: Merge source and target schema according to the current matchresult";
	public static final String SOURCESCHEMA = "SOURCE";
	public static final String SPECIFY_NUMBER = "The specified values for selection have to be a number.";
	public static final String SPECIFY_WEIGHTS = "For Saving you have to specify weights.";
	public static final String SPLITPANE_SWAP = "Switch between two- and three-window display mode";
	public static final String SRC_FRAGMENTS = "source fragments:";
	public static final String SRC_SCHEMA = " the source schema";
	public static final String SRC_SCHEMA_LOADED = "A Source Schema has been loaded.";
	public static final String STEP1 = "Step 1";
	public static final String STEP2 = "Step 2";
	public static final String STEP3 = "Step 3";
	public static final String STEP4 = "Step 4";
	public static final String STEPBYSTEP_COMBINEDREUSE = "Step by Step: Execute Combined Reuse Process";
	public static final String STEPBYSTEP__COMBINEDREUSE_CANCELED = "Execute Fragment Matching Process \"Step by Step\" was canceled.";
	public static final String STEPBYSTEP_FRAGMENTMATCHING = "Step by Step: Execute Fragment Matching Process";
	public static final String STEPBYSTEP__FRAGMENTMATCHING_CANCELED = "Execute Fragment Matching Process \"Step by Step\" was canceled.";
	public static final String STOP_MATCHING = "Stop Matching Process";
	public static final String STRATEGIES_DEFAULT = "The Strategies were set to their default values.";
	public static final String STRATEGY = "Strategy";
	public static final String SWAP_DONE = "Swap successfully done.";
	public static final String SWAP_NOTHING = "There was nothing to swap.";
	public static final String TARGETSCHEMA = "TARGET";
	public static final String TAXONOMY_VIEW2 = "Show selected Taxonomy";
	public static final String TEST_MATCHRESULT = "Test Matchresult";
	public static final String TOPK = "Top K";
	public static final String TOPK_PATHS = "Top K Matchresult Paths";
	public static final String TRG_FRAGMENTS = "target fragments:";
	public static final String TRG_SCHEMA = " the target schema";
	public static final String TRG_SCHEMA_LOADED = "A Target Schema has been loaded.";
	public static final String TYPE_MATCHER_NAME = "For Saving you have to type a name for the matcher.";
	public static final String USE_SEL_FRAG = "Do you want to use the selected Fragments for the matching process?";
	public static final String WARNING = "Warning";
	public static final String WEIGHT_LESS = "One weight less";
	public static final String WEIGHT_MORE = "One Weight more";
	public static final String WORD_NOT_VALID = "An empty word is not valid! Please type at least one letter.";
	public static final String WORD1 = "Word 1";
	public static final String WORD2 = "Word 2";
	// ************************************************************
	// menu
	public static final String ABB = "Abbreviations";
	public static final String ABOUT = "About";
	public static final String AUX_INFO = "Auxiliary Info";
	public static final String BETWEEN_INNER = "Between Inner Nodes";
	public static final String BETWEEN_LEAF = "Between Leaf Nodes";
	public static final String BETWEEN_MIXED = "Between Leaf and Inner Nodes";
	public static final String CHANGE_DOMAIN = "Change domain" ;
	public static final String CLOSE = "Close";
	public static final String COMPARE_TMP = "Compare";
	public static final String CONFIGURE = "Configure Strategy";
	public static final String CONFIGURE_INST = "Individual Instance-based Strat.";
	// Dung Phan 17:31 16.06.2009
	public static final String CONFIGURE_ML = "Machine Learning Matching";
	public static final String CONFIRM_CORRESPONDENCE = "Confirm Correspondence";
	public static final String CONFIRM_CORRESPONDENCES = "Confirm all Correspondence for this node";
	public static final String CREATE_CORRESPONDENCE = "Create Correspondence";
	public static final String CREATE_DOMAIN = "Create Domain";
	public static final String DEL = "Delete";
	public static final String DEL_CORRESPONDENCE = "Delete Correspondence";	
	public static final String DEL_CORRESPONDENCES = "Delete all Correspondences for this node";
	public static final String DEL_CORRESPONDENCES_BESIDE = "Delete all Corresp. for this node - beside selected";		
	public static final String DEL_DB = "Clean Repository";
	public static final String DEL_DOMAIN = "Delete Domain";
	public static final String DEL_VARIABLE = "Delete Variable";	
	public static final String DIFF_TMP = "Diff";
	public static final String DOMAIN = "Domain";
	public static final String DUPLICATE_TMP = "Duplicate";
	public static final String EDIT_TMP = "Edit Mode";
	public static final String EX = "Execute";
//	public static final String EX_MATCHER = "Existing Matchers";
	public static final String EX_WORKFLOWVARIABLES = "Workflow Variables";
	public static final String EXTRACT = "Extract Part";
	public static final String EXIT = "EXIT";
	public static final String EXPORT_MAPFILE = "Export File (TXT/ASC)";
	public static final String FOLD = "Fold";
	public static final String FOLD_FRAGMENT_CHILDREN = "Fold Fragment Children";
	public static final String IMPORT_EXAMPLES = "Import Examples";
	public static final String IMPORT_FILE = "Import File (XSD/XDR/OWL/CSV/SQL)"; //hung
	public static final String IMPORT_MAPFILE = "Import File (TXT/ASC)";
	public static final String IMPORT_MATCHER = "Reset Matchers";
	public static final String IMPORT_WORKFLOW = "Reset Workflow Variables";
	public static final String IMPORT_ODBC = "Import DB (ODBC)";
	public static final String IMPORT_URI = "Import URI (OWL)";
	public static final String IMPORT_USERMATCHER = "Import UserMatcher";	
	public static final String INFO = "Info";
	public static final String INFO_INST = "Instance Information";
	public static final String INFO_MR = "Matchresult Information";
	public static final String INSTANCEMATCHER = "InstanceMatcher";
	public static final String INSTANCES = "Instances";
	public static final String INTERSECT_TMP = "Intersect";
	public static final String INVERTDOMAIN = "InvertDomain";
	public static final String INVERTRANGE = "InvertRange";
	public static final String LOAD = "Load";
	public static final String LOAD_SRC = "Open Source";
	public static final String LOAD_TRG = "Open Target" ;
	public static final String NEW_MATCHER = "New Matcher" ;
	public static final String ReuseStrategy = "ReuseStrategy";
	public static final String VARIABLE_HIERARCHY = "Variable Hierarchy";
	public static final String MATCHER = "Matcher";
	public static final String MATCHRESULT = "Matchresult";
	public static final String MATCHRESULT_CORRESP = "Matchresult Correspondences";
    public static final String MATCHRESULT_XSLT_RL = "Right to Left XSLT";
    public static final String MATCHRESULT_XSLT_LR = "Left to Right XSLT";
	public static final String MATCHRESULT_INFO = "Matchresult Info";
	public static final String MATCHRESULTS_ = "Matchresults";
	public static final String MATCH = "Match";
	public static final String MMERGE = "Merge";
	public static final String MODE = "Mode";
	public static final String PARSE_ADD_FILE = "Parse Instance Files (XML)";
	public static final String PARSE_ODBC = "Parse Instances (ODBC)";
	public static final String PARSE_ORG_FILE = "Parse Schema File (OWL/RDF, CSV, SQL)";
	public static final String PIVOTSCHEMA = "Pivot Schema";
	public static final String PREP = "Preprocessing";
	public static final String RANGE = "Range";
	public static final String RETAIN_CORRESPONDENCES = "Retain only Fragment Correspondences";		
	public static final String REMOVE = "Remove All";
	public static final String REMOVE_CORRESPONDENCES = "Remove Fragment Correspondences";	
	public static final String REP = "Repository";
	public static final String RESULTS_RDF_ALIGNMENT = "Export RDF";
	public static final String RESULTS_SPICY = "Export Spicy";
	public static final String REUSE_MANUAL = "Reuse (manual)";
	public static final String SAVE = "Save";
	public static final String SCHEMA = "Schema";
	public static final String SCHEMA_EXPORT = "Choose Schema to export to file";
	public static final String SCHEMA_FILE = "Export";
	public static final String SCHEMA_INFO = "Schema Info";
	public static final String SCHEMA_OPEN = "Open";
	public static final String SCHEMAS = "Schemas";
	public static final String SMERGE = "SchemaMerge";
	public static final String SET_CORRESPONDENCE_TYPE = "Set Correspondence Type";	
	public static final String SET_DEFAULT = "Set Default";
	public static final String SET_HIGHEST_SIMVALUE = "Set Highest Similarity Value";
	public static final String SOURCE = "Source";
	public static final String SHOW_INST = "Show Instances";
	public static final String FRAGMATCHING_MANUAL = "Fragment Matching (manual)";
//	public static final String STEPBYSTEP_COMBREUSE = "Step by Step Combined Reuse";
	public static final String STOP = "Stop";
	public static final String STRAT = "Strategy";
	//	public static final String SWAP = "Swap";
	public static final String SYN = "Synonyms";
	public static final String TARGET = "Target";
	public static final String TAXONOMY_CHOOSE = "Select Taxonomy";
	public static final String UNFOLD = "Unfold";
	public static final String UNFOLD_FRAGMENT = "Unfold Fragment";
	public static final String UNLOCK_GUI = "Unlock GUI";
	public static final String WORKFLOW = "Workflow";
	public static final String VIEW = "View";
	// ************************************************************
	// dlg_reuse
	public static final String B_HTML = " Matchresult(s)</b></html>";
	public static final String COMPLETE = "<html><font size=\"+1\">Complete</font></html>";
	public static final String DIRECT_RESULTS = "<html><font size=\"+1\">Direct Results</font></html>";
	public static final String HTML_B = "<html><b>";
	public static final String INCOMPLETE = "<html><font size=\"+1\">Incomplete</font></html>";
	public static final String MATCHTASKS = " Match Task(s) + ";
	// ************************************************************
	// dlg_combinedreuse
	public static final String COMPLETE_PATH = "You can see the chosen complete Matchresult Path.";
	public static final String INCOMPLETE_PATH = "You can see the chosen incomplete Matchresult Path.";
	public static final String LACKING_SOURCE = "The lacking matchresult from the source schema to its other version has been calculated.";
	public static final String LACKING_TARGET = "The lacking matchresult from the target schema to its other version has been calculated.";
	public static final String BOTH_COMPOSED = "Both matchresults have been composed to a new matchresult.";
	public static final String LOADED_COMPOSED = "Boths Matchresults have been loaded and composed to a new matchresult.";
	public static final String INTERN1= "Intern Part 1";
	public static final String INTERN2= "Intern Part 2";
	public static final String UNMATCHED_SOURCE = "On the left side you can see the unmatched part of the source schema.";
	public static final String UNMATCHED_TARGET = "On the right side you can see the unmatched part of the target schema.";
	public static final String UNMATCHED_SOURCE2 = "It has been matched to the target schema.";
	public static final String UNMATCHED_TARGET2 = "It has been matched to the source schema.";
	public static final String CR_CONTINUE = "\nTo run the Combined Reuse Process again click Continue.";
	// ************************************************************
	// compare
	public static final String COMPARE_INT_MATCHRESULT = "Intended Matchresult: ";
	public static final String COMPARE_TEST_MATCHRESULT = "\nTest Matchresult: ";
	public static final String COMPARE_LINE = "\n=============================\n";
	public static final String COMPARE_LINE2 = "=============================\n";
	public static final String COMPARE_LINE3 = "\n=============================";
	public static final String COMPARE_SOURCE = "\nSource: ";
	public static final String COMPARE_TARGET = "\nTarget: ";
	public static final String COMPARE_PRECISION = "\nPrecision: ";
	public static final String COMPARE_RECALL = "\nRecall: ";
	public static final String COMPARE_FMEASURES = "\nF-Measures: ";
	// not used: Overall
//	public static final String COMPARE_OVERALL = "\nOverall: ";
	public static final String COMPARE_INT_C = "\nintended corresp.: ";
	public static final String COMPARE_TEST_C = "\ntest correspondences: ";
	public static final String COMPARE_CORRECT = "\ncorrect matches in test: ";
	// ************************************************************
	// Matcher Table => Headlines
	public static final String AGGREGATION = "Aggregation";
	// Dung Phan 21.06.2009 01:13
	public static final String ATTRIBUTE_VALUE = "Attribute value";
	public static final String BASEALGORITHM = "BaseAlgorithm";
	public static final String CLASSFILE = "Class";
	public static final String COMBINATION = "Combination";
	public static final String COMPOSITION = "Composition";
	// Dung Phan 18.07.2009 15:26
	public static final String CONTENT_LEARNER_ATTRIBUTE = "Content Learner Attribute";
	public static final String CONST_MATCHER = "Constituent Matchers/Similarity Measure";
	public static final String CONSTITUENTS = "Constituents";
	public static final String DIRECTION = "Direction";
	// Dung Phan 21.06.2009 01:13
	public static final String FILTER = "Filter";
	public static final String FILTER2 = "and filter";
	public static final String INFOMATCHER = "Info";
	// Dung Phan 21.06.2009 01:13
	public static final String TOKENIZER = "Tokenizer";
	// Dung Phan 21.06.2009 01:13
	public static final String STEMMER = "Stemmer";
	
	public static final String JAR = "Jar";
	//Dung Phan 21.06.2009 00:53
	public static final String LANGUAGE = "Content language";
	public static final String LEARNER_OPERATOR = "Learner";
	public static final String MATCHER_COMBINATION = "Matcher";
	public static final String MEASURE = "Measure";
	public static final String NAME = "Name";
	// Dung Phan 18.07.2009 15:26
	public static final String NAME_LEARNER_ATTRIBUTE = "Name Learner Attribute";
	public static final String PREPROCESSING = "Preprocessing";
	public static final String SELECTION = "Selection";
	public static final String TRAINING_LEARNER = "Training Learner";
	// ************************************************************
	// Buttons
	public static final String BUTTON_ADD = "Add";
	public static final String BUTTON_ADVANCED = "Advanced";
	public static final String BUTTON_AGAIN = "Again";	
	public static final String BUTTON_BACK = "Back";
	public static final String BUTTON_BASIC = "Basic";
	public static final String BUTTON_CANCEL = "Cancel";
	public static final String BUTTON_CHANGE = "Change";
	public static final String BUTTON_COMPARE = "Compare";
	public static final String BUTTON_CONFIGURE = "Configure";
	public static final String BUTTON_CONTINUE = "Continue";
	public static final String BUTTON_CREATE = "Create";
	public static final String BUTTON_DEFAULT = "Restore Defaults";
	public static final String BUTTON_DELETE = "Delete";
	public static final String BUTTON_DIFF = "Diff";
	public static final String BUTTON_DONE = "Done";	
	public static final String BUTTON_EXECUTE = "Execute";
	public static final String BUTTON_IMPORT = "Import";
	public static final String BUTTON_LOAD = "Load";
	public static final String BUTTON_MATCH = "Match";
	public static final String BUTTON_OK = "OK";
	public static final String BUTTON_SAVE = "Save";
	public static final String BUTTON_SAVEEXECUTE = "Save & Execute";
	public static final String BUTTON_SAVE_AS = "Save As";
	public static final String BUTTON_SAVE_DB = "Save (DB)";
	public static final String BUTTON_SEARCH = "Search...";
	//	public static final String BUTTON_SAVE_FILE = "Save (File)";
	public static final String BUTTON_STOP = "Stop";
	public static final String BUTTON_UDPATE_DB = "Update (DB)";
	public static final String BUTTON_VALIDATE = "Validate";
	public static final String BUTTON_VIEW = "View";
	// ************************************************************
	// Numbers
	public static final String Number_0 = "0";
	public static final String Number_0_0 = "0.0";
	public static final String Number_0_01 = "0.01";
	public static final String Number_0_1 = "0.1";
	public static final String Number_0_5 = "0.5";
	//	// ************************************************************
	//	// ManagementPane
	public static final String M_AUTHOR = "Author";
	public static final String M_COMMENT = "Comment";
	public static final String M_CONTENT = "Content";
	public static final String M_DOMAIN = "Domain";
	public static final String M_NAME = "Name";
	public static final String M_OPERATION = "Operation";
	public static final String M_PROVIDER = "Provider";
	public static final String M_SCHEMAS = "Schemas";
	public static final String M_TOTAL = "Total";
	public static final String M_URL = "Url";
	public static final String M_VERSION = "Version";
	public static final String WORKSPACE = "Workspace";
	//	// ************************************************************
	//	// PropertyPane - Node Information
	public static final String N_NAME = "Name";
	public static final String N_PATH = "Path";
	public static final String N_COMMENT = "Comment";
	public static final String N_INSTANCE_EXAMPLE = "Inst. Example";
	// ************************************************************
	// progress bar
	public static final String COMA_BUSY = "COMA++ is busy!";
	// ************************************************************
	// Fonts
	public static final String FONT_DIALOG = "Dialog";
	public static final String FONT_COURIER = "Courier";
	//	// ************************************************************
	//	// all used Icons for Tool Bars
	public static final String ICON_ARROW = "icons/Arrow.gif";
	public static final String ICON_ARROWDOWN = "icons/ArrowDown.gif";
	public static final String ICON_ARROWUP = "icons/ArrowUp.gif";
	//	public static final String ICON_C = "icons/C.gif";
	public static final String ICON_CLEAN = "icons/Clean.gif";
	public static final String ICON_CLOSE = "icons/Close.gif";
	public static final String ICON_COMPARE = "icons/Compare.gif";
	public static final String ICON_CONFIGURE = "icons/Configure.gif";
	public static final String ICON_DELETE_DB = "icons/Delete_DB.gif";
	public static final String ICON_DELETE_TMP = "icons/Delete_Tmp.gif";
	public static final String ICON_DIFFERENCE = "icons/Difference.gif";
	public static final String ICON_DUPLICATE = "icons/Duplicate.gif";
	public static final String ICON_DOMAIN = "icons/Domain.gif";
	public static final String ICON_EDIT = "icons/Edit.gif";
	public static final String ICON_EXECUTE = "icons/Execute.gif";
	public static final String ICON_EXISTING_MATCHER = "icons/ExistingMatcher.gif";
	public static final String ICON_EYE = "icons/Eye.gif";
	public static final String ICON_INSTANCE = "icons/Instance.gif";
	public static final String ICON_INSTANCE_S = "icons/InstanceS.gif";
	public static final String ICON_INSTANCE_C = "icons/InstanceC.gif";
	public static final String ICON_INTERSECT = "icons/Intersect.gif";
	public static final String ICON_INVERTDOMAIN = "icons/InvertDomain.gif";
	public static final String ICON_INVERTRANGE = "icons/InvertRange.gif";
	public static final String ICON_LESS_SMALL = "icons/LessSmall.gif";
	public static final String ICON_LINES = "icons/Lines.gif";
	public static final String ICON_MATCHRESULT_DB = "icons/MatchresultToDB.gif";
	//	public static final String ICON_MATCHRESULT_FILE = "icons/MatchresultToFile.gif";
	public static final String ICON_MERGE = "icons/Merge.gif";
	public static final String ICON_MORE_SMALL = "icons/MoreSmall.gif";
	public static final String ICON_OPENMATCHRESULT = "icons/OpenMatchresult.gif";
	public static final String ICON_OPENSOURCE = "icons/OpenSource.gif";
	public static final String ICON_OPENTARGET = "icons/OpenTarget.gif";
	public static final String ICON_RANGE = "icons/Range.gif";
	public static final String ICON_REUSE = "icons/Reuse.gif";
	public static final String ICON_SMERGE = "icons/SMerge.gif";
	public static final String ICON_SPLITPANE_TO_2 = "icons/SplitPaneTo2.gif";
	public static final String ICON_SPLITPANE_TO_3 = "icons/SplitPaneTo3.gif";
	public static final String ICON_STEPBYSTEP = "icons/StepByStep.gif";
	public static final String ICON_STEPBYSTEP2 = "icons/StepByStep2.gif";
	public static final String ICON_STOP = "icons/Stop.gif";
	// ************************************************************
	// About
	public static final String ABOUT1 = "COMA 3.0 Community Edition";
	public static final String VERSION_DATE = "Version: 3.0; (2012-04)";
	public static final String ABOUT2_1 = " This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY;";
	public static final String ABOUT2_2 = " without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.";
	public static final String ABOUT2_3 = " See the GNU Affero General Public License for more details";
	public static final String ABOUT3 = "WDI Lab, University of Leipzig, Germany";
	public static final String ABOUT4 = "http://wdilab.uni-leipzig.de";
//	public static final String COMA_ASCIIART = "________________ ______  __________               \n__  ____/__  __ \\___   |/  /___    |______ ______ \n_  /     _  / / /__  /|_/ / __  /| |___/ /____/ /_\n/ /___   / /_/ / _  /  / /  _  ___ |/_  __//_  __/\n\\____/   \\____/  /_/  /_/   /_/  |_| /_/    /_/   \n";
	public static final String COMA_ASCIIART = " _____ ________  ___ ___    _____   _____ \n/  __ \\  _  |  \\/  |/ _ \\  |____ | |  _  |\n| /  \\/ | | | .  . / /_\\ \\     / / | |/' |\n| |   | | | | |\\/| |  _  |     \\ \\ |  /| |\n| \\__/\\ \\_/ / |  | | | | | .___/ /_\\ |_/ /\n \\____/\\___/\\_|  |_|_| |_/ \\____/(_)\\___/ \n";
}