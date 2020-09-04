package de.amr.easy.game.controller;

import static de.amr.easy.game.Application.app;
import static de.amr.easy.game.Application.loginfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

	public Set<StateMachine<?, ?>> machines() {
		return machines.values().stream().flatMap(List::stream).collect(Collectors.toSet());
	}

	public Set<StateMachine<?, ?>> machines(String category) {
		return machines.entrySet().stream().filter(e -> e.getKey().equals(category)).map(e -> e.getValue())
				.flatMap(List::stream).collect(Collectors.toSet());
	}

	public void register(String categoryName, StateMachine<?, ?> fsm) {
		List<StateMachine<?, ?>> category = machines.get(categoryName);
		if (category == null) {
			category = new ArrayList<StateMachine<?, ?>>();
			machines.put(categoryName, category);
		}
		category.add(fsm);
		fsm.getTracer().setLogger(app().getLogger());
		loginfo("State machine registered: %s", fsm.getDescription());
	}

	public void register(String categoryName, Stream<StateMachine<?, ?>> machines) {
		machines.forEach(fsm -> register(categoryName, fsm));
	}

	public void unregister(StateMachine<?, ?> fsm) {
		Iterator<Map.Entry<String, List<StateMachine<?, ?>>>> it = machines.entrySet().iterator();
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

	public <FSM extends StateMachine<?, ?>> void unregister(Stream<FSM> machines) {
		machines.forEach(this::unregister);
	}
}