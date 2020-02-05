package de.amr.easy.game.tests;

import de.amr.easy.game.Application;
import de.amr.easy.game.config.AppSettings;
import de.amr.easy.game.ui.FramerateHistoryView;

public class FramerateTestApp extends Application {

	public static void main(String[] args) {
		launch(FramerateTestApp.class, args);
	}

	@Override
	protected void configure(AppSettings settings) {
		settings.title = "Game performance measurement";
		settings.titleExtended = true;
		settings.width = 600;
		settings.height = 250;
	}

	@Override
	public void init() {
		FramerateHistoryView viewController = new FramerateHistoryView(600, 250);
		viewController.setApp(this);
		setController(viewController);
	}
}