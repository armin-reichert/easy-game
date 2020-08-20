package de.amr.easy.game.entity;

import java.awt.geom.Rectangle2D;

import de.amr.easy.game.math.Vector2f;

/**
 * Encapsulates size, position, velocity and rotation of an entity.
 * 
 * @author Armin Reichert
 */
public class Transform {

	/**
	 * Width of the collision box.
	 */
	public int width;

	/**
	 * Height of the collision box.
	 */
	public int height; // in pixel

	/**
	 * x-coordinate, left is 0.
	 */
	public float x;

	/**
	 * y-coordinate, top = 0.
	 */
	public float y;

	/**
	 * x-component of velocity.
	 */
	public float vx;

	/**
	 * y-component of velocity.
	 */
	public float vy;

	/**
	 * Rotation (in radians).
	 */
	public double rotation;

	/**
	 * Position as a vector.
	 * 
	 * @return position vector
	 */
	public Vector2f getPosition() {
		return Vector2f.of(x, y);
	}

	/**
	 * Sets the position.
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the position.
	 * 
	 * @param position position vector
	 */
	public void setPosition(Vector2f position) {
		x = position.x;
		y = position.y;
	}

	/**
	 * Returns the center position.
	 * 
	 * @return center position vector
	 */
	public Vector2f getCenter() {
		return Vector2f.of(x + width / 2, y + height / 2);
	}

	/**
	 * Returns the current velocity.
	 * 
	 * @return velocity vector
	 */
	public Vector2f getVelocity() {
		return Vector2f.of(vx, vy);
	}

	/**
	 * Sets the current velocity.
	 * 
	 * @param vx x-component of velocity vector
	 * @param vy y-component of velocity vector
	 */
	public void setVelocity(float vx, float vy) {
		this.vx = vx;
		this.vy = vy;
	}

	/**
	 * Sets the current velocity.
	 * 
	 * @param velocity velocity vector
	 */
	public void setVelocity(Vector2f velocity) {
		vx = velocity.x;
		vy = velocity.y;
	}

	/**
	 * Moves one step using current velocity.
	 */
	public void move() {
		x += vx;
		y += vy;
	}

	/**
	 * Centers relative to the given rectangle.
	 * 
	 * @param left   left position of rectangle
	 * @param top    top position of rectangle
	 * @param width  width of rectangle
	 * @param height height of rectangle
	 */
	public void centerBoth(float left, float top, float width, float height) {
		x = left + (width - this.width) / 2;
		y = top + (height - this.height) / 2;
	}

	/**
	 * Centers horizontally relative to the given bounds.
	 * 
	 * @param left  left border
	 * @param right right border
	 */
	public void centerHorizontally(float left, float right) {
		x = (left + right - width) / 2;
	}

	/**
	 * Centers vertically relative to the given bounds.
	 * 
	 * @param top    top border
	 * @param bottom bottom border
	 */
	public void centerVertically(float top, float bottom) {
		y = (top + bottom - height) / 2;
	}

	/**
	 * Collision box.
	 * 
	 * @return collision box rectangle.
	 */
	public Rectangle2D getCollisionBox() {
		return new Rectangle2D.Float(x, y, width, height);
	}

	@Override
	public String toString() {
		return String.format("[position:(x=%.2f y=%.2f) size:(w=%d h=%d) velocity:(x=%.2f y=%.2f) rotation:%.2g\u00b0]", x,
				y, width, height, vx, vy, Math.toDegrees(rotation));
	}
}