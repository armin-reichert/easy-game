package de.amr.easy.game.ui;

import static de.amr.easy.game.Application.LOGGER;
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
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import de.amr.easy.game.Application;
import de.amr.easy.game.input.KeyboardHandler;
import de.amr.easy.game.input.MouseHandler;
import de.amr.easy.game.view.View;

/**
 * The application shell provides the window or the full-screen display where the current scene of
 * the application is shown.
 * 
 * @author Armin Reichert
 */
public class ApplicationShell implements PropertyChangeListener {

	private final Application app;
	private final Canvas canvas;
	private final JFrame frame;
	private final GraphicsDevice device;
	private BufferStrategy buffer;
	private boolean fullScreen;
	private int ups, fps;

	public ApplicationShell(Application app) {
		this.app = app;
		Application.PULSE.addRenderListener(this);
		Application.PULSE.addUpdateListener(this);
		fullScreen = app.settings.fullScreenOnStart;
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		canvas = createCanvas();
		frame = createFrame();
		LOGGER.info("Application shell created.");
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if ("ups".equals(e.getPropertyName())) {
			ups = (int) e.getNewValue();
		} else if ("fps".equals(e.getPropertyName())) {
			fps = (int) e.getNewValue();
		}
		EventQueue.invokeLater(() -> {
			frame.setTitle(format("%s [%d fps, %d ups, %dx%d pixel, scaling %.2f]", app.settings.title, fps, ups,
					app.settings.width, app.settings.height, app.settings.scale));
		});
	}

	public void showApplication() {
		if (fullScreen) {
			showFullScreen();
		} else {
			showAsWindow();
		}
	}

	public int getWidth() {
		return fullScreen ? frame.getWidth() : canvas.getWidth();
	}

	public int getHeight() {
		return fullScreen ? frame.getHeight() : canvas.getHeight();
	}

	public void renderView(View view) {
		if (buffer == null) {
			return;
		}
		do {
			do {
				Graphics2D g = null;
				try {
					g = (Graphics2D) buffer.getDrawGraphics();
					if (g != null) {
						g.setColor(app.settings.bgColor);
						g.fillRect(0, 0, getWidth(), getHeight());
						if (fullScreen && app.settings.fullScreenMode != null) {
							DisplayMode mode = app.settings.fullScreenMode.getDisplayMode();
							float scaledWidth = app.settings.width * app.settings.scale;
							if (mode.getWidth() > scaledWidth) {
								g.translate((mode.getWidth() - scaledWidth) / 2, 0);
							}
						}
						Graphics2D sg = (Graphics2D) g.create();
						sg.scale(app.settings.scale, app.settings.scale);
						view.draw(sg);
						sg.dispose();
						if (app.isPaused()) {
							String text = "PAUSED (Press CTRL+P to continue)";
							g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
							g.setColor(Color.RED);
							g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, getWidth() / text.length()));
							Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);
							g.drawString(text, (int) (getWidth() - bounds.getWidth()) / 2, getHeight() / 2);
						}
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
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ESCAPE) {
					app.exit();
				} else if (key == KeyEvent.VK_F11) {
					toggleFullScreen();
				} else if (key == KeyEvent.VK_F2) {
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

	public Canvas getCanvas() {
		return canvas;
	}

	private void toggleFullScreen() {
		fullScreen = !fullScreen;
		showApplication();
	}

	private void showFullScreen() {
		if (!device.isFullScreenSupported()) {
			LOGGER.info("Full-screen mode not supported for this device.");
			return;
		}
		if (app.settings.fullScreenMode == null) {
			LOGGER.info("Cannot enter full-screen mode: No full-screen mode specified.");
			return;
		}
		DisplayMode mode = app.settings.fullScreenMode.getDisplayMode();
		if (!isValidDisplayMode(mode)) {
			LOGGER.info("Cannot enter full-screen mode: Display mode not supported: " + formatDisplayMode(mode));
			return;
		}
		frame.dispose();
		frame.setVisible(false);
		frame.setUndecorated(true);
		frame.validate();
		frame.requestFocus();
		device.setFullScreenWindow(frame);
		device.setDisplayMode(mode);
		LOGGER.info("Full-screen mode: " + formatDisplayMode(mode));
	}

	private void showAsWindow() {
		// Note: The order of the following statements is important!
		device.setFullScreenWindow(null);
		frame.dispose();
		frame.setUndecorated(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		canvas.createBufferStrategy(2);
		frame.requestFocus();
		buffer = canvas.getBufferStrategy();
		LOGGER.info(String.format("Window-mode: %dx%d", app.settings.width, app.settings.height));
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
		return format("%d x %d, depth: %d, refresh rate: %d", mode.getWidth(), mode.getHeight(), mode.getBitDepth(),
				mode.getRefreshRate());
	}

	// TODO: provide useful control and info dialog

	private AppControlDialog controlDialog;

	private void showControlDialog() {
		if (controlDialog == null) {
			controlDialog = new AppControlDialog(frame, app);
		}
		controlDialog.setVisible(true);
	}
}