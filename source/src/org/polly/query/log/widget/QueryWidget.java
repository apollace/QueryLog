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
package org.polly.query.log.widget;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;

/**
 * 
 * @author Alessandro Pollace
 */
public class QueryWidget extends JPanel {
	private static final long serialVersionUID = 860109714297282262L;
	private JTextPaneNoWrap txtpnQuery = new JTextPaneNoWrap();

	private DefaultListModel<String> macrosNamesModel = new DefaultListModel<String>();
	private Map<String, String> macros = new HashMap<String, String>();

	private JPanel alwaysVisiblePane = new JPanel();
	private String lastSelectedMacro = null;
	private JList<String> macrosName = new JList<String>(macrosNamesModel);
	private JTextField txtpnMacrosValueTextPane = new JTextField();

	/**
	 * Create the panel.
	 */
	public QueryWidget() {
		buildAlwaysVisiblePanel();
		setLayout(new BorderLayout(0, 0));
		hideMacro();
	}

	private void hideMacro() {
		removeAll();
		add(alwaysVisiblePane);
	}

	private void showMacro() {
		removeAll();

		JSplitPane queryPanel = new JSplitPane();
		queryPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(queryPanel);

		queryPanel.setLeftComponent(alwaysVisiblePane);

		JSplitPane macrosPanel = new JSplitPane();
		queryPanel.setRightComponent(macrosPanel);

		JScrollPane macrosNameScrollPAnel = new JScrollPane();
		macrosNameScrollPAnel.setBorder(new TitledBorder(null, "Macros", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		macrosNameScrollPAnel.setPreferredSize(new Dimension(200, 2));
		macrosPanel.setLeftComponent(macrosNameScrollPAnel);
		macrosName.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectMacro();
			}
		});
		macrosName.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		macrosNameScrollPAnel.setViewportView(macrosName);

		JPanel macrosValuePanel = new JPanel();
		macrosValuePanel.setBorder(new TitledBorder(null, "Macro value", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		macrosPanel.setRightComponent(macrosValuePanel);
		macrosValuePanel.setLayout(new BorderLayout(0, 0));

		JScrollPane macrosValueScrollPAnel = new JScrollPane();
		macrosValuePanel.add(macrosValueScrollPAnel, BorderLayout.NORTH);

		macrosValueScrollPAnel.setViewportView(txtpnMacrosValueTextPane);

		JPanel macrosActionPane = new JPanel();
		macrosValuePanel.add(macrosActionPane, BorderLayout.SOUTH);
		macrosActionPane.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		JButton btnAddMacro = new JButton("Add macro");
		btnAddMacro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addMacro();
			}
		});
		macrosActionPane.add(btnAddMacro);

		JButton btnRemoveMacro = new JButton("Remove Macro");
		btnRemoveMacro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeMacro();
			}
		});
		macrosActionPane.add(btnRemoveMacro);
	}

	private void buildAlwaysVisiblePanel() {
		alwaysVisiblePane
				.setBorder(new TitledBorder(null, "Query", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		alwaysVisiblePane.setLayout(new BorderLayout(0, 0));
		JScrollPane queryEditorScrollPAne = new JScrollPane();
		alwaysVisiblePane.add(queryEditorScrollPAne);

		this.txtpnQuery.setFont(new Font("Monospaced", Font.PLAIN, 11));
		queryEditorScrollPAne.setViewportView(this.txtpnQuery);

		final JCheckBox chckbxShowMacros = new JCheckBox("Show macros");
		chckbxShowMacros.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxShowMacros.isSelected()) {
					showMacro();
					invalidate();
					validate();
				} else {
					hideMacro();
					invalidate();
					validate();
				}

			}
		});
		alwaysVisiblePane.add(chckbxShowMacros, BorderLayout.EAST);
	}

	public String getQueryToExecute() {
		// Using this method all unsaved macro will be saved
		selectMacro();

		String baseQuery = txtpnQuery.getText();
		for (Entry<String, String> entry : macros.entrySet()) {
			baseQuery = baseQuery.replace("${" + entry.getKey() + "}", entry.getValue());
		}

		return baseQuery;
	}

	public String getQueryToSave() {
		// Using this method all unsaved macro will be saved
		selectMacro();
		return txtpnQuery.getText();
	}

	public String getMacroToSave() {
		String macro = "";
		for (int i = 0; i < macrosNamesModel.getSize(); i++) {
			String macroName = macrosNamesModel.getElementAt(i);
			macro = macro + macroName + ":" + macros.get(macroName) + "___-___";
		}

		return macro;
	}

	public void setQuerySaved(String savedQuery) {
		txtpnQuery.setText(savedQuery);
	}

	public void setMacroSaved(String savedMacro) {
		if (savedMacro == null || savedMacro.trim().length() == 0){
			return;
		}
		String macros[] = savedMacro.split("___-___");
		for (String macro : macros) {
			int pos = macro.indexOf(":");
			String key = macro.substring(0, pos);
			String value = macro.substring(pos + 1);

			this.macros.put(key, value);
			macrosNamesModel.addElement(key);
		}
	}

	private void addMacro() {
		String s = (String) JOptionPane.showInputDialog(this, "Macro name");
		if (s == null)
			// just return the user has press cancel
			return;
		else if (s.trim().length() == 0 || !s.matches("[A-Za-z0-9]+")) {
			JOptionPane.showMessageDialog(this, "Invalid macro name");
			return;
		}

		if (macros.containsKey(s)) {
			JOptionPane.showMessageDialog(this, "Macro already exist");
			return;
		}

		macros.put(s, "");
		macrosNamesModel.addElement(s);
	}

	private void removeMacro() {
		String key = macrosName.getSelectedValue();

		macros.remove(key);
		macrosNamesModel.remove(macrosName.getSelectedIndex());
	}

	private void selectMacro() {
		if (lastSelectedMacro != null) {
			macros.put(lastSelectedMacro, txtpnMacrosValueTextPane.getText());
		}

		String key = macrosName.getSelectedValue();
		txtpnMacrosValueTextPane.setText(macros.get(key));
		lastSelectedMacro = key;
	}
}
