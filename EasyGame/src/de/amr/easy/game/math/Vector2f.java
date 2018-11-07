package de.amr.easy.game.math;

import static java.lang.Math.acos;
import static java.lang.Math.toDegrees;

/**
 * 2D vector with float precision.
 * 
 * @author Armin Reichert
 */
public class Vector2f {

	public static final Vector2f NULL = Vector2f.of(0, 0);

	public static Vector2f of(float x, float y) {
		return new Vector2f(x, y);
	}

	public static Vector2f sum(Vector2f v, Vector2f w) {
		return Vector2f.of(v.x + w.x, v.y + w.y);
	}

	public static Vector2f inverse(Vector2f v) {
		return Vector2f.of(-v.x, -v.y);
	}

	public static Vector2f diff(Vector2f v, Vector2f w) {
		return Vector2f.of(v.x - w.x, v.y - w.y);
	}

	public static Vector2f smul(float f, Vector2f v) {
		return Vector2f.of(f * v.x, f * v.y);
	}

	public static float dot(Vector2f v, Vector2f w) {
		return v.x * w.x + v.y * w.y;
	}

	public static float dist(Vector2f v, Vector2f w) {
		return diff(v, w).length();
	}

	public static double angle(Vector2f v, Vector2f w) {
		double cos_phi = dot(v, w) / (v.length() * w.length());
		return toDegrees(acos(cos_phi));
	}

	public final float x;
	public final float y;

	private Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
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

	public int roundedX() {
		return Math.round(x);
	}

	public int roundedY() {
		return Math.round(y);
	}

	public Vector2f normalized() {
		float len = length();
		return Vector2f.of(x / len, y / len);
	}

	public float length() {
		return (float) Math.hypot(x, y);
	}
}