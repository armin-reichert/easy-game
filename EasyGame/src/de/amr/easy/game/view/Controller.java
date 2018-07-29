package de.amr.easy.game.view;

/**
 * Common interface for controllers.
 * 
 * @author Armin Reichert
 */
public interface Controller {

	/**
	 * Called by the framework when a controller should be initialized.
	 */
	void init();

	/**
	 * Called by the framework when a controller should update its state.
	 */
	void update();

	/**
	 * Returns the current view.
	 * 
	 * @return the current view
	 */
	View currentView();

}