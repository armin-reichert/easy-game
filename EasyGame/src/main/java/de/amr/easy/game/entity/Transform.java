package de.amr.easy.game.entity;

import java.awt.geom.Rectangle2D;

import de.amr.easy.game.math.Vector2f;

/**
 * Encapsulates size, position, velocity and rotation of an entity.
 * 
 * @author Armin Reichert
 */
public class Transform {

	// size
	public int width;
	public int height;

	// position
	public float x;
	public float y;

	// velocity
	private float vx;
	private float vy;

	// rotation (radians)
	private double rotation;

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

	public void centerX(int outerWidth) {
		x = (outerWidth - width) / 2;
	}

	public void centerY(int outerHeight) {
		y = (outerHeight - height) / 2;
	}

	public void center(int width, int height) {
		centerX(width);
		centerY(height);
	}

	public void setVelocity(float vx, float vy) {
		this.vx = vx;
		this.vy = vy;
	}

	public void setVelocity(Vector2f v) {
		setVelocity(v.x, v.y);
	}

	public void setVelocityX(float vx) {
		this.vx = vx;
	}

	public void setVelocityY(float vy) {
		this.vy = vy;
	}

	public float getVelocityX() {
		return vx;
	}

	public float getVelocityY() {
		return vy;
	}

	public Vector2f getVelocity() {
		return Vector2f.of(vx, vy);
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
}