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

import de.amr.easy.game.entity.SpriteEntity;
import de.amr.easy.game.ui.sprites.AnimationType;
import de.amr.easy.game.ui.sprites.Sprite;
import de.amr.easy.game.view.AnimationController;

/**
 * A multi-line text that can be moved over the screen.
 * 
 * @author Armin Reichert
 */
public class TextWidget extends SpriteEntity implements AnimationController {

	public static class Builder {

		private final TextWidget widget;

		public Builder() {
			widget = new TextWidget();
		}

		public Builder visible(boolean visible) {
			widget.setVisible(visible);
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

	private TextWidget() {
//		debug_draw = true;
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

		tf.setWidth(0);
		tf.setHeight(0);
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setFont(font);
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i].replace(" ", spaces);
			Rectangle2D lineBounds = g.getFontMetrics().getStringBounds(line, g);
			tf.setHeight(tf.getHeight() + (int)Math.ceil(lineBounds.getHeight()));
			if (i < lines.length - 1) {
				tf.setHeight(tf.getHeight() + (int) Math.ceil(lineSpacing));
			}
			tf.setWidth(Math.max(tf.getWidth(), (int) Math.ceil(lineBounds.getWidth())));
		}

		// create correctly sized image
		tf.setWidth(Math.max(tf.getWidth(), 1));
		tf.setHeight(Math.max(tf.getHeight(), 1));
		image = new BufferedImage(tf.getWidth(), tf.getHeight(), BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		if (background != null) {
			g.setColor(background);
			g.fillRect(0, 0, tf.getWidth(), tf.getHeight());
		}
		g.setFont(font);
		g.setColor(color);
		FontMetrics fm = g.getFontMetrics();
		float y = 0;
		for (int i = 0; i < lines.length; ++i) {
			String text = lines[i];
			text = text.replace(" ", spaces);
			Rectangle2D lineBounds = fm.getStringBounds(text, g);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawString(text, (float) (tf.getWidth() - lineBounds.getWidth()) / 2, y + fm.getMaxAscent());
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			y += lineBounds.getHeight();
			if (i < lines.length - 1) {
				y += lineSpacing;
			}
		}

		// store sprite and set collision box
		sprites.set("s_text", Sprite.of(image, null).animate(AnimationType.BACK_AND_FORTH, blinkTimeMillis / 2));
		sprites.select("s_text");
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
		sprites.current().animate(AnimationType.BACK_AND_FORTH, millis);
	}

	public void setSpaceExpansion(int spaceExpansion) {
		this.spaceExpansion = spaceExpansion;
		updateSprite();
	}

	public void setCompletion(BooleanSupplier completion) {
		this.fnCompleted = completion;
	}

	@Override
	public void update() {
		if (moving) {
			tf.move();
			if (isAnimationCompleted()) {
				stopAnimation();
			}
		}
	}

	@Override
	public boolean isAnimationCompleted() {
		return fnCompleted.getAsBoolean();
	}

	@Override
	public void startAnimation() {
		moving = true;
	}

	@Override
	public void stopAnimation() {
		moving = false;
	}
}