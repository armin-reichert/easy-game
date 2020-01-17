package de.amr.easy.game.input;

import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The keyboard state. This state is updated at each clock tick.
 * 
 * @author Armin Reichert
 */
public class Keyboard {

	public enum Modifier {
		ALT, ALT_GRAPH, CONTROL, SHIFT
	}

	static final Logger LOGGER = Logger.getLogger(Keyboard.class.getName());

	static {
		LOGGER.setLevel(Level.OFF);
	}

	public static KeyboardHandler handler;

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
		return handler.pressedOnce(keyCode);
	}

	public static boolean keyPressedOnce(Modifier modifier, int keyCode) {
		if (!handler.pressedOnce(keyCode)) {
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
		return !isModifierDown() && (handler.pressedLonger(key) || handler.pressedOnce(key));
	}

	public static boolean isShiftDown() {
		return handler.shift;
	}

	public static boolean isControlDown() {
		return handler.control;
	}

	public static boolean isAltDown() {
		return handler.alt;
	}

	public static boolean isAltGraphDown() {
		return handler.altGraph;
	}
}