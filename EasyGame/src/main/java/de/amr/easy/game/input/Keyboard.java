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

	public static boolean isModifier(int keyCode) {
		return keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT
				|| keyCode == KeyEvent.VK_ALT_GRAPH;
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

	public static boolean keyPressedOnce(int keyCode) {
		return !isModifierDown() && handler.pressedOnce(keyCode);
	}

	public static boolean keyPressedOnce(Modifier modifier, int keyCode) {
		switch (modifier) {
		case ALT:
			return isAltDown() && handler.pressedOnce(keyCode);
		case ALT_GRAPH:
			return isAltGraphDown() && handler.pressedOnce(keyCode);
		case CONTROL:
			return isControlDown() && handler.pressedOnce(keyCode);
		case SHIFT:
			return isShiftDown() && handler.pressedOnce(keyCode);
		default:
			return false;
		}
	}

	public static boolean keyDown(Modifier modifier, int keyCode) {
		switch (modifier) {
		case ALT:
			return isAltDown() && pressedOnceOrLonger(keyCode);
		case ALT_GRAPH:
			return isAltGraphDown() && pressedOnceOrLonger(keyCode);
		case CONTROL:
			return isControlDown() && pressedOnceOrLonger(keyCode);
		case SHIFT:
			return isShiftDown() && pressedOnceOrLonger(keyCode);
		default:
			return pressedOnceOrLonger(keyCode);
		}
	}

	public static boolean keyDown(int keyCode) {
		return !isModifierDown() && pressedOnceOrLonger(keyCode);
	}
	
	private static boolean pressedOnceOrLonger(int keyCode) {
		return handler.pressedLonger(keyCode) || handler.pressedOnce(keyCode);
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