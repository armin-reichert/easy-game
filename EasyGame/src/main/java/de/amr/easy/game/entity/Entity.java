package de.amr.easy.game.entity;

import java.awt.geom.Rectangle2D;

import de.amr.easy.game.entity.collision.Collider;

/**
 * Base class for (game) entities.
 * <p>
 * An entity provides a {@link Transform transform} object that stores the position, velocity and
 * rotation of the object. Entities are also sensitive to collisions. By default, the transform's
 * position denotes the left upper corner of the collision box. Invisible entities do not trigger
 * collisions.
 * 
 * @author Armin Reichert
 */
public abstract class Entity implements Collider {

	public final Transform tf = new Transform();

	private boolean visible = true;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void init() {
	}

	public void update() {
	}

	@Override
	public Rectangle2D getCollisionBox() {
		return visible ? tf.getCollisionBox() : new Rectangle2D.Float(tf.getX(), tf.getY(), 0, 0);
	}
}