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

import javax.swing.JLayeredPane;

import de.wdilab.coma.gui.Controller;

/**
 * LinedMatchresultView contains the MatchresultView with the LinesComponten to draw the
 * lines of a loaded matchresult. The lines (correspondences) are between a source and 
 * a middle and this middle and a target model.
 * 
 * @author Sabine Massmann
 */
public class LinedMatchresultView3 extends LinedMatchresultView2 {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	//	final static int LABEL_ABOVE = 18;
	//	final static int LABEL_BELOW = 35;
	//----------------------------------------------
	//	private MatchresultView3 mw;
	//	double DIV_LOCATION_MATCHRESULT = 0.5;
	//	LinesComponent linesComponent;
	public LinedMatchresultView3(Controller _controller) {
		super(_controller);
		init(_controller);
	}

	void init(Controller _controller) {
		mw = new MatchresultView3(this, _controller);
		//		mw.setDividerLocation(DIV_LOCATION_MATCHRESULT);
		//		mw.setResizeWeight(0.5);
		//		main_Split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mw,
		//				managementPane);
		add(mw);
		setLayer(mw, JLayeredPane.DEFAULT_LAYER.intValue());
		//LinePane
		linesComponent = new LinesComponent3(_controller);
		add(linesComponent);
		setLayer(linesComponent, JLayeredPane.DEFAULT_LAYER
				.intValue() + 1);
	}
}