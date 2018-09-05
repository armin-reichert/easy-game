package de.amr.easy.game.tests;

import de.amr.easy.game.Application;

public class KeyboardTestApp extends Application {

	public static void main(String[] args) {
		launch(new KeyboardTestApp());
	}

	@Override
	public void init() {
		setController(new KeyboardTestScene());
	}

}
