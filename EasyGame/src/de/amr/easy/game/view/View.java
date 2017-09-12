package de.amr.easy.game.view;

import java.awt.Graphics2D;

/**
 * Common interface for objects that can draw themselves into the application area.
 * 
 * @author Armin Reichert
 */
public interface View {

	/**
	 * Called by the game framework when an object should draw itself.
	 * 
	 * @param g
	 *          the graphics context to draw with
	 */
	public void draw(Graphics2D g);
}
