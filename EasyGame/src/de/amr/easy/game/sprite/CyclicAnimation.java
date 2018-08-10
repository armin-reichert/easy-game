package de.amr.easy.game.sprite;

public class CyclicAnimation extends Animation {

	public CyclicAnimation(int numFrames) {
		super(numFrames);
	}

	@Override
	protected void nextFrame() {
		frameIndex = (frameIndex + 1) % numFrames;
	}

	@Override
	public float getSeconds() {
		return Float.MAX_VALUE;
	}
}