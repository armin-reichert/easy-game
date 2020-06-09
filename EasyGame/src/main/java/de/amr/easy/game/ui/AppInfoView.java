package de.amr.easy.game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import de.amr.easy.game.Application;
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
	private final Application app;
	private final TextWidget text;

	public AppInfoView(Application app, int width, int height) {
		this.app = app;
		this.width = width;
		this.height = height;
		text = TextWidget.create().text(buildInfoText()).color(Color.WHITE).font(new Font(Font.SANS_SERIF, Font.BOLD, 20))
				.build();
	}

	@Override
	public void init() {
		text.tf.centerX(width);
		text.tf.y = (height);
		text.tf.vy = -1.0f;
		text.start();
	}

	@Override
	public void update() {
		text.setText(buildInfoText());
		text.update();
		if (text.tf.y + text.tf.height < 0) {
			text.tf.y = (height);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(new Color(40, 4, 100));
		g.fillRect(0, 0, width, height);
		g.setColor(Color.GREEN);
		g.setStroke(new BasicStroke(2f));
		g.drawRect(0, 0, width, height);
		text.draw(g);
	}

	private String buildInfoText() {
		StringBuilder sb = new StringBuilder();
		if (app != null) {
			sb.append("YOU SHOULD NOT SEE THIS! (No main controller set?)\n\n\n");
			sb.append(app.getClass().getSimpleName()).append("\n\n");
			sb.append("clock frequency = " + app.clock().getTargetFramerate()).append(" Hz\n\n");
			sb.append("title = " + app.settings().title).append("\n");
			sb.append("width = " + app.settings().width).append("\n");
			sb.append("height = " + app.settings().height).append("\n");
			sb.append("scale = " + app.settings().scale).append("\n");
			sb.append("fullScreenMode = ");
			DisplayMode mode = app.settings().fullScreenMode;
			if (mode != null) {
				sb.append(String.format("%d x %d %d Bit", mode.getWidth(), mode.getHeight(), mode.getBitDepth())).append("\n");
			}
			app.settings().keys().forEach(key -> {
				sb.append(key + " = " + app.settings().getAsString(key)).append("\n");
			});
			sb.append("\n\nAvailable display modes:\n\n");
			GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			for (DisplayMode dm : device.getDisplayModes()) {
				sb.append(String.format("%dx%d %d bit %d Hz\n", dm.getWidth(), dm.getHeight(), dm.getBitDepth(),
						dm.getRefreshRate()));
			}
		}
		return sb.toString();
	}
}