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
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.wdilab.coma.gui.GUIConstants;
import de.wdilab.coma.gui.MainWindow;

/**
 * A basic implementation of the JDialog class. dialog "about" => name and
 * editors of this project
 * 
 * @author Sabine Massmann
 */
public class Dlg_About extends Dlg {
	/*
	 * Constructor of AboutDialog
	 */
	public Dlg_About(JFrame _parent) {
		super(_parent, GUIConstants.INFORMATION);
		init();
	}

	/*
	 * create the elements for this dialog with the given about information
	 * (only button: OK-Button = to close the dialog)
	 */
	private void init() {
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		Container mainPanel = new JPanel(new GridLayout(9, 1));
		Box mainPanel_h = Box.createHorizontalBox();
		mainPanel_h.add(Box.createHorizontalStrut(10));
		mainPanel_h.add(mainPanel);
		mainPanel_h.add(Box.createHorizontalStrut(10));
		Box mainPanel_v = Box.createVerticalBox();
		mainPanel_v.add(Box.createVerticalStrut(10));
		mainPanel_v.add(mainPanel_h);
		mainPanel_v.add(Box.createVerticalStrut(10));
		getContentPane().add(mainPanel_v, BorderLayout.CENTER);
		// set the content of this about dialog
		JLabel label = new JLabel(GUIConstants.ABOUT1, SwingConstants.CENTER);
		label.setFont(MainWindow.FONT24);
		mainPanel.add(label);
		//		java.util.Calendar cal = java.util.Calendar.getInstance();
		//		cal.setTime(new java.util.Date());
		//		String date = cal.get(Calendar.YEAR) + GUIConstants.DASH +
		// (cal.get(Calendar.MONTH)+1) + GUIConstants.DASH + cal.get(Calendar.DATE) ;
		//		label = new JLabel(GUIConstants.VERSION+date);
		label = new JLabel(GUIConstants.VERSION_DATE, SwingConstants.CENTER);
		label.setFont(MainWindow.FONT12);
		mainPanel.add(label);
		label = new JLabel(GUIConstants.ABOUT2_1, SwingConstants.CENTER);
		label.setFont(MainWindow.FONT12_BOLD);
		mainPanel.add(label);
		label = new JLabel(GUIConstants.ABOUT2_2, SwingConstants.CENTER);
		label.setFont(MainWindow.FONT12_BOLD);
		mainPanel.add(label);
		label = new JLabel(GUIConstants.ABOUT2_3, SwingConstants.CENTER);
		label.setFont(MainWindow.FONT12_BOLD);
		mainPanel.add(label);
		label = new JLabel(GUIConstants.EMPTY, SwingConstants.CENTER);
		label.setFont(MainWindow.FONT14);
		mainPanel.add(label);
		label = new JLabel(GUIConstants.ABOUT3, SwingConstants.CENTER);
		label.setFont(MainWindow.FONT12);
		mainPanel.add(label);
		label = new JLabel(GUIConstants.ABOUT4, SwingConstants.CENTER);
		label.setFont(MainWindow.FONT12);
		mainPanel.add(label);
		JButton OkBtn = new JButton(GUIConstants.BUTTON_OK);
		OkBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button is pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				setVisible(false);
			}
		});
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(OkBtn);
		mainPanel.add(panel);
		pack();
	}
}