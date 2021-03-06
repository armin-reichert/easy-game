package de.amr.easy.game.ui.sprites;

/**
 * Animation for a sprite. Loops through the image sequence of a sprite in a way defined by the
 * animation mode and frame timing.
 * 
 * @author Armin Reichert
 */
public abstract class SpriteAnimation {

	static final SpriteAnimation NO_ANIMATION = new SpriteAnimation(0) {

		@Override
		protected void nextFrame() {

		}
	};

	protected int numFrames;
	protected int frameDurationMillis;
	protected int frameIndex;
	protected long frameTime;
	protected long lastUpdateTime;
	protected boolean enabled;

	protected SpriteAnimation(int numFrames) {
		this.numFrames = numFrames;
		enabled = true;
		frameDurationMillis = 333;
		reset();
	}

	protected abstract void nextFrame();

	public void reset() {
		frameIndex = 0;
		frameTime = 0;
		lastUpdateTime = 0;
	}

	public void update() {
		if (!enabled) {
			return;
		}
		long now = System.currentTimeMillis();
		if (lastUpdateTime != 0) {
			frameTime += (now - lastUpdateTime);
			if (frameTime >= frameDurationMillis) {
				nextFrame();
				frameTime = 0;
			}
		}
		lastUpdateTime = now;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setFrameDuration(int millis) {
		frameDurationMillis = millis;
		reset();
	}

	public int currentFrameIndex() {
		return frameIndex;
	}
}