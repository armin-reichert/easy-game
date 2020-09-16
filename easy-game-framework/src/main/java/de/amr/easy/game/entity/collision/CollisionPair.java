package de.amr.easy.game.entity.collision;

import java.awt.geom.Rectangle2D;
import java.util.Objects;

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
	public int hashCode() {
		return Objects.hash(either, other);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CollisionPair other = (CollisionPair) obj;
		return Objects.equals(either, other.either) && Objects.equals(this.other, other.other);
	}

}
