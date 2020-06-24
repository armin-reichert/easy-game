package de.amr.easy.game.ui.f2dialog;

import static de.amr.easy.game.Application.app;
import static de.amr.easy.game.Application.ApplicationState.PAUSED;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import de.amr.easy.game.Application;
import de.amr.easy.game.controller.Lifecycle;
import net.miginfocom.swing.MigLayout;

/**
 * Dialog for inspecting and changing application environment. Opened with F2 key.
 * 
 * <p>
 * Applications can add custom tabs, see
 * {@link Application#addCustomSettingsTab(String, JComponent)}.
 * 
 * @author Armin Reichert
 */
public class F2Dialog extends JDialog implements Lifecycle, F2DialogAPI {

	public static final int CUSTOM_TABS_START = 3;

	Action actionTogglePlayPause = new AbstractAction("Play/Pause") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app().togglePause();
		}
	};

	JTabbedPane tabbedPane;
	private JButton btnPlayPause;
	private SoundView soundView;
	private ScreenView screenView;
	private ClockView clockView;
	private FramerateSelector framerateSelector;
	private Icon pauseIcon, playIcon;

	private void loadIcons() {
		Image icons = new ImageIcon(getClass().getResource("/icons/pause-play-and-stop-blank-icons.png")).getImage();
		BufferedImage buf = new BufferedImage(550, 155, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = buf.createGraphics();
		g.drawImage(icons, 0, 0, icons.getWidth(null), icons.getHeight(null), null);
		pauseIcon = new ImageIcon(buf.getSubimage(0, 0, 155, 155).getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH));
		playIcon = new ImageIcon(buf.getSubimage(198, 0, 155, 155).getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH));
	}

	public F2Dialog(Window owner) {
		super(owner);
		setSize(754, 480);
		getContentPane().setLayout(new MigLayout("", "[grow,fill]", "[grow,fill][]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, "cell 0 0,grow");

		clockView = new ClockView();
		tabbedPane.addTab("Clock", null, clockView, null);

		screenView = new ScreenView();
		tabbedPane.addTab("Screen", null, screenView, null);

		soundView = new SoundView();
		tabbedPane.addTab("Sound", null, soundView, null);

		btnPlayPause = new JButton("Pause");
		getContentPane().add(btnPlayPause, "flowx,cell 0 1");
		btnPlayPause.setAction(actionTogglePlayPause);
		btnPlayPause.setFont(new Font("SansSerif", Font.BOLD, 14));

		framerateSelector = new FramerateSelector();
		getContentPane().add(framerateSelector, "cell 0 1,growx");
	}

	@Override
	public void setVisible(boolean visible) {
		update();
		super.setVisible(visible);
	}

	@Override
	public void init() {
		loadIcons();
		clockView.init();
		soundView.init();
		screenView.init();
		framerateSelector.init();
		app().onEntry(PAUSED, state -> invokeLater(this::updatePlayPauseButton));
		app().onExit(PAUSED, state -> invokeLater(this::updatePlayPauseButton));
		app().clock().addFrequencyChangeListener(change -> invokeLater(this::update));
		app().soundManager().changes.addPropertyChangeListener("muted", e -> update());
		update();
	}

	@Override
	public void update() {
		clockView.update();
		screenView.update();
		soundView.update();
		framerateSelector.update();
		setTitle(String.format("Application '%s'", app().settings().title));
		updatePlayPauseButton();
	}

	private void updatePlayPauseButton() {
		btnPlayPause.setText("");
		if (app().isPaused()) {
			btnPlayPause.setIcon(playIcon);
			btnPlayPause.setToolTipText("Press to PLAY");
		} else {
			btnPlayPause.setIcon(pauseIcon);
			btnPlayPause.setToolTipText("Press to PAUSE");
		}
	}

	@Override
	public void addTab(String title, JComponent component) {
		tabbedPane.addTab(title, component);
		revalidate();
	}

	@Override
	public void selectCustomTab(int i) {
		tabbedPane.setSelectedIndex(CUSTOM_TABS_START + i);
	}

	@Override
	public void selectTab(int i) {
		tabbedPane.setSelectedIndex(i);
	}
}