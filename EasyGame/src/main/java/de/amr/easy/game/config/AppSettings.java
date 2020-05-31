package de.amr.easy.game.config;

import java.awt.Color;
import java.awt.DisplayMode;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * Application settings. Contains named properties and a generic map.
 * <p>
 * The named settings can be overwritten by corresponding command line parameters.
 * 
 * @author Armin Reichert
 */
public class AppSettings {

	private static class ColorConverter implements IStringConverter<Color> {

		@Override
		public Color convert(String rgb) {
			return Color.decode(rgb);
		}
	}

	private static class DisplayModeConverter implements IStringConverter<DisplayMode> {

		@Override
		public DisplayMode convert(String str) {
			String[] parts = str.split(",");
			if (parts.length != 3) {
				throw new ParameterException("Illegal display mode");
			}
			try {
				int width = Integer.parseInt(parts[0]);
				int height = Integer.parseInt(parts[1]);
				int bitDepth = Integer.parseInt(parts[2]);
				return new DisplayMode(width, height, bitDepth, DisplayMode.REFRESH_RATE_UNKNOWN);
			} catch (Exception e) {
				throw new ParameterException(e);
			}
		}
	}

	private final Map<String, Object> settings = new HashMap<>();

	// Predefined properties

	@Parameter(names = { "-help", "-usage" }, help = true, description = "list all parameters and exit")
	public boolean help;

	/** The application title. */
	@Parameter(names = { "-title" }, description = "application title")
	public String title = "My Application!";

	/**
	 * If <code>true</code>, additional info (frame rate, resolution) gets displayed in title.
	 */
	@Parameter(names = { "-titleExtended" }, description = "extended application title")
	public boolean titleExtended;

	/** Frame rate of clock. */
	@Parameter(names = { "-fps" }, description = "Frames/sec")
	public int fps = 60;

	/** The unscaled width of application area in pixel. */
	@Parameter(names = { "-width" }, description = "application width (unscaled)")
	public int width = 640;

	/** The unscaled height of the application area in pixel. */
	@Parameter(names = { "-height" }, description = "application height (unscaled)")
	public int height = 480;

	/** The scale factor for the screen. */
	@Parameter(names = { "-scale" }, description = "application scaling factor")
	public float scale = 1f;

	/** If <code>true</code>, the application starts in full-screen mode. */
	@Parameter(names = { "-fullScreenOnStart" }, description = "start app in fullscreen mode")
	public boolean fullScreenOnStart = false;

	/** The full-screen mode (resolution, depth), see {@link FullScreenMode}. */
	@Parameter(names = {
			"-fullScreenMode" }, converter = DisplayModeConverter.class, description = "fullscreen display mode")
	public DisplayMode fullScreenMode = null;

	/** If <code>true</code>, the cursor is visible in full-screen mode. */
	@Parameter(names = { "-fullScreenCursor" }, description = "cursor visible in fullscreen mode")
	public boolean fullScreenCursor = false;

	/** The background color of the application. */
	@Parameter(names = { "-bgColor" }, converter = ColorConverter.class, description = "application background")
	public Color bgColor = Color.BLACK;

	/** The volume (in percent) of the background music. */
	@Parameter(names = { "-bgMusicVolume" }, description = "background music volume in percent")
	public int bgMusicVolume = 75;

	/**
	 * @return stream of all keys of the generic settings
	 */
	public Stream<String> keys() {
		return settings.keySet().stream();
	}

	/**
	 * Sets a property value.
	 * 
	 * @param key   property name
	 * @param value property value
	 */
	public void set(String key, Object value) {
		settings.put(key, value);
	}

	/**
	 * Returns a (typed) property value. Value is cast to type of variable it is assigned to.
	 * 
	 * @param key property name
	 * @return property value
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) settings.get(key);
	}

	/**
	 * Returns a property value as string.
	 * 
	 * @param key property name
	 * @return property value as string
	 */
	public String getAsString(String key) {
		return String.valueOf(settings.get(key));
	}

	/**
	 * Returns a property value as boolean value. If property is undefined, returns <code>false</code>.
	 * 
	 * @param key property name
	 * @return property value as boolean
	 */
	public boolean getAsBoolean(String key) {
		return settings.containsKey(key) ? (Boolean) get(key) : false;
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
}