package de.amr.easy.game.scene;

import de.amr.easy.game.Application;
import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.ViewController;

/**
 * Represents a view/controller combination for a given application, handles clock updates by itself.
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
}