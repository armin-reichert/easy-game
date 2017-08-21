package de.amr.easy.game.sprite;

import java.awt.Image;

class CyclicAnimation extends Animation {

	public CyclicAnimation(Image... frames) {
		super(frames);
	}

	@Override
	protected void nextFrame() {
		++current;
		if (current == frames.length) {
			current = 0;
		}
	}
}
