package de.amr.easy.game.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An entity map that allows to store entities with a unique key or anonymously and that provides a
 * number of useful methods for accessing entities.
 * 
 * @author Armin Reichert
 */
public class EntityMap {

	private final Map<String, Entity> entries = new ConcurrentHashMap<>();

	public <E extends Entity> E store(String key, E entity) {
		entries.put(key, entity);
		return entity;
	}

	public <E extends Entity> E store(E entity) {
		return store("" + entity.hashCode(), entity);
	}

	public void remove(String key) {
		entries.remove(key);
	}

	public void removeEntity(Entity entity) {
		entries.entrySet().removeIf(e -> e.getValue().equals(entity));
	}

	public void removeAll(Class<? extends Entity> class_) {
		entries.entrySet().removeIf(e -> class_.isAssignableFrom(e.getValue().getClass()));
	}

	public boolean contains(String key) {
		return entries.containsKey(key);
	}

	public Stream<Entity> all() {
		return entries.values().stream();
	}

	public Stream<Entity> filter(Predicate<? super Entity> predicate) {
		return entries.values().stream().filter(predicate);
	}

	@SuppressWarnings("unchecked")
	public <E extends Entity> E named(String name) {
		return (E) entries.get(name);
	}

	public <E extends Entity> Stream<E> ofClass(Class<E> class_) {
		return filter(e -> e.getClass().equals(class_)).map(class_::cast);
	}

	public <E> Stream<E> implementing(Class<E> interface_) {
		return filter(e -> interface_.isAssignableFrom(e.getClass())).map(interface_::cast);
	}

}