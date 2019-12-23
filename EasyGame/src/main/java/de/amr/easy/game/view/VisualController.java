package de.amr.easy.game.view;

import java.util.Optional;

/**
 * A visual controller additionally provides a view to be displayed.
 * 
 * @author Armin Reichert
 */
public interface VisualController extends Lifecycle {

	/**
	 * @return the current view to be displayed
	 */
	Optional<View> currentView();

}
