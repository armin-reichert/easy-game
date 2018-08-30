package de.amr.easy.game.controls;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import de.amr.easy.game.entity.GameEntityUsingSprites;
import de.amr.easy.game.sprite.AnimationType;
import de.amr.easy.game.sprite.Sprite;

/**
 * Animation showing blinking text.
 * 
 * @author Armin Reichert
 */
public class BlinkingText extends GameEntityUsingSprites {

	private String text;
	private Font font;
	private Color background;
	private Color color;
	private int spaceExpansion;

	public BlinkingText() {
		this.text = "";
		this.font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
		this.background = Color.BLACK;
		this.color = Color.YELLOW;
		this.spaceExpansion = 1;
		createSprite();
	}

	public BlinkingText set(String text, Font font, Color background, Color color) {
		Objects.requireNonNull(text);
		Objects.requireNonNull(font);
		Objects.requireNonNull(background);
		Objects.requireNonNull(color);
		this.text = text;
		this.font = font;
		this.background = background;
		this.color = color;
		createSprite();
		return this;
	}

	public void setText(String text) {
		Objects.requireNonNull(text);
		this.text = text;
		createSprite();
	}

	public void setFont(Font font) {
		Objects.requireNonNull(font);
		this.font = font;
		createSprite();
	}

	public void setColor(Color color) {
		Objects.requireNonNull(color);
		this.color = color;
		createSprite();
	}

	public void setBackground(Color background) {
		Objects.requireNonNull(background);
		this.background = background;
		createSprite();
	}

	public void setBlinkTime(int millis) {
		currentSprite().animate(AnimationType.BACK_AND_FORTH, millis);
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
		setSprite("s_text", Sprite.of(image, null).animate(AnimationType.BACK_AND_FORTH, 500));
		setCurrentSprite("s_text");
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(background);
		g.fillRect(0, 0, currentSprite().getWidth(), currentSprite().getHeight());
		super.draw(g);
	}
}