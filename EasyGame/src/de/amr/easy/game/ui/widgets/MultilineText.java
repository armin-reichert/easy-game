package de.amr.easy.game.ui.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;

import de.amr.easy.game.entity.SpriteBasedGameEntity;
import de.amr.easy.game.math.Vector2f;
import de.amr.easy.game.ui.sprites.Sprite;
import de.amr.easy.game.view.AnimationController;

/**
 * A multi-line text that can be scrolled over the screen.
 * 
 * @author Armin Reichert
 */
public class MultilineText extends SpriteBasedGameEntity implements AnimationController {

	public static class Builder {

		private final MultilineText widget;

		public Builder() {
			widget = new MultilineText();
		}

		public Builder visible(boolean visible) {
			widget.setVisible(visible);
			return this;
		}

		public Builder text(String text) {
			widget.lines = text.split("\n");
			return this;
		}

		public Builder lineSpacing(float value) {
			widget.lineSpacing = value;
			return this;
		}

		public Builder background(Color color) {
			widget.background = color;
			return this;
		}

		public Builder color(Color color) {
			widget.color = color;
			return this;
		}

		public Builder font(Font font) {
			widget.font = font;
			return this;
		}

		public Builder velocity(float vx, float vy) {
			widget.velocity = Vector2f.of(vx, vy);
			return this;
		}

		public MultilineText build() {
			widget.updateSprite();
			return widget;
		}
	}

	public static Builder create() {
		return new Builder();
	}

	private String[] lines;
	private BooleanSupplier completion;
	private float lineSpacing;
	private Color background;
	private Color color;
	private Font font;
	private Vector2f velocity;

	private MultilineText() {
		completion = () -> false;
		lines = new String[0];
		font = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
		background = null; // transparent
		color = Color.BLUE;
		lineSpacing = 1.5f;
		velocity = Vector2f.NULL;
	}

	public int getHeight() {
		return Math.round(lines.length * (font.getSize()) + (lines.length - 1) * lineSpacing);
	}

	public String getText() {
		return String.join("\n", lines);
	}

	public void setText(String text) {
		this.lines = text.split("\n");
		updateSprite();
	}

	public void setFont(Font font) {
		this.font = font;
		updateSprite();
	}

	public void setBackground(Color color) {
		this.background = color;
		updateSprite();
	}

	public void setColor(Color color) {
		this.color = color;
		updateSprite();
	}

	public void setLineSpacing(float lineSpacing) {
		this.lineSpacing = lineSpacing;
		updateSprite();
	}

	public void setCompletion(BooleanSupplier completion) {
		this.completion = completion;
	}

	@Override
	public void update() {
		tf.move();
		if (isCompleted()) {
			stop();
		}
	}

	@Override
	public boolean isCompleted() {
		return completion.getAsBoolean();
	}

	@Override
	public void start() {
		tf.setVelocity(velocity);
	}

	@Override
	public void stop() {
		tf.setVelocity(0, 0);
	}

	private void updateSprite() {
		// helper image for computing bounds
		BufferedImage helperImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = helperImage.createGraphics();
		g.setFont(font);
		g.setColor(color);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		double textWidth = 1;
		double textHeight = 1;
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i];
			Rectangle2D lineBounds = g.getFontMetrics().getStringBounds(line, g);
			textHeight += lineBounds.getHeight();
			if (i < lines.length - 1) {
				textHeight += lineSpacing;
			}
			textWidth = Math.max(textWidth, lineBounds.getWidth());
		}

		// correctly sized image which will be used as sprite
		BufferedImage image = new BufferedImage((int) Math.ceil(textWidth), (int) Math.ceil(textHeight),
				BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		if (background != null) {
			g.setColor(background);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
		}
		g.setFont(font);
		g.setColor(color);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics fm = g.getFontMetrics();
		float y = 0;
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i];
			Rectangle2D lineBounds = fm.getStringBounds(line, g);
			g.drawString(line, (float) (textWidth - lineBounds.getWidth()) / 2, y + fm.getMaxAscent());
			y += lineBounds.getHeight();
			if (i < lines.length - 1) {
				y += lineSpacing;
			}
		}

		// store sprite and set collision box
		sprites.set("s_image", Sprite.of(image));
		sprites.select("s_image");
		tf.setWidth((int) textWidth);
		tf.setHeight((int) textHeight);
	}
}