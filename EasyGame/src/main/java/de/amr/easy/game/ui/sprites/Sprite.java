package de.amr.easy.game.ui.sprites;

import static de.amr.easy.game.assets.Assets.scaledImage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Arrays;
import java.util.Objects;

import de.amr.easy.game.assets.Assets;
import de.amr.easy.game.view.View;

/**
 * A sprite.
 * 
 * @author Armin Reichert
 */
public class Sprite implements View {

	private Image[] frames;
	private Animation animation;

	private Sprite() {
	}

	/**
	 * Creates a sprite with the given frames.
	 * 
	 * @param frames
	 *                 non-empty list of animation frames
	 */
	public static Sprite of(Image... frames) {
		if (frames.length == 0) {
			throw new IllegalArgumentException("Sprite needs at least a single frame");
		}
		Sprite sprite = new Sprite();
		sprite.frames = Arrays.copyOf(frames, frames.length);
		return sprite;
	}

	/**
	 * Creates a sprite from the frames stored in the assets with the given keys.
	 * 
	 * @param keys
	 *               list of keys of the images
	 */
	public static Sprite ofAssets(String... keys) {
		if (keys.length == 0) {
			throw new IllegalArgumentException("Sprite needs at least one image");
		}
		Sprite sprite = new Sprite();
		sprite.frames = new Image[keys.length];
		for (int i = 0; i < keys.length; ++i) {
			sprite.frames[i] = Assets.image(keys[i]);
		}
		return sprite;
	}

	/**
	 * Scales the i'th frame of this sprite to the given size.
	 * 
	 * @param i
	 *                 index of frame to be scaled
	 * @param width
	 *                 target width
	 * @param height
	 *                 target height
	 * @return this sprite to allow method chaining
	 */
	public Sprite scaleFrame(int i, int width, int height) {
		if (i < 0 || i >= frames.length) {
			throw new IllegalArgumentException("Sprite index out of range: " + i);
		}
		if (frames[i] != null) {
			frames[i] = scaledImage(frames[i], width, height);
		}
		return this;
	}

	/**
	 * Scales all images of this sprite to the given size.
	 * 
	 * @param width
	 *                 target width
	 * @param height
	 *                 target height
	 * @return this sprite to allow method chaining
	 */
	public Sprite scale(int width, int height) {
		for (int i = 0; i < frames.length; ++i) {
			frames[i] = scaledImage(frames[i], width, height);
		}
		return this;
	}

	/**
	 * Scales all frames to the same size (width = height).
	 * 
	 * @param size
	 *               the frame size
	 * @return the sprite
	 */
	public Sprite scale(int size) {
		return scale(size, size);
	}

	/**
	 * Returns the current frame of this sprite.
	 * 
	 * @return current frame or the single frame for a non-animated sprite
	 */
	public Image currentFrame() {
		return frames[animation == null ? 0 : animation.currentFrame()];
	}

	/**
	 * 
	 * @param i
	 *            frame index
	 * @return i'th frame of the sprite
	 */
	public Image frame(int i) {
		return frames[i];
	}

	/**
	 * Returns the width of this sprite.
	 * 
	 * @return width of current frame
	 */
	public int getWidth() {
		Image frame = currentFrame();
		return frame != null ? frame.getWidth(null) : 0;
	}

	/**
	 * Returns the maximum width of any frame.
	 * 
	 * @return maximum frame width
	 */
	public int getMaxWidth() {
		int max = frames[0].getWidth(null);
		for (int i = 1; i < frames.length; ++i) {
			max = Math.max(max, frames[i].getWidth(null));
		}
		return max;
	}

	/**
	 * Returns the height of this sprite.
	 * 
	 * @return height of current frame
	 */
	public int getHeight() {
		Image frame = currentFrame();
		return frame != null ? frame.getHeight(null) : 0;
	}

	/**
	 * Returns the maximum height of any frame.
	 * 
	 * @return maximum frame height
	 */
	public int getMaxHeight() {
		int max = frames[0].getHeight(null);
		for (int i = 1; i < frames.length; ++i) {
			max = Math.max(max, frames[i].getHeight(null));
		}
		return max;
	}

	/**
	 * Draws the current animation frame and moves to the next animation frame.
	 * 
	 * @param g
	 *            graphics context
	 */
	@Override
	public void draw(Graphics2D g) {
		Image frame = currentFrame();
		if (frame != null) {
			g.drawImage(frame, 0, 0, null);
		}
		if (animation != null && animation.isEnabled()) {
			animation.update();
		}
	}

	/**
	 * Creates an animation for this sprite.
	 * 
	 * @param type
	 *                 the animation type
	 * @param millis
	 *                 the time in milliseconds for each animation frame
	 */
	public Sprite animate(AnimationType type, int millis) {
		Objects.nonNull(type);
		if (type == AnimationType.LINEAR) {
			animation = new LinearAnimation(frames.length);
		} else if (type == AnimationType.BACK_AND_FORTH) {
			animation = new BackForthAnimation(frames.length);
		} else if (type == AnimationType.CYCLIC) {
			animation = new CyclicAnimation(frames.length);
		}
		animation.setFrameDuration(millis);
		animation.setEnabled(true);
		return this;
	}

	public float getAnimationSeconds() {
		return animation != null ? animation.getSeconds() : 0;
	}

	/**
	 * Enables or disables the animation of this sprite.
	 * 
	 * @param enabled
	 *                  the enabling state
	 */
	public void enableAnimation(boolean enabled) {
		if (animation != null) {
			animation.setEnabled(enabled);
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