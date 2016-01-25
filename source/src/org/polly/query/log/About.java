package org.polly.query.log;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
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
				txtpnChangelog.setText("Version 2.0.0\r\n+ New GUI\r\n+ Now a search can follow more than one requet at time\r\nVersion 1.0.0 - Release Date: 2016-01-20\r\n+ First release");
				scrollPane.setViewportView(txtpnChangelog);
			}
		}
	}

}
