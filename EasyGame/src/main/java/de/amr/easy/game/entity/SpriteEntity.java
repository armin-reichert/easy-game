package de.amr.easy.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;

import de.amr.easy.game.math.Vector2f;
import de.amr.easy.game.ui.sprites.Sprite;
import de.amr.easy.game.ui.sprites.SpriteMap;
import de.amr.easy.game.view.View;

/**
 * A game entity using sprites. Sprites are stored in a map and can be referenced by name. The
 * collision box of the entity may be different from the sprite size and has to be set explicitly.
 * 
 * @author Armin Reichert
 */
public abstract class SpriteEntity extends Entity implements View {

	/** The sprite map for this entity. */
	public final SpriteMap sprites = new SpriteMap();

	/** If <code>true</code> the collision box is drawn (for debugging). */
	public boolean showCollisionBox = false;

	@Override
	public void draw(Graphics2D g) {
		if (sprites.current().isPresent() && isVisible()) {
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
}