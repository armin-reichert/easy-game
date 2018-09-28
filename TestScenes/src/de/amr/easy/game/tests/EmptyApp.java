package de.amr.easy.game.tests;

import de.amr.easy.game.Application;

public class EmptyApp extends Application {

	@Override
	public void init() {
	}

	public static void main(String[] args) {
		launch(new EmptyApp(), args);
	}
	
	
	public EmptyApp() {
		settings.fullScreenMode = null;
	}
}