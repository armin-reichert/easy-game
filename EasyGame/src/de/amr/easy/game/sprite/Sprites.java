package de.amr.easy.game.sprite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Sprite map with selection.
 * 
 * @author Armin Reichert
 */
public class Sprites implements Iterable<Sprite> {

	private final Map<String, Sprite> spriteMap = new HashMap<>();
	private String selectedKey;

	@Override
	public Iterator<Sprite> iterator() {
		return spriteMap.values().iterator();
	}

	public Sprite get(String name) {
		return spriteMap.get(name);
	}

	public void set(String name, Sprite sprite) {
		spriteMap.put(name, sprite);
	}

	public void remove(String name) {
		spriteMap.remove(name);
	}

	public boolean exists(String name) {
		return spriteMap.containsKey(name);
	}

	public void select(String name) {
		selectedKey = name;
	}

	public final Sprite current() {
		return spriteMap.get(selectedKey);
	}

	public final Stream<Sprite> stream() {
		return spriteMap.values().stream();
	}

	public void enableAnimation(boolean enable) {
		forEach(sprite -> sprite.enableAnimation(enable));
	}
}