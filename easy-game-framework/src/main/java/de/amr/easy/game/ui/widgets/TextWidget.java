package de.amr.easy.game.ui.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import de.amr.easy.game.entity.GameObject;
import de.amr.easy.game.ui.sprites.AnimationType;
import de.amr.easy.game.ui.sprites.Sprite;

/**
 * A multi-line text that can be moved over the screen.
 * 
 * @author Armin Reichert
 */
public class TextWidget extends GameObject {

	public static class Builder {

		private final TextWidget widget;

		public Builder() {
			widget = new TextWidget();
		}

		public Builder visible(boolean visible) {
			widget.visible = visible;
			return this;
		}

		public Builder text(String text) {
			Objects.requireNonNull(text);
			widget.lines = text.split("\n");
			return this;
		}

		public Builder blinkTimeMillis(int millis) {
			widget.blinkTimeMillis = millis;
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
			Objects.requireNonNull(font);
			widget.font = font;
			return this;
		}

		public Builder spaceExpansion(int factor) {
			if (factor < 1) {
				throw new IllegalArgumentException("Space factor must be greater or equal one");
			}
			widget.spaceExpansion = factor;
			return this;
		}

		public TextWidget build() {
			widget.updateSprite();
			return widget;
		}
	}

	public static Builder create() {
		return new Builder();
	}

	private String[] lines;
	private float lineSpacing;
	private Color background;
	private Color color;
	private Font font;
	private BooleanSupplier fnCompleted;
	private int blinkTimeMillis;
	private int spaceExpansion;
	private boolean moving;
	private Sprite sprite;

	private TextWidget() {
		// debug_draw = true;
		moving = false;
		fnCompleted = () -> false;
		lines = new String[0];
		font = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
		background = null; // transparent
		color = Color.BLUE;
		lineSpacing = 1.5f;
		blinkTimeMillis = Integer.MAX_VALUE;
		updateSprite();
	}

	private void updateSprite() {
		String spaces = " ";
		for (int j = 1; j < spaceExpansion; ++j) {
			spaces += " ";
		}

		tf.width = (0);
		tf.height = (0);
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setFont(font);
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i].replace(" ", spaces);
			Rectangle2D lineBounds = g.getFontMetrics().getStringBounds(line, g);
			tf.height = (tf.height + (int) Math.ceil(lineBounds.getHeight()));
			if (i < lines.length - 1) {
				tf.height = (tf.height + (int) Math.ceil(lineSpacing));
			}
			tf.width = (Math.max(tf.width, (int) Math.ceil(lineBounds.getWidth())));
		}

		// create correctly sized image
		tf.width = (Math.max(tf.width, 1));
		tf.height = (Math.max(tf.height, 1));
		image = new BufferedImage(tf.width, tf.height, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		if (background != null) {
			g.setColor(background);
			g.fillRect(0, 0, tf.width, tf.height);
		}
		g.setFont(font);
		g.setColor(color);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		FontMetrics fm = g.getFontMetrics();
		float y = 0;
		for (int i = 0; i < lines.length; ++i) {
			String text = lines[i];
			text = text.replace(" ", spaces);
			Rectangle2D lineBounds = fm.getStringBounds(text, g);
			g.drawString(text, (float) (tf.width - lineBounds.getWidth()) / 2, y + fm.getMaxAscent());
			y += lineBounds.getHeight();
			if (i < lines.length - 1) {
				y += lineSpacing;
			}
		}
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		// store sprite and set collision box
		sprite = Sprite.of(image, null).animate(AnimationType.FORWARD_BACKWARDS, blinkTimeMillis / 2);
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

	public void setBlinkTime(int millis) {
		sprite.animate(AnimationType.FORWARD_BACKWARDS, millis);
	}

	public void setSpaceExpansion(int spaceExpansion) {
		this.spaceExpansion = spaceExpansion;
		updateSprite();
	}

	public void setCompletion(BooleanSupplier completion) {
		this.fnCompleted = completion;
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		if (moving) {
			tf.move();
			if (isComplete()) {
				stop();
			}
		}
	}

	@Override
	public boolean isComplete() {
		return fnCompleted.getAsBoolean();
	}

	@Override
	public void start() {
		moving = true;
	}

	@Override
	public void stop() {
		moving = false;
	}

	@Override
	public void draw(Graphics2D g) {
		if (visible) {
			g = (Graphics2D) g.create();
			g.translate(tf.x, tf.y);
			g.rotate(tf.rotation);
			sprite.draw(g);
			g.dispose();
		}
	}
}