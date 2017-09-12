package de.amr.easy.game.view;

public interface Controller {

	/**
	 * Called by the framework when a controller should be initialized.
	 */
	public void init();

	/**
	 * Called by the framework when a controller should update its state.
	 */
	public void update();

}