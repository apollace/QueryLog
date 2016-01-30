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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class ErrorManager extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();


	/**
	 * Create the dialog.
	 */
	public ErrorManager(Exception e) {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBackground(Color.WHITE);
		setTitle("Error Manager");
		setBounds(100, 100, 685, 483);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			panel.setBackground(Color.WHITE);
			contentPanel.add(panel, BorderLayout.NORTH);
			{
				JLabel lblOpps = new JLabel("I am sorry :-( ");
				lblOpps.setFont(new Font("Tahoma", Font.PLAIN, 30));
				panel.add(lblOpps);
			}
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				JTextPane txtpnExceptionpanel = new JTextPane();
				txtpnExceptionpanel.setEditable(false);
				txtpnExceptionpanel.setText("ExceptionPanel");
				scrollPane.setViewportView(txtpnExceptionpanel);
				
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				txtpnExceptionpanel.setText(e.getMessage() + "\n--\n" + exceptionAsString);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BorderLayout(0, 0));
			{
				JTextPane txtpnPlaseReportThis = new JTextPane();
				txtpnPlaseReportThis.setEditable(false);
				txtpnPlaseReportThis.setText("Plase report this problem on GitHub Issue management: https://github.com/apollace/QueryLog/issues");
				buttonPane.add(txtpnPlaseReportThis, BorderLayout.NORTH);
			}
		}
	}

}
