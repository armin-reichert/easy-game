package de.amr.easy.game;

import de.amr.easy.game.config.AppSettings;

/**
 * Base class for applications with default settings object and command-line
 * parsing.
 * 
 * @author Armin Reichert
 */
public abstract class GenericApplication extends Application<AppSettings> {

	@Override
	public AppSettings createAppSettings() {
		return new AppSettings();
	}
}
