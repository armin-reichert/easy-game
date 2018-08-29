package de.amr.easy.game.controls;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import de.amr.easy.game.entity.GameEntityUsingSprites;
import de.amr.easy.game.sprite.Sprite;

/**
 * A multi-line text that can be scrolled over the screen.
 * 
 * @author Armin Reichert
 */
public class TextArea extends GameEntityUsingSprites {

	private boolean visible;
	private String[] lines;
	private float lineSpacing;
	private Color color;
	private Font font;

	public TextArea(String text) {
		visible = true;
		setText(text);
		setScrollSpeed(0);
		setFont(new Font("Sans", Font.PLAIN, 40));
		setColor(Color.BLUE);
		setLineSpacing(1.5f);
		updateSprite();
	}

	public TextArea() {
		this("");
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
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

	public void setScrollSpeed(float speed) {
		tf.setVelocityY(speed);
	}

	public void setFont(Font font) {
		this.font = font;
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

	@Override
	public void update() {
		tf.move();
	}

	private void updateSprite() {
		// helper image for computing bounds
		BufferedImage helperImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D pen = helperImage.createGraphics();
		pen.setFont(font);
		pen.setColor(color);
		pen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		double textWidth = 1;
		double textHeight = 1;
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i];
			Rectangle2D lineBounds = pen.getFontMetrics().getStringBounds(line, pen);
			textHeight += lineBounds.getHeight();
			if (i < lines.length - 1) {
				textHeight += lineSpacing;
			}
			textWidth = Math.max(textWidth, lineBounds.getWidth());
		}

		// correctly sized image which will be used as sprite
		BufferedImage image = new BufferedImage((int) Math.ceil(textWidth), (int) Math.ceil(textHeight),
				BufferedImage.TYPE_INT_ARGB);
		pen = image.createGraphics();
		pen.setFont(font);
		pen.setColor(color);
		pen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics fm = pen.getFontMetrics();
		float y = 0;
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i];
			Rectangle2D lineBounds = fm.getStringBounds(line, pen);
			pen.drawString(line, (float) (textWidth - lineBounds.getWidth()) / 2, y + fm.getMaxAscent());
			y += lineBounds.getHeight();
			if (i < lines.length - 1) {
				y += lineSpacing;
			}
		}
		addSprite("s_image", new Sprite(image));
		setCurrentSprite("s_image");
		tf.setWidth((int) textWidth);
		tf.setHeight((int) textHeight);
	}

	@Override
	public void draw(Graphics2D g) {
		if (visible) {
			super.draw(g);
		}
	}
}