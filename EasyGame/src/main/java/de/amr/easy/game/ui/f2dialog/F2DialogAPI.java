package de.amr.easy.game.ui.f2dialog;

import javax.swing.JComponent;

public interface F2DialogAPI {

	void addTab(String title, JComponent component);

	void selectCustomTab(int i);

	void selectTab(int i);
}
