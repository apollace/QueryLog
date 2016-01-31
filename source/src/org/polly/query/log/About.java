/**
 * This file belonging to QueryLog an open source tool to search and trace
 * information contained in your logs.  
 * Copyright (C) 2016  Alessandro Pollace
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.polly.query.log;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import java.awt.Color;
import javax.swing.JScrollPane;

public class About extends JDialog {
	private static final long serialVersionUID = -5268378363452705371L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			About dialog = new About();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public About() {
		setBounds(100, 100, 639, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lblQuerylog = new JLabel("QueryLog");
			lblQuerylog.setHorizontalAlignment(SwingConstants.CENTER);
			lblQuerylog.setFont(new Font("Tahoma", Font.PLAIN, 18));
			contentPanel.add(lblQuerylog, BorderLayout.NORTH);
		}
		{
			JTextPane txtpnCredit = new JTextPane();
			txtpnCredit.setEditable(false);
			txtpnCredit.setText("Developed by Alessandro Pollace (github.com/apollace or it.linkedin.com/in/alessandropollace)");
			contentPanel.add(txtpnCredit, BorderLayout.SOUTH);
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				JTextPane txtpnChangelog = new JTextPane();
				txtpnChangelog.setText("Version 3.0.0 - Release Date: 2016-01-31\r\n+ Now QeryLog follow multiple requests simultaneously\r\n+ Improved error management\r\nVersion 2.0.0 - Release Date: 2016-01-30\r\n+ New GUI\r\n+ Search is performed by a new thread\r\nVersion 1.0.0 - Release Date: 2016-01-20\r\n+ First release");
				scrollPane.setViewportView(txtpnChangelog);
			}
		}
	}

}
