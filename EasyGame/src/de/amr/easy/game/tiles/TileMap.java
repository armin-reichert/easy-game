package de.amr.easy.game.tiles;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class TileMap {

	private final int tileSize;
	private final BufferedImage[][] tiles;

	public TileMap(BufferedImage image, int tileSize) {
		this.tileSize = tileSize;
		int numCols = image.getWidth() / tileSize;
		int numRows = image.getHeight() / tileSize;
		tiles = new BufferedImage[numRows][numCols];
		for (int row = 0; row < numRows; ++row) {
			for (int col = 0; col < numCols; ++col) {
				tiles[row][col] = image.getSubimage(col * tileSize, row * tileSize, tileSize, tileSize);
			}
		}
	}

	public int getTileSize() {
		return tileSize;
	}

	public Image tile(int row, int col) {
		return tiles[row][col];
	}
}
