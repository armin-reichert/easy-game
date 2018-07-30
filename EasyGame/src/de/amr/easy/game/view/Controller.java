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
	void init();

	/**
	 * Controller update hook.
	 */
	void update();

	/**
	 * @return the currently displayed view
	 */
	View currentView();

}