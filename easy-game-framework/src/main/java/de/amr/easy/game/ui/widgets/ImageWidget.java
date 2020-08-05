package de.amr.easy.game.ui.widgets;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import de.amr.easy.game.entity.GameObject;

/**
 * An image that can move over the screen.
 * 
 * @author Armin Reichert
 */
public class ImageWidget extends GameObject {

	private BufferedImage image;
	private boolean moving;
	private BooleanSupplier fnCompleted;

	public ImageWidget(BufferedImage image) {
		moving = false;
		fnCompleted = () -> false;
		setImage(image);
	}

	public void setImage(BufferedImage image) {
		this.image = Objects.requireNonNull(image);
		tf.width = image.getWidth();
		tf.height = image.getHeight();
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setCompletion(BooleanSupplier fnCompleted) {
		Objects.requireNonNull(fnCompleted);
		this.fnCompleted = fnCompleted;
	}

	@Override
	public void init() {
	}

	@Override
	public void start() {
		moving = true;
	}

	@Override
	public void stop() {
		moving = false;
	}

	@Override
	public boolean isComplete() {
		return fnCompleted.getAsBoolean();
	}

	@Override
	public void update() {
		if (moving) {
			tf.move();
			if (isComplete()) {
				stop();
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		if (visible) {
			g.drawImage(image, Math.round(tf.x), Math.round(tf.y), null);
		}
	}
}