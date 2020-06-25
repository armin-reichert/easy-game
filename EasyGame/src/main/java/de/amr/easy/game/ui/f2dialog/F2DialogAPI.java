package de.amr.easy.game.ui.f2dialog;

import javax.swing.JComponent;

/**
 * API for accessing F2 dialog.
 * 
 * @author Armin Reichert
 *
 */
public interface F2DialogAPI {

	/**
	 * Adds a custom tab at the end of the tabbed pane in the dialog.
	 * 
	 * @param title     tab title text
	 * @param component tab content
	 */
	void addCustomTab(String title, JComponent content);

	/**
	 * Selects the i'th custom tab.
	 * 
	 * @param i index of custom tab (0-based)
	 */
	void selectCustomTab(int i);

	/**
	 * Selects the i'th tab.
	 * 
	 * @param i index of tab (0-based)
	 */
	void selectTab(int i);
}
