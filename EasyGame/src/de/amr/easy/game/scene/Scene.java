package de.amr.easy.game.scene;

import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.View;

/**
 * Represents a view for a given application. This class expects a separate controller and does not
 * handle clock events by itself.
 * 
 * @author Armin Reichert
 */
public interface Scene extends View {

	/**
	 * @return the controller which handles the clock events
	 */
	Controller getController();

	/**
	 * @return the width in pixels
	 */
	int getWidth();

	/**
	 * @return the height in pixels
	 */
	int getHeight();
}