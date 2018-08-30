package de.amr.easy.game.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EntityMap {

	private final Map<String, GameEntity> map = new ConcurrentHashMap<>();

	public <E extends GameEntity> E store(String key, E entity) {
		map.put(key, entity);
		return entity;
	}

	public <E extends GameEntity> E store(E entity) {
		return store("" + entity.hashCode(), entity);
	}

	public void remove(String key) {
		map.remove(key);
	}

	public void removeEntity(GameEntity entity) {
		map.entrySet().removeIf(entry -> entry.getValue().equals(entity));
	}

	public void removeAll(Class<? extends GameEntity> classToDelete) {
		map.entrySet().removeIf(entry -> classToDelete.isAssignableFrom(entry.getValue().getClass()));
	}

	public boolean contains(String key) {
		return map.containsKey(key);
	}

	public Stream<GameEntity> all() {
		return map.values().stream();
	}

	public Stream<GameEntity> filter(Predicate<? super GameEntity> predicate) {
		return map.values().stream().filter(predicate);
	}

	@SuppressWarnings("unchecked")
	public <E extends GameEntity> E ofName(String name) {
		return (E) map.get(name);
	}

	@SuppressWarnings("unchecked")
	public <E> Stream<E> ofClass(Class<E> cls) {
		return map.values().stream().filter(e -> e.getClass().equals(cls)).map(e -> (E) e);
	}
}