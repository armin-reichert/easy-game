package de.amr.easy.game.ui;

import static de.amr.easy.game.Application.LOGGER;
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
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JFrame;

import de.amr.easy.game.Application;
import de.amr.easy.game.input.KeyboardHandler;
import de.amr.easy.game.input.MouseHandler;
import de.amr.easy.game.view.View;

/**
 * The application shell provides the window where the current view of the application is rendered.
 * In window mode, the view is rendered to a double-buffered canvas which is the single child of the
 * application frame. In fullscreen mode, the view is rendered to a fullscreen-exclusive frame. In
 * both cases, active rendering with the frequency of the application clock is performed.
 * <p>
 * Using the F11-key the user can toggle between full-screen and windowed mode.
 * 
 * @author Armin Reichert
 */
public class AppShell {

	private static final String PAUSED_TEXT = "PAUSED (Press CTRL+P to continue)";

	private static Cursor createInvisibleCursor() {
		Image dot = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
		return Toolkit.getDefaultToolkit().createCustomCursor(dot, new Point(0, 0), "invisibleCursor");
	}

	private class WindowClosingHandler extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			LOGGER.info("Application window closing, app will exit...");
			app.exit();
		}
	};

	private final Application app;
	private final GraphicsDevice device;
	private final JFrame appFrame;
	private final Canvas canvas;
	private final Window fullScreenWindow;
	private int renderCount;
	private AppSettingsDialog settingsDialog;
	private volatile boolean renderingEnabled;

	public AppShell(Application app) {
		this.app = app;
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		appFrame = createAppFrame();
		canvas = (Canvas) appFrame.getContentPane().getComponent(0);
		fullScreenWindow = createFullscreenWindow();
		if (app.settings.fullScreenOnStart) {
			enterFullScreenMode();
		} else {
			enterWindowMode();
		}
		LOGGER.info("Application shell created.");
	}

	public void render(View view) {
		Objects.requireNonNull(view);
		render(inFullScreenMode() ? fullScreenWindow.getBufferStrategy() : canvas.getBufferStrategy(), view);
	}

	public void toggleDisplayMode() {
		if (inFullScreenMode()) {
			enterWindowMode();
		} else {
			enterFullScreenMode();
		}
	}

	public void showSettingsDialog() {
		if (settingsDialog == null) {
			settingsDialog = new AppSettingsDialog(appFrame, app);
		}
		if (inFullScreenMode()) {
			LOGGER.info("Settings dialog cannot be opened in full-screen mode");
		} else {
			settingsDialog.setVisible(true);
		}
	}

	private void enterWindowMode() {
		renderingEnabled = false;
		device.setFullScreenWindow(null);
		appFrame.setVisible(true);
		appFrame.requestFocus();
		canvas.createBufferStrategy(2);
		LOGGER.info(String.format("Entered window mode %dx%d", app.settings.width, app.settings.height));
		renderingEnabled = true;
	}

	private void enterFullScreenMode() {
		if (!device.isFullScreenSupported()) {
			LOGGER.info("Cannot enter full-screen mode: device does not support full-screen mode.");
			return;
		}
		DisplayMode mode = app.settings.fullScreenMode;
		if (mode == null) {
			LOGGER.info("Cannot enter full-screen mode: No full-screen mode specified in application settings.");
			return;
		}
		if (!isValid(mode)) {
			LOGGER.info("Cannot enter full-screen mode: Display mode not supported: " + getText(mode));
			return;
		}
		renderingEnabled = false;
		device.setFullScreenWindow(fullScreenWindow);
		if (device.isDisplayChangeSupported()) {
			device.setDisplayMode(mode);
			fullScreenWindow.createBufferStrategy(2);
			fullScreenWindow.requestFocus();
			LOGGER.info("Entered full-screen mode " + getText(mode));
		} else {
			device.setFullScreenWindow(null);
			LOGGER.info("Cannot enter full-screen mode: Display change not supported: " + getText(mode));
		}
		renderingEnabled = true;
	}

	private JFrame createAppFrame() {
		int scaledWidth = (int) Math.ceil(app.settings.width * app.settings.scale);
		int scaledHeight = (int) Math.ceil(app.settings.height * app.settings.scale);
		Dimension size = new Dimension(scaledWidth, scaledHeight);
		Canvas canvas = new Canvas();
		canvas.setPreferredSize(size);
		canvas.setSize(size);
		canvas.setBackground(app.settings.bgColor);
		canvas.setIgnoreRepaint(true);
		canvas.setFocusable(false);

		JFrame frame = new JFrame();
		frame.setTitle(app.settings.title);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(canvas, BorderLayout.CENTER);
		frame.addWindowListener(new WindowClosingHandler());
		MouseHandler.handleMouseEventsFor(canvas);
		KeyboardHandler.handleKeyEventsFor(frame);

		frame.pack();
		frame.setLocationRelativeTo(null);

		return frame;
	}

	private Window createFullscreenWindow() {
		JFrame window = new JFrame();
		window.setBackground(app.settings.bgColor);
		if (!app.settings.fullScreenCursor) {
			window.setCursor(createInvisibleCursor());
		}
		window.setResizable(false);
		window.setUndecorated(true);
		window.setIgnoreRepaint(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.addWindowListener(new WindowClosingHandler());
		MouseHandler.handleMouseEventsFor(window);
		KeyboardHandler.handleKeyEventsFor(window);
		return window;
	}

	private boolean inFullScreenMode() {
		return device.getFullScreenWindow() != null;
	}

	private boolean isValid(DisplayMode mode) {
		return Arrays.stream(device.getDisplayModes()).anyMatch(dm -> dm.getWidth() == mode.getWidth()
				&& dm.getHeight() == mode.getHeight() && dm.getBitDepth() == mode.getBitDepth());
	}

	private String getText(DisplayMode mode) {
		return format("%d x %d, depth: %d, refresh rate: %s", mode.getWidth(), mode.getHeight(),
				mode.getBitDepth(), mode.getRefreshRate() == 0 ? "unknown" : mode.getRefreshRate() + " Hz");
	}

	private String getTitle(int ups, int fps) {
		if (app.settings.titleExtended) {
			return format("%s [%d fps, %d ups, %dx%d px, scaled %.2f]", app.settings.title, fps, ups,
					app.settings.width, app.settings.height, app.settings.scale);
		}
		return app.settings.title;
	}

	private void render(BufferStrategy buffer, View view) {
		if (!renderingEnabled) {
			return;
		}
		do {
			do {
				Graphics2D g = null;
				try {
					g = (Graphics2D) buffer.getDrawGraphics();
					drawScaledView(view, g);
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

		++renderCount;
		if (renderCount >= app.clock.getFrequency()) {
			String title = getTitle(app.clock.getUpdateRate(), app.clock.getRenderRate());
			EventQueue.invokeLater(() -> appFrame.setTitle(title));
			renderCount = 0;
		}
	}

	private void drawScaledView(View view, Graphics2D g) {
		Graphics2D sg = (Graphics2D) g.create();
		sg.setColor(app.settings.bgColor);
		if (inFullScreenMode()) {
			sg.fillRect(0, 0, fullScreenWindow.getWidth(), fullScreenWindow.getHeight());
			int unscaledWidth = app.settings.width;
			int unscaledHeight = app.settings.height;
			double zoom = Math.min(((double) fullScreenWindow.getWidth()) / unscaledWidth,
					((double) fullScreenWindow.getHeight()) / unscaledHeight);
			double scaledWidth = zoom * unscaledWidth;
			double scaledHeight = zoom * unscaledHeight;
			sg.translate((fullScreenWindow.getWidth() - scaledWidth) / 2,
					(fullScreenWindow.getHeight() - scaledHeight) / 2);
			sg.setClip(0, 0, (int) scaledWidth, (int) scaledHeight);
			sg.scale(zoom, zoom);
			view.draw(sg);
			if (app.isPaused()) {
				drawCenteredText(sg, PAUSED_TEXT, unscaledWidth, unscaledHeight);
			}
		} else {
			int width = canvas.getWidth(), height = canvas.getHeight();
			sg.fillRect(0, 0, width, height);
			sg.scale(app.settings.scale, app.settings.scale);
			view.draw(sg);
			if (app.isPaused()) {
				drawCenteredText(g, PAUSED_TEXT, width, height);
			}
		}
		sg.dispose();
	}

	private void drawCenteredText(Graphics2D g, String text, int containerWidth, int containerHeight) {
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, containerWidth / text.length()));
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);
		int dx = (containerWidth - (int) bounds.getWidth()) / 2;
		int dy = containerHeight / 2;
		g.setColor(Color.WHITE);
		g.translate(dx, dy);
		g.fill(bounds);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.RED);
		g.drawString(text, 0, 0);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.translate(-dx, -dy);
	}
}