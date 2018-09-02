package de.amr.easy.game.controls;

import java.awt.Image;
import java.util.function.BooleanSupplier;

import de.amr.easy.game.entity.GameEntityUsingSprites;
import de.amr.easy.game.math.Vector2f;
import de.amr.easy.game.sprite.Sprite;

/**
 * An image that can be scrolled.
 * 
 * @author Armin Reichert
 */
public class ScrollableImage extends GameEntityUsingSprites implements AnimationController {

	private float speedX;
	private float speedY;
	private BooleanSupplier completion;

	public ScrollableImage(Image image) {
		completion = () -> false;
		setSprite("s_image", Sprite.of(image));
		setCurrentSprite("s_image");
		tf.setWidth(currentSprite().getWidth());
		tf.setHeight(currentSprite().getHeight());
	}

	public void setSpeedX(float speedX) {
		this.speedX = speedX;
		tf.setVelocityX(speedX);
	}

	public void setSpeedY(float speedY) {
		this.speedY = speedY;
		tf.setVelocityY(speedY);
	}

	public void setCompletion(BooleanSupplier completion) {
		this.completion = completion;
	}

	@Override
	public void start() {
		tf.setVelocityX(speedX);
		tf.setVelocityY(speedY);
	}

	@Override
	public void stop() {
		tf.setVelocity(Vector2f.NULL);
	}

	@Override
	public boolean isCompleted() {
		return completion.getAsBoolean();
	}

	@Override
	public void update() {
		tf.move();
		if (isCompleted()) {
			stop();
		}
	}
}