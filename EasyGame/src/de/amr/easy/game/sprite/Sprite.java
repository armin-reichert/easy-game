package de.amr.easy.game.sprite;

import static de.amr.easy.game.assets.Assets.scaledImage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Objects;

import de.amr.easy.game.assets.Assets;

/**
 * A sprite is a sequence of images used to simulate an animated object.
 * 
 * @author Armin Reichert
 */
public class Sprite {

	private final Image[] frames;
	private Animation animation;

	/**
	 * Creates a sprite from the given image sequence.
	 * 
	 * @param frames
	 *          animation frames
	 */
	public Sprite(Image... frames) {
		if (frames.length == 0) {
			throw new IllegalArgumentException("Sprite needs at least one image");
		}
		this.frames = frames;
	}

	/**
	 * Creates a sprite from frames with the given asset keys.
	 * 
	 * @param keys
	 *          keys of images stored as assets
	 */
	public Sprite(String... keys) {
		if (keys.length == 0) {
			throw new IllegalArgumentException("Sprite needs at least one image");
		}
		frames = new Image[keys.length];
		for (int i = 0; i < keys.length; ++i) {
			frames[i] = Assets.image(keys[i]);
		}
	}

	/**
	 * Scales the i'th frame of this sprite to the given size.
	 * 
	 * @param i
	 *          index of frame to be scaled
	 * @param width
	 *          target width
	 * @param height
	 *          target height
	 * @return this sprite to allow method chaining
	 */
	public Sprite scaleFrame(int i, int width, int height) {
		if (i < 0 || i >= frames.length) {
			throw new IllegalArgumentException("Sprite index out of range: " + i);
		}
		frames[i] = scaledImage(frames[i], width, height);
		return this;
	}

	/**
	 * Scales all images of this sprite to the given size.
	 * 
	 * @param width
	 *          target width
	 * @param height
	 *          target height
	 * @return this sprite to allow method chaining
	 */
	public Sprite scale(int width, int height) {
		for (int i = 0; i < frames.length; ++i) {
			frames[i] = scaledImage(frames[i], width, height);
		}
		return this;
	}

	/**
	 * Scales all frames to the same size (width, height).
	 * 
	 * @param size
	 *          the frame size
	 * @return the sprite
	 */
	public Sprite scale(int size) {
		return scale(size, size);
	}

	/**
	 * Returns the current image of this sprite.
	 * 
	 * @return currently used image
	 */
	public Image getImage() {
		return animation != null ? animation.currentFrame() : frames[0];
	}

	/**
	 * Returns the width of this sprite.
	 * 
	 * @return width of current image
	 */
	public int getWidth() {
		return getImage().getWidth(null);
	}

	/**
	 * Returns the height of this sprite.
	 * 
	 * @return height of current image
	 */
	public int getHeight() {
		return getImage().getHeight(null);
	}

	/**
	 * Draws this sprite (its current image).
	 * 
	 * @param g
	 *          graphics context
	 */
	public void draw(Graphics2D g) {
		g.drawImage(getImage(), 0, 0, null);
		if (animation != null) {
			animation.update();
		}
	}

	/**
	 * Creates an animation for this sprite.
	 * 
	 * @param mode
	 *          the animation mode
	 * @param millis
	 *          the time in milliseconds for each animation frame
	 */
	public Sprite animation(AnimationMode mode, int millis) {
		Objects.nonNull(mode);
		if (mode == AnimationMode.LINEAR) {
			animation = new LinearAnimation(frames);
		} else if (mode == AnimationMode.BACK_AND_FORTH) {
			animation = new BackForthAnimation(frames);
		} else if (mode == AnimationMode.CYCLIC) {
			animation = new CyclicAnimation(frames);
		}
		animation.setFrameDuration(millis);
		animation.setEnabled(true);
		return this;
	}

	/**
	 * Enables or disables the animation of this sprite.
	 * 
	 * @param enabled
	 *          the enabling state
	 */
	public void enableAnimation(boolean enabled) {
		if (animation != null) {
			animation.setEnabled(enabled);
			animation.reset();
		}
	}

	/**
	 * Resets the animation of this sprite.
	 */
	public void resetAnimation() {
		if (animation != null) {
			animation.reset();
		}
	}
}