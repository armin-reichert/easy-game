package de.amr.easy.game.input;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class KeyCodes {

	private final Map<String, Integer> codes = new HashMap<>();
	
	public KeyCodes() {
		codes.put("a", KeyEvent.VK_A);
		codes.put("b", KeyEvent.VK_B);
		codes.put("c", KeyEvent.VK_C);
		codes.put("d", KeyEvent.VK_D);
		codes.put("e", KeyEvent.VK_E);
		codes.put("f", KeyEvent.VK_F);
		codes.put("g", KeyEvent.VK_G);
		codes.put("h", KeyEvent.VK_H);
		codes.put("i", KeyEvent.VK_I);
		codes.put("j", KeyEvent.VK_J);
		codes.put("k", KeyEvent.VK_K);
		codes.put("l", KeyEvent.VK_L);
		codes.put("m", KeyEvent.VK_M);
		codes.put("n", KeyEvent.VK_N);
		codes.put("o", KeyEvent.VK_O);
		codes.put("p", KeyEvent.VK_P);
		codes.put("q", KeyEvent.VK_Q);
		codes.put("r", KeyEvent.VK_R);
		codes.put("s", KeyEvent.VK_S);
		codes.put("t", KeyEvent.VK_T);
		codes.put("u", KeyEvent.VK_U);
		codes.put("v", KeyEvent.VK_V);
		codes.put("w", KeyEvent.VK_W);
		codes.put("x", KeyEvent.VK_X);
		codes.put("y", KeyEvent.VK_Y);
		codes.put("z", KeyEvent.VK_Z);

		codes.put("0", KeyEvent.VK_0);
		codes.put("1", KeyEvent.VK_1);
		codes.put("2", KeyEvent.VK_2);
		codes.put("3", KeyEvent.VK_3);
		codes.put("4", KeyEvent.VK_4);
		codes.put("5", KeyEvent.VK_5);
		codes.put("6", KeyEvent.VK_6);
		codes.put("7", KeyEvent.VK_7);
		codes.put("8", KeyEvent.VK_8);
		codes.put("9", KeyEvent.VK_9);

		codes.put("+", KeyEvent.VK_PLUS);
		codes.put("-", KeyEvent.VK_MINUS);
		codes.put(" ", KeyEvent.VK_SPACE);
	}

	public int get(String key) {
		if (codes.containsKey(key)) {
			return codes.get(key);
		}
		throw new IllegalArgumentException("No key code found for key " + key);
	}

}