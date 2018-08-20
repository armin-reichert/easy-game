package de.amr.easy.game.controls;

import static de.amr.easy.game.sprite.AnimationType.BACK_AND_FORTH;
import static java.lang.Math.round;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import de.amr.easy.game.entity.GameEntity;
import de.amr.easy.game.sprite.Sprite;

public class PumpingImage extends GameEntity {

	private Sprite sprite;
	private final int frameCount = 6;
	private final Image image;
	private float scale;

	public PumpingImage(Image image) {
		this.image = image;
		scale = 2;
		createFrames();
	}

	public void setScale(float scale) {
		this.scale = scale;
		createFrames();
	}

	@Override
	public int getWidth() {
		return sprite.getWidth();
	}

	@Override
	public int getHeight() {
		return sprite.getHeight();
	}

	@Override
	public Sprite currentSprite() {
		return sprite;
	}

	@Override
	public Stream<Sprite> getSprites() {
		return Stream.of(sprite);
	}

	private void createFrames() {
		Image[] frames = new Image[frameCount];
		float delta = scale / frames.length;
		int height = image.getHeight(null);
		for (int i = 0; i < frames.length; ++i) {
			int frameHeight = round(height + i * delta * height);
			frames[i] = image.getScaledInstance(-1, frameHeight, BufferedImage.SCALE_FAST);
		}
		sprite = new Sprite(frames);
		sprite.animate(BACK_AND_FORTH, 166);
		sprite.enableAnimation(true);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}