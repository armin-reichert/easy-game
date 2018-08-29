package de.amr.easy.game.controls;

import static de.amr.easy.game.sprite.AnimationType.BACK_AND_FORTH;
import static java.lang.Math.round;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import de.amr.easy.game.entity.GameEntityUsingSprites;
import de.amr.easy.game.sprite.Sprite;

public class PumpingImage extends GameEntityUsingSprites {

	private final int frameCount = 6;
	private final Image image;
	private float scale;
	private boolean visible;

	public PumpingImage(Image image) {
		this.image = image;
		scale = 2;
		visible = true;
		updateSprite();
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
		Sprite sprite = new Sprite(frames);
		sprite.animate(BACK_AND_FORTH, 166);
		sprite.enableAnimation(true);
		tf.setWidth(sprite.getWidth());
		tf.setHeight(sprite.getHeight());
		addSprite("s_image", sprite);
		setCurrentSprite("s_image");
	}

	@Override
	public void draw(Graphics2D g) {
		if (isVisible()) {
			super.draw(g);
		}
	}
}