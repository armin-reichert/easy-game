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
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
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
		Image cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
		return Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "invisibleCursor");
	}

	private static JFrame createAppFrame(Application app) {
		Dimension size = new Dimension((int) Math.ceil(app.settings.width * app.settings.scale),
				(int) Math.ceil(app.settings.height * app.settings.scale));

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
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				LOGGER.info("Application window closing, app will exit...");
				app.exit();
			}
		});
		MouseHandler.handleMouseEventsFor(canvas);
		KeyboardHandler.handleKeyEventsFor(frame);

		frame.pack();
		frame.setLocationRelativeTo(null);

		return frame;
	}

	private static Window createFullscreenWindow(Application app) {
		JFrame window = new JFrame();
		window.setBackground(app.settings.bgColor);
		if (app.settings.fullScreenCursor == false) {
			window.setCursor(createInvisibleCursor());
		}
		window.setUndecorated(true);
		window.setIgnoreRepaint(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				LOGGER.info("Application window closing, app will exit...");
				app.exit();
			}
		});
		MouseHandler.handleMouseEventsFor(window);
		KeyboardHandler.handleKeyEventsFor(window);
		return window;
	}

	private Application app;
	private GraphicsDevice device;
	private JFrame appFrame;
	private Canvas canvas;
	private Window fullScreenWindow;
	private int renderCount;
	private AppSettingsDialog settingsDialog;

	public AppShell(Application app) {
		this.app = app;
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		appFrame = createAppFrame(app);
		canvas = (Canvas) appFrame.getContentPane().getComponent(0);
		fullScreenWindow = createFullscreenWindow(app);
		if (app.settings.fullScreenOnStart) {
			enterFullScreenMode();
		} else {
			enterWindowMode();
		}
		LOGGER.info("Application shell created.");
	}

	private boolean fullScreenMode() {
		return device.getFullScreenWindow() != null;
	}

	public void showSettingsDialog() {
		if (settingsDialog == null) {
			settingsDialog = new AppSettingsDialog(appFrame, app);
		}
		if (fullScreenMode()) {
			LOGGER.info("Settings dialog cannot be opened in full-screen mode");
		} else {
			settingsDialog.setVisible(true);
		}
	}

	private String formatTitle(int ups, int fps) {
		if (app.settings.titleExtended) {
			return format("%s [%d fps, %d ups, %dx%d px, scaled %.2f]", app.settings.title, fps, ups,
					app.settings.width, app.settings.height, app.settings.scale);
		}
		return app.settings.title;
	}

	public void render(View view) {
		Objects.requireNonNull(view);
		render(fullScreenMode() ? fullScreenWindow.getBufferStrategy() : canvas.getBufferStrategy(), view);
	}

	private void render(BufferStrategy buffer, View view) {
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
			String title = formatTitle(app.clock.getUpdateRate(), app.clock.getRenderRate());
			EventQueue.invokeLater(() -> appFrame.setTitle(title));
			renderCount = 0;
		}
	}

	private void drawScaledView(View view, Graphics2D g) {
		Graphics2D sg = (Graphics2D) g.create();
		sg.setColor(app.settings.bgColor);
		if (fullScreenMode()) {
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
				drawTextCentered(sg, PAUSED_TEXT, unscaledWidth, unscaledHeight);
			}
		} else {
			int width = canvas.getWidth(), height = canvas.getHeight();
			sg.fillRect(0, 0, width, height);
			sg.scale(app.settings.scale, app.settings.scale);
			view.draw(sg);
			if (app.isPaused()) {
				drawTextCentered(g, PAUSED_TEXT, width, height);
			}
		}
		sg.dispose();
	}

	protected void drawTextCentered(Graphics2D g, String text, int width, int height) {
		g.setColor(Color.RED);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, width / text.length()));
		int textWidth = g.getFontMetrics().stringWidth(text);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.drawString(text, (width - textWidth) / 2, height / 2);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}

	public void toggleDisplayMode() {
		if (fullScreenMode()) {
			enterWindowMode();
		} else {
			enterFullScreenMode();
		}
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
		if (!isValidDisplayMode(mode)) {
			LOGGER.info("Cannot enter full-screen mode: Display mode not supported: " + formatDisplayMode(mode));
			return;
		}
		device.setFullScreenWindow(fullScreenWindow);
		if (device.isDisplayChangeSupported()) {
			device.setDisplayMode(mode);
			fullScreenWindow.createBufferStrategy(2);
			fullScreenWindow.requestFocus();
			LOGGER.info("Entered full-screen mode " + formatDisplayMode(mode));
		} else {
			device.setFullScreenWindow(null);
			LOGGER.info("Cannot enter full-screen mode: Display mode change not supported: " + formatDisplayMode(mode));
		}
	}

	private void enterWindowMode() {
		device.setFullScreenWindow(null);
		appFrame.setVisible(true);
		appFrame.requestFocus();
		canvas.createBufferStrategy(2);
		LOGGER.info(String.format("Entered window mode %dx%d", app.settings.width, app.settings.height));
	}

	private boolean isValidDisplayMode(DisplayMode displayMode) {
		for (DisplayMode dm : device.getDisplayModes()) {
			if (dm.getWidth() == displayMode.getWidth() && dm.getHeight() == displayMode.getHeight()
					&& dm.getBitDepth() == displayMode.getBitDepth()) {
				return true;
			}
		}
		return false;
	}

	private String formatDisplayMode(DisplayMode mode) {
		return format("%d x %d, depth: %d, refresh rate: %d", mode.getWidth(), mode.getHeight(),
				mode.getBitDepth(), mode.getRefreshRate());
	}
}