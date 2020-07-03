package de.amr.easy.game.assets;

import java.beans.PropertyChangeSupport;
import java.util.Optional;

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

	private static Optional<BooleanControl> muteControl(Line line) {
		return Optional.ofNullable((BooleanControl) line.getControl(BooleanControl.Type.MUTE));
	}

	private static void setLineMuted(Line line, boolean muted) {
		muteControl(line).ifPresent(control -> control.setValue(muted));
	}

	private static void setAllLinesMuted(boolean muted) {
		for (Mixer.Info info : AudioSystem.getMixerInfo()) {
			for (Line line : AudioSystem.getMixer(info).getSourceLines()) {
				setLineMuted(line, muted);
			}
		}
	}

	/**
	 * @return if the sound is muted
	 */
	public boolean isMuted() {
		return muted;
	}

	/**
	 * Mutes all currently open lines.
	 */
	public void muteAll() {
		if (!muted) {
			muted = true;
			setAllLinesMuted(true);
			changes.firePropertyChange("muted", false, true);
		}
	}

	/**
	 * Unmutes all currently open lines.
	 */
	public void unmuteAll() {
		if (muted) {
			muted = false;
			setAllLinesMuted(false);
			changes.firePropertyChange("muted", true, false);
		}
	}

	public void mute(SoundClip soundClip) {
		setLineMuted(soundClip.internal(), true);
	}

	public void unmute(SoundClip soundClip) {
		setLineMuted(soundClip.internal(), false);
	}

	public void play(SoundClip soundClip) {
		Clip clip = soundClip.internal();
		clip.stop();
		clip.setFramePosition(0);
		clip.start();
		if (muted) {
			mute(soundClip);
		}
	}

	public void start(SoundClip soundClip) {
		Clip clip = soundClip.internal();
		clip.start();
		if (muted) {
			mute(soundClip);
		}
	}

	public void stop(SoundClip soundClip) {
		Clip clip = soundClip.internal();
		clip.stop();
		clip.flush();
		if (muted) {
			mute(soundClip);
		}
	}

	public void playLoop(SoundClip soundClip) {
		Clip clip = soundClip.internal();
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		if (muted) {
			mute(soundClip);
		}
	}

	public void playLoop(SoundClip soundClip, int times) {
		Clip clip = soundClip.internal();
		clip.stop();
		clip.setFramePosition(0);
		clip.loop(times);
		if (muted) {
			mute(soundClip);
		}
	}
}