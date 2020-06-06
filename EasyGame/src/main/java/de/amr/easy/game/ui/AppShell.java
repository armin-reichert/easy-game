package de.amr.easy.game.ui;

import static de.amr.easy.game.Application.LOGGER;
import static de.amr.easy.game.Application.loginfo;
import static java.lang.String.format;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JFrame;

import de.amr.easy.game.Application;
import de.amr.easy.game.input.Keyboard;
import de.amr.easy.game.input.KeyboardHandler;
import de.amr.easy.game.input.Mouse;
import de.amr.easy.game.input.MouseHandler;
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

	public AppShell(Application app, int width, int height) {
		this.app = app;
		this.width = width;
		this.height = height;

		setIconImage(app.getIcon());
		setTitle(app.settings().title);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		float scaledWidth = width * app.settings().scale;
		float scaledHeight = height * app.settings().scale;
		Dimension size = new Dimension(Math.round(scaledWidth), Math.round(scaledHeight));
		canvas = new Canvas();
		canvas.setPreferredSize(size);
		canvas.setSize(size);
		canvas.setBackground(app.settings().bgColor);
		canvas.setIgnoreRepaint(true);
		canvas.setFocusable(false);
		add(canvas, BorderLayout.CENTER);

		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		if (app.settings().fullScreenMode == null) {
			DisplayMode[] modes = device.getDisplayModes();
			app.settings().fullScreenMode = modes[modes.length - 1]; // TODO find best fitting mode
		}
		fullScreenWindow = createFullScreenWindow();

		Keyboard.handler = new KeyboardHandler();
		Mouse.handler = new MouseHandler(app.settings().scale);
		KeyListener internalKeyHandler = createInternalKeyHandler();
		WindowListener windowHandler = createWindowHandler();
		addKeyListener(internalKeyHandler);
		addKeyListener(Keyboard.handler);
		addWindowListener(windowHandler);
		canvas.addMouseListener(Mouse.handler);
		canvas.addMouseMotionListener(Mouse.handler);
		fullScreenWindow.addKeyListener(internalKeyHandler);
		fullScreenWindow.addKeyListener(Keyboard.handler);
		fullScreenWindow.addWindowListener(windowHandler);
		fullScreenWindow.addMouseListener(Mouse.handler);
		fullScreenWindow.addMouseMotionListener(Mouse.handler);

		pack();
		setLocationRelativeTo(null);
	}

	private KeyListener createInternalKeyHandler() {
		return new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_P) {
					app.togglePause();
				} else if (e.getKeyCode() == KeyEvent.VK_F2) {
					app.showSettingsDialog();
				} else if (e.getKeyCode() == KeyEvent.VK_F11 || (e.getKeyCode() == KeyEvent.VK_ESCAPE && inFullScreenMode())) {
					app.toggleFullScreen();
				}
			}
		};
	}

	private WindowListener createWindowHandler() {
		return new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				LOGGER.info("Application window closing, app will exit...");
				app.close();
			}
		};
	}

	public void showSettingsDialog() {
		if (inFullScreenMode()) {
			loginfo("Settings dialog cannot be opened in full-screen mode");
			return;
		}
		if (settingsDialog == null) {
			settingsDialog = new AppSettingsDialog(this);
			settingsDialog.setApp(app);
		}
		settingsDialog.setVisible(true);
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
		BufferStrategy strategy = inFullScreenMode() ? fullScreenWindow.getBufferStrategy() : canvas.getBufferStrategy();
		if (strategy != null) {
			try {
				do {
					do {
						Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
						renderView(view, g);
						g.dispose();
					} while (strategy.contentsRestored());
					strategy.show();
				} while (strategy.contentsLost());
				++frames;
			} catch (Exception x) {
				loginfo("Rendering failed: %s", x); // happens when switching from fullscreen to window mode
			}
		}
		// update window title text
		if (frames >= app.clock().getTargetFramerate()) {
			frames = 0;
			EventQueue.invokeLater(() -> setTitle(titleText()));
		}
	}

	private void renderView(View view, Graphics2D g) {
		g = (Graphics2D) g.create();
		if (inFullScreenMode()) {
			float screenWidth = fullScreenWindow.getWidth(), screenHeight = fullScreenWindow.getHeight();
			float scale = Math.min(screenWidth / width, screenHeight / height);
			int scaledWidth = Math.round(scale * width);
			int scaledHeight = Math.round(scale * height);
			g.setColor(app.settings().bgColor);
			g.fillRect(0, 0, Math.round(screenWidth), Math.round(screenHeight));
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
			int fontSize = Math.round((width / PAUSED_TEXT.length()) * 1.6f);
			int textWidth = g.getFontMetrics().stringWidth(PAUSED_TEXT);
			g.setColor(Color.RED);
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
			g.drawString(PAUSED_TEXT, (width - textWidth) / 2, height / 2);
		}
		g.dispose();
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
		return window;
	}

	public JFrame getFullScreenWindow() {
		return fullScreenWindow;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	private void displayWindow() {
		device.setFullScreenWindow(null);
		requestFocus();
		canvas.createBufferStrategy(2);
		setVisible(true);
		loginfo("Entered window mode, resolution %dx%d (%dx%d px scaled by %.2f)", (int) (width * app.settings().scale),
				(int) (height * app.settings().scale), width, height, app.settings().scale);
	}

	private void displayFullScreen() throws FullScreenModeException {
		if (!device.isFullScreenSupported()) {
			throw new FullScreenModeException("Device does not support full-screen exclusive mode.");
		}
		final DisplayMode mode = app.settings().fullScreenMode;
		if (!isValidMode(mode)) {
			throw new FullScreenModeException("Display mode not supported: " + displayModeText(mode));
		}
		if (!app.settings().fullScreenCursor) {
			Cursor invisibleCursor = fullScreenWindow.getToolkit()
					.createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null);
			fullScreenWindow.setCursor(invisibleCursor);
		}
		device.setFullScreenWindow(fullScreenWindow);
		if (device.isDisplayChangeSupported()) {
			device.setDisplayMode(mode);
			fullScreenWindow.createBufferStrategy(2);
			fullScreenWindow.requestFocus();
			loginfo("Entered full-screen mode %s", displayModeText(mode));
		} else {
			device.setFullScreenWindow(null);
			throw new FullScreenModeException("Display change not supported: " + displayModeText(mode));
		}
	}

	public boolean inFullScreenMode() {
		return device.getFullScreenWindow() != null;
	}

	private boolean isValidMode(DisplayMode mode) {
		return Arrays.stream(device.getDisplayModes()).anyMatch(dm -> dm.getWidth() == mode.getWidth()
				&& dm.getHeight() == mode.getHeight() && dm.getBitDepth() == mode.getBitDepth());
	}

	private String displayModeText(DisplayMode mode) {
		return format("%d x %d, depth: %d, refresh rate: %s", mode.getWidth(), mode.getHeight(), mode.getBitDepth(),
				mode.getRefreshRate() == 0 ? "unknown" : mode.getRefreshRate() + " Hz");
	}

	private String titleText() {
		if (app.settings().titleExtended) {
			return format("%s [%d Hz %dx%d px scaled by %.2f]", app.settings().title, app.clock().getFrameRate(), width,
					height, app.settings().scale);
		}
		return app.settings().title;
	}
}