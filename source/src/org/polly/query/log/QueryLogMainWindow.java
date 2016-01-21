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

import java.awt.BorderLayout;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.polly.query.log.queries.IQuery;
import org.polly.query.log.queries.QueryFactory;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import javax.swing.JProgressBar;

public class QueryLogMainWindow {
	public class MyListModel<T> extends AbstractListModel<T> {
		private static final long serialVersionUID = 2277715872531101021L;
		private List<T> record;

		public MyListModel(List<T> people) {
			this.record = people;
		}

		@Override
		public int getSize() {
			return record.size();
		}

		@Override
		public T getElementAt(int index) {
			return record.get(index);
		}
	}

	private JFrame frmQuerylog;
	private JTextField txtLogfolder;
	private JTextField txtPageSize;
	private JTextPane txtpnQuerypanel;
	private JList<Record> listLogs;
	private JProgressBar progressBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(
				            UIManager.getSystemLookAndFeelClassName());
					QueryLogMainWindow window = new QueryLogMainWindow();
					window.frmQuerylog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public QueryLogMainWindow() {
		initialize();
	}

	private void query() throws IOException {
		List<Record> recordList = new ArrayList<Record>();

		int recordNumber = 0;
		String[] queriesString = txtpnQuerypanel.getText().split("\n");
		List<IQuery> queries = new ArrayList<IQuery>();
		for (String queryString : queriesString) {
			if (queryString.trim().length() == 0) {
				continue;
			}

			IQuery query = QueryFactory.getInstance().getQuery();
			query.setQuery(queryString);
			queries.add(query);
		}

		// for each log file
		int fileManaged = 0;
		progressBar.setValue(0);

		File logFolder = new File(txtLogfolder.getText());
		for (File logFile : logFolder.listFiles()) {
			String line;
			BufferedReader br = new BufferedReader(new FileReader(logFile));
			int lineNumber = 0;
			while ((line = br.readLine()) != null) {
				lineNumber++;

				if (line.trim().length() == 0) {
					continue;
				}

				if (queries.size() == 0) {
					recordNumber = addLineToResult(recordNumber, line, recordList, logFile, lineNumber);
				}

				for (IQuery query : queries) {
					if (query.match(line.getBytes())) {
						recordNumber = addLineToResult(recordNumber, line, recordList, logFile, lineNumber);
						break;
					}
				}

				if (recordNumber > Integer.valueOf(txtPageSize.getText())) {
					br.close();
					listLogs.setModel(new MyListModel<Record>(recordList));
					return;
				}
			}
			br.close();

			fileManaged++;
			progressBar.setValue((int) ((double) fileManaged / (double) logFolder.listFiles().length * 100.0));

			// Force GUI update of progress bar
			progressBar.paintImmediately(0, 0, frmQuerylog.getWidth(), frmQuerylog.getHeight());
		}

		listLogs.setModel(new MyListModel<Record>(recordList));
	}

	private int addLineToResult(int recordNumber, String line, List<Record> recordList, File file, int lineNumber) {
		recordList.add(new Record(file, lineNumber, line));
		recordNumber++;
		return recordNumber;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmQuerylog = new JFrame();
		frmQuerylog.setTitle("QueryLog");
		frmQuerylog.setBounds(100, 100, 1024, 768);
		frmQuerylog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmQuerylog.getContentPane().setLayout(new BorderLayout(0, 0));

		JToolBar toolBar = new JToolBar();
		frmQuerylog.getContentPane().add(toolBar, BorderLayout.NORTH);

		JLabel lblFolder = new JLabel("Log Folder");
		toolBar.add(lblFolder);

		txtLogfolder = new JTextField();
		txtLogfolder.setFont(new Font("Courier New", Font.PLAIN, 11));
		toolBar.add(txtLogfolder);
		txtLogfolder.setColumns(10);

		final JButton btnQuery = new JButton("Query");
		btnQuery.setPreferredSize(new Dimension(120, 23));
		btnQuery.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					btnQuery.setEnabled(false);
					btnQuery.setText("Searching...");
					query();
					listLogs.invalidate();
					listLogs.revalidate();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					btnQuery.setText("Query");
					btnQuery.setEnabled(true);
				}
			}
		});

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frmQuerylog.getContentPane().add(splitPane, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane();
		JPanel queryPanel = new JPanel();
		scrollPane.setViewportView(queryPanel);
		splitPane.setLeftComponent(scrollPane);
		queryPanel.setLayout(new BorderLayout());
		
		txtpnQuerypanel = new JTextPane();
		txtpnQuerypanel.setFont(new Font("Courier New", Font.PLAIN, 11));
		queryPanel.add(txtpnQuerypanel,  BorderLayout.CENTER);

		JLabel lblQuery = new JLabel("Query:");
		queryPanel.add(lblQuery,  BorderLayout.WEST);
		queryPanel.add(btnQuery,  BorderLayout.EAST);

		JPanel panelNoWrap = new JPanel();
		panelNoWrap.setLayout(new BorderLayout());

		splitPane.setRightComponent(panelNoWrap);

		JScrollPane listScrollPane = new JScrollPane();
		listLogs = new JList<Record>();
		listLogs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() < 2) {
					return;
				}
				if (arg0.getButton() != MouseEvent.BUTTON1) {
					return;
				}

				Record r = listLogs.getSelectedValue();
				try {
					FileView fv = new FileView(r.getFile(), r.getLineNumber());
					fv.setVisible(true);
					// fv.setVisible(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		listLogs.setFont(new Font("Courier New", Font.PLAIN, 12));
		listScrollPane.setViewportView(listLogs);
		panelNoWrap.add(listScrollPane);

		JPanel panel = new JPanel();
		frmQuerylog.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblPageSize = new JLabel("Max Size (lines):");
		panel.add(lblPageSize);

		txtPageSize = new JTextField();
		txtPageSize.setText("2000");
		panel.add(txtPageSize);
		txtPageSize.setColumns(10);

		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(250, 14));
		panel.add(progressBar);
	}

}
