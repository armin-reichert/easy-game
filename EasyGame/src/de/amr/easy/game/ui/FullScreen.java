package de.amr.easy.game.ui;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;

public class FullScreen {

	public static void dumpDisplayModes() {
		for (DisplayMode mode : GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDisplayModes()) {
			System.out
					.println(mode.getWidth() + "x" + mode.getHeight() + "," + mode.getBitDepth() + "," + mode.getRefreshRate());
		}
	}

	private DisplayMode displayMode;

	public static FullScreen Mode(int w, int h, int depth) {
		return new FullScreen(w, h, depth);
	}

	private FullScreen(int w, int h, int depth) {
		this.displayMode = new DisplayMode(w, h, depth, DisplayMode.REFRESH_RATE_UNKNOWN);
	}

	public DisplayMode getDisplayMode() {
		return displayMode;
	}

	@Override
	public String toString() {
		return String.format("FullScreen(width=%d, height=%d, depth=%d)", displayMode.getWidth(), displayMode.getHeight(),
				displayMode.getBitDepth());
	}
}
