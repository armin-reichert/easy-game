package de.amr.easy.game.entity.collision;

import static de.amr.easy.game.Application.LOGGER;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CollisionHandler {

	private final Map<CollisionPair, Object> collisionStarts = new HashMap<>();
	private final Map<CollisionPair, Object> collisionEnds = new HashMap<>();
	private final Set<CollisionPair> newCollisions = new HashSet<>();
	private final Set<CollisionPair> oldCollisions = new HashSet<>();
	private final Set<Collision> events = new HashSet<>();

	public void registerStart(Collider x, Collider y, Object event) {
		collisionStarts.put(new CollisionPair(x, y), event);
	}

	public void registerEnd(Collider x, Collider y, Object event) {
		collisionEnds.put(new CollisionPair(x, y), event);
	}

	public void unregisterStart(Collider x, Collider y) {
		collisionStarts.remove(new CollisionPair(x, y));
	}

	public void unregisterEnd(Collider x, Collider y) {
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
				Collision event = new Collision(pair.either(), pair.other(), pair.getIntersection(), collisionStarts.get(pair),
						true);
				events.add(event);
				LOGGER.fine(event.toString());
			}
		}
		for (CollisionPair pair : collisionEnds.keySet()) {
			if (!newCollisions.contains(pair) && oldCollisions.contains(pair)) {
				Collision event = new Collision(pair.either(), pair.other(), pair.getIntersection(), collisionEnds.get(pair),
						false);
				events.add(event);
				LOGGER.fine(event.toString());
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
		Rectangle2D intersection = p.either().getCollisionBox().createIntersection(p.other().getCollisionBox());
		if (!intersection.isEmpty()) {
			p.setIntersection(intersection);
			return true;
		}
		return false;
	}
}
