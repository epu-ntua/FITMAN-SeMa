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

package de.wdilab.coma.gui.extjtree;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.view.MatchresultView2;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Path;

/**
 * This class determines the information of the model tree that is shown including the 
 * coloring (background, font color, border) and an icons to show if instances exist
 * for the element. 
 * 
 * @author Sabine Massmann
 */
public class ExtJTreeCellRenderer extends DefaultTreeCellRenderer {
	private int side;
	private MatchresultView2 matchresultView;
	private Color generalBackground;
	private Color highlightBackground;
	HashMap<Path, String> pathstrings = new HashMap<Path, String>();
	HashMap<Element, String> tooltips = new HashMap<Element, String>();
	HashSet<DefaultMutableTreeNode> nodesToHighlight = null;
	HashMap<Element, HashSet<Path>> map = null;
	HashSet<Path> trgPaths = null;
	HashSet<Object> shared = new HashSet<Object>(), notShared = new HashSet<Object>();
	
	/*
	 * Constructor of ExtJTreeCellRenderer
	 */
	public ExtJTreeCellRenderer(int _side, MatchresultView2 _matchresultView) {
		super();
		side = _side;
		matchresultView = _matchresultView;
		generalBackground = MainWindow.GLOBAL_BACKGROUND;
		highlightBackground = MainWindow.HIGHLIGHT_BACKGROUND;
		setOpenIcon(null);
		setClosedIcon(null);
		setLeafIcon(null);
		setOpaque(true);
		setIcon(null);
	}
	
	public void setHighlighted(HashSet<DefaultMutableTreeNode> nodesWithString){
		nodesToHighlight = nodesWithString;
		matchresultView.repaint();
	}
	
	public void setNotRelevantMap(HashMap<Element, HashSet<Path>> map){
		this.map=map;
		if (side==ExtJTree.SOURCE){
			System.out.print("SOURCE  ");
		} else if (side==ExtJTree.TARGET){
			System.out.print("TARGET  ");
		}
		if (map==null){
			System.out.println("setNotRelevantMap null ");
		} else {
			System.out.println("setNotRelevantMap to value");
		}
	}
	
	public void setNotRelevantTrgPaths(Element element){
		if (element==null || map==null){
			trgPaths=null;
		} else {
			trgPaths = map.get(element);
		}		
	}

	// SHORT (only one label)
	/*
	 * get for an object the component, that will be shown in the tree
	 */
	public Component getTreeCellRendererComponent(JTree _tree, Object _value,
			boolean _selected, boolean _expanded, boolean _leaf, int _row,
			boolean _hasFocus) {
		JLabel textLabel = new JLabel(GUIConstants.EMPTY);
		textLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		textLabel.setIconTextGap(1);
		textLabel.setOpaque(true);
		textLabel.setFont(MainWindow.TREE_FONT_TEXT);
		textLabel.setBackground(generalBackground);
		textLabel.setForeground(MainWindow.FOREGROUND);
		String toolTip = null;
		String type = null;
		boolean hasInstancesSimple = false;
		boolean hasInstancesComplex = false;
		if (_value instanceof DefaultMutableTreeNode){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) _value;
			if (nodesToHighlight!=null && nodesToHighlight.contains(node)){
				textLabel.setBackground(highlightBackground);
				textLabel.setBorder(BorderFactory.createLineBorder(highlightBackground));
			} else {
					// creaty empty border to not get problems with highlighted border
					textLabel.setBorder(BorderFactory.createEmptyBorder(1,8,1,1));
					
			}
				if (node.getUserObject() instanceof Path) {
					Path path = (Path) node.getUserObject();
					Element el = path.getLastElement();
					
					if (matchresultView.getSuggestedFragments().contains(path)
							|| matchresultView.getSuggestedFragments().contains(el)) {
						textLabel
								.setBackground(MainWindow.SUGGESTED_FRAGMENT_BACKGROUND);
					}
					if (el != null) {
						textLabel.setText(el.getName());
						if (el.getType() != null) {
							type = el.getType();
						}
						hasInstancesSimple = el.hasDirectInstancesSimple();
						hasInstancesComplex = el.hasDirectInstancesComplex();

						toolTip = getToolTip(el, path);
						if (map!=null && side==ExtJTree.SOURCE){
							// test if part of the not relevant concepts
							if (map.containsKey(el)){
								textLabel.setBorder(BorderFactory.createLineBorder(Color.red));
							}
						}
						if (trgPaths!=null && side==ExtJTree.TARGET){
							// test if part of the not relevant concepts
							if (trgPaths.contains(path)){
								textLabel.setBorder(BorderFactory.createLineBorder(Color.green));
							}
						}
					}
				} else if (node.getUserObject() instanceof Element) {
					Element el = (Element)node.getUserObject();
					if (el != null) {
						textLabel.setText(el.getName());
						if (el.getType() != null) {
							type = el.getType();
						}
						hasInstancesSimple = el.hasDirectInstancesSimple();
						hasInstancesComplex = el.hasDirectInstancesComplex();

						toolTip = getToolTip(el, null);		
					}
				}
				boolean isShared = false;
				if (shared.contains(_value)){
					isShared = true;
				} else if (notShared.contains(_value)){
//					isShared = false;
				} else {
					// not yet registered
					Element el = null;
					if (node.getUserObject() instanceof Path) {
						Path path = (Path) node.getUserObject();
						el = path.getLastElement();
					} else {
						el = (Element)node.getUserObject();
					}

					Graph graph = null;
					if (side==ExtJTree.SOURCE){
						graph = matchresultView.getController().getGUIMatchresult().getSourceGraph();
					} else if (side==ExtJTree.TARGET){
						graph = matchresultView.getController().getGUIMatchresult().getTargetGraph();
					}
					if (graph!=null && el!=null && !graph.isRoot(el)){
						isShared = graph.isShared(el);
					}
					if (isShared){
						shared.add(_value);
					} else {
						notShared.add(_value);
					}
				}
				if (isShared){
					textLabel.setFont(MainWindow.TREE_FONT_TEXT_ITALIC);
//					textLabel.setIcon(Controller.getImageIcon(GUIConstants.ICON_INSTANCE_C));
				} else {
//					textLabel.setIcon(Controller.getImageIcon(GUIConstants.ICON_INSTANCE_C));
				}
		}
		if (hasInstancesSimple && !hasInstancesComplex){
			textLabel.setIcon(Controller.getImageIcon(GUIConstants.ICON_INSTANCE_S));
		} else if (!hasInstancesSimple && hasInstancesComplex){
			textLabel.setIcon(Controller.getImageIcon(GUIConstants.ICON_INSTANCE_C));
		} else if (hasInstancesSimple && hasInstancesComplex) {
			textLabel.setIcon(Controller.getImageIcon(GUIConstants.ICON_INSTANCE));
		}
		
		if (_selected) {
			textLabel.setBackground(MainWindow.SELECTED_BACKGROUND);
		}
		if (textLabel.getText() == null) {
			textLabel.setText(GUIConstants.EMPTY);
		} else if ((type != null) && (type.length() > 0)) {
			textLabel
					.setText(textLabel.getText() + GUIConstants.COLON_SPACE2 + type);
		}
		textLabel.setToolTipText(toolTip);
//		if ((toolTip != null) && (toolTip.length() > 0)
//				&& !toolTip.equals(Element.OBJ_KIND_ELEMENT)
//				&& !toolTip.equals(Element.OBJ_KIND_ELEMTYPE)) {
//			textLabel.setToolTipText(textLabel.getText() + GUIConstants.BRACKET_LEFT
//					+ toolTip + GUIConstants.BRACKET_RIGHT);
//		} else {
//			textLabel.setToolTipText(textLabel.getText());
//		}		
//		if (_row!=0 || _expanded){
//			Rectangle rec = _tree.getRowBounds(_row);
//			System.out.println(_value.toString() + "\t" + rec);
//		}
		return textLabel;
	}
	
	
	String getToolTip(Element _element, Path path){
		String toolTip = tooltips.get(_element);
		if (path==null && toolTip!=null){
				return toolTip;
		} 
		if (toolTip==null){
			toolTip = "<html>";
			if (_element.getName()!=null){
				toolTip+= "<b>Name</b>: " +_element.getName();
			}
			if (_element.getComment()!=null){
				if (toolTip.length()>6){
					toolTip += "<br>";
				}
				if (_element.getComment().length()>50){
					toolTip+= "<b>Comment</b>: " + _element.getComment().substring(0, 50) + "...";
				} else {
					toolTip+= "<b>Comment</b>: " + _element.getComment();
				}
			}
	
//			if (_element.hasDirectInstancesSimple()){	
//				if (toolTip.length()>6){
//					toolTip += "<br>";
//				}
//				toolTip +="<b>Instances</b> (Simple) [" + _element.getDirectInstancesSimple().size() +  "]: ";
//				String instancesExample = _element.getDirectInstancesSimple().get(0).toString();
//				if (_element.getDirectInstancesSimple().size()>1){	
//					instancesExample += " and "  + _element.getDirectInstancesSimple().get(1) ;
//				}
//				if (_element.getDirectInstancesSimple().size()>2){
//					instancesExample = " e.g. "+ instancesExample;
//				}
//				toolTip += instancesExample;
//
//			}
//			if (_element.getDirectInstancesComplex().size()>0){
//				if (toolTip.length()>6){
//					toolTip += "<br>";
//				}
//				toolTip +="<b>Instances</b> (Complex): " + _element.getDirectInstancesComplex().size() + " attributes, up to " + _element.getDirectInstancesComplexMaxSize() + " values";
//			}
			
			tooltips.put(_element, toolTip);
		}
		if (path!=null){
			String pathString = pathstrings.get(path);
			if (pathString==null){
				pathString = path.toString();
//				pathString = GUIConstants.EMPTY;
//				for (int i = 0; i < path.size(); i++) {
//					if (i>0){
//						pathString+="<b>.</b>";
//					}
//					Element current = (Element) path.get(i);
//					pathString+=current.getLabel();
//				}
				pathstrings.put(path, pathString);
			}
			if (toolTip.length()>6){
				toolTip += "<br>";
			}
			toolTip += "<b>Path</b>: " + pathString;
		}
		toolTip+="</html>";
		return toolTip;
	}
	

	// LONG (two label inside a Panel)
	/*
	 * get for an object the component, that will be shown in the tree
	 */
	//	public Component getTreeCellRendererComponent(JTree tree, Object value,
	//			boolean selected, boolean expanded, boolean leaf, int row,
	//			boolean hasFocus) {
	//		JPanel labelPanel = new JPanel(new BorderLayout(6, 0));
	//		labelPanel.setBackground(Color.white);
	//		JLabel textLabel = new JLabel(GUIConstants.EMPTY);
	//		textLabel.setHorizontalTextPosition(SwingConstants.LEFT);
	//		textLabel.setIconTextGap(1);
	//		// textLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));
	//		JLabel typeLabel = new JLabel(GUIConstants.EMPTY);
	//		typeLabel.setHorizontalTextPosition(SwingConstants.LEFT);
	//		labelPanel.add(textLabel, BorderLayout.CENTER);
	//		labelPanel.add(typeLabel, BorderLayout.EAST);
	//		textLabel.setOpaque(true);
	//		typeLabel.setOpaque(true);
	//		labelPanel.setOpaque(true);
	//		textLabel.setFont(TREE_FONT_TEXT);
	//		typeLabel.setFont(TREE_FONT_TYPE);
	//		setOpaque(true);
	//		setIcon(null);
	//		typeLabel.setBackground(lightBackground);
	//		typeLabel.setForeground(Color.GRAY);
	//		textLabel.setBackground(Color.white);
	//		textLabel.setForeground(Color.black);
	//		if (value instanceof DefaultMutableTreeNode
	//				&& ((DefaultMutableTreeNode) value).getUserObject() instanceof ArrayList)
	// {
	//			ArrayList path = (ArrayList) ((DefaultMutableTreeNode) value)
	//					.getUserObject();
	//			VertexImpl userObject = (VertexImpl) path.get(path.size() - 1);
	//			if (side == MatchresultView.SOURCE) {
	//				HashSet paths = JLinesComponent
	//						.getForSourcePathFragmentMatches(path, matchresultView);
	//				if (paths != null)
	//					textLabel.setBackground(usedFragmentBackground);
	//				//
	// textLabel.setBorder(BorderFactory.createLineBorder(fragmentBackground,2));
	//			} else {
	//				HashSet paths = JLinesComponent
	//						.getForTargetPathFragmentMatches(path, matchresultView);
	//				if (paths != null)
	//					textLabel.setBackground(usedFragmentBackground);
	//				//
	// textLabel.setBorder(BorderFactory.createLineBorder(fragmentBackground,2));
	//			}
	//			if (matchresultView.getSuggestedFragments().contains(path))
	//				textLabel.setBackground(suggestedFragmentBackground);
	//			if (userObject != null) {
	//				Element el = ((Element) userObject.getObject());
	//				textLabel.setText(el.getTextRep());
	//				if (el.getType() != null)
	//					typeLabel.setText(el.getType());
	//			}
	//			labelPanel.setToolTipText(textLabel.getText() + GUIConstants.COLON
	//					+ typeLabel.getText());
	//		} else {
	//			textLabel.setFont(TREE_FONT_ROOT);
	//			textLabel.setText(((DefaultMutableTreeNode) value).toString());
	//		}
	//		if (selected)
	//			textLabel.setBackground(selectedBackground);
	//		return labelPanel;
	//	}
	/*
	 * The generalBackground to set.
	 */
	public void setGeneralBackground(Color _generalBackground) {
		generalBackground = _generalBackground;
	}

}