package de.amr.easy.game.input;

import static de.amr.easy.game.input.KeyboardHandler.INSTANCE;

public class Keyboard {

	public enum Modifier { ALT, CONTROL, SHIFT }
	
	public static boolean keyPressedOnce(Modifier modifier, int key) {
		if (modifier == Modifier.ALT) {
			return isAltDown() && INSTANCE.pressedOnce(key);
		}
		if (modifier == Modifier.CONTROL) {
			return isControlDown() && INSTANCE.pressedOnce(key);
		}
		if (modifier == Modifier.SHIFT) {
			return isShiftDown() && INSTANCE.pressedOnce(key);
		}
		return false;
	}
	
	public static boolean keyPressedOnce(int key) {
		return INSTANCE.pressedOnce(key);
	}

	public static boolean keyDown(int key) {
		return INSTANCE.pressed(key) || INSTANCE.pressedOnce(key);
	}

	public static boolean isShiftDown() {
		return INSTANCE.isShiftDown();
	}

	public static boolean isAltDown() {
		return INSTANCE.isAltDown();
	}

	public static boolean isControlDown() {
		return INSTANCE.isControlDown();
	}
}