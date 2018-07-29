package de.amr.easy.game.view;

/**
 * A combined view/controller.
 * 
 * @author Armin Reichert
 */
public interface ViewController extends View, Controller {

	View currentView();
}