package de.amr.easy.game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import de.amr.easy.game.Application;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.view.View;

/**
 * Displays the framerate history in a diagram.
 * 
 * @author Armin Reichert
 */
public class FramerateHistoryView extends JComponent implements Lifecycle, View {

	private Application app;
	private BufferedImage bgImg;
	private int fpsIndex;
	private int[] fpsValues;
	private int stepX = 20;
	private int maxFps = 120;
	private int xOffset = 30;
	private int vmargin = 20;

	public FramerateHistoryView(int width, int height) {
		setSize(width, height);
		updateData(width / stepX);
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				updateData(e.getComponent().getWidth() / stepX);
				bgImg = null;
			}
		});
	}

	private void updateData(int numValues) {
		fpsValues = new int[numValues];
		fpsIndex = 0;
	}

	private void updateBgImage(int w, int h) {
		bgImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bgImg.createGraphics();
		g.translate(0, -vmargin);
		int hh = h - 2 * vmargin;
		float yScale = 1f * hh / maxFps;
		g.setColor(Color.LIGHT_GRAY);
		g.setStroke(new BasicStroke(0.1f));
		for (int f = 0; f <= maxFps; f += 20) {
			int y = h - Math.round(f * yScale);
			g.drawLine(xOffset, y, w, y);
			g.drawString(String.valueOf(f), 0, y);
		}
		int f = app != null ? app.clock().getTargetFramerate() : 60;
		g.setColor(Color.YELLOW);
		int y = h - Math.round(f * yScale);
		g.drawLine(xOffset, y, w, y);
	}

	private void drawValues(Graphics2D g, int w, int h) {
		float yScale = 1f * h / maxFps;
		for (int j = 0; j < fpsIndex - 1; ++j) {
			int x1 = xOffset + stepX * j;
			int y1 = h + vmargin - Math.round(fpsValues[j] * yScale);
			int x2 = xOffset + stepX * (j + 1);
			int y2 = h + vmargin - Math.round(fpsValues[j + 1] * yScale);
			Color color = Color.GREEN;
			if (j > 0) {
				int fps = app != null ? app.clock().getTargetFramerate() : 60;
				int deviation = fpsValues[j] - fps;
				int percent = (100 * deviation) / fps;
				if (Math.abs(percent) > 3) {
					color = Color.RED;
				}
			}
			g.setColor(color);
			g.drawLine(x1, y1, x2, y2);
			x1 = x2;
			y1 = y2;
		}
	}

	@Override
	public void draw(Graphics2D g) {
		if (bgImg == null) {
			updateBgImage(getWidth(), getHeight());
		}
		g.drawImage(bgImg, 0, 0, null);
		drawValues(g, getWidth(), getHeight() - 2 * vmargin);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		draw(g2);
		g2.dispose();
	}

	public void setApp(Application app) {
		if (app != this.app) {
			this.app = app;
			app.clock().addFrequencyChangeListener(e -> {
				updateData(getWidth() / stepX);
				bgImg = null;
			});
			Thread t = new Thread(() -> {
				while (true) {
					update();
					repaint();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}, "FramerateHistoryViewUpdate");
			t.start();
		}
	}

	@Override
	public void update() {
		if (fpsIndex == fpsValues.length) {
			fpsIndex = 0;
		}
		fpsValues[fpsIndex++] = app.clock().getFrameRate();
	}

	@Override
	public void init() {
	}
}