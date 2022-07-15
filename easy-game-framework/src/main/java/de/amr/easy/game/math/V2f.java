package de.amr.easy.game.math;

import static java.lang.Math.acos;
import static java.lang.Math.toDegrees;

/**
 * Immutable 2D vector with float precision.
 * 
 * @author Armin Reichert
 */
public record V2f(float x, float y) {

	public static final V2f NULL = new V2f(0, 0);

	public static final float EPSILON = 1e-6f;

	@Override
	public String toString() {
		return "(%.2f, %.2f)".formatted(x, y);
	}

	public static V2f v(float x, float y) {
		return x == 0 && y == 0 ? NULL : new V2f(x, y);
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return sum of the two vectors
	 */
	public static V2f sum(V2f v, V2f w) {
		return v(v.x + w.x, v.y + w.y);
	}

	/**
	 * @param v other vector
	 * @return sum of this and the other vector
	 */
	public V2f add(V2f v) {
		return v(x + v.x, y + v.y);
	}

	/**
	 * @param v other vector
	 * @return difference of this and the other vector
	 */
	public V2f subtract(V2f v) {
		return v(x - v.x, y - v.y);
	}

	/**
	 * @param v a vector
	 * @return the inverse vector
	 */
	public static V2f inverse(V2f v) {
		return v(-v.x, -v.y);
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return the difference vector <code>v - w</code>
	 */
	public static V2f diff(V2f v, V2f w) {
		return v(v.x - w.x, v.y - w.y);
	}

	/**
	 * @param f a scalar value
	 * @param v a vector
	 * @return the vector given by multiplying each coordinate with the scalar value
	 */
	public static V2f smul(float f, V2f v) {
		return v(f * v.x, f * v.y);
	}

	/**
	 * @param f a scalar value
	 * @return the vector given by multiplying this vector with the scalar value
	 */
	public V2f times(float f) {
		return v(f * x, f * y);
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return the dot product ("Skalarprodukt") of both vectors
	 */
	public static float dot(V2f v, V2f w) {
		return v.x * w.x + v.y * w.y;
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return the Euclidean distance between the points defined by the vectors
	 */
	public static float euclideanDist(V2f v, V2f w) {
		return diff(v, w).length();
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return the Manhattan distance between the points defined by the vectors
	 */
	public static float manhattanDist(V2f v, V2f w) {
		return Math.abs(v.x - w.x) + Math.abs(v.y - w.y);
	}

	/**
	 * @param v first vector
	 * @param w second vector
	 * @return the angle between the vectors (in degrees)
	 */
	public static double angle(V2f v, V2f w) {
		double cosPhi = dot(v, w) / (v.length() * w.length());
		return toDegrees(acos(cosPhi));
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
	 * @return The normalized vector, that is the unit vector with the same direction.
	 */
	public V2f normalized() {
		float len = length();
		return v(x / len, y / len);
	}

	/**
	 * @return The length of the vector.
	 */
	public float length() {
		return (float) Math.hypot(x, y);
	}

}