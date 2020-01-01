package de.amr.easy.game.tests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import de.amr.easy.game.GenericApplication;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.input.Keyboard;
import de.amr.easy.game.input.Mouse;
import de.amr.easy.game.view.View;

public class DrawingTestApp extends GenericApplication {

	public static void main(String[] args) {
		launch(new DrawingTestApp(), args);
	}

	public DrawingTestApp() {
		settings.title = "Drawing Test";
	}

	@Override
	public void init() {
		setController(new DrawingTestScene(this));
	}

}

class DrawingTestScene implements View, Lifecycle {

	private DrawingTestApp app;
	private BufferedImage drawArea;
	private Graphics2D pen;
	private int penWidth;
	private boolean randomColor;

	public DrawingTestScene(DrawingTestApp app) {
		this.app = app;
	}

	public int getWidth() {
		return app.settings.width;
	}

	public int getHeight() {
		return app.settings.height;
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(drawArea, 0, 0, null);
		g.setColor(Color.GREEN);
		g.setStroke(new BasicStroke(6));
		g.drawRect(0, 0, getWidth(), getHeight());
		g.drawString("Pen width: " + penWidth + " (Press +/- to change)", 20, 20);
	}

	@Override
	public void init() {
		drawArea = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		pen = drawArea.createGraphics();
		penWidth = 4;
	}

	@Override
	public void update() {
		if (Keyboard.keyPressedOnce(KeyEvent.VK_C)) {
			pen.setColor(Color.BLACK);
			pen.clearRect(0, 0, getWidth(), getHeight());
		}
		if (Keyboard.keyPressedOnce(KeyEvent.VK_PLUS)) {
			if (penWidth < 60)
				penWidth *= 2;
		}
		if (Keyboard.keyPressedOnce(KeyEvent.VK_MINUS)) {
			if (penWidth > 4)
				penWidth /= 2;
		}
		if (Keyboard.keyPressedOnce(KeyEvent.VK_R)) {
			randomColor = !randomColor;
		}
		if (Mouse.pressed() || Mouse.dragged()) {
			pen.setColor(randomColor ? randomColor() : Color.RED);
			pen.fillOval(Mouse.getX(), Mouse.getY(), penWidth, penWidth);
		}
	}

	private Color randomColor() {
		Random rand = new Random();
		return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
	}
}