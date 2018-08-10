package de.amr.easy.game.sprite;

/**
 * Animation for a sprite. Loops through the image sequence of a sprite in a way defined by the
 * animation mode and frame timing.
 * 
 * @author Armin Reichert
 */
public abstract class Animation {

	protected final int numFrames;
	protected boolean enabled;
	protected int frameDurationMillis;
	protected int frameIndex;
	protected long frameTime;
	protected long lastUpdateTime;

	protected Animation(int numFrames) {
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

	public int currentFrame() {
		return frameIndex;
	}
	
	public abstract float getSeconds();
}