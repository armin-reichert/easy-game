package de.amr.easy.game.sprite;

import java.awt.Image;

class CyclicAnimation extends Animation {

	public CyclicAnimation(Image... frames) {
		super(frames);
	}

	@Override
	protected void nextFrame() {
		++frameIndex;
		if (frameIndex == frames.length) {
			frameIndex = 0;
		}
	}

	@Override
	public float getSeconds() {
		return Float.MAX_VALUE;
	}
}