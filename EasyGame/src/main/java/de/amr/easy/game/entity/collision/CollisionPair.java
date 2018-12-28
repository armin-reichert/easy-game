package de.amr.easy.game.entity.collision;

import java.awt.geom.Rectangle2D;

public class CollisionPair {

	private final Collider either;
	private final Collider other;
	private Rectangle2D intersection;

	public CollisionPair(Collider x, Collider y) {
		this.either = x;
		this.other = y;
	}

	public Collider either() {
		return either;
	}

	public Collider other() {
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
		return pair.either == either && pair.other == other
				|| pair.either == other && pair.other == either;
	}

	@Override
	public int hashCode() {
		return either.hashCode() + other.hashCode();
	}
}
