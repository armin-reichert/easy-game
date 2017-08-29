package de.amr.easy.game.entity;

import de.amr.easy.game.math.Vector2f;

/**
 * Encapsulates position, velocity and rotation of a game object.
 * 
 * @author Armin Reichert
 */
public class Transform {

	private Vector2f position;
	private Vector2f velocity;
	private double rotation;

	public Transform() {
		position = Vector2f.of(0, 0);
		velocity = Vector2f.of(0, 0);
		rotation = 0.0;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public void setX(float x) {
		position = Vector2f.of(x, position.y);
	}

	public void setY(float y) {
		position = Vector2f.of(position.x, y);
	}

	public void moveTo(float x, float y) {
		position = Vector2f.of(x, y);
	}

	public void moveTo(Vector2f p) {
		position = Vector2f.of(p.x, p.y);
	}

	public void move() {
		position = Vector2f.sum(position, velocity);
	}

	public void setVelocity(float vx, float vy) {
		velocity = Vector2f.of(vx, vy);
	}

	public void setVelocity(Vector2f v) {
		velocity = Vector2f.of(v.x, v.y);
	}

	public void setVelocityX(float vx) {
		velocity = Vector2f.of(vx, velocity.y);
	}

	public void setVelocityY(float vy) {
		velocity = Vector2f.of(velocity.x, vy);
	}

	public float getVelocityX() {
		return velocity.x;
	}

	public float getVelocityY() {
		return velocity.y;
	}

	public Vector2f getVelocity() {
		return velocity;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
}
