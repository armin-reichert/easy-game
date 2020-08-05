package de.amr.easy.game.controller;

/**
 * Interface providing lifecycle hook methods.
 * 
 * @author Armin Reichert
 */
public interface Lifecycle {

	/**
	 * Called when an entity/component/controller is initialized.
	 */
	void init();

	/**
	 * Called when an entity/component/controller is updated/ticked.
	 */
	void update();

	/**
	 * Called before entity/component/controller ends its lifecycle.
	 */
	default void exit() {

	}

	/**
	 * Called when an entity/component/controller is started.
	 */
	default void start() {

	}

	/**
	 * Called when an entity/component/controller is stopped.
	 */
	default void stop() {

	}

	/**
	 * Tells if an entity/component/controller has completed its lifecycle.
	 */
	default boolean isComplete() {
		return true;
	}
}