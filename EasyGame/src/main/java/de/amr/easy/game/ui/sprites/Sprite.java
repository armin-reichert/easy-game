package de.amr.easy.game.ui.sprites;

import static de.amr.easy.game.assets.Assets.scaledImage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import de.amr.easy.game.assets.Assets;

/**
 * A sprite.
 * 
 * <p>
 * In my understanding, a "sprite" is a sequence of images ("frames") which, when played in
 * sequence, create the illusion of a movement or animation. In this implementation, the animation
 * is driven by drawing the sprite which might be naive but is sufficient for my purposes.
 * 
 * @author Armin Reichert
 */
public class Sprite {

	/**
	 * Uses this constant if you want to insert blank frames.
	 */
	public static final Image BLANK_FRAME = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	private final Image[] frames;
	private SpriteAnimation animation;

	private Sprite(int numFrames) {
		this.frames = new Image[numFrames];
	}

	private void setFrame(int i, Image image) {
		rangeCheck(i);
		frames[i] = image != null ? image : BLANK_FRAME;
	}

	private void rangeCheck(int i) {
		if (i < 0 || i >= frames.length) {
			throw new IllegalArgumentException("Sprite index out of range: " + i);
		}
	}

	/**
	 * Creates a sprite with the given frames.
	 * 
	 * @param images non-empty list of images
	 */
	public static Sprite of(Image... images) {
		if (images.length == 0) {
			throw new IllegalArgumentException("Sprite needs at least a single frame");
		}
		Sprite sprite = new Sprite(images.length);
		for (int i = 0; i < images.length; ++i) {
			sprite.setFrame(i, images[i]);
		}
		return sprite;
	}

	/**
	 * Creates a sprite from the frames stored in the assets with the given keys.
	 * 
	 * @param keys list of keys of the images
	 */
	public static Sprite ofAssets(String... keys) {
		if (keys.length == 0) {
			throw new IllegalArgumentException("Sprite needs at least one image");
		}
		Sprite sprite = new Sprite(keys.length);
		for (int i = 0; i < keys.length; ++i) {
			sprite.setFrame(i, Assets.image(keys[i]));
		}
		return sprite;
	}

	/**
	 * Scales the i'th frame of this sprite to the given size.
	 * 
	 * @param i            index of frame to be scaled
	 * @param targetWidth  target width
	 * @param targetHeight target height
	 * @return this sprite to allow method chaining
	 */
	public Sprite scale(int i, int targetWidth, int targetHeight) {
		rangeCheck(i);
		if (frames[i] != BLANK_FRAME) {
			frames[i] = scaledImage(frames[i], targetWidth, targetHeight);
		}
		return this;
	}

	/**
	 * Scales all images of this sprite to the given size.
	 * 
	 * @param targetWidth  target width
	 * @param targetHeight target height
	 * @return this sprite to allow method chaining
	 */
	public Sprite scale(int targetWidth, int targetHeight) {
		for (int i = 0; i < frames.length; ++i) {
			scale(i, targetWidth, targetHeight);
		}
		return this;
	}

	/**
	 * Scales all frames to the same size (width = height).
	 * 
	 * @param size the frame size
	 * @return the sprite
	 */
	public Sprite scale(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("Size must be positive, is " + size);
		}
		return scale(size, size);
	}

	/**
	 * Returns the current frame of this sprite.
	 * 
	 * @return current frame or the single frame for a non-animated sprite
	 */
	public Optional<Image> currentAnimationFrame() {
		return Optional.ofNullable(frames[animation == null ? 0 : animation.currentFrameIndex()]);
	}

	/**
	 * @param i frame index
	 * @return i'th frame of the sprite
	 */
	public Image frame(int i) {
		rangeCheck(i);
		return frames[i];
	}

	/**
	 * Returns the width of this sprite.
	 * 
	 * @return width of current frame
	 */
	public int getWidth() {
		return currentAnimationFrame().map(frame -> frame.getWidth(null)).orElse(0);
	}

	/**
	 * Returns the height of this sprite.
	 * 
	 * @return height of current frame
	 */
	public int getHeight() {
		return currentAnimationFrame().map(frame -> frame.getHeight(null)).orElse(0);
	}

	/**
	 * Returns the maximum width over all frames.
	 * 
	 * @return maximum frame width
	 */
	public int getMaxWidth() {
		return Arrays.stream(frames).map(frame -> frame.getWidth(null)).max(Integer::compare).orElse(0);
	}

	/**
	 * Returns the maximum height of any frame.
	 * 
	 * @return maximum frame height
	 */
	public int getMaxHeight() {
		return Arrays.stream(frames).map(frame -> frame.getHeight(null)).max(Integer::compare).orElse(0);
	}

	/**
	 * Draws the current animation frame and moves to the next animation frame.
	 * 
	 * @param g graphics context
	 */
	public void draw(Graphics2D g) {
		currentAnimationFrame().ifPresent(frame -> g.drawImage(frame, 0, 0, null));
		if (animation != null && animation.isEnabled()) {
			animation.update();
		}
	}

	/**
	 * Draws the current animation frame at the specified position and moves to the next animation
	 * frame.
	 * 
	 * @param g graphics context
	 * @param x x-position
	 * @param y y-position
	 */
	public void draw(Graphics2D g, double x, double y) {
		g.translate(x, y);
		draw(g);
		g.translate(-x, -y);
	}

	/**
	 * Creates an animation for this sprite.
	 * 
	 * @param type   the animation type
	 * @param millis the time in milliseconds for each animation frame
	 */
	public Sprite animate(AnimationType type, int millis) {
		Objects.requireNonNull(type);
		animation = createAnimation(type);
		animation.setFrameDuration(millis);
		animation.setEnabled(true);
		return this;
	}

	private SpriteAnimation createAnimation(AnimationType type) {
		switch (type) {
		case BACK_AND_FORTH:
			return new BackForthAnimation(frames.length);
		case CYCLIC:
			return new CyclicAnimation(frames.length);
		case LINEAR:
			return new LinearAnimation(frames.length);
		default:
			throw new IllegalArgumentException("Illegal animation type: " + type);
		}
	}

	/**
	 * Enables or disables the animation of this sprite.
	 * 
	 * @param enabled the enabling state
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