package de.amr.easy.game.scene;

import de.amr.easy.game.Application;
import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.View;

/**
 * Base class for active scenes of an application. An active scene is called by the framework to initialize and update
 * itself.
 * 
 * @author Armin Reichert
 *
 * @param <A>
 *          type of application
 */
public abstract class ActiveScene<A extends Application> extends PassiveScene<A> implements View, Controller {

	public ActiveScene(A app) {
		super(app);
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}
}