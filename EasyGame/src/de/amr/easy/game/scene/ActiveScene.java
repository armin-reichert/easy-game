package de.amr.easy.game.scene;

import de.amr.easy.game.Application;
import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.ViewController;

/**
 * Active scenes get called by the framework with every pulse.
 * 
 * @author Armin Reichert
 *
 * @param <A>
 *          type of application
 */
public abstract class ActiveScene<A extends Application> extends PassiveScene<A> implements ViewController {

	public ActiveScene(A app) {
		super(app);
	}

	@Override
	public Controller getController() {
		return this;
	}

	@Override
	public void update() {
	}
}