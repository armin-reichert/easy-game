package de.amr.easy.game.tests;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

import de.amr.easy.game.Application;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.entity.Entity;
import de.amr.easy.game.input.Keyboard;

public class KeyboardTestApp extends Application {

	public static void main(String[] args) {
		launch(new KeyboardTestApp(), args);
	}

	@Override
	public void init() {
		setController(new KeyboardTestScene());
	}
}

class KeyboardTestScene extends Entity implements Lifecycle {

	private boolean alt, control, shift;
	private int keyCode;

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
		alt = Keyboard.isAltDown();
		control = Keyboard.isControlDown();
		shift = Keyboard.isShiftDown();
		keyCode = 0;
		for (int code = 0; code < 0xFFFF; ++code) {
			if (Keyboard.keyDown(code)) {
				keyCode = code;
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		String text = "";
		if (alt)
			text += "Alt ";
		if (control)
			text += "Control ";
		if (shift)
			text += "Shift ";
		if (keyCode != 0 && !Keyboard.isModifier(keyCode)) {
			text += KeyEvent.getKeyText(keyCode);
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