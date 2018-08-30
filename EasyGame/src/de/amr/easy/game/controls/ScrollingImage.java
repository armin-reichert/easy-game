package de.amr.easy.game.controls;

import java.awt.Image;

import de.amr.easy.game.entity.GameEntityUsingSprites;
import de.amr.easy.game.sprite.Sprite;

/**
 * An animation scrolling an image.
 * 
 * @author Armin Reichert
 */
public abstract class ScrollingImage extends GameEntityUsingSprites implements AnimationController {

	public ScrollingImage(Image image) {
		setSprite("s_image", Sprite.of(image));
		setCurrentSprite("s_image");
		tf.setWidth(currentSprite().getWidth());
		tf.setHeight(currentSprite().getHeight());
	}

	@Override
	public void update() {
		tf.move();
	}
}