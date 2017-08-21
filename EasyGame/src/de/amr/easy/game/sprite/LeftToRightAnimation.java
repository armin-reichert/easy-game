package de.amr.easy.game.sprite;

import java.awt.Image;

public class LeftToRightAnimation extends Animation {
	
	public LeftToRightAnimation(Image... frames) {
		super(frames);
	}
	
	@Override
	protected void nextFrame() {
		if (current < frames.length - 1)
			++current;
	}

}
