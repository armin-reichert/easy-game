package de.amr.easy.game.view;

import java.awt.Graphics2D;

/**
 * Common interface for objects that can draw themselves into the application canvas.
 * 
 * @author Armin Reichert
 */
public interface View {

	/**
	 * Called by the framework to initialize the view.
	 */
	public void init();

	/**
	 * Called by the framework to draw the view.
	 * 
	 * @param g
	 *          the graphics context used for drawing
	 */
	public void draw(Graphics2D g);
}
