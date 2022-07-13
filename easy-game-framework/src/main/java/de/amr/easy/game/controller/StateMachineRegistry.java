package de.amr.easy.game.controller;

import static de.amr.easy.game.Application.app;
import static de.amr.easy.game.Application.loginfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import de.amr.statemachine.core.StateMachine;

/**
 * A central place to register/unregister the used state machines.
 * 
 * @author Armin Reichert
 */
public final class StateMachineRegistry {

	public static final StateMachineRegistry REGISTRY = new StateMachineRegistry();

	// maps categories to machines
	private final Map<String, List<StateMachine<?, ?>>> machines;

	private StateMachineRegistry() {
		machines = new HashMap<>();
	}

	public Stream<String> categories() {
		return machines.keySet().stream();
	}

	public Stream<StateMachine<?, ?>> machines() {
		return machines.values().stream().flatMap(List::stream);
	}

	public Stream<StateMachine<?, ?>> machines(String category) {
		return machines.entrySet().stream().filter(e -> e.getKey().equals(category)).map(e -> e.getValue())
				.flatMap(List::stream);
	}

	public void register(String categoryName, StateMachine<?, ?> fsm) {
		var category = machines.get(categoryName);
		if (category == null) {
			category = new ArrayList<>();
			machines.put(categoryName, category);
		}
		category.add(fsm);
		fsm.getTracer().setLogger(app().getLogger());
		loginfo("State machine registered: %s", fsm.getDescription());
	}

	public void unregister(StateMachine<?, ?> fsm) {
		var it = machines.entrySet().iterator();
		while (it.hasNext()) {
			List<StateMachine<?, ?>> category = it.next().getValue();
			if (category.contains(fsm)) {
				it.remove();
				fsm.getTracer().setLogger(null);
				loginfo("State machine unregistered: %s", fsm.getDescription());
				break;
			}
		}
	}
}