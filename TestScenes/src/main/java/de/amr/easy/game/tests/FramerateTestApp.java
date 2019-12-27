package de.amr.easy.game.tests;

import static de.amr.easy.game.Application.app;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.amr.easy.game.Application;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.input.Keyboard;
import de.amr.easy.game.timing.Clock;
import de.amr.easy.game.view.View;

public class FramerateTestApp extends Application {

	public static void main(String[] args) {
		launch(new FramerateTestApp(), args);
	}

	public FramerateTestApp() {
		settings.title = "Game performance measurement";
		settings.titleExtended = true;
		settings.width = 1000;
		settings.height = 200;
	}

	@Override
	public void init() {
		clock.setFrequency(60);
		setController(new FramerateTestScene());
	}
}

class FramerateTestScene implements View, Lifecycle {

	private Image bgImg;
	private int sampleIndex;
	private int[] fpsValues;
	private int stepX = 20;
	private int sampleSteps;

	public int getWidth() {
		return app().settings.width;
	}

	public int getHeight() {
		return app().settings.height;
	}

	@Override
	public void init() {
		fpsValues = new int[getWidth()];
		bgImg = createBgImage();
		app().clock.addFrequencyChangeListener(e -> {
			sampleSteps = 0;
			bgImg = createBgImage();
		});
	}

	@Override
	public void update() {
		if (Keyboard.keyPressedOnce(KeyEvent.VK_L)) {
			Logger logger = Logger.getLogger(Clock.class.getName());
			logger.setLevel(logger.getLevel() == Level.INFO ? Level.OFF : Level.INFO);
			Application.LOGGER.info("Clock logging is now " + logger.getLevel());
		}
		++sampleSteps;
		if (sampleSteps == app().clock.getFrequency()) {
			fpsValues[sampleIndex++] = app().clock.getRenderRate();
			if (sampleIndex * stepX >= getWidth()) {
				sampleIndex = 0;
			}
			sampleSteps = 0;
		}
	}

	private Image createBgImage() {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		int xOffset = 50;
		g.setColor(Color.LIGHT_GRAY);
		for (int y = 0; y < getHeight(); y += 20) {
			g.drawLine(xOffset, getHeight() - y, getWidth(), getHeight() - y);
			g.drawString(String.valueOf(y), 0, getHeight() - y);
		}
		int freq = app().clock.getFrequency();
		g.setColor(Color.YELLOW);
		g.drawLine(xOffset, getHeight() - freq, getWidth(), getHeight() - freq);
		return img;
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(bgImg, 0, 0, null);
		g.translate(0, getHeight());
		g.scale(1, -1);
		int xOffset = 50;
		for (int j = 0; j < sampleIndex - 1; ++j) {
			int x1 = xOffset + stepX * j;
			int y1 = fpsValues[j];
			int x2 = xOffset + stepX * (j + 1);
			int y2 = fpsValues[j + 1];
			Color color = Color.GREEN;
			if (j > 0) {
				int fps = app().clock.getFrequency();
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
		g.translate(0, -getHeight());
	}
}
