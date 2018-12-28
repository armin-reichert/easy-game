package de.amr.easy.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;

import de.amr.easy.game.math.Vector2f;
import de.amr.easy.game.ui.sprites.SpriteMap;
import de.amr.easy.game.view.View;

/**
 * A game entity using sprites. Sprites are stored in a map and accessible by name. The collision
 * box of the entity may be different from the sprite size and has to be set explicitly.
 * 
 * @author Armin Reichert
 */
public abstract class SpriteEntity extends Entity implements View {

	/** The sprite map for this entity. */
	public final SpriteMap sprites = new SpriteMap();

	public boolean debug_draw = false;

	@Override
	public void draw(Graphics2D g) {
		if (isVisible() && sprites.current() != null) {
			if (debug_draw) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.translate(tf.getX(), tf.getY());
				g2.setColor(Color.RED);
				g2.drawRect(0, 0, tf.getWidth(), tf.getHeight());
				g2.dispose();
			}
			Vector2f center = tf.getCenter();
			float dx = center.x - sprites.current().getWidth() / 2;
			float dy = center.y - sprites.current().getHeight() / 2;
			Graphics2D g2 = (Graphics2D) g.create();
			g2.translate(dx, dy);
			g2.rotate(tf.getRotation());
			sprites.current().draw(g2);
			g2.dispose();
		}
	}
}