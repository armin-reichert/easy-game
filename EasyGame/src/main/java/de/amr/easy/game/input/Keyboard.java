package de.amr.easy.game.input;

import static de.amr.easy.game.input.KeyboardHandler.KEYBOARD;

import java.awt.event.KeyEvent;

/**
 * The keyboard state. This state is updated at each clock tick.
 * 
 * @author Armin Reichert
 */
public class Keyboard {

	public enum Modifier {
		ALT, CONTROL, SHIFT
	}

	public static boolean keyPressedOnce(Modifier modifier, int key) {
		switch (modifier) {
		case ALT:
			return isAltDown() && KEYBOARD.pressedOnce(key);
		case CONTROL:
			return isControlDown() && KEYBOARD.pressedOnce(key);
		case SHIFT:
			return isShiftDown() && KEYBOARD.pressedOnce(key);
		default:
			return false;
		}
	}

	public static boolean isModifier(int keyCode) {
		return keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT;
	}

	public static boolean keyPressedOnce(int key) {
		return KEYBOARD.pressedOnce(key) && !isAltDown() && !isControlDown() && !isShiftDown();
	}

	public static boolean keyDown(int key) {
		return KEYBOARD.pressedLonger(key) || KEYBOARD.pressedOnce(key);
	}

	public static boolean isShiftDown() {
		return KEYBOARD.shift;
	}

	public static boolean isAltDown() {
		return KEYBOARD.alt;
	}

	public static boolean isControlDown() {
		return KEYBOARD.control;
	}
}