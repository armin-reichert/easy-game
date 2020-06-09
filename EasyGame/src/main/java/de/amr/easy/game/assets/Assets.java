package de.amr.easy.game.assets;

import static de.amr.easy.game.Application.LOGGER;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
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
import java.util.Objects;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class provides access to assets like images, sounds, fonts, texts etc. When an asset is
 * accessed for the first time, it is stored inside a cache and the cached value is returned
 * afterwards.
 * 
 * @author Armin Reichert
 */
public class Assets {

	// caches
	private static final Map<String, Font> fontCache = new HashMap<>();
	private static final Map<String, BufferedImage> imageCache = new HashMap<>();
	private static final Map<String, SoundClip> soundCache = new HashMap<>();
	private static final Map<String, String> textCache = new HashMap<>();

	/**
	 * Returns a stream to the asset at the given path.
	 * 
	 * @param path path inside assets folder
	 * @return inout stream for reading asset
	 */
	public static InputStream stream(String path) {
		path = Objects.requireNonNull(path);
		InputStream stream = Assets.class.getClassLoader().getResourceAsStream(path);
		if (stream == null) {
			throw new AssetException(String.format("Asset with path '%s' not found", path));
		}
		return stream;
	}

	/**
	 * Reads a text file from the given assets path.
	 * 
	 * @param path relative path inside "assets" folder
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
			throw new AssetException(String.format("Text file with path '%s' could not be read", path));
		}
	}

	/**
	 * Reads a true-type font from the given assets path.
	 * 
	 * @param path relative path inside "assets" folder
	 * @return the font
	 */
	public static Font readTrueTypeFont(String path) {
		try (InputStream is = stream(path)) {
			return Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (IOException e) {
			throw new AssetException(String.format("True-type font with path '%s' could not be read", path));
		} catch (FontFormatException x) {
			throw new AssetException(String.format("Font with path '%s' has wrong format", path));
		}
	}

	/**
	 * Reads an image from the given assets path.
	 * 
	 * @param path relative path inside "assets" folder
	 * @return the image
	 */
	public static BufferedImage readImage(String path) {
		try (InputStream is = stream(path)) {
			BufferedImage image = ImageIO.read(is);
			if (image == null) {
				throw new AssetException(String.format("Image with path '%s' not found", path));
			}
			return image;
		} catch (IOException e) {
			throw new RuntimeException(String.format("Image with path '%s' could not be read", path));
		}
	}

	/**
	 * Returns a scaled version of the given image as a buffered image.
	 * 
	 * @param image  an image
	 * @param width  the scaled width
	 * @param height the scaled height
	 * @return the scaled image of transparency type TRANSLUCENT
	 */
	public static BufferedImage scaledImage(Image image, int width, int height) {
		Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage buffered = createBufferedImage(width, height, Transparency.TRANSLUCENT);
		Graphics2D g = buffered.createGraphics();
		g.drawImage(scaled, 0, 0, null);
		g.dispose();
		return buffered;
	}

	/**
	 * Creates a buffered image of the given dimensions and transparency.
	 * 
	 * @param width        image width in pixels
	 * @param height       image height in pixels
	 * @param transparency transparency, see {@link Transparency}
	 * @return a buffered image
	 */
	public static BufferedImage createBufferedImage(int width, int height, int transparency) {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		return gc.createCompatibleImage(width, height, transparency);
	}

	/**
	 * Returns the names of all stored images.
	 * 
	 * @return the image names
	 */
	public static Iterable<String> imageNames() {
		return imageCache.keySet();
	}

	/**
	 * Returns a stream of all stored images.
	 * 
	 * @return stream of all stored images
	 */
	public static Stream<BufferedImage> images() {
		return imageCache.values().stream();
	}

	/**
	 * Returns the names of all stored sounds.
	 * 
	 * @return the sound names
	 */
	public static Iterable<String> soundNames() {
		return soundCache.keySet();
	}

	/**
	 * Returns a stream of all stored sound clips.
	 * 
	 * @return the sound clips
	 */
	public static Stream<SoundClip> sounds() {
		return soundCache.values().stream();
	}

	/**
	 * Stores the given image under the given path name.
	 * 
	 * @param path  path names
	 * @param image image
	 */
	public static void storeImage(String path, BufferedImage image) {
		if (imageCache.put(path, image) != null) {
			LOGGER.warning(String.format("Assets: Image '%s' has been replaced.", path));
		}
	}

	/**
	 * Stores the font with given name, size and style under the given key.
	 * 
	 * @param key      key under which the font my be accessed
	 * @param fontName font name
	 * @param size     font size
	 * @param style    font style
	 * @return font as specified
	 */
	public static Font storeTrueTypeFont(String key, String fontName, int style, float size) {
		if (!fontCache.containsKey(key)) {
			Font font = readTrueTypeFont(fontName).deriveFont(style, size);
			fontCache.put(key, font);
		}
		return fontCache.get(key);
	}

	/**
	 * Stores the font derived from the given base font and given size and style under the given key.
	 * 
	 * @param key   key under which the font my be accessed
	 * @param font  font from which this font is derived
	 * @param size  font size
	 * @param style font style
	 * @return derived font
	 */
	public static Font storeFont(String key, Font baseFont, int style, float size) {
		if (!fontCache.containsKey(key)) {
			Font font = baseFont.deriveFont(style, size);
			fontCache.put(key, font);
		}
		return fontCache.get(key);
	}

	/**
	 * Returns the font stored with the given key.
	 * 
	 * @param key font key
	 * @return font as requested
	 */
	public static Font font(String key) {
		if (fontCache.containsKey(key)) {
			return fontCache.get(key);
		}
		throw new AssetException("No font found with key: " + key);
	}

	/**
	 * Returns the image with the given path. If the image is requested for the first time, it is loaded
	 * from the specified path.
	 * 
	 * @param path path under assets folder or key in assets map
	 * @return image as requested
	 */
	public static BufferedImage image(String path) {
		if (!imageCache.containsKey(path)) {
			imageCache.put(path, readImage(path));
		}
		return imageCache.get(path);
	}

	/**
	 * Returns the sound clip with the given path.
	 * 
	 * @param path path to sound file
	 * @return sound object
	 */
	public static SoundClip sound(String path) {
		if (soundCache.containsKey(path)) {
			return soundCache.get(path);
		}
		try (InputStream is = stream(path)) {
			SoundClip sound = new SoundClip(is);
			soundCache.put(path, sound);
			return sound;
		} catch (IOException e) {
			throw new AssetException(String.format("Sound file at path '%s' could not be opened", path), e);
		} catch (LineUnavailableException e) {
			throw new AssetException(String.format("Sound file at path '%s' is not available", path), e);
		} catch (UnsupportedAudioFileException e) {
			throw new AssetException(String.format("Sound file at path '%s' has unsupported aufdio format", path), e);
		}
	}

	/**
	 * Returns the content of the text file under the specified path.
	 * 
	 * @param path path to text file
	 * @return text file content as a single string
	 */
	public static String text(String path) {
		if (textCache.containsKey(path)) {
			return textCache.get(path);
		}
		String text = readTextFile(path);
		textCache.put(path, text);
		return text;
	}

	/**
	 * Table of content.
	 * 
	 * @return textual description of stored assets
	 */
	public static String toc() {
		StringBuilder s = new StringBuilder();
		String[] fontNames = fontCache.keySet().toArray(new String[fontCache.size()]);
		String[] imageNames = imageCache.keySet().toArray(new String[imageCache.size()]);
		String[] soundNames = soundCache.keySet().toArray(new String[soundCache.size()]);
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
			s.append(name).append(": ").append(image.getWidth(null) + "x" + image.getHeight(null)).append("\n");
		}
		s.append("\n-- Sounds:\n");
		for (String name : soundNames) {
			SoundClip sound = sound(name);
			s.append(name).append(": ").append(sound.getClass().getSimpleName()).append("\n");
		}
		s.append("\n-- Texts:\n");
		textCache.entrySet().stream().forEach(entry -> {
			s.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		});
		return s.toString();
	}
}