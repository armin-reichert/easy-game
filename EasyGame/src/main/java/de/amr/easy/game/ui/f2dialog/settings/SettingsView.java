package de.amr.easy.game.ui.f2dialog.settings;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.amr.easy.game.controller.Lifecycle;
import net.miginfocom.swing.MigLayout;

public class SettingsView extends JPanel implements Lifecycle {

	private JTable table;

	public SettingsView() {
		setLayout(new MigLayout("", "[grow]", "[grow]"));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 0,grow");

		table = new JTable();
		table.setRowHeight(20);
		table.setRowSelectionAllowed(false);
		scrollPane.setViewportView(table);
	}

	@Override
	public void init() {
		table.setModel(new SettingsTableModel());
	}

	@Override
	public void update() {
		SettingsTableModel model = (SettingsTableModel) table.getModel();
		model.readAppSettings();
	}
}