package de.amr.easy.game.ui.sprites;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import de.amr.easy.game.assets.Assets;

public class Spritesheet {

	protected final BufferedImage image;
	protected final int tileSize;

	public Spritesheet(String path, int tileSize) {
		this(Assets.readImage(path), tileSize);
	}

	public Spritesheet(BufferedImage image, int tileSize) {
		this.image = image;
		this.tileSize = tileSize;
	}

	public BufferedImage region(int x, int y, int w, int h) {
		return image.getSubimage(x, y, w, h);
	}

	public BufferedImage tile(int col, int row) {
		return region(col * tileSize, row * tileSize, tileSize, tileSize);
	}

	public BufferedImage[] horizontalTiles(int n, int col, int row) {
		return IntStream.range(0, n).mapToObj(i -> tile(col + i, row)).toArray(BufferedImage[]::new);
	}

	public BufferedImage exchangeColor(BufferedImage img, int oldColorRGB, int newColorRGB) {
		BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		Graphics2D g = copy.createGraphics();
		g.drawImage(img, 0, 0, null);
		for (int x = 0; x < copy.getWidth(); ++x) {
			for (int y = 0; y < copy.getHeight(); ++y) {
				if (copy.getRGB(x, y) == oldColorRGB) {
					copy.setRGB(x, y, newColorRGB);
				}
			}
		}
		g.dispose();
		return copy;
	}
}