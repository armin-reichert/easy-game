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
public class Entity implements Collider {

	/** The transform for this entity. */
	public final Transform tf = new Transform();

	/**
	 * Visibility of this entity. Invisible entities are not rendered and do not cause collisions.
	 */
	public boolean visible = true;

	@Override
	public Rectangle2D getCollisionBox() {
		return visible ? tf.getCollisionBox() : new Rectangle2D.Float(tf.x, tf.y, 0, 0);
	}
}