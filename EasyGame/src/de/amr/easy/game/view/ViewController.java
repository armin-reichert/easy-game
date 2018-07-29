package de.amr.easy.game.view;

/**
 * A combined view/controller.
 * 
 * @author Armin Reichert
 */
public interface ViewController<GC> extends Controller {

	View<GC> currentView();
}