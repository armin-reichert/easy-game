package de.amr.easy.game.ui.f2dialog.core;

import static de.amr.easy.game.Application.app;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import de.amr.easy.game.Application;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.ui.AppShell;
import de.amr.easy.game.ui.f2dialog.F2Dialog;
import de.amr.easy.game.ui.f2dialog.clock.ClockView;
import de.amr.easy.game.ui.f2dialog.clock.FramerateSelector;
import de.amr.easy.game.ui.f2dialog.screen.ScreenView;
import de.amr.easy.game.ui.f2dialog.settings.SettingsView;
import de.amr.easy.game.ui.f2dialog.sound.SoundView;
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
public class F2DialogImpl extends JDialog implements Lifecycle, F2Dialog {

	private static class CustomTab {
		int index;
		BooleanSupplier fnEnabled;
	}

	public static final int CUSTOM_TABS_START = 4;

	private int dx;
	private int dy;
	private List<CustomTab> customTabs = new ArrayList<>();
	private Timer updateTimer;
	private SoundView soundView;
	private ScreenView screenView;
	private ClockView clockView;
	private SettingsView settingsView;
	private JTabbedPane tabbedPane;
	private JButton btnPlayPause;
	private FramerateSelector framerateSelector;
	private Icon pauseIcon, playIcon;

	public F2DialogImpl(Window owner) {
		super(owner);
		setSize(700, 500);
		getContentPane().setLayout(new MigLayout("", "[grow,fill]", "[grow,fill][]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, "cell 0 0,grow");

		clockView = new ClockView();
		tabbedPane.addTab("Clock", null, clockView, null);

		screenView = new ScreenView();
		tabbedPane.addTab("Screen", null, screenView, null);

		soundView = new SoundView();
		tabbedPane.addTab("Sound", null, soundView, null);
		tabbedPane.addChangeListener(e -> tabChanged());

		settingsView = new SettingsView();
		tabbedPane.addTab("Settings", null, settingsView, null);

		btnPlayPause = new JButton("Play/Pause");
		getContentPane().add(btnPlayPause, "flowx,cell 0 1");
		btnPlayPause.setFont(new Font("SansSerif", Font.BOLD, 14));
		btnPlayPause.addActionListener(e -> app().togglePause());

		framerateSelector = new FramerateSelector();
		getContentPane().add(framerateSelector, "cell 0 1,growx");
	}

	@Override
	public void setRelativeLocation(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			AppShell shell = Application.app().shell().get();
			setLocation(shell.getX() + dx, shell.getY() + dy);
			updateTimer.restart();
		} else {
			updateTimer.stop();
		}
	}

	@Override
	public void init() {
		setTitle(String.format("Application '%s'", app().settings().title));
		clockView.init();
		soundView.init();
		screenView.init();
		settingsView.init();
		framerateSelector.init();
		updateTimer = new Timer(200, e -> SwingUtilities.invokeLater(this::update));
	}

	@Override
	public void update() {
		Component comp = tabbedPane.getSelectedComponent();
		if (comp instanceof Lifecycle) {
			((Lifecycle) comp).update();
		}
		for (CustomTab customTab : customTabs) {
			tabbedPane.setEnabledAt(customTab.index, customTab.fnEnabled.getAsBoolean());
		}
		updatePlayPauseButton();
		framerateSelector.update(); // always visible
	}

	@Override
	public void addCustomTab(String title, JComponent component, BooleanSupplier fnEnabled) {
		CustomTab customTab = new CustomTab();
		customTab.index = CUSTOM_TABS_START + customTabs.size();
		customTab.fnEnabled = fnEnabled;
		customTabs.add(customTab);
		tabbedPane.addTab(title, component);
	}

	@Override
	public void selectCustomTab(int i) {
		CustomTab customTab = customTabs.get(i);
		if (customTab.fnEnabled.getAsBoolean()) {
			tabbedPane.setSelectedIndex(CUSTOM_TABS_START + i);
		}
	}

	@Override
	public void selectTab(int i) {
		tabbedPane.setSelectedIndex(i);
	}

	private void tabChanged() {
		update();
	}

	private void loadIcons() throws IOException {
		BufferedImage sheet = ImageIO.read(getClass().getResource("/icons/pause-play-and-stop-blank-icons.png"));
		Image pauseImage = sheet.getSubimage(0, 0, 155, 155).getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH);
		Image playImage = sheet.getSubimage(198, 0, 155, 155).getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH);
		pauseIcon = new ImageIcon(pauseImage);
		playIcon = new ImageIcon(playImage);
	}

	private void updatePlayPauseButton() {
		if (pauseIcon == null) { // first time
			try {
				loadIcons();
			} catch (Exception x) {
				throw new RuntimeException(x);
			}
			btnPlayPause.setText("");
		}
		if (app().isPaused()) {
			btnPlayPause.setIcon(playIcon);
			btnPlayPause.setToolTipText("Press to PLAY");
		} else {
			btnPlayPause.setIcon(pauseIcon);
			btnPlayPause.setToolTipText("Press to PAUSE");
		}
	}

}