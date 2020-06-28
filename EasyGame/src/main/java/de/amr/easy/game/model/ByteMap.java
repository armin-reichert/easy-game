package de.amr.easy.game.model;

/**
 * Map represented by a 2D byte array.
 * 
 * @author Armin Reichert
 */
public abstract class ByteMap {

	protected byte[][] data;

	public final int numCols;
	public final int numRows;

	public ByteMap(byte[][] data) {
		this.data = data;
		numRows = data.length;
		numCols = data[0].length;
	}

	public boolean contains(int row, int col) {
		return 0 <= row && row < numRows && 0 <= col && col < numCols;
	}

	public boolean is(int row, int col, byte bit) {
		return (data[row][col] & (1 << bit)) != 0;
	}

	public void set0(int row, int col, byte bit) {
		data[row][col] &= ~(1 << bit);
	}

	public void set1(int row, int col, byte bit) {
		data[row][col] |= (1 << bit);
	}
}