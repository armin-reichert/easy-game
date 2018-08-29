package de.amr.easy.game.entity;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import de.amr.easy.game.sprite.Sprite;
import de.amr.easy.game.view.View;
import de.amr.easy.game.view.ViewController;

public abstract class GameEntityUsingSprites extends GameEntity implements ViewController {

	private final Map<String, Sprite> spriteMap = new HashMap<>();
	private String currentSprite;

	public void addSprite(String name, Sprite sprite) {
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

	public void enableAnimation(boolean enable) {
		getSprites().forEach(sprite -> sprite.enableAnimation(enable));
	}

	@Override
	public View currentView() {
		return this;
	}

	@Override
	public void draw(Graphics2D g) {
		if (spriteMap.containsKey(currentSprite)) {
			Graphics2D pen = (Graphics2D) g.create();
			pen.translate(tf().getX(), tf().getY());
			pen.rotate(tf().getRotation());
			spriteMap.get(currentSprite).draw(pen);
			pen.dispose();
		}
	}
}