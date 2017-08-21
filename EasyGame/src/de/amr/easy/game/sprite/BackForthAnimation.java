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
			++current;
			if (current == frames.length - 1) {
				forward = false;
			}
		} else {
			--current;
			if (current == 0) {
				forward = true;
			}
		}
	}
}
