package de.amr.easy.game.view;

/**
 * Interface providing lifecycle hook methods (init, update).
 * 
 * @author Armin Reichert
 */
public interface Lifecycle {

	/**
	 * Initialization hook.
	 */
	void init();

	/**
	 * Update hook.
	 */
	void update();
}