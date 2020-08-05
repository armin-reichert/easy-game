package de.amr.easy.game.tests;

import de.amr.easy.game.Application;
import de.amr.easy.game.config.AppSettings;

public class EmptyApp extends Application {

	public static void main(String[] args) {
		launch(EmptyApp.class, args);
	}

	@Override
	protected void configure(AppSettings settings) {
		settings.title = "Empty App";
	}

	@Override
	public void init() {
	}
}