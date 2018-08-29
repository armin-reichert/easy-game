package de.amr.easy.game.controls;

import static de.amr.easy.game.sprite.AnimationType.BACK_AND_FORTH;
import static java.lang.Math.round;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import de.amr.easy.game.entity.GameEntityUsingSprites;
import de.amr.easy.game.sprite.Sprite;

public class PumpingImage extends GameEntityUsingSprites {

	public Supplier<Boolean> fnVisibility = () -> true;

	private final int frameCount = 6;
	private final Image image;
	private float scale;

	public PumpingImage(Image image) {
		this.image = image;
		scale = 2;
		createSprite();
	}

	public void setScale(float scale) {
		this.scale = scale;
		createSprite();
	}

	private void createSprite() {
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
		addSprite("s_image", sprite);
		setCurrentSprite("s_image");
	}
}