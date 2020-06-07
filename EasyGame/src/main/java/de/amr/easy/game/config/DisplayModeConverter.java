package de.amr.easy.game.config;

import java.awt.DisplayMode;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

class DisplayModeConverter implements IStringConverter<DisplayMode> {

	@Override
	public DisplayMode convert(String str) {
		String[] parts = str.split(",");
		if (parts.length != 3) {
			throw new ParameterException("Illegal display mode");
		}
		try {
			int width = Integer.parseInt(parts[0]);
			int height = Integer.parseInt(parts[1]);
			int bitDepth = Integer.parseInt(parts[2]);
			return new DisplayMode(width, height, bitDepth, DisplayMode.REFRESH_RATE_UNKNOWN);
		} catch (Exception e) {
			throw new ParameterException(e);
		}
	}
}