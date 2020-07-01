package de.amr.easy.game.ui.f2dialog;

import java.util.function.BooleanSupplier;

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
	 * @param content tab content
	 * @param fnEnabled supplies the enabled state of the tab
	 */
	void addCustomTab(String title, JComponent content, BooleanSupplier fnEnabled);

	/**
	 * Adds a custom tab at the end of the tabbed pane in the dialog.
	 * 
	 * @param title     tab title text
	 * @param content tab content
	 */
	default void addCustomTab(String title, JComponent content) {
		addCustomTab(title, content, () -> true);
	}

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
