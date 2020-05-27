package de.amr.easy.game.ui.widgets;

import static de.amr.easy.game.Application.app;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import de.amr.easy.game.entity.Entity;
import de.amr.easy.game.view.Pen;
import de.amr.easy.game.view.View;

/**
 * Displays the current frame rate.
 * 
 * @author Armin Reichert
 */
public class FrameRateWidget extends Entity implements View {

	public Color color = new Color(200, 200, 200);
	public Font font = new Font(Font.MONOSPACED, Font.BOLD, 8);

	/**
	 * Display format, first argument is the current, second argument the target framerate of the
	 * application.
	 */
	public String format = ("%d|%dfps");

	@Override
	public void draw(Graphics2D g) {
		if (visible) {
			try (Pen pen = new Pen(g)) {
				pen.color(color);
				pen.font(font);
				pen.smooth(() -> {
					String text = String.format(format, app().clock().getFrameRate(), app().clock().getTargetFramerate());
					pen.drawString(text, tf.x, tf.y);
				});
			}
		}
	}
}