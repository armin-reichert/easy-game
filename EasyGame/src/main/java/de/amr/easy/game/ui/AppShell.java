package de.amr.easy.game.ui;

import static de.amr.easy.game.Application.loginfo;
import static java.lang.String.format;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JFrame;

import de.amr.easy.game.Application;
import de.amr.easy.game.view.View;

/**
 * The application shell provides the window where the current view of the application is rendered.
 * <p>
 * In window mode, the view is rendered to a double-buffered canvas which is the single child of the
 * application frame.
 * 
 * <p>
 * In full-screen-exclusive mode, the view is rendered into a full-screen-exclusive frame. In both
 * cases, active rendering with the frequency of the application clock is performed.
 * 
 * <p>
 * The F11-key toggles between full-screen-exclusive and window mode.
 * 
 * @author Armin Reichert
 */
public class AppShell extends JFrame {

	private static final String PAUSED_TEXT = "PAUSED (Press CTRL+P to continue)";

	private final Application app;
	private final int width;
	private final int height;
	private final GraphicsDevice device;
	private final Canvas canvas;
	private final JFrame fullScreenWindow;
	private int frames;
	private AppSettingsDialog settingsDialog;
	private volatile boolean renderingEnabled;

	public AppShell(Application app, int width, int height) {
		this.app = app;
		this.width = width;
		this.height = height;
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		if (app.settings().fullScreenMode == null) {
			DisplayMode[] modes = device.getDisplayModes();
			app.settings().fullScreenMode = modes[modes.length - 1];
		}
		setIconImage(app.getIcon());
		setTitle(app.settings().title);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int scaledWidth = (int) Math.ceil(width * app.settings().scale);
		int scaledHeight = (int) Math.ceil(height * app.settings().scale);
		Dimension size = new Dimension(scaledWidth, scaledHeight);
		canvas = new Canvas();
		canvas.setPreferredSize(size);
		canvas.setSize(size);
		canvas.setBackground(app.settings().bgColor);
		canvas.setIgnoreRepaint(true);
		canvas.setFocusable(false);
		add(canvas, BorderLayout.CENTER);
		fullScreenWindow = createFullScreenWindow();
		pack();
		setLocationRelativeTo(null);
		loginfo("Application shell created.");
	}

	public void showSettingsDialog() {
		if (settingsDialog == null) {
			settingsDialog = new AppSettingsDialog(this);
			settingsDialog.setApp(app);
		}
		if (inFullScreenMode()) {
			loginfo("Settings dialog cannot be opened in full-screen mode");
		} else {
			settingsDialog.setVisible(true);
		}
	}

	public void display(boolean fullScreen) {
		if (fullScreen) {
			try {
				displayFullScreen();
			} catch (FullScreenModeException e) {
				displayWindow();
			}
		} else {
			displayWindow();
		}
	}

	public void render(View view) {
		if (renderingEnabled) {
			render(inFullScreenMode() ? fullScreenWindow.getBufferStrategy() : canvas.getBufferStrategy(), view);
			++frames;
			if (frames >= app.clock().getTargetFramerate()) {
				String title = getTitle(app.clock().getFrameRate());
				EventQueue.invokeLater(() -> setTitle(title));
				frames = 0;
			}
		}
	}

	public void toggleDisplayMode() {
		if (inFullScreenMode()) {
			displayWindow();
		} else {
			try {
				displayFullScreen();
			} catch (FullScreenModeException e) {
				loginfo(e.getMessage());
			}
		}
	}

	private JFrame createFullScreenWindow() {
		JFrame window = new JFrame();
		window.setBackground(app.settings().bgColor);
		window.setUndecorated(true);
		window.setResizable(false);
		window.setIgnoreRepaint(true);
		window.setCursor(window.getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
				new Point(), null));
		return window;
	}

	public JFrame getFullScreenWindow() {
		return fullScreenWindow;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	private void displayWindow() {
		renderingEnabled = false;
		device.setFullScreenWindow(null);
		setVisible(true);
		requestFocus();
		canvas.createBufferStrategy(2);
		loginfo("Entered window mode, resolution %dx%d (%dx%d scaled by %.2f)", (int) (width * app.settings().scale),
				(int) (height * app.settings().scale), width, height, app.settings().scale);
		renderingEnabled = true;
	}

	private void displayFullScreen() throws FullScreenModeException {
		if (!device.isFullScreenSupported()) {
			throw new FullScreenModeException("Device does not support full-screen exclusive mode.");
		}
		final DisplayMode mode = app.settings().fullScreenMode;
		if (!isValid(mode)) {
			throw new FullScreenModeException("Display mode not supported: " + getText(mode));
		}
		renderingEnabled = false;
		device.setFullScreenWindow(fullScreenWindow);
		if (device.isDisplayChangeSupported()) {
			device.setDisplayMode(mode);
			fullScreenWindow.createBufferStrategy(2);
			fullScreenWindow.requestFocus();
			loginfo("Entered full-screen mode %s", getText(mode));
		} else {
			device.setFullScreenWindow(null);
			throw new FullScreenModeException("Display change not supported: " + getText(mode));
		}
		renderingEnabled = true;
	}

	public boolean inFullScreenMode() {
		return device.getFullScreenWindow() != null;
	}

	private boolean isValid(DisplayMode mode) {
		return Arrays.stream(device.getDisplayModes()).anyMatch(dm -> dm.getWidth() == mode.getWidth()
				&& dm.getHeight() == mode.getHeight() && dm.getBitDepth() == mode.getBitDepth());
	}

	private String getText(DisplayMode mode) {
		return format("%d x %d, depth: %d, refresh rate: %s", mode.getWidth(), mode.getHeight(), mode.getBitDepth(),
				mode.getRefreshRate() == 0 ? "unknown" : mode.getRefreshRate() + " Hz");
	}

	private String getTitle(int fps) {
		if (app.settings().titleExtended) {
			return format("%s [%dfps %d x %dpx * %.2f]", app.settings().title, fps, width, height, app.settings().scale);
		}
		return app.settings().title;
	}

	private void render(BufferStrategy buffer, View view) {
		do {
			do {
				Graphics2D g = null;
				try {
					g = (Graphics2D) buffer.getDrawGraphics();
					drawView(view, g);
				} catch (Exception x) {
					x.printStackTrace(System.err);
				} finally {
					if (g != null) {
						g.dispose();
					}
				}
			} while (buffer.contentsRestored());
			try {
				buffer.show();
			} catch (Exception x) {
				x.printStackTrace(System.err);
			}
		} while (buffer.contentsLost());
	}

	private void drawView(View view, Graphics2D gc) {
		Graphics2D g = (Graphics2D) gc.create();
		if (inFullScreenMode()) {
			int screenWidth = fullScreenWindow.getWidth(), screenHeight = fullScreenWindow.getHeight();
			double scale = Math.min(1.0 * screenWidth / width, 1.0 * screenHeight / height);
			int scaledWidth = (int) Math.round(scale * width);
			int scaledHeight = (int) Math.round(scale * height);
			g.setColor(app.settings().bgColor);
			g.fillRect(0, 0, screenWidth, screenHeight);
			g.translate((screenWidth - scaledWidth) / 2, (screenHeight - scaledHeight) / 2);
			g.setClip(0, 0, scaledWidth, scaledHeight);
			g.scale(scale, scale);
		} else {
			g.setColor(app.settings().bgColor);
			g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			g.scale(app.settings().scale, app.settings().scale);
		}
		view.draw(g);
		if (app.isPaused()) {
			int pauseTextSize = (width / PAUSED_TEXT.length()) * 160 / 100;
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, pauseTextSize));
			int textWidth = g.getFontMetrics().stringWidth(PAUSED_TEXT);
			g.setColor(Color.RED);
			g.drawString(PAUSED_TEXT, (width - textWidth) / 2, height / 2);
		}
		g.dispose();
	}
}