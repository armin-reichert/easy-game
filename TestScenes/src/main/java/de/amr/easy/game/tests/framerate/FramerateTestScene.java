package de.amr.easy.game.tests.framerate;

import static de.amr.easy.game.Application.app;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.sun.glass.events.KeyEvent;

import de.amr.easy.game.input.Keyboard;
import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.View;

public class FramerateTestScene implements View, Controller {

	private Image bgImg;
	private int sampleIndex;
	private int[] fpsValues;
	private int stepX = 20;
	private int sampleSteps;
	private boolean consoleLog;

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
		consoleLog = false;
		app().clock.setLoggingEnabled(false);
	}

	@Override
	public void update() {
		if (Keyboard.keyPressedOnce(KeyEvent.VK_L)) {
			consoleLog = !consoleLog;
			app().clock.setLoggingEnabled(consoleLog);
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
			g.setColor(Color.GREEN);
			g.drawLine(x1, y1, x2, y2);
			x1 = x2;
			y1 = y2;
		}
		g.scale(1, -1);
		g.translate(0, -getHeight());
	}
}
