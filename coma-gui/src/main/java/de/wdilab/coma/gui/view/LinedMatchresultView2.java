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

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

import de.wdilab.coma.gui.Controller;

/**
 * LinedMatchresultView contains the MatchresultView with the LinesComponten to draw the
 * lines of a loaded matchresult. The lines (correspondences) are between a source and 
 * a target model.
 * 
 * @author Sabine Massmann
 */
public class LinedMatchresultView2 extends JLayeredPane {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	final static int LABEL_ABOVE = 18;
	final static int LABEL_BELOW = 135; // 35 without search, 55 with only search;
	final static int SIM_WIDHT = 0;
	final static int STD_HEIGHT =8; 
	//----------------------------------------------
	MatchresultView2 mw;
	double DIV_LOCATION_MATCHRESULT = 0.5;
	LinesComponent2 linesComponent;
	SimComponent2	simComponent;
	public LinedMatchresultView2(Controller _controller) {
		super();
		init2(_controller);
	}

	void init2(Controller _controller) {
		mw = new MatchresultView2(this, _controller);
		add(mw);
		setLayer(mw, JLayeredPane.DEFAULT_LAYER.intValue());
		//LinePane
		linesComponent = new LinesComponent2(_controller);
		add(linesComponent);
		// position of the linesComponent in front of the trees
		setLayer(linesComponent, JLayeredPane.DEFAULT_LAYER
				.intValue() + 1);
		
		simComponent = new SimComponent2(_controller);
		add(simComponent);
		setLayer(simComponent, JLayeredPane.DEFAULT_LAYER
				.intValue() + 2);
		
		mw.setDividerLocation(DIV_LOCATION_MATCHRESULT);
	}

	public void setBounds(int _x, int _y, int _width, int _height) {
		//		DIV_LOCATION_MATCHRESULT = (float) mw.getDividerLocation()
		//		/ mw.getWidth();
		//		int currentDiv = mw.getDividerLocation();
		//		int currentWidth = mw.getWidth();
		super.setBounds(_x, _y, _width, _height);
		mw.setBounds(0, 0, _width + 1, _height + 1);
//		mw.setDividerLocation(DIV_LOCATION_MATCHRESULT);
		if (mw instanceof MatchresultView3){
//			linesComponent.setBounds(0, LABEL_ABOVE, _width, _height - LABEL_BELOW); // wrong: lines to short
			linesComponent.setBounds(0, LABEL_ABOVE, _width, _height-2*LABEL_ABOVE); // lines okay -> no search in matchresultview3
			simComponent.setVisible(false);
		} else {
			linesComponent.setBounds(0, LABEL_ABOVE, _width, _height - LABEL_BELOW);
			int div = mw.getDividerLocation();
			int divSize = mw.getDividerSize();

			simComponent.setBounds(div-SIM_WIDHT, _height + 50 - LABEL_BELOW, divSize+2*SIM_WIDHT, 65);
			simComponent.setVisible(true);
		}
		//		mw.setDividerLocation(DIV_LOCATION_MATCHRESULT);
	}

	public void repaint() {
//		mw.repaint();
		EventQueue.invokeLater( new Runnable() {
			public void run() {
				mw.repaint();
				simComponent.repaint();
			}
		});

	}

	/*
	 * returns the LinesComponent2
	 */
	public LinesComponent2 getLinesComponent() {
		return linesComponent;
	}

	/*
	 * returns the MatchresultView2
	 */
	public MatchresultView2 getMatchresultView() {
		return mw;
	}

	public void setSize(Dimension _dimension) {
		super.setSize(_dimension);
//		System.out.println("setNewSize||||  dimension: " + _dimension);
	}

	public void setSize(int _x, int _y) {
		super.setSize(_x, _y);
//		System.out.println("setNewSize||||  width: " + _x + "  height: " + _y);
	}

	public void setBounds(Rectangle _rectangle) {
		super.setBounds(_rectangle);
//		System.out.println("setBounds||||  Rectangle: " + _rectangle);
	}

	public void showNextLine(boolean down) {
		linesComponent.showNextLine(down);
		
	}
	
	void setSim(float _nameSim, float _pathSim, float _commentSim, float _instanceSim){
		simComponent.setSim(_nameSim, _pathSim, _commentSim, _instanceSim);
		mw.repaint();
		simComponent.repaint();
	}
	
	void resetSim(){
		simComponent.resetSim();
		mw.repaint();
		simComponent.repaint();
	}
	
	class SimComponent2 extends JComponent{
		Controller controller;
		int[] pos = {5, 21, 37, 53};
		
		float[] sim = new float[pos.length];
		

		
		
		public SimComponent2(Controller _controller) {
			super();
			setOpaque(false);
			controller = _controller;
			 resetSim();
		}
		
		void setSim(float _nameSim, float _pathSim, float _commentSim, float _instanceSim){
			sim[0] = _nameSim;
			sim[1] = _pathSim;
			sim[2] = _commentSim;
			sim[3] = _instanceSim;		
		}
		
		void resetSim(){		
			sim[0] = -1;
			sim[1] = -1;
			sim[2] = -1;
			sim[3] = -1;		
		}
		

		public void paint(Graphics _graphics) {
			super.paint(_graphics);
			try {
//				_graphics.setColor(Color.yellow);
				int x = (int)simComponent.getVisibleRect().getX();
//				int y = (int)simComponent.getVisibleRect().getY();
				int width = (int)simComponent.getVisibleRect().getWidth()-1;
//				int height = (int)simComponent.getVisibleRect().getHeight()-1;
//				_graphics.drawRect(x, y, width, height);
				
//				int middleX =x + (int) width/2;
				for (int i = 0; i < pos.length; i++) {
					if (sim[i]<0){
						continue;
					}
					_graphics.setColor(Line.getColorForSim(sim[i]));
					_graphics.drawRect(x, pos[i], width, STD_HEIGHT);	
					_graphics.fillRect(x, pos[i], width, STD_HEIGHT);	

					
//					int middleY = (int) STD_HEIGHT/2 +pos[i];
//				    Polygon p = new Polygon();
//				    p.addPoint(x, middleY); // left
////				    System.out.println(x + " " + middleY);
//				    p.addPoint(middleX, pos[i]); // above
////				    System.out.println(middleX + " " + pos[i]);
//				    p.addPoint(x+width, middleY); // right
////				    System.out.println(x+width + " " + middleY);
//				    p.addPoint(middleX, pos[i]+STD_HEIGHT); // buttom
////				    System.out.println(middleX + " " + (pos[i]+STD_HEIGHT));
//				    _graphics.fillPolygon(p);

					
				}
//				int x2 = (int)simComponent.getX();
//				int y2 = (int)simComponent.getY();
//				int width2 = (int)simComponent.getWidth();
//				int height2 = (int)simComponent.getHeight();

			} catch (RuntimeException e) {
				
			}
		}
		
		public void repaint() {			
			paint(getGraphics());
		}
	}
}