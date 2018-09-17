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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import de.amr.easy.game.Application;
import de.amr.easy.game.input.KeyboardHandler;
import de.amr.easy.game.input.MouseHandler;
import de.amr.easy.game.view.View;

/**
 * The application shell provides the window (optionally full-screen) where the current view of the
 * application is rendered. The display is actively rendered depending on the frequency of the
 * application clock.
 * <p>
 * Using the F11-key the user can toggle between full-screen and windowed mode.
 * 
 * @author Armin Reichert
 */
public class ApplicationShell implements PropertyChangeListener {

	private enum Mode {
		WINDOW_MODE, FULLSCREEN_MODE
	}

	private static final String PAUSED_TEXT = "PAUSED (Press CTRL+P to continue)";

	private final Application app;
	private final Canvas canvas;
	private final JFrame frame;
	private final GraphicsDevice device;
	private final Cursor invisibleCursor;
	private BufferStrategy buffer;
	private volatile boolean renderingEnabled;
	private int ups, fps;
	private Mode currentMode;

	public ApplicationShell(Application app) {
		this.app = app;
		app.clock.addRenderListener(this);
		app.clock.addUpdateListener(this);
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		canvas = createCanvas();
		frame = createFrame();
		renderingEnabled = true;
		invisibleCursor = createInvisibleCursor();
		if (app.settings.fullScreenOnStart) {
			enterFullScreenExclusiveMode();
		} else {
			enterWindowMode();
		}
		LOGGER.info("Application shell created.");
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if ("ups".equals(e.getPropertyName())) {
			ups = (int) e.getNewValue();
		} else if ("fps".equals(e.getPropertyName())) {
			fps = (int) e.getNewValue();
		}
		EventQueue.invokeLater(() -> frame.setTitle(formatTitle()));
	}

	private String formatTitle() {
		if (app.settings.titleExtended) {
			return format("%s [%d fps, %d ups, %dx%d px, scaled %.2f]", app.settings.title, fps, ups,
					app.settings.width, app.settings.height, app.settings.scale);
		}
		return app.settings.title;
	}

	private int getWidth() {
		return currentMode == Mode.FULLSCREEN_MODE ? frame.getWidth() : canvas.getWidth();
	}

	private int getHeight() {
		return currentMode == Mode.FULLSCREEN_MODE ? frame.getHeight() : canvas.getHeight();
	}

	public void renderView(View view) {
		if (!renderingEnabled) {
			return;
		}
		if (buffer == null) {
			LOGGER.info("Could not render view: no buffer allocated");
			return;
		}
		do {
			do {
				Graphics2D g = null;
				try {
					g = (Graphics2D) buffer.getDrawGraphics();
					if (g != null) {
						drawView(view, g);
					}
				} catch (Exception x) {
					x.printStackTrace(System.err);
					LOGGER.info("Exception occured when rendering current view");
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

	private void drawView(View view, Graphics2D g) {
		g.setColor(app.settings.bgColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (currentMode == Mode.FULLSCREEN_MODE) {
			DisplayMode fullScreenMode = app.settings.fullScreenMode.getDisplayMode();
			int fullScreenWidth = fullScreenMode.getWidth();
			int fullScreenHeight = fullScreenMode.getHeight();
			int appWidth = (int) (app.settings.width * app.settings.scale);
			int appHeight = (int) (app.settings.height * app.settings.scale);
			if (appWidth < fullScreenWidth) {
				g.translate((fullScreenWidth - appWidth) / 2, 0);
			}
			if (appHeight < fullScreenHeight) {
				g.translate(0, (fullScreenHeight - appHeight) / 2);
			}
			g.setClip(0, 0, appWidth, appHeight);
		}
		g.scale(app.settings.scale, app.settings.scale);
		view.draw(g);
		if (app.isPaused()) {
			drawTextCentered(g, PAUSED_TEXT, getWidth(), getHeight());
		}
	}

	protected void drawTextCentered(Graphics2D g, String text, int width, int height) {
		g.setColor(Color.RED);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, width / text.length()));
		int textWidth = g.getFontMetrics().stringWidth(text);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.drawString(text, (width - textWidth) / 2, height / 2);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}

	private JFrame createFrame() {
		JFrame frame = new JFrame(device.getDefaultConfiguration());
		frame.setTitle(app.settings.title);
		frame.setBackground(app.settings.bgColor);
		frame.setResizable(false);
		frame.setFocusable(true);
		frame.setIgnoreRepaint(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(canvas, BorderLayout.CENTER);
		KeyboardHandler.handleKeyEventsFor(frame);
		frame.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F11) {
					toggleFullScreen();
				}
				if (e.getKeyCode() == KeyEvent.VK_F2) {
					showControlDialog();
				}
			}
		});
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				LOGGER.info("Application window closing, app will exit...");
				app.exit();
			}
		});
		return frame;
	}

	private Canvas createCanvas() {
		Canvas canvas = new Canvas();
		Dimension size = new Dimension(Math.round(app.settings.width * app.settings.scale),
				Math.round(app.settings.height * app.settings.scale));
		canvas.setPreferredSize(size);
		canvas.setSize(size);
		canvas.setBackground(app.settings.bgColor);
		canvas.setIgnoreRepaint(true);
		canvas.setFocusable(false);
		MouseHandler.handleMouseEventsFor(canvas);
		return canvas;
	}

	private Cursor createInvisibleCursor() {
		Image cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
		return Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "invisibleCursor");
	}

	private void toggleFullScreen() {
		if (currentMode == Mode.FULLSCREEN_MODE) {
			enterWindowMode();
		} else {
			enterFullScreenExclusiveMode();
		}
	}

	private void enterFullScreenExclusiveMode() {
		if (app.settings.fullScreenMode == null) {
			LOGGER.info("Cannot enter full-screen mode: No full-screen mode specified for this application.");
			return;
		}
		if (!device.isFullScreenSupported()) {
			LOGGER.info("Cannot enter full-screen mode: device does not allow support full-screen mode.");
			return;
		}
		DisplayMode mode = app.settings.fullScreenMode.getDisplayMode();
		if (!isValidDisplayMode(mode)) {
			LOGGER.info("Cannot enter full-screen mode: Display mode not supported: " + formatDisplayMode(mode));
			return;
		}
		renderingEnabled = false;
		frame.dispose();
		frame.setVisible(false);
		frame.setUndecorated(true);
		frame.setCursor(invisibleCursor);
		frame.validate();
		device.setFullScreenWindow(frame);
		device.setDisplayMode(mode);
		frame.requestFocus();
		LOGGER.info("Entered full-screen exclusive mode: " + formatDisplayMode(mode));
		renderingEnabled = true;
		currentMode = Mode.FULLSCREEN_MODE;
	}

	private void enterWindowMode() {
		renderingEnabled = false;
		// Note: The order of the following statements is important!
		device.setFullScreenWindow(null);
		frame.dispose();
		frame.setUndecorated(false);
		frame.setCursor(Cursor.getDefaultCursor());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		canvas.createBufferStrategy(2);
		buffer = canvas.getBufferStrategy();
		frame.requestFocus();
		LOGGER.info(String.format("Entered window-mode: %dx%d", app.settings.width, app.settings.height));
		renderingEnabled = true;
		currentMode = Mode.WINDOW_MODE;
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

	private ClockFrequencyDialog clockFrequencyDialog;

	private void showControlDialog() {
		if (clockFrequencyDialog == null) {
			clockFrequencyDialog = new ClockFrequencyDialog(frame, app);
		}
		clockFrequencyDialog.setVisible(true);
	}
}