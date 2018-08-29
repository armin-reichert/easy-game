package de.amr.easy.game.entity;

import java.awt.geom.Rectangle2D;

import de.amr.easy.game.entity.collision.Collider;
import de.amr.easy.game.view.ViewController;

/**
 * Base class for game entities.
 * <p>
 * A game entity is a view controller that provides a {@link Transform transform} object where the
 * position, velocity and rotation of the object are stored. Game objects are also sensitive to
 * collisions. By default, the transform position denotes the left upper corner of the rectangle
 * defining the bounding box of the object.
 * 
 * @author Armin Reichert
 */
public abstract class GameEntity implements ViewController, Collider {

	protected final Transform tf = new Transform();

	public Transform tf() {
		return tf;
	}

	public int getCollisionWidth() {
		return tf.getWidth();
	}

	public int getCollisionHeight() {
		return tf.getHeight();
	}

	@Override
	public Rectangle2D getCollisionBox() {
		return tf.getCollisionBox();
	}

	public void centerHorizontally(int width) {
		tf().setX((width - getWidth()) / 2);
	}

	public void centerVertically(int height) {
		tf().setY((height - getHeight()) / 2);
	}

	public void center(int width, int height) {
		centerHorizontally(width);
		centerVertically(height);
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}
}