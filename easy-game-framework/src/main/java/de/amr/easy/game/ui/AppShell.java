package de.amr.easy.game.ui;

import static de.amr.easy.game.Application.loginfo;
import static java.lang.Math.round;
import static java.lang.String.format;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.amr.easy.game.Application;
import de.amr.easy.game.input.Keyboard;
import de.amr.easy.game.input.Mouse;
import de.amr.easy.game.input.MouseHandler;
import de.amr.easy.game.ui.f2dialog.F2Dialog;
import de.amr.easy.game.ui.f2dialog.core.F2DialogImpl;
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

	// TODO localize
	private static final String PAUSED_TEXT = "PAUSED\n(Press CTRL+P to continue)";
	private static final Image MUTED_ICON = new ImageIcon(AppShell.class.getResource("/icons/muted.png")).getImage()
			.getScaledInstance(32, 32, Image.SCALE_SMOOTH);

	private final Application app;
	private final int viewWidth;
	private final int viewHeight;
	private final GraphicsDevice device;
	private final Canvas canvas;
	private final JFrame fullScreenWindow;
	private int frames;
	private F2DialogImpl f2Dialog;

	public AppShell(Application app, int width, int height) {
		this.app = app;
		this.viewWidth = width;
		this.viewHeight = height;

		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		if (app.settings().fullScreenMode == null) {
			DisplayMode[] modes = device.getDisplayModes();
			app.settings().fullScreenMode = modes[modes.length - 1]; // TODO find best fitting mode
		}

		setIconImage(app.getIcon());
		setTitle(app.settings().title);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		Dimension size = new Dimension(round(width * app.settings().scale), round(height * app.settings().scale));
		canvas = new Canvas();
		canvas.setPreferredSize(size);
		canvas.setSize(size);
		canvas.setIgnoreRepaint(true);
		canvas.setFocusable(false);
		add(canvas, BorderLayout.CENTER);

		fullScreenWindow = new JFrame();
		fullScreenWindow.setUndecorated(true);
		fullScreenWindow.setResizable(false);
		fullScreenWindow.setIgnoreRepaint(true);

		Mouse.handler = new MouseHandler(app.settings().scale);

		KeyListener predefinedKeys = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_P) {
					app.togglePause();
				} else if (e.getKeyCode() == KeyEvent.VK_F2) {
					app.showF2Dialog();
				} else if (e.getKeyCode() == KeyEvent.VK_F11) {
					app.toggleFullScreen();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && inFullScreenMode()) {
					app.toggleFullScreen();
				} else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_1) {
					app.settings().smoothRendering = !app.settings().smoothRendering;
				} else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_M) {
					if (app.soundManager().isMuted()) {
						app.soundManager().unmuteAll();
					} else {
						app.soundManager().muteAll();
					}
				}
			}
		};

		WindowListener windowHandler = new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				app.close();
			}
		};

		addKeyListener(predefinedKeys);
		Keyboard.listenTo(this);
		addWindowListener(windowHandler);

		canvas.addMouseListener(Mouse.handler);
		canvas.addMouseMotionListener(Mouse.handler);

		fullScreenWindow.addKeyListener(predefinedKeys);
		Keyboard.listenTo(fullScreenWindow);
		fullScreenWindow.addWindowListener(windowHandler);
		fullScreenWindow.addMouseListener(Mouse.handler);
		fullScreenWindow.addMouseMotionListener(Mouse.handler);

		createF2Dialog(700, 500);

		pack();
		setLocationRelativeTo(null);
	}

	public boolean inFullScreenMode() {
		return device.getFullScreenWindow() != null;
	}

	public void showWindow() {
		if (inFullScreenMode()) {
			device.setFullScreenWindow(null);
		}
		if (canvas.getBufferStrategy() == null) {
			canvas.createBufferStrategy(2);
		}
		requestFocus();
		setVisible(true);
		loginfo("Entered window mode, resolution %dx%d (%dx%d px scaled by %.2f)", (int) (viewWidth * app.settings().scale),
				(int) (viewHeight * app.settings().scale), viewWidth, viewHeight, app.settings().scale);
	}

	public void showFullScreenWindow() {
		if (!device.isFullScreenSupported()) {
			loginfo("Device does not support full-screen exclusive mode.");
			return;
		}

		fullScreenWindow.setVisible(true);
		fullScreenWindow.createBufferStrategy(2);
		fullScreenWindow.requestFocus();
		if (!app.settings().fullScreenCursor) {
			Cursor invisibleCursor = fullScreenWindow.getToolkit()
					.createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null);
			fullScreenWindow.setCursor(invisibleCursor);
		}

		device.setFullScreenWindow(fullScreenWindow);
		DisplayMode mode = app.settings().fullScreenMode;
		try {
			device.setDisplayMode(mode);
			loginfo("Entered full-screen mode %s", displayModeText(mode));
		} catch (Exception x) {
			loginfo("Device cannot use display mode " + mode);
		}
	}

	public F2Dialog getF2Dialog() {
		return f2Dialog;
	}

	public void showF2Dialog() {
		if (inFullScreenMode()) {
			loginfo("F2 dialog cannot be opened in full-screen mode");
			return;
		}
		f2Dialog.setVisible(true);
	}

	private void createF2Dialog(int width, int height) {
		f2Dialog = new F2DialogImpl(null);
		f2Dialog.setSize(width, height);
		f2Dialog.init();
	}

	public void render(View view) {
		BufferStrategy strategy = null;
		int width, height;
		if (inFullScreenMode()) {
			strategy = fullScreenWindow.getBufferStrategy();
			width = fullScreenWindow.getWidth();
			height = fullScreenWindow.getHeight();
		} else {
			strategy = canvas.getBufferStrategy();
			width = canvas.getWidth();
			height = canvas.getHeight();
		}
		if (strategy != null) {
			try {
				do {
					do {
						Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
						g.setColor(Color.BLACK);
						g.fillRect(0, 0, width, height);
						renderView(view, g);
						centerHorizontally(g);
						drawPausedText(g);
						drawMutedIcon(g);
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
			SwingUtilities.invokeLater(() -> setTitle(titleText()));
		}
	}

	private void renderView(View view, Graphics2D g) {
		g = (Graphics2D) g.create();
		if (app.settings().smoothRendering) {
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		}
		float scaling = scaling();
		centerHorizontally(g);
		g.scale(scaling, scaling);
		try {
			view.draw(g);
		} catch (Exception x) {
			loginfo("Exception occurred during view drawing");
			x.printStackTrace();
		} finally {
			g.dispose();
		}
	}

	private void centerHorizontally(Graphics2D g) {
		if (inFullScreenMode()) {
			Dimension scaledViewSize = scaledViewSize();
			float screenWidth = fullScreenWindow.getWidth(), screenHeight = fullScreenWindow.getHeight();
			g.translate((screenWidth - scaledViewSize.width) / 2, (screenHeight - scaledViewSize.height) / 2);
			g.setClip(0, 0, scaledViewSize.width, scaledViewSize.height);
		}
	}

	private void drawPausedText(Graphics2D g) {
		g = (Graphics2D) g.create();
		if (app.isPaused()) {
			Dimension scaledViewSize = scaledViewSize();
			String[] lines = PAUSED_TEXT.split("\n");
			int maxLineLength = Arrays.stream(lines).map(String::length).max(Integer::compare).get();
			int fontSize = round((scaledViewSize.width / maxLineLength) * 1.6f);
			Font font = new Font(Font.MONOSPACED, Font.BOLD, fontSize);
			g.setColor(new Color(120, 120, 120, 80));
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.fillRect(0, 0, scaledViewSize.width, scaledViewSize.height);
			g.setColor(Color.WHITE);
			int lineY = scaledViewSize.height / 2;
			for (int i = 0; i < lines.length; ++i) {
				String line = lines[i];
				g.setFont(i == 0 ? font.deriveFont(2f * font.getSize()) : font);
				int lineWidth = g.getFontMetrics().stringWidth(line);
				g.drawString(line, (scaledViewSize.width - lineWidth) / 2, lineY);
				lineY += 2 * fontSize;
			}
		}
		g.dispose();
	}

	private void drawMutedIcon(Graphics2D g) {
		if (app.soundManager().isMuted()) {
			int iconSize = MUTED_ICON.getWidth(null);
			Dimension viewSize = scaledViewSize();
			g.drawImage(MUTED_ICON, viewSize.width - iconSize - 2, viewSize.height - iconSize - 2, iconSize, iconSize, null);
		}
	}

	private float scaling() {
		float scale = app.settings().scale;
		if (inFullScreenMode()) {
			scale = 1f * fullScreenWindow.getHeight() / viewHeight;
			if (viewWidth * scale > fullScreenWindow.getWidth()) {
				scale = 1f * fullScreenWindow.getWidth() / viewWidth;
			}
		}
		return Math.max(scale, 0.01f); // avoid 'Determinant is 0' error
	}

	private Dimension scaledViewSize() {
		float scaling = scaling();
		return new Dimension(round(scaling * viewWidth), round(scaling * viewHeight));
	}

	private String displayModeText(DisplayMode mode) {
		return format("%d x %d, depth: %d, refresh rate: %s", mode.getWidth(), mode.getHeight(), mode.getBitDepth(),
				mode.getRefreshRate() == 0 ? "unknown" : mode.getRefreshRate() + " Hz");
	}

	private String titleText() {
		return app.settings().titleExtended ? format("%s [%d Hz %dx%d px scaled by %.2f]", app.settings().title,
				app.clock().getFrameRate(), viewWidth, viewHeight, app.settings().scale) : app.settings().title;
	}
}