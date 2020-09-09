package de.amr.easy.game.controller;

import java.util.stream.Stream;

import de.amr.statemachine.core.StateMachine;

public interface StateMachineControlled {

	default Stream<StateMachine<?, ?>> machines() {
		return Stream.empty();
	}
}