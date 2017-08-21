package de.amr.easy.game.input;

public class Keyboard {

	public static boolean keyPressedOnce(int key) {
		return KeyboardHandler.INSTANCE.pressedOnce(key);
	}

	public static boolean keyPressedOnce(int modifier, int key) {
		return keyDown(modifier) && keyPressedOnce(key);
	}

	public static boolean keyDown(int key) {
		return KeyboardHandler.INSTANCE.pressed(key) || keyPressedOnce(key);
	}
}