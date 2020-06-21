package de.amr.easy.game.ui.f2dialog;

import static de.amr.easy.game.Application.app;
import static de.amr.easy.game.Application.loginfo;
import static de.amr.easy.game.Application.ApplicationState.PAUSED;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

/**
 * Dialog for inspecting and changing application environment. Opened with F2 key.
 * 
 * @author Armin Reichert
 */
public class F2Dialog extends JDialog {

	static final int MAX_FPS = 180;
	static final int CUSTOM_TABS_START = 3;

	Action actionToggleClockDebugging = new AbstractAction("Clock Debugging") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app().clock().logging = !app().clock().logging;
		}
	};

	Action actionTogglePlayPause = new AbstractAction("Play/Pause") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app().togglePause();
		}
	};

	Action actionToggleSmoothRendering = new AbstractAction("Smooth Rendering") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app().settings().smoothRendering = cbSmoothRendering.isSelected();
			loginfo("Smooth Rendering is %s", app().settings().smoothRendering);
		}
	};

	ChangeListener fpsSliderChanged = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			int fps = Math.max(sliderFPS.getValue(), 1);
			app().clock().setTargetFrameRate(fps);
			updateViewState();
		}
	};

	private JSlider sliderFPS;
	private DisplayModeSelector comboDisplayMode;
	private JPanel fpsHistoryPanel;
	private FramerateHistoryView fpsHistoryView;
	private JCheckBox cbClockDebugging;
	private JTabbedPane tabbedPane;
	private JPanel panelClock;
	private JPanel panelScreen;
	private JPanel panelButtons;
	private JButton btnPlayPause;
	private JCheckBox cbSmoothRendering;
	private SoundView soundView;

	public F2Dialog(Window owner) {
		super(owner);
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
			app().settings().fullScreenMode = (DisplayMode) comboDisplayMode.getSelectedItem();
		});
		comboDisplayMode.setMaximumRowCount(comboDisplayMode.getItemCount());

		cbSmoothRendering = new JCheckBox("");
		cbSmoothRendering.setAction(actionToggleSmoothRendering);
		panelScreen.add(cbSmoothRendering, "cell 1 1");

		soundView = new SoundView();
		tabbedPane.addTab("Sound", null, soundView, null);
		soundView.setLayout(new MigLayout("", "[664px]", "[][350px]"));

		panelButtons = new JPanel();
		getContentPane().add(panelButtons, BorderLayout.SOUTH);

		btnPlayPause = new JButton("Pause");
		btnPlayPause.setAction(actionTogglePlayPause);
		btnPlayPause.setFont(new Font("SansSerif", Font.BOLD, 14));
		panelButtons.add(btnPlayPause);
	}

	@Override
	public void setVisible(boolean visible) {
		updateViewState();
		super.setVisible(visible);
	}

	public void init() {
		soundView.init();
		fpsHistoryView.init();
		app().onEntry(PAUSED, state -> invokeLater(() -> btnPlayPause.setText("Resume")));
		app().onExit(PAUSED, state -> invokeLater(() -> btnPlayPause.setText("Pause")));
		app().clock().addFrequencyChangeListener(change -> invokeLater(this::updateViewState));
		app().soundManager().changes.addPropertyChangeListener("muted", e -> updateViewState());
		updateViewState();
	}

	private void updateViewState() {
		setTitle(String.format("Application '%s'", app().settings().title));
		sliderFPS.setValue(app().clock().getTargetFramerate());
		sliderFPS.setToolTipText("Frame rate = " + sliderFPS.getValue());
		comboDisplayMode.select(app().settings().fullScreenMode);
		cbClockDebugging.setSelected(app().clock().logging);
		btnPlayPause.setText(app().isPaused() ? "Resume" : "Pause");
		soundView.updateViewState();
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