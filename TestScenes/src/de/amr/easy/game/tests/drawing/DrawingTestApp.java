package de.amr.easy.game.tests.drawing;

import de.amr.easy.game.Application;

public class DrawingTestApp extends Application {

	public static void main(String[] args) {
		launch(new DrawingTestApp());
	}

	public DrawingTestApp() {
		settings.title = "Drawing Test";
		settings.width = 800;
		settings.height = 600;
	}

	@Override
	public void init() {
		setController(new DrawingTestScene(this));
	}

}