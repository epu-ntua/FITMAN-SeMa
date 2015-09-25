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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.SaveToDBThread;
import de.wdilab.coma.insert.metadata.OWLParser_V3;

/**
 * This dialog offers the change to import an ontology (or more specific owl file).
 * 
 * @author David Aumueller, Sabine Massmann
 */
public class Dlg_ImportOWL extends JDialog {
	Controller controller = null;
	JPopupMenu popupMenu = null; // for paste
	private JButton jButton1;
	private JButton importButton;
	private JCheckBox filterCheckbox;
	private JCheckBox labelsPreferredCheckbox;
	private JCheckBox stressExternalURIsCheckbox;
	private JCheckBox conceptHierarchyOnlyCheckbox;
	private JPanel pane;
	JTextField filterURL;
	private JTextField importURL;
	JComboBox importURLs;

	public Dlg_ImportOWL() {
		initComponents();
	}

	public Dlg_ImportOWL(JFrame _parent, final Controller _controller) {
		super(_parent);
		initComponents();
		controller = _controller;
		// from Controller.importOWLInDB()
		_controller.setStatus(GUIConstants.SAVE_SCHEMA_DB);
		SaveToDBThread saveSchema = new SaveToDBThread(_controller
				.getMainWindow(), _controller,
				SaveToDBThread.STATE_IMPORT_OWL_URI);
		saveSchema.start();
		// updateAll(controller);
	}

	private void initComponents() {
		GridBagConstraints c = new GridBagConstraints();
		pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		pane.setBorder(new EmptyBorder(
				new Insets(11, 11, 12, 12)));
		//c.fill = c.VERTICAL;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 2;
		c.weighty = 2;
		importURL = new JTextField();
		filterCheckbox = new JCheckBox();
		filterCheckbox.setSelected(false);
		labelsPreferredCheckbox = new JCheckBox();
		labelsPreferredCheckbox.setSelected(true);
		conceptHierarchyOnlyCheckbox = new JCheckBox();
		conceptHierarchyOnlyCheckbox.setSelected(false);
		stressExternalURIsCheckbox = new JCheckBox();
//		stressExternalURIsCheckbox.setSelected(true);
		filterURL = new JTextField();
		//filterURL.setEnabled(false);
		jButton1 = new JButton();
		importButton = new JButton();
		setTitle("Import OWL Ontology");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent _event) {
				exitForm(_event);
			}
		});
		//JList jlist = new JList();
		String urls = System.getProperty("owl_ontology_urls") + "";
		String oneUrl[] = urls.split(",");
		importURLs = new JComboBox();
		importURLs.setEditable(true);
		for (int i = 0; i < oneUrl.length; i++) {
			importURLs.addItem(oneUrl[i].trim());
			//jlist.add(oneUrl[i].trim());
		}
		c.gridx = 1;
		c.gridy = 0;
		//importURL.setText("http://www.purl.org/net/ontology/beer");
		//importURL.setMinimumSize(new Dimension(100, 5));
		pane.add(importURLs, c); //instead of importURL
		//addPastePossib(importURL);
		//addPastePossib(filterURL); //since final field in inner class: not
		// possible to use multiple times...
		// http://java.sun.com/developer/JDCTechTips/2001/tt1120.html
		importURL.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent _event) {
				JTextField textField = (JTextField) _event.getSource();
				String content = textField.getText();
				filterURL.setText(content);
			}
		});
		/*
		 * wont work: importURLs.addFocusListener(new
		 * FocusAdapter() { public void
		 * focusLost(java.awt.event.FocusEvent e) { System.out.println(e);
		 * JComboBox box = (JComboBox)e.getSource(); System.out.println(box);
		 * String content = (String)box.getSelectedItem();
		 * System.out.println(content); filterURL.setText(content); } });
		 */
		filterURL.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent _event) {
				String content = (String) importURLs
						.getSelectedItem();
				filterURL.setText(content);
			}
		});
		filterCheckbox.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent _event) {
				String content = (String) importURLs
						.getSelectedItem();
				filterURL.setText(content);
				/*
				 * if (filterCheckbox.isSelected()==true) {
				 * filterURL.setEnabled(true);
				 * stressExternalURIsCheckbox.setEnabled(false); } else {
				 * filterURL.setEnabled(false);
				 * stressExternalURIsCheckbox.setEnabled(true); }
				 */
			}
		});
		c.gridx = 0;
		c.gridy = 0;
		pane.add(new JLabel("URI: "), c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(new JLabel("Filter: "), c);
		c.gridx = 2;
		c.gridy = 1;
		filterCheckbox.setText("enable");
		filterCheckbox
				.setToolTipText("Filter: Include only URIs starting with entered filter URI or keep empty/cancel for all");
		filterCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
		pane.add(filterCheckbox, c);
		c.gridx = 1;
		c.gridy = 1;
		//filterURL.setText(importURL.getText());
		pane.add(filterURL, c);
		c.gridx = 1;
		c.gridy = 2;
		conceptHierarchyOnlyCheckbox
				.setText("read in only taxonomy backbone (concept hierarchy)");
		conceptHierarchyOnlyCheckbox
				.setAlignmentX(Component.LEFT_ALIGNMENT);
		conceptHierarchyOnlyCheckbox
				.addActionListener(new ActionListener() {
					public void actionPerformed(
							ActionEvent _event) {
						conceptHierarchyOnlyCheckboxActionPerformed(_event);
					}
				});
		pane.add(conceptHierarchyOnlyCheckbox, c);
		c.gridx = 1;
		c.gridy = 3;
		labelsPreferredCheckbox.setText("try to use rdfs:label as name");
		labelsPreferredCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelsPreferredCheckbox
				.addActionListener(new ActionListener() {
					public void actionPerformed(
							ActionEvent _event) {
						jCheckBox2ActionPerformed(_event);
					}
				});
		pane.add(labelsPreferredCheckbox, c);
		c.gridx = 1;
		c.gridy = 4;
		stressExternalURIsCheckbox.setText("mark external URI in name");
		stressExternalURIsCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
		stressExternalURIsCheckbox
				.addActionListener(new ActionListener() {
					public void actionPerformed(
							ActionEvent _event) {
						jCheckBox3ActionPerformed(_event);
					}
				});
		pane.add(stressExternalURIsCheckbox, c);
		c.gridx = 1;
		c.gridy = 9;
		importButton.setText("Import");
		importButton
				.addActionListener(new ActionListener() {
					public void actionPerformed(
							ActionEvent _event) {
						importButtonActionPerformed(_event);
					}
				});
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		pane.add(importButton, c);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 2;
		c.gridy = 9;
		pane.add(jButton1, c);
		jButton1.setText("Cancel");
		jButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				jButton1ActionPerformed(_event);
			}
		});
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		//jCheckBox3.setText("jCheckBox3");
		//pane.add(jCheckBox3);
		getContentPane().add(pane, BorderLayout.CENTER);
		//		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
		//				.getScreenSize();
		//setBounds((screenSize.width-400)/2, (screenSize.height-300)/2, 400,
		// 300);
		pack();
	}

	void jCheckBox2ActionPerformed(ActionEvent _event) {
	}

	void jCheckBox3ActionPerformed(ActionEvent _event) {
	}

	void conceptHierarchyOnlyCheckboxActionPerformed(ActionEvent _event) {
	}

	void importButtonActionPerformed(ActionEvent _event) {
		setVisible(false);
		//String owluri = importURL.getText();
		String owluri = (String) importURLs.getSelectedItem();
		String owluriFilter = filterURL.getText();
		boolean useFilter = filterCheckbox.isSelected();
		boolean labelsPreferred = labelsPreferredCheckbox.isSelected();
		boolean stressExternalURIs = stressExternalURIsCheckbox
				.isSelected();
		boolean conceptHierarchyOnly = conceptHierarchyOnlyCheckbox
				.isSelected();
		if (owluri != null) {
			System.out.println(owluri + " " + owluriFilter + " " + useFilter
					+ labelsPreferred + stressExternalURIs);
//			parse.OWLParser.parseWrapper(owluri, owluriFilter, useFilter,
//					labelsPreferred, stressExternalURIs, conceptHierarchyOnly);
//			OWLParser.parseOWLOnto(owluri, owluriFilter, useFilter,
//					labelsPreferred, stressExternalURIs, conceptHierarchyOnly,null,null,null,null,null);
			OWLParser_V3 parser = new OWLParser_V3(true);
			parser.parseSingleSource(owluri);
//			parser.parse(owluri, null, useFilter, owluriFilter, labelsPreferred, stressExternalURIs, conceptHierarchyOnly,
//					null,null,null,null);
			//updateAll(controller);
			//controller.updateAllOld(true, true);
            controller.updateAll(true); //parse and import
			//} else controller.setStatus(GUIConstants.NOT_LOADED_NO_FILTER);
		} else {
			controller.setStatus(GUIConstants.NOT_LOADED_NO_URI);
		}
	}

	void jButton1ActionPerformed(ActionEvent _event) {
		setVisible(false);
		controller.setStatus(GUIConstants.IMPORT_SCHEMAS_ABORTED);
	}

	/** Exit the Application */
	void exitForm(WindowEvent _event) {
		setVisible(false);
	}

	/**
	 *  args are the command line arguments
	 */
	public static void main(String args[]) {
		new Dlg_ImportOWL().setVisible(true);
	}
	//	/* paste button */
	//	private void addPastePossib(final JTextField field) {
	//		field.addMouseListener(new MouseAdapter() {
	//			public void mousePressed(MouseEvent _event) {
	//				//System.out.println("PASTE");
	//				// if right mouse click
	//				if (arg0.getButton() == MouseEvent.BUTTON3
	//						&& Dlg_ImportOWL.popupMenu != null) {
	//					Dlg_ImportOWL.popupMenu.show((Component) arg0
	//							.getSource(), arg0.getX(), arg0.getY());
	//				}
	//			}
	//		});
	//		field.selectAll();
	//		//pane.add(field);
	//		popupMenu = new JPopupMenu();
	//		JMenuItem item = new JMenuItem("Paste");
	//		item.addActionListener(new ActionListener() {
	//			public void actionPerformed(ActionEvent _event) {
	//				Toolkit toolkit = Dlg_ImportOWL.controller.getMainWindow()
	//						.getToolkit();
	//				Clipboard clip = toolkit.getSystemClipboard();
	//				Vector text = new Vector();
	//				// Get the clipboard contents
	//				Transferable contents = clip
	//						.getContents(Dlg_ImportOWL.controller
	//								.getMainWindow());
	//				if (contents == null) {
	//					System.out.println("The clipboard is empty.");
	//				} else {
	//					// If the contents support the string data flavor then
	//					// retrieve and parse the data contained
	//					// on the clipboard
	//					if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
	//						try {
	//							String data = (String) contents
	//									.getTransferData(DataFlavor.stringFlavor);
	//							if (data != null) {
	//								StringTokenizer st = new StringTokenizer(data,
	//										"\n");
	//								while (st.hasMoreElements()) {
	//									text.addElement(st.nextToken());
	//								}
	//							}
	//						} catch (IOException ex) {
	//							//System.out.println("IOException");
	//						} catch (UnsupportedFlavorException ex) {
	//							//System.out.println("UnsupportedFlavorException");
	//						}
	//					}// else System.out.println("Wrong flavor.");
	//				}
	//				if (text.size() > 0) {
	//					int i = 0;
	//					//for (int i=0;i>text.size();i++){
	//					//System.out.println(text.get(i));
	//					String oldText = field.getText();
	//					String selText = field.getSelectedText();
	//					int start = field.getSelectionStart();
	//					int end = field.getSelectionEnd();
	//					if (selText != null) {
	//						field.setText(oldText.replaceAll(selText, (String) text
	//								.get(i)));
	//					} else {
	//						field
	//								.setText(oldText.substring(0, start)
	//										+ (String) text.get(i)
	//										+ oldText.substring(end));
	//					}
	//				}
	//			}
	//		});
	//		popupMenu.add(item);
	//		popupMenu.pack();
	//	}
}