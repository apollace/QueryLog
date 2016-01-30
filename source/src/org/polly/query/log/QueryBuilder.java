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
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

public class QueryBuilder extends JFrame {
	private static final long serialVersionUID = -2017839544769425892L;
	private JSplitPane contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public QueryBuilder() {
		setTitle("Query builder");
		setBounds(100, 100, 450, 300);
		contentPane = new JSplitPane();
		contentPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.setLeftComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);

		JTextPane txtpnQuery = new JTextPane();
		scrollPane.setViewportView(txtpnQuery);

		JLabel lblQuery = new JLabel("Query");
		panel.add(lblQuery, BorderLayout.WEST);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBorder(new TitledBorder(null, "Insert your test log here", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		contentPane.setRightComponent(scrollPane_1);

		JTextPane txtpnLogSample = new JTextPane();
		scrollPane_1.setViewportView(txtpnLogSample);
	}

}
