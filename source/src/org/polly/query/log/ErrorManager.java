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
