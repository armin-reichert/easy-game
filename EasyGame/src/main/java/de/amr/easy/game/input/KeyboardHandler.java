package de.amr.easy.game.input;

import static java.awt.event.KeyEvent.getKeyModifiersText;
import static java.awt.event.KeyEvent.getKeyText;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;

/**
 * Listens to keyboard events and stores their state in bitmaps. The game loop
 * polls the state at each clock tick.
 * 
 * @author Armin Reichert
 */
public class KeyboardHandler implements KeyListener {

	int modifiers;
	boolean shift;
	boolean alt;
	boolean altGraph;
	boolean control;

	BitSet pressed = new BitSet();
	BitSet pressed_once = new BitSet();
	BitSet pressed_longer = new BitSet();

	public synchronized void poll() {
		for (int code = 1; code < Math.min(pressed.size(), 1024); ++code) {
			if (Keyboard.isModifier(code)) {
				continue;
			}
			if (!pressed.get(code)) {
				pressed_once.clear(code);
				pressed_longer.clear(code);
			} else if (!pressed_once.get(code) && !pressed_longer.get(code)) {
				// this key is pressed for the first time
				pressed_once.set(code, true);
				Keyboard.LOGGER.info(String.format("Key pressed first time: '%s'", text(code)));
			} else {
				// this key is pressed for two or more frames
				pressed_once.set(code, false);
				pressed_longer.set(code, true);
			}
		}
	}

	@Override
	public synchronized void keyPressed(KeyEvent e) {
		modifiers = e.getModifiers();
		shift = e.isShiftDown();
		alt = e.isAltDown();
		altGraph = e.isAltGraphDown();
		control = e.isControlDown();
		if (!Keyboard.isModifier(e.getKeyCode())) {
			pressed.set(e.getKeyCode());
		}
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		modifiers = e.getModifiers();
		shift = e.isShiftDown();
		alt = e.isAltDown();
		altGraph = e.isAltGraphDown();
		control = e.isControlDown();
		if (!Keyboard.isModifier(e.getKeyCode())) {
			pressed.clear(e.getKeyCode());
		}
	}

	@Override
	public synchronized void keyTyped(KeyEvent e) {
		// not used
	}

	private String text(int keyCode) {
		return modifiers != 0 ? getKeyModifiersText(modifiers) + "+" + getKeyText(keyCode) : getKeyText(keyCode);
	}
}