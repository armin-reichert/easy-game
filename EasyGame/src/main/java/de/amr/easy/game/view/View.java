package de.amr.easy.game.view;

import java.awt.Graphics2D;

/**
 * A visible screen area that can be drawn into.
 * 
 * @author Armin Reichert
 */
public interface View {

	/**
	 * Makes the view visible.
	 */
	void show();

	/**
	 * Makes the view invisible.
	 * 
	 */
	void hide();

	/**
	 * Draws the content of the view.
	 * 
	 * @param g
	 *            the graphics context used for drawing
	 */
	void draw(Graphics2D g);
}
