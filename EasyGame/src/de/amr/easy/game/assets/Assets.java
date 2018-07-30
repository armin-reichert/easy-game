package de.amr.easy.game.assets;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
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
public enum Assets {

	/** The singleton object of this class. */
	OBJ;

	private final Map<String, Font> fonts = new HashMap<>();
	private final Map<String, Image> images = new HashMap<>();
	private final Map<String, Sound> sounds = new HashMap<>();
	private final Map<String, String> texts = new HashMap<>();

	private static InputStream toInputStream(String path) {
		InputStream is = Assets.class.getClassLoader().getResourceAsStream(path);
		if (is == null) {
			Application.LOG.severe("Could not access resource with assets path: " + path);
			throw new RuntimeException();
		}
		return is;
	}

	/**
	 * Reads a text file from the given assets path.
	 * 
	 * @param path
	 *          relative path inside "assets" folder
	 * @return the text file content as a single string
	 */
	public static String readTextFile(String path) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(toInputStream(path)));) {
			StringBuilder sb = new StringBuilder();
			for (String line; (line = reader.readLine()) != null;) {
				sb.append(line).append('\n');
			}
			return sb.toString();
		} catch (IOException e) {
			Application.LOG.severe("Could not read text resource from path: " + path);
			throw new RuntimeException(e);
		}
	}

	private static Font readTrueTypeFont(String path) {
		try (InputStream fontStream = toInputStream(path)) {
			return Font.createFont(Font.TRUETYPE_FONT, fontStream);
		} catch (Exception e) {
			Application.LOG.severe("Could not read font from asset path: " + path);
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
		try (InputStream is = toInputStream(path)) {
			BufferedImage image = ImageIO.read(is);
			if (image != null) {
				return image;
			}
			Application.LOG.severe("No image resource found at asset path: " + path);
			throw new IllegalArgumentException();
		} catch (IOException e) {
			Application.LOG.severe("Could not read image resource from asset path: " + path);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the names of all images accessed so far.
	 * 
	 * @return the image names
	 */
	public static Iterable<String> imageNames() {
		return OBJ.images.keySet();
	}

	/**
	 * Returns the names of all sounds accessed so far.
	 * 
	 * @return the sound names
	 */
	public static Iterable<String> soundNames() {
		return OBJ.sounds.keySet();
	}

	/**
	 * Returns the sound objects accessed so far.
	 * 
	 * @return the sound objects
	 */
	public static Stream<Sound> sounds() {
		return OBJ.sounds.values().stream();
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
		if (OBJ.images.put(path, image) != null) {
			Application.LOG.warning("Image with name: " + path + " has been replaced.");
		}
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
		BufferedImage copy = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = copy.getGraphics();
		g.drawImage(scaled, 0, 0, null);
		g.dispose();
		return copy;
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
		if (!OBJ.fonts.containsKey(key)) {
			Font font = readTrueTypeFont(fontName).deriveFont(style, size);
			OBJ.fonts.put(key, font);
		}
		return OBJ.fonts.get(key);
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
		if (!OBJ.fonts.containsKey(key)) {
			Font font = baseFont.deriveFont(style, size);
			OBJ.fonts.put(key, font);
		}
		return OBJ.fonts.get(key);
	}

	/**
	 * Returns the font stored with the given key.
	 * 
	 * @param key
	 *          font key
	 * @return font as requested
	 */
	public static Font font(String key) {
		if (OBJ.fonts.containsKey(key)) {
			return OBJ.fonts.get(key);
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
		if (!OBJ.images.containsKey(path)) {
			OBJ.images.put(path, readImage(path));
		}
		return (T) OBJ.images.get(path);
	}

	/**
	 * Returns the sound with the given path.
	 * 
	 * @param path
	 *          path to sound file
	 * @return sound object
	 */
	public static Sound sound(String path) {
		if (OBJ.sounds.containsKey(path)) {
			return OBJ.sounds.get(path);
		}
		try (InputStream is = toInputStream(path)) {
			AudioClip clip = new AudioClip(is);
			OBJ.sounds.put(path, clip);
			return clip;
		} catch (Exception e) {
			Application.LOG.severe("Could not read sound resource from asset path: " + path);
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
		if (OBJ.texts.containsKey(path)) {
			return OBJ.texts.get(path);
		}
		String text = readTextFile(path);
		OBJ.texts.put(path, text);
		return text;
	}

	public static String overview() {
		StringBuilder s = new StringBuilder();
		String[] fontNames = OBJ.fonts.keySet().toArray(new String[OBJ.fonts.size()]);
		String[] imageNames = OBJ.images.keySet().toArray(new String[OBJ.images.size()]);
		String[] soundNames = OBJ.sounds.keySet().toArray(new String[OBJ.sounds.size()]);
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
		OBJ.texts.entrySet().stream().forEach(entry -> {
			s.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		});
		return s.toString();
	}
}
