package de.amr.easy.game.ui.f2dialog;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

import de.amr.easy.game.Application;

/**
 * When the {@link F2DialogAPI} is used while the UI is not yet created, for example inside the
 * {@link Application#init()} method, this class buffers all API calls and adds the tabs when the
 * application shell is created.
 * 
 * @author Armin Reichert
 */
public class F2DialogBuffer implements F2DialogAPI {

	private Map<String, JComponent> tabs = new LinkedHashMap<>();
	private int selection = -1;

	@Override
	public void addTab(String title, JComponent component) {
		tabs.put(title, component);
	}

	@Override
	public void selectTab(int i) {
		selection = i;
	}

	@Override
	public void selectCustomTab(int i) {
		selection = F2Dialog.CUSTOM_TABS_START + i;
	}

	public void addTo(F2Dialog dialog) {
		for (Map.Entry<String, JComponent> entry : tabs.entrySet()) {
			dialog.getTabbedPane().addTab(entry.getKey(), entry.getValue());
		}
		if (selection != -1) {
			dialog.selectTab(selection);
		}
	}
}