package de.amr.easy.game.entity.collision;

import java.awt.geom.Rectangle2D;

public class Collision {

	private final CollisionSensitive first;
	private final CollisionSensitive second;
	private final Rectangle2D intersection;
	private final boolean collisionStart;
	private final Object appEvent;

	public Collision(CollisionSensitive first, CollisionSensitive second, Rectangle2D intersection, Object appEvent,
			boolean collisionStart) {
		this.first = first;
		this.second = second;
		this.intersection = intersection;
		this.appEvent = appEvent;
		this.collisionStart = collisionStart;
	}

	@Override
	public String toString() {
		return "Collision" + (collisionStart ? "Start" : "End") + "(" + first.getClass().getSimpleName() + "<->"
				+ second.getClass().getSimpleName() + ") -> " + appEvent;
	}

	public CollisionSensitive getFirst() {
		return first;
	}

	public CollisionSensitive getSecond() {
		return second;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAppEvent() {
		return (T) appEvent;
	}

	public boolean isCollisionStart() {
		return collisionStart;
	}

	public Rectangle2D getIntersection() {
		return intersection;
	}
}