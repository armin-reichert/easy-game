package de.amr.easy.game.entity;

import java.awt.geom.Rectangle2D;

import de.amr.easy.game.math.Vector2f;

/**
 * Encapsulates collision size, position, velocity and rotation of a game object.
 * 
 * @author Armin Reichert
 */
public class Transform {

	private int width;
	private int height;
	private Vector2f position;
	private Vector2f velocity;
	private double rotation;

	public Transform() {
		width = 0;
		height = 0;
		position = Vector2f.of(0, 0);
		velocity = Vector2f.of(0, 0);
		rotation = 0.0;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public Vector2f getPosition() {
		return Vector2f.of(position.x, position.y);
	}

	public Rectangle2D getCollisionBox() {
		return new Rectangle2D.Float(position.x, position.y, width, height);
	}

	public Vector2f getCenter() {
		return Vector2f.of(position.x + width / 2, position.y + height / 2);
	}

	public void setX(float x) {
		position = Vector2f.of(x, position.y);
	}

	public void setY(float y) {
		position = Vector2f.of(position.x, y);
	}

	public void setPosition(float x, float y) {
		position = Vector2f.of(x, y);
	}

	public void setPosition(Vector2f p) {
		position = Vector2f.of(p.x, p.y);
	}

	public void move() {
		position = Vector2f.sum(position, velocity);
	}

	public void centerX(int width) {
		setX((width - getWidth()) / 2);
	}

	public void centerY(int height) {
		setY((height - getHeight()) / 2);
	}

	public void center(int width, int height) {
		centerX(width);
		centerY(height);
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
