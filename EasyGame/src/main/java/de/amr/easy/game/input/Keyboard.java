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
		ALT, ALT_GRAPH, CONTROL, SHIFT
	}

	public static boolean isModifier(int keyCode) {
		return keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT
				|| keyCode == KeyEvent.VK_ALT_GRAPH;
	}

	public static boolean isModifierDown() {
		return isShiftDown() || isControlDown() || isAltDown() || isAltGraphDown();
	}

	public static boolean keyPressedOnce(int keyCode) {
		if (isModifierDown()) {
			return false;
		}
		return KEYBOARD.pressedOnce(keyCode);
	}

	public static boolean keyPressedOnce(Modifier modifier, int keyCode) {
		if (!KEYBOARD.pressedOnce(keyCode)) {
			return false;
		}
		switch (modifier) {
		case ALT:
			return isAltDown();
		case ALT_GRAPH:
			return isAltGraphDown();
		case CONTROL:
			return isControlDown();
		case SHIFT:
			return isShiftDown();
		default:
			return false;
		}
	}

	public static boolean keyDown(int key) {
		return !isModifierDown() && (KEYBOARD.pressedLonger(key) || KEYBOARD.pressedOnce(key));
	}

	public static boolean isShiftDown() {
		return KEYBOARD.shift;
	}

	public static boolean isControlDown() {
		return KEYBOARD.control;
	}

	public static boolean isAltDown() {
		return KEYBOARD.alt;
	}

	public static boolean isAltGraphDown() {
		return KEYBOARD.altGraph;
	}
}