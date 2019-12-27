package de.amr.easy.game.view;

import java.awt.Graphics2D;

import de.amr.easy.game.controller.Lifecycle;

/**
 * A drawable screen area.
 * 
 * @author Armin Reichert
 */
public interface View extends Lifecycle {

	/**
	 * Called by the framework to draw the view.
	 * 
	 * @param g the graphics context used for drawing
	 */
	void draw(Graphics2D g);
}
