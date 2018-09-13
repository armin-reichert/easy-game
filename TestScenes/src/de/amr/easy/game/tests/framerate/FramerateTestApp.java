package de.amr.easy.game.tests.framerate;

import de.amr.easy.game.Application;

public class FramerateTestApp extends Application {

	public static void main(String[] args) {
		launch(new FramerateTestApp());
	}

	public FramerateTestApp() {
		settings.title = "Game performance measurement";
		settings.titleExtended = true;
		settings.width = 1000;
		// PULSE.setLogger(LOG);
		clock.setFrequency(100);
	}

	@Override
	public void init() {
		setController(new FramerateTestScene(this));
	}
}
