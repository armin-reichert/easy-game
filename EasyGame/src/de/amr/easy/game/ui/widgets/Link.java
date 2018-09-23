package de.amr.easy.game.ui.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import de.amr.easy.game.Application;
import de.amr.easy.game.entity.GameEntity;
import de.amr.easy.game.input.Mouse;
import de.amr.easy.game.view.View;

public class Link extends GameEntity implements View {

	public static class Builder {

		private final Link product;

		public Builder() {
			product = new Link();
		}

		public Builder text(String text) {
			product.text = text;
			return this;
		}

		public Builder font(Font font) {
			product.font = font;
			return this;
		}

		public Builder color(Color color) {
			product.color = color;
			return this;
		}

		public Builder url(String spec) {
			try {
				product.url = new URL(spec);
			} catch (MalformedURLException e) {
				Application.LOGGER.info("Invalid link URL: " + spec);
			}
			return this;
		}

		public Link build() {
			product.computeSize();
			return product;
		}
	}

	public static Builder create() {
		return new Builder();
	}

	private String text;
	private Font font;
	private Color color;
	private URL url;

	private Link() {
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		if (!this.text.equals(text)) {
			this.text = text;
			computeSize();
		}
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		Objects.requireNonNull(font);
		if (!this.font.equals(font)) {
			this.font = font;
			computeSize();
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if (this.color != color) {
			this.color = color;
			computeSize();
		}
	}

	public void setURL(String spec) {
		try {
			url = new URL(spec);
		} catch (MalformedURLException e) {
			Application.LOGGER.info("Invalid link URL: " + spec);
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		if (Mouse.clicked()) {
			int x = Mouse.getX(), y = Mouse.getY();
			if (getCollisionBox().contains(new Point2D.Float(x, y))) {
				openURL();
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.translate(tf.getX(), tf.getY());
		g.setColor(color);
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.drawString(text, 0, g.getFontMetrics().getAscent());
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.translate(-tf.getX(), -tf.getY());
	}

	private void computeSize() {
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		tf.setWidth(fm.stringWidth(text));
		tf.setHeight(fm.getHeight());
		g.dispose();
	}

	private void openURL() {
		// TODO only works under Windows
		try {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}