package de.amr.easy.game.ui.sprites;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Sprite map with selection.
 * 
 * @author Armin Reichert
 */
public class SpriteMap implements Iterable<Sprite> {

	private Map<String, Sprite> spritesByKey = Collections.emptyMap();
	private String selectedKey;

	@Override
	public Iterator<Sprite> iterator() {
		return spritesByKey.values().iterator();
	}

	public Stream<Sprite> values() {
		return spritesByKey.values().stream();
	}

	/**
	 * @param spriteKey sprite key
	 * @return sprite with given key or {@code null}
	 */
	public Sprite get(String spriteKey) {
		return spritesByKey.get(spriteKey);
	}

	/**
	 * Stores the given sprite under the given key.
	 * 
	 * @param spriteKey key for accessing sprite
	 * @param sprite    a sprite
	 */
	public void set(String spriteKey, Sprite sprite) {
		if (spriteKey == null) {
			throw new IllegalArgumentException("Sprite key must not be NULL");
		}
		if (sprite == null) {
			throw new IllegalArgumentException("Sprite must not be NULL");
		}
		if (spritesByKey == Collections.EMPTY_MAP) {
			spritesByKey = new HashMap<>();
		}
		spritesByKey.put(spriteKey, sprite);
	}

	/**
	 * Removes the sprite with the given name.
	 * 
	 * @param spriteKey key for accessing sprite
	 */
	public void remove(String spriteKey) {
		spritesByKey.remove(spriteKey);
	}

	/**
	 * Tells if a sprite with the given key exists.
	 * 
	 * @param spriteKey key for accessing sprite
	 * @return {@code true} if sprite with given key exists in this map
	 */
	public boolean exists(String spriteKey) {
		return spritesByKey.containsKey(spriteKey);
	}

	/**
	 * Selects the sprite with the given key.
	 * 
	 * @param spriteKey key for accessing sprite
	 */
	public Optional<Sprite> select(String spriteKey) {
		selectedKey = spriteKey;
		return current();
	}

	/**
	 * @return currently selected sprite key
	 */
	public String selectedKey() {
		return selectedKey;
	}

	/**
	 * @return the currently selected sprite
	 */
	public final Optional<Sprite> current() {
		return Optional.ofNullable(spritesByKey.get(selectedKey));
	}
}