package de.amr.easy.game.entity;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import de.amr.easy.game.sprite.Sprite;
import de.amr.easy.game.view.View;

/**
 * A game entity using sprites. Sprites are stored in a map and are accessed by their name. The
 * collision box of the entity may be different from the sprite size and has to be set explicitly.
 * 
 * @author Armin Reichert
 */
public abstract class GameEntityUsingSprites extends GameEntity implements View {

	private final Map<String, Sprite> spriteMap = new HashMap<>();
	private String currentSprite;

	public void setSprite(String name, Sprite sprite) {
		spriteMap.put(name, sprite);
	}

	public void removeSprite(String name) {
		spriteMap.remove(name);
	}

	public Sprite getSprite(String name) {
		return spriteMap.get(name);
	}

	public void setCurrentSprite(String name) {
		currentSprite = name;
	}

	public final Sprite currentSprite() {
		return spriteMap.get(currentSprite);
	}

	public final Stream<Sprite> getSprites() {
		return spriteMap.values().stream();
	}

	public void enableSprites(boolean enable) {
		getSprites().forEach(sprite -> sprite.enableAnimation(enable));
	}

	@Override
	public void draw(Graphics2D g) {
		if (spriteMap.containsKey(currentSprite)) {
			Graphics2D pen = (Graphics2D) g.create();
			pen.translate(tf.getX(), tf.getY());
			pen.rotate(tf.getRotation());
			spriteMap.get(currentSprite).draw(pen);
			pen.dispose();
		}
	}
}