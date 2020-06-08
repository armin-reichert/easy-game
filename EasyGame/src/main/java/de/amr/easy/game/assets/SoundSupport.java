package de.amr.easy.game.assets;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

/**
 * Sound support class.
 * 
 * @author Armin Reichert
 */
public class SoundSupport {

	/**
	 * Mutes all currently open lines.
	 */
	public static void muteAll() {
		setAllLinesMuted(true);
	}

	/**
	 * Unmutes all currently open lines.
	 */
	public static void unmuteAll() {
		setAllLinesMuted(false);
	}

	private static void setAllLinesMuted(boolean muted) {
		for (Mixer.Info info : AudioSystem.getMixerInfo()) {
			for (Line line : AudioSystem.getMixer(info).getSourceLines()) {
				muteLine(line, muted);
			}
		}
	}

	private static void muteLine(Line line, boolean muted) {
		BooleanControl muteControl = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
		if (muteControl != null) {
			muteControl.setValue(muted);
		}
	}
}