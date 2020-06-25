package de.amr.easy.game.ui.f2dialog.clock;

import static de.amr.easy.game.Application.app;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import de.amr.easy.game.controller.Lifecycle;
import net.miginfocom.swing.MigLayout;

public class ClockView extends JPanel implements Lifecycle {

	private Action actionToggleClockDebugging = new AbstractAction("Clock Debugging") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app().clock().logging = !app().clock().logging;
		}
	};
	private JPanel fpsHistoryPanel;
	private FramerateHistoryView fpsHistoryView;
	private JCheckBox cbClockDebugging;

	public ClockView() {
		setLayout(new MigLayout("", "[150px:150px,left][grow,fill]", "[grow,fill][]"));

		fpsHistoryPanel = new JPanel();
		add(fpsHistoryPanel, "cell 0 0 2 1,grow");
		fpsHistoryPanel
				.setBorder(new TitledBorder(null, "Framerate History", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		fpsHistoryPanel.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));

		fpsHistoryView = new FramerateHistoryView(500, 150, 180);
		fpsHistoryView.setBackground(Color.BLACK);
		fpsHistoryPanel.add(fpsHistoryView, "cell 0 0,grow");

		cbClockDebugging = new JCheckBox("Clock Debugging");
		cbClockDebugging.setHorizontalAlignment(SwingConstants.RIGHT);
		add(cbClockDebugging, "cell 1 1,alignx right");
		cbClockDebugging.setAction(actionToggleClockDebugging);

	}

	@Override
	public void init() {
		fpsHistoryView.setApp(app());
	}

	@Override
	public void update() {
		cbClockDebugging.setSelected(app().clock().logging);
	}
}