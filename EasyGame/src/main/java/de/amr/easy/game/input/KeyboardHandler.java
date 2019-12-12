package de.amr.easy.game.input;

import static java.awt.event.KeyEvent.getKeyModifiersText;
import static java.awt.event.KeyEvent.getKeyText;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;

/**
 * Listens to keyboard events and stores their state in bitmaps. The game loop
 * polls the state at each clock tick.
 * 
 * @author Armin Reichert
 *
 */
public enum KeyboardHandler implements KeyListener {

	KEYBOARD; // singleton

	public static void handleKeyEventsFor(Component component) {
		component.addKeyListener(KEYBOARD);
	}

	public static synchronized void poll() {
		KEYBOARD.pollKeyboard();
	}

	private int modifiers;
	public boolean shift;
	public boolean alt;
	public boolean altGraph;
	public boolean control;

	private final BitSet pressed = new BitSet();
	private final BitSet pressedOneFrame = new BitSet();
	private final BitSet pressedTwoFramesOrMore = new BitSet();

	@Override
	public synchronized void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		modifiers = e.getModifiers();
		shift = e.isShiftDown();
		alt = e.isAltDown();
		altGraph = e.isAltGraphDown();
		control = e.isControlDown();
		pressed.set(keyCode);
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		modifiers = 0;
		shift = e.isShiftDown();
		alt = e.isAltDown();
		control = e.isControlDown();
		pressed.clear(keyCode);
	}

	@Override
	public synchronized void keyTyped(KeyEvent e) {
		// not used
	}

	private void pollKeyboard() {
		for (int keyCode = 0; keyCode < pressed.size(); ++keyCode) {
			if (Keyboard.isModifier(keyCode)) {
				continue;
			}
			if (!pressed.get(keyCode)) { // 0 frames
				pressedOneFrame.clear(keyCode);
				pressedTwoFramesOrMore.clear(keyCode);
			} else if (!pressedOneFrame.get(keyCode) && !pressedTwoFramesOrMore.get(keyCode)) { // one frame
				pressedOneFrame.set(keyCode, true);
				pressedTwoFramesOrMore.set(keyCode, false);
				Keyboard.LOGGER.info(String.format("Key pressed once: '%s'", text(keyCode)));
			} else if (pressedOneFrame.get(keyCode)) { // two frames
				pressedOneFrame.set(keyCode, false);
				pressedTwoFramesOrMore.set(keyCode, true);
			} else {
				pressedTwoFramesOrMore.set(keyCode, true); // more than two frames
			}
		}
	}

	private String text(int keyCode) {
		return modifiers != 0 ? getKeyModifiersText(modifiers) + "+" + getKeyText(keyCode) : getKeyText(keyCode);
	}

	boolean pressedOnce(int key) {
		return pressedOneFrame.get(key);
	}

	boolean pressedLonger(int key) {
		return pressedTwoFramesOrMore.get(key);
	}
}