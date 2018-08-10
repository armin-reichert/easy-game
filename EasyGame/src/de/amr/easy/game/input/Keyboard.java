package de.amr.easy.game.input;

import static de.amr.easy.game.input.KeyboardHandler.INSTANCE;

public class Keyboard {
	
	public static boolean keyPressedOnce(int key) {
		return INSTANCE.pressedOnce(key);
	}

	public static boolean keyPressedOnce(int modifier, int key) {
		return keyDown(modifier) && INSTANCE.pressedOnce(key);
	}

	public static boolean keyDown(int key) {
		return INSTANCE.pressed(key) || INSTANCE.pressedOnce(key);
	}
}