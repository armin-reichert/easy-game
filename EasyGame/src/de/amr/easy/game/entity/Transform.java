package de.amr.easy.game.entity;

import de.amr.easy.game.math.Vector2;

/**
 * Encapsulates position, velocity and rotation of a game object.
 * 
 * @author Armin Reichert
 */
public class Transform {

	private final Vector2 position;
	private final Vector2 velocity;
	private double rotation;

	public Transform() {
		position = new Vector2(0, 0);
		velocity = new Vector2(0, 0);
		rotation = 0.0;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public void setX(float x) {
		position.x = x;
	}

	public void setY(float y) {
		position.y = y;
	}

	public void moveTo(float x, float y) {
		position.assign(x, y);
	}

	public void moveTo(Vector2 p) {
		position.assign(p);
	}

	public void move() {
		position.add(velocity);
	}

	public void setVelocity(float vx, float vy) {
		velocity.assign(vx, vy);
	}

	public void setVelocity(Vector2 v) {
		velocity.assign(v);
	}

	public void setVelocityX(float vx) {
		velocity.x = vx;
	}

	public void setVelocityY(float vy) {
		velocity.y = vy;
	}

	public float getVelocityX() {
		return velocity.x;
	}

	public float getVelocityY() {
		return velocity.y;
	}

	public Vector2 getVelocity() {
		return new Vector2(velocity);
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
}
