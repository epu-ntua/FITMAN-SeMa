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

package de.wdilab.coma.gui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.DefaultScrollPane;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.ManagementPane;
import de.wdilab.coma.gui.extjtree.ExtJTree;
import de.wdilab.coma.gui.extjtree.ExtJTreeCellRenderer;
import de.wdilab.coma.matching.SimilarityMeasure;
import de.wdilab.coma.matching.execution.Match2Values;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Path;

/**
 * MatchresultView extends JSplitPane, displays source + target tree.
 * 
 * @author Sabine Massmann
 */
public class MatchresultView2 extends JSplitPane {
	//	private boolean debug = true;
	public Controller controller;
	protected Graph source, target;
	protected ExtJTree sourceTree, targetTree;
	protected JScrollPane sourceTreePane, targetTreePane;
	// 0 SOURCE, 1 TARGET, -1 NONE, 2 MIDDLE
	protected int lastSelectedTree = MainWindow.NONE;
	protected HashMap<Object, TreePath>  srcPath2TreePath, trgPath2TreePath;
	protected ArrayList fragmentPairs, suggestedFragments;
	// CHANGED
	protected boolean changed = true;
	protected LinedMatchresultView2 parent;
	JLabel sourceLabel, targetLabel;
	JPopupMenu popupSrc, popupTrg;
	JMenuItem createCorrespondenceSrc, deleteCorrespondenceSrc, deleteCorrespondencesSrc, 
	retainFragmentCorrespondencesSrc, removeFragmentCorrespondencesSrc,
	createCorrespondenceTrg, deleteCorrespondenceTrg, deleteCorrespondencesTrg, 
	deleteCorrespondencesSrcBeside, deleteCorrespondencesTrgBeside, 
	retainFragmentCorrespondencesTrg, removeFragmentCorrespondencesTrg,
	setHighestSimValueSrc, setHighestSimValueTrg;
//	JMenuItem confirmCorrespondenceSrc, confirmCorrespondenceTrg;
	JMenuItem srcShowInst, srcFold, srcFoldFragmentChildren, srcUnfold, srcUnfoldFragment;
	JMenuItem trgShowInst, trgFold, trgFoldFragmentChildren, trgUnfold, trgUnfoldFragment;
	PropertieJPanel nodeSrc,  nodeTrg;
	
	
	// View
	private int view;
	
	/*
	 * Constructor of the MatchresultView
	 */
	public MatchresultView2(LinedMatchresultView2 _parent, Controller _controller) {
		super(JSplitPane.HORIZONTAL_SPLIT, false);
		parent = _parent;
		setDoubleBuffered(true);
		setBackground(MainWindow.GLOBAL_BACKGROUND);
		controller = _controller;
		sourceTree = getSourceTree(null);
		setSourceTree(sourceTree, null);
		targetTree = getTargetTree(null);
		setTargetTree(targetTree, null);
		//		setDividerLocation(400);
		setResizeWeight(0.5);
		init();
	}

	/*
	 * initialize srcPath2TreePath, trgPath2TreePath,
	 * fragmentPairs,aUpRootPaths, bUpRootPaths
	 */
	public void init() {
		srcPath2TreePath = new HashMap<Object, TreePath>();
		trgPath2TreePath = new HashMap<Object, TreePath>();
		fragmentPairs = new ArrayList();
		suggestedFragments = new ArrayList();
		
		initPopUp();
	}
	
	
	private void initSrc(JPopupMenu popup){
		// Unfold Source Fragment
		if (srcUnfoldFragment==null){
			srcUnfoldFragment = new JMenuItem(GUIConstants.UNFOLD_FRAGMENT, KeyEvent.VK_N);
//			this.sourceTree.getSelectionPath();
			srcUnfoldFragment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent _event) {
					if (controller.getGUIMatchresult().containsSource()) {
						controller.unfoldFragment(controller
										.getMatchresultView().getSourceTree());
					}
				}
			});
		}
		popup.add(srcUnfoldFragment);
		
		popup.addSeparator();		
		// Show Instances
		if (srcShowInst==null){
			srcShowInst = new JMenuItem(GUIConstants.SHOW_INST, KeyEvent.VK_S);
			srcShowInst.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent _event) {
					TreePath sourcePath = sourceTree.getSelectionPath();
					Object object = ((DefaultMutableTreeNode)sourcePath.getLastPathComponent()).getUserObject();
					Element aElement = null;
					if (object instanceof Path){
						Path aObj = (Path) object;
						aElement = aObj.getLastElement();
					} else if (object instanceof Element){
						aElement = (Element) object;
					}				
					controller.showInstances(source.getSource(), aElement);					
				}
			});
		}
		popup.add(srcShowInst);
		popup.add(new JSeparator());	  
	}
	
	private void initTrg(JPopupMenu popup){
		// Unfold Target Fragment
		if (trgUnfoldFragment==null){
			trgUnfoldFragment = new JMenuItem(GUIConstants.UNFOLD_FRAGMENT, KeyEvent.VK_N);
			trgUnfoldFragment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent _event) {
					if (controller.getGUIMatchresult().containsTarget()) {
						controller.unfoldFragment(controller
										.getMatchresultView().getTargetTree());
					}
				}
			});			
		}
		popup.add(trgUnfoldFragment);	
		
		popup.addSeparator();		
		// Show Instances
		if (trgShowInst==null){
			trgShowInst = new JMenuItem(GUIConstants.SHOW_INST, KeyEvent.VK_S);
			trgShowInst.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent _event) {
					TreePath targetPath = targetTree.getSelectionPath();
					Object object = ((DefaultMutableTreeNode)targetPath.getLastPathComponent()).getUserObject();
					Element bElement = null;
					if (object instanceof Path){
						Path bObj = (Path) object;
						bElement = bObj.getLastElement();
					} else if (object instanceof Element){
						bElement = (Element) object;
					}
				 controller.showInstances(target.getSource(),bElement);			
				}
			});
		}
		popup.add(trgShowInst);
		popup.add(new JSeparator());	  
	}
	
	private void initPopUp(){
		// ******************************
	    //Create the source popup menu.
	    popupSrc = new JPopupMenu();
		//Fold Source
	    srcFold = new JMenuItem(GUIConstants.FOLD, KeyEvent.VK_F);
	    srcFold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if (controller.getGUIMatchresult().containsSource()) {
					controller.fold(controller
							.getMatchresultView().getSourceTree());
				}
			}
		});
		popupSrc.add(srcFold);
		//Fold Source Fragment Children
		srcFoldFragmentChildren = new JMenuItem(GUIConstants.FOLD_FRAGMENT_CHILDREN);
		srcFoldFragmentChildren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if (controller.getGUIMatchresult().containsSource()) {
					controller.foldFragmentChildren(controller
							.getMatchresultView().getSourceTree());
				}
			}
		});
		popupSrc.add(srcFoldFragmentChildren);
		// Unfold Source
		srcUnfold = new JMenuItem(GUIConstants.UNFOLD, KeyEvent.VK_U);
		srcUnfold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if (controller.getGUIMatchresult().containsSource()) {
					controller.unfold(controller.getMatchresultView()
									.getSourceTree());
				}
			}
		});
		popupSrc.add(srcUnfold);
		
		initSrc(popupSrc);
	    
	    // Create Correspondence
	    createCorrespondenceSrc = new JMenuItem(GUIConstants.CREATE_CORRESPONDENCE);
	    createCorrespondenceSrc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				editCorrespondence(true);
			}
		});
	    popupSrc.add(createCorrespondenceSrc);
	    // Set Highest Similarity Value
	    setHighestSimValueSrc = new JMenuItem(GUIConstants.SET_HIGHEST_SIMVALUE);
	    setHighestSimValueSrc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				editCorrespondence(true);				
			}
		});
	    popupSrc.add(setHighestSimValueSrc);
//	    // Confirm Correspondence
//	    confirmCorrespondenceSrc = new JMenuItem(GUIConstants.CONFIRM_CORRESPONDENCE);
//	    confirmCorrespondenceSrc.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				confirmCorrespondence();
//			}
//		});
//	    popupSrc.add(confirmCorrespondenceSrc);
//	    // Confirm all Correspondences for this node
//	    confirmCorrespondencesSrc = new JMenuItem(GUIConstants.CONFIRM_CORRESPONDENCES);
//	    confirmCorrespondencesSrc.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				confirmCorrespondences(true);
//			}
//		});
//	    popupSrc.add(confirmCorrespondencesSrc);

	    // Delete Correspondence
	    deleteCorrespondenceSrc = new JMenuItem(GUIConstants.DEL_CORRESPONDENCE);
	    deleteCorrespondenceSrc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				editCorrespondence(false);
			}
		});
	    popupSrc.add(deleteCorrespondenceSrc);
	    // Delete all Correspondences for this node
	    deleteCorrespondencesSrc = new JMenuItem(GUIConstants.DEL_CORRESPONDENCES);
	    deleteCorrespondencesSrc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				deleteCorrespondences(true);	
			}
		});
	    popupSrc.add(deleteCorrespondencesSrc);
	    
	    // Delete all Correspondences for this node - beside the selected one
	    deleteCorrespondencesSrcBeside = new JMenuItem(GUIConstants.DEL_CORRESPONDENCES_BESIDE);
	    deleteCorrespondencesSrcBeside.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				deleteCorrespondencesBesides(true);	
			}
		});
	    popupSrc.add(deleteCorrespondencesSrcBeside);
	    
	    if (sourceTree.getViewKind()==MainWindow.VIEW_GRAPH){
    
		    retainFragmentCorrespondencesSrc = new JMenuItem(GUIConstants.RETAIN_CORRESPONDENCES);
		    retainFragmentCorrespondencesSrc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent _event) {
					remainFragmentCorrespondences(true);	
				}
			});
		    popupSrc.add(retainFragmentCorrespondencesSrc);
		    removeFragmentCorrespondencesSrc = new JMenuItem(GUIConstants.REMOVE_CORRESPONDENCES);
		    removeFragmentCorrespondencesSrc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent _event) {
					removeFragmentCorrespondences(true);	
				}
			});
		    popupSrc.add(removeFragmentCorrespondencesSrc);
	    }

		// ******************************	    
	    //Create the target popup menu.
	    popupTrg = new JPopupMenu();
		//Fold Target
	    trgFold = new JMenuItem(GUIConstants.FOLD, KeyEvent.VK_F);
	    trgFold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if (controller.getGUIMatchresult().containsTarget()) {
					controller.fold(controller
							.getMatchresultView().getTargetTree());
				}
			}
		});
		popupTrg.add(trgFold);
		//Fold Target Fragment Children
		trgFoldFragmentChildren = new JMenuItem(GUIConstants.FOLD_FRAGMENT_CHILDREN);
		trgFoldFragmentChildren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if (controller.getGUIMatchresult().containsTarget()) {
					controller.foldFragmentChildren(controller
							.getMatchresultView().getTargetTree());
				}
			}
		});
		popupTrg.add(trgFoldFragmentChildren);
		// Unfold Target
		trgUnfold = new JMenuItem(GUIConstants.UNFOLD, KeyEvent.VK_U);
		trgUnfold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				if (controller.getGUIMatchresult().containsTarget()) {
					controller.unfold(controller.getMatchresultView()
									.getTargetTree());
				}
			}
		});
		popupTrg.add(trgUnfold);
		initTrg(popupTrg);
		
	    // Create Correspondence
	    createCorrespondenceTrg = new JMenuItem(GUIConstants.CREATE_CORRESPONDENCE);
	    createCorrespondenceTrg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				editCorrespondence(true);
			}
		});
	    popupTrg.add(createCorrespondenceTrg);
	    // Set Highest Similarity Value
	    setHighestSimValueTrg = new JMenuItem(GUIConstants.SET_HIGHEST_SIMVALUE);
	    setHighestSimValueTrg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				editCorrespondence(true);
				controller.getMainWindow().clearMatchresultView();
			}
		});
	    popupTrg.add(setHighestSimValueTrg);
//	    // Confirm Correspondence
//	    confirmCorrespondenceTrg = new JMenuItem(GUIConstants.CONFIRM_CORRESPONDENCE);
//	    confirmCorrespondenceTrg.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				confirmCorrespondence();
//			}
//		});
//	    popupTrg.add(confirmCorrespondenceTrg);
//	    // Confirm all Correspondences for this node
//	    confirmCorrespondencesTrg = new JMenuItem(GUIConstants.CONFIRM_CORRESPONDENCES);
//	    confirmCorrespondencesTrg.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent _event) {
//				confirmCorrespondences(false);
//			}
//		});
//	    popupTrg.add(confirmCorrespondencesTrg);
	    
	    // Delete Correspondence
	    deleteCorrespondenceTrg = new JMenuItem(GUIConstants.DEL_CORRESPONDENCE);
	    deleteCorrespondenceTrg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				editCorrespondence(false);
			}
		});
	    popupTrg.add(deleteCorrespondenceTrg);
	    // Delete all Correspondences for this node
	    deleteCorrespondencesTrg = new JMenuItem(GUIConstants.DEL_CORRESPONDENCES);
	    deleteCorrespondencesTrg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				deleteCorrespondences(false);	
			}
		});   
	    popupTrg.add(deleteCorrespondencesTrg);	  
	    
	    deleteCorrespondencesTrgBeside = new JMenuItem(GUIConstants.DEL_CORRESPONDENCES_BESIDE);
	    deleteCorrespondencesTrgBeside.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				deleteCorrespondencesBesides(false);	
			}
		});
	    popupTrg.add(deleteCorrespondencesTrgBeside);
	    
	    if (targetTree.getViewKind()==MainWindow.VIEW_GRAPH){    
		    retainFragmentCorrespondencesTrg = new JMenuItem(GUIConstants.RETAIN_CORRESPONDENCES);
		    retainFragmentCorrespondencesTrg.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent _event) {
					remainFragmentCorrespondences(false);	
				}
			});
		    popupTrg.add(retainFragmentCorrespondencesTrg);
		    removeFragmentCorrespondencesTrg = new JMenuItem(GUIConstants.REMOVE_CORRESPONDENCES);
		    removeFragmentCorrespondencesTrg.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent _event) {
					removeFragmentCorrespondences(false);	
				}
			});
		    popupTrg.add(removeFragmentCorrespondencesTrg);
	    }

	}
	
	/*
	 * for a given source and target tree node - if there is no correspondence
	 * between them create one (_create=true; similarity 1.0) - if there is a correspondence
	 * delete it (_create=false) 
	 * - both performed only in selection mode and for the current selected matchresult
	 */
	private void editCorrespondence(boolean _create){
    	if (targetTree.getSelectionPath() != null) {
    		DefaultMutableTreeNode target = (DefaultMutableTreeNode) targetTree
				.getSelectionPath().getLastPathComponent();
			MatchResult mr = controller.getGUIMatchresult().getMatchResult();
			System.out.println(mr.getMatchCount());
			Object bObj = target.getUserObject();
			if (sourceTree.getSelectionPath() != null) {
				DefaultMutableTreeNode source = (DefaultMutableTreeNode) sourceTree
						.getSelectionPath().getLastPathComponent();
				Object aObj = source.getUserObject();				
				if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
					// change objects from paths to nodes
					aObj = ((Path)aObj).getLastElement();
					bObj = ((Path)bObj).getLastElement();
				}				
				if (_create){
					 mr.append(aObj, bObj, MatchResult.SIM_MAX);
				} else {
					mr.remove(aObj, bObj);
				}
			}
			controller.getManagementPane().updateMatchresult(mr);
			controller.setNewMatchResult(mr, /*true,*/ false);
			controller.getMainWindow().clearMatchresultView();
			setChanged(true);			
    	}
	}
	
//	private void confirmCorrespondence(){
//		TreePath trgPath = targetTree.getSelectionPath();
//
//    	if (trgPath != null) {
//    		DefaultMutableTreeNode target = (DefaultMutableTreeNode) trgPath.getLastPathComponent();
//			MatchResult mr = controller.getMatchresult()
//			.getMatchResult();
//			ArrayList bObj = (ArrayList) target.getUserObject();
//			TreePath srcPath = sourceTree.getSelectionPath();
//			if (srcPath != null) {
//				DefaultMutableTreeNode source = (DefaultMutableTreeNode) srcPath.getLastPathComponent();
//				ArrayList aObj = (ArrayList) source.getUserObject();
//				HashSet<Line> srcLines = parent.getLinesComponent().getSrcLines().get(srcPath);
//				HashSet<Line> trgLines = parent.getLinesComponent().getTrgLines().get(trgPath);
//				HashSet<Line> linesTmp = (HashSet<Line>) srcLines.clone();
//				linesTmp.retainAll(trgLines);
//				// there should be exactly one line
//				for (Iterator iterator = linesTmp.iterator(); iterator
//						.hasNext();) {
//					Line line = (Line) iterator.next();
//					line.setConfirmed(true);
//				}
////				if (_create){
//					 mr.append(aObj, bObj, MatchResult.SIM_MAX);
////				} else {
////					mr.remove(aObj, bObj);
////				}
//			}
//			controller.getManagementPane().updateMatchresult(mr);
//			controller.setNewMatchResult(mr, true,false);
//			setChanged(true);			
//    	}
//	}
	
	private void deleteCorrespondences(boolean _source){
		MatchResult mr = controller.getGUIMatchresult()
		.getMatchResult();
		if (_source){
			if (sourceTree.getSelectionPath() != null) {
				DefaultMutableTreeNode source = (DefaultMutableTreeNode) sourceTree
						.getSelectionPath().getLastPathComponent();
				Object aObj = source.getUserObject();
				if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
					// change objects from paths to nodes
					aObj = ((Path)aObj).getLastElement();
				}
				ArrayList bMatchObjects = mr.getTrgMatchObjects(aObj);
				for (int i=0; i<bMatchObjects.size();i++){
					mr.remove(aObj, bMatchObjects.get(i));			
				}
			}
		} else {
			if (targetTree.getSelectionPath() != null) {
				DefaultMutableTreeNode target = (DefaultMutableTreeNode) targetTree
						.getSelectionPath().getLastPathComponent();
				Object bObj = target.getUserObject();
				if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
					// change objects from paths to nodes
					bObj = ((Path)bObj).getLastElement();
				}
				ArrayList aMatchObjects = mr.getSrcMatchObjects(bObj);
				for (int i=0; i<aMatchObjects.size();i++){
					mr.remove(aMatchObjects.get(i), bObj);			
				}
			}
		}
		controller.getManagementPane().updateMatchresult(mr);
		controller.setNewMatchResult(mr, /*true,*/ false);
		controller.getMainWindow().clearMatchresultView();
		setChanged(true);	
	}
	
	private void deleteCorrespondencesBesides(boolean _source){
		Object aObjBesides = null, bObjBesides = null;
		MatchResult mr = controller.getGUIMatchresult().getMatchResult();
		System.out.println(mr.getMatchCount());
		if (targetTree.getSelectionPath() != null && sourceTree.getSelectionPath() != null) {
    		DefaultMutableTreeNode target = (DefaultMutableTreeNode) targetTree
				.getSelectionPath().getLastPathComponent();
			bObjBesides = target.getUserObject();
			if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
				// change objects from paths to nodes
				bObjBesides = ((Path)bObjBesides).getLastElement();
			}	
			if (sourceTree.getSelectionPath() != null) {
				DefaultMutableTreeNode source = (DefaultMutableTreeNode) sourceTree
						.getSelectionPath().getLastPathComponent();
				aObjBesides = source.getUserObject();
				if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
					// change objects from paths to nodes
					aObjBesides = ((Path)aObjBesides).getLastElement();
				}	
			}
		}
		if (aObjBesides==null || bObjBesides==null){
			deleteCorrespondences(_source);
			return;
		}
		if (_source){
			if (sourceTree.getSelectionPath() != null) {
				DefaultMutableTreeNode source = (DefaultMutableTreeNode) sourceTree
						.getSelectionPath().getLastPathComponent();
				Object aObj = source.getUserObject();
				if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
					// change objects from paths to nodes
					aObj = ((Path)aObj).getLastElement();
				}	
				ArrayList bMatchObjects = mr.getTrgMatchObjects(aObj);
				for (int i=0; i<bMatchObjects.size();i++){
					Object bObj = bMatchObjects.get(i);
					if (!aObj.equals(aObjBesides) || !bObj.equals(bObjBesides)){
						mr.remove(aObj, bObj);			
					}
				}
			}
		} else {
			if (targetTree.getSelectionPath() != null) {
				DefaultMutableTreeNode target = (DefaultMutableTreeNode) targetTree
						.getSelectionPath().getLastPathComponent();
				Object bObj = target.getUserObject();
				if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
					// change objects from paths to nodes
					bObj = ((Path)bObj).getLastElement();
				}	
				ArrayList aMatchObjects = mr.getSrcMatchObjects(bObj);
				for (int i=0; i<aMatchObjects.size();i++){
					Object aObj = aMatchObjects.get(i);
					if (!aObj.equals(aObjBesides) || !bObj.equals(bObjBesides)){
						mr.remove(aObj, bObj);			
					}		
				}
			}
		}
		controller.getManagementPane().updateMatchresult(mr);
		controller.setNewMatchResult(mr, /*true,*/ false);
		controller.getMainWindow().clearMatchresultView();
		setChanged(true);	
	}
	
	
	private void remainFragmentCorrespondences(boolean _source){
		MatchResult mr = controller.getGUIMatchresult()
		.getMatchResult();
		if (_source){
			if (sourceTree.getSelectionPath() != null) {
				DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) sourceTree
				.getSelectionPath().getLastPathComponent();
				Object aObj = sourceNode.getUserObject();
				if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
					// change objects from paths to nodes
					aObj = ((Path)aObj).getLastElement();
				}	
				ArrayList aMatchObjects = mr.getSrcMatchObjects();
				ArrayList restrictAObj = new ArrayList();
				if (aObj instanceof Path){
					for (int i=0; i<aMatchObjects.size();i++){
						 Path current = (Path) aMatchObjects.get(i);
						if (current.containsAll((Path)aObj)){
							restrictAObj.add(current);
						}
					}
				} else {
					for (int i=0; i<aMatchObjects.size();i++){
						Element current = (Element) aMatchObjects.get(i);
						if (current.equals(aObj)){
							restrictAObj.add(current);
						}
					}
				}
				
				
//				mr.print();
				mr = MatchResult.restrict(mr, restrictAObj, mr.getTrgObjects());
//				mr.print();
			}
		} else {
			if (targetTree.getSelectionPath() != null) {
				DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetTree
				.getSelectionPath().getLastPathComponent();
				Object bObj = targetNode.getUserObject();
				if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
					// change objects from paths to nodes
					bObj = ((Path)bObj).getLastElement();
				}	
				ArrayList bMatchObjects = mr.getTrgMatchObjects();
				ArrayList restrictBObj = new ArrayList();
				if (bObj instanceof Path){
					for (int i=0; i<bMatchObjects.size();i++){
						ArrayList current = (ArrayList) bMatchObjects.get(i);
						if (current.containsAll((ArrayList)bObj)){
							restrictBObj.add(current);
						}
					}
				} else {
					for (int i=0; i<bMatchObjects.size();i++){
						Element current = (Element) bMatchObjects.get(i);
						if (current.equals(bObj)){
							restrictBObj.add(current);
						}
					}
				}
//				mr.print();
				mr = MatchResult.restrict(mr, mr.getSrcObjects(), restrictBObj);
//				mr.print();
			}
		}
		controller.getManagementPane().updateMatchresult(mr);
		controller.setNewMatchResult(mr, /*true,*/ false);
		controller.getMainWindow().clearMatchresultView();
		setChanged(true);	
	}
	
	private void removeFragmentCorrespondences(boolean _source){
		MatchResult mr = controller.getGUIMatchresult()
		.getMatchResult();
		if (_source){
			if (sourceTree.getSelectionPath() != null) {
				DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) sourceTree
				.getSelectionPath().getLastPathComponent();
				Path aObj = (Path) sourceNode.getUserObject();
				ArrayList aMatchObjects = mr.getSrcMatchObjects();
				ArrayList restrictAObj = new ArrayList();
				for (int i=0; i<aMatchObjects.size();i++){
					Path current = (Path) aMatchObjects.get(i);
					if (current.containsAll(aObj)){
						restrictAObj.add(current);
					}
				}
				aMatchObjects.removeAll(restrictAObj);
//				mr.print();
				mr = MatchResult.restrict(mr, aMatchObjects, mr.getTrgObjects());
//				mr.print();
			}
		} else {
			if (targetTree.getSelectionPath() != null) {
				DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetTree
				.getSelectionPath().getLastPathComponent();
				Path bObj = (Path) targetNode.getUserObject();
				ArrayList bMatchObjects = mr.getTrgMatchObjects();
				ArrayList restrictBObj = new ArrayList();
				for (int i=0; i<bMatchObjects.size();i++){
					Path current = (Path) bMatchObjects.get(i);
					if (current.containsAll(bObj)){
						restrictBObj.add(current);
					}
				}
				bMatchObjects.removeAll(restrictBObj);
//				mr.print();
				mr = MatchResult.restrict(mr, mr.getSrcObjects(), bMatchObjects);
//				mr.print();
			}
		}
		controller.getManagementPane().updateMatchresult(mr);
		controller.setNewMatchResult(mr, /*true,*/ false);
		controller.getMainWindow().clearMatchresultView();
		setChanged(true);	
	}
	
	/*
	 * scan through the given match results for pairs, where at least one path
	 * is a local path (= fragment was matched)
	 */
	public void scanForSuggestedFragments(MatchResult _result,
			Graph _sourceGraph, boolean _deleteOld,
			ArrayList _srcObjects, ArrayList _trgObjects) {
		if ((_result == null) || (_srcObjects == null) || (_trgObjects == null)
				|| (_sourceGraph == null)) {
			return;
		}
		if ((suggestedFragments != null) && _deleteOld) {
			suggestedFragments.clear();
		}
		ArrayList as = new ArrayList();
		ArrayList bs = new ArrayList();
		for (int i = 0; i < _srcObjects.size(); i++) {
			Object aList = _srcObjects.get(i);
			if (aList != null) {
				for (int j = 0; j < _trgObjects.size(); j++) {
					Object bList =_trgObjects.get(j);
					float sim = _result.getSimilarity(aList, bList);
					if (sim > 0) {
						if (!suggestedFragments.contains(aList)) {
							suggestedFragments.add(aList);
						}
						if (!suggestedFragments.contains(bList)) {
							suggestedFragments.add(bList);
						}
						if (!as.contains(aList)) {
							as.add(aList);
						}
						if (!bs.contains(bList)) {
							bs.add(bList);
						}
					}
				}
			}
		}
		_srcObjects.clear();
		_srcObjects.addAll(as);
		_trgObjects.clear();
		_trgObjects.addAll(bs);
	}

	/*
	 * scan through the given match results for pairs, where at least one path
	 * is a local path (= fragment was matched)
	 */
	public void scanForSuggestedFragments(ArrayList _as,
			Graph _sourceGraph, boolean _deleteOld) {
		if ((_as == null) || (_as.size() == 0) || (_sourceGraph == null)) {
			return;
		}
		if ((suggestedFragments) != null && _deleteOld) {
			suggestedFragments.clear();
		}
		// as are VertexImpl, search their upPaths in the Graph
		// save them to suggestesFragments
		if (_as.get(0) instanceof Element) {
			for (int i = 0; i < _as.size(); i++) {
				Element a = (Element) _as.get(i);
				ArrayList aUp = _sourceGraph.getUpRootPaths(a);
				if (aUp != null) {
					suggestedFragments.addAll(aUp);
				}
			}
			suggestedFragments.addAll(_as);
		} else if (_as.get(0) instanceof Path) {
			suggestedFragments.addAll(_as);
		}
	}

	/* return source tree */
	public ExtJTree getSourceTree() {
		return sourceTree;
	}

	/* return source tree */
	public ExtJTree getTargetTree() {
		return targetTree;
	}

	/*
	 * sets a new Controller
	 */
	public void setController(Controller _controller) {
		controller = _controller;
	}

	/*
	 * create for the given schema graph a source tree and repaint the split
	 * pane
	 */
	public void setSourceSchema(Graph _Graph) {
		source = _Graph;
		ExtJTree tree =null;
		switch(view){
		case MainWindow.VIEW_GRAPH:
			tree = getSourceTree(source);
			break;
		case MainWindow.VIEW_NODES:
			tree =  getSourceNodesTree(source);
			break;
		}
		setSourceTree(tree, source);
	}

	/*
	 * create for the given schema graph a target tree and repaint the split
	 * pane
	 */
	public void setTargetSchema(Graph _Graph) {
		target = _Graph;
		ExtJTree tree =null;
		switch(view){
		case MainWindow.VIEW_GRAPH:
			tree = getTargetTree(target);
			break;
		case MainWindow.VIEW_NODES:
			tree =  getTargetNodesTree(target);
			break;
		}
		setTargetTree(tree, target);
	}

	
	private DefaultMutableTreeNode getTree(Graph graph){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		ArrayList roots = graph.getRoots();
		roots = sortNodesId(roots);
		if (roots != null) {
			for (int i = 0; i < roots.size(); i++) {
				buildTree(root, new Path(graph), (Element) roots.get(i),
						graph);
			}
		}
		return root;
	}
	
	/*
	 * start the creation of a source tree for a given schema graph, expand the
	 * tree and add CellRenderer (if schema graph is null create a default
	 * source tree)
	 */
	private ExtJTree getSourceTree(Graph graph) {
		boolean verbose = false;
		ExtJTree newTree = null;
		if (graph == null) {
			newTree = new ExtJTree(null, this, ExtJTree.SOURCE, MainWindow.VIEW_GRAPH);
		} else {
			 long start = System.currentTimeMillis();
			DefaultMutableTreeNode root =getTree(graph);
			 long end = System.currentTimeMillis();
			 if (verbose)
				 System.out.println("--> getTree(_Graph): " + (float) (end - start) / 1000);
			 start = System.currentTimeMillis();	
			newTree = new ExtJTree(root, this, ExtJTree.SOURCE, MainWindow.VIEW_GRAPH);
			//			don't know why but we need this line to show the ToolTip of the
			// elements
			end = System.currentTimeMillis();
			 if (verbose)
				 System.out.println("--> new ExtJTree " + (float) (end - start) / 1000);
			 start = System.currentTimeMillis();	
			 
			newTree.setToolTipText(GUIConstants.EMPTY);
			DefaultTreeCellRenderer renderer = new ExtJTreeCellRenderer(
					ExtJTree.SOURCE, this);
			newTree.setCellRenderer(renderer);
			end = System.currentTimeMillis();
			 if (verbose)
				 System.out.println("--> setCellRenderer " + (float) (end - start) / 1000);
			 start = System.currentTimeMillis();	
			newTree.expandAll();
			end = System.currentTimeMillis();
			 if (verbose)
				 System.out.println("--> expandAll " + (float) (end - start) / 1000);
			 start = System.currentTimeMillis();	

			buildPath2TreePath(newTree, srcPath2TreePath);
			end = System.currentTimeMillis();
			 if (verbose)
				 System.out.println("--> buildPath2TreePath " + (float) (end - start) / 1000);
		}
		return newTree;
	}
	
	
	private DefaultMutableTreeNode getNodeTree(Graph _graph){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		ArrayList nodes = new ArrayList();
		Iterator verticesIt = _graph.getElementIterator();
		while (verticesIt.hasNext()){
			nodes.add(verticesIt.next());				
		}
		nodes = sortNodesNames(nodes);
		for (int i = 0; i < nodes.size(); i++) {
//			ArrayList path = new ArrayList();
//			path.add(nodes.get(i));
//			DefaultMutableTreeNode node = new DefaultMutableTreeNode(path);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodes.get(i));
			root.add(node);
		}
		return root;
	}
	
	private ExtJTree getSourceNodesTree(Graph _Graph) {
		ExtJTree newTree = null;
		if (_Graph == null) {
			newTree = new ExtJTree(null, this, ExtJTree.SOURCE, MainWindow.VIEW_NODES);
		} else {
			DefaultMutableTreeNode root = getNodeTree(_Graph);
			newTree = new ExtJTree(root, this, ExtJTree.SOURCE, MainWindow.VIEW_NODES);
//			//			don't know why but we need this line to show the ToolTip of the
//			// elements
			newTree.setToolTipText(GUIConstants.EMPTY);
			DefaultTreeCellRenderer renderer = new ExtJTreeCellRenderer(
					ExtJTree.SOURCE, this);
			newTree.setCellRenderer(renderer);
			newTree.expandAll();
			srcPath2TreePath.clear();
			buildPath2TreePath(newTree, srcPath2TreePath);
		}
		return newTree;
	}
	
	private ExtJTree getTargetNodesTree(Graph _Graph) {
		ExtJTree newTree = null;
		if (_Graph == null) {
			newTree = new ExtJTree(null, this, ExtJTree.TARGET, MainWindow.VIEW_NODES);
		} else {
			DefaultMutableTreeNode root = getNodeTree(_Graph);
			newTree = new ExtJTree(root, this, ExtJTree.TARGET, MainWindow.VIEW_NODES);
//			//			don't know why but we need this line to show the ToolTip of the
//			// elements
			newTree.setToolTipText(GUIConstants.EMPTY);
			DefaultTreeCellRenderer renderer = new ExtJTreeCellRenderer(
					ExtJTree.TARGET, this);
			newTree.setCellRenderer(renderer);
			newTree.expandAll();
			trgPath2TreePath.clear();
			buildPath2TreePath(newTree, trgPath2TreePath);
		}
		return newTree;
	}

	/*
	 * start the creation of a target tree for a given schema graph, expand the
	 * tree and add CellRenderer (if schema graph is null create a default
	 * target tree)
	 */
	private ExtJTree getTargetTree(Graph _Graph) {
		ExtJTree newTree = null;
		if (_Graph == null) {
			newTree = new ExtJTree(null, this, ExtJTree.TARGET, MainWindow.VIEW_GRAPH);
		} else {
			DefaultMutableTreeNode root =getTree(_Graph);
			newTree = new ExtJTree(root, this, ExtJTree.TARGET, MainWindow.VIEW_GRAPH);
			//			don't know why but we need this line to show the ToolTip of the
			// elements
			newTree.setToolTipText(GUIConstants.EMPTY);
			DefaultTreeCellRenderer renderer = new ExtJTreeCellRenderer(
					ExtJTree.TARGET, this);
			newTree.setCellRenderer(renderer);
			newTree.expandAll();
			buildPath2TreePath(newTree, trgPath2TreePath);
		}
		return newTree;
	}

	public static ArrayList sortNodesId(ArrayList<Element> _nodes) {
		ArrayList sortNodes = new ArrayList();
		if ((_nodes == null) || (_nodes.size() < 2)) {
			return _nodes;
		}
		while (!_nodes.isEmpty()) {
			int smallest = 0;
			int smallestID = _nodes.get(0).getId();
			for (int i = 1; i < _nodes.size(); i++) {
				int current = _nodes.get(i).getId();
				if (current < smallestID) {
					smallest = i;
					smallestID = current;
				}
			}
			sortNodes.add(_nodes.get(smallest));
			//			System.out.println(nodes.get(smallest)+ " --- " + sortNodes);
			_nodes.remove(smallest);
		}
		return sortNodes;
	}
	
	public static ArrayList sortNodesNames(ArrayList _nodes) {
		ArrayList sortNodes = new ArrayList();
		if ((_nodes == null) || (_nodes.size() < 2)) {
			return _nodes;
		}
		while (!_nodes.isEmpty()) {
			int smallest = 0;
			Element el = (Element) _nodes.get(0);
			String smallestName = el.getName();
			for (int i = 1; i < _nodes.size(); i++) {
				Element currentEl = (Element) _nodes.get(i);
				String currentName = currentEl.getName();
				if (currentName.compareToIgnoreCase(smallestName)<=0) {
					smallest = i;
					smallestName = currentName;
				}
			}
			sortNodes.add(_nodes.get(smallest));
			//			System.out.println(nodes.get(smallest)+ " --- " + sortNodes);
			_nodes.remove(smallest);
		}
		return sortNodes;
	}

	/*
	 * build a Tree using the schema graph by creating a children
	 * DefaultMutableTreeNode and adding the VertexImpl + path
	 */
	protected void buildTree(DefaultMutableTreeNode _parent, Path _path,
			Element _current, Graph graph) {
		//		DefaultMutableTreeNode node = new DefaultMutableTreeNode(list);
		Path newPath = new Path(_path);
		newPath.add(_current);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(newPath);
		// call this function for all children to build recursively the tree
		ArrayList children = graph.getChildren(_current);
		if (children != null) {
			children = sortNodesId(children);
			for (int i = 0; i < children.size(); i++) {
				buildTree(node, newPath, (Element) children.get(i),
						graph);
			}
		}
		_parent.add(node);
	}

	/*
	 * go for a given tree through all elements and add to hashmap the schema
	 * graph path as key and the Tree Path as value (needed for drawing lines
	 * and find the source / target Tree Path faster)
	 */
	protected void buildPath2TreePath(ExtJTree _tree, HashMap _path2TreePath) {
		if (_tree != null) {
			int rows = _tree.getRowCount();
			for (int i = 0; i < rows; i++) {
				TreePath path = _tree.getPathForRow(i);
				if (((DefaultMutableTreeNode) path.getLastPathComponent())
						.getUserObject() instanceof Path) {
					Path userObject = (Path) ((DefaultMutableTreeNode) path
							.getLastPathComponent()).getUserObject();
					if (userObject != null) {
						_path2TreePath.put(userObject, path);
					}
				}
			}
		}
	}

	/*
	 * swap the source and the target Graphs by swaping there trees,
	 * upRootPaths and Path2TreePaths
	 */
	public void swap() {
		if (lastSelectedTree == ExtJTree.SOURCE) {
			lastSelectedTree = ExtJTree.TARGET;
		} else if (lastSelectedTree == ExtJTree.TARGET) {
			lastSelectedTree = ExtJTree.SOURCE;
		}
		// swap graphs
		Graph helpGraph = source;
		source = target;
		target = helpGraph;
		// swap Trees
		ExtJTree helpTree = sourceTree;
		sourceTree = targetTree;
		targetTree = helpTree;
		// swap path2TreePaths
		HashMap helpMap = srcPath2TreePath;
		srcPath2TreePath = trgPath2TreePath;
		trgPath2TreePath = helpMap;
		setSourceTree(sourceTree, source);
		setTargetTree(targetTree, target);
	}

	class MyAdjustmentListener implements AdjustmentListener {
		int lastValue=0;
		
		public void adjustmentValueChanged(AdjustmentEvent e) {
			if (e.getID()==AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED && !isChanged()){
				int newValue = e.getValue();
				if (newValue!=lastValue){
					lastValue = newValue;
				}
				setChanged(true);
			} 
		}

	}
	/*
	 * set the source tree, create a new tree pane and add Listener
	 */
	public void setSourceTree(ExtJTree _sourceTree, Graph _Graph) {
		sourceTree = _sourceTree;
		String name = GUIConstants.EMPTY;
//		String content = null;
		String tooltiptext = null;
		if (_Graph != null) {
			name = GUIConstants.EMPTY + _Graph.getSource().getName();
//			content = Source.typeToString(_Graph.getSource().getType());
			tooltiptext = GUIConstants.EMPTY + _Graph.getSource().getName()
					+ GUIConstants.COLON_SPACE2
					+ _Graph.getSource().getProvider();
//					+ GUIConstants.SPACE_SMALLER + _Graph.getSource().getUrl()
//					+ GUIConstants.BIGGER;
		}
		String labelText = name;
//		if (content != null) {
//			labelText = //GUIConstants.TARGETSCHEMA + GUIConstants.COLON_SPACE +
//			name + GUIConstants.BRACKET_LEFT + content + GUIConstants.BRACKET_RIGHT;
//		} else {
//			labelText = GUIConstants.SOURCESCHEMA + //GUIConstants.COLON_SPACE +
//					name;
//		}
		if (controller.getMainWindow()!=null){
			LinedMatchresultView2 view2 = controller.getMainWindow().getNewContentPane().getLMW2();
			view2.getLinesComponent().setHighlightedSrc(null);
		}
		sourceTreePane = new DefaultScrollPane(_sourceTree, this);
		sourceTreePane.getVerticalScrollBar().setUI(new MyMetalScrollBarUI());
		sourceTreePane.getHorizontalScrollBar().addAdjustmentListener(new MyAdjustmentListener());
		sourceTreePane.getVerticalScrollBar().addAdjustmentListener(new MyAdjustmentListener());
	    
		JPanel sourcePanel = new JPanel(new BorderLayout());
		sourceLabel = new JLabel(labelText, SwingConstants.CENTER);
		sourceLabel.setLayout(new BorderLayout());
//		JButton button = new JButton(new ImageIcon(GUIConstants.ICON_CLOSE));
		JButton	button = new JButton(Controller.getImageIcon(GUIConstants.ICON_CLOSE));
		button.setToolTipText(GUIConstants.CLOSE_SCHEMA);
		button.setMargin(ManagementPane.BUTTON_INSETS);
		sourceLabel.add(button, BorderLayout.EAST);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				controller.closeSchema(true, false);
			}
		});
		sourceLabel.setToolTipText(tooltiptext);
		sourcePanel.add(sourceLabel, BorderLayout.NORTH);
		sourcePanel.add(sourceTreePane, BorderLayout.CENTER);
		if (!(this instanceof MatchresultView3)){
			sourcePanel.add(initSearch(sourceTree, true), BorderLayout.SOUTH);
		}
		//		setLeftComponent(sourceTreePane);
		setLeftComponent(sourcePanel);
		setDividerLocation(0.5);

	    //Add listener to components that can bring up popup menus.
	    sourceTree.addMouseListener(new PopupSrcListener());
}
	
	JPanel nodeInformation(ExtJTree tree, boolean src){
		JPanel panel = new JPanel(new BorderLayout());
		JLabel label = null;
		if (src){
			label = new JLabel("Source Information"); // Source Node Information
		} else {
			label = new JLabel("Target Information"); // Target Node Information 
		}
		panel.add(label, BorderLayout.NORTH);
		if (src){
			nodeSrc = new PropertieJPanel(true, source);
			panel.add(nodeSrc, BorderLayout.SOUTH);
		} else {
			nodeTrg = new PropertieJPanel(false, target);
			panel.add(nodeTrg, BorderLayout.SOUTH);
		}
		return panel;
	}
	
	
	private JPanel initSearch(ExtJTree tree, boolean src){
		JPanel panel = new JPanel(new BorderLayout());
		JLabel searchLabel = new JLabel("Search ");
		panel.add(searchLabel, BorderLayout.WEST);
		JTextField searchField = new JTextField();
		panel.add(searchField, BorderLayout.CENTER);
		searchField.addKeyListener(new SearchKeyAdapter(tree, controller, src));
		
		JButton	button = new JButton(Controller.getImageIcon(GUIConstants.ICON_CLOSE));
		button.setToolTipText("Empty Search");
		button.setMargin(ManagementPane.BUTTON_INSETS);
		panel.add(button, BorderLayout.EAST);
		 
		button.addActionListener(new SearchActionListener(tree, controller, src));
		panel.add(nodeInformation(tree,src), BorderLayout.NORTH);
		
		return panel;
	}
	
	class SearchActionListener implements ActionListener {
		private ExtJTree tree;
		boolean src=true;
		Controller controller = null;
		
		public SearchActionListener(ExtJTree _tree, Controller _controller, boolean _src) {
			tree = _tree;
			controller = _controller;
			src=_src;
		}
		public void actionPerformed(ActionEvent e) {
			if (tree.getCellRenderer() instanceof ExtJTreeCellRenderer){
				((ExtJTreeCellRenderer) tree.getCellRenderer()).setHighlighted(null);
			}
			LinedMatchresultView2 view = controller.getMainWindow().getNewContentPane().getLMW2();
			if (src){
				view.getLinesComponent().setHighlightedSrc(null);
			} else {
				view.getLinesComponent().setHighlightedTrg(null);
			}
		}
	}
	
	class SearchKeyAdapter extends KeyAdapter {
		private ExtJTree tree;
		private Controller controller;
		boolean src=true;

		/*
		 * Constructor for ExtJTreeMouseAdapter
		 */
		public SearchKeyAdapter(ExtJTree _tree,Controller _controller, boolean _src) {
			tree = _tree;
			controller = _controller;
			src=_src;
		}

		public void keyReleased(KeyEvent _event) {
			char c = Character.toLowerCase(_event.getKeyChar());
			if (c == '\n') {
				 LinedMatchresultView2 view = controller.getMainWindow().getNewContentPane().getLMW2();
				String test = //"Name";
				((JTextField)_event.getSource()).getText();
				HashMap<Integer,DefaultMutableTreeNode> nodesWithString = tree.getMatches(test);
				if (nodesWithString!=null && test.length()>0){
					((ExtJTreeCellRenderer) tree.getCellRenderer()).setHighlighted(new HashSet(nodesWithString.values()));
					if (src){
						view.getLinesComponent().setHighlightedSrc(nodesWithString);
					} else {
						view.getLinesComponent().setHighlightedTrg(nodesWithString);
					}
				} else if (tree.getCellRenderer() instanceof ExtJTreeCellRenderer) {
					// highlight nothing
					((ExtJTreeCellRenderer) tree.getCellRenderer()).setHighlighted(null);
					if (src){
						view.getLinesComponent().setHighlightedSrc(null);
					} else {
						view.getLinesComponent().setHighlightedTrg(null);
					}
				}		   
				
		      }
		}
	}
//
//	class PopupActionListener implements ActionListener{
//
//		public void actionPerformed(ActionEvent _event) {
//			System.out.println(_event.getSource());
//		}
//		
//	}

	class PopupSrcListener extends MouseAdapter {
	    public void mousePressed(MouseEvent _event) {
	        maybeShowPopup(_event);
	        if (sourceTree.getSelectionPath() != null) {
        		DefaultMutableTreeNode source = (DefaultMutableTreeNode) sourceTree
					.getSelectionPath().getLastPathComponent();
        		if ( source.getUserObject() instanceof Path){
        			Path aObj = (Path) source.getUserObject();
					nodeSrc.showProperties(aObj);
					if (targetTree.getSelectionPath() != null){
		        		DefaultMutableTreeNode target = (DefaultMutableTreeNode) targetTree
						.getSelectionPath().getLastPathComponent();
		        		Path bObj = (Path) target.getUserObject();
						compareProperties(aObj, bObj);
					}
        		}
	        } else {
//	    		ColoredTableCellRenderer srcRenderer = nodeSrc.getCellRenderer();
//	    		ColoredTableCellRenderer trgRenderer = nodeTrg.getCellRenderer();
//	    		srcRenderer.resetSim();
//	    		trgRenderer.resetSim();
////	    		nodeSrc.repaint();
//	    		nodeTrg.repaint();
	        	nodeSrc.showNothing(source);
	        	controller.getMainWindow().getNewContentPane().getLMW2().resetSim();
	        }
	    }

	    public void mouseReleased(MouseEvent _event) {
//	    	Rectangle r = sourceTree.getBounds();
//        		System.out.println("Rectangle sourceTree: " + r);
//    		 r = sourceTree.getPathBounds(sourceTree
//					.getSelectionPath());
//        		System.out.println("Rectangle getSelectionPath: " + r);
//        		JViewport v= sourceTreePane.getViewport();
//        		r =  v.getVisibleRect();
//        		System.out.println("Rectangle VisibleRect: " + r);
	        maybeShowPopup(_event);
	    }
	
	    private void maybeShowPopup(MouseEvent _event) {
	        if (_event.isPopupTrigger()) {        		
				MatchResult mr = controller.getGUIMatchresult().getMatchResult();
				if (mr==null){
					createCorrespondenceSrc.setEnabled(false);
					setHighestSimValueSrc.setEnabled(false);
					deleteCorrespondenceSrc.setEnabled(false);
					deleteCorrespondencesSrc.setEnabled(false);
	        		removeFragmentCorrespondencesSrc.setEnabled(false);
	        		retainFragmentCorrespondencesSrc.setEnabled(false);
				} else {
		        	if (sourceTree.getSelectionPath() != null) {
		        		DefaultMutableTreeNode source = (DefaultMutableTreeNode) sourceTree
							.getSelectionPath().getLastPathComponent();
						Object aObj = source.getUserObject();
						if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
							// change objects from paths to nodes
							aObj = ((Path)aObj).getLastElement();
						}
						if (targetTree.getSelectionPath() != null) {
							DefaultMutableTreeNode target = (DefaultMutableTreeNode) targetTree
									.getSelectionPath().getLastPathComponent();
							Object bObj = target.getUserObject();
							if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
								// change objects from paths to nodes
								bObj = ((Path)bObj).getLastElement();
							}
							float sim = mr.getSimilarity(aObj, bObj);
							if ((sim == MatchResult.SIM_UNDEF)
									|| (sim == MatchResult.SIM_MIN)) {
								createCorrespondenceSrc.setEnabled(true);
								setHighestSimValueSrc.setEnabled(false);
								deleteCorrespondenceSrc.setEnabled(false);
//								confirmCorrespondenceSrc.setEnabled(true);
							} else {
								createCorrespondenceSrc.setEnabled(false);
								if (sim<MatchResult.SIM_MAX){
									setHighestSimValueSrc.setEnabled(true);
								} else {
									setHighestSimValueSrc.setEnabled(false);
								}
								deleteCorrespondenceSrc.setEnabled(true);
//								confirmCorrespondenceSrc.setEnabled(true);
							}
						} else {
							createCorrespondenceSrc.setEnabled(false);
							setHighestSimValueSrc.setEnabled(false);
							deleteCorrespondenceSrc.setEnabled(false);
//							confirmCorrespondenceSrc.setEnabled(false);
						}
						ArrayList bMatchObjects = mr.getTrgMatchObjects(aObj);
						if (bMatchObjects!=null && bMatchObjects.size()>0){
//							if (bMatchObjects.size()==1){
//								setHighestSimValueSrc.setEnabled(true);
//							}
							deleteCorrespondencesSrc.setEnabled(true);
						} else {
							deleteCorrespondencesSrc.setEnabled(false);
						}
		        	} else {
		        		createCorrespondenceSrc.setEnabled(false);
		        		setHighestSimValueSrc.setEnabled(false);
		        		deleteCorrespondenceSrc.setEnabled(false);
						deleteCorrespondencesSrc.setEnabled(false);
					}
				}
	        		TreePath sourcePath = sourceTree.getSelectionPath();
	        		if (sourcePath!=null){
	        			Element aElement = null;
	        			Object object = ((DefaultMutableTreeNode) sourcePath.getLastPathComponent()).getUserObject();
	        			if (object instanceof Path){
	        				Path aObj = (Path)object;
	        				aElement = aObj.getLastElement();
	        			} else if (object instanceof Element){
	        				aElement = (Element) object;
	        			}
		        		 
		        		if (aElement!=null && (aElement.hasDirectInstancesSimple() || aElement.hasDirectInstancesComplex())){
		        			srcShowInst.setEnabled(true);
		        		} else {
		        			srcShowInst.setEnabled(false);
		        		}
		        		if (mr!=null){
			        		retainFragmentCorrespondencesSrc.setEnabled(true);
		        		}
	        			Enumeration<TreePath> descendants = sourceTree.getExpandedDescendants(sourcePath);
	        			if (descendants!=null ){
	        				srcFoldFragmentChildren.setEnabled(true);
			        		srcUnfoldFragment.setEnabled(true);
			        		if (mr!=null){
				        		removeFragmentCorrespondencesSrc.setEnabled(true);
			        		} else {
				        		removeFragmentCorrespondencesSrc.setEnabled(false);
			        		}
	        			} else {
	        				removeFragmentCorrespondencesSrc.setEnabled(false);
	        				srcFoldFragmentChildren.setEnabled(false);
			        		srcUnfoldFragment.setEnabled(false);
	        			}
	        		} else  {
	        			srcShowInst.setEnabled(false);
		        		removeFragmentCorrespondencesSrc.setEnabled(false);
		        		retainFragmentCorrespondencesSrc.setEnabled(false);
		        		srcUnfoldFragment.setEnabled(false);
		        		srcFoldFragmentChildren.setEnabled(false);
	        		}	        		
		            popupSrc.show(_event.getComponent(),
		                       _event.getX(), _event.getY());			
	        }
	    }
	}

    private void compareProperties(Path aObj, Path bObj) {
		Element aElement = aObj.getLastElement();
    	String aName= aElement.getName();
//    	String aPath = Graph.pathToNameString(aObj);
//    	String aPath = aObj.toString();
    	String aPath = aObj.toNameString();
    	String aComment = aElement.getComment();

		Element bElement = bObj.getLastElement();
    	String bName= bElement.getName();
//    	String bPath = Graph.pathToNameString(bObj);
//    	String bPath = bPath.toString();
    	String bPath = bObj.toNameString();
    	String bComment = bElement.getComment();
    	    	
    	float nameSim = -1, pathSim=-1, commentSim=-1, instanceSim=-1;
    	if (aName!=null && bName!=null){
//    		nameSim = SimilarityMeasure.computeTrigramSimilarity(aName, bName);
    		nameSim = Match2Values.execute(SimilarityMeasure.SIM_STR_TRIGRAM, aName, bName);
    	}
    	if (aPath!=null && bPath!=null){
    		aPath = aPath.replace(".", " ");
    		bPath = bPath.replace(".", " ");
//    		pathSim = SimilarityMeasure.computeTrigramSimilarity(aPath, bPath);
    		pathSim = Match2Values.execute(SimilarityMeasure.SIM_STR_COSINE, aPath, bPath);
    	}
    	if (aComment!=null && bComment!=null){
//    		commentSim = SimilarityMeasure.computeTrigramSimilarity(aComment, bComment);
    		commentSim = Match2Values.execute(SimilarityMeasure.SIM_STR_COSINE, aPath, bPath);
    	}
//    	float instanceSimSimple = InstanceMatcher.computeSimpleSimilarity(aElement, bElement);
    	float instanceSimSimple =  Match2Values.execute(SimilarityMeasure.SIM_STR_COSINE, aElement.getDirectInstancesSimple(), bElement.getDirectInstancesSimple());
//    	float instanceSimComplex = InstanceMatcher.computeComplexSimilarity(aElement, bElement);
    	float instanceSimComplex =  Match2Values.execute(SimilarityMeasure.SIM_STR_COSINE, aElement.getDirectInstancesComplex(), bElement.getDirectInstancesComplex());
    	if (instanceSimSimple>-1){
    		if (instanceSimComplex>-1){
    			instanceSim = Math.max(instanceSimSimple, instanceSimComplex);
    		} else {
    			instanceSim = instanceSimSimple;
    		}
    	} if (instanceSimComplex>-1){
    		instanceSim = instanceSimComplex;
    	}
//		System.out.println("nameSim: " + nameSim + "  pathSim: " + pathSim +"  commentSim: " + commentSim +" instanceSim: " + instanceSim);
		
//		ColoredTableCellRenderer srcRenderer = nodeSrc.getCellRenderer();
//		ColoredTableCellRenderer trgRenderer = nodeTrg.getCellRenderer();
//		srcRenderer.setSim(nameSim, pathSim, commentSim, instanceSim);
//		trgRenderer.setSim(nameSim, pathSim, commentSim, instanceSim);
//		nodeSrc.repaint();
//		nodeTrg.repaint();
		controller.getMainWindow().getNewContentPane().getLMW2().setSim(nameSim, pathSim, commentSim, instanceSim);		
	}
	
	class PopupTrgListener extends MouseAdapter {
	    public void mousePressed(MouseEvent _event) {
	        maybeShowPopup(_event);
	        if (nodeTrg!=null){
		        if (targetTree.getSelectionPath() != null) {
	        		DefaultMutableTreeNode target = (DefaultMutableTreeNode) targetTree
						.getSelectionPath().getLastPathComponent();
	        		if (target.getUserObject() instanceof Path){
	        			Path bObj = (Path) target.getUserObject();
						nodeTrg.showProperties(bObj);
						if (sourceTree.getSelectionPath() != null){
			        		DefaultMutableTreeNode source = (DefaultMutableTreeNode) sourceTree
							.getSelectionPath().getLastPathComponent();
			        		Path aObj = (Path) source.getUserObject();
							compareProperties(aObj, bObj);
						}
	        		}
		        } else {
	//	    		ColoredTableCellRenderer srcRenderer = nodeSrc.getCellRenderer();
	//	    		ColoredTableCellRenderer trgRenderer = nodeTrg.getCellRenderer();
	//	    		srcRenderer.resetSim();
	//	    		trgRenderer.resetSim();
	//	    		nodeSrc.repaint();
	////	    		nodeTrg.repaint();
		        	nodeTrg.showNothing(target);
		        	controller.getMainWindow().getNewContentPane().getLMW2().resetSim();
		        }
	        }
	    }
	


		public void mouseReleased(MouseEvent _event) {
	        maybeShowPopup(_event);
	    }
	
	    private void maybeShowPopup(MouseEvent _event) {
	    	Class m = MatchresultView2.this.getClass();
	    	if (m.equals(MatchresultView3.class)){
	    		return;
	    	}
	        if (_event.isPopupTrigger()) {
				MatchResult mr = controller.getGUIMatchresult().getMatchResult();
				if (mr==null){
					createCorrespondenceTrg.setEnabled(false);
					setHighestSimValueTrg.setEnabled(false);
					deleteCorrespondenceTrg.setEnabled(false);
					deleteCorrespondencesTrg.setEnabled(false);
	        		removeFragmentCorrespondencesTrg.setEnabled(false);
	        		retainFragmentCorrespondencesTrg.setEnabled(false);
				} else {
		        	if (targetTree.getSelectionPath() != null) {
		        		DefaultMutableTreeNode target = (DefaultMutableTreeNode) targetTree
							.getSelectionPath().getLastPathComponent();
		        		Object bObj = target.getUserObject();
						if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
							// change objects from paths to nodes
							bObj = ((Path)bObj).getLastElement();
						}
						if (sourceTree.getSelectionPath() != null) {
							DefaultMutableTreeNode source = (DefaultMutableTreeNode) sourceTree
									.getSelectionPath().getLastPathComponent();
							Object aObj = source.getUserObject();
							if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
								// change objects from paths to nodes
								aObj = ((Path)aObj).getLastElement();
							}
							float sim = mr.getSimilarity(aObj, bObj);
							if ((sim == MatchResult.SIM_UNDEF)
									|| (sim == MatchResult.SIM_MIN)) {
								createCorrespondenceTrg.setEnabled(true);
								setHighestSimValueTrg.setEnabled(false);
								deleteCorrespondenceTrg.setEnabled(false);		
//								confirmCorrespondenceTrg.setEnabled(true);
							} else {
								createCorrespondenceTrg.setEnabled(false);
								if (sim<MatchResult.SIM_MAX){
									setHighestSimValueTrg.setEnabled(true);
								} else {
									setHighestSimValueTrg.setEnabled(false);
								}
								deleteCorrespondenceTrg.setEnabled(true);
//								confirmCorrespondenceTrg.setEnabled(true);
							}
						} else {
							createCorrespondenceTrg.setEnabled(false);
							setHighestSimValueTrg.setEnabled(false);
							deleteCorrespondenceTrg.setEnabled(false);
//							confirmCorrespondenceTrg.setEnabled(false);
						}
						ArrayList aMatchObjects = mr.getSrcMatchObjects(bObj);
						if (aMatchObjects!=null && aMatchObjects.size()>0){
//							if (aMatchObjects.size()==1){
//								setHighestSimValueTrg.setEnabled(true);
//							}
							deleteCorrespondencesTrg.setEnabled(true);
						} else {
							deleteCorrespondencesTrg.setEnabled(false);
						}
		        	} else {
						createCorrespondenceTrg.setEnabled(false);
						setHighestSimValueTrg.setEnabled(false);
						deleteCorrespondenceTrg.setEnabled(false);
						deleteCorrespondencesTrg.setEnabled(false);
					}     
				}

	        		TreePath targetPath = targetTree.getSelectionPath();
		    		if (targetPath!=null){
		    			Object object = ((DefaultMutableTreeNode)targetPath.getLastPathComponent()).getUserObject();
		    			Element bElement  = null;
		    			if (object instanceof Path){
		    				Path bObj = (Path)object;
		    				bElement =  bObj.getLastElement();
		    			} else if (object instanceof Element){
		    				bElement = (Element) object;
		    			}
			    		if (bElement!=null && (bElement.hasDirectInstancesSimple() || bElement.hasDirectInstancesComplex())){
			    			trgShowInst.setEnabled(true);
			    		} else {
			    			trgShowInst.setEnabled(false);
			    		}
		        		if (mr!=null){			        		
			        		retainFragmentCorrespondencesTrg.setEnabled(true);
		        		}
	        			Enumeration<TreePath> descendants = targetTree.getExpandedDescendants(targetPath);
	        			if (descendants!=null){
	        				trgFoldFragmentChildren.setEnabled(true);
			        		trgUnfoldFragment.setEnabled(true);
			        		if (mr!=null){
			        			removeFragmentCorrespondencesTrg.setEnabled(true);
			        		} else {
			        			removeFragmentCorrespondencesTrg.setEnabled(false);
			        		}
	        			} else {
	        				removeFragmentCorrespondencesTrg.setEnabled(false);
			        		trgUnfoldFragment.setEnabled(false);
	        				trgFoldFragmentChildren.setEnabled(false);
	        			}
		    		} else {
		    			trgShowInst.setEnabled(false);
		        		removeFragmentCorrespondencesTrg.setEnabled(false);
		        		retainFragmentCorrespondencesTrg.setEnabled(false);
		        		trgUnfoldFragment.setEnabled(false);
		        		trgFoldFragmentChildren.setEnabled(false);
		    		}
		        	popupTrg.show(_event.getComponent(),
			                _event.getX(), _event.getY());
	        }
	    }
	}
	
	protected void getObjects(DefaultMutableTreeNode _node, ArrayList _objects) {
		if (_node == null) {
			return;
		}
		_objects.add(_node.getUserObject());
		if (_node.getChildCount() > 0) {
			for (int i = 0; i < _node.getChildCount(); i++) {
				getObjects((DefaultMutableTreeNode) _node.getChildAt(i),
						_objects);
			}
		}
	}

	//	/*
	//	 * set the source tree, create a new tree pane and add Listener
	//	 */
	//	void setSourceTree(ExtJTree _sourceTree) {
	//		if (_sourceTree==null){
	//			return;
	//		}
	//		sourceTree = _sourceTree;
	//		srcPath2TreePath.clear();
	//		setChanged(true);
	//		ArrayList aObjects = new ArrayList();
	//		getObjects( _sourceTree.getRootNode(), aObjects);
	//		MatchResult result = controller.getMatchresult().getMatchResult();
	//		ArrayList bObjects = result.getTrgObjects();
	//// System.out.println(result);
	//		MatchResult subresult = result.restrict(aObjects, bObjects);
	//// System.out.println(subresult);
	//		controller.getMainWindow().getNewContentPane().getLinesComponent().setNewMatchResult(subresult);
	//		buildPath2TreePath(_sourceTree, srcPath2TreePath);
	//
	//		String name = GUIConstants.EMPTY;
	//		String content = null;
	//		String tooltiptext = null;
	//		if (source != null) {
	//			name = "[FRAGMENT]" + GUIConstants.EMPTY + source.getSource().getName();
	//			content = GUIConstants.EMPTY + source.getSource().getType();
	//			tooltiptext = GUIConstants.EMPTY + source.getSource().getName()
	//					+ GUIConstants.COLON_SPACE2
	//					+ source.getSource().getProvider()
	//					+ GUIConstants.SPACE_SMALLER + source.getSource().getUrl()
	//					+ GUIConstants.BIGGER;
	//		}
	//		String labelText;
	//		if (content != null) {
	//			labelText = //GUIConstants.TARGETSCHEMA + GUIConstants.COLON_SPACE +
	//			name + GUIConstants.BRACKET_LEFT + content + GUIConstants.BRACKET_RIGHT;
	//		} else {
	//			labelText = GUIConstants.SOURCESCHEMA + //GUIConstants.COLON_SPACE +
	//					name;
	//		}
	//		sourceTreePane = new DefaultScrollPane(_sourceTree, this);
	//		JPanel sourcePanel = new JPanel(new BorderLayout());
	//		JLabel sourceLabel = new JLabel(labelText);
	//		sourceLabel.setLayout(new BorderLayout());
	//		JButton button = new JButton(new ImageIcon(GUIConstants.ICON_CLOSE));
	//		button.setToolTipText(GUIConstants.CLOSE_SCHEMA);
	//		button.setMargin(ManagementPane.BUTTON_INSETS);
	//		sourceLabel.add(button, BorderLayout.EAST);
	//		button.addActionListener(new ActionListener() {
	//			public void actionPerformed(ActionEvent _event) {
	//				MatchresultView2.controller.closeSchema(true, false);
	//			}
	//		});
	//		sourceLabel.setToolTipText(tooltiptext);
	//		sourceLabel.setHorizontalAlignment(SwingConstants.CENTER);
	//		sourcePanel.add(sourceLabel, BorderLayout.NORTH);
	//		sourcePanel.add(sourceTreePane, BorderLayout.CENTER);
	//		// setLeftComponent(sourceTreePane);
	//		setLeftComponent(sourcePanel);
	//		setDividerLocation(0.5);
	//	}
	/*
	 * set the source tree, create a new tree pane and add Listener
	 */
	private void setTargetTree(ExtJTree _targetTree, Graph _Graph) {
		targetTree = _targetTree;
		String name = GUIConstants.EMPTY;
//		String content = null;
		String tooltiptext = null;
		if (_Graph != null) {
			name = GUIConstants.EMPTY + _Graph.getSource().getName();
//			content =  Source.typeToString(_Graph.getSource().getType());
			tooltiptext = GUIConstants.EMPTY + _Graph.getSource().getName()
					+ GUIConstants.COLON_SPACE2
					+ _Graph.getSource().getProvider();
//					+ GUIConstants.SPACE_SMALLER + _Graph.getSource().getUrl()
//					+ GUIConstants.BIGGER;
		}
		String labelText = name;
//		if (content != null) {
//			labelText = //GUIConstants.TARGETSCHEMA + GUIConstants.COLON_SPACE +
//			name + GUIConstants.BRACKET_LEFT + content + GUIConstants.BRACKET_RIGHT;
//		} else {
//			labelText = GUIConstants.TARGETSCHEMA + //GUIConstants.COLON_SPACE +
//					name;
//		}
		targetTreePane = new DefaultScrollPane(_targetTree, this);
		if (controller.getMainWindow()!=null){
			LinedMatchresultView2 view2 = controller.getMainWindow().getNewContentPane().getLMW2();
			view2.getLinesComponent().setHighlightedTrg(null);
		}
		targetTreePane.getVerticalScrollBar().setUI(new MyMetalScrollBarUI());
		targetTreePane.getHorizontalScrollBar().addAdjustmentListener(new MyAdjustmentListener());
		targetTreePane.getVerticalScrollBar().addAdjustmentListener(new MyAdjustmentListener());
		JPanel targetPanel = new JPanel(new BorderLayout());
		targetLabel = new JLabel(labelText, SwingConstants.CENTER);
		targetLabel.setLayout(new BorderLayout());
//		JButton button = new JButton(new ImageIcon(GUIConstants.ICON_CLOSE));
		JButton button = new JButton(Controller.getImageIcon(GUIConstants.ICON_CLOSE));
		button.setToolTipText(GUIConstants.CLOSE_SCHEMA);
		button.setMargin(ManagementPane.BUTTON_INSETS);
		targetLabel.add(button, BorderLayout.EAST);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				controller.closeSchema(false, false);
			}
		});
		//		targetLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		targetLabel.setToolTipText(tooltiptext);
		targetPanel.add(targetLabel, BorderLayout.NORTH);
		targetPanel.add(targetTreePane, BorderLayout.CENTER);
		if (!(this instanceof MatchresultView3)){
			targetPanel.add(initSearch(targetTree, false), BorderLayout.SOUTH);
		}
		//		setRightComponent(targetTreePane);
		setRightComponent(targetPanel);
		setDividerLocation(0.5);
		
	    //Add listener to components that can bring up popup menus.
	    targetTree.addMouseListener(new PopupTrgListener());
	}

	/*
	 * get the selected Fragment as VertexImpl from the given tree
	 */
	public static ArrayList<Element> getSelectedFragments(ExtJTree _tree) {
		ArrayList<Element> list = new ArrayList<Element>();
		TreePath[] paths = _tree.getSelectionPaths();
		if (paths != null) {
			for (int i = 0; i < paths.length; i++) {
				TreePath path = paths[i];
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				if (selectedNode.getUserObject() instanceof Path) {
					Path pathV = ((Path) selectedNode.getUserObject());
					Element v = pathV.getLastElement();
					list.add(v);
					//					list.add(pathV);
				} else if (selectedNode.getUserObject() instanceof Element) {
					Element v = ((Element) selectedNode.getUserObject());
					list.add(v);
				}
			}
			return list;
		}
		return null;
	}

	/*
	 * get the selected Fragmentpath as ArrayList from the given tree
	 */
	public static ArrayList<Path> getSelectedFragmentPaths(ExtJTree _tree) {
		ArrayList<Path> list = new ArrayList<Path>();
		TreePath[] paths = _tree.getSelectionPaths();
		if (paths != null) {
			for (int i = 0; i < paths.length; i++) {
				TreePath path = paths[i];
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				if (selectedNode.getUserObject() instanceof Path) {
					list.add((Path)selectedNode.getUserObject());
				} else if (selectedNode.getUserObject() instanceof Element) {
//					Element v = ((Element) selectedNode.getUserObject());
					//TODO get all up paths
//					list.add(v);
					//					list.add(pathV);
				}
			}
			return list;
		}
		return null;
	}

	/*
	 * returns an integer representing the tree, that was last selected
	 */
	public int getLastSelectedTree() {
		return lastSelectedTree;
	}

	/*
	 * sets the integer representing the tree, that was last selected
	 */
	public void setLastSelectedTree(int _tree) {
		lastSelectedTree = _tree;
	}
	
	public int getView(){
		return view;
	}

	public void setView(int _view){
		if (view == _view) {
			return;
		}
		// set new view
		view = _view;
		if (source!=null){	
			setSourceSchema(source);
		}
		if (target!=null){	
			setTargetSchema(target);
		}
	}
	
	/*
	 * return a vector containing all fragment pairs
	 */
	public ArrayList getFragmentPairs() {
		return fragmentPairs;
	}

	/*
	 * return source treepane
	 */
	public JScrollPane getSourceTreePane() {
		return sourceTreePane;
	}

	/*
	 * return the Hashmap with key: source path and value: corresponding tree
	 * path
	 */
	public HashMap<Object, TreePath>  getSrcPath2TreePath() {
		return srcPath2TreePath;
	}

	/*
	 * return target treepane
	 */
	public JScrollPane getTargetTreePane() {
		return targetTreePane;
	}

	/*
	 * return the Hashmap with key: target path and value: corresponding tree
	 * path
	 */
	public HashMap<Object, TreePath> getTrgPath2TreePath() {
		return trgPath2TreePath;
	}

	/*
	 * return the controller
	 */
	public Controller getController() {
		return controller;
	}

	/*
	 * @return suggestes Fragments
	 */
	public ArrayList getSuggestedFragments() {
		return suggestedFragments;
	}

	/*
	 * delete the suggested fragments
	 */
	public void deleteSuggestedFragments() {
		suggestedFragments.clear();
		controller.getMatchresultView().setChanged(true);
	}

	/**
	 * @return Returns the changed.
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * The changed to set.
	 */
	public void setChanged(boolean _changed) {
		if (changed==_changed){
			return;
		}
		changed = _changed;
		if (changed) {
			parent.linesComponent.repaint();
		}
	}

	public void setSourceLabel(Graph _Graph) {
		String name = GUIConstants.EMPTY;
//		String content = null;
		String tooltiptext = null;
		if (_Graph != null) {
			name = GUIConstants.EMPTY + _Graph.getSource().getName();
//			content = Source.typeToString( _Graph.getSource().getType());
			tooltiptext = GUIConstants.EMPTY + _Graph.getSource().getName()
					+ GUIConstants.COLON_SPACE2
					+ _Graph.getSource().getProvider();
//					+ GUIConstants.SPACE_SMALLER + _Graph.getSource().getUrl()
//					+ GUIConstants.BIGGER;
		}
		String labelText = name;
//		if (content != null) {
//			labelText = //GUIConstants.TARGETSCHEMA + GUIConstants.COLON_SPACE +
//			name + GUIConstants.BRACKET_LEFT + content + GUIConstants.BRACKET_RIGHT;
//		} else {
//			labelText = GUIConstants.SOURCESCHEMA + //GUIConstants.COLON_SPACE +
//					name;
//		}
		sourceLabel.setToolTipText(tooltiptext);
		sourceLabel.setText(labelText);
	}

	public void setTargetLabel(Graph _Graph) {
		String name = GUIConstants.EMPTY;
//		String content = null;
		String tooltiptext = null;
		if (_Graph != null) {
			name = GUIConstants.EMPTY + _Graph.getSource().getName();
//			content = Source.typeToString(_Graph.getSource().getType());
			tooltiptext = GUIConstants.EMPTY + _Graph.getSource().getName()
					+ GUIConstants.COLON_SPACE2
					+ _Graph.getSource().getProvider();
//					+ GUIConstants.SPACE_SMALLER + _Graph.getSource().getUrl()
//					+ GUIConstants.BIGGER;
		}
		String labelText = name;
//		if (content != null) {
//			labelText = //GUIConstants.TARGETSCHEMA + GUIConstants.COLON_SPACE +
//			name + GUIConstants.BRACKET_LEFT + content + GUIConstants.BRACKET_RIGHT;
//		} else {
//			labelText = GUIConstants.SOURCESCHEMA + //GUIConstants.COLON_SPACE +
//					name;
//		}
		targetLabel.setToolTipText(tooltiptext);
		targetLabel.setText(labelText);
	}
	
	private void setCorrespondenceType(String type){
		if (targetTree.getSelectionPath() != null) {
			DefaultMutableTreeNode target = (DefaultMutableTreeNode) targetTree
				.getSelectionPath().getLastPathComponent();
			MatchResult mr = controller.getGUIMatchresult().getMatchResult();
			System.out.println(mr.getMatchCount());
			Object bObj = target.getUserObject();
			if (sourceTree.getSelectionPath() != null) {
				DefaultMutableTreeNode source = (DefaultMutableTreeNode) sourceTree
						.getSelectionPath().getLastPathComponent();
				Object aObj = source.getUserObject();				
				if (controller.getView()==MainWindow.VIEW_GRAPH && mr.containsOnlyNodes()){
					// change objects from paths to nodes
					aObj = ((Path)aObj).getLastElement();
					bObj = ((Path)bObj).getLastElement();
				}	
				
			}
			controller.getManagementPane().updateMatchresult(mr);
			controller.setNewMatchResult(mr, /*true,*/ false);
			controller.getMainWindow().clearMatchresultView();
			setChanged(true);			
		}
	}

	/*
	 * PropertieJPanel extends JPanel
	 */
	private class PropertieJPanel extends JPanel {
		TableModel tm;
		JTable table;
		Object current;
		String[][] data;
		static final int MAX = 4; 
		ColoredTableCellRenderer renderer;
		boolean src = true;
		Graph graph = null;
		String[][] dataDefault = new String[MAX][2];
		TableColumn leftCol, rightCol;
		
		
		public PropertieJPanel(boolean src, Graph graph) {
			super(new BorderLayout());
			this.graph = graph;
			this.src = src;
			
			data = new String[MAX][2];
			
			//crate table column model
			DefaultTableColumnModel cm = new DefaultTableColumnModel();
			if (src){
				leftCol = new TableColumn(0, 50);
				rightCol = new TableColumn(1, 250);
			} else {
				leftCol = new TableColumn(0, 250);				
				rightCol = new TableColumn(1, 50); 
			}
			cm.addColumn(leftCol);
			cm.addColumn(rightCol);
			
			if (graph!=null){
				leftCol.setPreferredWidth(150);
				rightCol.setPreferredWidth(150);
				int i1 = 0;
				int i2 = 1;
				if (!src){
					i1=1;
					i2=0;
				}
					dataDefault[0][i1] = "All Nodes/Paths";
					dataDefault[1][i1] = "Inner Nodes/Paths";
					dataDefault[2][i1] = "Leaf Nodes/Paths";
					dataDefault[3][i1] = "Shared/Root Nodes";

					dataDefault[0][i2] = graph.getElementCount() + " / " + graph.getAllPaths().size();
					dataDefault[1][i2] = graph.getInnerNodesCount() + " / " + graph.getInnerPathsCount();
					dataDefault[2][i2] = graph.getLeafNodesCount() + " / " + graph.getLeafPathsCount() ;
					dataDefault[3][i2] = graph.getSharedNodesCount() + " / " + graph.getRootNodesCount();

				for (int i = 0; i < data.length; i++) {
					for (int j = 0; j < data[0].length; j++) {
						data[i][j] = dataDefault[i][j];
					}
				}
			}

			//crate table model
			tm = new TableModel(data);
			// create table and fill ContentPane
			table = new JTable(tm, cm);
			renderer = new ColoredTableCellRenderer(src);
			table.setDefaultRenderer(Object.class, renderer);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setRowSelectionInterval(0, 0);
			// create a scrollpane containing this table
			add(table, BorderLayout.NORTH);
		}

		ColoredTableCellRenderer getCellRenderer(){
			return renderer;
		}
		
		/*
		 * empty all entries
		 */
		public void showNothing(Graph graph) {
			current = null;
			leftCol.setPreferredWidth(150);
			rightCol.setPreferredWidth(150);
//			for (int i = 0; i < MAX; i++) {
//				data[i][0] = GUIConstants.EMPTY;
//				data[i][1] = GUIConstants.EMPTY;
//			}
			// change: instead of being empty change to schema information
			if (graph==null){
				if (src){
					graph = source;
				} else {
					graph = target;
				}
			}
			
			
			if ( graph!=null && (this.graph==null || (this.graph!=null && !this.graph.equals(graph)))){
				this.graph = graph;
				dataDefault[0][1] = graph.getElementCount() + "/" + graph.getAllPaths().size();
				dataDefault[1][1] = graph.getInners().size() + "/" + graph.getInnerPaths().size() ;
				dataDefault[2][1] = graph.getLeaves().size() + "/" + graph.getLeafPaths().size() ;
				dataDefault[3][1] = graph.getShared().size() + "/" + graph.getRoots().size();
			}
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[0].length; j++) {
					data[i][j] = dataDefault[i][j];
				}
			}

			
			repaint();
		}

		/*
		 * show properties of a schema
		 */
		public void showProperties(Path path) {
			if (current == path) {
				return;
			}
			if (src){
				leftCol.setPreferredWidth(50);
				rightCol.setPreferredWidth(250);
			} else {
				leftCol.setPreferredWidth(250);
				rightCol.setPreferredWidth(50);
			}
			
			
			int i=-1, j=-1;
			if (src){
				i=0;
				j=1;
			} else {
				i=1;
				j=0;
			}
			
			Element element = path.getLastElement();
			data[0][i] = GUIConstants.N_NAME;
			data[0][j] = element.getName();
			
			data[1][i] = GUIConstants.N_PATH;
			data[1][j] = path.toNameString();
			
			if (element.getComment()!=null){
				data[2][i] = GUIConstants.N_COMMENT;
				data[2][j] = element.getComment();
			} else {
				data[2][i] = GUIConstants.EMPTY;
				data[2][j] = GUIConstants.EMPTY;
			}
			
			if (element.hasDirectInstancesSimple()){
				data[3][i] = GUIConstants.N_INSTANCE_EXAMPLE;
				String inst = element.getDirectInstancesSimple().get(0).toString();
				data[3][j] = inst;
			} else 	if (element.hasDirectInstancesComplex()){
				data[3][i] = GUIConstants.N_INSTANCE_EXAMPLE;
				Set attributes = element.getDirectInstancesComplex().keySet();
				String inst=GUIConstants.EMPTY;
				for (Iterator iterator = attributes.iterator(); iterator
						.hasNext();) {
					String attribute = (String) iterator.next();
					ArrayList<String> values = element.getDirectInstancesComplex().get(attribute);
					inst+=values.get(0)+" ";
				}
				data[3][j] = inst;
			} else {
				data[3][i] = GUIConstants.EMPTY;
				data[3][j] = GUIConstants.EMPTY;
			}

			repaint();
		}

		/**
		 * @return Returns the table.
		 */
		public JTable getTable() {
			return table;
		}
	}
	
	class TableModel extends AbstractTableModel {
		private String[][] data;

		/*
		 * Constructor of TableModel
		 */
		public TableModel(String[][] _data) {
			super();
			data = _data;
		}

		/*
		 * returns true in case a cell is editable
		 */
		public boolean isCellEditable(int _rowIndex, int _columnIndex) {
//			if ((_columnIndex == 1) && ((_rowIndex == 0) || (_rowIndex == 1))) {
//				return true;
//			}
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
				} else {
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
//		final int rowName = 0, rowPath = 1, rowComment= 2, rowInstance = 3;
//		float nameSim = -1, pathSim=-1, commentSim=-1, instanceSim=-1;
		
		boolean source=true;
		
		/*
		 * Constructor ColoredTableCellRenderer
		 */
		public ColoredTableCellRenderer(boolean src) {
			super();
			source = src;
		}
		
//		void setSim(float _nameSim, float _pathSim, float _commentSim, float _instanceSim){
//			nameSim = _nameSim;
//			pathSim = _pathSim;
//			commentSim = _commentSim;
//			instanceSim = _instanceSim;			
//		}
//		
//		void resetSim(){
//			nameSim = -1;
//			pathSim = -1;
//			commentSim = -1;
//			instanceSim = -1;
//		}

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
			if (source){
			
			if (_column == 0) {
				label.setFont(MainWindow.FONT12_BOLD);
			} else {
				label.setFont(MainWindow.FONT11);
				label.setToolTipText(label.getText());
//				if ((_row == 0) || (_row == 1)) {
//					label.setBackground(MainWindow.GLOBAL_BACKGROUND);
//				}
//				switch (_row) {
//				case rowName:
//					if (nameSim>-1){
////						label.setBackground(Color.green);
//						label.setBackground(Line.getColorForSim(nameSim));
//					}
//					break;
//				case rowPath:
//					if (pathSim>-1){
////						label.setBackground(Color.red);
//						label.setBackground(Line.getColorForSim(pathSim));
//					}
//					break;
//				case rowComment:
//					if (commentSim>-1){
////						label.setBackground(Color.blue);
//						label.setBackground(Line.getColorForSim(commentSim));
//					}
//					break;
//				case rowInstance:
//					if (instanceSim>-1){
////						label.setBackground(Color.yellow);
//						label.setBackground(Line.getColorForSim(instanceSim));
//					}
//					break;
//
//				default:
//					break;
				}
			} else {
				if (_column == 1) {
					label.setFont(MainWindow.FONT12_BOLD);
				} else {
					label.setFont(MainWindow.FONT11);
					label.setToolTipText(label.getText());
				}
			}
			return label;
		}
	}
}