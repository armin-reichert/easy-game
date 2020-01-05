package de.amr.easy.game.ui;

import static de.amr.easy.game.Application.app;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.ui.widgets.TextWidget;
import de.amr.easy.game.view.View;

/**
 * This view displays general application info.
 * 
 * @author Armin Reichert
 */
public class AppInfoView implements Lifecycle, View {

	private final int width;
	private final int height;
	private TextWidget text;

	public AppInfoView(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void init() {
		text = TextWidget.create().text(infoText()).color(Color.WHITE).font(new Font(Font.SANS_SERIF, Font.BOLD, 14))
				.build();
		text.tf.centerX(width);
		text.tf.setY(height);
		text.tf.setVelocityY(-1.0f);
		text.start();
	}

	@Override
	public void update() {
		text.setText(infoText());
		text.update();
		if (text.tf.getY() + text.tf.getHeight() < 0) {
			text.tf.setY(height);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.GREEN);
		g.setStroke(new BasicStroke(2f));
		g.drawRect(0, 0, width, height);
		text.draw(g);
	}

	private String infoText() {
		StringBuilder sb = new StringBuilder();
		sb.append("YOU SHOULD NOT SEE THIS! (No main controller set?)\n\n\n");
		sb.append(app().getClass().getSimpleName()).append("\n\n");
		sb.append("clock frequency = " + app().clock().getFrequency()).append(" Hz\n\n");
		sb.append("title = " + app().settings().title).append("\n");
		sb.append("width = " + app().settings().width).append("\n");
		sb.append("height = " + app().settings().height).append("\n");
		sb.append("scale = " + app().settings().scale).append("\n");
		sb.append("fullScreenMode = ");
		DisplayMode mode = app().settings().fullScreenMode;
		if (mode != null) {
			sb.append(String.format("%d x %d %d Bit", mode.getWidth(), mode.getHeight(), mode.getBitDepth())).append("\n");
		}
		sb.append("bgColor = " + app().settings().bgColor).append("\n");
		app().settings().keys().forEach(key -> {
			sb.append(key + " = " + app().settings().getAsString(key)).append("\n");
		});
		sb.append("\n\nAvailable display modes:\n\n");
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		for (DisplayMode dm : device.getDisplayModes()) {
			sb.append(
					String.format("%dx%d %d bit %d Hz\n", dm.getWidth(), dm.getHeight(), dm.getBitDepth(), dm.getRefreshRate()));
		}
		return sb.toString();
	}
}