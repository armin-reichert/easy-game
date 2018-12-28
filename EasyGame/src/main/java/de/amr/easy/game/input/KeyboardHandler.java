package de.amr.easy.game.input;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;

import de.amr.easy.game.Application;

public enum KeyboardHandler implements KeyListener {

	INSTANCE;

	public static void handleKeyEventsFor(Component component) {
		component.addKeyListener(INSTANCE);
	}

	public static synchronized void poll() {
		INSTANCE._poll();
	}

	private boolean shiftDown;
	private boolean altDown;
	private boolean controlDown;
	private final BitSet pressed = new BitSet();
	private final BitSet once = new BitSet();
	private final BitSet longer = new BitSet();

	@Override
	public synchronized void keyPressed(KeyEvent e) {
		pressed.set(e.getKeyCode());
		shiftDown = e.isShiftDown();
		altDown = e.isAltDown();
		controlDown = e.isControlDown();
		Application.LOGGER.fine(e.toString());
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		pressed.clear(e.getKeyCode());
		shiftDown = e.isShiftDown();
		altDown = e.isAltDown();
		controlDown = e.isControlDown();
		Application.LOGGER.fine(e.toString());
	}

	@Override
	public synchronized void keyTyped(KeyEvent e) {
		// not used
	}

	private void _poll() {
		for (int key = 0; key < pressed.size(); ++key) {
			if (pressed.get(key)) {
				if (!once.get(key) && !longer.get(key)) {
					once.set(key);
					Application.LOGGER.fine("Pressed first time " + key);
				} else if (once.get(key)) {
					once.clear(key);
					longer.set(key);
				}
			} else {
				once.clear(key);
				longer.clear(key);
			}
		}
	}

	boolean pressedOnce(int key) {
		return once.get(key);
	}

	boolean pressed(int key) {
		return longer.get(key);
	}

	boolean isShiftDown() {
		return shiftDown;
	}

	boolean isAltDown() {
		return altDown;
	}

	boolean isControlDown() {
		return controlDown;
	}
}