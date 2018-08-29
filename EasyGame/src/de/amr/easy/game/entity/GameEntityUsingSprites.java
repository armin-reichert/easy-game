package de.amr.easy.game.entity;

import java.awt.Graphics2D;
import java.util.stream.Stream;

import de.amr.easy.game.sprite.Sprite;

public abstract class GameEntityUsingSprites extends GameEntity {
	
	public abstract Sprite currentSprite();

	public abstract Stream<Sprite> getSprites();

	public void enableAnimation(boolean enable) {
		getSprites().forEach(sprite -> sprite.enableAnimation(enable));
	}

	@Override
	public void draw(Graphics2D g) {
		Graphics2D pen = (Graphics2D) g.create();
		pen.translate(tf().getX(), tf().getY());
		pen.rotate(tf().getRotation());
		currentSprite().draw(pen);
		pen.dispose();
	}
}