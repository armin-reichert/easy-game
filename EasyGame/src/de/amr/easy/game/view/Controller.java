package de.amr.easy.game.view;

/**
 * Common interface for controllers.
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

	/**
	 * @return the current view
	 */
	View currentView();
}