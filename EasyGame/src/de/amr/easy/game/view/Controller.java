package de.amr.easy.game.view;

/**
 * Common interface for controllers.
 * 
 * @author Armin Reichert
 */
public interface Controller {

	/**
	 * Controller intialization hook.
	 */
	default void init() {
		
	}

	/**
	 * Controller update hook.
	 */
	default void update() {
		
	}

	/**
	 * @return the currently displayed view
	 */
	View currentView();

}