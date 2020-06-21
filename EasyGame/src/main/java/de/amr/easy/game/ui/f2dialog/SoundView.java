package de.amr.easy.game.ui.f2dialog;

import static de.amr.easy.game.Application.app;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import java.awt.BorderLayout;

public class SoundView extends JPanel {

	Action actionToggleMuted = new AbstractAction("Muted") {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (cbMuted.isSelected()) {
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
		content.setLayout(new MigLayout("", "[456px]", "[18px][406px]"));

		cbMuted = new JCheckBox("Muted");
		content.add(cbMuted, "cell 0 0,aligny top");
		cbMuted.setAction(actionToggleMuted);

		JScrollPane scrollPane = new JScrollPane();
		content.add(scrollPane, "cell 0 1,grow");

		table = new JTable();
		scrollPane.setViewportView(table);
	}

	public void init() {
		SoundTableModel tableModel = new SoundTableModel();
		table.setModel(tableModel);
	}

	public void updateViewState() {
		cbMuted.setSelected(app().soundManager().isMuted());
		SoundTableModel tableModel = (SoundTableModel) table.getModel();
		tableModel.update();
	}
}
