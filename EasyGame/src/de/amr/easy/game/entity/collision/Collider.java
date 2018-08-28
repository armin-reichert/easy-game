package de.amr.easy.game.entity.collision;

import java.awt.geom.Rectangle2D;

public interface Collider {

	Rectangle2D getCollisionBox();

	default boolean collidesWith(Collider other) {
		return getCollisionBox().intersects(other.getCollisionBox());
	}
}