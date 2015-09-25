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

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import de.wdilab.coma.insert.metadata.CSVParser;
import de.wdilab.coma.insert.metadata.ODBCParser;
import de.wdilab.coma.insert.metadata.OWLParser_V3;
import de.wdilab.coma.insert.metadata.SQLParser;
import de.wdilab.coma.insert.metadata.XDRParser;
import de.wdilab.coma.insert.metadata.XSDParser;
import de.wdilab.coma.insert.relationships.MatchResultParser;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * SaveSchemaToDBThread is a Thread, that saves a Schema to the database (while
 * the GUI still can be used)
 * 
 * @author Sabine Massmann
 */
public class SaveToDBThread extends Thread {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	public static  final int STATE_IMPORT_FILE = 0;
	public static  final int STATE_IMPORT_OWL_URI = 1;
	public static  final int STATE_IMPORT_MATCHRESULT = 2;
	public static  final int STATE_IMPORT_CONTEST = 3;
	public static  final int STATE_IMPORT_MATCHRESULTS = 4;
	public static final int STATE_IMPORT_ODBC = 5;
	public static  final int STATE_IMPORT_SCHEMA = 6;
	public static  final int STATE_IMPORT_MATCHRESULTFILE = 7;
	public static  final int STATE_IMPORT_SCHEMAS = 8;
	//----------------------------------------------
	boolean debug = true;
	Controller controller = null;
	int state = -1;
	MatchResult matchResult = null;
	Graph graph = null;
	// for importing a list of matchResults or graphs
	ArrayList matchResults = null;
	ArrayList graphs = null;
	String[] ontologies = null;
	String ontologiesSource = null;
	String file = null;
	// ODBCParser
	String entry, user, pass, schema;
	String author, domain, version, comment, schemaName=null;
	String[] schemaNames = null;
	
	
	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public SaveToDBThread(MainWindow _mainWindow, Controller _controller,
			int _state) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
	}
	
	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public SaveToDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, String _fileName) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		file=_fileName;
	}

	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public SaveToDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, String _source, String[] _owl, String _file) {
		super(_mainWindow);
		ontologiesSource = _source;
		ontologies = _owl;
		file = _file;
		controller = _controller;
		state = _state;
	}

	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public SaveToDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, String _entry, String _user, String _pass,
			String _schema) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		entry = _entry;
		user = _user;
		pass = _pass;
		schema = _schema;
	}

	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public SaveToDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, MatchResult _result) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		matchResult = _result;
	}

	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public SaveToDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, Graph _graph) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		graph = _graph;
	}

	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public SaveToDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, boolean _isSchema, ArrayList _list) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		if (_isSchema)
			graphs = _list;
		else
			matchResults = _list;
	}

	
	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public SaveToDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, String _file, String _author, String _domain, String _version, String _comment) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		file = _file;
		author = _author;
		domain= _domain;
		version =_version;
		comment =_comment;
	}
	
	
	/*
	 * Constructor of SaveSchemaToDBThread
	 */
	public SaveToDBThread(MainWindow _mainWindow, Controller _controller,
			int _state, String _file, String _author, String _domain, String _version, String _comment, String _schemaName) {
		super(_mainWindow);
		controller = _controller;
		state = _state;
		file = _file;
		author = _author;
		domain= _domain;
		version =_version;
		comment =_comment;
		schemaName = _schemaName;
	}
	
	/*
	 * to start the Thread call start()
	 */
	public void run() {
		controller.getManagementPane().setMenuStateDB(true);
		controller.getMainWindow().getNewContentPane()
				.setProgressBar(true);
		boolean error = false;
		switch (state) {
			case STATE_IMPORT_FILE :
				importFile();
				controller.getManagementPane().setSelectedTab(
						ManagementPane.REPOSITORY);
				break;
			case STATE_IMPORT_OWL_URI :
				//importOWL_URI();
				//importOntology();
				// import all uris that are given in the array above
				//importOWL_URI(owl);
				controller.getManagementPane().setSelectedTab(
						ManagementPane.REPOSITORY);
				break;
			case STATE_IMPORT_CONTEST :
				// import all uris that are given in the array above
				importOWL_URI(ontologies);
				controller.updateAll(true); //parse and import
				controller.getManagementPane().setSelectedTab(
						ManagementPane.REPOSITORY);
				break;
			case STATE_IMPORT_MATCHRESULT :
				error = importMatchResult(matchResult);
				break;
			case STATE_IMPORT_MATCHRESULTS :
				error = importMatchResults(matchResults);
				break;
			case STATE_IMPORT_ODBC :
				importODBC();
				controller.getManagementPane().setSelectedTab(
						ManagementPane.REPOSITORY);
				break;
			case STATE_IMPORT_SCHEMA :
//				error = importSchema(graph);
				break;
			case STATE_IMPORT_SCHEMAS :
//				error = importSchema(graphs);
				break;
			case STATE_IMPORT_MATCHRESULTFILE :
				importMatchresultFile();
				break;
		}
		if (!error) {
			controller.getManagementPane().setSelectedTab(
					ManagementPane.REPOSITORY);
		}
		//		file:/E:/EON-Contest/102/onto.rdf
		//		file:/E:/beer.owl
		controller.getManagementPane().setMenuStateDB(false);
		controller.getMainWindow().getNewContentPane().setProgressBar(
				false);
	}

	/*
	 * save a new schema into the data base
	 */
	private void importOWL_URI(String[] _owluris) {
		if (_owluris != null) {
			OWLParser_V3 parser = new OWLParser_V3(true);
			for (int i = 0; i < _owluris.length; i++) {
				// start owl parser with entered uri as string
				//System.out.println("User entered URI: " + owluris[i]);
				String current = null;
				if (ontologies != null) {
					current = ontologiesSource.concat(_owluris[i]);
				} else {
					current = _owluris[i];
				}
//				if (file!=null){
//				current = current.concat(file);
//				}
				System.out.println(current);
//								parse.OWLParser.parseWrapper(current, null, false, true,
//										true, false);
//				OWLParserHai.parseOWLOnto(current);
//				OWLParser.parseOWLOnto(current,null,null,null,null,schemaName);
//				OWLParser.parseOWLOnto(current,null, false, true, false, false,
//						null,null,null,null,schemaName);
				parser.parseSingleSource(current, schemaName);
			}
//			controller.updateAll(true); //parse and import
		} else {
			controller.setStatus(GUIConstants.NOT_LOADED_NO_NAME);
		}
	}

	/*
	 * import an ODBC database into the data base
	 */
	private void importODBC() {
		if (entry != null && entry.length() > 0 && schema != null
				&& schema.length() > 0) {
			System.out.println("Import schema from ODBC: [" + entry + "]["
					+ user + "][" + pass + "][" + schema + "]");
			ODBCParser parser = new ODBCParser(true);
			parser.parseSingleSource(entry, user, pass,
					schema);
			controller.updateAll(true);
//			controller.setStatus(GUIConstants.NOT_LOADED_ODBC_INVALID);
		} else
			controller.setStatus(GUIConstants.NOT_LOADED_ODBC_SPECIFY);
	}

	/*
	 * private MatchResult checkMatchresult(MatchResult _result){ if
	 * (controller.getManager().getSourceRel(_result.getSourceGraph().getSource(),
	 * _result.getTargetGraph().getSource(), _result.getResultName())!=null){ //
	 * allready a matchresult with this name and schemas in the database String name =
	 * _result.getResultName(); int count = 1; String newName = name +
	 * GUIConstants.UNDERSCORE + count; while
	 * (controller.getManager().getSource(newName) != null) {
	 * count++; newName = name + GUIConstants.UNDERSCORE + count; }
	 * _result.setResultName(newName); String message = "There already exists a
	 * matchresult with the name: " + name + GUIConstants.LINEBREAK+ "The name has been
	 * changed to: "+ newName;
	 * JOptionPane.showMessageDialog(controller.getMainWindow(),
	 * message,GUIConstants.INFORMATION, JOptionPane.INFORMATION_MESSAGE); } return
	 * _result; }
	 */
	private boolean checkMatchresult(MatchResult _result) {
		if (_result == null)
			return false;
		String name = _result.getName();
		if (controller.getManager().getAccessor().getSourceRelIdWithName(
				_result.getSourceGraph().getSource(),
				_result.getTargetGraph().getSource(), name) != SourceRelationship.UNDEF) {
			String message = "There already exists a matchresult with the name: "
					+ name
					+ GUIConstants.LINEBREAK
					+ "Please specify a unique name for the matchresult to be saved";
			JOptionPane.showMessageDialog(controller.getMainWindow(),
					message, GUIConstants.INFORMATION,
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}

//	private boolean checkSchema(Graph _graph) {
//		if (_graph == null)
//			return false;
//		String name = _graph.getSource().getName();
//		if (controller.getManager().getFirstSource(name) != null) {
//			String message = "There already exists a schema with the name: "
//					+ name + GUIConstants.LINEBREAK
//					+ "Please specify a unique name for the schema to be saved";
//			JOptionPane.showMessageDialog(controller.getMainWindow(),
//					message, GUIConstants.INFORMATION,
//					JOptionPane.INFORMATION_MESSAGE);
//			return false;
//		}
//		return true;
//	}

//	/*
//	 * save a given Schema to the data base
//	 */
//	private boolean importSchema(Graph _graph) {
//		controller.setStatus(GUIConstants.SAVE_SCHEMA);
////		boolean ok = checkSchema(_graph);
////		if (ok) {
//			controller.getManager().saveGraph(_graph);
//			controller.setStatus(GUIConstants.SCHEMA_DB);
//			//  load the Respository new
//			controller.updateAll(false); //no parse or import
////		} else {
////			controller.setStatus(GUIConstants.SCHEMA_DB_FAILED);
////		}
////		return !ok;
//		return false;
//	}

//	private boolean importSchema(ArrayList _list) {
//		if ((_list == null) || (_list.size() == 0)) {
//			return true;
//		}
//		boolean check = true;
//		for (int i = 0; i < _list.size(); i++) {
//			Graph graph = (Graph) _list.get(i);
////			boolean ok = checkSchema(graph);
////			if (ok) {
//				controller.setStatus(GUIConstants.SCHEMA_DB);
//				controller.getManager().saveGraph(graph);
////			}
////			check = (check && ok);
//		}
//		//controller.updateAllOld(true, true);
//		controller.updateAll(false); //no parse or import
//		if (!check) {
//			controller.setStatus(GUIConstants.SCHEMA_DB_FAILED);
//		}
//		return !check;
//	}

	/*
	 * save a given MatchResult to the data base
	 */
	private boolean importMatchResult(MatchResult _result) {
		// done by Schema Manager
		//          if (!preprocessing.equals(GUIConstants.PREP_REDUCED))
		//              // matchResult =
		//              // manager.validateSimplifiedMatchResult(matchResult);
		//              matchResult = Manager.transformMatchResult(matchResult,
		//                      Graph.GRAPH_STATE_REDUCED);
		controller.setStatus(GUIConstants.SAVE_MATCHRESULT_DB);
		//      matchResult.print();
		boolean ok = checkMatchresult(_result);
		if (ok) {
//			controller.getManager().saveMatchResult(_result);
			controller.setStatus(GUIConstants.MATCHRESULT_DB);
			// load the Respository new
			// the loaded Schemas and created Matchresults remain in the mainwindow
			//controller.updateAllOld(true, true);
			controller.updateAll(false); //no parse or import
		} else {
			controller.setStatus(GUIConstants.MATCHRESULT_DB_FAILED);
		}
		return !ok;
	}

	/*
	 * save a given MatchResult to the data base
	 */
	private boolean importMatchResults(ArrayList _list) {
		if ((_list == null) || (_list.size() == 0)) {
			return true;
		}
		boolean check = true;
		for (int i = 0; i < _list.size(); i++) {
			MatchResult result = (MatchResult) _list.get(i);
			boolean ok = checkMatchresult(result);
			if (ok) {
				controller.setStatus(GUIConstants.SAVE_MATCHRESULT_DB);
//				//		matchResult.print();
				controller.getManager().saveMatchResult(result);
			}
			check = (check && ok);
		}
		//controller.updateAllOld(true, true);
		controller.updateAll(false); //no parse or import
		if (!check) {
			controller.setStatus(GUIConstants.MATCHRESULT_DB_FAILED);
		}
		return !check;
	}

	/*
		 * save a new schema into the data base
		 */
		private void importFile() {
			controller.setStatus(GUIConstants.SAVE_SCHEMA_DB);
			try {
	//			File file = controller.getFile("\\Sources");
	//			System.out.println(file);
//				// open a file chooser (for only xsd and xdr files)	
//				FileDialog dlg = new FileDialog(controller
//						.getMainWindow());
//				dlg.setDirectory(controller.getDefaultDirectory().toString());
//				dlg.setVisible(true);
//				String file = dlg.getFile();
//				if (file!=null){
//					controller.setDefaultDirectory(new File(dlg.getDirectory()));
//					String filename = dlg.getDirectory() + file;
//					importFile(filename, new File (filename));
//					controller.updateAll(true); //parse and import
//				}
				JFileChooser chooser = new JFileChooser(controller.getDefaultDirectory());
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setMultiSelectionEnabled(true);
				chooser.setCurrentDirectory(controller.getDefaultDirectory());
	//			chooser.setCurrentDirectory(file);

				chooser.setFileFilter(new MyFileFilter( new String[]{Strings.XSD, Strings.XDR, Strings.OWL, 
						Strings.RDF, Strings.CSV, Strings.TXT, Strings.ASC, Strings.SQL})); //hung
				
				//TODO save once selected filter into system property to pre-select
				// next time same with directory.
				int returnVal = chooser.showOpenDialog(controller
						.getMainWindow());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					controller.setDefaultDirectory(chooser
							.getCurrentDirectory());
//					File _file = chooser.getSelectedFile();
//					String _name = chooser.getSelectedFile().toString();
					File[] _fileArray = chooser.getSelectedFiles();
					
					if (_fileArray.length==1 && _fileArray[0].isDirectory()){
//						String file = _fileArray[0].toString();
//						int index1 = file.lastIndexOf(".");
//						int index2 = file.lastIndexOf("\\");
//						int index3 = file.lastIndexOf("/");
						_fileArray = parseDirectory(_fileArray[0]);
						System.out.println();
						
					}
					
					for (int fi = 0; fi < _fileArray.length; fi++) {
						File _file = _fileArray[fi];
						String _name = _file.toString();
//						String _name = file;
//						File _file = new File(_name);
						if (_name != null) {
							//_name = _name.replaceAll(" ", "%20");
							if (!importFile(_name, _file)){
								String[] files =  _file.list();
//								ArrayList<File> filesAll = getFiles(chooser, _file);
//								if (files == null || filesAll.isEmpty()) {
//									controller.setStatus(GUIConstants.NO_SCHEMA_FILES);
//									return;
//								}
								int containingXSD = 0;
								int containingXDR = 0;
								for (int i = 0; i < files.length; i++) {
									if (files[i].toLowerCase().endsWith(
											Strings.XSD)) {
										containingXSD += 1;
									}
									if (files[i].toLowerCase().endsWith(
											Strings.XDR)) {
										containingXDR += 1;
									}
								}
								if (containingXSD > 1) {
									//	case: two or more given XSD-Files in the
									// selected
									// directory
									int res = JOptionPane.showConfirmDialog(
											controller.getMainWindow(),
											GUIConstants.IND_XSD_SCHEMA3,
											GUIConstants.QUESTION,
											JOptionPane.YES_NO_CANCEL_OPTION);
									if (res == JOptionPane.YES_OPTION) {
//										XSDParser.parseMultipleXSD(_name, null,null,null,null,schemaNames);
										XSDParser parser = new XSDParser(true);
										parser.parseMultipleSources(_name,schemaNames);
									} else if (res == JOptionPane.NO_OPTION) {
//										XSDParser.parseCompositeXSD(_name, null,null,null,null,schemaName);
										XSDParser parser = new XSDParser(true);
										parser.parseCompositeSources(_name, schemaName);
									} else {
										controller
												.setStatus(GUIConstants.LOAD_SCHEMA_DB_ABORTED);
										return;
									}
								} else if (containingXSD == 1) {
									// case: one given XSD-File in the selected
									// diretory
//									XSDParser.parseMultipleXSD(_name, null,null,null,null,schemaNames);
									XSDParser parser = new XSDParser(true);
									parser.parseMultipleSources(_name,schemaNames);
								}
								if (containingXDR > 1) {
									//	case: two or more given XDR-Files in the
									// selected
									// directory
									int res = JOptionPane.showConfirmDialog(
											controller.getMainWindow(),
											GUIConstants.IND_XDR_SCHEMA3,
											GUIConstants.QUESTION,
											JOptionPane.YES_NO_CANCEL_OPTION);
									if (res == JOptionPane.YES_OPTION) {
//										XDRParser.parseMultipleXDR(_name, null,null,null,null,schemaNames);
										XDRParser parser = new XDRParser(true);
										parser.parseMultipleSources(_name, schemaNames);
									
									} else if (res == JOptionPane.NO_OPTION) {
//										XDRParser.parseCompositeXDR(_name, null,null,null,null,schemaName);
										System.err.println("Error: XDRParser.parseCompositeXDR not supported");										
									} else {
										controller
												.setStatus(GUIConstants.LOAD_SCHEMA_DB_ABORTED);
										return;
									}
								} else if (containingXDR == 1) {
									// case: one given XDR-File in the selected
									// diretory
//									XDRParser.parseMultipleXDR(_name, null,null,null,null,schemaNames);
									XDRParser parser = new XDRParser(true);
									parser.parseMultipleSources(_name, schemaNames);
								}
//								if (containingXDR==0 && containingXSD==0){
//									for (int i = 0; i < filesAll.size(); i++) {
//										File file = filesAll.get(i);
//										importFile(file.getPath(), file);
//									}
//								}
							}
							//controller.updateAllOld(true, true);
							controller.updateAll(true); //parse and import
							if (Controller.CALCULATE_SCHEMA_SIM) {
								ArrayList results = controller
										.calculateAndSaveSchemaSim();
								importMatchResults(results);
							}
							try {
								// let some time pass
								// -> much less new exceptions (Too many connections)
								Thread.sleep(100); // milliseconds
							} catch (InterruptedException el) {
//								el.printStackTrace();
							}
						} else
							controller.setStatus(GUIConstants.NOT_LOADED_WRONG_DIR); //still
						// needed?
					} // end for loop over multiple files
				} else
					controller.setStatus(GUIConstants.NOT_LOADED_NO_NAME);
			} catch (Exception x) {
				System.out.println(x.getMessage());
				controller.setStatus(GUIConstants.LOAD_SCHEMA_DB_ABORTED);
			}
		}


		private boolean importFile(String _name, File _file){
			String nameLow = _name.toLowerCase();
			if (nameLow.endsWith(Strings.OWL)
					|| nameLow.endsWith(Strings.RDF)) {
				String[] owluris = {_file.toURI().toString()};
				importOWL_URI(owluris);
				return true;
			} else if (nameLow.endsWith(Strings.XSD)) {
				// case: one given XSD-File
//				XSDParser.parseSingleXSD(_name, null,null,null,null,null,schemaName);
				XSDParser parser = new XSDParser(true);
				parser.parseSingleSource(_name, schemaName);
				return true;
			} else if (nameLow.endsWith(Strings.XDR)) {
				// case: one given XDR-File
//				XDRParser.parseSingleXDR(_name, null,null,null,null,null,schemaName);
				XDRParser parser = new XDRParser(true);
				parser.parseSingleSource(_name, schemaName);
				return true;
//			} else if (nameLow.endsWith(GUIConstants.ASC)
//					|| nameLow.endsWith(GUIConstants.TXT)) {
//				// case: asciitree
//				ASCIITreeParser.parseASCIITree(_name);
//				return true;
			} else if (nameLow.endsWith(Strings.CSV)) {
				// case: one given CSV-File
//				CSVParser.parseSingleCSV(_name, null, null, null, null, null, schemaName);
				CSVParser parser = new CSVParser(true);
				parser.parseSingleSource(_name, schemaName);
				return true;
			}
			//hung
			else if (nameLow.endsWith(Strings.SQL)) {
				// case: one given SQL-File
//				SQLParser.parseSingleSQL(_name, null, null, null, null, null, schemaName);
				SQLParser parser = new SQLParser(true);
				parser.parseSingleSource(_name, schemaName);
				return true;
			}
			//hung
			return false;
		}
		
	private void importMatchresultFile() {
		controller.setStatus(GUIConstants.SAVE_MATCHRESULT_DB);
		try {
			// open a file chooser (for only xsd and xdr files)
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setMultiSelectionEnabled(true);
			chooser.setCurrentDirectory(controller.getDefaultDirectory());
			chooser.setFileFilter(new MyFileFilter( new String[]{Strings.TXT, Strings.ASC,Strings.RDF}));
			int returnVal = chooser.showOpenDialog(controller
					.getMainWindow());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				controller.setDefaultDirectory(chooser
						.getCurrentDirectory());
				File _fileArray[] = chooser.getSelectedFiles();
				ArrayList<MatchResult> matchResults = new ArrayList<MatchResult>();
				MatchResultParser parser1 = new MatchResultParser(controller.getManager());
				for (int fi = 0; fi < _fileArray.length; fi++) {
					File _file = _fileArray[fi];
					String _name = _file.toString();
					//System.out.println("Process file: " + _name);
					if (_name != null) {
						if (_name.toLowerCase().endsWith(Strings.TXT)
								|| _name.toLowerCase().endsWith(Strings.ASC)) {
							ArrayList<MatchResult> results = parser1.loadMatchResultFile(_name);
//							ArrayList<MatchResult> results = controller
//									.getManager().loadMatchResultFile(
//											_name);
							if (results != null)
								matchResults.addAll(results);
						} else if (_name.toLowerCase().endsWith(Strings.RDF)){
//							MatchResult result = controller
//							.getManager().loadOWLAlignmentFile(_name, null, null);							
//							if (result != null){
//								String name = _name.substring(_name.indexOf("\\")+1);
//								result.setName(name);
//								matchResults.add(result);
//							}
						}
					}
				}
				if (!matchResults.isEmpty()) {
					int saved = 0;
					for (int mi = 0; mi < matchResults.size(); mi++) {
						MatchResult result = matchResults.get(mi);
						//System.out.println("Process matchresult " +
						// result.getResultName());
//						boolean ok = 
							controller.getManager()
								.saveMatchResult(result);
//						if (ok)
//							saved++;
					}
					controller.updateAll(false);
					if (saved == matchResults.size())
						controller.setStatus(GUIConstants.MATCHRESULT_DB);
					else
						controller
								.setStatus(GUIConstants.MATCHRESULT_LOADED_FILE_ERROR);
				} else
					controller
							.setStatus(GUIConstants.MATCHRESULT_LOADED_FILE_ERROR);
			} else
				controller.setStatus(GUIConstants.MATCHRESULT_LOADED_FILE_ERROR);
		} catch (Exception x) {
			x.printStackTrace();
			controller.setStatus(GUIConstants.IMPORT_MATCHRESULTS_ABORTED);
		}
	}
	
//	private ArrayList<File> getFiles(JFileChooser _chooser, File _file){
//		ArrayList<File> filesAll = new ArrayList<File>();
//		File[] files =  _chooser.getFileSystemView()
//		.getFiles(_file, true);
//		for (int i = 0; i < files.length; i++) {
//			File file = files[i];
//			if (
////					file.toString().toLowerCase().endsWith(GUIConstants.ASC)
////					||file.toString().toLowerCase().endsWith(GUIConstants.TXT)
////					||
//					file.toString().toLowerCase().endsWith(GUIConstants.XSD)
//					||file.toString().toLowerCase().endsWith(GUIConstants.XDR)
//					||file.toString().toLowerCase().endsWith(GUIConstants.RDF)
//					||file.toString().toLowerCase().endsWith(GUIConstants.OWL)
//					){
//					filesAll.add(file);
//			} else {
//				ArrayList<File> currentFileList = getFiles(_chooser, file);
//				if (currentFileList.size()>0){
//					filesAll.addAll(currentFileList);
//				}
//			}
//		}
//		return filesAll;
//	}
	

	/*
	 * MyFileFilter extends FileFilter it allows two different Strings as ending
	 * for an accepted file
	 */
	class MyFileFilter extends FileFilter {
		String[] filter;

		public MyFileFilter(String[] _filter) {
			filter = _filter;
		}

		public boolean accept(File _file) {
			if ( _file.isDirectory()){
				return true;
			}
			String fileLow = _file.getName().toLowerCase();
			for (int i = 0; i < filter.length; i++) {
				if (fileLow.endsWith(filter[i])){
					return true;
				}
				
			}
			return false;
		}

		public String getDescription() {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < filter.length; i++) {
				buffer.append(GUIConstants.STAR + filter[i] + GUIConstants.SPACE1);
			}
			return buffer.toString();
		}
	}
	
	public static File[] parseDirectory(File _dirName) {
		ArrayList<File> list = new ArrayList<File>();
		getFiles(list, _dirName);
		if (list.size() == 0)
			return null;
		File[] files = new File[list.size()];
		for (int i = 0; i < list.size(); i++) {
			files[i]= list.get(i);			
		}
		return files;
	}

	private static void getFiles(ArrayList<File> _list, File _dirName) {
//		FilenameFilter filter = new FilenameFilter() {
//			public boolean accept(File dir, String name) {
//				return (name.toLowerCase().endsWith(".htm") || name
//						.toLowerCase().endsWith(".html"));
//			}
//		};

//		String[] infiles = currentDir.list(filter);
		String[] infiles = _dirName.list();
		addAll(_list, _dirName, infiles);

		File[] listFiles = _dirName.listFiles();
		if (listFiles != null) {
			for (int i = 0; i < listFiles.length; i++) {
				File file = listFiles[i];
				getFiles(_list, file);
			}
		}
	}
	
	private static void addAll(ArrayList<File> _list, File _dirName, String[] _files) {
		if (_list == null || _files == null)
			return;
		for (int i = 0; i < _files.length; i++) {
			String fileName = _dirName.toString() + "\\" + _files[i];
			File file = new File (fileName);
			if (file.isDirectory()){
				continue;
			}
			_list.add(file);
		}
	}
}