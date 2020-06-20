package de.amr.easy.game.ui;

import static de.amr.easy.game.Application.loginfo;
import static de.amr.easy.game.Application.ApplicationState.PAUSED;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.easy.game.Application;
import net.miginfocom.swing.MigLayout;

/**
 * Dialog for changing application settings.
 * 
 * @author Armin Reichert
 */
public class AppSettingsDialog extends JDialog {

	static final int MAX_FPS = 180;
	static final int CUSTOM_TABS_START = 3;

	private final Action actionToggleClockDebugging = new AbstractAction("Clock Debugging") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.clock().logging = !app.clock().logging;
		}
	};

	private final Action actionTogglePlayPause = new AbstractAction("Play/Pause") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.togglePause();
			updatePlayPauseButtonText(app.isPaused());

		}
	};

	private final Action actionToggleMuted = new AbstractAction("Muted") {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (cbMuted.isSelected()) {
				app.soundManager().muteAll();
			} else {
				app.soundManager().unmuteAll();
			}
		}
	};

	private final Action actionToggleSmoothRendering = new AbstractAction("Smooth Rendering") {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (cbSmoothRendering.isSelected()) {
				app.settings().smoothRendering = true;
			} else {
				app.settings().smoothRendering = false;
			}
			loginfo("Smooth Rendering is %s", app.settings().smoothRendering);
		}
	};

	private final ChangeListener fpsSliderChanged = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			int fps = Math.max(sliderFPS.getValue(), 1);
			app.clock().setTargetFrameRate(fps);
			updateUIState();
		}
	};

	private Application app;

	private JSlider sliderFPS;
	private DisplayModeSelector comboDisplayMode;
	private JPanel fpsHistoryPanel;
	private FramerateHistoryView fpsHistoryView;
	private JCheckBox cbClockDebugging;
	private JTabbedPane tabbedPane;
	private JPanel panelClock;
	private JPanel panelScreen;
	private JPanel panelSound;
	private JCheckBox cbMuted;
	private JPanel panelButtons;
	private JButton btnPlayPause;
	private JCheckBox cbSmoothRendering;

	public AppSettingsDialog(JFrame parent) {
		super(parent);
		setSize(680, 460);
		getContentPane().setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane);

		panelClock = new JPanel();
		tabbedPane.addTab("Clock", null, panelClock, null);
		panelClock.setLayout(new MigLayout("", "[][grow,fill]", "[][][grow,fill]"));
		JLabel lblFPS = new JLabel("Ticks/sec");
		panelClock.add(lblFPS, "cell 0 0");
		sliderFPS = new JSlider(0, MAX_FPS);
		panelClock.add(sliderFPS, "cell 1 0");
		sliderFPS.addChangeListener(fpsSliderChanged);
		sliderFPS.setMajorTickSpacing(50);
		sliderFPS.setMinorTickSpacing(10);
		sliderFPS.setPaintTicks(true);
		sliderFPS.setLabelTable(sliderFPS.createStandardLabels(10));
		sliderFPS.setPaintLabels(true);

		cbClockDebugging = new JCheckBox("Clock Debugging");
		panelClock.add(cbClockDebugging, "cell 1 1");
		cbClockDebugging.setAction(actionToggleClockDebugging);

		fpsHistoryPanel = new JPanel();
		panelClock.add(fpsHistoryPanel, "cell 0 2 2 1,growx");
		fpsHistoryPanel
				.setBorder(new TitledBorder(null, "Framerate History", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		fpsHistoryPanel.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));

		fpsHistoryView = new FramerateHistoryView(500, 150, MAX_FPS);
		fpsHistoryView.setBackground(Color.BLACK);
		fpsHistoryPanel.add(fpsHistoryView, "cell 0 0,grow");

		panelScreen = new JPanel();
		tabbedPane.addTab("Screen", null, panelScreen, null);
		panelScreen.setLayout(new MigLayout("", "[][]", "[][]"));
		JLabel lblDisplayMode = new JLabel("Fullscreen Resolution");
		panelScreen.add(lblDisplayMode, "cell 0 0");
		comboDisplayMode = new DisplayModeSelector();
		panelScreen.add(comboDisplayMode, "cell 1 0");
		comboDisplayMode.setMinimumSize(new Dimension(220, 26));
		comboDisplayMode.addActionListener(e -> {
			app.settings().fullScreenMode = (DisplayMode) comboDisplayMode.getSelectedItem();
		});
		comboDisplayMode.setMaximumRowCount(comboDisplayMode.getItemCount());

		cbSmoothRendering = new JCheckBox("");
		cbSmoothRendering.setAction(actionToggleSmoothRendering);
		panelScreen.add(cbSmoothRendering, "cell 1 1");

		panelSound = new JPanel();
		tabbedPane.addTab("Sound", null, panelSound, null);
		panelSound.setLayout(new MigLayout("", "[]", "[]"));

		cbMuted = new JCheckBox("Muted");
		cbMuted.setAction(actionToggleMuted);
		panelSound.add(cbMuted, "cell 0 0");

		panelButtons = new JPanel();
		getContentPane().add(panelButtons, BorderLayout.SOUTH);

		btnPlayPause = new JButton("Pause");
		btnPlayPause.setAction(actionTogglePlayPause);
		btnPlayPause.setFont(new Font("SansSerif", Font.BOLD, 14));
		panelButtons.add(btnPlayPause);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		updateUIState();
	}

	public void setApp(Application app) {
		this.app = app;
		fpsHistoryView.setApp(app);
		app.onEntry(PAUSED, state -> updatePlayPauseButtonText(true));
		app.onExit(PAUSED, state -> updatePlayPauseButtonText(false));
		app.clock().addFrequencyChangeListener(change -> updateUIState());
		app.soundManager().changes.addPropertyChangeListener("muted", change -> updateUIState());
		updateUIState();
	}

	private void updateUIState() {
		if (app == null) {
			return;
		}
		setTitle(String.format("Application '%s'", app.settings().title));
		sliderFPS.setValue(app.clock().getTargetFramerate());
		sliderFPS.setToolTipText("Frame rate = " + sliderFPS.getValue());
		comboDisplayMode.select(app.settings().fullScreenMode);
		cbClockDebugging.setSelected(app.clock().logging);
		cbMuted.setSelected(app.soundManager().isMuted());
		updatePlayPauseButtonText(app.isPaused());
	}

	private void updatePlayPauseButtonText(boolean paused) {
		btnPlayPause.setText(paused ? "Resume Game" : "Pause Game");
	}

	public void addCustomTab(String title, JComponent component) {
		tabbedPane.addTab(title, component);
		revalidate();
	}

	public void selectCustomTab(int i) {
		tabbedPane.setSelectedIndex(CUSTOM_TABS_START + i);
		repaint();
	}
}