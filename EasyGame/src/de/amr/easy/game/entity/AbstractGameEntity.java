package de.amr.easy.game.entity;

import java.awt.geom.Rectangle2D;

import de.amr.easy.game.entity.collision.Collider;
import de.amr.easy.game.view.Controller;

/**
 * Base class for game entities.
 * <p>
 * A game entity provides a {@link Transform transform} object that stores the position, velocity
 * and rotation of the object. Game objects are also sensitive to collisions. By default, the
 * transform position denotes the left upper corner of the rectangle defining the bounding box of
 * the object. Invisible entities do not trigger collisions.
 * 
 * @author Armin Reichert
 */
public abstract class AbstractGameEntity implements Controller, Collider {

	public final Transform tf = new Transform();

	private boolean visible = true;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public Rectangle2D getCollisionBox() {
		return isVisible() ? tf.getCollisionBox() : new Rectangle2D.Float(tf.getX(), tf.getY(), 0, 0);
	}
}