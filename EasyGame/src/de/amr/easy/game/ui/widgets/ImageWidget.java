package de.amr.easy.game.ui.widgets;

import java.awt.Image;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import de.amr.easy.game.entity.SpriteEntity;
import de.amr.easy.game.ui.sprites.Sprite;
import de.amr.easy.game.view.AnimationController;

/**
 * An image that can be moved over the screen.
 * 
 * @author Armin Reichert
 */
public class ImageWidget extends SpriteEntity implements AnimationController {

	private boolean moving;
	private BooleanSupplier fnCompleted;

	public ImageWidget(Image image) {
		moving = false;
		fnCompleted = () -> false;
		Sprite sprite = Sprite.of(image);
		sprites.set("s_image", sprite);
		sprites.select("s_image");
		tf.setWidth(sprite.getWidth());
		tf.setHeight(sprite.getHeight());
	}

	public void setCompletion(BooleanSupplier fnCompleted) {
		Objects.requireNonNull(fnCompleted);
		this.fnCompleted = fnCompleted;
	}

	@Override
	public void startAnimation() {
		moving = true;
	}

	@Override
	public void stopAnimation() {
		moving = false;
	}

	@Override
	public boolean isAnimationCompleted() {
		return fnCompleted.getAsBoolean();
	}

	@Override
	public void update() {
		if (moving) {
			tf.move();
			if (isAnimationCompleted()) {
				stopAnimation();
			}
		}
	}
}