package de.amr.easy.game.math;

import static java.lang.Math.acos;
import static java.lang.Math.toDegrees;

/**
 * Immutable 2D vector with float precision.
 * 
 * @author Armin Reichert
 */
public class Vector2f {

	/** The x-coordinate. */
	public final float x;

	/** The y-coordinate. */
	public final float y;

	private Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a vector and make null vector unique.
	 */
	private static Vector2f vector(float x, float y) {
		return x == 0 && y == 0 ? NULL : new Vector2f(x, y);
	}

	/**
	 * The NULL vector. It is safe to compare using <code>==</code>.
	 */
	public static final Vector2f NULL = new Vector2f(0, 0);

	/**
	 * Creates a vector with given x and y coordinates.
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return vector with given coordinates
	 */
	public static Vector2f of(float x, float y) {
		return vector(x, y);
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return sum of the two vectors
	 */
	public static Vector2f sum(Vector2f v, Vector2f w) {
		return vector(v.x + w.x, v.y + w.y);
	}

	/**
	 * @param v a vector
	 * @return the inverse vector
	 */
	public static Vector2f inverse(Vector2f v) {
		return vector(-v.x, -v.y);
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return the difference vector <code>v - w</code>
	 */
	public static Vector2f diff(Vector2f v, Vector2f w) {
		return vector(v.x - w.x, v.y - w.y);
	}

	/**
	 * @param f a scalar value
	 * @param v a vector
	 * @return the vector given by multiplying each coordinate with the scalar value
	 */
	public static Vector2f smul(float f, Vector2f v) {
		return vector(f * v.x, f * v.y);
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return the dot product ("Skalarprodukt") of both vectors
	 */
	public static float dot(Vector2f v, Vector2f w) {
		return v.x * w.x + v.y * w.y;
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return the Euclidean distance between the points defined by the vectors
	 */
	public static float euclideanDist(Vector2f v, Vector2f w) {
		return diff(v, w).length();
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return the Manhattan distance between the points defined by the vectors
	 */
	public static float manhattanDist(Vector2f v, Vector2f w) {
		return Math.abs(v.x - w.x) + Math.abs(v.y - w.y);
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return the angle between the vectors (in degrees)
	 */
	public static double angle(Vector2f v, Vector2f w) {
		double cos_phi = dot(v, w) / (v.length() * w.length());
		return toDegrees(acos(cos_phi));
	}

	/**
	 * @return x-coordinate rounded to the closest integer
	 */
	public int roundedX() {
		return Math.round(x);
	}

	/**
	 * @return y-coordinate rounded to the closest integer
	 */
	public int roundedY() {
		return Math.round(y);
	}

	/**
	 * @return The normalized vector, that is the unit vector with the same
	 *         direction.
	 */
	public Vector2f normalized() {
		float len = length();
		return vector(x / len, y / len);
	}

	/**
	 * @return The length of the vector.
	 */
	public float length() {
		return (float) Math.hypot(x, y);
	}

	@Override
	public int hashCode() {
		int bits = 7;
		bits = 31 * bits + java.lang.Float.floatToIntBits(x);
		bits = 31 * bits + java.lang.Float.floatToIntBits(y);
		return bits;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof Vector2f) {
			Vector2f v = (Vector2f) obj;
			return (x == v.x) && (y == v.y);
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}