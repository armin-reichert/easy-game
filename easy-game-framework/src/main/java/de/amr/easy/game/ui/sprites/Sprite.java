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
 * An animated sprite.
 * 
 * <p>
 * In my book, a "sprite" is a sequence of images ("frames") which, when played in sequence, create
 * the illusion of a movement or animation. In this implementation, the animation proceeds by
 * drawing the sprite.
 * 
 * @author Armin Reichert
 */
public class Sprite {

	static final Image BLANK_FRAME = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

	/**
	 * Creates a sprite using the given images. Null images are treated as blank frames.
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
	 * Creates a sprite from the images stored in the assets with the given keys.
	 * 
	 * @param keys asset keys of the images
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

	private final Image[] frames;
	private SpriteAnimation animation;

	private Sprite(int numFrames) {
		this.frames = new Image[numFrames];
		animation = SpriteAnimation.NO_ANIMATION;
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
		if (animation.isEnabled()) {
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
		case FORWARD_BACKWARDS:
			return new ForwardBackwardAnimation(frames.length);
		case CYCLIC:
			return new CyclicAnimation(frames.length);
		case LINEAR:
			return new LinearAnimation(frames.length);
		default:
			throw new IllegalArgumentException("Illegal animation type: " + type);
		}
	}

	/**
	 * Returns the current frame of this sprite.
	 * 
	 * @return current frame or the single frame for a non-animated sprite
	 */
	public Optional<Image> currentAnimationFrame() {
		if (animation == SpriteAnimation.NO_ANIMATION) {
			return Optional.of(frames[0]);
		}
		return Optional.of(frames[animation.currentFrameIndex()]);
	}

	/**
	 * Enables or disables the animation of this sprite.
	 * 
	 * @param enabled the enabling state
	 */
	public void enableAnimation(boolean enabled) {
		animation.setEnabled(enabled);
	}

	/**
	 * Resets the animation of this sprite.
	 */
	public void resetAnimation() {
		animation.reset();
	}
}