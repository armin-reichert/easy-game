package de.amr.easy.game.view;

/**
 * Common interface for objects that can draw themselves into the application canvas.
 * 
 * @author Armin Reichert
 */
public interface View<GraphicsContext> {

	/**
	 * @return the width in pixels
	 */
	int getWidth();

	/**
	 * @return the height in pixels
	 */
	int getHeight();

	/**
	 * Called by the framework to draw the view.
	 * 
	 * @param g
	 *          the graphics context used for drawing
	 */
	void draw(GraphicsContext g);
}
