package de.amr.easy.game.controls;

import static de.amr.easy.game.sprite.AnimationType.BACK_AND_FORTH;
import static java.lang.Math.round;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Objects;

import de.amr.easy.game.entity.GameEntityUsingSprites;
import de.amr.easy.game.sprite.Sprite;

public class PumpingImage extends GameEntityUsingSprites {

	public static class Builder {

		private final PumpingImage product;

		private Builder() {
			product = new PumpingImage();
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

		public PumpingImage build() {
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
	private boolean visible;
	private int periodMillis;

	private PumpingImage() {
		frameCount = 6;
		scale = 2;
		visible = true;
		periodMillis = 1000;
	}

	public void setScale(float scale) {
		this.scale = scale;
		updateSprite();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
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
		setSprite("s_image", sprite);
		setCurrentSprite("s_image");
		tf.setWidth(sprite.getMaxWidth());
		tf.setHeight(sprite.getMaxHeight());
	}

	@Override
	public void draw(Graphics2D g) {
		if (visible) {
			int dx = (tf.getWidth() - sprite.currentFrame().getWidth(null)) / 2;
			int dy = (tf.getHeight() - sprite.currentFrame().getHeight(null)) / 2;
			g.translate(dx, dy);
			super.draw(g);
			g.translate(-dx, -dy);
		}
	}
}