package de.amr.easy.game.entity.collision;

import java.awt.geom.Rectangle2D;

public interface CollisionSensitive {

	Rectangle2D getCollisionBox();

	default boolean collidesWith(CollisionSensitive other) {
		return getCollisionBox().intersects(other.getCollisionBox());
	}
}