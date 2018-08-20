package de.amr.easy.game.config;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import de.amr.easy.game.ui.FullScreen;

/**
 * Application settings. Contains predefined properties and a generic map.
 * 
 * @author Armin Reichert
 */
public class AppSettings {

	private final Map<String, Object> settings = new HashMap<>();

	// Predefined properties

	/** The application title. */
	public String title = "My Application!";

	/** The unscaled width of application area in pixel. */
	public int width = 600;

	/** The unscaled height of the application area in pixel. */
	public int height = 400;

	/** The scale factor for the screen. */
	public float scale = 1f;

	/** If <code>true</code>, the application starts in full-screen mode. */
	public boolean fullScreenOnStart = false;

	/** The full-screen mode (resolution, depth), see {@link FullScreen}. */
	public FullScreen fullScreenMode = FullScreen.Mode(800, 600, 32);

	/** The background color of the application. */
	public Color bgColor = Color.BLACK;

	public Stream<String> keys() {
		return settings.keySet().stream();
	}

	/**
	 * Sets a property value.
	 * 
	 * @param key
	 *                property name
	 * @param value
	 *                property value
	 */
	public void set(String key, Object value) {
		settings.put(key, value);
	}

	/**
	 * Returns a (typed) property value. Value is cast to type of variable it is assigned to.
	 * 
	 * @param key
	 *              property name
	 * @return property value
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) settings.get(key);
	}

	/**
	 * Returns a property value as string.
	 * 
	 * @param key
	 *              property name
	 * @return property value as string
	 */
	public String getAsString(String key) {
		return String.valueOf(settings.get(key));
	}

	/**
	 * Returns a property value as boolean value. If property is undefined, returns <code>false</code>.
	 * 
	 * @param key
	 *              property name
	 * @return property value as boolean
	 */
	public boolean getAsBoolean(String key) {
		return settings.containsKey(key) ? (Boolean) get(key) : false;
	}

	/**
	 * Returns a property value as an integer.
	 * 
	 * @param key
	 *              property name
	 * @return property value as integer
	 */
	public int getAsInt(String key) {
		return (int) get(key);
	}

	/**
	 * Returns a property value as a float..
	 * 
	 * @param key
	 *              property name
	 * @return property value as float
	 */
	public float getAsFloat(String key) {
		return (float) get(key);
	}
}