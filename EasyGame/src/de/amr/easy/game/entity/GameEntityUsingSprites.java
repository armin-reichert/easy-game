package de.amr.easy.game.entity;

import java.awt.Graphics2D;

import de.amr.easy.game.sprite.UsingSprites;

public abstract class GameEntityUsingSprites extends GameEntity implements UsingSprites {

	@Override
	public void draw(Graphics2D g) {
		Graphics2D pen = (Graphics2D) g.create();
		pen.translate(tf.getX(), tf.getY());
		pen.rotate(tf.getRotation());
		currentSprite().draw(pen);
		pen.dispose();
	}
}