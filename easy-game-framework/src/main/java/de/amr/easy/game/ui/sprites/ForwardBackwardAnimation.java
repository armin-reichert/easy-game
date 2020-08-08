package de.amr.easy.game.ui.sprites;

/**
 * An animation going through the frames forward, backwards etc.
 * 
 * @author Armin Reichert
 */
public class ForwardBackwardAnimation extends SpriteAnimation {

	private boolean forward;

	public ForwardBackwardAnimation(int numFrames) {
		super(numFrames);
		forward = true;
	}

	@Override
	public void reset() {
		super.reset();
		forward = true;
	}

	@Override
	protected void nextFrame() {
		if (forward) {
			if (frameIndex == numFrames - 1) {
				forward = false;
			} else {
				++frameIndex;
			}
		} else {
			if (frameIndex == 0) {
				forward = true;
			} else {
				--frameIndex;
			}
		}
	}
}