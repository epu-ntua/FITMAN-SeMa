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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.gui.dlg.Dlg_Domain;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * ManagementPane extends JPanel. It contains the lists of the current domains, schemas, matchresults
 * of the repository and the workspace. It manages changes in this lists e.g. loading and deleting.
 * 
 * @author Sabine Massmann
 */
public class ManagementPane extends JPanel {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	static final int SCHEMAS = 0;
	static final int MATCHRESULTS = 1;
	static final int REPOSITORY = 0;
	static final int WORKSPACE = 1;
	public static final Insets BUTTON_INSETS = new Insets(0, 0, 0, 0);
	static final Insets BUTTON_INSETS_LESS = new Insets(-1, -1, -1, -1);
//	static final Insets BUTTON_INSETS_LESS = new Insets(-3, -3, -3, -3);
	//----------------------------------------------
	Controller controller;
	JTabbedPane tabPane;
	JList list_RepDomains, list_RepSchemas, list_RepMatchresults, list_WorkMatchresults,
			list_WorkSchemas;
	JScrollPane listScrollPane_RepDomain, listScrollPane_RepSchemas, listScrollPane_RepMatchresults,
			listScrollPane_WorkMatchresults, listScrollPane_WorkSchemas;
	PropertieJPanel properties_Repository, properties_Workspace;
	ArrayList<AbstractButton> buttonListDB, buttonListRun;
	ArrayList<String> repDomains;
	ArrayList<Source> repSchemas;
	ArrayList<SourceRelationship> repMatchresults; 
	ArrayList<MatchResult> workMatchresults;
	ArrayList<Graph> workSchemas;
	int countWorkMatchresults = 0;
	int countWorkSchemas = 0;
	JToggleButton  /*edit,*/splitpane;
	private ArrayList<AbstractButton> runButtonList = new ArrayList<AbstractButton>();
	private ArrayList<AbstractButton> saveSchemaButtonList = new ArrayList<AbstractButton>();
//	JRadioButtonMenuItem allContext, nodes, // filteredContext,
//	// not used: taxonomy
////	taxonomy,	
//	fragment, reuse/*, combinedReuse*/;
	JTabbedPane tabRepository, tabWorkspace;
	JPopupMenu popupDomain, popupSchema;
	JMenu worflowMenu;
//	ButtonGroup worflowBG;
	/*
	 * Constructor of ManagementPane
	 */
	public ManagementPane(Controller _controller) {
		super(new BorderLayout());
		controller = _controller;
		buttonListDB = new ArrayList<AbstractButton>();
		buttonListRun = new ArrayList<AbstractButton>();
		repSchemas = new ArrayList<Source>();
		repMatchresults = new ArrayList<SourceRelationship>();
		workMatchresults = new ArrayList<MatchResult>();
		workSchemas = new ArrayList<Graph>();
		tabPane = new JTabbedPane();
		tabPane.setTabPlacement(SwingConstants.TOP);
		tabPane.setBackground(MainWindow.GLOBAL_BACKGROUND_DARK);
		//				tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		// ToolBar
		JToolBar toolBar = createToolBar();
		//		bar.setBounds(0, 0, width, TOOLBAR_HEIGHT);
		toolBar.setOpaque(true);
		add(toolBar, BorderLayout.NORTH);
		add(tabPane, BorderLayout.CENTER);
		String name = GUIConstants.REP;
		//		tabPane.addTab(name, getSchemasPanel());
		//		name = GUIConstants.M_MATCHRESULTS;
		//		tabPane.addTab(name, getMatchresultsPanel());
		tabPane.setFont(MainWindow.FONT12_BOLD);
		tabPane.addTab(name, getRepositoryPanel());
		name = GUIConstants.WORKSPACE;
		tabPane.addTab(name, getWorkspacePanel());
		initPopUp();
	}

	/*
	 * set selected tab ( repository, workspace)
	 */
	public void setSelectedTab(int _select) {
		tabPane.setSelectedIndex(_select);
	}

	/*
	 * create and return the schema panel containing: toolbar, schema list and
	 * properties
	 */
	JPanel getRepositoryPanel() {
		ButtonGroup b = new ButtonGroup();
		JToolBar toolBar = new JToolBar();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
		toolBar.setOrientation(SwingConstants.HORIZONTAL);
		toolBar.setFloatable(false);
		// Open Source
//		JButton button = new JButton(new ImageIcon(GUIConstants.ICON_OPENSOURCE));
		JButton button = new JButton(Controller.getImageIcon(GUIConstants.ICON_OPENSOURCE));
		button.setToolTipText(GUIConstants.OPEN_AS_SOURCE);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if ((list_RepSchemas != null)
						&& !list_RepSchemas
								.isSelectionEmpty()) {
					Source s = (Source) list_RepSchemas
							.getSelectedValue();
					controller
							.loadSourceSchema(s);
				} else {
					controller
							.setStatus(GUIConstants.NO_SCHEMA_SEL);
				}
			}
		});
		toolBar.add(button);
		b.add(button);
		buttonListDB.add(button);
		// Open Target
//		button = new JButton(new ImageIcon(GUIConstants.ICON_OPENTARGET));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_OPENTARGET));
		button.setToolTipText(GUIConstants.OPEN_AS_TARGET);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if ((list_RepSchemas != null)
						&& !list_RepSchemas
								.isSelectionEmpty()) {
					Source s = (Source) list_RepSchemas
							.getSelectedValue();
					controller
							.loadTargetSchema(s);
				} else {
					controller
							.setStatus(GUIConstants.NO_SCHEMA_SEL);
				}
			}
		});
		toolBar.add(button);
		b.add(button);
		buttonListDB.add(button);
		// Open Matchresult
//		button = new JButton(new ImageIcon(GUIConstants.ICON_OPENMATCHRESULT));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_OPENMATCHRESULT));
		button.setToolTipText(GUIConstants.OPEN_MATCHRESULT);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if ((list_RepMatchresults != null)
						&& !list_RepMatchresults
								.isSelectionEmpty()) {
					Object[] matchresults = list_RepMatchresults
							.getSelectedValues();
//					ArrayList<Object> matchresultList = new ArrayList<Object>();
					for (int i = 0; i < matchresults.length; i++) {
						if (matchresults[i] instanceof SourceRelationship) {
//							matchresultList.add(matchresults[i]);
							controller.loadMatchresultFromDB((SourceRelationship)matchresults[i]);
						}
					}
//					controller.loadMatchresultsFromDB(matchresultList);
				} else {
					controller
							.setStatus(GUIConstants.NO_MATCHRESULT_SEL);
				}
			}
		});
		toolBar.add(button);
		b.add(button);
		buttonListDB.add(button);
		// Delete this Schema(s) OR Matchresult(s)
	//		button = new JButton(new ImageIcon(GUIConstants.ICON_DELETE_DB));
			button = new JButton(Controller.getImageIcon(GUIConstants.ICON_DELETE_DB));
		button.setToolTipText(GUIConstants.DELETE_SCHEMA_MATCHRESULT_DB);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if ((list_RepSchemas != null)
						&& !list_RepSchemas
								.isSelectionEmpty()) {
					// Sources
					Object[] values = list_RepSchemas
							.getSelectedValues();
					ArrayList<Object> removeList = new ArrayList<Object>();
					for (int i = 0; i < values.length; i++) {
						removeList.add(values[i]);
					}
						repSchemas.removeAll(removeList);
						controller.deleteSchemaDB(removeList);
						list_RepSchemas.setListData(repSchemas.toArray());
						properties_Repository.showNothing();
						listScrollPane_RepSchemas.repaint();
						return;
				}
				if ((list_RepMatchresults != null)
							&& !list_RepMatchresults
									.isSelectionEmpty()) {
						// SourceRelationships
						Object[] values = list_RepMatchresults
								.getSelectedValues();
						ArrayList<Object> removeList = new ArrayList<Object>();
						for (int i = 0; i < values.length; i++) {
							removeList.add(values[i]);
						}
						repMatchresults.removeAll(removeList);
						controller.deleteMatchresultDB(removeList);
						list_RepMatchresults.setListData(repMatchresults.toArray());
						properties_Repository.showNothing();
						listScrollPane_RepMatchresults.repaint();
						return;
					}
					controller.setStatus(GUIConstants.NOTHING_SEL);
				}
			});
			b.add(button);
			toolBar.add(button);
			buttonListDB.add(button);
		JPanel repositoryPanel = new JPanel(new BorderLayout());
		repSchemas = LoadFromDBThread.getAllSchemas(controller);
		if (repSchemas == null) {
			repSchemas = new ArrayList<Source>();
			list_RepSchemas = new JList();
			repDomains = new ArrayList<String>();
			list_RepDomains = new JList();
		} else {
			list_RepSchemas = new JList(repSchemas.toArray());
		}
		setAllDomains(repSchemas);
		list_RepDomains.setCellRenderer(new JListCellRenderer());
		list_RepSchemas.setCellRenderer(new JListCellRenderer());
		listScrollPane_RepSchemas = new JScrollPane(list_RepSchemas);
		listScrollPane_RepDomain = new JScrollPane(list_RepDomains);
		list_RepSchemas
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent _event) {
						if (list_RepSchemas != null) {
							Source s = (Source) list_RepSchemas
									.getSelectedValue();
							if (s != null) {
								properties_Repository
										.showProperties(s);
							}
						}
					}
				});
		list_RepSchemas.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent _event) {
				if (list_RepSchemas != null) {
					Source s = (Source) list_RepSchemas
							.getSelectedValue();
					if (s != null) {
						properties_Repository
								.showProperties(s);
					}
				}
			}
		});
		list_RepSchemas
				.addMouseListener(new SchemaMouseAdapter(REPOSITORY));
		list_RepSchemas.setBackground(MainWindow.GLOBAL_BACKGROUND);
		list_RepDomains
		.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent _event) {
				if (list_RepDomains != null) {
					String d = (String) list_RepDomains
							.getSelectedValue();
					if (d != null) {
						properties_Repository
								.showNothing();
						showDomain(d);
					}
				}
			}
		});
	list_RepDomains.addFocusListener(new FocusAdapter() {
		public void focusGained(FocusEvent _event) {
			if (list_RepDomains != null) {
				String d = (String) list_RepDomains
						.getSelectedValue();
				if (d != null) {
					properties_Repository
						.showNothing();
					showDomain(d);
				}
			}
		}
	});
	list_RepDomains
			.addMouseListener(new SchemaMouseAdapter(REPOSITORY));
	list_RepDomains.setBackground(MainWindow.GLOBAL_BACKGROUND);
		listScrollPane_RepSchemas
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		listScrollPane_RepSchemas
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		listScrollPane_RepDomain
		.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		listScrollPane_RepDomain
		.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		repMatchresults = LoadFromDBThread.getAllDBMatchresults(controller);
		// TO DO: change back (later)
		//		allDBMatchResults
		// =LoadFromDBThread.getAllDBMatchresultsInclInternal(controller);
		if (repMatchresults == null) {
			repMatchresults = new ArrayList<SourceRelationship>();
		}
		list_RepMatchresults = new JList(repMatchresults.toArray());
		list_RepMatchresults.setCellRenderer(new JListCellRenderer());
		list_RepMatchresults.addMouseListener(new MatchresultMouseAdapter(
				REPOSITORY));
		list_RepMatchresults.setBackground(MainWindow.GLOBAL_BACKGROUND);
		listScrollPane_RepMatchresults = new JScrollPane(list_RepMatchresults);
		list_RepMatchresults
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent _event) {
						if ((list_RepMatchresults != null)
								&& !list_RepMatchresults
										.isSelectionEmpty()) {
							SourceRelationship sr = (SourceRelationship) list_RepMatchresults
									.getSelectedValue();
							properties_Repository
									.showProperties(sr);
						}
					}
				});
		list_RepMatchresults.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent _event) {
				if ((list_RepMatchresults != null)
						&& !list_RepMatchresults
								.isSelectionEmpty()) {
					SourceRelationship sr = (SourceRelationship) list_RepMatchresults
							.getSelectedValue();
					properties_Repository
							.showProperties(sr);
				}
			}
		});
		listScrollPane_RepMatchresults
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		listScrollPane_RepMatchresults
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		repositoryPanel.add(toolBar, BorderLayout.NORTH);
		
		
		JPanel panelLists = new JPanel(new GridLayout(3, 1));
		JPanel panel0 = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Domains", SwingConstants.CENTER);
		label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		panel0.add(label, BorderLayout.NORTH);
		panel0.add(listScrollPane_RepDomain, BorderLayout.CENTER);
		JPanel panel1 = new JPanel(new BorderLayout());
		label = new JLabel(GUIConstants.SCHEMAS, SwingConstants.CENTER);
		label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		panel1.add(label, BorderLayout.NORTH);
		panel1.add(listScrollPane_RepSchemas, BorderLayout.CENTER);
		JPanel panel2 = new JPanel(new BorderLayout());
		label = new JLabel(GUIConstants.MATCHRESULTS_, SwingConstants.CENTER);
		label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		panel2.add(label, BorderLayout.NORTH);
		panel2.add(listScrollPane_RepMatchresults, BorderLayout.CENTER);
		panelLists.add(panel0);
		panelLists.add(panel1);
		panelLists.add(panel2);
		repositoryPanel.add(panelLists, BorderLayout.CENTER);
		properties_Repository = new PropertieJPanel(REPOSITORY, controller.getManager());
		repositoryPanel.add(properties_Repository, BorderLayout.SOUTH);
		return repositoryPanel;
	}
	
//	JComboBox getDomains(){
//		Vector matcherData = new Vector();
//		matcherData.add("PurchaseOrder");
//		matcherData.add("Literature");
//		matcherData.add("Biology");
//		JComboBox combo = new JComboBox(matcherData);
//		combo.setBackground(MainWindow.GLOBAL_BACKGROUND);
//		combo.setMaximumRowCount(MainWindow.MAX_ROW);
//		return combo;
//	}
	
	void setAllDomains(ArrayList _schemas){
		repDomains = new ArrayList<String>();
		if (list_RepDomains==null){
			list_RepDomains = new JList();
		}
		if (_schemas==null || _schemas.isEmpty()){
			list_RepDomains.setListData(repDomains.toArray());
		} else {
//			repDomains.add(GUIConstants.SHOW_ALL_HTML);
			repDomains.add(GUIConstants.SHOW_ALL_NORMAL);
			for (int i=0; i<_schemas.size(); i++){
				String domain =((Source)_schemas.get(i)).getDomain();
				if (domain==null){
//					domain=GUIConstants.NO_DOMAIN_HTML;
					domain=GUIConstants.NO_DOMAIN_NORMAL;
				}
				if (!repDomains.contains(domain)){
	//				for (int j=0; j<repDomains.size(); j++){
	//					String current = (String)repDomains.get(j);
	//					if (current.compareToIgnoreCase(domain)>)
	//				}
					repDomains.add(domain);
				}
			}
		list_RepDomains.setListData(repDomains.toArray());
//		list_RepDomains.setSelectedValue(GUIConstants.SHOW_ALL_HTML, true);
		list_RepDomains.setSelectedValue(GUIConstants.SHOW_ALL_NORMAL, true);
		}
	}

	JPanel getWorkspaceToolbars() {
		ButtonGroup b = new ButtonGroup();
		JToolBar toolBar1 = new JToolBar();
		toolBar1.setMargin(BUTTON_INSETS_LESS);
		toolBar1.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
		toolBar1.setOrientation(SwingConstants.HORIZONTAL);
		toolBar1.setFloatable(false);
		// Open Source
//		JButton button = new JButton(new ImageIcon(GUIConstants.ICON_OPENSOURCE));
		JButton button = new JButton(Controller.getImageIcon(GUIConstants.ICON_OPENSOURCE));
		button.setToolTipText(GUIConstants.OPEN_AS_SOURCE);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if ((list_WorkSchemas != null)
						&& !list_WorkSchemas
								.isSelectionEmpty()) {
					Graph s = (Graph) list_WorkSchemas
							.getSelectedValue();
					controller.loadSourceSchema(s);
				} else {
					controller
							.setStatus(GUIConstants.NO_SCHEMA_SEL);
				}
			}
		});
		toolBar1.add(button);
		b.add(button);
		buttonListDB.add(button);
		// Open Target
//		button = new JButton(new ImageIcon(GUIConstants.ICON_OPENTARGET));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_OPENTARGET));
		button.setToolTipText(GUIConstants.OPEN_AS_TARGET);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if ((list_WorkSchemas != null)
						&& !list_WorkSchemas
								.isSelectionEmpty()) {
					Graph s = (Graph) list_WorkSchemas
							.getSelectedValue();
					controller.loadTargetSchema(s);
				} else {
					controller
							.setStatus(GUIConstants.NO_SCHEMA_SEL);
				}
			}
		});
		toolBar1.add(button);
		b.add(button);
		buttonListDB.add(button);
		// Open Matchresult
//		button = new JButton(new ImageIcon(GUIConstants.ICON_OPENMATCHRESULT));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_OPENMATCHRESULT));
		button.setToolTipText(GUIConstants.OPEN_MATCHRESULT);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if ((list_WorkMatchresults != null)
						&& !list_WorkMatchresults
								.isSelectionEmpty()) {
					MatchResult mr = (MatchResult) list_WorkMatchresults
							.getSelectedValue();
					controller.setNewMatchResult(mr, true);
					properties_Workspace.showProperties(mr);
				} else {
					controller
							.setStatus(GUIConstants.NO_MATCHRESULT_SEL);
				}
			}
		});
		toolBar1.add(button);
		b.add(button);
		buttonListDB.add(button);
		// Save Schema/Matchresult to DB
	//		button = new JButton(new ImageIcon(GUIConstants.ICON_MATCHRESULT_DB));
			button = new JButton(Controller.getImageIcon(GUIConstants.ICON_MATCHRESULT_DB));
		button.setToolTipText(GUIConstants.SAVE_SCHEMA_MATCHRESULT);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if ((list_WorkSchemas != null)
						&& !list_WorkSchemas
								.isSelectionEmpty()) {
					int[] selected = list_WorkSchemas.getSelectedIndices();
					ArrayList<Graph> graphs = new ArrayList<Graph>();
					for (int i = 0; i < selected.length; i++) {
						graphs.add(workSchemas
								.get(selected[i]));
						}
						controller.saveSchemaToDB(graphs);
						return;
					}
					if ((list_WorkMatchresults != null)
							&& !list_WorkMatchresults
									.isSelectionEmpty()) {
						int[] selected = list_WorkMatchresults.getSelectedIndices();
						ArrayList<MatchResult> results = new ArrayList<MatchResult>();
						for (int i = 0; i < selected.length; i++) {
							results.add(workMatchresults
									.get(selected[i]));
						}
						controller.saveMatchresultToDB(results);
						return;
					}
					controller.setStatus(GUIConstants.NOTHING_SEL);
				}
			});
			b.add(button);
			toolBar1.add(button);
			buttonListDB.add(button);
		// Delete this Schema(s) OR Matchresult(s)
//		button = new JButton(new ImageIcon(GUIConstants.ICON_DELETE_DB));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_DELETE_TMP));
		button.setToolTipText(GUIConstants.DELETE_SCHEMA_MATCHRESULT_WS);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if ((list_WorkSchemas != null)
						&& !list_WorkSchemas
								.isSelectionEmpty()) {
					removeSelectedWorkSchemas();
					return;
				}
				if ((list_WorkMatchresults != null)
						&& !list_WorkMatchresults
								.isSelectionEmpty()) {
					removeSelectedWorkMatchresult();
					return;
				}
				controller.setStatus(GUIConstants.NOTHING_SEL);
			}
		});
		b.add(button);
		toolBar1.add(button);
		buttonListDB.add(button);
		
		
		// use: schema management
		JToolBar toolBar2 = new JToolBar();
		toolBar2.setMargin(BUTTON_INSETS_LESS);
		toolBar2.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
		toolBar2.setOrientation(SwingConstants.HORIZONTAL);
		toolBar2.setFloatable(false);
		// Merge this and another temporary Matchresult
//		button = new JButton(new ImageIcon(GUIConstants.ICON_MERGE));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_MERGE));
		button.setToolTipText(GUIConstants.MERGE_MATCHRESULT);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				int[] indices = list_WorkMatchresults
						.getSelectedIndices();
				if (indices == null) {
					controller
							.setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);
					return;
				}
				ArrayList<MatchResult> list = new ArrayList<MatchResult>();
				for (int i = 0; i < indices.length; i++) {
					list.add(workMatchresults.get(indices[i]));
				}
				controller.mergeMatchresult(list);
			}
		});
		b.add(button);
		toolBar2.add(button);
		buttonListRun.add(button);
		// Intersect:Get the overlapping part from this and another temporary
		// Matchresult
//		button = new JButton(new ImageIcon(GUIConstants.ICON_INTERSECT));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_INTERSECT));
		button.setToolTipText(GUIConstants.INTERSECT_MATCHRESULT);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				int[] indices = list_WorkMatchresults
						.getSelectedIndices();
				if (indices == null) {
					controller
							.setStatus(GUIConstants.NO_MATCHRESULT_CHOSEN);
					return;
				}
				ArrayList<MatchResult> list = new ArrayList<MatchResult>();
				for (int i = 0; i < indices.length; i++) {
					list.add(workMatchresults.get(indices[i]));
				}
				controller.intersectMatchresult(list);
			}
		});
		b.add(button);
		toolBar2.add(button);
		buttonListRun.add(button);
		// Diff: Get the difference from this and another temporary Matchresult
//		button = new JButton(new ImageIcon(GUIConstants.ICON_DIFFERENCE));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_DIFFERENCE));
		button.setToolTipText(GUIConstants.DIFFERENCE_MATCHRESULT);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				MatchResult mr = (MatchResult) list_WorkMatchresults
						.getSelectedValue();
				controller.differenceMatchresult(mr);
			}
		});
		b.add(button);
		toolBar2.add(button);
		buttonListRun.add(button);
		// Compare this Matchresult with another
//		button = new JButton(new ImageIcon(GUIConstants.ICON_COMPARE));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_COMPARE));
		button.setToolTipText(GUIConstants.COMPARE_MATCHRESULT_WITH);
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				controller.compareMatchresult((MatchResult) list_WorkMatchresults.getSelectedValue());
			}
		});
		b.add(button);
		toolBar2.add(button);
		buttonListRun.add(button);
//		// Edit a Matchresult
////		edit = new JToggleButton(new ImageIcon(GUIConstants.ICON_EDIT));
//		edit = new JToggleButton(Controller.getImageIcon(GUIConstants.ICON_EDIT));
//		edit.setToolTipText(GUIConstants.EDIT_MATCHRESULT);
//		edit.setMargin(BUTTON_INSETS_LESS);
//		edit.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				boolean state = ((JToggleButton) _event.getSource())
//						.isSelected();
//				if (controller.editMatchresult(state, true)) {
//					controller.getMainWindow()
//							.setEditMatchresult(state);
//				} else {
//					((JToggleButton) _event.getSource()).setSelected(!state);
//				}
//			}
//		});
//		//      b.add(edit);
//		toolBar2.add(edit);
//		runButtonList.add(edit);

		
		
		// Duplicate this Matchresult
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_DUPLICATE));
		button.setToolTipText("Duplicate this Matchresult");
		button.setMargin(BUTTON_INSETS_LESS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				controller.duplicateMatchresult();
			}
		});
		b.add(button);
		toolBar2.add(button);
		buttonListRun.add(button);
		
	
		
		
		// use: schema management
		JToolBar toolBar3 = new JToolBar();
		toolBar3.setMargin(BUTTON_INSETS_LESS);
		toolBar3.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
		toolBar3.setOrientation(SwingConstants.HORIZONTAL);
		toolBar3.setFloatable(false);
		
//		// Domain (Matchresult)
////		button = new JButton(new ImageIcon(GUIConstants.ICON_DOMAIN));
//		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_DOMAIN));
//		button.setToolTipText(GUIConstants.DOMAIN_TOOLTIP);
//		button.setMargin(BUTTON_INSETS_LESS);
//		button.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				MatchResult mr = (MatchResult) list_WorkMatchresults
//						.getSelectedValue();
//				controller.domainMatchresult(false, mr);
//			}
//		});
//		toolBar3.add(button);
//		runButtonList.add(button);
//		// InvertDomain (Matchresult)
////		button = new JButton(new ImageIcon(GUIConstants.ICON_INVERTDOMAIN));
//		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_INVERTDOMAIN));
//		button.setToolTipText(GUIConstants.INVERTDOMAIN_TOOLTIP);
//		button.setMargin(BUTTON_INSETS_LESS);
//		button.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				MatchResult mr = (MatchResult) list_WorkMatchresults
//						.getSelectedValue();
//				controller.domainMatchresult(true, mr);
//			}
//		});
//		toolBar3.add(button);
//		runButtonList.add(button);
//		// Range (Matchresult)
////		button = new JButton(new ImageIcon(GUIConstants.ICON_RANGE));
//		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_RANGE));		
//		button.setToolTipText(GUIConstants.RANGE_TOOLTIP);
//		button.setMargin(BUTTON_INSETS_LESS);
//		button.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				MatchResult mr = (MatchResult) list_WorkMatchresults
//						.getSelectedValue();
//				controller.rangeMatchresult(false, mr);
//			}
//		});
//		toolBar3.add(button);
//		runButtonList.add(button);
//		// InvertRange (Matchresult)
////		button = new JButton(new ImageIcon(GUIConstants.ICON_INVERTRANGE));
//		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_INVERTRANGE));		
//		button.setToolTipText(GUIConstants.INVERTRANGE_TOOLTIP);
//		button.setMargin(BUTTON_INSETS_LESS);
//		button.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				MatchResult mr = (MatchResult) list_WorkMatchresults
//						.getSelectedValue();
//				controller.rangeMatchresult(true, mr);
//			}
//		});
//		toolBar3.add(button);
//		runButtonList.add(button);
//		// SMerge (Matchresult)
////		button = new JButton(new ImageIcon(GUIConstants.ICON_SMERGE));
//		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_SMERGE));		
//		button.setToolTipText(GUIConstants.SMERGE_TOOLTIP);
//		button.setMargin(BUTTON_INSETS_LESS);
//		button.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				MatchResult mr = (MatchResult) list_WorkMatchresults
//						.getSelectedValue();
//				controller.smergeMatchresult(mr);
//			}
//		});
//		toolBar3.add(button);
//		runButtonList.add(button);


		JPanel toolBars = new JPanel(new BorderLayout(0, -3));
		//		toolBar1.setBorder(BorderFactory.createLineBorder(Color.RED));
		//		toolBar2.setBorder(BorderFactory.createLineBorder(Color.RED));
		toolBar1.setOpaque(false);
		toolBar1.setBorderPainted(false);
		toolBars.add(toolBar1, BorderLayout.NORTH);
		toolBar2.setBorderPainted(false);
		toolBars.add(toolBar2, BorderLayout.CENTER);
		
//		toolBar3.setBorderPainted(false);
//		toolBars.add(toolBar3, BorderLayout.SOUTH);
		return toolBars;
	}

	/*
	 * create and return the operation panel containing: toolbar, temporary
	 * matchresult list and properties
	 */
	JPanel getWorkspacePanel() {
		JPanel workspacePanel = new JPanel(new BorderLayout());
		list_WorkMatchresults = new JList(workMatchresults.toArray());
		list_WorkMatchresults.setCellRenderer(new JListCellRenderer());
		listScrollPane_WorkMatchresults = new JScrollPane(
				list_WorkMatchresults);
		workspacePanel.add(listScrollPane_WorkMatchresults,
				BorderLayout.CENTER);
		list_WorkMatchresults.addMouseListener(new MatchresultMouseAdapter(
				WORKSPACE));
		list_WorkMatchresults
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent _event) {
						if ((list_WorkMatchresults != null)
								&& !list_WorkMatchresults
										.isSelectionEmpty()) {
							MatchResult mr =  workMatchresults
									.get(list_WorkMatchresults
											.getSelectedIndex());
							properties_Workspace
									.showProperties(mr);
						}
					}
				});
		list_WorkMatchresults.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent _event) {
				if ((list_WorkMatchresults != null)
						&& !list_WorkMatchresults
								.isSelectionEmpty()) {
					MatchResult mr =  workMatchresults
							.get(list_WorkMatchresults
									.getSelectedIndex());
					properties_Workspace.showProperties(mr);
				}
			}
		});
		list_WorkMatchresults.setBackground(MainWindow.GLOBAL_BACKGROUND);
		listScrollPane_WorkMatchresults
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		listScrollPane_WorkMatchresults
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		list_WorkSchemas = new JList(workSchemas.toArray());
		list_WorkSchemas.setCellRenderer(new JListCellRenderer());
		listScrollPane_WorkSchemas = new JScrollPane(list_WorkSchemas);
		list_WorkSchemas
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent _event) {
						if ((list_WorkSchemas != null)
								&& !list_WorkSchemas
										.isSelectionEmpty()) {
							Graph graph = (Graph) list_WorkSchemas
									.getSelectedValue();
							properties_Workspace
									.showProperties(graph);
						}
					}
				});
		list_WorkSchemas.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent _event) {
				if ((list_WorkSchemas != null)
						&& !list_WorkSchemas
								.isSelectionEmpty()) {
					Graph graph =  workSchemas
							.get(list_WorkSchemas
									.getSelectedIndex());
					properties_Workspace
							.showProperties(graph);
				}
			}
		});
		list_WorkSchemas
				.addMouseListener(new SchemaMouseAdapter(WORKSPACE));
		list_WorkSchemas.setBackground(MainWindow.GLOBAL_BACKGROUND);
		listScrollPane_WorkSchemas
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		listScrollPane_WorkSchemas
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel toolBars = getWorkspaceToolbars();
		workspacePanel.add(toolBars, BorderLayout.NORTH);
		JPanel panelLists = new JPanel(new GridLayout(2, 1));
		JPanel panel1 = new JPanel(new BorderLayout());
		JLabel label = new JLabel(GUIConstants.SCHEMAS, SwingConstants.CENTER);
		label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		panel1.add(label, BorderLayout.NORTH);
		panel1.add(listScrollPane_WorkSchemas, BorderLayout.CENTER);
		JPanel panel2 = new JPanel(new BorderLayout());
		label = new JLabel(GUIConstants.MATCHRESULTS_, SwingConstants.CENTER);
		label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		panel2.add(label, BorderLayout.NORTH);
		panel2.add(listScrollPane_WorkMatchresults, BorderLayout.CENTER);
		panelLists.add(panel1);
		panelLists.add(panel2);
		workspacePanel.add(panelLists, BorderLayout.CENTER);
		properties_Workspace = new PropertieJPanel(WORKSPACE, controller.getManager());
		workspacePanel.add(properties_Workspace, BorderLayout.SOUTH);
		return workspacePanel;
	}

	/*
	 * PropertieJPanel extends JPanel
	 */
	private class PropertieJPanel extends JPanel {
		TableModel tm;
		JTable table;
		Object current;
		String[][] data;
		static final int MAX = 8; 
//		int tab, subTab;
		ColoredTableCellRenderer renderer;
		Manager manager=null;
		
		public PropertieJPanel(int _tab, Manager manager) {
			super(new BorderLayout());
			this.manager=manager;
			data = new String[MAX][2];
			//crate table column model
			DefaultTableColumnModel cm = new DefaultTableColumnModel();
			TableColumn col = new TableColumn(0, 90);
			cm.addColumn(col);
			col = new TableColumn(1, 200);
			cm.addColumn(col);
			//crate table model
//			tab = _tab;
			tm = new TableModel(data, _tab);
			// create table and fill ContentPane
			table = new JTable(tm, cm);
			renderer = new ColoredTableCellRenderer(_tab);
			table.setDefaultRenderer(Object.class, renderer);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setRowSelectionInterval(0, 0);
			// create a scrollpane containing this table
			add(table, BorderLayout.NORTH);
		}

		/*
		 * empty all entries
		 */
		public void showNothing() {
			current = null;
			for (int i = 0; i < MAX; i++) {
				data[i][0] = GUIConstants.EMPTY;
				data[i][1] = GUIConstants.EMPTY;
			}
			repaint();
		}

		/*
		 * show properties of a schema
		 */
		public void showProperties(Source _source) {
			if (current == _source) {
				return;
			}
			tm.setSubTab(SCHEMAS);
			renderer.setSubTab(SCHEMAS);
			current = _source;
			data[0][0] = GUIConstants.M_NAME;
			data[1][0] = GUIConstants.M_COMMENT;
			data[2][0] = GUIConstants.M_PROVIDER;
			data[3][0] = GUIConstants.M_URL;
			data[4][0] = GUIConstants.M_CONTENT;
			data[5][0] = GUIConstants.M_AUTHOR;
			data[6][0] = GUIConstants.M_DOMAIN;
			data[7][0] = GUIConstants.M_VERSION;
			data[0][1] = _source.getName();
			data[1][1] = _source.getComment();
			data[2][1] = _source.getProvider();
			data[3][1] = _source.getUrl();
			data[4][1] = Source.typeToString(_source.getType());
			data[5][1] = _source.getAuthor();
			data[6][1] = _source.getDomain();
			data[7][1] = _source.getVersion();
			repaint();
		}

		/*
		 * show properties of a schema
		 */
		public void showProperties(Graph _graph) {
			if (current == _graph) {
				return;
			}
			properties_Workspace.getTable()
					.editingCanceled(new ChangeEvent(_graph));
			Source source = _graph.getSource();
			tm.setSubTab(SCHEMAS);
			renderer.setSubTab(SCHEMAS);
			current = source;
			data[0][0] = GUIConstants.M_NAME;
			data[1][0] = GUIConstants.M_COMMENT;
			data[2][0] = GUIConstants.M_PROVIDER;
			data[3][0] = GUIConstants.M_URL;
			data[4][0] = GUIConstants.M_CONTENT;
			data[0][1] = source.getName();
			data[1][1] = source.getComment();
			data[2][1] = source.getProvider();
//			data[3][1] = source.getUrl();
			data[4][1] = Source.typeToString(source.getType());
			
			data[5][0] = GUIConstants.M_AUTHOR;
			data[6][0] = GUIConstants.M_DOMAIN;
			data[7][0] = GUIConstants.M_VERSION;
			data[5][1] = source.getAuthor();
			data[6][1] = source.getDomain();
			data[7][1] = source.getVersion();
			repaint();
		}

		/*
		 * show properties of a matchresult (from the repository)
		 */
		public void showProperties(SourceRelationship sourceRel) {
			if (current == sourceRel) {
				return;
			}
			tm.setSubTab(MATCHRESULTS);
			renderer.setSubTab(MATCHRESULTS);
			current = sourceRel;
			data[0][0] = GUIConstants.M_NAME;
			data[1][0] = GUIConstants.M_COMMENT;
			data[2][0] = GUIConstants.M_SCHEMAS;
			data[3][0] = GUIConstants.M_OPERATION;
			data[4][0] = GUIConstants.M_TOTAL;
			data[0][1] = Source.typeToString(sourceRel.getType());
			data[1][1] = sourceRel.getComment();
			data[2][1] = controller.getManager().getSourceName(sourceRel) + GUIConstants.COMMA_SPACE
					+ controller.getManager().getTargetName(sourceRel);
			data[3][1] = sourceRel.getProvider();
			data[4][1] = GUIConstants.EMPTY + manager.getAccessor().getObjectRelCnt(sourceRel.getId());
			
			data[5][0] = GUIConstants.EMPTY;
			data[6][0] = GUIConstants.EMPTY;
			data[7][0] = GUIConstants.EMPTY;
			data[5][1] = GUIConstants.EMPTY;
			data[6][1] = GUIConstants.EMPTY;
			data[7][1] = GUIConstants.EMPTY;
			repaint();
		}

		/*
		 * show properties of a temporary matchresult
		 */
		public void showProperties(MatchResult _result) {
			if (current == _result) {
				return;
			}
			properties_Workspace.getTable()
					.editingCanceled(new ChangeEvent(_result));
			tm.setSubTab(MATCHRESULTS);
			renderer.setSubTab(MATCHRESULTS);
			current = _result;
			data[0][0] = GUIConstants.M_NAME;
			data[0][1] = _result.getName();
			data[1][0] = GUIConstants.M_COMMENT;
			data[1][1] = _result.getMatchInfo();
			data[2][0] = GUIConstants.M_SCHEMAS;
			data[2][1] = _result.getSourceGraph().getSource().getName()
					+ GUIConstants.COMMA_SPACE
					+ _result.getTargetGraph().getSource().getName();
//			if (_result.getMatcherName() != null) {
//				data[3][0] = GUIConstants.M_OPERATION;
//				data[3][1] = _result.getMatcherName();
//			} else {
				data[3][0] = GUIConstants.EMPTY;
				data[3][1] = GUIConstants.EMPTY;
//			}
			//			if (_result.getMatcherConfig() != null) {
			//				data[4][0] = GUIConstants.M_CONFIG;
			//				data[4][1] = _result.getMatcherConfig().toString();
			//			} else {
			//				data[4][0] = GUIConstants.EMPTY;
			//				data[4][1] = GUIConstants.EMPTY;
			//			}
			data[4][0] = GUIConstants.M_TOTAL;
			data[4][1] = _result.getMatchCount()+"";
			data[5][0] = GUIConstants.EMPTY;
			data[6][0] = GUIConstants.EMPTY;
			data[7][0] = GUIConstants.EMPTY;
			data[5][1] = GUIConstants.EMPTY;
			data[6][1] = GUIConstants.EMPTY;
			data[7][1] = GUIConstants.EMPTY;			
			repaint();
		}

		/*
		 * update the number of correspondences for the current view
		 */
		public void updateCount(MatchResult _result) {
			data[4][1] = _result.getMatchCount()+"";
			repaint();
		}

		/**
		 * @return Returns the table.
		 */
		public JTable getTable() {
			return table;
		}
	}

	/*
	 * while using the database disable most of the menu itemes and buttons when
	 * finished enable them
	 */
	public void setStateDB(boolean _state) {
//		if (buttonListDB.size() > 0) {
//			for (int i = 0; i < buttonListDB.size(); i++) {
//				if (buttonListDB.get(i) instanceof JButton) {
//					JButton current = ((JButton) buttonListDB.get(i));
//					current.setEnabled(!_state);
//				}
//			}
//		}
	}

	/*
	 * while running matching disable most of the menu itemes and buttons when
	 * finished matching enable them
	 */
	public void setStateRun(boolean _state) {
		setStateDB(_state);
		if (buttonListRun.size() > 0) {
			for (int i = 0; i < buttonListRun.size(); i++) {
//				if (buttonListRun.get(i) instanceof JButton) {
//					JButton current = ((JButton) buttonListRun.get(i));
//					current.setEnabled(!_state);
//				} else
				if (buttonListRun.get(i) instanceof JToggleButton) {
					JToggleButton current = ((JToggleButton) buttonListRun
							.get(i));
					current.setEnabled(!_state);
				}
			}
		}
	}

	/**
	 * update tooltip on run/play button
	 */
	public void setButtonRunTooltip(String _tooltiptextsuffix) {
		if (runButtonList.size() > 0) {
			for (int i = 0; i < runButtonList.size(); i++) {
				if (runButtonList.get(i) instanceof JButton) {
					JButton current = ((JButton) runButtonList.get(i));
					if (current.getToolTipText().startsWith(
							GUIConstants.EXECUTE_MATCHING)) { // it's the play button
						current.setToolTipText(GUIConstants.EXECUTE_MATCHING
								+ _tooltiptextsuffix);
					}
				}
			}
		}
	}

	/*
	 * set the given controller to the current one, update all schemas and
	 * matchresults in DB
	 */
	public void setController(Controller _controller) {
		controller = _controller;
		properties_Repository.showNothing();
		// Schemas
		ArrayList<Source> listModel_SchemasALL = LoadFromDBThread
				.getAllSchemas(_controller);
		if (listModel_SchemasALL != null) {
			ArrayList<Source> listModel_SchemasNEW = (ArrayList) listModel_SchemasALL
					.clone();
			ArrayList<Source> listModel_SchemasREMOVED = (ArrayList) repSchemas
					.clone();
			listModel_SchemasNEW.removeAll(repSchemas);
			listModel_SchemasREMOVED.removeAll(listModel_SchemasALL);
			if (listModel_SchemasNEW.size() > 0) {
				ArrayList helpList = (ArrayList) listModel_SchemasNEW.clone();
				repSchemas.removeAll(listModel_SchemasREMOVED);
				listModel_SchemasNEW.addAll(repSchemas);
				repSchemas = listModel_SchemasNEW;
				repSchemas = Sort.sortSources(repSchemas);
				list_RepSchemas.setListData(repSchemas.toArray());
				int[] selected = new int[helpList.size()];
				for (int i = 0; i < helpList.size(); i++) {
					selected[i] = repSchemas.lastIndexOf(helpList.get(i));
				}
				list_RepSchemas.setSelectedIndices(selected);
				Object o = list_RepSchemas.getSelectedValue();
				list_RepSchemas.setSelectedValue(o, true);
				list_RepMatchresults.clearSelection();
			} else {
				repSchemas = listModel_SchemasALL;
				Object selected = list_RepSchemas.getSelectedValue();
				list_RepSchemas.setListData(repSchemas.toArray());
				if ((selected != null) && repSchemas.contains(selected)) {
					list_RepSchemas.setSelectedIndex(repSchemas
							.lastIndexOf(selected));
				}
			}
//			listScrollPane_RepSchemas.getVerticalScrollBar().setValue(0);
		} else {
			repSchemas = new ArrayList<Source>();
//			Object selected = list_RepSchemas.getSelectedValue();
			list_RepSchemas.setListData(repSchemas.toArray());
//			if ((selected != null) && repSchemas.contains(selected)) {
//				list_RepSchemas.setSelectedIndex(repSchemas
//						.lastIndexOf(selected));
//			}
		}
		setAllDomains(repSchemas);
		listScrollPane_RepSchemas.repaint();
		// Matchresults (DB)
		//			ArrayList allDBMatchResultsALL
		// =LoadFromDBThread.getAllDBMatchresults(controller);
		// TO DO: change back (later)
		ArrayList<SourceRelationship> allDBMatchResultsALL = LoadFromDBThread
				.getAllDBMatchresultsInclInternal(_controller);
		if (allDBMatchResultsALL == null) {
			allDBMatchResultsALL = new ArrayList<SourceRelationship>();
		}
		ArrayList<SourceRelationship> allDBMatchResultsNEW = (ArrayList<SourceRelationship>) allDBMatchResultsALL
				.clone();
		allDBMatchResultsNEW.removeAll(repMatchresults);
		ArrayList<SourceRelationship> allDBMatchResultsREMOVED = (ArrayList<SourceRelationship>) repMatchresults
				.clone();
		allDBMatchResultsREMOVED.removeAll(allDBMatchResultsALL);
		if (allDBMatchResultsNEW.size() > 0) {
			ArrayList helpList = (ArrayList) allDBMatchResultsNEW.clone();
			repMatchresults.removeAll(allDBMatchResultsREMOVED);
			allDBMatchResultsNEW.addAll(repMatchresults);
			repMatchresults = allDBMatchResultsNEW;
			repMatchresults = Sort.sortSourceRelationsships(repMatchresults);
			list_RepMatchresults.setListData(repMatchresults.toArray());
			int[] selected = new int[helpList.size()];
			for (int i = 0; i < helpList.size(); i++) {
				selected[i] = repMatchresults.indexOf(helpList.get(i));
			}
			list_RepMatchresults.setSelectedIndices(selected);
			
list_RepMatchresults.ensureIndexIsVisible(selected[0]);
//Object o = list_RepMatchresults.getSelectedValue();
//			list_RepMatchresults.setSelectedValue(o, true);

			list_RepSchemas.clearSelection();
		} else {
			repMatchresults = allDBMatchResultsALL;
			Object selected = list_RepMatchresults.getSelectedValue();
			list_RepMatchresults.setListData(repMatchresults.toArray());
			if ((selected != null) && repMatchresults.contains(selected)) {
				list_RepMatchresults.setSelectedIndex(repMatchresults
						.indexOf(selected));
			}
		}
		listScrollPane_RepMatchresults.repaint();
	}

	/*
	 * returns the current selected temporary Matchresult
	 */
	public MatchResult getSelectedWorkMatchresult() {
		if ((list_WorkMatchresults != null)
				&& !list_WorkMatchresults.isSelectionEmpty()) {
			MatchResult selected = (MatchResult) list_WorkMatchresults
					.getSelectedValue();
			return selected;
		}
		return null;
	}

	/*
	 * returns the current selected temporary Schema
	 */
	public Graph getSelectedWorkSchema() {
		if ((list_WorkSchemas != null)
				&& !list_WorkSchemas.isSelectionEmpty()) {
			Graph selected = (Graph) list_WorkSchemas
					.getSelectedValue();
			return selected;
		}
		return null;
	}

	/*
	 * swap the matchresults (if some exist) and update the properties of the
	 * current selected matchresult
	 */
	public void swap() {
		if ((workMatchresults == null) || (workMatchresults.size() == 0)) {
			return;
		}
		ArrayList<MatchResult> tmpMatchresultsSwap = new ArrayList<MatchResult>();
		// swap all matchresults
		for (int i = 0; i < workMatchresults.size(); i++) {
			MatchResult mr = workMatchresults.get(i);
			MatchResult transposed = MatchResult.transpose(mr);
//			transposed.setMatcherName(mr.getMatcherName());
//			transposed.setMatcherConfig(mr.getMatcherConfig());
			tmpMatchresultsSwap.add(transposed);
		}
		workMatchresults = tmpMatchresultsSwap;
		MatchResult selected = (MatchResult) list_WorkMatchresults
				.getSelectedValue();
		// reload property panel of Operations (if a matchresult was selected)
		// -> list stays the same (same name of match result)
		if (selected != null) {
			properties_Workspace.showProperties(selected);
		}
	}

	/*
	 * remove the current selected temporary Matchresult
	 */
	public void removeWorkMatchresult(Graph _graph) {
		if ((workMatchresults != null) && _graph != null) {
			ArrayList<MatchResult> removeWorkResult = new ArrayList<MatchResult>();
			MatchResult result = controller.getMainWindow()
					.getNewContentPane().getResult();
			for (int i = 0; i < workMatchresults.size(); i++) {
				MatchResult mr =  workMatchresults.get(i);
				if (mr.getSourceGraph().getSource().equals(_graph.getSource())) {
					removeWorkResult.add(mr);
					if (result != null && mr.equals(result)) {
						properties_Workspace.showNothing();
						controller.setNewMatchResult(null);
					}
				} else if (mr.getTargetGraph().getSource().equals(
						_graph.getSource())) {
					removeWorkResult.add(mr);
					if (result != null && mr.equals(result)) {
						properties_Workspace.showNothing();
						controller.setNewMatchResult(null);
					}
				}
			}
			if (removeWorkResult.size() > 0) {
				workMatchresults.removeAll(removeWorkResult);
				list_WorkMatchresults.setListData(workMatchresults.toArray());
				if (workMatchresults.isEmpty()){
					countWorkMatchresults = 0;
				}
			}
		}
	}

	/*
	 * remove the current selected temporary Matchresult
	 */
	public void removeSelectedWorkMatchresult() {
		if ((list_WorkMatchresults != null)
				&& !list_WorkMatchresults.isSelectionEmpty()) {
			int[] selectedAll = list_WorkMatchresults.getSelectedIndices();
			if (selectedAll != null) {
				ArrayList<MatchResult> removeWorkResult = new ArrayList<MatchResult>();
				int firstSelected = selectedAll[0];
				for (int i = selectedAll.length - 1; i >= 0; i--) {
					int selected = selectedAll[i];
					MatchResult mr = workMatchresults
							.get(selected);
					removeWorkResult.add(mr);
				}
				workMatchresults.removeAll(removeWorkResult);
				properties_Workspace.showNothing();
				list_WorkMatchresults.setListData(workMatchresults.toArray());
				if (workMatchresults.size() > 0) {
					if (firstSelected > 0) {
						list_WorkMatchresults
								.setSelectedIndex(firstSelected - 1);
						properties_Workspace
								.showProperties( workMatchresults
										.get(firstSelected - 1));
						//						controller.setNewMatchResult(
						//								(MatchResult) workMatchResults
						//										.get(firstSelected - 1), true);
					} else {
						list_WorkMatchresults.setSelectedIndex(firstSelected);
						properties_Workspace
								.showProperties( workMatchresults
										.get(firstSelected));
						//						controller.setNewMatchResult(
						//								(MatchResult) workMatchResults
						//										.get(firstSelected), true);
					}
				}
				controller.setNewMatchResult(null);
				listScrollPane_WorkMatchresults.repaint();
				listScrollPane_WorkSchemas.repaint();
			}
		}
	}

	/*
	 * remove the current selected temporary Matchresult
	 */
	public void removeSelectedWorkSchemas() {
		if ((list_WorkSchemas != null)
				&& !list_WorkSchemas.isSelectionEmpty()) {
			Object[] graphs = list_WorkSchemas.getSelectedValues();
			int firstSelected = list_WorkSchemas.getSelectedIndices()[0];
			for (int i = 0; i < graphs.length; i++) {
				Graph graph = (Graph) graphs[i];
				workSchemas.remove(graph);
				removeWorkMatchresult(graph);
				if (graph.getSource().equals(
						controller.getGUIMatchresult().getSourceSource())) {
					controller.closeSchema(true, false);
				}
				if (graph.getSource().equals(
						controller.getGUIMatchresult().getTargetSource())) {
					controller.closeSchema(false, false);
				}
			}
			list_WorkSchemas.setListData(workSchemas.toArray());
			if (workSchemas.size() > 0) {
				if (firstSelected < workSchemas.size()) {
					if (firstSelected > 0) {
						list_WorkSchemas
								.setSelectedIndex(firstSelected - 1);
						properties_Workspace
								.showProperties( workSchemas
										.get(firstSelected - 1));
						//						controller.setNewMatchResult(
						//								(MatchResult) workMatchResults
						//										.get(firstSelected - 1), true);
					} else {
						list_WorkSchemas.setSelectedIndex(firstSelected);
						properties_Workspace
								.showProperties( workSchemas
										.get(firstSelected));
						//						controller.setNewMatchResult(
						//								(MatchResult) workMatchResults
						//										.get(firstSelected), true);
					}
				} else {
					list_WorkSchemas.setSelectedIndex(0);
					properties_Workspace
							.showProperties(workSchemas
									.get(0));
				}
			} else {
				properties_Workspace.showNothing();
			}
			listScrollPane_WorkSchemas.repaint();
			listScrollPane_WorkMatchresults.repaint();
		}
	}

	/*
	 * return all temporary Matchresults
	 */
	public ArrayList getAllWorkMatchresults() {
		return (ArrayList) workMatchresults.clone();
	}
	
	/*
	 * return all Domains
	 */
	public ArrayList getAllDomains() {
		return (ArrayList) repDomains.clone();
	}
	
	/*
	 * return all Repository Schemas
	 */
	public ArrayList getRepSchemas() {
		return (ArrayList) repSchemas.clone();
	}
	
	/*
	 * return all temporary Matchresults
	 */
	public ArrayList getAllWorkSchemas() {
		return workSchemas;
	}

	/*
	 * count all temporary Matchresults
	 */
	public int countAllWorkMatchresults() {
		return workMatchresults.size();
	}

	/*
	 * update the current Matchresult by adding or deleting a correspondence for the
	 * given source and target node
	 */
	public MatchResult updateMatchresult(DefaultMutableTreeNode _source,
			DefaultMutableTreeNode _target) {
		if ((list_WorkMatchresults != null)
				&& !list_WorkMatchresults.isSelectionEmpty()) {
			int selected = list_WorkMatchresults.getSelectedIndex();
			MatchResult mr = (MatchResult) list_WorkMatchresults
					.getSelectedValue();
			ArrayList aObj = (ArrayList) _source.getUserObject();
			ArrayList bObj = (ArrayList) _target.getUserObject();
			float sim = mr.getSimilarity(aObj, bObj);
			if ((sim == MatchResult.SIM_UNDEF) || (sim == MatchResult.SIM_MIN)) {
				mr.append(aObj, bObj, MatchResult.SIM_MAX);
			} else {
				mr.remove(aObj, bObj);
			}
			workMatchresults.set(selected, mr);
			// update number of correspondences in properties
			properties_Workspace.updateCount(mr);
			return mr;
		}
		return null;
	}
	
	/*
	 * update the current Matchresult 
	 */
	public void updateMatchresult(MatchResult _mr) {
		if ((list_WorkMatchresults != null)
				&& !list_WorkMatchresults.isSelectionEmpty()) {
			int selected = list_WorkMatchresults.getSelectedIndex();
			workMatchresults.set(selected, _mr);
			// update number of correspondences in properties
			properties_Workspace.updateCount(_mr);
		}
	}	

	/*
	 * update the current shown temporary Matchresult with the given name
	 */
	public void updateMatchresultName(String _name) {
		// update Matchresult name
		if ((list_WorkMatchresults != null)
				&& !list_WorkMatchresults.isSelectionEmpty()) {
			int selected = list_WorkMatchresults.getSelectedIndex();
			MatchResult mr = (MatchResult) list_WorkMatchresults
					.getSelectedValue();
			if ((controller.getGUIMatchresult().getMatchResult() != null)
					&& controller.getGUIMatchresult().getMatchResult().equals(mr)) {
				mr.setName(_name);
				controller.getMainWindow().getNewContentPane()
						.setMatchresultLabel(mr);
			} else {
				mr.setName(_name);
			}
			workMatchresults.set(selected, mr);
			list_WorkMatchresults.setListData(workMatchresults.toArray());
			list_WorkMatchresults.setSelectedIndex(selected);
			listScrollPane_WorkMatchresults.repaint();
		}
	}

	/*
	 * update the current shown temporary Matchresult with the given name
	 */
	public void updateSchemaName(String _name) {
		// update Schema name
		if ((list_WorkSchemas != null)
				&& !list_WorkSchemas.isSelectionEmpty()) {
			int selected = list_WorkSchemas.getSelectedIndex();
			Graph graph = (Graph) list_WorkSchemas
					.getSelectedValue();
			Source source = graph.getSource();
			//					boolean updateSource=false;
			//					if (listModel_RepSchemas.contains(source)){
			//						updateSource=true;
			//					}
			source.setName(_name);
			workSchemas.set(selected, graph);
			list_WorkSchemas.setListData(workSchemas.toArray());
//			list_WorkSchemas.setSelectedIndex(selected);
			list_WorkSchemas.setSelectedValue(graph, true);
			Source sourceMatchresult = controller.getGUIMatchresult()
					.getSourceSource();
			Source targetMatchresult = controller.getGUIMatchresult()
					.getTargetSource();
			if (sourceMatchresult != null && sourceMatchresult.equals(source)) {
				controller.getMatchresultView().setSourceLabel(graph);
			}
			if (targetMatchresult != null && targetMatchresult.equals(source)) {
				controller.getMatchresultView().setTargetLabel(graph);
			}
			listScrollPane_WorkSchemas.repaint();			
		}
	}

	/*
	 * update the current shown temporary Matchresult with the given comment
	 */
	public void updateMatchresultComment(String _info) {
		if ((list_WorkMatchresults != null)
				&& !list_WorkMatchresults.isSelectionEmpty()) {
			int selected = list_WorkMatchresults.getSelectedIndex();
			MatchResult mr = (MatchResult) list_WorkMatchresults
					.getSelectedValue();
			mr.setMatchInfo(_info);
			workMatchresults.set(selected, mr);
		}
	}

	/*
	 * update the current shown temporary Schema with the given comment
	 */
	public void updateSchemaComment(String _info) {
		if ((list_WorkSchemas != null)
				&& !list_WorkSchemas.isSelectionEmpty()) {
			int selected = list_WorkSchemas.getSelectedIndex();
			Graph graph = (Graph) list_WorkSchemas
					.getSelectedValue();
			graph.getSource().setComment(_info);
			workSchemas.set(selected, graph);
		}
	}

	/*
	 * add the given MatchResult to the "Operation"-Tab and select (if true)
	 */
	public void addMatchResult(MatchResult _result, boolean _select) {
		String mrName = _result.getName();
		if (mrName == null) {
			countWorkMatchresults++;
			mrName = GUIConstants.MATCHRESULT + countWorkMatchresults;
			_result.setName(mrName);
		}
		if (workMatchresults.size() > 0) {
			boolean check = true;
			int add = 2;
			while (check) {
				check = false;
				for (int i = 0; i < workMatchresults.size(); i++) {
					MatchResult current = workMatchresults.get(i);
					String currentName = current.getName();
					if (currentName.equals(_result.getName())) {
						check = true;
						_result.setName(mrName + GUIConstants.BRACKET_LEFT
								+ add + GUIConstants.BRACKET_RIGHT);
						add += 1;
					}
				}
			}
		}
//		int index = 0;
		Sort.addSortedMatchresult(workMatchresults, _result);
//		workMatchresults.add(index, _result);
		list_WorkMatchresults.setListData(workMatchresults.toArray());
		if (_select) {
			list_WorkSchemas.clearSelection();
			list_WorkMatchresults.setSelectedValue(_result, true);
			properties_Workspace.showProperties(_result);
			controller.setNewMatchResult(_result, true);
//			listScrollPane_WorkMatchresults.scrollRectToVisible(new Rectangle(
//					0, 0));
		}
		addSchemaToWorkspace(_result.getSourceGraph(), false, false);
		addSchemaToWorkspace(_result.getTargetGraph(), false, false);
		listScrollPane_WorkMatchresults.repaint();
		listScrollPane_WorkSchemas.repaint();
	}

	public void addSchemaToWorkspace(Graph _graph, boolean _select,
			boolean _createUniqueName) {
		//		allTmpSchemas.add(_graph);
		String schemaName = _graph.getSource().getName();
		if (schemaName == null) {
			countWorkSchemas++;
			schemaName = GUIConstants.SCHEMA + countWorkSchemas;
			_graph.getSource().setName(schemaName);
		}
		if (_createUniqueName) {
			if (controller.getManager().existSourceWithName(schemaName)
					|| containsSchemaName(schemaName)) {
				int count = 1;
				String newName = schemaName + GUIConstants.UNDERSCORE + count;
				while (controller.getManager().existSourceWithName(newName) 
						|| containsSchemaName(newName)) {
					count++;
					newName = schemaName + GUIConstants.UNDERSCORE + count;
				}
				schemaName = newName;
				_graph.getSource().setName(newName);
			}
		} else {
			if (containsSchema(_graph)) {
				if (controller.getGUIMatchresult().containsSource() && 
						controller.getGUIMatchresult().containsTarget()){
					setSelectedTab(ManagementPane.WORKSPACE);
				}
				return;
			}
		}
//		int index = 0;
//		workSchemas.add(index, _graph);
		Sort.addSortedSchema(workSchemas, _graph);
		list_WorkSchemas.setListData(workSchemas.toArray());
		if (_select) {
			list_WorkMatchresults.clearSelection();
			list_WorkSchemas.setSelectedValue(_graph, true);
//			listScrollPane_WorkSchemas.scrollRectToVisible(new Rectangle(
//					0, 0));
			properties_Workspace.showProperties(_graph);
			updateSchemaName(_graph.getSource().getName());
			int index = workSchemas.indexOf(_graph);
			list_WorkSchemas.ensureIndexIsVisible(index);
		}
		if (controller.getGUIMatchresult().containsSource() && 
				controller.getGUIMatchresult().containsTarget()){
			setSelectedTab(ManagementPane.WORKSPACE);
		}
		listScrollPane_WorkSchemas.repaint();
	}

	boolean containsSchema(Graph _graph) {
		if ((workSchemas.size() == 0) || (_graph == null)) {
			return false;
		}
		for (int i = 0; i < workSchemas.size(); i++) {
			Graph graph = workSchemas.get(i);
			if (graph.getSource().equals(_graph.getSource())) {
				return true;
			}
		}
		return false;
	}

	boolean containsSchemaName(String _schemaName) {
		if ((workSchemas.size() == 0) || (_schemaName == null)) {
			return false;
		}
		for (int i = 0; i < workSchemas.size(); i++) {
			Graph graph = workSchemas.get(i);
			String currentName = graph.getSource().getName();
			if (_schemaName.equals(currentName)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsDomain(String _domain) {
		if ((repDomains.size() == 0) || (_domain == null)) {
			return false;
		}
		return repDomains.contains(_domain);
	}
	
	public boolean addDomain(String _domain) {
		if (_domain == null || _domain.length()==0 ||  repDomains.contains(_domain)) {
			return false;
		}
		repDomains.add(_domain);
		list_RepDomains.setListData(repDomains.toArray());
//		list_RepDomains.setSelectedValue(GUIConstants.SHOW_ALL_HTML, true);
		list_RepDomains.setSelectedValue(GUIConstants.SHOW_ALL_NORMAL, true);
		return true;
	}
	
	public boolean deleteDomain(String _domain) {
		if (_domain == null || _domain.length()==0 ||  !repDomains.contains(_domain)) {
			return false;
		}
		repDomains.remove(_domain);
		list_RepDomains.setListData(repDomains.toArray());
//		list_RepDomains.setSelectedValue(GUIConstants.SHOW_ALL_HTML, true);
		list_RepDomains.setSelectedValue(GUIConstants.SHOW_ALL_NORMAL, true);
		return true;
	}
	
	public void selectDomain(String domain) {
		if (domain.startsWith("<")){
			domain = domain.replaceFirst("<", "&lt;");
		}
		if (domain.endsWith(">")){
			domain = domain.substring(0, domain.length()-1) +  "&gt;";
		}
		list_RepDomains.setSelectedValue(domain, true);
		listScrollPane_RepDomain.repaint();
	}
	
	/*
	 * update all Matchresults depending on the given Preprocessing
	 */
	public void updateAllTabs() {
		if (workMatchresults.size() == 0) {
			return;
		}
		int selected = list_WorkMatchresults.getSelectedIndex();
		for (int i = 0; i < workMatchresults.size(); i++) {
			MatchResult mr = workMatchresults.get(i);
			// change preprocessing of MatchResult
//			mr = MatchResult.transformMatchResult(mr, controller
//					.getPreprocessing());
			//			allTmpMatchResults.set(i, mr.getResultName());
			workMatchresults.set(i, mr);
			// update number of correspondences in properties
			if (selected == i) {
				properties_Workspace.updateCount(mr);
				controller.setNewMatchResult(mr, true);
			}
		}
	}

	public void updateCurrentMatchresult() {
		if (workMatchresults.size() == 0) {
			return;
		}
		int selected = list_WorkMatchresults.getSelectedIndex();
		MatchResult mr=null;
		if (selected < 0) {
			mr = controller.getGUIMatchresult().getMatchResult();
//		} else {
//			// TODO needed or not?
//			mr = workMatchresults.get(selected);
//			// change preprocessing of MatchResult
////			mr = MatchResult.transformMatchResult(mr, controller
////					.getPreprocessing());
//			//			allTmpMatchResults.set(i, mr.getResultName());
//			workMatchresults.set(selected, mr);
//			// update number of correspondences in properties
//			properties_Workspace.updateCount(mr);
		}
		if (mr!=null){
			controller.setNewMatchResult(mr);
		}
	}

	class JListCellRenderer implements ListCellRenderer {
		
		/*
		 * return for a schema (SOURCE) the number of matchresults
		 * from the current repository it participates in
		 */
		int getSourceRels(Source _source){
			int count = 0;
			for (int i = 0; i < repMatchresults.size(); i++) {
				SourceRelationship sr = repMatchresults.get(i);
				if (sr.getSourceId()== _source.getId() ||
						sr.getTargetId()== _source.getId()){
					count++;
				}
			}
		return count;
		}

		/*
		 * return for a domain the number of schemas
		 * from the current repository that have this domain
		 */
		ArrayList getDomainSources(String _domain){
			ArrayList<Source> schemas = new ArrayList<Source>();			
//			if (GUIConstants.SHOW_ALL_HTML.equals(_domain)){
			if (GUIConstants.SHOW_ALL_NORMAL.equals(_domain)){
				return schemas;
//			} else if (_domain==null || GUIConstants.NO_DOMAIN_HTML.equals(_domain)){
			} else if (_domain==null || GUIConstants.NO_DOMAIN_NORMAL.equals(_domain)){
				for (int i = 0; i < repSchemas.size(); i++) {
					Source s = repSchemas.get(i);
					if (s.getDomain()==null){
						schemas.add(s);
					}
				}
				return schemas;
			}
			for (int i = 0; i < repSchemas.size(); i++) {
				Source s = repSchemas.get(i);
				if (_domain.equalsIgnoreCase(s.getDomain())){
					schemas.add(s);
				}
			}
			return schemas;
		}
		
		/*
		 * return for a schema (SOURCE) the number of matchresults
		 * from the current repository it participates in
		 */
		int getSourceRels(ArrayList _sources){
			int count = 0;
			for (int i = 0; i < repMatchresults.size(); i++) {
				SourceRelationship sr = repMatchresults.get(i);
				for (int j = 0; j < _sources.size(); j++) {
					Source s = (Source) _sources.get(j);
					if (sr.getSourceId()== s.getId() ||
							sr.getTargetId()== s.getId()){
						count++;
						break;
					}
				}
			}
			return count;
		}
		
		/*
		 * return for a schema (SOURCE) the number of matchresults
		 * from the current workspace it participates in
		 */
		int getMatchResults(Graph _graph){
			int count = 0;
			int id = _graph.getSource().getId();
			for (int i = 0; i < workMatchresults.size(); i++) {
				MatchResult mr =  workMatchresults.get(i);
				if (mr.getSourceGraph().getSource().getId()== id ||
						mr.getTargetGraph().getSource().getId()== id){
					count++;
				}
			}
		return count;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
		 *      java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList _list, Object _value,
				int _index, boolean _isSelected, boolean _cellHasFocus) {
			JLabel label = new JLabel();
			String text;
			if (_value instanceof Source) {
				// show the schema names and in brakes the number of 
				// matchresults they participate in
				int count = getSourceRels(((Source) _value));
//				if (count>0){
//					text = "<html><body>"+ ((Source) _value).getName() + "<font color=\"#888888\"> (" + count+  ")</font></body>";
//					String toolTip = "<html><body>"+ ((Source) _value).getName() + "<font color=\"#888888\"> (" + count+  " Matchresults)</font></body>";
//					label.setToolTipText(toolTip);
//				} else {
//					text = "<html><body>"+ ((Source) _value).getName() + "</body>";
//				}
				if (count>0){
					text =  ((Source) _value).getName() + " (" + count+  ")";
					String toolTip = ((Source) _value).getName() + " (" + count+  " Matchresults)";
					label.setToolTipText(toolTip);
				} else {
					text = ((Source) _value).getName();
				}
			} else if (_value instanceof SourceRelationship) {
				text = ((SourceRelationship) _value).getName();
			} else if (_value instanceof Graph) {
				// show the schema names and in brakes the number of 
				// matchresults they participate in
				int count = getMatchResults(((Graph) _value));
//				if (count>0){
//					text = "<html><body>"+ ((Graph) _value).getSource().getName() + "<font color=\"#888888\"> (" + count+  ")</font></body>";
//					String toolTip = "<html><body>"+ ((Graph) _value).getSource().getName() + "<font color=\"#888888\"> (" + count+  " Matchresults)</font></body>";
//					label.setToolTipText(toolTip);
//				} else {
//					text = "<html><body>"+ ((Graph) _value).getSource().getName() + "</body>";
//				}
				if (count>0){
					text = ((Graph) _value).getSource().getName() + " (" + count+  ")";
					String toolTip =((Graph) _value).getSource().getName() + " (" + count+  " Matchresults)";
					label.setToolTipText(toolTip);
				} else {
					text =((Graph) _value).getSource().getName();
				}
			} else if (_value instanceof MatchResult) {
				text = ((MatchResult) _value).getName();
			} else {
				// Domain
				if (_value==null){
//					text = GUIConstants.NO_DOMAIN_HTML;
					text = GUIConstants.NO_DOMAIN_NORMAL;
				} else {
					text = _value.toString();				
				}
				int countSchemas = 0;
				int countMatchresults = 0;
//				if (text.equals(GUIConstants.SHOW_ALL_HTML)){
				if (text.equals(GUIConstants.SHOW_ALL_NORMAL)){
					countSchemas = repSchemas.size();
					countMatchresults = repMatchresults.size();
				} else {
					ArrayList schemas = getDomainSources(text);
					countSchemas = schemas.size();
					countMatchresults = getSourceRels(schemas);
				}
//				String toolTip = "<html><body>"+ text + "<font color=\"#888888\"> (" + countSchemas+  " Schemas + " + countMatchresults + " Matchresults)</font></body>";
//				text = "<html><body>"+ text + "<font color=\"#888888\"> (" + countSchemas+  " + " + countMatchresults + ")</font></body>";
				String toolTip = text + " (" + countSchemas+  " Schemas + " + countMatchresults + " Matchresults)";
				text =  text + " (" + countSchemas+  " + " + countMatchresults + ")";

				label.setToolTipText(toolTip);
			}
			label.setText(text);
			label.setOpaque(true);
			label.setForeground(Color.BLACK);
			if (_isSelected) {
				label.setBackground(MainWindow.SELECTED_BACKGROUND);
				if (_cellHasFocus) {
					label.setBorder(BorderFactory.createLineBorder(
							MainWindow.SELECTED_BORDER, 2));
				} else {
					label.setBorder(BorderFactory.createLineBorder(
							MainWindow.SELECTED_BACKGROUND, 2));
				}
			} else {
				label.setBackground(MainWindow.GLOBAL_BACKGROUND);
				label.setBorder(BorderFactory.createLineBorder(
						MainWindow.GLOBAL_BACKGROUND, 2));
			}
			return label;
			//			return null;
		}
	}
	class SchemaMouseAdapter extends MouseAdapter {
		boolean sourceloaded = false;
		int kind;

		public SchemaMouseAdapter(int _kind) {
			super();
			kind = _kind;
		}

		public void mouseClicked(MouseEvent _event) {
			if (kind == REPOSITORY) {
				list_RepMatchresults.clearSelection();
				if (_event.getClickCount() == 2
						&& (repSchemas.size() > 0)) {
					Source s = (Source) list_RepSchemas
							.getSelectedValue();
					if (s==null){
						return;
					}
					if (sourceloaded) {
						controller.loadTargetSchema(s);
						sourceloaded = false;
					} else {
						if (controller.getGUIMatchresult()
								.getSourceSource() != null
								&& controller.getGUIMatchresult()
										.getTargetSource() == null) {
							controller.loadTargetSchema(s);
						} else {
							controller.loadSourceSchema(s);
							sourceloaded = true;
						}
					}
				}
			} else {
				list_WorkMatchresults.clearSelection();
				if (_event.getClickCount() == 2
						&& (workSchemas.size() > 0)) {
					Graph s = (Graph) list_WorkSchemas
							.getSelectedValue();
					if (sourceloaded) {
						controller.loadTargetSchema(s);
						sourceloaded = false;
					} else {
						if (controller.getGUIMatchresult()
								.getSourceSource() != null
								&& controller.getGUIMatchresult()
										.getTargetSource() == null) {
							controller.loadTargetSchema(s);
						} else {
							controller.loadSourceSchema(s);
							sourceloaded = true;
						}
					}
				}
			}
		}
	}
	class MatchresultMouseAdapter extends MouseAdapter {
		int kind;

		public MatchresultMouseAdapter(int _kind) {
			super();
			kind = _kind;
		}

		public void mouseClicked(MouseEvent _event) {
			if (kind == REPOSITORY) {
				list_RepSchemas.clearSelection();
				if ((_event.getClickCount() == 2)
						&& _event.getSource().equals(
								list_RepMatchresults)
						&& (repMatchresults.size() > 0)) {
					int index = list_RepMatchresults
							.locationToIndex(_event.getPoint());
					if (index < 0) {
						return;
					}
					SourceRelationship sr = (SourceRelationship)list_RepMatchresults.getSelectedValue();
					controller.loadMatchresultFromDB(sr);
				}
			} else {
				list_WorkSchemas.clearSelection();
				if ((_event.getClickCount() == 2)
						&& _event.getSource().equals(
								list_WorkMatchresults)
						&& (workMatchresults.size() > 0)) {
					MatchResult mr = (MatchResult) list_WorkMatchresults
							.getSelectedValue();
					controller.setNewMatchResult(mr, true);
				}
			}
		}
	}
	/*
	 * TableModel extends AbstractTableModel
	 */
	private class TableModel extends AbstractTableModel {
		private String[][] data;
		int tab, subTab;

		/*
		 * Constructor of TableModel
		 */
		public TableModel(String[][] _data, int _tab) {
			super();
			data = _data;
			tab = _tab;
		}

		public void setSubTab(int _subTab) {
			subTab = _subTab;
		}

		/*
		 * returns true in case a cell is editable
		 */
		public boolean isCellEditable(int _rowIndex, int _columnIndex) {
			if (tab != WORKSPACE) {
				return false;
			}
			if ((_columnIndex == 1) && ((_rowIndex == 0) || (_rowIndex == 1))) {
				return true;
			}
			return false;
		}

		/*
		 * for a given row and column set the given value, in case row=0 and
		 * column1 replace from the value +-/* with _
		 */
		public void setValueAt(Object _aValue, int _rowIndex, int _columnIndex) {
			String value = (String) _aValue;
			if (value != data[_rowIndex][_columnIndex]) {
				if ((_rowIndex == 0) && (_columnIndex == 1)) {
					char[] strChar = value.toCharArray();
					value = GUIConstants.EMPTY;
					for (int i = 0; i < strChar.length; i++) {
						if ((strChar[i] != '/') && (strChar[i] != '*')
								&& (strChar[i] != '-') && (strChar[i] != '+')) {
							value += strChar[i];
						}
					}
					if (subTab == SCHEMAS) {
						updateSchemaName(value);
					} else {
						updateMatchresultName(value);
					}
				} else {
					if (subTab == SCHEMAS) {
						updateSchemaComment(value);
					} else {
						updateMatchresultComment(value);
					}
				}
				data[_rowIndex][_columnIndex] = value;
			}
		}

		/*
		 * returns the number of rows that the given data has
		 */
		public int getRowCount() {
			return data.length;
		}

		/*
		 * returns the number of columns that the given data has
		 */
		public int getColumnCount() {
			return data[0].length;
		}

		/*
		 * return the value at a given row + column
		 */
		public Object getValueAt(int _row, int _column) {
			return data[_row][_column];
		}
	}
	/*
	 * ColoredTableCellRenderer implements TableCellRenderer
	 */
	private class ColoredTableCellRenderer implements TableCellRenderer {
		int tab, subTab;

		/*
		 * Constructor ColoredTableCellRenderer
		 */
		public ColoredTableCellRenderer(int _tab) {
			super();
			tab = _tab;
		}

		public void setSubTab(int _subTab) {
			subTab = _subTab;
		}

		/*
		 * returns the component as it shall be shown for a given value of the
		 * table depending of its place (row, column) and its state (selected,
		 * focused)
		 */
		public Component getTableCellRendererComponent(JTable _table,
				Object _value, boolean _isSelected, boolean _hasFocus,
				int _row, int _column) {
			//Label erzeugen
			JLabel label = new JLabel((String) _value);
			label.setOpaque(true);
			Border b = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			label.setBorder(b);
			label.setFont(_table.getFont());
			label.setForeground(_table.getForeground());
			label.setBackground(MainWindow.LIGHTGRAY);
			if (_hasFocus) {
				label.setBorder(BorderFactory.createLineBorder(
						MainWindow.BORDER, 1));
			}
			_column = _table.convertColumnIndexToModel(_column);
			if (_column == 0) {
				label.setFont(MainWindow.FONT12_BOLD);
			} else {
				label.setFont(MainWindow.FONT11);
				label.setToolTipText(label.getText());
				if (tab == WORKSPACE) {
					if ((_row == 0) || (_row == 1)) {
						label.setBackground(MainWindow.GLOBAL_BACKGROUND);
					}
				}
			}
			return label;
		}
	}
	
	/*
	 * creates the toolbar return the toolbar with buttons for: load
	 * source/target schema, edit properties, run, stop
	 */
	private JToolBar createToolBar() {
		ButtonGroup b = new ButtonGroup();
		JToolBar toolBar = new JToolBar();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		toolBar.setOrientation(SwingConstants.HORIZONTAL);
		toolBar.setFloatable(false);
		JButton button = null;
		// Existing Matcher
//		JButton button = new JButton(new ImageIcon(GUIConstants.ICON_EXISTING_MATCHER));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_EXISTING_MATCHER));
		button.setToolTipText(GUIConstants.EX_WORKFLOWVARIABLES);
		button.setMargin(BUTTON_INSETS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				controller.showWorkflowVariables();
			}
		});
		toolBar.add(button);
		b.add(button);
		runButtonList.add(button);
//		// Configure Strategy
////		button = new JButton(new ImageIcon(GUIConstants.ICON_CONFIGURE));
//		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_CONFIGURE));
//		button.setToolTipText(GUIConstants.CONFIGURE_STRATEGY);
//		button.setMargin(BUTTON_INSETS);
//		button.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.configureStrategy();
//			}
//		});
//		toolBar.add(button);
//		b.add(button);
//		runButtonList.add(button);
//		// Separator
////		toolBar.addSeparator();
//		toolBar.addSeparator(new Dimension(5,5));
		// Execute Matching
		JMenuBar menuBar = new JMenuBar();
		worflowMenu = new JMenu();
		worflowMenu.setLayout(new BorderLayout());
//		JLabel arrow = new JLabel(new ImageIcon(GUIConstants.ICON_ARROW));
		JLabel arrow = new JLabel(Controller.getImageIcon(GUIConstants.ICON_ARROW));
		//		arrow.setBorder(BorderFactory.createEtchedBorder());
		worflowMenu.add(arrow, BorderLayout.WEST);
		//		menu.setIcon(new ImageIcon(GUIConstants.ICON_ARROW));
		worflowMenu.setBorder(BorderFactory.createEtchedBorder());
		//		menu.setMargin(new Insets(-5,-5,-5,-5));
		//		menu.setBackground(Color.YELLOW);
		//		menu.setBorder(BorderFactory.createLineBorder(Color.RED));
		worflowMenu.setFont(MainWindow.FONT6);
		worflowMenu.setPreferredSize(new Dimension(12, 10));
		worflowMenu.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
			// take current workflows as possible executable 
			HashMap<String, String> workflows = controller.getAccessor().getWorkflowVariablesWithType("W");
			worflowMenu.removeAll();			
			if (workflows!=null){				
				ArrayList<String> workflowsKeys = new ArrayList<String>(workflows.keySet());		
				Collections.sort(workflowsKeys);
				for (Iterator iterator = workflowsKeys.iterator(); iterator.hasNext();) {
					String workflow = (String) iterator.next();
					JMenuItem workflowButton = new JMenuItem(workflow);
					workflowButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent _event) {
							Object source = _event.getSource();
							if (source instanceof JMenuItem)
							controller.executeMatching(((JMenuItem)source).getText());
						}
					});
					worflowMenu.add(workflowButton);
				}
			}
		}
		});
		

//		allContext = new JRadioButtonMenuItem(Strings.STRAT_CONTEXT,
//				false);
//		allContext.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
////				//set strategy here and execute
////				String strategy = controller.getStrategy();
////				if (!strategy.equals(GUIConstants.STRAT_ALLCONTEXT) && !strategy.equals(GUIConstants.STRAT_FILTEREDCONTEXT)){
////					String lastContextStrategy = controller.getLastContextStrategy();
////					controller.setStrategy(lastContextStrategy);					
////				}
////				System.out.println("set strategy and going to execute "
////						+ controller.getStrategy());				
////				setButtonRunTooltip(
////								": "
////										+ ((JMenuItem) _event.getSource())
////												.getText());
////				controller.executeMatching();
//			}
//		});
//		nodes = new JRadioButtonMenuItem(Strings.STRAT_NODES,
//				false);
//		nodes.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
////				//set strategy here and execute
////				String strategy = controller.getStrategy();
////				if (!strategy.equals(GUIConstants.STRAT_NODES)){
////					controller.setStrategy(((JMenuItem) _event
////							.getSource()).getText());					
////				}
////				System.out.println("set strategy and going to execute "
////						+ controller.getStrategy());				
////				setButtonRunTooltip(
////								": "
////										+ ((JMenuItem) _event.getSource())
////												.getText());
////				controller.executeMatching();
//			}
//		});
////		filteredContext = new JRadioButtonMenuItem(
////				GUIConstants.STRAT_FILTEREDCONTEXT, false);
////		filteredContext.addActionListener(new ActionListener() {
////			public void actionPerformed(ActionEvent _event) {
////				//set strategy here and execute
////				controller.setStrategy(((JMenuItem) _event
////						.getSource()).getText());
////				System.out.println("set strategy and going to execute "
////						+ ((JMenuItem) _event.getSource()).getText());
////				controller.getManagementPane()
////						.setButtonRunTooltip(
////								": "
////										+ ((JMenuItem) _event.getSource())
////												.getText());
////				controller.executeMatching();
////			}
////		});
//		fragment = new JRadioButtonMenuItem(Strings.STRAT_FRAGMENT, false);
//		fragment.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
////				//set strategy here and execute
////				controller.setStrategy(((JMenuItem) _event
////						.getSource()).getText());
////				System.out.println("set strategy and going to execute "
////						+ ((JMenuItem) _event.getSource()).getText());
////				controller.getManagementPane()
////						.setButtonRunTooltip(
////								": "
////										+ ((JMenuItem) _event.getSource())
////												.getText());
////				controller.executeMatching();
//			}
//		});
////		// not used: taxonomy
////		taxonomy = new JRadioButtonMenuItem(GUIConstants.STRAT_TAXONOMY, false);
////		if (!Controller.SIMPLE_GUI) {
////			taxonomy.addActionListener(new ActionListener() {
////				public void actionPerformed(ActionEvent _event) {
////					System.out
////							.println("set path strategy w/ taxonomy config; going to execute "
////									+ ((JMenuItem) _event.getSource())
////											.getText());
////					controller
////							.setStrategy(((JMenuItem) _event.getSource())
////									.getText());
////					controller
////							.executeTaxonomyMatching(((JMenuItem) _event
////									.getSource()).getText());
////				}
////			});
////		}
//		reuse = new JRadioButtonMenuItem(Strings.STRAT_REUSE, false);
//		reuse.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
////				controller.setStrategy(((JMenuItem) _event
////						.getSource()).getText());
////				controller.executeReuseMatching(false);
//			}
//		});
//		combinedReuse = new JRadioButtonMenuItem(
//				GUIConstants.STRAT_COMBINEDREUSE, false);
//		if (!Controller.SIMPLE_GUI && !Controller.WORKING_GUI) {
//			combinedReuse.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent _event) {
////					controller
////							.setStrategy(((JMenuItem) _event.getSource())
////									.getText());
////					controller.executeReuseMatching(true);
//				}
//			});
//		}
//		String strat = controller.getStrategy();
////		setStrategy(strat);
//		// AllContext
//		menu.add(allContext);
//		bg.add(allContext);
//		// Nodes
//		menu.add(nodes);
//		bg.add(nodes);
////		// FilteredContext
////		menu.add(filteredContext);
////		bg.add(filteredContext);
//		// Fragment
//		menu.add(fragment);
//		bg.add(fragment);
////		// not used: taxonomy
////		if (!Controller.SIMPLE_GUI) {
////			// Taxonomy
////			menu.add(taxonomy);
////			bg.add(taxonomy);
////		}
//		// Reuse
//		menu.add(reuse);
//		bg.add(reuse);
//		if (!Controller.SIMPLE_GUI && !Controller.WORKING_GUI) {
//			// CombinedReuse
//			menu.add(combinedReuse);
//			bg.add(combinedReuse);
//		}
		menuBar.add(worflowMenu);
		//		button = new JButton(new ImageIcon(GUIConstants.ICON_EXECUTE));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_EXECUTE));
		button = new JButton();
		button.setLayout(new BorderLayout());
		button.setToolTipText(GUIConstants.EXECUTE_MATCHING);
		button.setMargin(BUTTON_INSETS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
//				executeMatching();
			}
		});
//		JLabel label = new JLabel(new ImageIcon(GUIConstants.ICON_EXECUTE));
		JLabel label = new JLabel(Controller.getImageIcon(GUIConstants.ICON_EXECUTE));
		button.add(label, BorderLayout.WEST);
		button.add(menuBar, BorderLayout.EAST);
		toolBar.add(button);
		b.add(button);
		runButtonList.add(button);
		saveSchemaButtonList.add(button);
		// Stop Matching
//		button = new JButton(new ImageIcon(GUIConstants.ICON_STOP));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_STOP));
		button.setToolTipText(GUIConstants.STOP_MATCHING);
		button.setMargin(BUTTON_INSETS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				controller.stopMatching();
			}
		});
		button.setEnabled(false);
		button.setName(GUIConstants.STOP);
		b.add(button);
		toolBar.add(button);
		runButtonList.add(button);
		saveSchemaButtonList.add(button);
		// Step By Step Execute Fragment Matching
//		button = new JButton(new ImageIcon(GUIConstants.ICON_STEPBYSTEP));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_STEPBYSTEP));
		button.setToolTipText(GUIConstants.STEPBYSTEP_FRAGMENTMATCHING);
		button.setMargin(BUTTON_INSETS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				controller.stepByStepFragmentMatching();
			}
		});
		toolBar.add(button);
		b.add(button);
		runButtonList.add(button);
		saveSchemaButtonList.add(button);
		// Reuse a Matchresult
//		button = new JButton(new ImageIcon(GUIConstants.ICON_REUSE));
		button = new JButton(Controller.getImageIcon(GUIConstants.ICON_REUSE));
		button.setToolTipText(GUIConstants.REUSE_INFO);
		button.setMargin(BUTTON_INSETS);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				controller.reuseMatchresult();
			}
		});
		b.add(button);
		toolBar.add(button);
		runButtonList.add(button);
		saveSchemaButtonList.add(button);
//		// Separator
//		toolBar.addSeparator(new Dimension(5,5));
//		// Change View from 2-Splitpane to 3-Splitpane (or back)
//		splitpane = new JToggleButton(new ImageIcon(
//				GUIConstants.ICON_SPLITPANE_TO_3));
		splitpane = new JToggleButton(Controller.getImageIcon(GUIConstants.ICON_SPLITPANE_TO_3));
		splitpane.setToolTipText(GUIConstants.SPLITPANE_SWAP);
		splitpane.setMargin(BUTTON_INSETS);
		splitpane.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				boolean state = ((JToggleButton) _event.getSource())
						.isSelected();
				if (state) {
//					ManagementPane.splitpane.setIcon(new ImageIcon(
//							GUIConstants.ICON_SPLITPANE_TO_2));
					splitpane.setIcon(Controller.getImageIcon(
							GUIConstants.ICON_SPLITPANE_TO_2));
					controller.getMainWindow()
							.getNewContentPane().changeTo3SplitPane();
				} else {
//					ManagementPane.splitpane.setIcon(new ImageIcon(
//							GUIConstants.ICON_SPLITPANE_TO_3));
					splitpane.setIcon(Controller.getImageIcon(
							GUIConstants.ICON_SPLITPANE_TO_3));
					controller.getMainWindow()
							.getNewContentPane().changeTo2SplitPane();
				}
				splitpane.repaint();
			}
		});
		splitpane.setEnabled(false);
		toolBar.add(splitpane);
//		// Clean the Splitpane
//		button = new JButton(controller.getImageIcon(GUIConstants.ICON_CLEAN));
//		button.setToolTipText(GUIConstants.CLEAN_INFO);
//		button.setMargin(BUTTON_INSETS);
//		button.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				controller.cleanMatchresultLines();
//			}
//		});
//		b.add(button);
//		toolBar.add(button);
		return toolBar;
	}

//	public void setStrategy(String _strat) {
//		if (_strat.equals(GUIConstants.STRAT_ALLCONTEXT)) {
//			allContext.setSelected(true);
//		} else if (_strat.equals(GUIConstants.STRAT_FILTEREDCONTEXT)) {
//			allContext.setSelected(true);
//		} else if (_strat.equals(GUIConstants.STRAT_NODES)) {
//			nodes.setSelected(true);
//		} else if (_strat.equals(GUIConstants.STRAT_FRAGMENT)) {
//			fragment.setSelected(true);
//			// not used: taxonomy
////		} else if (_strat.equals(GUIConstants.STRAT_TAXONOMY)) {
////			taxonomy.setSelected(true);
//		} else if (_strat.equals(GUIConstants.STRAT_REUSE)) {
//			reuse.setSelected(true);
//		} else {
//			allContext.setSelected(true);
//		}
//	}
	
//	public void executeMatching(){
//		if (reuse.isSelected()) {
//			controller.executeReuseMatching(false);
//		} else if (combinedReuse.isSelected()) {
//			controller.executeReuseMatching(true);
//			// not used: taxonomy
////		} else if (taxonomy.isSelected()) {
////			controller
////					.executeTaxonomyMatching(taxonomy.getText());
//		} else {
//			controller.executeMatching();
//		}
//	}
	
	public boolean domainContainSchemas(String _domain){
		if (_domain==null){
			return false;
		}
		ArrayList<Source> currentSources = new ArrayList<Source>();
		for (int i=0; i<repSchemas.size(); i++){
			String domain =repSchemas.get(i).getDomain();
			if (domain!=null && domain.equals(_domain)){
				currentSources.add(repSchemas.get(i));
			}
		}
		if (currentSources.isEmpty()){
			return false;
		}
		return true;
	}
	
	void showDomain(String _domain){
//		if (_domain==null || _domain.equals(GUIConstants.NO_DOMAIN_HTML)){
		if (_domain==null || _domain.equals(GUIConstants.NO_DOMAIN_NORMAL)){
			ArrayList<Source> currentSources = new ArrayList<Source>();
			for (int i=0; i<repSchemas.size(); i++){
				String domain =repSchemas.get(i).getDomain();
				if (domain==null){
					currentSources.add(repSchemas.get(i));
				}
			}
			list_RepSchemas.setListData(currentSources.toArray());
			
			ArrayList<SourceRelationship> currentMatchresults = new ArrayList<SourceRelationship>();
			for (int i=0; i<repMatchresults.size(); i++){
				SourceRelationship sr =repMatchresults.get(i);
				for (int j=0; j<currentSources.size(); j++){
					int sourceId = currentSources.get(j).getId();
					if (sr.getSourceId()==sourceId || sr.getTargetId()==sourceId){
						currentMatchresults.add(sr);
						break;
					}
				}
			}
			list_RepMatchresults.setListData(currentMatchresults.toArray());
//		} else if (_domain.equals(GUIConstants.SHOW_ALL_HTML)){
		} else if (_domain.equals(GUIConstants.SHOW_ALL_NORMAL)){	
			list_RepSchemas.setListData(repSchemas.toArray());
			list_RepMatchresults.setListData(repMatchresults.toArray());
		}else {	
			ArrayList<Source> currentSources = new ArrayList<Source>();
			for (int i=0; i<repSchemas.size(); i++){
				String domain =repSchemas.get(i).getDomain();
				if (_domain.equals(domain)){
					currentSources.add(repSchemas.get(i));
				}
			}
			list_RepSchemas.setListData(currentSources.toArray());
		
			ArrayList<SourceRelationship> currentMatchresults = new ArrayList<SourceRelationship>();
			for (int i=0; i<repMatchresults.size(); i++){
				SourceRelationship sr =repMatchresults.get(i);
				for (int j=0; j<currentSources.size(); j++){
					int sourceId = currentSources.get(j).getId();
					if (sr.getSourceId()==sourceId || sr.getTargetId()==sourceId){
						currentMatchresults.add(sr);
						break;
					}
				}
			}
			list_RepMatchresults.setListData(currentMatchresults.toArray());
		}
		list_RepSchemas.clearSelection();
		list_RepMatchresults.clearSelection();
	}

//	/*
//	 * set the state of the edit button to the given one
//	 */
//	public boolean getEditState() {
//		return edit.isSelected();
//	}
//
//	/*
//	 * set the state of the edit button to the given one
//	 */
//	public void setEditState(boolean _state) {
//		edit.setSelected(_state);
//	}
//
//	/*
//	 * set the state of the edit button to the given one
//	 */
//	public void setEditEnabled(boolean _enable) {
//		edit.setEnabled(_enable);
//	}

	/*
	 * while saving a schema to the database disable most of the menu itemes and
	 * buttons when finished saving enable them
	 */
	public void setMenuStateDB(boolean _state) {
		if (saveSchemaButtonList.size() > 0) {
			for (int i = 0; i < saveSchemaButtonList.size(); i++) {
				if (saveSchemaButtonList.get(i) instanceof JButton) {
					JButton current = ((JButton) saveSchemaButtonList
							.get(i));
					String text = current.getName();
					if (GUIConstants.STOP.equals(text)) {
						current.setEnabled(false);
//					} else {
//						current.setEnabled(!_state);
					}
//				} else if (saveSchemaButtonList.get(i) instanceof JToggleButton) {
//					JToggleButton current = ((JToggleButton) saveSchemaButtonList
//							.get(i));
//					current.setEnabled(!_state);
				}
			}
		}
		setStateDB(_state);
		controller.getMainWindow().setMenuStateSaveSchema(_state);
	}

	/*
	 * while running matching disable most of the menu itemes and buttons when
	 * finished matching enable them
	 */
	public void setMenuStateRun(boolean _state) {
		if (runButtonList.size() > 0) {
			for (int i = 0; i < runButtonList.size(); i++) {
				if (runButtonList.get(i) instanceof JButton) {
					JButton current = ((JButton) runButtonList.get(i));
					String text = current.getName();
					if (GUIConstants.STOP.equals(text)) {
						current.setEnabled(_state);
//					} else {
//						current.setEnabled(!_state);
					}
//				} else if (runButtonList.get(i) instanceof JToggleButton) {
//					JToggleButton current = ((JToggleButton) runButtonList
//							.get(i));
//					current.setEnabled(!_state);
				}
			}
		}
		setStateRun(_state);
		controller.getMainWindow().setMenuStateRun(_state);
	}

	public void setSplitPaneButtonClick() {
		splitpane.setEnabled(true);
		splitpane.setSelected(true);
		splitpane.setIcon(Controller.getImageIcon(
				GUIConstants.ICON_SPLITPANE_TO_2));
	}
	
	public void setSplitPaneButtonEnabled(boolean _enable) {
		if (splitpane.isEnabled() == _enable) {
			return;
		}
		splitpane.setEnabled(_enable);
		if (!_enable) {
//			splitpane.setIcon(new ImageIcon(
//					GUIConstants.ICON_SPLITPANE_TO_3));
			splitpane.setIcon(Controller.getImageIcon(
					GUIConstants.ICON_SPLITPANE_TO_3));
			splitpane.setSelected(_enable);
			splitpane.repaint();
			controller.getMainWindow().getNewContentPane()
					.changeTo2SplitPane();
		}
		splitpane.setEnabled(_enable);
	}
	
	Point positionPopUp = null;
	
	private void initPopUp(){
		// ******************************
	    //Create the domain popup menu.
	    popupDomain = new JPopupMenu();
	    // Create Domain
	    JMenuItem menuItem = new JMenuItem(GUIConstants.CREATE_DOMAIN);
	    menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				Dlg_Domain dlg = new Dlg_Domain(controller.getMainWindow(),GUIConstants.CREATE_DOMAIN,controller);
				if (positionPopUp==null){
					dlg.showDlg(controller.getDialogPosition());
				} else {
					dlg.showDlg(positionPopUp);
				}
			}
		});
	    popupDomain.add(menuItem);
	    
	    // Delete Domain
	    menuItem = new JMenuItem(GUIConstants.DEL_DOMAIN);
	    menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				Dlg_Domain dlg = new Dlg_Domain(controller.getMainWindow(),GUIConstants.DEL_DOMAIN,controller);
				if (positionPopUp==null){
					dlg.showDlg(controller.getDialogPosition());
				} else {
					dlg.showDlg(positionPopUp);
				}
			}
		});
	    popupDomain.add(menuItem);

	    //Add listener to components that can bring up popup menus.
//	    listScrollPane_RepDomain.addMouseListener(new PopupListener(true));
	    list_RepDomains.addMouseListener(new PopupListener(true));
	    
		// ******************************
	    //Create the domain popup menu.
	    popupSchema = new JPopupMenu();
	    // Create Domain
	    menuItem = new JMenuItem(GUIConstants.CHANGE_DOMAIN);
	    menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				Dlg_Domain dlg = new Dlg_Domain(controller.getMainWindow(),GUIConstants.CHANGE_DOMAIN,controller);
				if (positionPopUp==null){
					dlg.showDlg(controller.getDialogPosition());
				} else {
					dlg.showDlg(positionPopUp);
				}
			}
		});
	    popupSchema.add(menuItem);
	    
	    
	    //Add listener to components that can bring up popup menus.
//	    listScrollPane_RepSchemas.addMouseListener(new PopupListener(false));
	    list_RepSchemas.addMouseListener(new PopupListener(false));
	}
	
	/*
	 * remove the current selected temporary Matchresult
	 */
	public void removeAllWorkSchemas() {
		if (list_WorkSchemas != null) {
			Object[] graphs = workSchemas.toArray();
			for (int i = 0; i < graphs.length; i++) {
				Graph graph = (Graph) graphs[i];
				workSchemas.remove(graph);
				removeWorkMatchresult(graph);
				if (graph.getSource().equals(
						controller.getGUIMatchresult().getSourceSource())) {
					controller.closeSchema(true, false);
				}
				if (graph.getSource().equals(
						controller.getGUIMatchresult().getTargetSource())) {
					controller.closeSchema(false, false);
				}
			}
			list_WorkSchemas.setListData(workSchemas.toArray());
			properties_Workspace.showNothing();
			listScrollPane_WorkSchemas.repaint();
			listScrollPane_WorkMatchresults.repaint();
		}
	}

	class PopupListener extends MouseAdapter {
		boolean domain;
		public PopupListener(boolean domain){
			super();
			this.domain = domain;
		}
		
	    public void mousePressed(MouseEvent _event) {
	        maybeShowPopup(_event);
	    }
	
	    public void mouseReleased(MouseEvent _event) {
	        maybeShowPopup(_event);
	    }
	
	    private void maybeShowPopup(MouseEvent _event) {
	        if (_event.isPopupTrigger()) {
	        	if (domain){
//	        		System.out.println(_event.getX() + "  " + _event.getY());
	        		positionPopUp = _event.getLocationOnScreen();
		            popupDomain.show(_event.getComponent(),
		                       _event.getX(), _event.getY());
	        	} else {
	        		positionPopUp = _event.getLocationOnScreen();
		            popupSchema.show(_event.getComponent(),
		                       _event.getX(), _event.getY());
	        	}
	        }
	    }
	}
	
}