package de.amr.easy.game.entity;

import java.awt.Graphics2D;

import de.amr.easy.game.sprite.Sprites;
import de.amr.easy.game.view.View;

/**
 * A game entity using sprites. Sprites are stored in a map and are accessed by their name. The
 * collision box of the entity may be different from the sprite size and has to be set explicitly.
 * 
 * @author Armin Reichert
 */
public abstract class GameEntityUsingSprites extends GameEntity implements View {
	
	public final Sprites sprites = new Sprites();

	@Override
	public void draw(Graphics2D g) {
		if (sprites.current() != null) {
			Graphics2D gg = (Graphics2D) g.create();
			gg.translate(tf.getX(), tf.getY());
			gg.rotate(tf.getRotation());
			sprites.current().draw(gg);
			gg.dispose();
		}
	}
}