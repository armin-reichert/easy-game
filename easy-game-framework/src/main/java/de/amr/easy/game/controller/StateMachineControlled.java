package de.amr.easy.game.controller;

import java.util.stream.Stream;

import de.amr.statemachine.core.StateMachine;

public interface StateMachineControlled {

	/**
	 * @return stated machines used by implementing class
	 */
	default Stream<StateMachine<?, ?>> machines() {
		return Stream.empty();
	}
}