package de.amr.easy.game.tests;

import de.amr.easy.game.Application;
import de.amr.easy.game.config.AppSettings;
import de.amr.easy.game.ui.FramerateView;

public class FramerateTestApp extends Application {

	public static void main(String[] args) {
		launch(FramerateTestApp.class, args);
	}

	@Override
	protected void configure(AppSettings settings) {
		settings.title = "Game performance measurement";
		settings.titleExtended = true;
		settings.width = 1000;
		settings.height = 400;
	}

	@Override
	public void init() {
		clock().setFrequency(60);
		setController(new FramerateView(1000, 400));
	}
}
