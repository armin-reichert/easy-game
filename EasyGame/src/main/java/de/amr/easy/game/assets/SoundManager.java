package de.amr.easy.game.assets;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

/**
 * Sound manager.
 * 
 * @author Armin Reichert
 */
public class SoundManager {

	private boolean muted;

	public boolean isMuted() {
		return muted;
	}

	/**
	 * Mutes all currently open lines.
	 */
	public void muteAll() {
		setLinesMuted(true);
	}

	/**
	 * Unmutes all currently open lines.
	 */
	public void unmuteAll() {
		setLinesMuted(false);
	}

	private void setLinesMuted(boolean muted) {
		this.muted = muted;
		for (Mixer.Info info : AudioSystem.getMixerInfo()) {
			for (Line line : AudioSystem.getMixer(info).getSourceLines()) {
				BooleanControl muteControl = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
				if (muteControl != null) {
					muteControl.setValue(muted);
				}
			}
		}
	}
}