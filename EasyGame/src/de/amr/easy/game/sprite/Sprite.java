package de.amr.easy.game.sprite;

import static de.amr.easy.game.assets.Assets.scaledImage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Optional;

import de.amr.easy.game.assets.Assets;

/**
 * A sprite is a sequence of images used to simulate an animated object.
 * 
 * @author Armin Reichert
 */
public class Sprite {

	private final Image[] images;
	private Optional<Animation> animation = Optional.empty();

	/**
	 * Creates a sprite from the given image sequence.
	 * 
	 * @param images
	 */
	public Sprite(Image... images) {
		if (images.length == 0) {
			throw new IllegalArgumentException("Sprite needs at least one image");
		}
		this.images = images;
	}

	/**
	 * Creates a sprite from the image sequence taken from the application assets and with the given
	 * keys.
	 * 
	 * @param imageKeys
	 *          image keys as stored inside the assets
	 */
	public Sprite(String... imageKeys) {
		if (imageKeys.length == 0) {
			throw new IllegalArgumentException("Sprite needs at least one image");
		}
		images = new Image[imageKeys.length];
		int i = 0;
		for (String key : imageKeys) {
			images[i++] = Assets.image(key);
		}
	}

	/**
	 * Scales the i'th image of this sprite to the given size.
	 * 
	 * @param index
	 *          index of image to be scaled
	 * @param width
	 *          target width
	 * @param height
	 *          target height
	 * @return this sprite to allow method chaining
	 */
	public Sprite scale(int index, int width, int height) {
		if (index < 0 || index >= images.length) {
			throw new IllegalArgumentException("Sprite index out of range: " + index);
		}
		images[index] = scaledImage(images[index], width, height);
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
		for (int i = 0; i < images.length; ++i) {
			images[i] = scaledImage(images[i], width, height);
		}
		return this;
	}

	/**
	 * Returns the current image of this sprite.
	 * 
	 * @return currently used image
	 */
	public Image getImage() {
		return animation.isPresent() ? animation.get().currentImage() : images[0];
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
		animation.ifPresent(Animation::update);
	}

	/**
	 * Creates an animation for this sprite.
	 * 
	 * @param mode
	 *          the animation mode
	 * @param frameDurationMillis
	 *          the time in milliseconds for each animation frame
	 */
	public void makeAnimated(AnimationMode mode, int frameDurationMillis) {
		if (mode == AnimationMode.LINEAR) {
			animation = Optional.of(new LeftToRightAnimation(images));
		} else if (mode == AnimationMode.BACK_AND_FORTH) {
			animation = Optional.of(new BackForthAnimation(images));
		} else if (mode == AnimationMode.CYCLIC) {
			animation = Optional.of(new CyclicAnimation(images));
		} else {
			throw new IllegalArgumentException("Illegal animation mode: " + mode);
		}
		animation.get().setFrameDuration(frameDurationMillis);
		animation.get().setEnabled(true);
	}

	/**
	 * Enables or disables the animation of this sprite.
	 * 
	 * @param enabled
	 *          the new state
	 */
	public void setAnimationEnabled(boolean enabled) {
		animation.ifPresent(a -> a.setEnabled(enabled));
	}

	/**
	 * Resets the animation of this sprite to its initial state.
	 */
	public void resetAnimation() {
		animation.ifPresent(Animation::reset);
	}
}
