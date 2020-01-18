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

	private static KeyCodes codes = new KeyCodes();

	// this is set by the application
	public static KeyboardHandler handler;

	private static int code(String key) {
		return codes.get(key.toLowerCase());
	}

	public static boolean isModifier(int code) {
		return code == KeyEvent.VK_SHIFT || code == KeyEvent.VK_CONTROL || code == KeyEvent.VK_ALT
				|| code == KeyEvent.VK_ALT_GRAPH;
	}

	public static boolean isModifierDown() {
		return isShiftDown() || isControlDown() || isAltDown() || isAltGraphDown();
	}

	public static boolean keyPressedOnce(String key) {
		return keyPressedOnce(code(key));
	}

	public static boolean keyPressedOnce(Modifier modifier, String key) {
		return keyPressedOnce(modifier, code(key));
	}

	public static boolean keyPressedOnce(int code) {
		if (isModifierDown()) {
			return false;
		}
		return handler.pressedOnce(code);
	}

	public static boolean keyPressedOnce(Modifier modifier, int code) {
		if (!handler.pressedOnce(code)) {
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

	public static boolean keyDown(int code) {
		return handler.pressedLonger(code) || handler.pressedOnce(code);
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