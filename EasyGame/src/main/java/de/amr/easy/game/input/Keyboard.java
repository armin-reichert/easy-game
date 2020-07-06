package de.amr.easy.game.input;

import static java.awt.event.KeyEvent.getKeyText;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.amr.easy.game.Application;

/**
 * The keyboard state which is updated at each clock tick.
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

	private static void loginfo(String msg, Object... args) {
		LOGGER.info(String.format(msg, args));
	}

	private static final KeyboardState keyboardState = new KeyboardState();
	private static final KeyCodes codes = new KeyCodes();

	private static int code(String keyString) {
		return codes.get(keyString.toLowerCase());
	}

	public static synchronized void poll() {
		keyboardState.update();
	}

	public static void listenTo(Component component) {
		component.addKeyListener(keyboardState);
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
		boolean pressed = !isModifierDown() && keyboardState.pressedOnce.get(keyCode);
		if (pressed) {
			loginfo("Key pressed (no modifiers): %d", keyCode);
		}
		return pressed;
	}

	public static boolean keyPressedOnce(Modifier modifier, int keyCode) {
		switch (modifier) {
		case ALT:
			return isAltDown() && keyboardState.pressedOnce.get(keyCode);
		case ALT_GRAPH:
			return isAltGraphDown() && keyboardState.pressedOnce.get(keyCode);
		case CONTROL:
			return isControlDown() && keyboardState.pressedOnce.get(keyCode);
		case SHIFT:
			return isShiftDown() && keyboardState.pressedOnce.get(keyCode);
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
		return keyboardState.pressedLonger.get(keyCode) || keyboardState.pressedOnce.get(keyCode);
	}

	public static boolean isShiftDown() {
		return keyboardState.shift;
	}

	public static boolean isControlDown() {
		return keyboardState.control;
	}

	public static boolean isAltDown() {
		return keyboardState.alt;
	}

	public static boolean isAltGraphDown() {
		return keyboardState.altGraph;
	}

	private static class KeyboardState implements KeyListener {

		private int modifiers;
		private boolean shift;
		private boolean alt;
		private boolean altGraph;
		private boolean control;

		private final BitSet pressedNow = new BitSet();
		private final BitSet pressedOnce = new BitSet();
		private final BitSet pressedLonger = new BitSet();

		@Override
		public synchronized void keyPressed(KeyEvent e) {
			modifiers = e.getModifiersEx();
			shift = e.isShiftDown();
			alt = e.isAltDown();
			altGraph = e.isAltGraphDown();
			control = e.isControlDown();
			if (!isModifier(e.getKeyCode())) {
				pressedNow.set(e.getKeyCode());
			}
		}

		@Override
		public synchronized void keyReleased(KeyEvent e) {
			modifiers = e.getModifiersEx();
			shift = e.isShiftDown();
			alt = e.isAltDown();
			altGraph = e.isAltGraphDown();
			control = e.isControlDown();
			if (!isModifier(e.getKeyCode())) {
				pressedNow.clear(e.getKeyCode());
			}
		}

		@Override
		public synchronized void keyTyped(KeyEvent e) {
			// not used
		}

		private void update() {
			long time = Application.app().clock().getTotalTicks();
			for (int key = 1; key < Math.min(pressedNow.size(), 1024); ++key) {
				if (isModifier(key)) {
					continue;
				}
				if (!pressedNow.get(key)) {
					if (pressedOnce.get(key) || pressedLonger.get(key)) {
						loginfo("Time: %d: Key not pressed anymore: '%s'", time, text(key));
					}
					pressedOnce.set(key, false);
					pressedLonger.set(key, false);
					continue;
				}
				if (pressedOnce.get(key)) {
					// key was already pressed in last frame
					pressedOnce.set(key, false);
					pressedLonger.set(key, true);
					loginfo("Time: %d: Key stays pressed: '%s'", time, text(key));
					continue;
				}
				if (!pressedLonger.get(key)) {
					// key is pressed for the first time
					loginfo("Time: %d; Key pressed first time: '%s'", time, text(key));
					pressedOnce.set(key, true);
					pressedLonger.set(key, false);
				}
			}
		}

		private boolean isModifier(int keyCode) {
			return (keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT
					|| keyCode == KeyEvent.VK_ALT_GRAPH);
		}

		private String text(int keyCode) {
			String modifiersText = InputEvent.getModifiersExText(modifiers);
			return modifiersText.length() > 0 ? modifiersText + "+" + getKeyText(keyCode) : getKeyText(keyCode);
		}
	}
}