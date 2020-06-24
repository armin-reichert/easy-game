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
import javax.swing.table.TableCellRenderer;

import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.ui.f2dialog.SoundTableModel.Field;
import net.miginfocom.swing.MigLayout;

/**
 * Displays all sounds loaded by the asset manager.
 * 
 * @author Armin Reichert
 */
public class SoundView extends JPanel implements Lifecycle {

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
		setLayout(new BorderLayout());

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
		setCellRenderer(Field.Volume, new PercentRenderer());
		setCellRenderer(Field.Duration, new SecondsRenderer());
	}

	private void setCellRenderer(Field column, TableCellRenderer r) {
		table.getColumnModel().getColumn(column.ordinal()).setCellRenderer(r);
	}

	@Override
	public void init() {
		setupTable(new SoundTableModel());
	}

	@Override
	public void update() {
		cbMuted.setSelected(app().soundManager().isMuted());
		SoundTableModel tableModel = (SoundTableModel) table.getModel();
		tableModel.update();
	}
}