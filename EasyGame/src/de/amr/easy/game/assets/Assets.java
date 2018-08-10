package de.amr.easy.game.assets;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import de.amr.easy.game.Application;

/**
 * This class provides functionality to access assets like image, sounds, fonts etc.
 * 
 * @author Armin Reichert
 */
public class Assets {

	private Assets() {
	}

	private static final Map<String, Font> fontMap = new HashMap<>();
	private static final Map<String, Image> imageMap = new HashMap<>();
	private static final Map<String, Sound> soundMap = new HashMap<>();
	private static final Map<String, String> textMap = new HashMap<>();

	private static InputStream stream(String path) {
		InputStream stream = Assets.class.getClassLoader().getResourceAsStream(path);
		if (stream == null) {
			Application.logger.severe(String.format("Resource with assets path %s not found", path));
			throw new RuntimeException();
		}
		return stream;
	}

	/**
	 * Reads a text file from the given assets path.
	 * 
	 * @param path
	 *          relative path inside "assets" folder
	 * @return the text file content as a single string
	 */
	public static String readTextFile(String path) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream(path)))) {
			StringBuilder sb = new StringBuilder();
			for (String line; (line = reader.readLine()) != null;) {
				sb.append(line).append('\n');
			}
			return sb.toString();
		} catch (IOException e) {
			Application.logger.severe("Could not read text resource: " + path);
			throw new RuntimeException(e);
		}
	}

	private static Font readTrueTypeFont(String fontFilePath) {
		try (InputStream fontStream = stream(fontFilePath)) {
			return Font.createFont(Font.TRUETYPE_FONT, fontStream);
		} catch (Exception e) {
			Application.logger.severe("Could not read font: " + fontFilePath);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads an image from the given assets path.
	 * 
	 * @param path
	 *          relative path inside "assets" folder
	 * @return the image
	 */
	public static BufferedImage readImage(String path) {
		try (InputStream stream = stream(path)) {
			BufferedImage image = ImageIO.read(stream);
			if (image != null) {
				return createOptimizedCopy(image);
			}
			Application.logger.severe("Image resource not found: " + path);
			throw new IllegalArgumentException();
		} catch (IOException e) {
			Application.logger.severe("Could not read image resource: " + path);
			throw new RuntimeException(e);
		}
	}

	private static BufferedImage createOptimizedCopy(Image image) {
		GraphicsConfiguration conf = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage copy = conf.createCompatibleImage(image.getWidth(null), image.getHeight(null),
				Transparency.TRANSLUCENT);
		Graphics g = copy.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return copy;
	}
	
	/**
	 * Returns a scaled version of the given image as a buffered image.
	 * 
	 * @param image
	 *          an image
	 * @param width
	 *          the scaled width
	 * @param height
	 *          the scaled height
	 * @return the scaled image
	 */
	public static BufferedImage scaledImage(Image image, int width, int height) {
		Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return createOptimizedCopy(scaled);
	}

	/**
	 * Returns the names of all images accessed so far.
	 * 
	 * @return the image names
	 */
	public static Iterable<String> imageNames() {
		return imageMap.keySet();
	}

	/**
	 * Returns the names of all sounds accessed so far.
	 * 
	 * @return the sound names
	 */
	public static Iterable<String> soundNames() {
		return soundMap.keySet();
	}

	/**
	 * Returns the sound objects accessed so far.
	 * 
	 * @return the sound objects
	 */
	public static Stream<Sound> sounds() {
		return soundMap.values().stream();
	}

	/**
	 * Stores the given image under the given path name.
	 * 
	 * @param path
	 *          path names
	 * @param image
	 *          image
	 */
	public static void storeImage(String path, Image image) {
		if (imageMap.put(path, image) != null) {
			Application.logger.warning("Image with name: " + path + " has been replaced.");
		}
	}

	/**
	 * Stores the font with given name, size and style under the given key.
	 * 
	 * @param key
	 *          key under which the font my be accessed
	 * @param fontName
	 *          font name
	 * @param size
	 *          font size
	 * @param style
	 *          font style
	 * @return font as specified
	 */
	public static Font storeTrueTypeFont(String key, String fontName, int style, float size) {
		if (!fontMap.containsKey(key)) {
			Font font = readTrueTypeFont(fontName).deriveFont(style, size);
			fontMap.put(key, font);
		}
		return fontMap.get(key);
	}

	/**
	 * Stores the font derived from the given base font and given size and style under the given key.
	 * 
	 * @param key
	 *          key under which the font my be accessed
	 * @param font
	 *          font from which this font is derived
	 * @param size
	 *          font size
	 * @param style
	 *          font style
	 * @return derived font
	 */
	public static Font storeFont(String key, Font baseFont, int style, float size) {
		if (!fontMap.containsKey(key)) {
			Font font = baseFont.deriveFont(style, size);
			fontMap.put(key, font);
		}
		return fontMap.get(key);
	}

	/**
	 * Returns the font stored with the given key.
	 * 
	 * @param key
	 *          font key
	 * @return font as requested
	 */
	public static Font font(String key) {
		if (fontMap.containsKey(key)) {
			return fontMap.get(key);
		}
		throw new IllegalStateException("No font found with key: " + key);
	}

	/**
	 * Returns the image with the given path. If the image is requested for the first time, it is
	 * loaded from the specified path.
	 * 
	 * @param path
	 *          path under assets folder or key in assets map
	 * @return image as requested
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Image> T image(String path) {
		if (!imageMap.containsKey(path)) {
			imageMap.put(path, readImage(path));
		}
		return (T) imageMap.get(path);
	}

	/**
	 * Returns the sound with the given path.
	 * 
	 * @param path
	 *          path to sound file
	 * @return sound object
	 */
	public static Sound sound(String path) {
		if (soundMap.containsKey(path)) {
			return soundMap.get(path);
		}
		try (InputStream is = stream(path)) {
			AudioClip clip = new AudioClip(is);
			soundMap.put(path, clip);
			return clip;
		} catch (Exception e) {
			Application.logger.severe("Could not read sound resource from asset path: " + path);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the content of the text file under the specified path.
	 * 
	 * @param path
	 *          path to text file
	 * @return text file content as a single string
	 */
	public static String text(String path) {
		if (textMap.containsKey(path)) {
			return textMap.get(path);
		}
		String text = readTextFile(path);
		textMap.put(path, text);
		return text;
	}

	public static String overview() {
		StringBuilder s = new StringBuilder();
		String[] fontNames = fontMap.keySet().toArray(new String[fontMap.size()]);
		String[] imageNames = imageMap.keySet().toArray(new String[imageMap.size()]);
		String[] soundNames = soundMap.keySet().toArray(new String[soundMap.size()]);
		Arrays.sort(fontNames);
		Arrays.sort(imageNames);
		Arrays.sort(soundNames);
		s.append("\n-- Fonts:\n");
		for (String name : fontNames) {
			s.append(name).append(": ").append(font(name)).append("\n");
		}
		s.append("\n-- Images:\n");
		for (String name : imageNames) {
			Image image = image(name);
			s.append(name).append(": ").append(image.getWidth(null) + "x" + image.getHeight(null))
					.append("\n");
		}
		s.append("\n-- Sounds:\n");
		for (String name : soundNames) {
			Sound sound = sound(name);
			s.append(name).append(": ").append(sound.getClass().getSimpleName()).append("\n");
		}
		s.append("\n-- Texts:\n");
		textMap.entrySet().stream().forEach(entry -> {
			s.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		});
		return s.toString();
	}
}
