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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.wdilab.coma.gui.dlg.Dlg_ImportDefault;
import de.wdilab.coma.gui.dlg.Dlg_ImportInstancesODBC;
import de.wdilab.coma.gui.dlg.Dlg_ImportODBC;
import de.wdilab.coma.gui.dlg.Dlg_ImportOWL;
import de.wdilab.coma.structure.Graph;

/**
 * MainWindow extends JFrame implements Runnable. It arranges the main menu of the GUI
 * that contains repository, match, match result, view and info.
 *
 * @author Sabine Massmann
 */
public class MainWindow extends JFrame implements Runnable {
    //----------------------------------------------
    //	  STATIC FINAL
    //----------------------------------------------
    public static  final int bigger = 2;
    public static  final Font FONT11 = new Font(GUIConstants.FONT_DIALOG, Font.PLAIN, 11+bigger);
    public static  final Font FONT6 = new Font(GUIConstants.FONT_DIALOG, Font.PLAIN, 6+bigger);
    public static  final Font FONT12 = new Font(GUIConstants.FONT_DIALOG, Font.PLAIN, 12+bigger);
    public static  final Font FONT13 = new Font(GUIConstants.FONT_DIALOG, Font.PLAIN, 13+bigger);
    public static  final Font FONT14 = new Font(GUIConstants.FONT_DIALOG, Font.PLAIN, 14+bigger);
    public static  final Font FONT24 = new Font(GUIConstants.FONT_DIALOG, Font.PLAIN, 24+bigger);
    public static  final Font FONT11_ITALIC = new Font(GUIConstants.FONT_DIALOG, Font.ITALIC, 11+bigger);
    public static  final Font FONT12_BOLD = new Font(GUIConstants.FONT_DIALOG, Font.BOLD, 12+bigger);
    public static  final Font FONT14_BOLD = new Font(GUIConstants.FONT_DIALOG, Font.BOLD, 14+bigger);
    public static  final Font TREE_FONT_TEXT = new Font(GUIConstants.FONT_COURIER, Font.PLAIN, 12);
    public static  final Font TREE_FONT_TEXT_ITALIC = new Font(GUIConstants.FONT_COURIER, Font.ITALIC, 12);
    public static  final int MAX_ROW = 5;
    public static  final int WINDOW_WIDTH = 1000; // binary: 900
    public static  final int WINDOW_HEIGHT = 800; // binary: 600

    public static  final int NONE = -1;


    // Color during edit mode
//	public static  final Color EDIT_BACKGROUND = new Color(0xcc, 0xff, 0xff);
//	public static  final Color EDIT_BACKGROUND = new Color(0xb8, 0xff, 0xb8);
    public static final Color USED_FRAGMENT_BACKGROUND = new Color(0xd0, 0xd0,
            0xff);
    public static final Color SUGGESTED_FRAGMENT_BACKGROUND = new Color(0xff,
            0xbb, 0x66);
    //	public static  final Color SELECTED_BACKGROUND = new Color(0xdd, 0xaa, 0xee);
//	public static  final Color SELECTED_BORDER = new Color(0xbb, 0x88, 0xcc);
    public static  final Color SELECTED_BACKGROUND = new Color(0x66, 0xcc, 0xff);
    public static  final Color SELECTED_BORDER = new Color(0x33, 0x99, 0xcc);
    // global lines
    public static  final Color GLOBAL = Color.RED;
    public static  final Color LIGHT = Color.BLUE;
    //	public static  final Color LIGHT = new Color(170, 190, 230);
    // MainWindow.GLOBAL_BACKGROUND
    public static  final Color GLOBAL_BACKGROUND = Color.WHITE;
    public static  final Color HIGHLIGHT_BACKGROUND =  new Color(255, 128, 255);// Color.MAGENTA;Color.YELLOW; new Color(255, 128, 255);
    public static  final Color LIGHTGRAY = new Color(0xe0, 0xe0, 0xe0);
    public static  final Color FOREGROUND_SPECIAL = Color.BLUE;
    public static  final Color FOREGROUND = Color.BLACK;
    public static  final Color BORDER = Color.GRAY;
    public static  final Color GLOBAL_BACKGROUND_DARK = Color.GRAY;
    //	public static  final Dimension DIM_INFO = new Dimension(500, 330);
    public static  final Dimension DIM_INFO = new Dimension(650, 400);
    public static  final Dimension DIM_LARGE = new Dimension(800, 600);
    public static  final Dimension DIM_LARGE2 = new Dimension(450, 750);
    public static  final Dimension DIM_MEDIUM = new Dimension(500, 300);
    public static  final Dimension DIM_MEDIUM2 = new Dimension(300, 500);
    public static  final Dimension DIM_SMALL = new Dimension(300, 400);
    public static  final Dimension DIM_SMALL2 = new Dimension(300, 200);
    public static  final Dimension DIM_REUSE = new Dimension(400, 500);
    public static  final Dimension DIM_EXISTING_MATCHER = new Dimension(800, 700);
    public static  final Dimension DIM_INST = new Dimension(450, 650);

    public static  final int VIEW_UNDEF = -1;
    public static  final int VIEW_GRAPH = 0;
    public static  final int VIEW_NODES = 1;
    //----------------------------------------------
    Controller controller;
    ArrayList<JMenuItem> runMenuItemList = new ArrayList<JMenuItem>();
    ArrayList<JMenuItem> saveSchemaMenuItemList = new ArrayList<JMenuItem>();
    MainWindowContentPane contentPane;
    int widthAdd;
    int heightAdd;
    JRadioButtonMenuItem loaded, resolved, reduced, simplified;
    JRadioButtonMenuItem graph, nodes;
    //	JCheckBoxMenuItem edit;
    // Unlock MenuItem
    JMenuItem unlock, /* unfoldSrc, unfoldTrg, */ stop;
    JMenu executeM;

    //	Image im;
    //	 MediaTracker tracker;
	/*
	 * constructor, send all important commands (given through the menu) to
	 * controller
	 */
    public MainWindow(final Controller _controller) {
        super();
        //
        //        tracker = new MediaTracker(this);
        //// java.net.URL url =
        // com.ibm.clio.Clio.getImageURL("ClioBannerB.gif");
        //        try {
        //            im = Toolkit.getDefaultToolkit().getImage(GUIConstants.ICON_C);
        //        }
        //        catch (Exception e) {
        //            System.err.println(e);
        //            return;
        //        }
        //        tracker.addImage(im, 0);
        //        try {
        //            tracker.waitForID(0);
        //        }
        //        catch (InterruptedException e) {
        //        }
        //        setIconImage(im);
        controller = _controller;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //Create and set up the content pane.
        contentPane = new MainWindowContentPane(_controller, WINDOW_WIDTH,
                WINDOW_HEIGHT);
//		contentPane.setOpaque(true); //content panes must be opaque

//		setJMenuBar(createMenuBar());
        contentPane.add(createMenuBar(), BorderLayout.NORTH);

        setContentPane(contentPane);
        //Display the window.
        setLocation(_controller.getDialogPosition());
        pack();
        setVisible(true);
        widthAdd = getWidth() - WINDOW_WIDTH;
        heightAdd = getHeight() - WINDOW_HEIGHT;
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent _event) {
                Component comp = _event.getComponent();
                _controller.setDialogPosition(comp.getLocation());
//				MainWindow.contentPane.setNewSize(comp.getWidth()
//						- MainWindow.widthAdd, comp.getHeight()
//						- MainWindow.heightAdd);
            }

            public void componentMoved(ComponentEvent _event) {
                Component comp = _event.getComponent();
                _controller.setDialogPosition(comp.getLocation());
            }
        });
    }

    /*
     * sets a new Controller
     */
    public void setController(Controller _controller) {
        controller = _controller;
        contentPane.setController(_controller);
    }

    public void clearMatchresultView() {
        contentPane.getMatchresultView().getSourceTree().setSelectionNull();
        contentPane.getMatchresultView().getTargetTree().setSelectionNull();
        contentPane.getMatchresultView().setLastSelectedTree(MainWindow.NONE);
    }

    /*
     * run this class that implements Runnable => directly call this method
     */
    public void run() {
        setVisible(true);
    }

//	JMenu getMenu(String _name, int _mnemonic){
//		JMenu menu = new JMenu(_name);
//		menu.setMnemonic(_mnemonic);
//		menu.setFont(FONT12_BOLD);
//		return	menu;
//	}

    JMenu getMenu(String _name){
        JMenu menu = new JMenu(_name);
        menu.setFont(FONT12_BOLD);
        return	menu;
    }

    JMenuItem getMenuItem(String _name){
        JMenuItem item = new JMenuItem(_name);
        item.setFont(FONT12_BOLD);
        return	item;
    }

    JMenuItem getMenuItem(String _name, Icon _icon){
        JMenuItem item = new JMenuItem(_name, _icon);
        item.setFont(FONT12_BOLD);
        return	item;
    }

//	JMenuItem getMenuItem(String _name, int _key){
//		JMenuItem item = new JMenuItem(_name, _key);
//		item.setFont(FONT12_BOLD);
//		return	item;
//	}


    JMenu createMenuRepository(){
        // Repository
        JMenu repository = getMenu(GUIConstants.REP);
        // Delete & Create New Database
        JMenuItem item = getMenuItem(GUIConstants.DEL_DB);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.createNewDatabase(true);
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        repository.add(item);

        // Import all default Schemas into Database
        item = getMenuItem(GUIConstants.IMPORT_EXAMPLES);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                Dlg_ImportDefault dlg = new Dlg_ImportDefault(
                        controller.getMainWindow(),	controller, true,
                        GUIConstants.IMPORT_EXAMPLES, Dlg_ImportDefault.EXAMPLE_ALL);
                dlg.showDlg(controller.getDialogPosition());
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        repository.add(item);

        repository.addSeparator();

        // SCHEMAS
        JMenu schemasM = getMenu(GUIConstants.SCHEMAS);
        repository.add(schemasM);
        // Import File
        item = getMenuItem(GUIConstants.IMPORT_FILE);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.importSchemaInDB();
            }
        });
        saveSchemaMenuItemList.add(item);
        schemasM.add(item);
        // Import URI/Ontology
        item = getMenuItem(GUIConstants.IMPORT_URI);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                //controller.importOWLInDB();
                //				controller.importOntology();
                //			}
                Dlg_ImportOWL dlg = new Dlg_ImportOWL(
                        controller.getMainWindow(),
                        controller);
                dlg.setLocation(controller.getDialogPosition());
                dlg.setVisible(true);
            }
        });
        saveSchemaMenuItemList.add(item);
        schemasM.add(item);
        // Import ODBC
        item = getMenuItem(GUIConstants.IMPORT_ODBC);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                Dlg_ImportODBC dlg = new Dlg_ImportODBC(
                        controller.getMainWindow(),
                        controller);
                dlg.setLocation(controller.getDialogPosition());
                dlg.setVisible(true);
            }
        });
        saveSchemaMenuItemList.add(item);
        schemasM.add(item);
        // Export a Schema to a File
        item = getMenuItem(GUIConstants.SCHEMA_FILE);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.exportToFile();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        schemasM.add(item);
        // Delete Schema
        item = getMenuItem(GUIConstants.DEL, Controller.getImageIcon(GUIConstants.ICON_DELETE_DB));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.deleteSchemaDB(null);
            }
        });
        saveSchemaMenuItemList.add(item);
        schemasM.add(item);
//		// Import all default Schemas into Database
//		item = getMenuItem(GUIConstants.IMPORT_EXAMPLES);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				Dlg_ImportDefault dlg = new Dlg_ImportDefault(
//						controller.getMainWindow(),	controller, true,
//						GUIConstants.IMPORT_EXAMPLES, Dlg_ImportDefault.EXAMPLE_SCHEMA);
//				dlg.showDlg(controller.getDialogPosition());
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		schemasM.add(item);
        // MATCHRESULTS
        JMenu matchresultsM = getMenu(GUIConstants.MATCHRESULTS_);
        repository.add(matchresultsM);
        // Import Matchresult File
        item = getMenuItem(GUIConstants.IMPORT_MAPFILE);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.importMatchresultInDB();
                //controller.loadMatchresultFromFile();
            }
        });
        saveSchemaMenuItemList.add(item);
        matchresultsM.add(item);
//		// Import Matchresult File
//		item = getMenuItem("Import Anatomy Matchresult File");
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.importAccessionMatchresultInDB();
//			}
//		});
//		saveSchemaMenuItemList.add(item);
//		matchresultsM.add(item);
        // Export Matchresults to File
        item = getMenuItem(GUIConstants.EXPORT_MAPFILE);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                //Save one or  multipe matchresults selected from the list to file
                controller.saveMatchresultToFile();
            }
        });
        saveSchemaMenuItemList.add(item);
        matchresultsM.add(item);
        // Results To RDF Alignment
        item = getMenuItem(GUIConstants.RESULTS_RDF_ALIGNMENT);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.showResultsInRDFAlignment();
            }
        });
        runMenuItemList.add(item);
        matchresultsM.add(item);
//		// Results To Spicy
//		item = getMenuItem( "Create XQuery (with Spicy)");
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
////				controller.showResultsInSpicyFormat();
//				controller.showResultsinXQuery();
//			}
//		});
//		runMenuItemList.add(item);
//		matchresultsM.add(item);
        // Delete (DB)
        item = getMenuItem(GUIConstants.DEL, Controller.getImageIcon(GUIConstants.ICON_DELETE_DB));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.deleteMatchresultDB();
            }
        });
        runMenuItemList.add(item);
        //		saveSchemaMenuItemList.add(item);
        matchresultsM.add(item);
//		// Import all default Matchresult into Database
//		item = getMenuItem(GUIConstants.IMPORT_EXAMPLES);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				Dlg_ImportDefault dlg = new Dlg_ImportDefault(
//						controller.getMainWindow(),	controller, false,
//						GUIConstants.IMPORT_EXAMPLES, Dlg_ImportDefault.EXAMPLE_MATCHRESULT);
//				dlg.showDlg(controller.getDialogPosition());
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		matchresultsM.add(item);


        // INSTANCES
        JMenu instancesM = getMenu(GUIConstants.INSTANCES);
        repository.add(instancesM);
        // Parse ontology file
        item = getMenuItem(GUIConstants.PARSE_ORG_FILE);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.parseInstancesOrgFile(null, true);
            }
        });
        saveSchemaMenuItemList.add(item);
        instancesM.add(item);
        // Parse additional file
        item = getMenuItem(GUIConstants.PARSE_ADD_FILE);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.parseInstancesAddFile((ArrayList)null, true);
            }
        });
        saveSchemaMenuItemList.add(item);
        instancesM.add(item);

        // Parse additional file
        item = getMenuItem(GUIConstants.PARSE_ADD_FILE + " - Batch");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.parseInstancesBatch(null, true);
            }
        });
        saveSchemaMenuItemList.add(item);
        instancesM.add(item);

        // Import instances ODBC
        item = getMenuItem(GUIConstants.PARSE_ODBC);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                Dlg_ImportInstancesODBC dlg = new Dlg_ImportInstancesODBC(
                        controller.getMainWindow(),
                        controller);
                dlg.setLocation(controller.getDialogPosition());
                dlg.setVisible(true);
            }
        });
        saveSchemaMenuItemList.add(item);
        instancesM.add(item);


//		// Import default Instances into Database
//		item = getMenuItem(GUIConstants.IMPORT_EXAMPLES);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				Dlg_ImportDefault dlg = new Dlg_ImportDefault(
//						controller.getMainWindow(),	controller, true,
//						GUIConstants.IMPORT_EXAMPLES, Dlg_ImportDefault.EXAMPLE_INSTANCES);
//				dlg.showDlg(controller.getDialogPosition());
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		instancesM.add(item);

        // EDIT
        JMenu editStuff = getMenu(GUIConstants.AUX_INFO);
        repository.add(editStuff);

        // Show Synonyms
        item = getMenuItem(GUIConstants.ABB);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.showAbbreviations();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        editStuff.add(item);
        // Show Abbreviations
        item = getMenuItem(GUIConstants.SYN);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.showSynonyms();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        editStuff.add(item);

        // Separator
        editStuff.addSeparator();

        // Import Default Abbreviations + Synonyms
        item = getMenuItem(GUIConstants.SET_DEFAULT);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.setDefaultAbbreviationsAndSynonyms();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        editStuff.add(item);

        // Delete Abbreviations + Synonyms
        item = getMenuItem(GUIConstants.REMOVE);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.removeAbbreviationsAndSynonyms();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        editStuff.add(item);




        repository.addSeparator();
        // EXIT
        item = getMenuItem(GUIConstants.EXIT);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                System.exit(0);
            }
        });
        repository.add(item);
        return repository;
    }

    JMenu createMenuMatch(){
        // MATCH
        JMenu match = getMenu(GUIConstants.MATCH);

        // Configure
//		Controller.getImageIcon(GUIConstants.ICON_CONFIGURE)

        JMenuItem item = null;
//		JMenuItem item = getMenuItem(GUIConstants.CONFIGURE, Controller.getImageIcon(GUIConstants.ICON_CONFIGURE));
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.configureStrategy();
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		match.add(item);


        // Execute
		/*
		 * item = getMenuItem(GUIConstants.EX, KeyEvent.VK_E);
		 * item.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent _event) { controller.executeMatching(); }
		 * }); runMenuItemList.add(item); saveSchemaMenuItemList.add(item);
		 * matching.add(item);
		 */
        executeM = getMenu(GUIConstants.EX + " " + GUIConstants.WORKFLOW); // GUIConstants.ex??
        executeM.setIcon(Controller.getImageIcon(GUIConstants.ICON_EXECUTE));
        //executeM.setMnemonic(KeyEvent.VK_E);
        match.add(executeM);
        executeM.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // take current workflows as possible executable
                HashMap<String, String> workflows = controller.getAccessor().getWorkflowVariablesWithType("W");
                executeM.removeAll();
                if (workflows!=null){
                    ArrayList<String> workflowsKeys = new ArrayList<String>(workflows.keySet());
                    Collections.sort(workflowsKeys);
                    for (Iterator iterator = workflowsKeys.iterator(); iterator.hasNext();) {
                        String workflow = (String) iterator.next();
                        JMenuItem workflowButton = getMenuItem(workflow);
                        workflowButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent _event) {
                                Object source = _event.getSource();
                                if (source instanceof JMenuItem)
                                    controller.executeMatching(((JMenuItem)source).getText());
                            }
                        });
                        executeM.add(workflowButton);
                    }
                }
            }
        });
        // execute first/second/third/whatever strategy
//		// AllContext
//		item =getMenuItem(GUIConstants.STRAT_CONTEXT);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				//set strategy here and execute
//				String strategy = controller.getStrategy();
//				if (!strategy.equals(GUIConstants.STRAT_ALLCONTEXT) && !strategy.equals(GUIConstants.STRAT_FILTEREDCONTEXT)){
//					String lastContextStrategy = controller.getLastContextStrategy();
//					controller.setStrategy(lastContextStrategy);
//				}
//				System.out.println("set strategy and going to execute "
//						+ controller.getStrategy());
//				controller.getManagementPane()
//						.setButtonRunTooltip(
//								": " +
//								controller.getStrategy());
//				controller.executeMatching();
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		executeM.add(item);
//		// execute first/second/third/whatever strategy
//		//  Nodes
//		item = getMenuItem(GUIConstants.STRAT_NODES);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.setStrategy(((JMenuItem) _event
//						.getSource()).getText());
//				System.out.println("set strategy and going to execute "
//						+ ((JMenuItem) _event.getSource()).getText());
//				controller.getManagementPane()
//						.setButtonRunTooltip(
//								": "
//										+ ((JMenuItem) _event.getSource())
//												.getText());
//				controller.executeMatching();
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		executeM.add(item);
//		// execute first/second/third/whatever strategy
//		//  Fragment
//		item = getMenuItem(GUIConstants.STRAT_FRAGMENT);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.setStrategy(((JMenuItem) _event
//						.getSource()).getText());
//				System.out.println("set strategy and going to execute "
//						+ ((JMenuItem) _event.getSource()).getText());
//				controller.getManagementPane()
//						.setButtonRunTooltip(
//								": "
//										+ ((JMenuItem) _event.getSource())
//												.getText());
//				controller.executeMatching();
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		executeM.add(item);
//		// not used: taxonomy
//		// execute first/second/third/whatever strategy
//		if (!Controller.SIMPLE_GUI) {
//			//  Taxonomy
//			item = getMenuItem(GUIConstants.STRAT_TAXONOMY);
//			item.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent _event) {
//					System.out
//							.println("set path strategy w/ taxonomy config; going to execute "
//									+ ((JMenuItem) _event.getSource())
//											.getText());
//					controller.setStrategy(((JMenuItem) _event
//							.getSource()).getText());
//					controller
//							.executeTaxonomyMatching(((JMenuItem) _event
//									.getSource()).getText());
//				}
//			});
//			runMenuItemList.add(item);
//			saveSchemaMenuItemList.add(item);
//			executeM.add(item);
//		}
//		// execute first/second/third/whatever strategy
//		//  Reuse
//		item = getMenuItem(GUIConstants.STRAT_REUSE);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.setStrategy(((JMenuItem) _event
//						.getSource()).getText());
//				controller.executeReuseMatching(false);
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		executeM.add(item);
//		// execute first/second/third/whatever strategy
//		//  Combined Reuse
//		if (!Controller.SIMPLE_GUI && !Controller.WORKING_GUI) {
//			item = getMenuItem(GUIConstants.STRAT_COMBINEDREUSE);
//			item.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent _event) {
//					controller.setStrategy(((JMenuItem) _event
//							.getSource()).getText());
//					controller.executeReuseMatching(true);
//				}
//			});
//			runMenuItemList.add(item);
//			saveSchemaMenuItemList.add(item);
//			executeM.add(item);
//		}
        // Stop
        stop = getMenuItem(GUIConstants.STOP, Controller.getImageIcon(GUIConstants.ICON_STOP));
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.stopMatching();
            }
        });
        stop.setEnabled(false);
        runMenuItemList.add(stop);
        saveSchemaMenuItemList.add(stop);
        match.add(stop);
//		match.add(new JSeparator());

        // Reuse (manual)
        item = getMenuItem(GUIConstants.REUSE_MANUAL, Controller.getImageIcon(GUIConstants.ICON_REUSE));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.reuseMatchresult();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        match.add(item);
        // Step By Step Fragment Matching
        item = getMenuItem(GUIConstants.FRAGMATCHING_MANUAL, Controller.getImageIcon(GUIConstants.ICON_STEPBYSTEP));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.stepByStepFragmentMatching();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        match.add(item);

//		item = getMenuItem(GUIConstants.CONFIGURE_INST);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.configureInstanceStrategy();
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		match.add(item);

//		// Dung Phan 17:33 16.06.2009
//		item = getMenuItem(GUIConstants.CONFIGURE_ML);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.configureMachineLearningStrategy();
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		match.add(item);

        // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        // not used: taxonomy
//		match.addSeparator();
//		if (!Controller.SIMPLE_GUI) {
//			item = getMenuItem("View Taxonomy", KeyEvent.VK_X);
//			item.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent _event) {
//					controller.viewTaxonomy();
//				}
//			});
//			runMenuItemList.add(item);
//			saveSchemaMenuItemList.add(item);
//			match.add(item);
//		}
        // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        match.addSeparator();
        // Manager
        //JMenu library = new JMenu(GUIConstants.MATCHER_LIB);
        //library.setMnemonic(KeyEvent.VK_L);

        // Existing Workflow Variables
        item = getMenuItem(GUIConstants.EX_WORKFLOWVARIABLES, Controller.getImageIcon(GUIConstants.ICON_EXISTING_MATCHER));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.showWorkflowVariables();
            }
        });
        saveSchemaMenuItemList.add(item);
        match.add(item);
        // New Matcher
//		saveSchemaMenuItemList.add(item);
//		match.add(item);
//		// NEW MATCHER
//		JMenu matcherM = getMenu(GUIConstants.NEW_MATCHER);
//		match.add(matcherM);
//		// New Matcher
//		item = getMenuItem(GUIConstants.MATCHER);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.createNewMatcher();
//			}
//		});
//		saveSchemaMenuItemList.add(item);
//		matcherM.add(item);
//		// New ReuseStrategy
//		item = getMenuItem(GUIConstants.ReuseStrategy);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.createNewReuseStrategy();
//			}
//		});
//		saveSchemaMenuItemList.add(item);
//		matcherM.add(item);
//		// New InstanceMatcher
//		item = getMenuItem(GUIConstants.INSTANCEMATCHER);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.createNewInstanceMatcher();
//			}
//		});
//		saveSchemaMenuItemList.add(item);
//		matcherM.add(item);

//		// Import UserMatcher
//		item =getMenuItem(GUIConstants.IMPORT_USERMATCHER);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.importUserMatcher();
//			}
//		});
//		saveSchemaMenuItemList.add(item);
//		match.add(item);
//		// Import UserMatcher
//		item =getMenuItem("Update UserInstMatcher with current configuration");
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.saveInstanceStrategyToUsermatcher();
//			}
//		});
//		saveSchemaMenuItemList.add(item);
//		match.add(item);
        // Matcher Hierarchy
        item = getMenuItem(GUIConstants.VARIABLE_HIERARCHY);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.showVariableHierarchy();
            }
        });
        saveSchemaMenuItemList.add(item);
        match.add(item);
        // Delete Variable
        item = getMenuItem(GUIConstants.DEL_VARIABLE);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.deleteVariable();
            }
        });
        saveSchemaMenuItemList.add(item);
        match.add(item);
        // Reset to System defaults
        item = getMenuItem(GUIConstants.IMPORT_WORKFLOW);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.importDefaultVariables();
            }
        });
        saveSchemaMenuItemList.add(item);
        match.add(item);
        return match;
    }

    JMenu createMenuMatchresult(){
        // Matchresult
		/*
		 * would be nice to have current matchresult name instead of "temp" ? String
		 * matchresultName = ""; MatchresultTab tab =
		 * getNewContentPane().getMatchresultPane().getSelectedTab(); if (tab !=
		 * null) matchresultName = tab.getName();
		 */
        JMenu matchresult = getMenu(GUIConstants.MATCHRESULT);
        //		item = getMenuItem("New", KeyEvent.VK_N);
        //		item.addActionListener(new ActionListener() {
        //			public void actionPerformed(ActionEvent _event) {
        //				controller.createNewMatchresult();
        //			}
        //		});
        //		runMenuItemList.add(item);
        //		saveSchemaMenuItemList.add(item);
        //		matchresult.add(item);




        //Load
        JMenuItem item = getMenuItem(GUIConstants.LOAD, Controller.getImageIcon(GUIConstants.ICON_OPENMATCHRESULT));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.loadMatchresultFromDB();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        matchresult.add(item);
        //Save (DB)
        item = getMenuItem(GUIConstants.SAVE, Controller.getImageIcon(GUIConstants.ICON_MATCHRESULT_DB));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.saveMatchresultToDB();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        matchresult.add(item);
//		// Edit
//		if (controller.getMainWindow() != null) {
//			edit = new JCheckBoxMenuItem(GUIConstants.EDIT_TMP, Controller.getImageIcon(GUIConstants.ICON_EDIT),
//					controller.getMatchresultView().isEdit());
//		} else {
//			edit = new JCheckBoxMenuItem(GUIConstants.EDIT_TMP, Controller.getImageIcon(GUIConstants.ICON_EDIT), false);
//		}
//		edit.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				boolean state = ((JCheckBoxMenuItem) _event.getSource())
//						.getState();
//				if (controller.editMatchresult(state, true)) {
//					controller.getManagementPane()
//							.setEditState(state);
//				} else {
//					((JCheckBoxMenuItem) _event.getSource())
//							.setSelected(!state);
//				}
//			}
//		});
//		edit.setFont(FONT12_BOLD);
//		matchresult.add(edit);

        // Duplicate (Temp)
        item = getMenuItem(GUIConstants.DUPLICATE_TMP);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.duplicateMatchresult();
            }
        });
        runMenuItemList.add(item);
        //      saveSchemaMenuItemList.add(item);
        matchresult.add(item);
        // Delete (Temp)
        item = getMenuItem(GUIConstants.DEL, Controller.getImageIcon(GUIConstants.ICON_DELETE_TMP));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.deleteMatchresultTemp();
            }
        });
        runMenuItemList.add(item);
        //      saveSchemaMenuItemList.add(item);
        matchresult.add(item);


//		JMenu partMatchresult = getMenu(GUIConstants.EXTRACT);
//		//executeM.setMnemonic(KeyEvent.VK_E);
//		matchresult.add(partMatchresult);
//		// LeaveCorr
//		item = getMenuItem(GUIConstants.BETWEEN_LEAF);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.createPartMatchResult(MatchResult.MAP_OP_PART_LEAVES);
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		partMatchresult.add(item);
//		// InnerCorr
//		item = getMenuItem(GUIConstants.BETWEEN_INNER);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.createPartMatchResult(MatchResult.MAP_OP_PART_INNERS);
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		partMatchresult.add(item);
//		// MixCorr
//		item = getMenuItem(GUIConstants.BETWEEN_MIXED);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.createPartMatchResult(MatchResult.MAP_OP_PART_MIXED);
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		partMatchresult.add(item);

        matchresult.add(new JSeparator());
        // MMERGE
        item = getMenuItem(GUIConstants.MMERGE, Controller.getImageIcon(GUIConstants.ICON_MERGE));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.mergeMatchresult();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        matchresult.add(item);
        // Intersec (Temp)
        item = getMenuItem(GUIConstants.INTERSECT_TMP, Controller.getImageIcon(GUIConstants.ICON_INTERSECT));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.intersectMatchresult();
            }
        });
        runMenuItemList.add(item);
        //		saveSchemaMenuItemList.add(item);
        matchresult.add(item);

        // Diff (Temp)
        item = getMenuItem(GUIConstants.DIFF_TMP, Controller.getImageIcon(GUIConstants.ICON_DIFFERENCE));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller
                        .differenceMatchresult(controller
                                .getGUIMatchresult().getMatchResult());
            }
        });
        runMenuItemList.add(item);
        //		saveSchemaMenuItemList.add(item);
        matchresult.add(item);
        // Compare (Temp)
        item = getMenuItem(GUIConstants.COMPARE_TMP, Controller.getImageIcon(GUIConstants.ICON_COMPARE));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.compareMatchresult(null);
            }
        });
        runMenuItemList.add(item);
        //		saveSchemaMenuItemList.add(item);
        matchresult.add(item);

//		// use: schema management
//		matchresult.add(new JSeparator());
//		// DOMAIN
////		item = getMenuItem(GUIConstants.DOMAIN, Controller.getImageIcon(GUIConstants.ICON_DOMAIN));
//		item = getMenuItem(GUIConstants.DOMAIN);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.domainMatchresult(false, controller.getMatchresult()
//						.getMatchResult());
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		matchresult.add(item);
//		// INVERTDOMAIN
////		item = getMenuItem(GUIConstants.INVERTDOMAIN, Controller.getImageIcon(GUIConstants.ICON_INVERTDOMAIN));
//		item = getMenuItem(GUIConstants.INVERTDOMAIN);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.domainMatchresult(true, controller.getMatchresult()
//						.getMatchResult());
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		matchresult.add(item);
//		// RANGE
////		item = getMenuItem(GUIConstants.RANGE, Controller.getImageIcon(GUIConstants.ICON_RANGE));
//		item = getMenuItem(GUIConstants.RANGE);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.rangeMatchresult(false,
//						controller.getMatchresult()
//								.getMatchResult());
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		matchresult.add(item);
//		// INVERTRANGE
////		item = getMenuItem(GUIConstants.INVERTRANGE, Controller.getImageIcon(GUIConstants.ICON_INVERTRANGE));
//		item = getMenuItem(GUIConstants.INVERTRANGE);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.rangeMatchresult(true,
//						controller.getMatchresult()
//								.getMatchResult());
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		matchresult.add(item);
//		// SMERGE
////		item = getMenuItem(GUIConstants.SMERGE, Controller.getImageIcon(GUIConstants.ICON_SMERGE));
//		item = getMenuItem(GUIConstants.SMERGE);
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller
//						.smergeMatchresult(controller.getMatchresult()
//								.getMatchResult());
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		matchresult.add(item);
//
//		matchresult.add(new JSeparator());
//		item = getMenuItem("Query Display");
//		item.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.showQueryDisplay();
//			}
//		});
//		runMenuItemList.add(item);
//		saveSchemaMenuItemList.add(item);
//		matchresult.add(item);

        return matchresult;
    }

    JMenu createMenuInfo(){
        // Help
        JMenu info = getMenu(GUIConstants.INFO);
        // About
        JMenuItem item = getMenuItem(GUIConstants.ABOUT);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.showAbout();
            }
        });
        info.add(item);
        info.add(new JSeparator());
        // UNLOCK_GUI
        unlock = getMenuItem(GUIConstants.UNLOCK_GUI);
        unlock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.getMainWindow().getNewContentPane()
                        .unlockGUI();
            }
        });
        unlock.setEnabled(false);
        info.add(unlock);

        return info;
    }

    JMenu createMenuXSLT(){

        JMenu xslt = getMenu("XSLT");

        //create xslt R-->L
        JMenuItem item = getMenuItem("Create Right to Left XSLT");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.exportXSLTrightToLeft(false);
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        xslt.add(item);

        //create XSLT L-->R
        item = getMenuItem("Create Left to Right XSLT");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.exportXSLTLeftToRight(false);
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        xslt.add(item);

        //create xslt R-->L strict
        item = getMenuItem("Create Right to Left XSLT - strict");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.exportXSLTrightToLeft(true);
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        xslt.add(item);

        //create XSLT L-->R strict
        item = getMenuItem("Create Left to Right XSLT - strict");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.exportXSLTLeftToRight(true);
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        xslt.add(item);
//
        return xslt;
    }



    JMenu createMenuGERepo(){

        JMenu geRepo = getMenu("SemanticDB");

        //create download ontology button
        JMenuItem item = getMenuItem("Download Ontology");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.showGEontologyList();
            }
        });
//        item.setEnabled(false);
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        geRepo.add(item);

        //create download ontology button
        item = getMenuItem("Upload Ontology");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.uploadOntology();
            }
        });
//        item.setEnabled(false);
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        geRepo.add(item);

        //create workspace button
        item = getMenuItem("Create Workspace");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.createWorkspace();
            }
        });
//        item.setEnabled(false);
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        geRepo.add(item);

        //create workspace button
        item = getMenuItem("Add to Existing Workspace");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.configureWorkspace();
            }
        });
        item.setEnabled(false);
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        geRepo.add(item);

        //create query workspace button
        item = getMenuItem("Query Workspace");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.existingWorkspaceQuery();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        geRepo.add(item);

        //export matching triples
        item = getMenuItem("Export triples locally");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.exportTriplesLocally();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        geRepo.add(item);


//
        return geRepo;
    }

    JMenu createMenuView(){
        // View
        JMenu view = getMenu(GUIConstants.VIEW);

        // Schema Information
        JMenuItem item = getMenuItem(GUIConstants.SCHEMA_INFO);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.showSchemaInformations();
            }
        });
        runMenuItemList.add(item);
        saveSchemaMenuItemList.add(item);
        view.add(item);
        // Info Match Results
        item = getMenuItem(GUIConstants.MATCHRESULT_INFO);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.showInfoMatchResults();
            }
        });
        runMenuItemList.add(item);
        view.add(item);
        // Match Results Correspondences
        item = getMenuItem(GUIConstants.MATCHRESULT_CORRESP);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                controller.showMatchResults();
            }
        });
        runMenuItemList.add(item);
        view.add(item);
        // Separator
        view.addSeparator();

        // Clear Workspace
        item = getMenuItem("Clear Workspace");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
//				// close schemas -> mapping automatically closed
//				controller.closeSchema(true, false);
//				controller.closeSchema(false, false);
                // clear schema list -> mapping list automatically cleared
                // -> automatically close shown schemas and matchresults
                controller.getMainWindow().getNewContentPane().changeTo2SplitPane();
                controller.cleanMatchresultLines();
                controller.getManagementPane().removeAllWorkSchemas();

            }
        });
        runMenuItemList.add(item);
        view.add(item);

        // Separator
        view.addSeparator();

        // Mode..
        int currentView = controller.getView();
        JMenu mode = getMenu(GUIConstants.MODE);
        ButtonGroup bg = new ButtonGroup();
        graph = new JRadioButtonMenuItem(viewToString(VIEW_GRAPH), false);
        nodes = new JRadioButtonMenuItem(viewToString(VIEW_NODES), false);
        graph.setFont(FONT12_BOLD);
        nodes.setFont(FONT12_BOLD);
        switch (currentView) {
            // Graph
            case VIEW_GRAPH :
                graph.setSelected(true);
                break;
            // Nodes
            case VIEW_NODES :
                nodes.setSelected(true);
                break;
        }
        // Graph
        graph.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                if (((JMenuItem) _event.getSource()).isSelected()) {
                    int newView = stringToView(((JMenuItem) _event
                            .getSource()).getText());
                    controller.setView(newView);
                }
            }
        });
        mode.add(graph);
        bg.add(graph);
        // Nodes
        nodes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                if (((JMenuItem) _event.getSource()).isSelected()) {
                    int newView = stringToView(((JMenuItem) _event
                            .getSource()).getText());
                    controller.setView(newView);
                }
            }
        });
        mode.add(nodes);
        bg.add(nodes);
        view.add(mode);


        // Preprocessing
        int prep = controller.getPreprocessing();
        JMenu preprocessing = getMenu(GUIConstants.PREP);
        bg = new ButtonGroup();
        loaded = new JRadioButtonMenuItem(Graph
                .preprocessingToString(Graph.PREP_LOADED), false);
        resolved = new JRadioButtonMenuItem(Graph
                .preprocessingToString(Graph.PREP_RESOLVED), false);
        reduced = new JRadioButtonMenuItem(Graph
                .preprocessingToString(Graph.PREP_REDUCED), false);
        simplified = new JRadioButtonMenuItem(Graph
                .preprocessingToString(Graph.PREP_SIMPLIFIED), false);
        loaded.setFont(FONT12_BOLD);
        resolved.setFont(FONT12_BOLD);
        reduced.setFont(FONT12_BOLD);
        simplified.setFont(FONT12_BOLD);
        switch (prep) {
            // Loaded
            case Graph.PREP_LOADED :
                loaded.setSelected(true);
                break;
            // Resolved
            case Graph.PREP_RESOLVED :
                resolved.setSelected(true);
                break;
            // Reduced
            case Graph.PREP_REDUCED :
                reduced.setSelected(true);
                break;
            case Graph.PREP_SIMPLIFIED :
                simplified.setSelected(true);
                break;
        }
        // Loaded
        loaded.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                if (((JMenuItem) _event.getSource()).isSelected()) {
                    int newPrep = Graph.stringToPreprocessing(((JMenuItem) _event
                            .getSource()).getText());
                    controller.setPreprocessing(newPrep);
                }
            }
        });
        preprocessing.add(loaded);
        bg.add(loaded);
        // Resolved
        resolved.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                if (((JMenuItem) _event.getSource()).isSelected()) {
                    int newPrep = Graph.stringToPreprocessing(((JMenuItem) _event
                            .getSource()).getText());
                    controller.setPreprocessing(newPrep);
                }
            }
        });
        preprocessing.add(resolved);
        bg.add(resolved);
        // Reduced
        reduced.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                if (((JMenuItem) _event.getSource()).isSelected()) {
                    int newPrep = Graph.stringToPreprocessing(((JMenuItem) _event
                            .getSource()).getText());
                    controller.setPreprocessing(newPrep);
                }
            }
        });
        preprocessing.add(reduced);
        bg.add(reduced);
        // Simplified
        simplified.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent _event) {
                if (((JMenuItem) _event.getSource()).isSelected()) {
                    int newPrep = Graph.stringToPreprocessing(((JMenuItem) _event
                            .getSource()).getText());
                    controller.setPreprocessing(newPrep);
                }
            }
        });
        preprocessing.add(simplified);
        bg.add(simplified);
        view.add(preprocessing);


        //		// Swap Source and Target
        //		item = new JMenuItem(GUIConstants.SWAP, KeyEvent.VK_W);
        //		item.addActionListener(new ActionListener() {
        //			public void actionPerformed(ActionEvent _event) {
        //				controller.swap();
        //			}
        //		});
        //		repository.add(item);

        return view;
    }

    /*
     * creates (and returns) a menu bar -schema, matchresult, library,
     * match,repository, help
     */
    private JMenuBar createMenuBar() {
        // create whole menu bar
        JMenuBar menu = new JMenuBar();
        JMenu repository = createMenuRepository();
        JMenu match = createMenuMatch();
        JMenu matchresult = createMenuMatchresult();
        JMenu view = createMenuView();
        JMenu info = createMenuInfo();
        JMenu xslt = createMenuXSLT();
        JMenu geRepo = createMenuGERepo();
        menu.add(repository);
        menu.add(match);
        menu.add(matchresult);
        menu.add(view);
        menu.add(xslt);
        menu.add(geRepo);
        menu.add(Box.createHorizontalGlue());
        menu.add(info);
        return menu;
    }

    /*
     * while saving a schema to the database disable most of the menu itemes and
     * buttons when finished saving enable them
     */
    public void setMenuStateSaveSchema(boolean _state) {
        if (saveSchemaMenuItemList.size() > 0) {
            for (int i = 0; i < saveSchemaMenuItemList.size(); i++) {
                JMenuItem current =  saveSchemaMenuItemList.get(i);
                String text = current.getText();
                if (text.equals(GUIConstants.STOP)) {
                    current.setEnabled(false);
//				} else {
//					current.setEnabled(!_state);
                }
            }
        }
        getNewContentPane().setProgressBar(_state);
        //		contentPane.setMenuStateSaveSchema(state);
    }

    /*
     * while running matching disable most of the menu itemes and buttons when
     * finished matching enable them
     */
    public void setMenuStateRun(boolean _state) {
        if (runMenuItemList.size() > 0) {
            for (int i = 0; i < runMenuItemList.size(); i++) {
                JMenuItem current = runMenuItemList.get(i);
                String text = current.getText();
                if (text.equals(GUIConstants.STOP)) {
                    current.setEnabled(_state);
//				} else {
//					current.setEnabled(!_state);
                }
            }
        }
        getNewContentPane().setProgressBar(_state);
        //		contentPane.setMenuStateRun(state);
    }

    /*
     * @return MainWindowContentPane
     */
    public MainWindowContentPane getNewContentPane() {
        return contentPane;
    }

//	/*
//	 * set true when edit matchresult (otherwise false)
//	 */
//	public void setEditMatchresult(boolean _state) {
//		edit.setState(_state);
//	}
//
//	/*
//	 * set true when edit matchresult (otherwise false)
//	 */
//	public void setEditEnabled(boolean _enabled) {
//		edit.setEnabled(_enabled);
//	}

    /*
     * set the current preprocessing strategy
     */
    public void setPreprocessing(int _preprocessing) {
        switch (_preprocessing) {
            case Graph.PREP_LOADED :
                loaded.setSelected(true);
                resolved.setSelected(false);
                reduced.setSelected(false);
                simplified.setSelected(false);
                break;
            case Graph.PREP_RESOLVED :
                loaded.setSelected(false);
                resolved.setSelected(true);
                reduced.setSelected(false);
                simplified.setSelected(false);
                break;
            case Graph.PREP_REDUCED :
                loaded.setSelected(false);
                resolved.setSelected(false);
                reduced.setSelected(true);
                simplified.setSelected(false);
                break;
            case Graph.PREP_SIMPLIFIED :
                loaded.setSelected(false);
                resolved.setSelected(false);
                reduced.setSelected(false);
                simplified.setSelected(true);
                break;
        }
    }

    /*
     * @return Returns the unlock.
     */
    public JMenuItem getUnlock() {
        return unlock;
    }

//	/*
//	 * enable the JMenuItem "Unfold Src" by given value true, otherwise disable
//	 */
//	public void enableUnfoldSrc(boolean _enable) {
//		unfoldSrc.setEnabled(_enable);
//	}

//	/*
//	 * enable the JMenuItem "Unfold Trg" by given value true, otherwise disable
//	 */
//	public void enableUnfoldTrg(boolean _enable) {
//		unfoldTrg.setEnabled(_enable);
//	}

    /*
     * Create GridBagConstraints with the given Input (x, y, width, height)
     * Insets is default always (1,1,1,1)
     */
    public static GridBagConstraints makegbc(int _x, int _y, int _width,
                                             int _height) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = _x;
        gbc.gridy = _y;
        gbc.gridwidth = _width;
        gbc.gridheight = _height;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 1, 1, 1);
        return gbc;
    }

    public static String viewToString(int _view) {
        switch (_view) {
            case VIEW_GRAPH:     return "Graph";
            case VIEW_NODES:   return "Nodes";
            default: return "Graph";
        }
    }
    public static int stringToView(String _viewStr) {
        if (_viewStr==null) return VIEW_GRAPH;
        else if (_viewStr.equals("Graph"))     return VIEW_GRAPH;
        else if (_viewStr.equals("Nodes"))   return VIEW_NODES;
        return VIEW_GRAPH;
    }
}