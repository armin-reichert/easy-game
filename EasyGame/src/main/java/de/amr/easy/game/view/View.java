package de.amr.easy.game.view;

import java.awt.Graphics2D;

/**
 * A visible screen area that can be drawn into.
 * 
 * @author Armin Reichert
 */
public interface View {

	/**
	 * Tells if this view is visible and will get rendered.
	 * 
	 * @return if this view is visible
	 */
	default boolean visible() {
		return true;
	}

	/**
	 * Sets this view to visible. Does nothing by default.
	 * 
	 * @param visible if this view should become visible
	 */
	default void setVisible(boolean visible) {

	}

	/**
	 * Draws the content of the view.
	 * 
	 * @param g the graphics context used for drawing
	 */
	void draw(Graphics2D g);
}
