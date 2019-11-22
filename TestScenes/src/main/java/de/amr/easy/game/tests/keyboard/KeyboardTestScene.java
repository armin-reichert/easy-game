package de.amr.easy.game.tests.keyboard;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

import de.amr.easy.game.input.Keyboard;
import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.View;

public class KeyboardTestScene implements View, Controller {

	private boolean altDown, controlDown, shiftDown;
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
		altDown = Keyboard.isAltDown();
		controlDown = Keyboard.isControlDown();
		shiftDown = Keyboard.isShiftDown();
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
		if (altDown)
			text += "Alt ";
		if (controlDown)
			text += "Control ";
		if (shiftDown)
			text += "Shift ";
		if (keyCode != 0)
			text += KeyEvent.getKeyText(keyCode);
		if ("".equals(text)) {
			text = "Hold some key and some modifier keys!";
		}
		FontMetrics fm = g.getFontMetrics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.drawString(text, (getWidth() - fm.stringWidth(text)) / 2,
				(getHeight() - fm.getHeight()) / 2 + fm.getAscent());
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
}