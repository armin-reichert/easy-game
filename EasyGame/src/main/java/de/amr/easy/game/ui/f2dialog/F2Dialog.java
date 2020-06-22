package de.amr.easy.game.ui.f2dialog;

import static de.amr.easy.game.Application.app;
import static de.amr.easy.game.Application.ApplicationState.PAUSED;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import de.amr.easy.game.Application;
import de.amr.easy.game.controller.Lifecycle;

/**
 * Dialog for inspecting and changing application environment. Opened with F2 key.
 * 
 * <p>
 * Applications can add custom tabs, see
 * {@link Application#addCustomSettingsTab(String, JComponent)}.
 * 
 * @author Armin Reichert
 */
public class F2Dialog extends JDialog implements Lifecycle {

	static final int CUSTOM_TABS_START = 3;

	Action actionTogglePlayPause = new AbstractAction("Play/Pause") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app().togglePause();
		}
	};

	private JTabbedPane tabbedPane;
	private JPanel panelButtons;
	private JButton btnPlayPause;
	private SoundView soundView;
	private ScreenView screenView;
	private ClockView clockView;

	public F2Dialog(Window owner) {
		super(owner);
		setSize(680, 400);
		getContentPane().setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane);

		clockView = new ClockView();
		tabbedPane.addTab("Clock", null, clockView, null);

		screenView = new ScreenView();
		tabbedPane.addTab("Screen", null, screenView, null);

		soundView = new SoundView();
		tabbedPane.addTab("Sound", null, soundView, null);

		panelButtons = new JPanel();
		getContentPane().add(panelButtons, BorderLayout.SOUTH);

		btnPlayPause = new JButton("Pause");
		btnPlayPause.setAction(actionTogglePlayPause);
		btnPlayPause.setFont(new Font("SansSerif", Font.BOLD, 14));
		panelButtons.add(btnPlayPause);
	}

	@Override
	public void setVisible(boolean visible) {
		update();
		super.setVisible(visible);
	}

	@Override
	public void init() {
		clockView.init();
		soundView.init();
		screenView.init();
		app().onEntry(PAUSED, state -> invokeLater(() -> btnPlayPause.setText("Resume")));
		app().onExit(PAUSED, state -> invokeLater(() -> btnPlayPause.setText("Pause")));
		app().clock().addFrequencyChangeListener(change -> invokeLater(this::update));
		app().soundManager().changes.addPropertyChangeListener("muted", e -> update());
		update();
	}

	@Override
	public void update() {
		clockView.update();
		screenView.update();
		soundView.update();
		setTitle(String.format("Application '%s'", app().settings().title));
		btnPlayPause.setText(app().isPaused() ? "Resume" : "Pause");
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