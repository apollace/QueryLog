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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.polly.query.log.controller.ProjectController;
import org.polly.query.log.controller.ProjectController.ProjectException;
import org.polly.query.log.controller.ProjectController.PropertyListener;
import org.polly.query.log.controller.QueryController;
import org.polly.query.log.utils.QueryLogProjectFileFilter;
import org.polly.query.log.widget.JTextPaneNoWrap;

/**
 * 
 * @author Alessandro Pollace
 */
public class QueryLog {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
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

	/*
	 * UI component required as class attribute
	 */
	private JList<String> requestHeadersList = new JList<String>(this.headerListModel);
	private JTextPane txtpnQuery = new JTextPane();
	private JTextPane textPaneResults = new JTextPaneNoWrap();
	private JFrame frmQuerylog = null;
	private JProgressBar progressBar = new JProgressBar();

	/*
	 * Controllers
	 */
	private static final ProjectController project = new ProjectController();
	private QueryController queryController = new QueryController();

	/*
	 * Data
	 */
	private String projectSavedPath = null;
	private DefaultListModel<String> headerListModel = new DefaultListModel<String>();

	/**
	 * Create the application.
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public QueryLog() throws IOException {
		this.initialize();
	}

	/**
	 *
	 * @return
	 */
	private JSplitPane buildMainWindowStructure() {
		this.frmQuerylog = new JFrame();
		this.frmQuerylog.setTitle("QueryLog");
		this.frmQuerylog.setBounds(100, 100, 658, 419);
		this.frmQuerylog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JSplitPane mainSplitPane = new JSplitPane();
		mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.frmQuerylog.getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		return mainSplitPane;
	}

	/**
	 *
	 */
	private void buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		this.frmQuerylog.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpenLogsFolder = new JMenuItem("Open logs folder");
		mntmOpenLogsFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				QueryLog.this.menuSetLogFolder();
			}
		});

		JMenuItem mntmSaveProject = new JMenuItem("Save project");
		mntmSaveProject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				QueryLog.this.menuSaveProject();
			}
		});
		mntmSaveProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(mntmSaveProject);

		JMenuItem mntmOpenProject = new JMenuItem("Open project");
		mntmOpenProject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				QueryLog.this.menuOpenProject();
			}
		});
		mntmOpenProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnFile.add(mntmOpenProject);

		JMenuItem mntmSaveProjectAs = new JMenuItem("Save project as");
		mntmSaveProjectAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				QueryLog.this.menuSaveProjectAs();
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
			@Override
			public void actionPerformed(ActionEvent e) {
				QueryLog.this.menuRunQuery();
			}
		});
		mntmRunQuery.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnQuery.add(mntmRunQuery);

		JMenuItem mntmStopQuery = new JMenuItem("Stop query");
		mntmStopQuery.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				QueryLog.this.menuStopQuery();
			}
		});
		mntmStopQuery.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		mnQuery.add(mntmStopQuery);

		JMenu menuMoreInformation = new JMenu("?");
		menuBar.add(menuMoreInformation);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new About().setVisible(true);
			}
		});
		menuMoreInformation.add(mntmAbout);
	}

	/**
	 *
	 * @param mainSplitPane
	 */
	private void buildQueryPanel(JSplitPane mainSplitPane) {
		JPanel queryPanel = new JPanel();
		queryPanel.setBorder(new TitledBorder(null, "Query", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainSplitPane.setLeftComponent(queryPanel);
		queryPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane queryEditorScrollPAne = new JScrollPane();
		queryPanel.add(queryEditorScrollPAne);

		this.txtpnQuery.setFont(new Font("Monospaced", Font.PLAIN, 11));
		queryEditorScrollPAne.setViewportView(this.txtpnQuery);

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

		propertyPanel.add(this.progressBar);
	}

	/**
	 *
	 * @param mainSplitPane
	 */
	private void buildResultsPanel(JSplitPane mainSplitPane) {
		JSplitPane resultsSplitPane = new JSplitPane();
		mainSplitPane.setRightComponent(resultsSplitPane);

		JScrollPane restultsScrollPane = new JScrollPane();
		resultsSplitPane.setRightComponent(restultsScrollPane);

		// Set properties of result panel
		this.textPaneResults.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.textPaneResults.setEditable(false);
		restultsScrollPane.setViewportView(this.textPaneResults);

		JPanel requestHeadersPanel = new JPanel();
		requestHeadersPanel.setPreferredSize(new Dimension(200, 10));
		requestHeadersPanel.setBorder(
				new TitledBorder(null, "Request headers", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		resultsSplitPane.setLeftComponent(requestHeadersPanel);
		requestHeadersPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane requestHeadersScrollPane = new JScrollPane();
		requestHeadersPanel.add(requestHeadersScrollPane, BorderLayout.CENTER);
		this.requestHeadersList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String content = QueryLog.this.queryController
						.getRequestContent(QueryLog.this.requestHeadersList.getSelectedValue());
				QueryLog.this.textPaneResults.setText(content);
			}
		});

		this.requestHeadersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		requestHeadersScrollPane.setViewportView(this.requestHeadersList);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JSplitPane mainSplitPane = this.buildMainWindowStructure();
		this.buildQueryPanel(mainSplitPane);
		this.buildResultsPanel(mainSplitPane);
		this.buildMenuBar();
	}

	/**
	 *
	 */
	private void menuOpenProject() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open Project");
		fc.setFileFilter(new QueryLogProjectFileFilter());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fc.showOpenDialog(QueryLog.this.frmQuerylog) == JFileChooser.CANCEL_OPTION) {
			return;
		}

		this.projectSavedPath = fc.getSelectedFile().getAbsolutePath();
		try {
			project.openProject(this.projectSavedPath);
		} catch (ProjectException e) {
			// TODO
			e.printStackTrace();
		}

		this.txtpnQuery.setText(project.getProperty(ProjectController.QUERY));
	}

	/**
	 *
	 */
	private void menuRunQuery() {
		this.headerListModel.clear();

		String query = this.txtpnQuery.getText();
		String logFolder = project.getProperty(ProjectController.LOGS_FOLDER);

		this.queryController.search(query, logFolder);
		this.startWaitLoop();
	}

	/**
	 *
	 */
	private void menuSaveProject() {
		if (this.projectSavedPath == null) {
			this.menuSaveProjectAs();
		}

		if (!this.projectSavedPath.endsWith(".qlp")) {
			this.projectSavedPath = this.projectSavedPath + ".qlp";
		}
		this.saveProject();
	}

	/**
	 *
	 */
	private void menuSaveProjectAs() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save project");
		fc.setFileFilter(new QueryLogProjectFileFilter());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fc.showSaveDialog(QueryLog.this.frmQuerylog) == JFileChooser.CANCEL_OPTION) {
			return;
		}

		this.projectSavedPath = fc.getSelectedFile().getAbsolutePath();
		this.saveProject();
	}

	/**
	 *
	 */
	private void menuSetLogFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select your log folder");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showSaveDialog(QueryLog.this.frmQuerylog) == JFileChooser.CANCEL_OPTION) {
			return;
		}
		project.setProperty(ProjectController.LOGS_FOLDER, fc.getSelectedFile().getAbsolutePath());
	}

	/**
	 *
	 */
	private void menuStopQuery() {
		this.queryController.stopSearch();
	}

	/**
	 *
	 */
	private void saveProject() {
		project.setProperty(ProjectController.QUERY, this.txtpnQuery.getText());
		try {
			project.saveProject(this.projectSavedPath);
		} catch (ProjectException e) {
			// TODO
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	private void startWaitLoop() {
		String headers[] = this.queryController.getRequestsHeader();
		for (String header : headers) {
			if (!this.headerListModel.contains(header)) {
				this.headerListModel.addElement(header);
			}
		}

		int advancement = this.queryController.getAdvancement();
		this.progressBar.setValue(advancement);
		if (advancement == 100) {
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				QueryLog.this.startWaitLoop();
			}
		});
	}
}
