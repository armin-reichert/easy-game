package de.amr.easy.game.ui;

public class FullScreenModeException extends Exception {

	public FullScreenModeException(String message) {
		super("Cannot enter full-screen mode: " + message);
	}
}