package de.amr.easy.game.ui.f2dialog.screen;

import static de.amr.easy.game.Application.app;
import static de.amr.easy.game.Application.loginfo;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.amr.easy.game.controller.Lifecycle;
import net.miginfocom.swing.MigLayout;

public class ScreenView extends JPanel implements Lifecycle {

	Action actionToggleSmoothRendering = new AbstractAction("Smooth Rendering") {

		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox cb = (JCheckBox) e.getSource();
			app().settings().smoothRendering = cb.isSelected();
			loginfo("Smooth Rendering is %s", app().settings().smoothRendering);
		}
	};

	private DisplayModeSelector comboDisplayMode;
	private JCheckBox cbSmoothRendering;

	public ScreenView() {
		setLayout(new MigLayout("", "[][]", "[][]"));
		JLabel lblDisplayMode = new JLabel("Fullscreen Resolution");
		add(lblDisplayMode, "cell 0 0");
		comboDisplayMode = new DisplayModeSelector();
		add(comboDisplayMode, "cell 1 0");
		comboDisplayMode.setMinimumSize(new Dimension(220, 26));
		comboDisplayMode.addActionListener(e -> {
			app().settings().fullScreenMode = (DisplayMode) comboDisplayMode.getSelectedItem();
		});
		comboDisplayMode.setMaximumRowCount(comboDisplayMode.getItemCount());

		cbSmoothRendering = new JCheckBox("");
		cbSmoothRendering.setAction(actionToggleSmoothRendering);
		add(cbSmoothRendering, "cell 1 1");
	}

	@Override
	public void init() {
		comboDisplayMode.select(app().settings().fullScreenMode);
		cbSmoothRendering.setSelected(app().settings().smoothRendering);
	}

	@Override
	public void update() {
		comboDisplayMode.select(app().settings().fullScreenMode);
		cbSmoothRendering.setSelected(app().settings().smoothRendering);
	}
}