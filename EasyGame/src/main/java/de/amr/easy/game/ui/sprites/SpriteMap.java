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

	private Map<String, Sprite> spriteByName = Collections.emptyMap();

	private String selectedKey;

	@Override
	public Iterator<Sprite> iterator() {
		return spriteByName.values().iterator();
	}

	/**
	 * @param name
	 *               sprite name
	 * @return sprite with given name
	 */
	public Sprite get(String name) {
		return spriteByName.get(name);
	}

	/**
	 * Stores the given sprite under the given name.
	 * 
	 * @param name
	 *                 sprite name
	 * @param sprite
	 *                 a sprite
	 */
	public void set(String name, Sprite sprite) {
		if (spriteByName == Collections.EMPTY_MAP) {
			spriteByName = new HashMap<>();
		}
		spriteByName.put(name, sprite);
	}

	/**
	 * Removes the sprite with the given name.
	 * 
	 * @param name
	 *               sprite name
	 */
	public void remove(String name) {
		spriteByName.remove(name);
	}

	/**
	 * Tells if a sprite with the given name exists.
	 * 
	 * @param name
	 *               sprite
	 * @return {@code true} if sprite with given name exists
	 */
	public boolean exists(String name) {
		return spriteByName.containsKey(name);
	}

	/**
	 * Selects the sprite with the given name.
	 * 
	 * @param name
	 *               sprite name
	 */
	public void select(String name) {
		selectedKey = name;
	}

	/**
	 * @return the currently selected sprite
	 */
	public final Optional<Sprite> current() {
		return Optional.ofNullable(spriteByName.get(selectedKey));
	}

	/**
	 * 
	 * @return stream of all sprites stored in this map
	 */
	public final Stream<Sprite> stream() {
		return spriteByName.values().stream();
	}

	/**
	 * Enables/disables the animation for all sprites in this map.
	 * 
	 * @param enable
	 *                 enabling state
	 */
	public void enableAnimation(boolean enable) {
		forEach(sprite -> sprite.enableAnimation(enable));
	}
}