package de.amr.easy.game.assets;

import static de.amr.easy.game.Application.loginfo;

import java.beans.PropertyChangeSupport;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

/**
 * Sound manager. Handles global muting state and provides common functionality for playing sound
 * clips.
 * 
 * @author Armin Reichert
 * 
 * @see SoundClip
 */
public class SoundManager {

	private boolean muted;
	public PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public boolean isMuted() {
		return muted;
	}

	/**
	 * Mutes all currently open lines.
	 */
	public void muteAll() {
		boolean wasMuted = muted;
		setLinesMuted(true);
		loginfo("All lines muted");
		changes.firePropertyChange("muted", wasMuted, muted);
	}

	/**
	 * Unmutes all currently open lines.
	 */
	public void unmuteAll() {
		boolean wasMuted = muted;
		setLinesMuted(false);
		loginfo("All lines unmuted");
		changes.firePropertyChange("muted", wasMuted, muted);
	}

	private void setLinesMuted(boolean muted) {
		this.muted = muted;
		for (Mixer.Info info : AudioSystem.getMixerInfo()) {
			for (Line line : AudioSystem.getMixer(info).getSourceLines()) {
				setLineMuted(muted, line);
			}
		}
	}

	private void setLineMuted(boolean muted, Line line) {
		BooleanControl muteControl = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
		if (muteControl != null) {
			muteControl.setValue(muted);
		}
	}

	public void play(SoundClip soundClip) {
		soundClip.internal().stop();
		soundClip.internal().setFramePosition(0);
		soundClip.internal().start();
		if (muted) {
			setLineMuted(muted, soundClip.internal());
		}
	}

	public void stop(SoundClip soundClip) {
		soundClip.internal().stop();
		soundClip.internal().flush();
	}

	public void playLoop(SoundClip soundClip) {
		soundClip.internal().loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void playLoop(SoundClip soundClip, int times) {
		soundClip.internal().stop();
		soundClip.internal().setFramePosition(0);
		soundClip.internal().loop(times);
	}
}