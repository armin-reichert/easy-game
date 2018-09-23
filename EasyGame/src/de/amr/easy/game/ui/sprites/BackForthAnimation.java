package de.amr.easy.game.ui.sprites;

public class BackForthAnimation extends Animation {

	private boolean forward;

	public BackForthAnimation(int numFrames) {
		super(numFrames);
	}

	@Override
	public void reset() {
		forward = true;
		super.reset();
	}

	@Override
	protected void nextFrame() {
		if (forward) {
			++frameIndex;
			if (frameIndex == numFrames - 1) {
				forward = false;
			}
		} else {
			--frameIndex;
			if (frameIndex == 0) {
				forward = true;
			}
		}
	}

	@Override
	public float getSeconds() {
		return Float.MAX_VALUE;
	}
}