package de.amr.easy.game.view;

/**
 * Common interface for controllers.
 * 
 * <p>
 * A controller handles update events which for example are supplied by a clock
 * ("ticks").
 * 
 * @author Armin Reichert
 */
public interface Controller {

	/**
	 * Initialization hook.
	 */
	void init();

	/**
	 * Update hook.
	 */
	void update();
}