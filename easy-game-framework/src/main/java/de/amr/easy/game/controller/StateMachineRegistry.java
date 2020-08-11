package de.amr.easy.game.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import de.amr.easy.game.Application;
import de.amr.statemachine.core.StateMachine;

/**
 * A central place to register/unregister the used state machines.
 * 
 * @author Armin Reichert
 */
public final class StateMachineRegistry {

	public static final StateMachineRegistry REGISTRY = new StateMachineRegistry();

	private final Set<StateMachine<?, ?>> machines;

	private StateMachineRegistry() {
		machines = new HashSet<>();
	}

	public Collection<StateMachine<?, ?>> machines() {
		return Collections.unmodifiableSet(machines);
	}

	public <FSM extends StateMachine<?, ?>> void register(Stream<FSM> machines) {
		machines.filter(Objects::nonNull).forEach(fsm -> {
			this.machines.add(fsm);
			fsm.getTracer().setLogger(Application.app().getLogger());
			Application.loginfo("State machine registered: %s", fsm);
		});
	}

	public <FSM extends StateMachine<?, ?>> void unregister(Stream<FSM> machines) {
		machines.filter(Objects::nonNull).forEach(fsm -> {
			this.machines.remove(fsm);
			fsm.getTracer().setLogger(null);
			Application.loginfo("State machine unregistered: %s", fsm);
		});
	}
}