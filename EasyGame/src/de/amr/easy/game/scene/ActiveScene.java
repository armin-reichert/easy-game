package de.amr.easy.game.scene;

import de.amr.easy.game.Application;
import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.View;
import de.amr.easy.game.view.ViewController;

/**
 * Represents a combined view/controller which handles clock events.
 * 
 * @author Armin Reichert
 *
 * @param <A>
 *          type of application
 */
public abstract class ActiveScene<A extends Application> extends Scene<A> implements ViewController {

	public ActiveScene(A app) {
		super(app);
	}

	@Override
	public Controller getController() {
		return this;
	}

	@Override
	public View currentView() {
		return this;
	}
}