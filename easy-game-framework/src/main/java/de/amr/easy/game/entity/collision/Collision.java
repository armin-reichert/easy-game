package de.amr.easy.game.entity.collision;

import java.awt.geom.Rectangle2D;

public class Collision {

	private final Collider first;
	private final Collider second;
	private final Rectangle2D intersection;
	private final boolean collisionStart;
	private final Object appEvent;

	public Collision(Collider first, Collider second, Rectangle2D intersection, Object appEvent, boolean collisionStart) {
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

	public Collider getFirst() {
		return first;
	}

	public Collider getSecond() {
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