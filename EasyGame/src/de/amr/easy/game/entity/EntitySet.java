package de.amr.easy.game.entity;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class EntitySet {

	private final Set<GameEntity> entitySet = ConcurrentHashMap.newKeySet();

	public Stream<GameEntity> all() {
		return entitySet.stream();
	}

	@SuppressWarnings("unchecked")
	public <E extends GameEntity> Stream<E> filter(Class<E> type) {
		Objects.requireNonNull(type);
		return (Stream<E>) all().filter(e -> type.isAssignableFrom(e.getClass()));
	}

	public void add(GameEntity... entities) {
		Stream.of(entities).forEach(this::add);
	}

	public <E extends GameEntity> E add(E entity) {
		Objects.requireNonNull(entity);
		entitySet.add(entity);
		return entity;
	}

	public void remove(GameEntity entity) {
		Objects.requireNonNull(entity);
		entitySet.remove(entity);
	}

	public <E extends GameEntity> void removeAll(Class<E> type) {
		Objects.requireNonNull(type);
		entitySet.removeIf((e) -> type.isAssignableFrom(e.getClass()));
	}

	public void removeAll() {
		entitySet.clear();
	}

	public <E extends GameEntity> E findAny(Class<E> type) {
		Objects.requireNonNull(type);
		return filter(type).findAny().orElse(null);
	}

	public <E extends GameEntity> E findByName(Class<E> type, String name) {
		Objects.requireNonNull(name);
		return filter(type).filter(e -> name.equals(e.getName())).findFirst().orElseThrow(IllegalArgumentException::new);
	}

	public boolean contains(String name) {
		Objects.requireNonNull(name);
		return entitySet.stream().filter(e -> name.equals(e.getName())).findAny().isPresent();
	}
}
