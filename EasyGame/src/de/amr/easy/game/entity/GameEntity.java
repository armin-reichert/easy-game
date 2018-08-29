package de.amr.easy.game.entity;

import java.awt.geom.Rectangle2D;

import de.amr.easy.game.entity.collision.Collider;
import de.amr.easy.game.view.Controller;

/**
 * Base class for game entities.
 * <p>
 * A game entity is a controller that provides a {@link Transform transform} object that stores the
 * position, velocity and rotation of the object. Game objects are also sensitive to collisions. By
 * default, the transform position denotes the left upper corner of the rectangle defining the
 * bounding box of the object.
 * 
 * @author Armin Reichert
 */
public class GameEntity implements Controller, Collider {

	public final Transform tf = new Transform();

	@Override
	public Rectangle2D getCollisionBox() {
		return tf.getCollisionBox();
	}

	public void centerHorizontally(int width) {
		tf.setX((width - tf.getWidth()) / 2);
	}

	public void centerVertically(int height) {
		tf.setY((height - tf.getHeight()) / 2);
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