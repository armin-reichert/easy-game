package de.amr.easy.game.controller;

import static de.amr.easy.game.Application.app;
import static de.amr.easy.game.Application.loginfo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

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

	public <FSM extends StateMachine<?, ?>> void register(FSM fsm) {
		machines.add(fsm);
		fsm.getTracer().setLogger(app().getLogger());
		loginfo("State machine registered: %s", fsm);
	}

	public <FSM extends StateMachine<?, ?>> void unregister(FSM fsm) {
		machines.remove(fsm);
		fsm.getTracer().setLogger(null);
		loginfo("State machine unregistered: %s", fsm);
	}

	public <FSM extends StateMachine<?, ?>> void register(Stream<FSM> machines) {
		machines.filter(Objects::nonNull).forEach(this::register);
	}

	public <FSM extends StateMachine<?, ?>> void unregister(Stream<FSM> machines) {
		machines.filter(Objects::nonNull).forEach(this::unregister);
	}
}