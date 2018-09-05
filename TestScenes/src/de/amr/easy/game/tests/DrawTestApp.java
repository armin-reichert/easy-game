package de.amr.easy.game.tests;

import de.amr.easy.game.Application;

public class DrawTestApp extends Application {

	public static void main(String[] args) {
		launch(new DrawTestApp());
	}

	public DrawTestApp() {
		settings.title = "Drawing Test";
		settings.width = 800;
		settings.height = 600;
	}

	@Override
	public void init() {
		setController(new DrawTestScene(this));
	}

}