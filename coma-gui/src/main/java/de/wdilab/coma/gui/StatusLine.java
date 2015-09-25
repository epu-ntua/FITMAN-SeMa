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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * StatusLine extends JLabel and is used to display messages that come up, e.g.
 * information and error messages.
 * 
 * @author Sabine Massmann
 */
public class StatusLine extends JPanel {
	private Timer timer;
	JLabel label;
	private JProgressBar pb;

	/*
	 * Constructor of StatusLine
	 */
	public StatusLine(String _text) {
		super(new BorderLayout());
		label = new JLabel(_text);
		label.setToolTipText(_text);
		label.setFont(MainWindow.FONT12);
		label.setForeground(Color.BLACK);
		add(label, BorderLayout.CENTER);
		//ProgressBar
		pb = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
		pb.setString(GUIConstants.EMPTY);
		pb.setStringPainted(true);
		pb.setOpaque(true);
		add(pb, BorderLayout.EAST);
		pb.setVisible(true);
		// add a Timer, after 3 seconds change text font from important to
		// normal
		timer = new Timer(4000, new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				label.setFont(MainWindow.FONT12);
				label.setForeground(Color.BLACK);
				//				StatusLine.repaint();
			}
		});
		timer.setRepeats(false);
	}

	JProgressBar getProgressBar() {
		return pb;
	}

	void setStringVisible(boolean _visible) {
		if (_visible) {
			pb.setString(GUIConstants.COMA_BUSY);
		} else {
			pb.setString(GUIConstants.EMPTY);
		}
	}

	/*
	 * set the text to the given string
	 */
	public void setText(String _text) {
		label.setText(_text);
		label.setToolTipText(_text);
		label.setFont(MainWindow.FONT12_BOLD);
		label.setForeground(Color.BLUE);
		if (timer != null) {
			timer.restart();
		}
	}
}