package de.amr.easy.game.sprite;

import java.awt.Image;

/**
 * Animation for a sprite. Loops through the image sequence of a sprite in a way defined by the
 * animation mode and frame timing.
 * 
 * @author Armin Reichert
 */
public abstract class Animation {

	protected final Image[] frames;
	protected boolean enabled;
	protected int frameDuration;
	protected int frameIndex;
	protected long frameTime;
	protected long lastUpdateTime;

	protected Animation(Image... frames) {
		if (frames.length == 0) {
			throw new IllegalArgumentException("Animation must have at least one frame");
		}
		this.frames = frames;
		enabled = true;
		frameDuration = 333;
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
			if (frameTime >= frameDuration) {
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
		frameDuration = millis;
		reset();
	}

	public Image currentFrame() {
		return frames[frameIndex];
	}
}