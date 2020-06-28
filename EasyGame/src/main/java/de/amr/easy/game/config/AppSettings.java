package de.amr.easy.game.config;

import static de.amr.easy.game.Application.loginfo;

import java.awt.DisplayMode;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.beust.jcommander.Parameter;

/**
 * Application settings. Contains predefined properties and a generic map. The predefined settings
 * can be overwritten by corresponding command-line parameters.
 * 
 * @author Armin Reichert
 */
public class AppSettings {

	private final Map<String, Object> userDefined = new HashMap<>();

	// Predefined properties

	@Parameter(names = { "-help", "-usage" }, help = true, description = "list all parameters and exit")
	public boolean help;

	/** The application title. */
	@Parameter(names = { "-title" }, description = "application title")
	public String title = "My Application!";

	@Parameter(names = { "-smoothRendering" }, description = "Use antialiased rendering")
	public boolean smoothRendering = false;

	@Parameter(names = { "-titleExtended" }, description = "Application title shows frame rate and screen resolution")
	public boolean titleExtended;

	@Parameter(names = { "-fps" }, description = "Clock speed (ticks/sec)")
	public int fps = 60;

	@Parameter(names = { "-width" }, description = "Application window width (unscaled)")
	public int width = 640;

	@Parameter(names = { "-height" }, description = "Application window height (unscaled)")
	public int height = 480;

	@Parameter(names = { "-scale" }, description = "Application window scaling factor")
	public float scale = 1f;

	@Parameter(names = { "-fullScreenOnStart", "-fullScreen" }, description = "Start in full-screen mode")
	public boolean fullScreen = false;

	@Parameter(names = { "-fullScreenCursor" }, description = "Cursor visible in fullscreen mode")
	public boolean fullScreenCursor = false;

	@Parameter(names = {
			"-fullScreenMode" }, converter = DisplayModeConverter.class, description = "Full-screen display mode e.g. 800,600,16")
	public DisplayMode fullScreenMode = null;

	@Parameter(names = { "-muted" }, description = "Application starts with sound muted")
	public boolean muted = false;

	/**
	 * @return stream of all keys of the user-defined settings
	 */
	public Stream<String> keys() {
		return userDefined.keySet().stream();
	}

	/**
	 * Sets a user-defined setting value.
	 * 
	 * @param name  setting name
	 * @param value setting value
	 */
	public void set(String name, Object value) {
		userDefined.put(name, value);
	}

	/**
	 * Returns a (typed) property value. Value is cast to type of variable it is assigned to.
	 * 
	 * @param key property name
	 * @return property value
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) userDefined.get(key);
	}

	/**
	 * Returns a property value as string.
	 * 
	 * @param key property name
	 * @return property value as string
	 */
	public String getAsString(String key) {
		return String.valueOf(userDefined.get(key));
	}

	/**
	 * Returns a property value as boolean value. If property is undefined, returns <code>false</code>.
	 * 
	 * @param key property name
	 * @return property value as boolean
	 */
	public boolean getAsBoolean(String key) {
		return userDefined.containsKey(key) ? (Boolean) get(key) : false;
	}

	/**
	 * Returns a property value as an integer.
	 * 
	 * @param key property name
	 * @return property value as integer
	 */
	public int getAsInt(String key) {
		return (int) get(key);
	}

	/**
	 * Returns a property value as a float..
	 * 
	 * @param key property name
	 * @return property value as float
	 */
	public float getAsFloat(String key) {
		return (float) get(key);
	}

	/**
	 * Prints the current settings to the application log.
	 */
	public void print() {
		printValue("Title", "%s", title);
		printValue("Width", "%d", width);
		printValue("Height", "%d", height);
		printValue("Scaling", "%.2f", scale);
		printValue("Full-screen mode", "%s", fullScreen);
		printValue("Full-screen resolution", "%s", fullScreenMode);
		printValue("Framerate (ticks/sec)", "%d", fps);
		printValue("Smooth rendering", "%s", smoothRendering);
		printValue("Muted", "%s", muted);
	}

	public void printValue(String name, String format, Object value) {
		loginfo("\t%-25s %s", name + ":", String.format(format, value));
	}
}