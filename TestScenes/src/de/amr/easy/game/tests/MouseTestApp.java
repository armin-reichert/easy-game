package de.amr.easy.game.tests;

import de.amr.easy.game.Application;

public class MouseTestApp extends Application {

	public static void main(String[] args) {
		launch(new MouseTestApp());
	}

	public MouseTestApp() {
		settings.title = "Mouse Test";
		settings.width = 800;
		settings.height = 600;
	}

	@Override
	public void init() {
		setController(new MouseTestScene(this));
	}
}
