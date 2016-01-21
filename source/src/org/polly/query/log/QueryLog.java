package org.polly.query.log;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import java.awt.Dimension;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

public class QueryLog {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					QueryLog window = new QueryLog();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public QueryLog() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 658, 419);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JSplitPane mainSplitPane = new JSplitPane();
		mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		
		JSplitPane requestsSplitPane = new JSplitPane();
		requestsSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setRightComponent(requestsSplitPane);		
		
		JPanel requestsPanel = new JPanel();
		requestsPanel.setBorder(new TitledBorder(null, "Requests", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		requestsSplitPane.setLeftComponent(requestsPanel);
		requestsPanel.setLayout(new BorderLayout(0, 0));
		
		JList list = new JList();
		list.setPreferredSize(new Dimension(150, 0));
		requestsPanel.add(list, BorderLayout.CENTER);
		
		JPanel selectedRequestPane = new JPanel();
		requestsSplitPane.setRightComponent(selectedRequestPane);
		selectedRequestPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		selectedRequestPane.add(scrollPane, BorderLayout.CENTER);
		
		JTextPane txtpnRequestAnalyzer = new JTextPane();
		txtpnRequestAnalyzer.setEditable(false);
		txtpnRequestAnalyzer.setFont(new Font("Monospaced", Font.PLAIN, 11));
		scrollPane.setViewportView(txtpnRequestAnalyzer);
		
		JPanel queryPanel = new JPanel();
		queryPanel.setBorder(new TitledBorder(null, "Query", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainSplitPane.setLeftComponent(queryPanel);
		queryPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		queryPanel.add(scrollPane_1, BorderLayout.CENTER);
		
		JTextPane txtpnQuery = new JTextPane();
		txtpnQuery.setFont(new Font("Monospaced", Font.PLAIN, 11));
		scrollPane_1.setViewportView(txtpnQuery);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpenLogsFolder = new JMenuItem("Open logs folder");
		mntmOpenLogsFolder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnFile.add(mntmOpenLogsFolder);
		
		JMenuItem mntmReloadLastLogs = new JMenuItem("Reload last logs folder");
		mntmReloadLastLogs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		mnFile.add(mntmReloadLastLogs);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmSaveQueryResult = new JMenuItem("Save query result");
		mntmSaveQueryResult.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(mntmSaveQueryResult);
		
		JMenuItem mntmSaveQueryResult_1 = new JMenuItem("Save query result only selected request");
		mntmSaveQueryResult_1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		mnFile.add(mntmSaveQueryResult_1);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		mnFile.add(mntmExit);
		
		JMenu mnQuery = new JMenu("Query");
		menuBar.add(mnQuery);
		
		JMenuItem mntmRunQuery = new JMenuItem("Run query");
		mntmRunQuery.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnQuery.add(mntmRunQuery);
		
		JMenuItem mntmStopQuery = new JMenuItem("Stop query");
		mntmStopQuery.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		mnQuery.add(mntmStopQuery);
	}

}
