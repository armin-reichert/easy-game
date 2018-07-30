package de.amr.easy.game.view;

/**
 * Interface for objects that are view and controller at the same time.
 * 
 * @author Armin Reichert
 */
public interface ViewController extends View, Controller {
	
	@Override
	default View currentView() {
		return this;
	}

}
