package de.amr.easy.game.tests;

import static java.lang.String.format;

import java.awt.Color;
import java.awt.Graphics2D;

import de.amr.easy.game.Application;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.input.Mouse;
import de.amr.easy.game.ui.widgets.TextWidget;
import de.amr.easy.game.view.View;

public class MouseTestApp extends Application {

	public static void main(String[] args) {
		launch(new MouseTestApp(), args);
	}

	public MouseTestApp() {
		settings.title = "Mouse Test";
		settings.width = 800;
		settings.height = 600;
	}

	@Override
	public void init() {
		setController(new MouseTestScene(this));
	}
}

class MouseTestScene implements View, Lifecycle {

	private final MouseTestApp app;
	private TextWidget messageDisplay;

	public MouseTestScene(MouseTestApp app) {
		this.app = app;
	}

	public int getWidth() {
		return app.settings.width;
	}

	public int getHeight() {
		return app.settings.height;
	}

	@Override
	public void init() {
		messageDisplay = TextWidget.create().build();
	}

	@Override
	public void update() {

		if (Mouse.clicked() || Mouse.pressed() || Mouse.released() || Mouse.moved() || Mouse.dragged()) {
			messageDisplay.setText("");
		}
		if (Mouse.clicked()) {
			info(format("Mouse clicked at (%d, %d), %s button", Mouse.getX(), Mouse.getY(), whichMouseButton()));
		}
		if (Mouse.pressed()) {
			info(format("Mouse pressed at (%d, %d), %s button", Mouse.getX(), Mouse.getY(), whichMouseButton()));
		}
		if (Mouse.released()) {
			info(format("Mouse released at (%d, %d), %s button", Mouse.getX(), Mouse.getY(), whichMouseButton()));
		}
		if (Mouse.moved()) {
			info(format("Mouse moved to (%d, %d), %s button", Mouse.getX(), Mouse.getY(), whichMouseButton()));
		}
		if (Mouse.dragged()) {
			info(format("Mouse dragged to (%d, %d), %s button", Mouse.getX(), Mouse.getY(), whichMouseButton()));
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		messageDisplay.tf.center(getWidth(), getHeight());
		messageDisplay.draw(g);
		g.translate(Mouse.getX(), Mouse.getY());
		g.setColor(Color.BLACK);
		g.drawLine(-10, 0, 10, 0);
		g.drawLine(0, -10, 0, 10);
		g.translate(-Mouse.getX(), -Mouse.getY());
	}

	private void info(String text) {
		messageDisplay.setText(messageDisplay.getText() + "\n" + text);
	}

	private String whichMouseButton() {
		if (Mouse.isLeftButton())
			return "left";
		else if (Mouse.isMiddleButton())
			return "middle";
		else if (Mouse.isRightButton())
			return "right";
		else
			return "no";
	}
}
