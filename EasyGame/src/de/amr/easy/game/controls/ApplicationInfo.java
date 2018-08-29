package de.amr.easy.game.controls;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import de.amr.easy.game.Application;
import de.amr.easy.game.entity.GameEntity;
import de.amr.easy.game.view.ViewController;

/**
 * This view displays general application info.
 * 
 * @author Armin Reichert
 */
public class ApplicationInfo extends GameEntity implements ViewController {

	private final Application app;
	private final TextArea text;

	public ApplicationInfo(Application app) {
		this.app = app;
		tf.setWidth(app.settings.width);
		tf.setHeight(app.settings.height);
		text = new TextArea();
	}

	@Override
	public void init() {
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
		text.setText(sb.toString());
		text.tf().setY(app.settings.height);
		text.setScrollSpeed(-0.5f);
		text.setColor(Color.WHITE);
		text.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
	}

	@Override
	public void update() {
		text.update();
		if (text.tf().getY() < -text.tf().getHeight()) {
			text.tf().setY(app.settings.height);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(app.settings.bgColor);
		g.fillRect(0, 0, tf.getWidth(), tf.getHeight());
		text.centerHorizontally(tf.getWidth());
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		text.draw(g);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
}