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
import javax.swing.text.DefaultCaret;

import org.polly.query.log.controller.ProjectController;
import org.polly.query.log.controller.ProjectController.ProjectException;
import org.polly.query.log.controller.ProjectController.PropertyListener;
import org.polly.query.log.controller.QueryController;
import org.polly.query.log.utils.QueryLogProjectFileFilter;
import org.polly.query.log.widget.JTextPaneNoWrap;

import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;

import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.event.InputEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.JProgressBar;
import javax.swing.JList;
import java.awt.Dimension;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JSeparator;

public class QueryLog {
	private String projectSavedPath = null;
	private static final ProjectController project = new ProjectController();

	private DefaultListModel<String> headerListModel = new DefaultListModel<String>();
	private JList<String> headers = new JList<String>(headerListModel);

	private JTextPane txtpnQuery = new JTextPane();
	private JTextPane textPaneResults = new JTextPaneNoWrap();
	private JFrame frame = null;
	private JFrame frmQuerylog;
	private JProgressBar progressBar = new JProgressBar();

	private QueryController queryController = new QueryController();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					QueryLog window = new QueryLog();
					window.frmQuerylog.setVisible(true);
				} catch (Exception e) {
					new ErrorManager(e).setVisible(true);
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
		initialize();
	}

	private void startWaitLoop() {
		String headers[] = queryController.getRequestsHeader();
		for (String header : headers) {
			if (!headerListModel.contains(header)) {
				headerListModel.addElement(header);
			}
		}

		int advancement = queryController.getAdvancement();
		progressBar.setValue(advancement);
		if (advancement == 100) {
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
		frmQuerylog = new JFrame();
		frmQuerylog.setTitle("QueryLog");
		frmQuerylog.setBounds(100, 100, 658, 419);
		frmQuerylog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JSplitPane mainSplitPane = new JSplitPane();
		mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frmQuerylog.getContentPane().add(mainSplitPane, BorderLayout.CENTER);

		JPanel queryPanel = new JPanel();
		queryPanel.setBorder(new TitledBorder(null, "Query", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainSplitPane.setLeftComponent(queryPanel);
		queryPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		queryPanel.add(scrollPane_1);

		txtpnQuery.setFont(new Font("Monospaced", Font.PLAIN, 11));
		scrollPane_1.setViewportView(txtpnQuery);

		JPanel propertyPanel = new JPanel();
		queryPanel.add(propertyPanel, BorderLayout.SOUTH);
		propertyPanel.setLayout(new GridLayout(4, 0, 0, 0));

		JLabel lblLogPath = new JLabel("Log path");
		lblLogPath.setFont(new Font("Tahoma", Font.BOLD, 11));
		propertyPanel.add(lblLogPath);

		final JLabel lblLogPathView = new JLabel("Plase select a log directory via File menu");
		propertyPanel.add(lblLogPathView);
		project.addListener(ProjectController.LOGS_FOLDER, new PropertyListener() {
			@Override
			public void onChange(String propertyValue) {
				lblLogPathView.setText(propertyValue);
			}
		});

		JLabel lblAdvancement = new JLabel("Advancement");
		lblAdvancement.setFont(new Font("Tahoma", Font.BOLD, 11));
		propertyPanel.add(lblAdvancement);

		propertyPanel.add(progressBar);

		JSplitPane splitPane = new JSplitPane();
		mainSplitPane.setRightComponent(splitPane);

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);

		// Disable line wrap
		DefaultCaret caret = (DefaultCaret) textPaneResults.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

		// Set properties of result panel
		textPaneResults.setFont(new Font("Monospaced", Font.PLAIN, 11));
		textPaneResults.setEditable(false);
		scrollPane.setViewportView(textPaneResults);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(200, 10));
		panel.setBorder(new TitledBorder(null, "Request headers", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setLeftComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_2 = new JScrollPane();
		panel.add(scrollPane_2, BorderLayout.CENTER);
		headers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String content = queryController.getRequestContent(headers.getSelectedValue());
				textPaneResults.setText(content);
			}
		});

		headers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_2.setViewportView(headers);

		JMenuBar menuBar = new JMenuBar();
		frmQuerylog.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpenLogsFolder = new JMenuItem("Open logs folder");
		mntmOpenLogsFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuSetLogFolder();
			}
		});

		JMenuItem mntmSaveProject = new JMenuItem("Save project");
		mntmSaveProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuSaveProject();
			}
		});
		mntmSaveProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(mntmSaveProject);

		JMenuItem mntmReloadLastLogs = new JMenuItem("Open project");
		mntmReloadLastLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuOpenProject();
			}
		});
		mntmReloadLastLogs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnFile.add(mntmReloadLastLogs);

		JMenuItem mntmSaveProjectAs = new JMenuItem("Save project as");
		mntmSaveProjectAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuSaveProjectAs();
			}
		});
		mntmSaveProjectAs
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnFile.add(mntmSaveProjectAs);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		mntmOpenLogsFolder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
		mnFile.add(mntmOpenLogsFolder);

		JMenu mnQuery = new JMenu("Query");
		menuBar.add(mnQuery);

		JMenuItem mntmRunQuery = new JMenuItem("Run query");
		mntmRunQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				headerListModel.clear();

				String query = txtpnQuery.getText();
				String logFolder = project.getProperty(ProjectController.LOGS_FOLDER);

				queryController.search(query, logFolder);
				startWaitLoop();
			}
		});
		mntmRunQuery.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnQuery.add(mntmRunQuery);

		JMenuItem mntmStopQuery = new JMenuItem("Stop query");
		mntmStopQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queryController.stopSearch();
			}
		});
		mntmStopQuery.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		mnQuery.add(mntmStopQuery);

		JMenu menu = new JMenu("?");
		menuBar.add(menu);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new About().setVisible(true);
			}
		});
		menu.add(mntmAbout);
	}

	private void saveProject() {
		project.setProperty(ProjectController.QUERY, txtpnQuery.getText());
		try {
			project.saveProject(projectSavedPath);
		} catch (ProjectException e) {
			// TODO
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void menuSaveProject() {
		if (projectSavedPath == null) {
			menuSaveProjectAs();
		}

		if (!projectSavedPath.endsWith(".qlp"))
			projectSavedPath = projectSavedPath + ".qlp";
		saveProject();
	}

	/**
	 * 
	 */
	private void menuSaveProjectAs() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save project");
		fc.setFileFilter(new QueryLogProjectFileFilter());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fc.showSaveDialog(QueryLog.this.frame) == JFileChooser.CANCEL_OPTION) {
			return;
		}

		projectSavedPath = fc.getSelectedFile().getAbsolutePath();
		saveProject();
	}

	/**
	 * 
	 */
	private void menuOpenProject() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open Project");
		fc.setFileFilter(new QueryLogProjectFileFilter());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fc.showOpenDialog(QueryLog.this.frame) == JFileChooser.CANCEL_OPTION) {
			return;
		}

		projectSavedPath = fc.getSelectedFile().getAbsolutePath();
		try {
			project.openProject(projectSavedPath);
		} catch (ProjectException e) {
			// TODO
			e.printStackTrace();
		}

		txtpnQuery.setText(project.getProperty(ProjectController.QUERY));
	}

	private void menuSetLogFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select your log folder");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showSaveDialog(QueryLog.this.frame) == JFileChooser.CANCEL_OPTION) {
			return;
		}
		project.setProperty(ProjectController.LOGS_FOLDER, fc.getSelectedFile().getAbsolutePath());
	}
}
