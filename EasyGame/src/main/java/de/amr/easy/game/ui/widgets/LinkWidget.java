package de.amr.easy.game.ui.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import de.amr.easy.game.Application;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.entity.Entity;
import de.amr.easy.game.input.Mouse;
import de.amr.easy.game.view.View;

/**
 * A link widget.
 * 
 * <p>
 * TODO: Opening a link works only under Windows.
 * 
 * @author Armin Reichert
 */
public class LinkWidget extends Entity implements Lifecycle, View {

	public static class Builder {

		private final LinkWidget link;

		public Builder() {
			link = new LinkWidget();
		}

		public Builder text(String text) {
			Objects.requireNonNull(text);
			link.text = text;
			return this;
		}

		public Builder font(Font font) {
			Objects.requireNonNull(font);
			link.font = underlined(font);
			return this;
		}

		public Builder color(Color color) {
			Objects.requireNonNull(color);
			link.color = color;
			return this;
		}

		public Builder url(String spec) {
			Objects.requireNonNull(spec);
			try {
				link.url = new URL(spec);
			} catch (MalformedURLException e) {
				Application.LOGGER.info("Invalid link URL: " + spec);
			}
			return this;
		}

		public LinkWidget build() {
			link.computeTextBounds();
			return link;
		}
	}

	public static Builder create() {
		return new Builder();
	}

	private static Font underlined(Font font) {
		Map<TextAttribute, Integer> attributes = Collections.singletonMap(TextAttribute.UNDERLINE,
				TextAttribute.UNDERLINE_ON);
		return font.deriveFont(attributes);
	}

	private String text;
	private Font font;
	private Color color;
	private URL url;

	private LinkWidget() {
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		Objects.requireNonNull(text);
		if (!this.text.equals(text)) {
			this.text = text;
			computeTextBounds();
		}
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		Objects.requireNonNull(font);
		if (!this.font.equals(font)) {
			this.font = underlined(font);
			computeTextBounds();
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		Objects.requireNonNull(color);
		if (this.color != color) {
			this.color = color;
			computeTextBounds();
		}
	}

	public void setURL(String spec) {
		Objects.requireNonNull(spec);
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
		if (Mouse.clicked() && getCollisionBox().contains(new Point2D.Float(Mouse.getX(), Mouse.getY()))) {
			openURL();
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.translate(tf.getX(), tf.getY());
		g.setColor(color);
		Map<TextAttribute, Integer> attributes = Collections.singletonMap(TextAttribute.UNDERLINE,
				TextAttribute.UNDERLINE_ON);
		g.setFont(font.deriveFont(attributes));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.drawString(text, 0, g.getFontMetrics().getAscent());
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.translate(-tf.getX(), -tf.getY());
	}

	private void computeTextBounds() {
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setFont(font);
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);
		tf.setWidth((int) bounds.getWidth());
		tf.setHeight((int) bounds.getHeight());
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