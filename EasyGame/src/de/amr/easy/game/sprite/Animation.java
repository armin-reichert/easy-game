package de.amr.easy.game.sprite;

import java.awt.Image;

/**
 * Animation for a sprite. Loops through the image sequence of a sprite in a way defined by the
 * animation mode and frame timing.
 * 
 * @author Armin Reichert
 */
abstract class Animation {

	protected final Image[] frames;
	private boolean enabled;
	private int frameDuration;
	private long frameRunning;
	private long lastTime;
	protected int current;

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

	protected void reset() {
		frameRunning = 0;
		lastTime = System.currentTimeMillis();
		current = 0;
	}

	public void update() {
		if (!enabled) {
			return;
		}
		long currentTime = System.currentTimeMillis();
		frameRunning += (currentTime - lastTime);
		if (frameRunning >= frameDuration) {
			nextFrame();
			frameRunning = 0;
		}
		lastTime = currentTime;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			if (!enabled) {
				reset();
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setFrameDuration(int frameDuration) {
		this.frameDuration = frameDuration;
		reset();
	}

	public Image currentImage() {
		return frames[current];
	}
}