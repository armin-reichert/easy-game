package de.amr.easy.game.tests.keyboard;

import de.amr.easy.game.Application;

public class KeyboardTestApp extends Application {

	public static void main(String[] args) {
		launch(new KeyboardTestApp(), args);
	}

	@Override
	public void init() {
		setController(new KeyboardTestScene());
	}

}
