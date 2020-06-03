package de.amr.easy.game.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.easy.game.Application;
import de.amr.easy.game.Application.ApplicationState;
import net.miginfocom.swing.MigLayout;

/**
 * Dialog for changing clock frequency and full-screen display mode.
 * 
 * @author Armin Reichert
 */
public class AppSettingsDialog extends JDialog {

	static final int MAX_FPS = 180;

	private Application app;
	private JSlider sliderFPS;
	private DisplayModeSelector comboDisplayMode;
	private JPanel fpsHistoryPanel;
	private FramerateHistoryView fpsHistoryView;
	private JButton togglePause;
	private JCheckBox cbClockDebugging;
	private final Action actionToggleClockDebugging = new AbstractAction() {

		{
			putValue(Action.NAME, "Clock Debugging");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			app.clock().logging = !app.clock().logging;
		}
	};

	public AppSettingsDialog(JFrame parent) {
		super(parent);
		setSize(665, 452);
		sliderFPS = new JSlider(0, MAX_FPS);
		sliderFPS.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (sliderFPS.getValue() > 0) {
					app.clock().setTargetFrameRate(sliderFPS.getValue());
					setFpsTooltip();
				} else {
					sliderFPS.setValue(1);
				}
			}
		});
		sliderFPS.setMajorTickSpacing(50);
		sliderFPS.setMinorTickSpacing(10);
		sliderFPS.setPaintTicks(true);
		sliderFPS.setLabelTable(sliderFPS.createStandardLabels(10));
		getContentPane().setLayout(new MigLayout("", "[][3px:n:3px][grow,fill]", "[][][][grow,fill][center]"));
		JLabel lblDisplayMode = new JLabel("Fullscreen Resolution");
		getContentPane().add(lblDisplayMode, "cell 0 0,alignx trailing");
		comboDisplayMode = new DisplayModeSelector();
		comboDisplayMode.setMinimumSize(new Dimension(220, 26));
		comboDisplayMode.addActionListener(e -> {
			app.settings().fullScreenMode = (DisplayMode) comboDisplayMode.getSelectedItem();
		});
		comboDisplayMode.setMaximumRowCount(comboDisplayMode.getItemCount());
		getContentPane().add(comboDisplayMode, "cell 2 0");
		JLabel lblFPS = new JLabel("Ticks/sec");
		getContentPane().add(lblFPS, "cell 0 1,alignx right");
		sliderFPS.setPaintLabels(true);
		setFpsTooltip();
		getContentPane().add(sliderFPS, "cell 2 1,growx");

		cbClockDebugging = new JCheckBox("Clock Debugging");
		cbClockDebugging.setAction(actionToggleClockDebugging);
		getContentPane().add(cbClockDebugging, "cell 2 2");

		fpsHistoryPanel = new JPanel();
		fpsHistoryPanel
				.setBorder(new TitledBorder(null, "Framerate History", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(fpsHistoryPanel, "cell 0 3 3 1,grow");
		fpsHistoryPanel.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));

		fpsHistoryView = new FramerateHistoryView(500, 150, MAX_FPS);
		fpsHistoryView.setBackground(Color.BLACK);
		fpsHistoryPanel.add(fpsHistoryView, "cell 0 0,grow");

		togglePause = new JButton("Pause");
		togglePause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.togglePause();
				updatePausedButtonText(app.isPaused());
			}
		});
		getContentPane().add(togglePause, "flowx,cell 0 4 3 1,alignx center");
	}

	public void setApp(Application app) {
		this.app = app;
		app.onStateEntry(ApplicationState.PAUSED, () -> updatePausedButtonText(true));
		app.onStateExit(ApplicationState.PAUSED, () -> updatePausedButtonText(false));
		app.clock().addFrequencyChangeListener(e -> sliderFPS.setValue((Integer) e.getNewValue()));
		fpsHistoryView.setApp(app);
		updateState(app);
	}

	private void updateState(Application app) {
		setTitle(String.format("Application '%s'", app.settings().title));
		sliderFPS.setValue(app.clock().getTargetFramerate());
		comboDisplayMode.select(app.settings().fullScreenMode);
		cbClockDebugging.setSelected(app.clock().logging);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			updateState(app);
		}
	}

	private void updatePausedButtonText(boolean paused) {
		togglePause.setText(paused ? "Play" : "Pause");
	}

	private void setFpsTooltip() {
		sliderFPS.setToolTipText("Frame rate = " + sliderFPS.getValue());
	}
}