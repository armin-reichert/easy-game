package de.amr.easy.game.sprite;

import java.awt.Image;

class BackForthAnimation extends Animation {

	private boolean forward;

	public BackForthAnimation(Image... frames) {
		super(frames);
	}

	@Override
	protected void reset() {
		forward = true;
		super.reset();
	}

	@Override
	protected void nextFrame() {
		if (forward) {
			++frameIndex;
			if (frameIndex == frames.length - 1) {
				forward = false;
			}
		} else {
			--frameIndex;
			if (frameIndex == 0) {
				forward = true;
			}
		}
	}
}
