package de.amr.easy.game.input;

import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON2_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;
import static java.awt.event.MouseEvent.NOBUTTON;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.Supplier;

public enum MouseHandler implements MouseListener, MouseMotionListener {

	INSTANCE;

	public static void handleMouseEventsFor(Component component) {
		component.addMouseListener(INSTANCE);
		component.addMouseMotionListener(INSTANCE);
	}

	public static synchronized void poll() {
		INSTANCE._poll();
	}
	
	public Supplier<Float> fnScale = () -> 1f;
	
	boolean clicked;
	boolean pressed;
	boolean released;
	boolean moved;
	boolean dragged;
	int x;
	int y;
	int button;
	
	private boolean clickedDetected;
	private boolean pressedDetected;
	private boolean releasedDetected;
	private boolean movedDetected;
	private boolean draggedDetected;
	private MouseEvent event;

	private synchronized void _poll() {
		clicked = clickedDetected;
		pressed = pressedDetected;
		released = releasedDetected;
		moved = movedDetected;
		dragged = draggedDetected;
		x = event != null ? Math.round(event.getX() / fnScale.get()) : -1;
		y = event != null ? Math.round(event.getY() / fnScale.get()) : -1;

		// if (event != null) {
		// Application.LOG.info(event.toString());
		// }

		clickedDetected = pressedDetected = releasedDetected = movedDetected = draggedDetected = false;
		event = null;
	}

	@Override
	public synchronized void mouseClicked(MouseEvent event) {
		this.event = event;
		clickedDetected = true;
		button = event.getButton();
	}

	@Override
	public synchronized void mousePressed(MouseEvent event) {
		this.event = event;
		pressedDetected = true;
		button = event.getButton();
	}

	@Override
	public synchronized void mouseReleased(MouseEvent event) {
		this.event = event;
		releasedDetected = true;
		button = event.getButton();
	}

	@Override
	public synchronized void mouseEntered(MouseEvent event) {
		this.event = event;
	}

	@Override
	public synchronized void mouseExited(MouseEvent event) {
		this.event = event;
	}

	@Override
	public synchronized void mouseDragged(MouseEvent event) {
		this.event = event;
		draggedDetected = true;
		findButtonFromModifiers(event.getModifiersEx());
	}

	@Override
	public synchronized void mouseMoved(MouseEvent event) {
		this.event = event;
		movedDetected = true;
		button = NOBUTTON;
	}

	private void findButtonFromModifiers(int modifiers) {
		if ((modifiers & BUTTON1_DOWN_MASK) != 0) {
			button = 1;
		} else if ((modifiers & BUTTON2_DOWN_MASK) != 0) {
			button = 2;
		} else if ((modifiers & BUTTON3_DOWN_MASK) != 0) {
			button = 3;
		}
	}
};
