package de.amr.easy.game.tests;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

import de.amr.easy.game.Application;
import de.amr.easy.game.config.AppSettings;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.entity.Entity;
import de.amr.easy.game.input.Keyboard;
import de.amr.easy.game.input.Keyboard.Modifier;

public class KeyboardTestApp extends Application {

	public static void main(String[] args) {
		launch(KeyboardTestApp.class, args);
	}

	@Override
	protected void configure(AppSettings settings) {
		settings.title = "Keyboard Test App";
	}

	@Override
	public void init() {
		setController(new KeyboardTestScene());
	}
}

class KeyboardTestScene extends Entity implements Lifecycle {

	private int pressedKeyCode;

	public int getWidth() {
		return 600;
	}

	public int getHeight() {
		return 400;
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		pressedKeyCode = -1;
		for (int code = 0; code < 0xFFFF; ++code) {
			if (Keyboard.keyDown(code) || Keyboard.keyPressedOnce(code)) {
				pressedKeyCode = code;
			}
			for (Modifier modifier : Modifier.values()) {
				if (Keyboard.keyDown(modifier, code) || Keyboard.keyPressedOnce(modifier, code)) {
					pressedKeyCode = code;
				}
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		String text = "";
		if (Keyboard.isAltDown())
			text += "Alt ";
		if (Keyboard.isControlDown())
			text += "Control ";
		if (Keyboard.isShiftDown())
			text += "Shift ";
		if (Keyboard.isAltGraphDown())
			text += "AltGr";
		if (pressedKeyCode != -1) {
			text += KeyEvent.getKeyText(pressedKeyCode);
		}
		if ("".equals(text)) {
			text = "Hold some key and some modifier keys!";
		}
		FontMetrics fm = g.getFontMetrics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
}