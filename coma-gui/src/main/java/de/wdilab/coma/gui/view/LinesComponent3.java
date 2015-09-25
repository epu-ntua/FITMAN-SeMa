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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.tree.TreePath;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.extjtree.ExtJTree;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;

/**
 * This class calculates the lines of the current matching. The lines are from a source
 * to a middle model and from that middle model to a target model. If an element is selected 
 * its corresponding lines are searched for and displayed.
 * 
 * @author Sabine Massmann
 */
public class LinesComponent3 extends LinesComponent2 {
	static final int LEFT = 0;
	static final int MIDDLE = 1;
	static final int RIGHT = 2;
	ExtJTree middleTree;
	ArrayList linesList2;
	private HashMap<TreePath, ArrayList<Match>> middleSrcMatches;
	private HashMap<TreePath, ArrayList<Match>> middleTrgMatches;
	boolean currentMRWithMiddleMR = false;
	// not used: taxonomy
//	boolean currentTaxonomyMatches = false;
	HashMap taxSimComposePaths;
	MatchResult taxSim;

	// private HashMap middleMatches2_fragment;
	/*
	 * Constructor of JLinesComponent size of it is given through
	 * (xStart,yStart) and height + width
	 */
	public LinesComponent3(Controller _controller) {
		super(_controller);
		linesList2 = new ArrayList();
		middleSrcMatches = new HashMap<TreePath, ArrayList<Match>>();
		middleTrgMatches = new HashMap<TreePath, ArrayList<Match>>();
		// middleMatches2_fragment = new HashMap();
	}

	public void setNewMatchResult(MatchResult _result) {
		// System.out.println("DO: setNewMatchResult");
		// TO DO repository reloaded.... different....
		if (_result == null) {
			return;
		}
		MatchresultView3 mw3 = null;
		try {
			mw3 = (MatchresultView3) controller.getMatchresultView();
		} catch (ClassCastException e) {
			System.out.println("LinesComponent3.setNewMatchResult(): ClassCastException");
			super.setNewMatchResult(_result);
			return;
		}
		MatchResult[] mrs = null;
		if (_result.getUserObject() instanceof MatchResult[]) {
			mrs = (MatchResult[]) _result.getUserObject();
			if (mrs == null) {
				super.setNewMatchResult(_result);
				currentMRWithMiddleMR = false;
				// not used: taxonomy
//				currentTaxonomyMatches = false;
				return;
			}
			// not used: taxonomy
//			if (mrs.length == 3) {
//				currentTaxonomyMatches = true;
//			} else {
//				currentTaxonomyMatches = false;
//			}
		} else {
			super.setNewMatchResult(_result);
			currentMRWithMiddleMR = false;
			// not used: taxonomy
//			currentTaxonomyMatches = false;
			return;
		}
		currentMRWithMiddleMR = true;
		srcMatches.clear();
		srcMatches_fragment.clear();
		middleSrcMatches.clear();
		trgMatches.clear();
		trgMatches_fragment.clear();
		sourceTree = mw3.getSourceTree();
		middleTree = mw3.getMiddleTree();
		targetTree = mw3.getTargetTree();
		MatchResult mrLeft = mrs[0];
		MatchResult mrRight = mrs[1];
		ArrayList srcObjects = mrLeft.getSrcObjects();
		ArrayList middleObjectsLeft = mrLeft.getTrgObjects();
		ArrayList middleObjectsRight = mrRight.getSrcObjects();
		ArrayList trgObjects = mrRight.getTrgObjects();
		if (mrLeft instanceof MatchResultArray){
			float[][] simMatrixLeft = ((MatchResultArray)mrLeft).getSimMatrix();
			// System.out
			// .println("**************************************************");
			calculateLines(srcObjects, middleObjectsLeft, sourceTree,
					middleTree, mw3.getSrcPath2TreePath(), mw3
							.getMiddlePath2TreePath(), simMatrixLeft, srcMatches,
					middleSrcMatches);
		} else {
			calculateLines(srcObjects, middleObjectsLeft, sourceTree,
					middleTree, mw3.getSrcPath2TreePath(), mw3
							.getMiddlePath2TreePath(), mrLeft, srcMatches,
					middleSrcMatches);
		}
		if (mrRight instanceof MatchResultArray){	
			float[][] simMatrixRight = ((MatchResultArray)mrRight).getSimMatrix();
			calculateLines(middleObjectsRight, trgObjects, middleTree,
					targetTree, mw3.getMiddlePath2TreePath(), mw3
							.getTrgPath2TreePath(), simMatrixRight,
					middleTrgMatches, trgMatches);
		} else {
			calculateLines(middleObjectsRight, trgObjects, middleTree,
					targetTree, mw3.getMiddlePath2TreePath(), mw3
							.getTrgPath2TreePath(), mrRight,
					middleTrgMatches, trgMatches);
		}
		// try to paint lines from the taxonomy to itself
		// not used: taxonomy
//		if (currentTaxonomyMatches) {
//			taxSim = mrs[2];
//			taxSimComposePaths = (HashMap) mrs[2].getUserObject();
//			// MatchResult taxSim =mrs[2];
//			// ArrayList middleLeftObjects = taxSim.getSrcObjects();
//			// ArrayList middleRightObjects = taxSim.getTrgObjects();
//			// float[][] simMatrixTax = taxSim.getSimMatrix();
//			// calculateLines(middleLeftObjects, middleRightObjects,
//			// middleTree,
//			// middleTree, mw3.getMiddlePath2TreePath(), mw3
//			// .getMiddlePath2TreePath(), simMatrixTax,
//			// middleMatchesSelfLeft, middleMatchesSelfRight);
//		}
		// System.out
		// .println("**************************************************");
	}

	/*
	 * paint this component by painting the component and the current lines
	 */
	public void paint(Graphics _graphics) {
		super.paint(_graphics);
		// g.drawLine(10, 10, 100, 100);
		drawCurrentLines((Graphics2D) _graphics);
	}

	private void calculateAllLines(Graphics2D _graphics) {
		// calculate all Lines
		// RED THIN LINES = global matches
		if (srcMatches.size() > 0) {
			Iterator<TreePath> it = srcMatches.keySet().iterator();
			int offset = setGraphics(STATE_GLOBAL, _graphics);
			while (it.hasNext()) {
				TreePath srcPath = it.next();
				calculateSrcLines(_graphics, srcPath, offset, false, true);
			}
		}
		// RED THIN LINES = global matches
		if (trgMatches.size() > 0) {
			Iterator<TreePath> it = trgMatches.keySet().iterator();
			int offset = setGraphics(STATE_GLOBAL, _graphics);
			while (it.hasNext()) {
				TreePath trgPath = it.next();
				calculateTrgLines(_graphics, trgPath, offset, false, true);
			}
		}

		// // RED THIN LINES = global matches
		// if (middleMatchesSelfLeft_global.size() > 0) {
		// Iterator it = middleMatchesSelfLeft_global.keySet().iterator();
		// int offset = setGraphics(STATE_GLOBAL, _graphics);
		// while (it.hasNext()) {
		// TreePath middlePath = (TreePath) it.next();
		// calculateGlobalMiddleSelfLines(_graphics, middlePath, offset, false);
		// }
		// }
		lastDrawing = ALL;
		scrolledRow = NONE;
	}

	/*
	 * draw from the selected element of the source tree all correspondences to
	 * that element
	 */
	private void calculateSelSrcLines(Graphics2D _graphics) {
		TreePath selSrcPath = sourceTree.getLeadSelectionPath();
		alreadyScrolled = false;
		lastDrawing = SEL_SRC;
		// calculate Lines for the selected source element
		if (srcMatches.size() > 0) {
			int offset = setGraphics(STATE_GLOBAL, _graphics);
			calculateSrcLines(_graphics, selSrcPath, offset, true, true);
		}
	}

	/*
	 * draw all global correspondences of the current matchresult between a given
	 * target row and connected nodes in the source tree
	 */
	private boolean calculateTrgLines(Graphics2D _graphics, TreePath _trgPath,
			int _offset, boolean _selected, boolean _drawAll) {
		ArrayList middlePaths = trgMatches.get(_trgPath);
		if (middlePaths != null && middlePaths.size() > 0) {
			for (int i = 0; i < middlePaths.size(); i++) {
				Match match = (Match) middlePaths.get(i);
				TreePath middlePath = match.getTreePath();
				calculateMiddleTrgLines(_graphics, middlePath, _trgPath, match,
						_offset, _selected, _drawAll);
			}
			return true;
		}
		return false;
	}

	/*
	 * draw all global correspondences of the current matchresult between a given
	 * target row and connected nodes in the source tree
	 */
	private void calculateMiddleTrgLines(Graphics2D _graphics,
			TreePath _middlePath, TreePath _trgPath, Match _match, int _offset,
			boolean _selected, boolean drawAll) {
		int trgRow = targetTree.getRowForPath(_trgPath);
		int middleRow = middleTree.getRowForPath(_middlePath);
		int offset = setGraphics(STATE_GLOBAL, _graphics);
		boolean existCompose;
		// not used: taxonomy
//		if (currentTaxonomyMatches) {
//			Iterator it = ((HashMap) taxSimComposePaths.clone()).keySet()
//					.iterator();
//			boolean contain = false;
//			while (it.hasNext()) {
//				Object[] current = (Object[]) it.next();
//				ArrayList dObj = (ArrayList) ((DefaultMutableTreeNode) _trgPath
//						.getLastPathComponent()).getUserObject();
//				ArrayList cObj = (ArrayList) ((DefaultMutableTreeNode) _middlePath
//						.getLastPathComponent()).getUserObject();
//				if (current[2].equals(cObj) && current[3].equals(dObj)) {
//					contain = true;
//					if (drawAll) {
//						HashMap<Object, TreePath> srcPath2TreePath = controller
//								.getMatchresultView().getSrcPath2TreePath();
//						TreePath srcPath = srcPath2TreePath
//								.get(current[0]);
//						calculateSrcLines(_graphics, srcPath, _offset,
//								_selected, false);
//						if (!current[1].equals(current[2])) {
//							HashMap<Object, TreePath> middlePath2TreePath = ((MatchresultView3) controller
//									.getMatchresultView()).getMiddlePath2TreePath();
//							TreePath middlePath1 = middlePath2TreePath
//									.get(current[1]);
//							TreePath middlePath2 = middlePath2TreePath
//									.get(current[2]);
//							float sim = taxSim.getSimilarity(current[1],
//									current[2]);
//							Match match = new Match(null, sim);
//							calculateMiddleMiddleLines(_graphics, middlePath1,
//									middlePath2, match, _offset, _selected,
//									drawAll);
//						}
//					}
//				}
//			}
//			existCompose = contain;
//		} else {
			existCompose = calculateMiddleLines(_graphics, _middlePath, offset,
					_selected, false, true);
//		}
		if (!existCompose) {
			offset = setGraphics(LinesComponent2.STATE_LIGHT, _graphics);
		}
		drawLine(_graphics, middleRow, trgRow, _match, _offset, _selected,
				RIGHT);
	}

//	/*
//	 * draw all global correspondences of the current matchresult between a given
//	 * target row and connected nodes in the source tree
//	 */
//	private void calculateMiddleMiddleLines(Graphics2D _graphics,
//			TreePath _middlePath1, TreePath _middlePath2, Match _match,
//			int _offset, boolean _selected, boolean _drawAll) {
//		int middleRow1 = middleTree.getRowForPath(_middlePath1);
//		int middleRow2 = middleTree.getRowForPath(_middlePath2);
//		int offset = setGraphics(STATE_GLOBAL, _graphics);
//		drawLine(_graphics, middleRow1, middleRow2, _match, offset, _selected,
//				MIDDLE);
//	}

	/*
	 * draw from the selected element of the target tree all correspondences to
	 * that element
	 */
	private void calculateSelTrgLines(Graphics2D _graphics) {
		TreePath selTrgPath = targetTree.getLeadSelectionPath();
		alreadyScrolled = false;
		lastDrawing = SEL_TRG;
		// calculate Lines for the selected source element
		if (trgMatches.size() > 0) {
			int offset = setGraphics(STATE_GLOBAL, _graphics);
			calculateTrgLines(_graphics, selTrgPath, offset, true, true);
		}
	}

	/*
	 * draw all lines that are currently shown (either to/from an element or all
	 * correspondences)
	 */
	void drawCurrentLines(Graphics2D _graphics) {
		// System.out.print("drawCurrentLines");
		if (controller.getMainWindow() == null
				|| controller.getGUIMatchresult() == null) {
			return;
		}
		if (!currentMRWithMiddleMR) {
			super.drawCurrentLines(_graphics);
			return;
		}
		if (!controller.getMatchresultView().isChanged()) {
			if (linesList.size() > 0) {
				for (int i = 0; i < linesList.size(); i++) {
					linesList.get(i).paint(_graphics);
				}
			}
			// System.out.println("print lines AGAIN");
			return;
		}
		controller.getMatchresultView().setChanged(false);
		linesList.clear();
		// System.out.println("drawCurrentLines: print lines NEW");
		// if (controller.getStep() != null) {
//		if ((controller.getStepFragmentMatching() != null)
//				&& controller.getStepFragmentMatching().isActive()) {
//			// System.out.println("drawLinesBetweenFragments:");
//			// sourceTree =
//			// controller.getMatchresultView().getSourceTree();
//			// targetTree =
//			// controller.getMatchresultView().getTargetTree();
//			// drawLinesBetweenFragments(_graphics);
//			return;
//		} else 
			if (!controller.getGUIMatchresult().containsMatchResult()) {
			// there is no match result and so no lines to paint
			return;
		}
		// System.out.println("drawCurrentLines");
		if ((sourceTree != null) && (targetTree != null)) {
			int selectedTree = controller.getMatchresultView()
					.getLastSelectedTree();
			// 0 SOURCE, 1 TARGET, -1 NONE
			// if (sourceTree.isSelectionEmpty() &&
			// targetTree.isSelectionEmpty())
			if (selectedTree == ExtJTree.SOURCE) {
				// System.out.println("JLinesComponent.drawLinesSelSrc2Trg: "
				// + System.currentTimeMillis());
				calculateSelSrcLines(_graphics);
			} else if (selectedTree == ExtJTree.TARGET) {
				// System.out.println("JLinesComponent.drawLinesSelTrg2Src: "
				// + System.currentTimeMillis());
				calculateSelTrgLines(_graphics);
			} else if (selectedTree == ExtJTree.MIDDLE) {
				// System.out.println("JLinesComponent.drawLinesSelTrg2Src: "
				// + System.currentTimeMillis());
				calculateSelMiddleLines(_graphics);
			} else {
				// System.out.println("JLinesComponent.drawAllLines2Trg: "
				// + System.currentTimeMillis());
				// drawAllLines2Trg(_graphics);
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
			boolean _fromSel, String _simValue, int _state) {
		// System.out.print("drawLine");
		ExtJTree sourceTreeLocal, targetTreeLocal;
		int divLocationLeft, divLocationRight;
		int SEL_SRC_LOCAL = 0, SEL_TRG_LOCAL = 0;
		MatchresultView3 mw3 = (MatchresultView3) controller.getMatchresultView();
		switch (_state) {
		case LEFT:
			sourceTreeLocal = sourceTree;
			targetTreeLocal = middleTree;
			divLocationLeft = 0;
			divLocationRight = mw3.getDividerLocationLeft();
			SEL_SRC_LOCAL = LinesComponent2.SEL_SRC;
			SEL_TRG_LOCAL = LinesComponent2.SEL_MIDDLE;
			break;
		case MIDDLE:
			sourceTreeLocal = middleTree;
			targetTreeLocal = middleTree;
			divLocationLeft = mw3.getDividerLocationLeft();
			divLocationRight = mw3.getDividerLocationRight();
			SEL_SRC_LOCAL = LinesComponent2.SEL_MIDDLE;
			SEL_TRG_LOCAL = LinesComponent2.SEL_MIDDLE;
			break;
		case RIGHT:
		default:
			sourceTreeLocal = middleTree;
			targetTreeLocal = targetTree;
			divLocationLeft = mw3.getDividerLocationLeft()
					+ mw3.getDividerSize();
			divLocationRight = mw3.getDividerLocationRight();
			SEL_SRC_LOCAL = LinesComponent2.SEL_MIDDLE;
			SEL_TRG_LOCAL = LinesComponent2.SEL_TRG;
			break;
		}
		if (_scrollSource
				&& _fromSel
				&& !alreadyScrolled
				&& ((lastDrawing != SEL_TRG_LOCAL) || ((lastDrawing == SEL_TRG_LOCAL) && (scrolledRow != _srcRow)))) {
			// System.out.println("_scrollSource: "+_scrollSource
			// + " |_fromSel:" + _fromSel
			// + " |alreadyScrolled:" + alreadyScrolled
			// + " |lastDrawing:" + lastDrawing
			// +" |scrolledRow:" +scrolledRow);
			scrolledRow = _srcRow;
			alreadyScrolled = true;
			sourceTreeLocal.scrollRowToVisible(_srcRow);
		}
		Rectangle r = sourceTreeLocal.getRowBounds(_srcRow);
		int xStart = divLocationLeft + r.x + r.width
				+ (int) sourceTreeLocal.getLocation().getX();
		if (xStart > (divLocationRight - 15) && _state != MIDDLE) {
			xStart = divLocationRight - 15;
		}
		if (_scrollTarget
				&& _fromSel
				&& !alreadyScrolled
				&& (lastDrawing != SEL_SRC_LOCAL || ((lastDrawing == SEL_SRC_LOCAL) && (scrolledRow != _trgRow)))) {
			// System.out.println("_scrollTarget: "+_scrollTarget
			// + " |lastDrawing:" + lastDrawing
			// +" |scrolledRow:" +scrolledRow);
			scrolledRow = _trgRow;
			alreadyScrolled = true;
			targetTreeLocal.scrollRowToVisible(_trgRow);
		}
		r = targetTreeLocal.getRowBounds(_trgRow);
//		int rowHeight = (MainWindow.TREE_FONT_TEXT.getSize() + 5);
		int rowHeight = r.height;
		// calculate start point of this line
		int yStart = Y_ROW_0 + _srcRow * rowHeight
				+ (int) sourceTreeLocal.getLocation().getY() + _offset;
		// if ((yStart)<3) yStart=3;
		// calculate end point of this line
		int xEnd;
		if (_state == MIDDLE) {
			xEnd = divLocationLeft + r.x + r.width
					+ (int) sourceTreeLocal.getLocation().getX();
		} else {
			xEnd = divLocationRight + mw3.getDividerSize() + r.x
					+ (int) targetTreeLocal.getLocation().getX();
		}
		if (xEnd < (divLocationRight + 15) && _state != MIDDLE) {
			xEnd = divLocationRight + 15;
		}
		int yEnd = Y_ROW_0 + _trgRow * rowHeight
				+ (int) targetTreeLocal.getLocation().getY() + _offset;
		
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
				_fromSel, _state == MIDDLE);
		linesList.add(line);
		line.repaint();
	}

	private void drawLine(Graphics2D _graphics, int _row1, int _row2,
			Match _match, int _offset, boolean _selected, int _state) {
		if ((_row1 > -1) && (_row2 > -1)) {
			float shortValue = (float) ((int) (LENGTH * _match.getSimValue()))
					/ LENGTH;
			String simValue = GUIConstants.EMPTY + shortValue;
			// RED THIN LINE => 2 global paths have a match
			drawLine(_row1, _row2, _offset, _graphics, false, !alreadyScrolled,
					_selected, simValue, _state);
			alreadyScrolled = true;
		}
	}

	/*
	 * draw from the selected element of the source tree all correspondences to
	 * that element
	 */
	private void calculateSelMiddleLines(Graphics2D _graphics) {
		TreePath selMiddlePath = middleTree.getLeadSelectionPath();
		alreadyScrolled = false;
		lastDrawing = SEL_MIDDLE;
		// calculate Lines for the selected middle element
		ArrayList srcPaths = middleSrcMatches.get(selMiddlePath);
		if (srcPaths != null && srcPaths.size() > 0) {
			for (int i = 0; i < srcPaths.size(); i++) {
				Match match = (Match) srcPaths.get(i);
				TreePath srcPath = match.getTreePath();
				int offset = setGraphics(STATE_GLOBAL, _graphics);
				calculateSrcMiddleLines(_graphics, srcPath, selMiddlePath,
						match, offset, true, true);
			}
		}
		ArrayList trgPaths = middleTrgMatches.get(selMiddlePath);
		if (trgPaths != null && trgPaths.size() > 0) {
			for (int i = 0; i < trgPaths.size(); i++) {
				Match match = (Match) trgPaths.get(i);
				TreePath trgPath = match.getTreePath();
				int offset = setGraphics(STATE_GLOBAL, _graphics);
				calculateMiddleTrgLines(_graphics, selMiddlePath, trgPath,
						match, offset, true, true);
			}
		}
	}

	/*
	 * draw all global correspondences of the current matchresult between a given
	 * source row and connected nodes in the target tree
	 */
	private void calculateSrcMiddleLines(Graphics2D _graphics,
			TreePath _srcPath, TreePath middlePath, Match _match, int _offset,
			boolean _selected, boolean _drawAll) {
		int srcRow = sourceTree.getRowForPath(_srcPath);
		int middleRow = middleTree.getRowForPath(middlePath);
		int offset = setGraphics(STATE_GLOBAL, _graphics);
		boolean existCompose;
		// not used: taxonomy
//		if (currentTaxonomyMatches) {
//			Iterator it = ((HashMap) taxSimComposePaths.clone()).keySet()
//					.iterator();
//			boolean contain = false;
//			while (it.hasNext()) {
//				Object[] current = (Object[]) it.next();
//				ArrayList aObj = (ArrayList) ((DefaultMutableTreeNode) _srcPath
//						.getLastPathComponent()).getUserObject();
//				ArrayList bObj = (ArrayList) ((DefaultMutableTreeNode) middlePath
//						.getLastPathComponent()).getUserObject();
//				if (current[0].equals(aObj) && current[1].equals(bObj)) {
//					contain = true;
//					if (_drawAll) {
//						HashMap<Object, TreePath> trgPath2TreePath = controller
//								.getMatchresultView().getTrgPath2TreePath();
//						TreePath trgPath = trgPath2TreePath
//								.get(current[3]);
//						calculateTrgLines(_graphics, trgPath, _offset,
//								_selected, false);
//						if (!current[1].equals(current[2])) {
//							HashMap<Object, TreePath> middlePath2TreePath = ((MatchresultView3) controller
//									.getMatchresultView()).getMiddlePath2TreePath();
//							TreePath middlePath1 = middlePath2TreePath
//									.get(current[1]);
//							TreePath middlePath2 = middlePath2TreePath
//									.get(current[2]);
//							float sim = taxSim.getSimilarity(current[1],
//									current[2]);
//							Match match = new Match(null, sim);
//							calculateMiddleMiddleLines(_graphics, middlePath1,
//									middlePath2, match, _offset, _selected,
//									_drawAll);
//						}
//					}
//				}
//			}
//			existCompose = contain;
//		} else {
			existCompose = calculateMiddleLines(_graphics, middlePath, offset,
					_selected, false, false);
//		}
		if (!existCompose) {
			offset = setGraphics(LinesComponent2.STATE_LIGHT, _graphics);
		}
		drawLine(_graphics, srcRow, middleRow, _match, _offset, _selected, LEFT);
	}

	/*
	 * draw all global correspondences of the current matchresult between a given
	 * source row and connected nodes in the target tree
	 */
	private boolean calculateSrcLines(Graphics2D _graphics, TreePath _srcPath,
			int _offset, boolean _selected, boolean _drawAll) {
		ArrayList middlePaths = srcMatches.get(_srcPath);
		if (middlePaths != null && middlePaths.size() > 0) {
			for (int i = 0; i < middlePaths.size(); i++) {
				Match match = (Match) middlePaths.get(i);
				TreePath middlePath = match.getTreePath();
				calculateSrcMiddleLines(_graphics, _srcPath, middlePath, match,
						_offset, _selected, _drawAll);
			}
			return true;
		}
		return false;
	}

	/*
	 * draw all global correspondences of the current matchresult between a given
	 * source row and connected nodes in the target tree
	 */
	private boolean calculateMiddleLines(Graphics2D _graphics,
			TreePath _middlePath, int _offset, boolean _selected,
			boolean _drawAll, boolean _left) {
		ArrayList<Match> paths = null;
		if (_left) {
			paths = middleSrcMatches.get(_middlePath);
		} else {
			paths = middleTrgMatches.get(_middlePath);
		}
		if (paths != null && paths.size() > 0) {
			for (int i = 0; i < paths.size(); i++) {
				Match match = paths.get(i);
				TreePath path = match.getTreePath();
				if (_drawAll){
					if (_left) {
						calculateSrcMiddleLines(_graphics, path, _middlePath,
								match, _offset, _selected, _drawAll);
					} else {
						calculateMiddleTrgLines(_graphics, _middlePath, path,
								match, _offset, _selected, _drawAll);
					}
				}
			}
			return true;
		}
		return false;
	}
}