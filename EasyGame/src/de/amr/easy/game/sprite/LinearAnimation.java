package de.amr.easy.game.sprite;

import java.awt.Image;

public class LinearAnimation extends Animation {

	public LinearAnimation(Image... frames) {
		super(frames);
	}

	@Override
	protected void nextFrame() {
		if (frameIndex < frames.length - 1)
			++frameIndex;
	}

	@Override
	public float getSeconds() {
		return (frames.length * frameDurationMillis / 1000f);
	}
}