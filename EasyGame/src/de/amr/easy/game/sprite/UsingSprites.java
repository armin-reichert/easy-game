package de.amr.easy.game.sprite;

import java.util.stream.Stream;

public interface UsingSprites {

	Sprite currentSprite();

	Stream<Sprite> getSprites();

	default void enableAnimation(boolean enable) {
		getSprites().forEach(sprite -> sprite.enableAnimation(enable));
	}
}