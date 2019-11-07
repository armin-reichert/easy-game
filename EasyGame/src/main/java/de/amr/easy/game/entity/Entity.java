package de.amr.easy.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import de.amr.easy.game.entity.collision.Collider;
import de.amr.easy.game.math.Vector2f;
import de.amr.easy.game.ui.sprites.Sprite;
import de.amr.easy.game.ui.sprites.SpriteMap;
import de.amr.easy.game.view.View;

/**
 * Base class for (game) entities.
 * <p>
 * An entity provides a {@link Transform transform} object that stores the position, velocity and
 * rotation of the object. Entities are also sensitive to collisions. By default, the transform's
 * position denotes the left upper corner of the collision box. Invisible entities do not trigger
 * collisions.
 * 
 * <p>
 * Optionally, it can store sprites which can be referenced by string keys.
 * 
 * @author Armin Reichert
 */
public abstract class Entity implements Collider, View {

	/** The transform for this entity. */
	public final Transform tf = new Transform();

	/** The sprite map for this entity. */
	public final SpriteMap sprites = new SpriteMap();

	/** Visibility of this entity. Invisible entities are not rendered and do not cause collisions. */
	public boolean visible = true;

	/** If <code>true</code> the collision box is drawn (for debugging). */
	public boolean showCollisionBox = false;

	@Override
	public void draw(Graphics2D g) {
		if (sprites.current().isPresent() && visible) {
			if (showCollisionBox) {
				g.translate(tf.getX(), tf.getY());
				g.setColor(Color.RED);
				g.drawRect(0, 0, tf.getWidth(), tf.getHeight());
				g.translate(-tf.getX(), -tf.getY());
			}
			Sprite sprite = sprites.current().get();
			Vector2f center = tf.getCenter();
			float dx = center.x - sprite.getWidth() / 2;
			float dy = center.y - sprite.getHeight() / 2;
			Graphics2D g2 = (Graphics2D) g.create();
			g2.translate(dx, dy);
			g2.rotate(tf.getRotation());
			sprite.draw(g2);
			g2.dispose();
		}
	}

	/**
	 * Initialization hook method.
	 */
	public void init() {
	}

	/**
	 * Update ("tick") hook method.
	 */
	public void update() {
	}

	@Override
	public Rectangle2D getCollisionBox() {
		return visible ? tf.getCollisionBox() : new Rectangle2D.Float(tf.getX(), tf.getY(), 0, 0);
	}
}