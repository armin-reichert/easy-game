package de.amr.easy.game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import de.amr.easy.game.Application;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.view.View;

/**
 * Displays the framerate history in a diagram.
 * 
 * @author Armin Reichert
 */
public class FramerateHistoryView implements View, Lifecycle {

	private Application app;
	private int width;
	private int height;
	private Image bgImg;
	private int sampleIndex;
	private int[] fpsValues;
	private int stepX = 20;
	private int sampleSteps;
	private int maxFps = 120;

	public FramerateHistoryView(int width, int height) {
		setSize(width, height);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		fpsValues = new int[width];
		bgImg = createBgImage(width, height);
	}

	public void setApp(Application app) {
		if (app != this.app) {
			this.app = app;
			app.clock().addFrequencyChangeListener(e -> {
				sampleSteps = 0;
				bgImg = createBgImage(width, height);
			});
		}
	}

	@Override
	public void setVisible(boolean visible) {
	}

	@Override
	public boolean visible() {
		return true;
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		++sampleSteps;
		if (sampleSteps == app.clock().getFrequency()) {
			fpsValues[sampleIndex++] = app.clock().getFrameRate();
			if (sampleIndex * stepX >= width) {
				sampleIndex = 0;
			}
			sampleSteps = 0;
		}
	}

	private Image createBgImage(int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		int xOffset = 50;
		g.setColor(Color.LIGHT_GRAY);
		g.setStroke(new BasicStroke(0.1f));
		float yScale = 1f * height / maxFps;
		for (int f = 0; f <= maxFps; f += 20) {
			int y = height - Math.round(f * yScale);
			g.drawLine(xOffset, y, width, y);
			g.drawString(String.valueOf(f), 0, y);
		}
		int f = app != null ? app.clock().getFrequency() : 60;
		g.setColor(Color.YELLOW);
		int y = height - Math.round(f * yScale);
		g.drawLine(xOffset, y, width, y);
		return img;
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(bgImg, 0, 0, null);
		g.translate(0, height);
		g.scale(1, -1);
		int xOffset = 50;
		for (int j = 0; j < sampleIndex - 1; ++j) {
			int x1 = xOffset + stepX * j;
			int y1 = fpsValues[j];
			int x2 = xOffset + stepX * (j + 1);
			int y2 = fpsValues[j + 1];
			Color color = Color.GREEN;
			if (j > 0) {
				int fps = app != null ? app.clock().getFrequency() : 60;
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
		g.scale(1, -1);
		g.translate(0, -height);
	}
}