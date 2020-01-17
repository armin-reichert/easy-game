package de.amr.easy.game.input;

import java.awt.event.MouseEvent;

/**
 * Static facade for accessing mouse input.
 * 
 * @author Armin Reichert
 *
 */
public class Mouse {

	public static MouseHandler handler;

	public static boolean clicked() {
		return handler.clicked;
	}

	public static boolean pressed() {
		return handler.pressed;
	}

	public static boolean released() {
		return handler.released;
	}

	public static boolean moved() {
		return handler.moved;
	}

	public static boolean dragged() {
		return handler.dragged;
	}

	public static int getX() {
		return handler.x;
	}

	public static int getY() {
		return handler.y;
	}

	public static boolean isLeftButton() {
		return handler.button == MouseEvent.BUTTON1;
	}

	public static boolean isMiddleButton() {
		return handler.button == MouseEvent.BUTTON2;
	}

	public static boolean isRightButton() {
		return handler.button == MouseEvent.BUTTON3;
	}
}