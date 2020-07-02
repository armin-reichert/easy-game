package de.amr.easy.game.input;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping from names to key codes.
 * 
 * @author Armin Reichert
 */
public class KeyCodes {

	private final Map<String, Integer> codeByName = new HashMap<>();

	public KeyCodes() {
		codeByName.put("a", KeyEvent.VK_A);
		codeByName.put("b", KeyEvent.VK_B);
		codeByName.put("c", KeyEvent.VK_C);
		codeByName.put("d", KeyEvent.VK_D);
		codeByName.put("e", KeyEvent.VK_E);
		codeByName.put("f", KeyEvent.VK_F);
		codeByName.put("g", KeyEvent.VK_G);
		codeByName.put("h", KeyEvent.VK_H);
		codeByName.put("i", KeyEvent.VK_I);
		codeByName.put("j", KeyEvent.VK_J);
		codeByName.put("k", KeyEvent.VK_K);
		codeByName.put("l", KeyEvent.VK_L);
		codeByName.put("m", KeyEvent.VK_M);
		codeByName.put("n", KeyEvent.VK_N);
		codeByName.put("o", KeyEvent.VK_O);
		codeByName.put("p", KeyEvent.VK_P);
		codeByName.put("q", KeyEvent.VK_Q);
		codeByName.put("r", KeyEvent.VK_R);
		codeByName.put("s", KeyEvent.VK_S);
		codeByName.put("t", KeyEvent.VK_T);
		codeByName.put("u", KeyEvent.VK_U);
		codeByName.put("v", KeyEvent.VK_V);
		codeByName.put("w", KeyEvent.VK_W);
		codeByName.put("x", KeyEvent.VK_X);
		codeByName.put("y", KeyEvent.VK_Y);
		codeByName.put("z", KeyEvent.VK_Z);

		codeByName.put("0", KeyEvent.VK_0);
		codeByName.put("1", KeyEvent.VK_1);
		codeByName.put("2", KeyEvent.VK_2);
		codeByName.put("3", KeyEvent.VK_3);
		codeByName.put("4", KeyEvent.VK_4);
		codeByName.put("5", KeyEvent.VK_5);
		codeByName.put("6", KeyEvent.VK_6);
		codeByName.put("7", KeyEvent.VK_7);
		codeByName.put("8", KeyEvent.VK_8);
		codeByName.put("9", KeyEvent.VK_9);

		codeByName.put("+", KeyEvent.VK_PLUS);
		codeByName.put("-", KeyEvent.VK_MINUS);
		codeByName.put(" ", KeyEvent.VK_SPACE);
		codeByName.put("space", KeyEvent.VK_SPACE);

		codeByName.put("left", KeyEvent.VK_LEFT);
		codeByName.put("right", KeyEvent.VK_RIGHT);
		codeByName.put("up", KeyEvent.VK_UP);
		codeByName.put("down", KeyEvent.VK_DOWN);
	}

	public int get(String key) {
		if (codeByName.containsKey(key)) {
			return codeByName.get(key);
		}
		throw new IllegalArgumentException("No key code found for key " + key);
	}

}