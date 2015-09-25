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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import de.wdilab.coma.gui.extjtree.ExtJTree;
import de.wdilab.coma.gui.view.MatchresultView2;

/**
 * This class contains a scroll pane with an attached mouse adapter. It is used to contain the trees 
 * of the source, middle and target model.
 * 
 * @author Sabine Massmann
 */
public class DefaultScrollPane extends JScrollPane {
	MatchresultView2 view;

	public DefaultScrollPane(ExtJTree _tree, MatchresultView2 _view) {
		super(_tree);
		view = _view;
		this
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		this
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent _event) {
				view.setChanged(true);
			}
		});
		getVerticalScrollBar().addMouseListener(
				new ScrollBarMouseAdapter(view));
		getHorizontalScrollBar().addMouseListener(
				new ScrollBarMouseAdapter(view));
		Component[] cs = getVerticalScrollBar().getComponents();
		if (cs.length > 0) {
			for (int i = 0; i < cs.length; i++) {
				cs[i].addMouseListener(new ScrollBarMouseAdapter(view));
			}
		}
		cs = getHorizontalScrollBar().getComponents();
		if (cs.length > 0) {
			for (int i = 0; i < cs.length; i++) {
				cs[i].addMouseListener(new ScrollBarMouseAdapter(view));
			}
		}
		getVerticalScrollBar().addMouseMotionListener(
				new MouseMotionAdapter() {
					public void mouseDragged(MouseEvent _event) {
						view.setChanged(true);
					}
				});
		getHorizontalScrollBar().addMouseMotionListener(
				new MouseMotionAdapter() {
					public void mouseDragged(MouseEvent _event) {
						view.setChanged(true);
					}
				});
	}
}


/*
 * ScrollBarMouseAdapter extends MouseAdapter for scrollbars
 */
class ScrollBarMouseAdapter extends MouseAdapter {
	private MatchresultView2 view;

	/*
	 * Constructor for ScrollBarMouseAdapter
	 */
	public ScrollBarMouseAdapter(MatchresultView2 _matchresultView) {
		view = _matchresultView;
	}

	/*
	 * if mouse was pressed repaint Matchresultview (incl. current lines)
	 */
	public void mousePressed(MouseEvent _event) {
		view.setChanged(true);
	}

	/*
	 * if mouse is released repaint Matchresultview (incl. current lines)
	 */
	public void mouseReleased(MouseEvent _event) {
		//				view.repaint();
	}
}
