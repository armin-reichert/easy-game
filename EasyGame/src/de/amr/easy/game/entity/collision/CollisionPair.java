package de.amr.easy.game.entity.collision;

import java.awt.geom.Rectangle2D;

public class CollisionPair {

	private final CollisionSensitive either;
	private final CollisionSensitive other;
	private Rectangle2D intersection;

	public CollisionPair(CollisionSensitive x, CollisionSensitive y) {
		this.either = x;
		this.other = y;
	}

	public CollisionSensitive either() {
		return either;
	}

	public CollisionSensitive other() {
		return other;
	}

	public Rectangle2D getIntersection() {
		return intersection;
	}

	public void setIntersection(Rectangle2D intersection) {
		this.intersection = intersection;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		CollisionPair pair = (CollisionPair) obj;
		return pair.either == either && pair.other == other || pair.either == other && pair.other == either;
	}

	@Override
	public int hashCode() {
		return either.hashCode() + other.hashCode();
	}
}
