package de.amr.easy.game.ui.widgets;

import static de.amr.easy.game.ui.sprites.AnimationType.BACK_AND_FORTH;
import static java.lang.Math.round;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Objects;

import de.amr.easy.game.entity.GameEntityUsingSprites;
import de.amr.easy.game.ui.sprites.Sprite;

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
			product.setVisible(visible);
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
	private int frameCount;
	private float scale;
	private int periodMillis;

	private PumpingImage() {
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
		Sprite sprite = Sprite.of(frames);
		sprite.animate(BACK_AND_FORTH, periodMillis / frameCount);
		sprite.enableAnimation(true);
		sprites.set("s_image", sprite);
		sprites.select("s_image");
		tf.setWidth(sprite.getMaxWidth());
		tf.setHeight(sprite.getMaxHeight());
	}
}