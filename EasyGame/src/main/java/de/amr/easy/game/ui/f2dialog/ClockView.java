package de.amr.easy.game.ui.f2dialog;

import static de.amr.easy.game.Application.app;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.easy.game.controller.Lifecycle;
import net.miginfocom.swing.MigLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ClockView extends JPanel implements Lifecycle {

	private static final int MAX_FPS = 180;

	private ChangeListener fpsSliderChanged = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			int fps = Math.max(sliderFPS.getValue(), 1);
			app().clock().setTargetFrameRate(fps);
			update();
		}
	};

	private Action actionToggleClockDebugging = new AbstractAction("Clock Debugging") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app().clock().logging = !app().clock().logging;
		}
	};

	private JSlider sliderFPS;
	private JPanel fpsHistoryPanel;
	private FramerateHistoryView fpsHistoryView;
	private JCheckBox cbClockDebugging;
	private JLabel lblFPS;

	public ClockView() {
		setLayout(new MigLayout("", "[][grow,fill]", "[][grow,fill][]"));
		lblFPS = new JLabel("60 Ticks/sec");
		lblFPS.setBorder(new EmptyBorder(0, 3, 0, 6));
		lblFPS.setForeground(Color.BLUE);
		lblFPS.setFont(new Font("SansSerif", Font.BOLD, 18));
		add(lblFPS, "cell 0 0,aligny center");
		sliderFPS = new JSlider(0, MAX_FPS);
		lblFPS.setLabelFor(sliderFPS);
		add(sliderFPS, "cell 1 0");
		sliderFPS.addChangeListener(fpsSliderChanged);
		sliderFPS.setMajorTickSpacing(50);
		sliderFPS.setMinorTickSpacing(10);
		sliderFPS.setPaintTicks(true);
		sliderFPS.setLabelTable(sliderFPS.createStandardLabels(10));
		sliderFPS.setPaintLabels(true);

		fpsHistoryPanel = new JPanel();
		add(fpsHistoryPanel, "cell 0 1 2 1,growx");
		fpsHistoryPanel
				.setBorder(new TitledBorder(null, "Framerate History", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		fpsHistoryPanel.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));

		fpsHistoryView = new FramerateHistoryView(500, 150, MAX_FPS);
		fpsHistoryView.setBackground(Color.BLACK);
		fpsHistoryPanel.add(fpsHistoryView, "cell 0 0,grow");

		cbClockDebugging = new JCheckBox("Clock Debugging");
		cbClockDebugging.setHorizontalAlignment(SwingConstants.RIGHT);
		add(cbClockDebugging, "cell 1 2,alignx right");
		cbClockDebugging.setAction(actionToggleClockDebugging);

	}

	@Override
	public void init() {
		fpsHistoryView.init();
	}

	@Override
	public void update() {
		lblFPS.setText(String.format("%d ticks/sec", app().clock().getTargetFramerate()));
		sliderFPS.setValue(app().clock().getTargetFramerate());
		sliderFPS.setToolTipText("Frame rate = " + sliderFPS.getValue());
		cbClockDebugging.setSelected(app().clock().logging);
	}
}