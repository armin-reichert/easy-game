package de.amr.easy.game.entity.collision;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.amr.easy.game.Application;

public class CollisionHandler {

	private final Map<CollisionPair, Object> collisionStarts = new HashMap<>();
	private final Map<CollisionPair, Object> collisionEnds = new HashMap<>();
	private final Set<CollisionPair> newCollisions = new HashSet<>();
	private final Set<CollisionPair> oldCollisions = new HashSet<>();
	private final Set<Collision> events = new HashSet<>();

	public void registerStart(CollisionSensitive x, CollisionSensitive y, Object event) {
		collisionStarts.put(new CollisionPair(x, y), event);
	}

	public void registerEnd(CollisionSensitive x, CollisionSensitive y, Object event) {
		collisionEnds.put(new CollisionPair(x, y), event);
	}

	public void unregisterStart(CollisionSensitive x, CollisionSensitive y) {
		collisionStarts.remove(new CollisionPair(x, y));
	}

	public void unregisterEnd(CollisionSensitive x, CollisionSensitive y) {
		collisionEnds.remove(new CollisionPair(x, y));
	}

	public Iterable<Collision> collisions() {
		return events;
	}

	public void update() {
		oldCollisions.clear();
		oldCollisions.addAll(newCollisions);
		newCollisions.clear();
		events.clear();
		for (CollisionPair pair : collisionStarts.keySet()) {
			if (checkCollision(pair)) {
				newCollisions.add(pair);
			}
		}
		for (CollisionPair pair : collisionEnds.keySet()) {
			if (checkCollision(pair)) {
				newCollisions.add(pair);
			}
		}
		for (CollisionPair pair : collisionStarts.keySet()) {
			if (newCollisions.contains(pair) && !oldCollisions.contains(pair)) {
				Collision event = new Collision(pair.either(), pair.other(), pair.getIntersection(),
						collisionStarts.get(pair), true);
				events.add(event);
				Application.LOG.fine(event.toString());
			}
		}
		for (CollisionPair pair : collisionEnds.keySet()) {
			if (!newCollisions.contains(pair) && oldCollisions.contains(pair)) {
				Collision event = new Collision(pair.either(), pair.other(), pair.getIntersection(),
						collisionEnds.get(pair), false);
				events.add(event);
				Application.LOG.fine(event.toString());
			}
		}
	}

	public void clear() {
		newCollisions.clear();
		oldCollisions.clear();
		events.clear();
		collisionStarts.clear();
		collisionEnds.clear();
	}

	private boolean checkCollision(CollisionPair p) {
		Rectangle2D intersection = p.either().getCollisionBox()
				.createIntersection(p.other().getCollisionBox());
		if (!intersection.isEmpty()) {
			p.setIntersection(intersection);
			return true;
		}
		return false;
	}
}
