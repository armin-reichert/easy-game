package de.amr.easy.game.view;

/**
 * Common interface for objects that may draw themselves inside the application canvas
 * ({@link #draw(java.awt.Graphics2D)}) and react to the {@link #init()} and {@link #update()}
 * lifetime events.
 * 
 * @author Armin Reichert
 */
public interface View extends Drawable {

	/**
	 * Called by the game framework when an object should be initialized.
	 */
	public void init();

	/**
	 * Called by the game framework when an object should update its state.
	 */
	public void update();
}