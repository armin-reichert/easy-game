package de.amr.easy.game.view;

/**
 * A view which serves as controller too.
 * 
 * @author Armin Reichert
 */
public interface ViewController extends View, Controller {

	@Override
	default View currentView() {
		return this;
	}

}