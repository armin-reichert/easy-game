package de.amr.easy.game.ui.widgets;

import static de.amr.easy.game.ui.sprites.AnimationType.BACK_AND_FORTH;
import static java.lang.Math.round;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Objects;

import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.entity.Entity;
import de.amr.easy.game.math.Vector2f;
import de.amr.easy.game.ui.sprites.Sprite;
import de.amr.easy.game.view.View;

public class PumpingImageWidget extends Entity implements Lifecycle, View {

	public static class Builder {

		private final PumpingImageWidget product;

		private Builder() {
			product = new PumpingImageWidget();
		}

		public Builder image(Image image) {
			product.image = image;
			return this;
		}

		public Builder frameCount(int frameCount) {
			product.frameCount = frameCount;
			return this;
		}

		public Builder scale(int scale) {
			product.scale = scale;
			return this;
		}

		public Builder visible(boolean visible) {
			product.visible = visible;
			return this;
		}

		public Builder periodMillis(int millis) {
			product.periodMillis = millis;
			return this;
		}

		public PumpingImageWidget build() {
			Objects.requireNonNull(product.image);
			product.updateSprite();
			return product;
		}
	}

	public static Builder create() {
		return new Builder();
	}

	private Image image;
	private Sprite sprite;
	private int frameCount;
	private float scale;
	private int periodMillis;

	private PumpingImageWidget() {
		frameCount = 6;
		scale = 2;
		periodMillis = 1000;
	}

	public void setScale(float scale) {
		this.scale = scale;
		updateSprite();
	}

	private void updateSprite() {
		Image[] frames = new Image[frameCount];
		float delta = scale / frames.length;
		int height = image.getHeight(null);
		for (int i = 0; i < frames.length; ++i) {
			int frameHeight = round(height + i * delta * height);
			frames[i] = image.getScaledInstance(-1, frameHeight, BufferedImage.SCALE_FAST);
		}
		sprite = Sprite.of(frames);
		sprite.animate(BACK_AND_FORTH, periodMillis / frameCount);
		sprite.enableAnimation(true);
		tf.width = (sprite.getMaxWidth());
		tf.height = (sprite.getMaxHeight());
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public void draw(Graphics2D g) {
		if (visible) {
			g = (Graphics2D) g.create();
			Vector2f center = tf.getCenter();
			int dx = -sprite.getWidth() / 2, dy = -sprite.getHeight() / 2;
			g.translate(center.x + dx, center.y + dy);
			g.rotate(tf.getRotation());
			sprite.draw(g);
			g.dispose();
		}
	}
}