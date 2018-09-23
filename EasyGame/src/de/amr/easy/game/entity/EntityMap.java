package de.amr.easy.game.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EntityMap {

	private final Map<String, AbstractGameEntity> map = new ConcurrentHashMap<>();

	public <E extends AbstractGameEntity> E store(String key, E entity) {
		map.put(key, entity);
		return entity;
	}

	public <E extends AbstractGameEntity> E store(E entity) {
		return store("" + entity.hashCode(), entity);
	}

	public void remove(String key) {
		map.remove(key);
	}

	public void removeEntity(AbstractGameEntity entity) {
		map.entrySet().removeIf(entry -> entry.getValue().equals(entity));
	}

	public void removeAll(Class<? extends AbstractGameEntity> classToDelete) {
		map.entrySet().removeIf(entry -> classToDelete.isAssignableFrom(entry.getValue().getClass()));
	}

	public boolean contains(String key) {
		return map.containsKey(key);
	}

	public Stream<AbstractGameEntity> all() {
		return map.values().stream();
	}

	public Stream<AbstractGameEntity> filter(Predicate<? super AbstractGameEntity> predicate) {
		return map.values().stream().filter(predicate);
	}

	@SuppressWarnings("unchecked")
	public <E extends AbstractGameEntity> E ofName(String name) {
		return (E) map.get(name);
	}

	@SuppressWarnings("unchecked")
	public <E> Stream<E> ofClass(Class<E> cls) {
		return map.values().stream().filter(e -> e.getClass().equals(cls)).map(e -> (E) e);
	}
}