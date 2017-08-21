package de.amr.easy.game.math;

import static java.lang.Math.acos;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

public class Vector2 {

	public static Vector2 sum(Vector2 v, Vector2 w) {
		return new Vector2(v.x + w.x, v.y + w.y);
	}

	public static Vector2 inverse(Vector2 v) {
		return new Vector2(-v.x, -v.y);
	}

	public static Vector2 nullVector() {
		return new Vector2(0, 0);
	}

	public static Vector2 diff(Vector2 v, Vector2 w) {
		return new Vector2(v.x - w.x, v.y - w.y);
	}

	public static Vector2 times(float f, Vector2 v) {
		return new Vector2(f * v.x, f * v.y);
	}

	public static float dot(Vector2 v, Vector2 w) {
		return v.x * w.x + v.y * w.y;
	}

	public static float dist(Vector2 v, Vector2 w) {
		return diff(v, w).length();
	}

	public static double angle(Vector2 v, Vector2 w) {
		double cos_phi = dot(v, w) / (v.length() * w.length());
		return toDegrees(acos(cos_phi));
	}

	public float x;

	public float y;

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(Vector2 v) {
		this(v.x, v.y);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Vector2) {
			Vector2 v = (Vector2) other;
			return v.x == x && v.y == y;
		}
		return super.equals(other);
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

	public Vector2 assign(Vector2 v) {
		x = v.x;
		y = v.y;
		return this;
	}

	public Vector2 assign(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2 add(Vector2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public Vector2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector2 sub(Vector2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public Vector2 sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	public Vector2 times(float factor) {
		x *= factor;
		y *= factor;
		return this;
	}

	public Vector2 normalize() {
		float len = length();
		if (len != 0) {
			x /= len;
			y /= len;
		}
		return this;
	}

	public float length() {
		return (float) sqrt(dot(this, this));
	}
}
