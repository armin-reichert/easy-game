package de.amr.easy.game.controls;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import de.amr.easy.game.Application;
import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.View;

/**
 * This view displays general application info.
 * 
 * @author Armin Reichert
 */
public class ApplicationInfoView implements Controller, View {

	private final Application app;
	private TextArea text;

	public ApplicationInfoView(Application app) {
		this.app = app;
	}

	@Override
	public void init() {
		text = TextArea.create().text(createText()).speedY(-0.5f).color(Color.WHITE)
				.font(new Font(Font.SANS_SERIF, Font.BOLD, 14)).build();
		text.centerHorizontally(app.settings.width);
		text.tf.setY(app.settings.height);
		text.start();
	}

	@Override
	public void update() {
		text.update();
		if (text.tf.getY() + text.tf.getHeight() < 0) {
			text.tf.setY(app.settings.height);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(app.settings.bgColor);
		g.fillRect(0, 0, app.settings.width, app.settings.height);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		text.draw(g);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}

	private String createText() {
		StringBuilder sb = new StringBuilder();
		sb.append(app.getClass().getSimpleName()).append("\n\n");
		sb.append("title = " + app.settings.title).append("\n");
		sb.append("width = " + app.settings.width).append("\n");
		sb.append("height = " + app.settings.height).append("\n");
		sb.append("scale = " + app.settings.scale).append("\n");
		sb.append("fullScreenMode = " + app.settings.fullScreenMode).append("\n");
		sb.append("bgColor = " + app.settings.bgColor).append("\n");
		app.settings.keys().forEach(key -> {
			sb.append(key + " = " + app.settings.getAsString(key)).append("\n");
		});
		return sb.toString();
	}
}