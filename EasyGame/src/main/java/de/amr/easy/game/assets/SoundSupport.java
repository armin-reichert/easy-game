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
	 * 
	 * @param muted if the line should get muted
	 */
	public static void muteAll() {
		setAllLinesMuted(true);
	}

	/**
	 * Unmutes all currently open lines.
	 * 
	 * @param muted if the line should get muted
	 */
	public static void unmuteAll() {
		setAllLinesMuted(false);
	}

	private static void setAllLinesMuted(boolean muted) {
		Mixer.Info[] infos = AudioSystem.getMixerInfo();
		for (Mixer.Info info : infos) {
			Mixer mixer = AudioSystem.getMixer(info);
			for (Line line : mixer.getSourceLines()) {
				muteLine(line, muted);
			}
		}
	}

	private static void muteLine(Line line, boolean muted) {
		BooleanControl bc = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
		if (bc != null) {
			bc.setValue(muted);
		}
	}
}