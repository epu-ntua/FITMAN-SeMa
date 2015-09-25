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
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;

import de.wdilab.coma.gui.MainWindow;

/**
 * This class represents a single correspondence in the graphical interface.
 * It connects one source object (either node or path) with one target object
 * (either node or path). The color represents usually the confidence between 
 * 0 and 1.
 * 
 * @author Sabine Massmann
 */
public class Line extends Component {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	static final Color valueBackground = Color.BLACK;
	static final Color C0_05 = new Color(210, 50, 0);
	static final Color C05_15 = new Color(230, 80, 0);
	static final Color C15_25 = new Color(240, 120, 0);
	static final Color C25_35 = new Color(240, 160, 0);
	static final Color C35_45 = new Color(240, 200, 0);
	static final Color C45_55 = new Color(235, 235, 0);
	static final Color C55_65 = new Color(210, 235, 0);
	static final Color C65_75 = new Color(150, 220, 0);
	static final Color C75_85 = new Color(80, 180, 0);
	static final Color C85_95 = new Color(30, 130, 0);
	static final Color C95_100 = new Color(0, 90, 0);
//	static final Color C100_ = new Color(0, 0, 255); // blue
//	static final Color C100_ = new Color(70, 70, 200); // purplegreyblue
//	static final Color C100_ = new Color(130, 160, 235); // greyblue
	static final Color C100_ = new Color(0x55, 0xBB, 0xEE); // #66CCFF	#3399CC
//	= new Color(150, 180, 255);
	static final int addx = 60;
	//----------------------------------------------	
	Color color;
	Stroke stroke;
	Graphics2D graphics;
	int xStart;
	int yStart;
	int xEnd;
	int yEnd;
	String simValue;
	float simValueFloat;
	boolean fromSel;
	boolean middleLine;
	CubicCurve2D myCurve = null;
	Line2D.Double line = null;
	int x =-1, y =-1;
	
	
	public Line(Object _parent, Graphics2D _graphics, int _xStart, int _yStart, int _xEnd,
			int _yEnd, String _simValue, boolean _fromSel, boolean _middleLine) {
		super();
		color = _graphics.getColor();	
		float width = (new BasicStroke()).getLineWidth() * 2;
		stroke = new BasicStroke(width);
		graphics = _graphics;
		xStart = _xStart;
		yStart = _yStart;
		xEnd = _xEnd;
		yEnd = _yEnd;
		simValue = _simValue;
		fromSel = _fromSel;
		middleLine = _middleLine;
		if(simValue!=null && 
				(!(_parent instanceof LinesComponent3) ||  _graphics.getColor().equals(MainWindow.GLOBAL))) {
			Float t = new Float(simValue);

			// specify line color			
			simValueFloat = t.floatValue();
			color = getColorForSim(simValueFloat);
			stroke = getStrokeForSim(simValueFloat, width);
		}
	}
	
	public static BasicStroke getStrokeForSim(float sim, float width){
		BasicStroke stroke = null;
		// e.g. equal or normal
		stroke = new BasicStroke(width);
		return stroke;
	}
	
	public static Color getColorForSim(float sim){
		if (sim < 0.05) {
			return C0_05;
		} else if (sim > 1) { // e.g. eq, isa, invisa of a line
			return C100_;
		} else if (sim > 0.95) {
			return C95_100;
		} else if (sim < 0.15) {
			return C05_15;
		} else if (sim < 0.25) {
			return C15_25;
		} else if (sim < 0.35) {
			return C25_35;
		} else if (sim < 0.45) {
			return C35_45;
		} else if (sim < 0.55) {
			return C45_55;
		} else if (sim < 0.65) {
			return C55_65;
		} else if (sim < 0.75) {
			return C65_75;
		} else if (sim < 0.85) {
			return C75_85;
		} else if (sim < 0.95) {
			return C85_95;
		} else {
			return C45_55;
		}
	}
	

	public void repaint() {
		graphics.setColor(color);
		graphics.setStroke(stroke);
		if (middleLine) {
			if (myCurve==null){
				// create a cubic curve
				int addy = Math.round((yEnd - yStart) / 4);
				myCurve = new CubicCurve2D.Double(
						xStart,	yStart, // point 1
						xStart + addx, yStart + addy, // control point c1
						xEnd + addx, yEnd - addy, // control point c2
						xEnd, yEnd); // point 2
			}
			graphics.draw(myCurve);
		} else {
			// calculate difference in height
			int diff = Math.abs(yEnd - yStart);	
			if (diff > 5) {
				if (myCurve==null){
					// if both elements aren't at the same height
					// create a cubic curve
					int add = Math.round((xEnd - xStart) / 3);
					myCurve = new CubicCurve2D.Double(
							xStart, yStart, // point 1
							xStart + add, yStart, // control point c1
							xEnd - add, yEnd, // control point c2
							xEnd, yEnd); // point 2
				}
				graphics.draw(myCurve);
			} else {
				if (line==null){
					// create a line
					line = new Line2D.Double(xStart, yStart, xEnd, yEnd);
				}
				graphics.draw(line);
			}
		}
		if (simValue != null && fromSel) {
			// if line is selected, paint the similarity value	
			Color oldColor = graphics.getColor();
			graphics.setFont(MainWindow.FONT14);			
			if (x==-1 && y==-1){
				// calculate the position of the similarity value
				x = xStart + Math.abs(Math.round((xEnd - xStart) / 2));
				y = yStart;
				if (yStart > yEnd) {
					y -= Math.abs(Math.round((yEnd - yStart) / 2));
				} else {
					y += Math.abs(Math.round((yEnd - yStart) / 2));
				}
				y+=5;
			}
			// draw the outline part of the similarity value, e.g. surrounded with black
			graphics.setColor(valueBackground);
			if (middleLine) {
				graphics.drawString(simValue, x+ 1 + addx - 10, y);
				graphics.drawString(simValue, x- 1 + addx - 10, y);
			} else {
				graphics.drawString(simValue, x+ 1, y);
				graphics.drawString(simValue, x, y+1);
				graphics.drawString(simValue, x- 1, y);
				graphics.drawString(simValue, x, y-1);
			}
			// draw the filling part of the similarity value, e.g. filled in green
			graphics.setColor(oldColor);
			if (middleLine) {
				graphics.drawString(simValue, x  + addx - 10, y + 1);
			} else {
				graphics.drawString(simValue, x , y);
			}
		}
	}

	public void paint(Graphics2D _graphics) {
		graphics = _graphics;
		repaint();
	}
}