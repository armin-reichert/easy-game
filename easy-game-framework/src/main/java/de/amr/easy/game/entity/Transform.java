package de.amr.easy.game.entity;

import java.awt.geom.Rectangle2D;

import de.amr.easy.game.math.Vector2f;

/**
 * Encapsulates size, position, velocity and rotation of an entity.
 * 
 * @author Armin Reichert
 */
public class Transform {

	public int width; // in pixel
	public int height; // in pixel

	public float x;
	public float y;

	public float vx; // velocity in x-direction
	public float vy; // velocity in y-direction

	public double rotation; // in radians

	@Override
	public String toString() {
		return String.format("[position:(x=%.2f y=%.2f) size:(w=%d h=%d) velocity:(x=%.2f y=%.2f) rotation:%.2g\u00b0]", x,
				y, width, height, vx, vy, Math.toDegrees(rotation));
	}

	public Vector2f getPosition() {
		return Vector2f.of(x, y);
	}

	public Rectangle2D getCollisionBox() {
		return new Rectangle2D.Float(x, y, width, height);
	}

	public Vector2f getCenter() {
		return Vector2f.of(x + width / 2, y + height / 2);
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setPosition(Vector2f p) {
		x = p.x;
		y = p.y;
	}

	public void move() {
		x += vx;
		y += vy;
	}

	public void centerBoth(float left, float top, float width, float height) {
		x = left + (width - this.width) / 2;
		y = top + (height - this.height) / 2;
	}

	public void centerHorizontally(float left, float right) {
		x = (left + right - width) / 2;
	}

	public void centerVertically(float top, float bottom) {
		y = (top + bottom - height) / 2;
	}

	public void setVelocity(float vx, float vy) {
		this.vx = vx;
		this.vy = vy;
	}

	public void setVelocity(Vector2f v) {
		setVelocity(v.x, v.y);
	}

	public Vector2f getVelocity() {
		return Vector2f.of(vx, vy);
	}
}