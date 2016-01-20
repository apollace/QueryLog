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
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JToolBar;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class FileView extends JFrame {
	private static final long serialVersionUID = -2886785337637359762L;
	private JPanel contentPane;
	private JTextPane txtpnFileView;

	File file;
	int targetLineNumber;

	// The Start and end offset for target line
	int offsetStart = 0;
	int offsetEnd = 0;

	ActionListener highlightSelection = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			highlightClear();
			highlightSection();
			highlightTargetLine();
		}
	};

	ActionListener highlightRemove = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			highlightClear();
			highlightTargetLine();
		}
	};
	private JTextField txtWindowsize;

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public FileView(File file, int targetLineNumber) throws IOException {
		setBounds(100, 100, 1024, 768);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setTitle(file.getName());

		this.file = file;
		this.targetLineNumber = targetLineNumber;

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		txtpnFileView = new JTextPane();
		txtpnFileView.setEditable(false);

		txtpnFileView.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() < 2) {
					return;
				}
				if (arg0.getButton() != MouseEvent.BUTTON1) {
					return;
				}

				highlightClear();
				highlightSection();
				highlightTargetLine();
			}
		});
		txtpnFileView.setFont(new Font("Courier New", Font.PLAIN, 12));
		scrollPane.setViewportView(txtpnFileView);

		JToolBar toolBar = new JToolBar();
		scrollPane.setColumnHeaderView(toolBar);

		JLabel lblWindowSize = new JLabel("Window size");
		toolBar.add(lblWindowSize);

		txtWindowsize = new JTextField();
		txtWindowsize
				.setToolTipText("The number of lines (before and after selected row) to show");
		txtWindowsize.setText("100");
		toolBar.add(txtWindowsize);
		txtWindowsize.setColumns(10);

		loadFile();

		JButton btnReload = new JButton("Reload");
		btnReload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		toolBar.add(btnReload);

		JMenuBar menuBar = new JMenuBar();
		contentPane.add(menuBar, BorderLayout.NORTH);

		JMenu mnHighlight = new JMenu("Highlight");
		menuBar.add(mnHighlight);

		JMenuItem mntmHighlightSelection = new JMenuItem("Highlight selection");
		mntmHighlightSelection.addActionListener(highlightSelection);
		mnHighlight.add(mntmHighlightSelection);

		JMenuItem mntmHighlightRemove = new JMenuItem("Highlight remove");
		mntmHighlightRemove.addActionListener(highlightRemove);
		mnHighlight.add(mntmHighlightRemove);

		// Add keyboard shortcuts
		mntmHighlightSelection.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_H, InputEvent.CTRL_MASK));
		txtpnFileView.registerKeyboardAction(highlightSelection,
				KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK),
				JComponent.WHEN_FOCUSED);

		mntmHighlightRemove.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_R, InputEvent.CTRL_MASK));
		txtpnFileView.registerKeyboardAction(highlightRemove,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK),
				JComponent.WHEN_FOCUSED);
	}

	CaretListener cl = new CaretListener() {

		@Override
		public void caretUpdate(CaretEvent e) {
			highlightClear();

			DefaultHighlightPainter highlightPainter = new DefaultHighlightPainter(
					Color.GREEN);

			int start = e.getDot() < e.getMark() ? e.getDot() : e.getMark();
			int end = e.getDot() > e.getMark() ? e.getDot() : e.getMark();
			
			try {
				txtpnFileView.getHighlighter().addHighlight(start, end,
						highlightPainter);
			} catch (BadLocationException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

			highlightTargetLine();

		}

	};

	private void loadFile() throws IOException {
		String line;
		offsetStart = 0;
		int windowSize = Integer.valueOf(txtWindowsize.getText());
		int currentLineNumber = 0;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			currentLineNumber++;
			if ((currentLineNumber > targetLineNumber - windowSize)
					&& (currentLineNumber < targetLineNumber + windowSize)) {
				sb.append(line).append("\n");

				if (currentLineNumber < targetLineNumber) {
					// + 1 is for \n char
					offsetStart += line.length() + 1;
				} else if (currentLineNumber == targetLineNumber) {
					offsetEnd = offsetStart + line.length();
				}
			}
		}
		br.close();
		txtpnFileView.setText(sb.toString());
		txtpnFileView.addCaretListener(cl);

		highlightTargetLine();
	}

	private void highlightTargetLine() {
		DefaultHighlightPainter highlightPainter = new DefaultHighlightPainter(
				Color.YELLOW);

		try {
			txtpnFileView.getHighlighter().addHighlight(offsetStart, offsetEnd,
					highlightPainter);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void highlightSection() {
		DefaultHighlightPainter highlightPainter = new DefaultHighlightPainter(
				Color.ORANGE);
		try {
			String text = txtpnFileView.getText();
			String selectedText = txtpnFileView.getSelectedText();

			int offset = 0;
			while ((offset = text.indexOf(selectedText, offset)) != -1) {
				int endOffset = offset + selectedText.length();
				txtpnFileView.getHighlighter().addHighlight(offset, endOffset,
						highlightPainter);
				offset = endOffset;
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void highlightClear() {
		DefaultHighlighter highlighter = (DefaultHighlighter) txtpnFileView
				.getHighlighter();
		highlighter.removeAllHighlights();
	}
}
