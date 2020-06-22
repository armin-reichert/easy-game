package de.amr.easy.game.ui.f2dialog;

import static de.amr.easy.game.Application.app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.amr.easy.game.ui.f2dialog.SoundTableModel.Column;
import net.miginfocom.swing.MigLayout;

/**
 * Displays all sounds loaded by the asset manager.
 * 
 * @author Armin Reichert
 */
public class SoundView extends JPanel {

	Action actionToggleMuted = new AbstractAction("Muted") {

		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox cb = (JCheckBox) e.getSource();
			if (cb.isSelected()) {
				app().soundManager().muteAll();
			} else {
				app().soundManager().unmuteAll();
			}
		}
	};

	private JCheckBox cbMuted;
	private JTable table;
	private JPanel content;

	public SoundView() {
		setLayout(new BorderLayout(0, 0));

		content = new JPanel();
		add(content, BorderLayout.CENTER);
		content.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		cbMuted = new JCheckBox("Muted");
		content.add(cbMuted, "cell 0 0,aligny top");
		cbMuted.setAction(actionToggleMuted);

		JScrollPane scrollPane = new JScrollPane();
		content.add(scrollPane, "cell 0 1,grow");

		table = new JTable();
		table.setRowHeight(24);
		scrollPane.setViewportView(table);
		setupTable(SoundTableModel.SAMPLE_DATA);
	}

	private void setupTable(SoundTableModel model) {
		table.setModel(model);
		table.getColumnModel().getColumn(Column.Volume.ordinal()).setCellRenderer(new PercentRenderer());
	}

	public void init() {
		setupTable(new SoundTableModel());
	}

	public void updateViewState() {
		cbMuted.setSelected(app().soundManager().isMuted());
		SoundTableModel tableModel = (SoundTableModel) table.getModel();
		tableModel.update();
	}
}