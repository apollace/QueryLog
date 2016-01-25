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

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.polly.query.log.controller.QueryController;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;

import java.awt.Dimension;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.awt.event.InputEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class QueryLog {

	private JFrame frame;
	private JTextPane txtpnRequestAnalyzer;
	private String logPath = "";

	private QueryController queryController = new QueryController();

	private final static String PROP_FILENAME = "QueryLog.settings";
	private Properties properties = new Properties();

	// Keys used to store properties
	private final static String LAST_USED_LOG_DIR = "last_sed_log_dir";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public QueryLog() throws IOException {
		try {
			properties.load(new FileReader(PROP_FILENAME));
		} catch (FileNotFoundException e) {
			// It is first start, the file cannot be exist
		}
		initialize();
	}

	private String getProperty(String key) {
		return properties.getProperty(key);
	}

	private void setProperty(String key, String property) {
		properties.setProperty(key, property);

		File f = new File(PROP_FILENAME);
		OutputStream out;
		try {
			out = new FileOutputStream(f);
			properties.store(out, "Autosaved settings");
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
		}
	}

	private void startWaitLoop() {
		String newData = queryController.getNewMatches();
		if (newData != null && newData.length() > 0) {
			try {
				Document doc = txtpnRequestAnalyzer.getDocument();
				doc.insertString(doc.getLength(), newData, null);
			} catch (BadLocationException exc) {
				exc.printStackTrace();
			}
		}
		if (queryController.getAdvancement() == 100) {
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				startWaitLoop();
			}
		});
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

		txtpnRequestAnalyzer = new JTextPane();
		txtpnRequestAnalyzer.setEditable(false);
		txtpnRequestAnalyzer.setFont(new Font("Monospaced", Font.PLAIN, 11));
		scrollPane.setViewportView(txtpnRequestAnalyzer);

		JPanel queryPanel = new JPanel();
		queryPanel.setBorder(new TitledBorder(null, "Query", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainSplitPane.setLeftComponent(queryPanel);
		queryPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		queryPanel.add(scrollPane_1, BorderLayout.CENTER);

		final JTextPane txtpnQuery = new JTextPane();
		txtpnQuery.setFont(new Font("Monospaced", Font.PLAIN, 11));
		scrollPane_1.setViewportView(txtpnQuery);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpenLogsFolder = new JMenuItem("Open logs folder");
		mntmOpenLogsFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Select your log folder");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showSaveDialog(QueryLog.this.frame) == JFileChooser.CANCEL_OPTION) {
					return;
				}
				QueryLog.this.logPath = fc.getSelectedFile().getAbsolutePath();
				setProperty(LAST_USED_LOG_DIR, logPath);
			}
		});
		mntmOpenLogsFolder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnFile.add(mntmOpenLogsFolder);

		JMenuItem mntmReloadLastLogs = new JMenuItem("Reload last logs folder");
		mntmReloadLastLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logPath = getProperty(LAST_USED_LOG_DIR);
			}
		});
		mntmReloadLastLogs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		mnFile.add(mntmReloadLastLogs);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmSaveQueryResult = new JMenuItem("Save query result");
		mntmSaveQueryResult.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(mntmSaveQueryResult);

		JMenuItem mntmSaveQueryResult_1 = new JMenuItem("Save query result only selected request");
		mntmSaveQueryResult_1
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		mnFile.add(mntmSaveQueryResult_1);

		JMenu mnQuery = new JMenu("Query");
		menuBar.add(mnQuery);

		JMenuItem mntmRunQuery = new JMenuItem("Run query");
		mntmRunQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queryController.search(txtpnQuery.getText(), logPath);
				startWaitLoop();
			}
		});
		mntmRunQuery.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnQuery.add(mntmRunQuery);

		JMenuItem mntmStopQuery = new JMenuItem("Stop query");
		mntmStopQuery.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		mnQuery.add(mntmStopQuery);
	}

}
