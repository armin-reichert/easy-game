package de.amr.easy.game.ui.sprites;

public class LinearAnimation extends SpriteAnimation {

	public LinearAnimation(int numFrames) {
		super(numFrames);
	}

	@Override
	protected void nextFrame() {
		if (frameIndex < numFrames - 1)
			++frameIndex;
	}
}