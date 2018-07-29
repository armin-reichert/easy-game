package de.amr.easy.game.scene;

import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.View;
import de.amr.easy.game.view.ViewController;

/**
 * Represents a combined view/controller which handles clock events.
 * 
 * @author Armin Reichert
 */
public interface ActiveScene<GC> extends Scene<GC>, ViewController<GC> {

	@Override
	default Controller getController() {
		return this;
	}

	@Override
	default View<GC> currentView() {
		return this;
	}
}