package de.amr.easy.game.entity;

import java.awt.Graphics2D;

import de.amr.easy.game.math.Vector2f;
import de.amr.easy.game.ui.sprites.Sprites;
import de.amr.easy.game.view.View;

/**
 * A game entity using sprites. Sprites are stored in a map and are accessed by their name. The
 * collision box of the entity may be different from the sprite size and has to be set explicitly.
 * 
 * @author Armin Reichert
 */
public abstract class GameEntityUsingSprites extends GameEntity implements View {

	public final Sprites sprites = new Sprites();
	private boolean visible = true;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void draw(Graphics2D g) {
		if (visible && sprites.current() != null) {
			Vector2f center = tf.getCenter();
			float dx = center.x - sprites.current().getWidth() / 2;
			float dy = center.y - sprites.current().getHeight() / 2;
			g.translate(dx, dy);
			g.rotate(tf.getRotation());
			sprites.current().draw(g);
			g.translate(-dx, -dy);
		}

	}
}