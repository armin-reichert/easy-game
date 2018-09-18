package de.amr.easy.game.tests.framerate;

import static de.amr.easy.game.Application.app;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.View;

public class FramerateTestScene implements View, Controller {

	private Image bgImg;
	private int sampleIndex;
	private int[] fpsValues;
	private int stepX = 20;
	private int sampleSteps;

	public int getWidth() {
		return 1000;
	}

	public int getHeight() {
		return 400;
	}

	@Override
	public void init() {
		fpsValues = new int[getWidth()];
		bgImg = createBgImage();
	}

	@Override
	public void update() {
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
