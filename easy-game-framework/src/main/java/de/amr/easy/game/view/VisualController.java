package de.amr.easy.game.view;

import java.util.Optional;

import de.amr.easy.game.controller.Lifecycle;

/**
 * A controller providing a view.
 * 
 * @author Armin Reichert
 */
public interface VisualController extends Lifecycle {

	/**
	 * @return the view to be displayed
	 */
	Optional<View> currentView();
}