package de.amr.easy.game.view;

import java.awt.Graphics2D;

/**
 * A <code>View</code> represents a screen area that can be drawn into.
 * 
 * @author Armin Reichert
 */
public interface View {

	/**
	 * Draws the content of the view.
	 * 
	 * @param g the graphics context used for drawing
	 */
	void draw(Graphics2D g);
}
