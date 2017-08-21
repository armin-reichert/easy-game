package de.amr.easy.game.input;

import java.awt.event.MouseEvent;

public class Mouse {

	public static boolean clicked() {
		return MouseHandler.INSTANCE.clicked;
	}

	public static boolean pressed() {
		return MouseHandler.INSTANCE.pressed;
	}

	public static boolean released() {
		return MouseHandler.INSTANCE.released;
	}

	public static boolean moved() {
		return MouseHandler.INSTANCE.moved;
	}

	public static boolean dragged() {
		return MouseHandler.INSTANCE.dragged;
	}

	public static int getX() {
		return MouseHandler.INSTANCE.x;
	}

	public static int getY() {
		return MouseHandler.INSTANCE.y;
	}

	public static boolean isLeftButton() {
		return MouseHandler.INSTANCE.button == MouseEvent.BUTTON1;
	}

	public static boolean isMiddleButton() {
		return MouseHandler.INSTANCE.button == MouseEvent.BUTTON2;
	}

	public static boolean isRightButton() {
		return MouseHandler.INSTANCE.button == MouseEvent.BUTTON3;
	}
}