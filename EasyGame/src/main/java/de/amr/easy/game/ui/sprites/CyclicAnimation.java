package de.amr.easy.game.ui.sprites;

public class CyclicAnimation extends SpriteAnimation {

	public CyclicAnimation(int numFrames) {
		super(numFrames);
	}

	@Override
	protected void nextFrame() {
		frameIndex = (frameIndex + 1) % numFrames;
	}
}