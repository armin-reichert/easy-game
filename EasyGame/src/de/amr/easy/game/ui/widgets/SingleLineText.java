package de.amr.easy.game.ui.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import de.amr.easy.game.entity.SpriteBasedGameEntity;
import de.amr.easy.game.ui.sprites.AnimationType;
import de.amr.easy.game.ui.sprites.Sprite;

/**
 * A single-line text widget with the ability to set background color, text color, font and blink
 * time.
 * 
 * @author Armin Reichert
 */
public class SingleLineText extends SpriteBasedGameEntity {

	public static class Builder {

		private final SingleLineText widget;

		public Builder() {
			widget = new SingleLineText();
		}

		public Builder text(String text) {
			Objects.requireNonNull(text);
			widget.text = text;
			return this;
		}

		public Builder blinkTimeMillis(int millis) {
			widget.blinkTimeMillis = millis;
			return this;
		}

		public Builder font(Font font) {
			Objects.requireNonNull(font);
			widget.font = font;
			return this;
		}

		public Builder color(Color color) {
			Objects.requireNonNull(color);
			widget.color = color;
			return this;
		}

		public Builder background(Color color) {
			Objects.requireNonNull(color);
			widget.background = color;
			return this;
		}

		public Builder spaceExpansion(int factor) {
			if (factor < 1) {
				throw new IllegalArgumentException("Space factor must be greater or equal one");
			}
			widget.spaceExpansion = factor;
			return this;
		}

		public SingleLineText build() {
			widget.createSprite();
			return widget;
		}
	}

	public static Builder create() {
		return new Builder();
	}

	private String text;
	private int blinkTimeMillis;
	private Font font;
	private Color background;
	private Color color;
	private int spaceExpansion;

	private SingleLineText() {
		text = "";
		blinkTimeMillis = Integer.MAX_VALUE;
		font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
		background = Color.BLACK;
		color = Color.YELLOW;
		spaceExpansion = 1;
		createSprite();
	}

	public void setText(String text) {
		Objects.requireNonNull(text);
		if (!this.text.equals(text)) {
			this.text = text;
			createSprite();
		}
	}

	public void setBlinkTimeMillis(int millis) {
		this.blinkTimeMillis = millis;
		sprites.current().animate(AnimationType.BACK_AND_FORTH, blinkTimeMillis);
	}

	public void setFont(Font font) {
		Objects.requireNonNull(font);
		if (!this.font.equals(font)) {
			this.font = font;
			createSprite();
		}
	}

	public void setColor(Color color) {
		Objects.requireNonNull(color);
		if (!this.color.equals(color)) {
			this.color = color;
			createSprite();
		}
	}

	public void setBackground(Color background) {
		Objects.requireNonNull(background);
		if (!this.background.equals(background)) {
			this.background = background;
			createSprite();
		}
	}

	public void setBlinkTime(int millis) {
		sprites.current().animate(AnimationType.BACK_AND_FORTH, millis);
	}

	public void setSpaceExpansion(int spaceExpansion) {
		this.spaceExpansion = spaceExpansion;
		createSprite();
	}

	private void createSprite() {
		// compute image bounds
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setFont(font);
		String patchedText = text;
		String space = " ";
		for (int i = 1; i < spaceExpansion; ++i) {
			space = space + " ";
		}
		patchedText = text.replace(" ", space);
		int width = Math.max(1, g.getFontMetrics().stringWidth(patchedText));
		int height = font.getSize();
		tf.setWidth(width);
		tf.setHeight(height);
		g.dispose();
		// create correctly sized image
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = image.createGraphics();
		g.setColor(background);
		g.fillRect(0, 0, width, height);
		g.setColor(color);
		g.setFont(font);
		g.drawString(patchedText, 0, height);
		g.dispose();
		sprites.set("s_text", Sprite.of(image, null).animate(AnimationType.BACK_AND_FORTH, blinkTimeMillis / 2));
		sprites.select("s_text");
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(background);
		g.fillRect(0, 0, sprites.current().getWidth(), sprites.current().getHeight());
		super.draw(g);
	}
}