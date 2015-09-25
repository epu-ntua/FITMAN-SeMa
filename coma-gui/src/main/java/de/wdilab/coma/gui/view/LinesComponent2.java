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

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;
import de.wdilab.coma.gui.extjtree.ExtJTree;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;
import de.wdilab.coma.structure.Path;

/**
 * This class calculates the lines of the current matching. The lines are from a source
 * to a target model. If an element is selected its corresponding lines are searched for
 * and displayed often with its similarity values.
 * 
 * @author Sabine Massmann
 */
public class LinesComponent2 extends JComponent {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	static final int Y_ROW_0 = 8;
	static final int ALL = 0;
	static final int SEL_TRG = 1;
	static final int SEL_SRC = 2;
	static final int SEL_MIDDLE = 3;
	static final int NONE = -1;
	static final int OFFSET_GLOBAL = 0;
	static final int STATE_GLOBAL = 0;
	static final int STATE_FRAGMENT = 2;
	static final int STATE_SUGGESTION = 3;
	static final int STATE_LIGHT = 4;
	// needed for just showing two number behind the comma of the simValue 
	// (like X.XX)
	static final int LENGTH = 100;
	//----------------------------------------------
	Controller controller;
	ExtJTree sourceTree;
	ExtJTree targetTree;
	int lastDrawing = NONE;
	int scrolledRow = NONE;
	ArrayList<Line> linesList;
	boolean alreadyScrolled = false;
	HashMap<TreePath, ArrayList<Match>> srcMatches;
	HashMap<TreePath, ArrayList<Match>> trgMatches;
	HashMap srcMatches_fragment;
	HashMap trgMatches_fragment;
	MatchResult result;
	
	HashMap<Integer,DefaultMutableTreeNode> nodesToHighlightSrc = null;
	HashMap<Integer,DefaultMutableTreeNode> nodesToHighlightTrg = null;

	/*
	 * Constructor of JLinesComponent size of it is given through
	 * (xStart,yStart) and height + width
	 */
	public LinesComponent2(Controller _controller) {
		super();
		linesList = new ArrayList<Line>();
		setOpaque(false);
		controller = _controller;
		srcMatches = new HashMap<TreePath, ArrayList<Match>>();
		trgMatches = new HashMap<TreePath, ArrayList<Match>>();
		srcMatches_fragment = new HashMap();
		trgMatches_fragment = new HashMap();
	}

	/*
	 * paint this component by painting the component and the current lines
	 */
	public void paint(Graphics _graphics) {
		try {
			super.paint(_graphics);
			try {
				drawCurrentLines((Graphics2D) _graphics);
				ExtJTree tree = controller.getMatchresultView().getSourceTree();
				JScrollPane pane = controller.getMatchresultView().getSourceTreePane();
				drawHighlights((Graphics2D) _graphics, nodesToHighlightSrc, tree, pane);
				tree = controller.getMatchresultView().getTargetTree();
				pane = controller.getMatchresultView().getTargetTreePane();
				drawHighlights((Graphics2D) _graphics, nodesToHighlightTrg, tree, pane);
			} catch (RuntimeException e) {
			}
		} catch (NullPointerException e) {
			System.out.println("LinesComponent2.paint() Error " + e.getMessage());
		}
	}
	
	public void drawHighlights(Graphics2D _graphics, HashMap<Integer,DefaultMutableTreeNode> nodesToHighlight,
			ExtJTree tree, JScrollPane pane){
		if (nodesToHighlight==null){
			return;
		}
//		JScrollBar vSB = controller.getMatchresultView().getSourceTreePane().getVerticalScrollBar();
//		Rectangle r = vSB.getBounds();
//		System.out.println("Rectangle vSB : "+ r);				
//		JScrollBar vTB =  controller.getMatchresultView().getTargetTreePane().getVerticalScrollBar();
//		r = vTB.getBounds();
//		System.out.println("Rectangle vTB : "+ r);
		JScrollBar bar = pane.getVerticalScrollBar();
    	Rectangle boundTree = tree.getBounds();
    	Rectangle boundVisible = pane.getVisibleRect();    	
    	// for transformation: graph height to view height (=scrollbar height);
//    	boundVisible = boundVisible -2*buttonMin-((JButton)current).getWidth();

//    	System.out.println("**************");
    	_graphics.setColor(MainWindow.HIGHLIGHT_BACKGROUND);
    	_graphics.setStroke(new BasicStroke(2));
    	int x = Math.round((float)boundVisible.getWidth())-bar.getWidth()+1;
    	if (controller.getMatchresultView().getTargetTree().equals(tree)){
    		x += controller.getMatchresultView().getDividerLocation() + 9;
    	}

    	int xwidth = x+bar.getWidth()-3;
		ScrollBarUI ui = bar.getUI();
		int buttonMin = 0;
		// a button (increase or decrease -> have the same height)
		Component current = bar.getComponent(0);
		if (current instanceof JButton){
			buttonMin = ((JButton)current).getHeight()+3;
		}
    	
		if (ui!=null && ui instanceof MyMetalScrollBarUI){
			MyMetalScrollBarUI test = (MyMetalScrollBarUI)ui;
			Rectangle bounds = test.getThumbBounds();
//			System.out.println(" bounds: " + bounds);
			
//	    	int minY=0;  // above the scroll up button
	    	int minY=buttonMin-1; // e.g.,17 first line to draw

//	    	int maxY=(int)boundVisible.getHeight()-buttonMin-1; // beneath the scroll down button
	    	// assumption both scroll button are the same size
	    	int maxY=(int)boundVisible.getHeight()-2*buttonMin+1;  // last line to draw
	    	
//	    	_graphics.drawLine(x, minY, xwidth, minY); 
//	    	_graphics.drawLine(x, maxY, xwidth, maxY);
	    	
	    	double diff = (maxY-minY)/boundTree.getHeight();
			
			int thumbMin = Math.round((float)bounds.getY());
			int thumbMax = thumbMin + Math.round((float)bounds.getHeight());
	    	for (Iterator iterator = nodesToHighlight.keySet().iterator(); iterator.hasNext();) {
	    		Integer row = (Integer) iterator.next();
	    		Rectangle r3 = tree.getRowBounds(row.intValue());
	    		if (r3==null){
	    			continue;
	    		}
	    		int y = minY + Math.round((float)(r3.getY()*diff));
	    		// do not paint if line would be in front of a button or the knob/thumb
	    		if (y<thumbMin  || y>thumbMax){	    		
	    			_graphics.drawLine(x, y, xwidth, y);
//	    			System.out.println( +thumbMin + " , " + thumbMax + " , " + y );
	    		}
			}
		}


	}

	/*
	 * remove all information about the current match result
	 */
	public void cleanMatchresultLines() {
		srcMatches.clear();
		trgMatches.clear();
		srcMatches_fragment.clear();
		trgMatches_fragment.clear();
	}

	/*
	 * set new match result and calculate the lines new
	 */
	public void setNewMatchResult(MatchResult _result) {
		srcMatches.clear();
		trgMatches.clear();
		srcMatches_fragment.clear();
		trgMatches_fragment.clear();
		result = _result;
		if (_result == null) {
			return;
		}
		MatchresultView2 mw2 = controller.getMatchresultView();
		sourceTree = mw2.getSourceTree();
		targetTree = mw2.getTargetTree();
		ArrayList srcObjects = _result.getSrcObjects();
		ArrayList trgObjects = _result.getTrgObjects();
		if (_result instanceof MatchResultArray){
			float[][] simMatrix = ((MatchResultArray) _result).getSimMatrix();
			calculateLines(srcObjects, trgObjects, sourceTree, targetTree,
					 mw2.getSrcPath2TreePath(), mw2.getTrgPath2TreePath(),
					 simMatrix, srcMatches,	trgMatches);
		} else {
//			System.out.println("LinesComponent2.setNewMatchResult: Error at the moment not MatchResultDB supported");
			calculateLines(srcObjects, trgObjects, sourceTree, targetTree,
					 mw2.getSrcPath2TreePath(), mw2.getTrgPath2TreePath(),
					 _result, srcMatches,	trgMatches);
		}
	}

	/*
	 * change the graphics parameter (color and stroke) depending on the kind of
	 * lines
	 *  
	 */
	protected int setGraphics(int state, Graphics2D _graphics) {
		int offset = 0;
		switch (state) {
			case STATE_GLOBAL :
				_graphics.setColor(MainWindow.GLOBAL);
				_graphics.setStroke(new BasicStroke(1));
				offset = OFFSET_GLOBAL;
				break;
//			case STATE_LIGHT :
//				_graphics.setColor(MainWindow.LIGHT);
//				_graphics.setStroke(new BasicStroke(1));
//				offset = OFFSET_GLOBAL;
//				break;
			case STATE_FRAGMENT :
				_graphics.setColor(MainWindow.USED_FRAGMENT_BACKGROUND);
				_graphics.setStroke(new BasicStroke(3));
				offset = OFFSET_GLOBAL;
				break;
			case STATE_SUGGESTION :
				_graphics.setColor(MainWindow.SUGGESTED_FRAGMENT_BACKGROUND);
				_graphics.setStroke(new BasicStroke(2));
				break;
		}
		return offset;
	}

	/*
	 * calculate and paint all lines of the current match result
	 */
	private void calculateAllLines(Graphics2D _graphics) {
		
		if (srcMatches.size() > 0) {
			Iterator<TreePath> it = srcMatches.keySet().iterator();
			int offset = setGraphics(STATE_GLOBAL, _graphics);
			try {
				while (it.hasNext()) {
					TreePath srcPath = it.next();
					calculateSrcLines(_graphics, srcPath, offset, false);
				}
			} catch (java.util.ConcurrentModificationException e) {
				try {
					// let some time pass to calculate lines
					// -> much less new exceptions
					Thread.sleep(100); // milliseconds
				} catch (InterruptedException el) {
//					el.printStackTrace();
				}
				calculateAllLines(_graphics);
			}
		}
		lastDrawing = ALL;
		scrolledRow = NONE;
	}

	/*
	 * calculate and paint lines for the given trees and match results
	 */
	static void calculateLines(ArrayList _srcObjects, ArrayList _trgObjects,
			ExtJTree _sourceTree, ExtJTree _targetTree, HashMap<Object, TreePath> _srcpath2TreePath,
			HashMap<Object, TreePath> _trgpath2TreePath, float[][] _simMatrix,
			HashMap<TreePath, ArrayList<Match>> _srcMatches, HashMap<TreePath, ArrayList<Match>> _trgMatches) {
		for (int i = 0; i < _srcObjects.size(); i++) {
			Object currentSrc = _srcObjects.get(i);
			HashSet<TreePath> srcPaths = findTreePath(currentSrc, _sourceTree,
					_srcpath2TreePath);
			if (srcPaths==null) continue;
			
			for (TreePath srcPath : srcPaths) {
				ArrayList<Match> trgTreePaths = _srcMatches.get(srcPath); 
				if (trgTreePaths==null) {
					trgTreePaths = new ArrayList<Match>();
				}
				// srcPath==null -> then its a local match
				for (int j = 0; j < _trgObjects.size(); j++) {
					float sim = _simMatrix[i][j];
					if (sim > 0) {
						Object currentTrg = _trgObjects.get(j);
						HashSet<TreePath> trgPaths = findTreePath(currentTrg,
								_targetTree, _trgpath2TreePath);
						if (trgPaths==null) continue;
						//	trgPath==null -> then its a local match
						// two global paths						
						for (TreePath trgPath : trgPaths) {
							trgTreePaths.add(new Match(trgPath, sim));
							ArrayList<Match> list = _trgMatches.get(trgPath);
							if (list==null) {
								list = new ArrayList<Match>();
							}
							list.add(new Match(srcPath, sim));
							_trgMatches.put(trgPath, list);
						}
					}
				}
				if (srcPath != null) {
					_srcMatches.put(srcPath, trgTreePaths);
				}
			}
		}
	}

	/*
	 * draw all global correspondences of the current matchresult between a given
	 * source row and connected nodes in the target tree
	 */
	private  void calculateSrcLines(Graphics2D _graphics, TreePath _srcPath,
			int _offset, boolean _selected) {
		ArrayList trgPaths = srcMatches.get(_srcPath);
		if (trgPaths != null && trgPaths.size() > 0) {
			int srcRow = sourceTree.getRowForPath(_srcPath);
			for (int i = 0; i < trgPaths.size(); i++) {
				Match match = (Match) trgPaths.get(i);
				TreePath trgPath = match.getTreePath();
				int trgRow = targetTree.getRowForPath(trgPath);
				if ((srcRow > -1) && (trgRow > -1)) {
					float shortValue = (float) ((int) (LENGTH * match
							.getSimValue()))
							/ LENGTH;
					String simValue = GUIConstants.EMPTY + shortValue;
					// draw line => 2 global paths have a match
					drawLine(srcRow, trgRow, _offset, _graphics, false, 
									!alreadyScrolled, _selected, simValue);
					alreadyScrolled = true;
				}
			}
		}
	}
	
	/*
	 * draw from the selected element of the source tree all correspondences to
	 * that element
	 */
	private void calculateSelSrcLines(Graphics2D _graphics) {
		TreePath selSrcPath = sourceTree.getLeadSelectionPath();
		alreadyScrolled = false;
		lastDrawing = SEL_SRC;
		// calculate lines for the selected source element
		if (srcMatches.size() > 0) {
			int offset = setGraphics(STATE_GLOBAL, _graphics);
			calculateSrcLines(_graphics, selSrcPath, offset, true);
		}
	}

	/*
	 * draw all global correspondences of the current matchresult between a given
	 * target row and connected nodes in the source tree
	 */
	private void calculateTrgLines(Graphics2D _graphics, TreePath _trgPath,
			int _offset, boolean _selected) {
		ArrayList srcPaths = trgMatches.get(_trgPath);
		if (srcPaths != null && srcPaths.size() > 0) {
			int trgRow = targetTree.getRowForPath(_trgPath);
			for (int i = 0; i < srcPaths.size(); i++) {
				Match match = (Match) srcPaths.get(i);
				TreePath srcPath = match.getTreePath();
				int srcRow = sourceTree.getRowForPath(srcPath);
				if ((srcRow > -1) && (trgRow > -1)) {
					float shortValue = (float) ((int) (LENGTH * match
							.getSimValue()))
							/ LENGTH;
					String simValue = GUIConstants.EMPTY + shortValue;
					// draw line => 2 global paths have a match
					drawLine(srcRow, trgRow, _offset, _graphics, 
							!alreadyScrolled,false, _selected, simValue);
					alreadyScrolled = true;
				}
			}
		}
	}

	/*
	 * draw from the selected element of the target tree all correspondences to
	 * that element
	 */
	private void calculateSelTrgLines(Graphics2D _graphics) {
		TreePath selTrgPath = targetTree.getLeadSelectionPath();
		alreadyScrolled = false;
		lastDrawing = SEL_TRG;
		// calculate lines for the selected source element
		if (trgMatches.size() > 0) {
			int offset = setGraphics(STATE_GLOBAL, _graphics);
			calculateTrgLines(_graphics, selTrgPath, offset, true);
		}
	}

	/*
	 * draw all lines that are currently shown (either to/from an element or all
	 * correspondences)
	 */
	void drawCurrentLines(Graphics2D _graphics) {
		if (controller.getMainWindow() == null
				|| controller.getGUIMatchresult() == null) {
			return;
		}
		if (!controller.getMatchresultView().isChanged()) {
			// print lines again (no new calculation)
			if (linesList.size() > 0) {
				for (int i = 0; i < linesList.size(); i++) {
					linesList.get(i).paint(_graphics);
				}
			}
			return;
		}
		controller.getMatchresultView().setChanged(false);
		linesList.clear();
		// calculate lines new and print them  
		if ((controller.getStepFragmentMatching() != null)
				&& controller.getStepFragmentMatching().isActive()
				) {
			//	 draw lines between fragments
			sourceTree = controller.getMatchresultView().getSourceTree();
			targetTree = controller.getMatchresultView().getTargetTree();
			drawLinesBetweenFragments(_graphics);
			return;
		} else 
			if (!controller.getGUIMatchresult().containsMatchResult()) {
			// there is no match result and so no lines to paint
			return;
		}
		// if there is a source and target tree - draw lines
		if ((sourceTree != null) && (targetTree != null)) {
			int selectedTree = controller.getMatchresultView()
					.getLastSelectedTree();
			if (selectedTree == ExtJTree.SOURCE) {
				// draw the lines attached to the selected source element
				calculateSelSrcLines(_graphics);
			} else if (selectedTree == ExtJTree.TARGET) {
				// draw the lines attached to the selected target element
				calculateSelTrgLines(_graphics);
			} else {
				// draw all lines
				calculateAllLines(_graphics);
			}
		}
	}

	/*
	 * draw a line from a given source row to a given target row using the given
	 * Graphics2D
	 */
	private void drawLine(int _srcRow, int _trgRow, int _offset,
			Graphics2D _graphics, boolean _scrollSource, boolean _scrollTarget,
			boolean _fromSel, String _simValue) {
		//		System.out.print("drawLine");
		if (_scrollSource && _fromSel && !alreadyScrolled
				&& ((lastDrawing != SEL_TRG) || ((lastDrawing == SEL_TRG) && (scrolledRow != _srcRow)))) {
			// scroll source row to be visible
			scrolledRow = _srcRow;
			alreadyScrolled = true;
			sourceTree.scrollRowToVisible(_srcRow);
		}
		if (_scrollTarget && _fromSel && !alreadyScrolled
				&& (lastDrawing != SEL_SRC || ((lastDrawing == SEL_SRC) && (scrolledRow != _trgRow)))) {
			// scroll target row to be visible
			scrolledRow = _trgRow;
			alreadyScrolled = true;
			targetTree.scrollRowToVisible(_trgRow);
		}
		// calculate start point of this line
		Rectangle r = sourceTree.getRowBounds(_srcRow);
		int xStart = r.x + r.width + (int) sourceTree.getLocation().getX()+3;
		if (xStart > (controller.getMatchresultView().getDividerLocation() - 15)) {
			xStart = controller.getMatchresultView().getDividerLocation() - 15;
		}

		r = targetTree.getRowBounds(_trgRow);
		int rowHeight = r.height;
		int yStart = Y_ROW_0 + _srcRow * rowHeight + (int) sourceTree.getLocation().getY() + _offset;
		
		// calculate end point of this line
		int xEnd = controller.getMatchresultView().getDividerLocation()
				+ controller.getMatchresultView().getDividerSize() + r.x
				+ (int) targetTree.getLocation().getX();
		if (xEnd < (controller.getMatchresultView().getDividerLocation() + 15)) {
			xEnd = controller.getMatchresultView().getDividerLocation() + 15;
		}
		int yEnd = Y_ROW_0 + _trgRow * rowHeight
				+ (int) targetTree.getLocation().getY() + _offset;


		double maxY = this.getBounds().getHeight();
		if (!_fromSel){
			// don't show lines that are either painted just below or beneath the view (can't be seen)
			// or that start outside of the view and end there as well (they just irritate the user)
			if (yStart<0){
				if (yEnd> maxY || yEnd<0){
					return;
				}
			} else if (yStart>maxY){
				if (yEnd<0 || yEnd> maxY){
					return;
				}
			}
		}
		// create, draw and save line to list
		Line line = new Line(this, _graphics, xStart, yStart, xEnd, yEnd, _simValue,
				_fromSel, false);
		linesList.add(line);
		line.repaint();
	}

	/*
	 * draw all correspondences of the current matchresult between a given target
	 * row and connected nodes in the source tree
	 */
	private  void drawLinesBetweenFragments(Graphics2D _graphics) {
		int rows = targetTree.getRowCount();
		if (rows > 0) {
			for (int trgRow = 0; trgRow < rows; trgRow++) {
				TreePath path = targetTree.getPathForRow(trgRow);
				// returns without drawing if row is not visible/ doesn't exist
				if (path == null) {
					return;
				}
				DefaultMutableTreeNode trgNode = (DefaultMutableTreeNode) path
						.getLastPathComponent();
//				if (!(trgNode.getUserObject() instanceof Path)) {
//					return;
//				}
				// suggested fragment pair lines
				int offset = setGraphics(STATE_SUGGESTION, _graphics);
					if (controller.getStepFragmentMatching() != null) {
						MatchResult matches = controller.getStepFragmentMatching()
								.getFragmentPairs();
					if (matches == null) {
						return;
					}
					Object b =  trgNode.getUserObject();
					ArrayList amatches = matches.getSrcMatchObjects(b);
//					if (amatches==null && b instanceof Path){
//						amatches = matches.getSrcMatchObjects(((Path)b).getl
//					}
							
					if ((amatches != null) && (amatches.size() > 0)) {
						for (int j = 0; j < amatches.size(); j++) {
							Path object = (Path) amatches.get(j);
							int srcRow = findRow(object,sourceTree, 
								controller.getMatchresultView().getSrcPath2TreePath());
							if (srcRow > -1) {
								drawLine(srcRow, trgRow, offset,_graphics, 
										false, false, false, null);
							}
						}
					}
				}
			}
		}
	}

	/*
	 * returns the combination from a global (Up Root) Path with a local (Down)
	 * Path normally there will be one overlapping node
	 */
	static ArrayList combineLokalAndGlobalPath(ArrayList _globalPath,
			ArrayList _lokalPath, Element _fragmentNode) {
		ArrayList combinedPath = new ArrayList();
		if (_globalPath.contains(_fragmentNode)) {
			for (int i = 0; i < _globalPath.indexOf(_fragmentNode); i++) {
				combinedPath.add(_globalPath.get(i));
			}
		} else {
			combinedPath.addAll(_globalPath);
		}
		combinedPath.addAll(_lokalPath);
		return combinedPath;
	}

	/*
	 * returns for a List of local target Paths all variations of global target
	 * Paths
	 */
	static ArrayList<ArrayList> getGlobalPaths(ArrayList _lokalPath, Hashtable _upRootPaths) {
		ArrayList<ArrayList> finalPaths = new ArrayList<ArrayList>();
		if (_lokalPath != null) {
			Element node = (Element) _lokalPath.get(0);
			ArrayList upRootPaths = (ArrayList) _upRootPaths.get(node);
			if (upRootPaths != null) {
				for (int j = 0; j < upRootPaths.size(); j++) {
					ArrayList finalPath = combineLokalAndGlobalPath(
							(ArrayList) upRootPaths.get(j), _lokalPath, node);
					finalPaths.add(finalPath);
				}
			}
		}
		if (finalPaths.size() > 0) {
			return finalPaths;
		}
		return null;
	}

	/*
	 * get for a path (of a node in the schema graph) the TreePath in the tree
	 * representation
	 */
	protected static HashSet<TreePath> findTreePath(Object _path, ExtJTree _tree,
			HashMap<Object, TreePath> _path2TreePath) {
		if (_tree != null) {
			TreePath treePath = _path2TreePath.get(_path);
			if (treePath != null) {
				HashSet<TreePath> treePaths = new HashSet<TreePath>();
				treePaths.add(treePath);
				return treePaths;
			}
			return ExtJTree.getTreePath(_path, _tree, _path2TreePath);
		}
		return null;
	}

	/*
	 * get for a path (of a node in the schema graph) the row in the tree
	 * representation
	 */
	protected static int findRow(Path _path, ExtJTree _tree,
			HashMap<Object, TreePath> _path2TreePath) {
		if (_tree != null) {
			TreePath treePath = _path2TreePath.get(_path);
			if (treePath != null) {
				return _tree.getRowForPath(treePath);
			}
			HashSet<TreePath> treePaths = ExtJTree.getTreePath(_path, _tree,_path2TreePath);
			if (treePaths!=null)
			return _tree.getRowForPath(treePaths.iterator().next());
		}
		return -1;
	}

	/*
	 * @see java.awt.Component#setBounds(int, int, int, int)
	 */
	public void setBounds(int _x, int _y, int _width, int _height) {
		super.setBounds(_x, _y, _width, _height);
		if ((controller != null)
				&& (controller.getMatchresultView() != null)) {
			controller.getMatchresultView().setChanged(true);
		}
	}

	/*
	 * returns the current match result
	 */
	public MatchResult getResult() {
		return result;
	}
	
	public void setHighlightedSrc(HashMap<Integer,DefaultMutableTreeNode> nodesWithString){
		nodesToHighlightSrc = nodesWithString;
		this.repaint();
	}
	
	public void setHighlightedTrg(HashMap<Integer,DefaultMutableTreeNode> nodesWithString){
		nodesToHighlightTrg = nodesWithString;
		this.repaint();
	}
	
	private boolean isPathVisible(ExtJTree tree, TreePath path){
		Rectangle visibleRect = tree.getVisibleRect();
		double minHeight = visibleRect.getY();
		double maxHeight = visibleRect.getY()+visibleRect.getHeight();		
		Rectangle pathBounds = tree.getPathBounds(path);
		double currentY = pathBounds.getY();
		if (currentY>=minHeight && maxHeight>=currentY){
			return true;
		}
		return false;
	}

	private void showNextLineDown(ExtJTree tree, ArrayList paths){
		if (paths==null){
			return;
		}
		Rectangle visibleRect = tree.getVisibleRect();
		double maxHeight = visibleRect.getY()+visibleRect.getHeight();		
		// calculate next trgPath (with either higher row, or the lowest)
		int minRow = -1;
		int nextBiggerRow = -1;
		for (int i = 0; i < paths.size(); i++) {
			Match match = (Match) paths.get(i);
			TreePath path = match.getTreePath();
			if (tree.isVisible(path)){
				int row = tree.getRowForPath(path);
				Rectangle pathBounds = tree.getPathBounds(path);
				double currentY = pathBounds.getY();
				if (currentY>maxHeight){
					if (nextBiggerRow==-1 || nextBiggerRow>row){
						nextBiggerRow=row;
					} 
				}
				if (row<minRow || minRow==-1){
					minRow=row;
				}
			}
		}
		if (nextBiggerRow>-1){
			tree.scrollRowToVisible(nextBiggerRow);
		} else {
			tree.scrollRowToVisible(minRow);
		}
	}
	

	private void showNextLineUp(ExtJTree tree, ArrayList paths){
		Rectangle visibleRect = tree.getVisibleRect();
		double minHeight = visibleRect.getY();		
		// calculate next trgPath (with either higher row, or the lowest)
		int maxRow = -1;
		int nextLowerRow = -1;
		for (int i = 0; i < paths.size(); i++) {
			Match match = (Match) paths.get(i);
			TreePath path = match.getTreePath();
			if (tree.isVisible(path)){
				int row = tree.getRowForPath(path);
				Rectangle pathBounds = tree.getPathBounds(path);
				double currentY = pathBounds.getY();
				if (currentY<minHeight){
					if (minHeight==-1 || nextLowerRow<row){
						nextLowerRow=row;
					} 
				}
				if (row>maxRow){
					maxRow=row;
				}
			}
		}
		if (nextLowerRow>-1){
			tree.scrollRowToVisible(nextLowerRow);
		} else {
			tree.scrollRowToVisible(maxRow);
		}
	}
	
	public void showNextLine(boolean down) {
		TreePath sourcePath = sourceTree.getSelectionPath();
		TreePath targetPath = targetTree.getSelectionPath();
		if (sourcePath==null && targetPath==null){
			return;
		}
		if (down){
			if (sourcePath!=null && targetPath==null){
				ArrayList trgPaths = srcMatches.get(sourcePath);
				showNextLineDown(targetTree, trgPaths);
			} else if (targetPath!=null && sourcePath==null){
				ArrayList srcPaths = trgMatches.get(targetPath);
				showNextLineDown(sourceTree, srcPaths);
			}
		} else {
			if (sourcePath!=null && targetPath==null){
				ArrayList trgPaths = srcMatches.get(sourcePath);
				showNextLineUp(targetTree, trgPaths);
			} else if (targetPath!=null && sourcePath==null){
				ArrayList srcPaths = trgMatches.get(targetPath);
				showNextLineUp(sourceTree, srcPaths);
			}
		}
		
	}

	/*
	 * calculate and paint lines for the given trees and match results
	 */
	static void calculateLines(ArrayList _srcObjects, ArrayList _trgObjects,
			ExtJTree _sourceTree, ExtJTree _targetTree, HashMap<Object, TreePath> _srcpath2TreePath,
			HashMap<Object, TreePath> _trgpath2TreePath, MatchResult result,
			HashMap<TreePath, ArrayList<Match>> _srcMatches, HashMap<TreePath, ArrayList<Match>> _trgMatches) {
		for (int i = 0; i < _srcObjects.size(); i++) {
			Object currentSrc = _srcObjects.get(i);
			HashSet<TreePath> srcPaths = findTreePath(currentSrc, _sourceTree,
					_srcpath2TreePath);
			if (srcPaths==null) continue;
			
			for (TreePath srcPath : srcPaths) {
				ArrayList<Match> trgTreePaths = _srcMatches.get(srcPath); 
				if (trgTreePaths==null) {
					trgTreePaths = new ArrayList<Match>();
				}
				// srcPath==null -> then its a local match
				for (int j = 0; j < _trgObjects.size(); j++) {
					Object currentTrg = _trgObjects.get(j);					
					float sim = result.getSimilarity(currentSrc, currentTrg);
					if (sim > 0) {
						HashSet<TreePath> trgPaths = findTreePath(currentTrg,
								_targetTree, _trgpath2TreePath);
						if (trgPaths==null) continue;
						//	trgPath==null -> then its a local match
						// two global paths						
						for (TreePath trgPath : trgPaths) {
							trgTreePaths.add(new Match(trgPath, sim));
							ArrayList<Match> list = _trgMatches.get(trgPath);
							if (list==null) {
								list = new ArrayList<Match>();
							}
							list.add(new Match(srcPath, sim));
							_trgMatches.put(trgPath, list);
						}
					}
				}
				if (srcPath != null) {
					_srcMatches.put(srcPath, trgTreePaths);
				}
			}
		}
	}
	
}