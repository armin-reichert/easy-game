package de.amr.games.perf;

import de.amr.easy.game.Application;

public class GamePerformanceApp extends Application {

	public static void main(String[] args) {
		launch(new GamePerformanceApp());
	}

	public GamePerformanceApp() {
		settings.title = "Game performance measurement";
		settings.titleExtended = true;
		settings.width = 1000;
		// PULSE.setLogger(LOG);
		CLOCK.setFrequency(100);
	}

	@Override
	public void init() {
		setController(new MainScene(this));
	}
}
