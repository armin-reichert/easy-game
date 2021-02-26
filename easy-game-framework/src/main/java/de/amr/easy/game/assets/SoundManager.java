package de.amr.easy.game.assets;

import java.beans.PropertyChangeSupport;
import java.util.Optional;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
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

	private static Optional<FloatControl> masterGainControl(Line line) {
		return Optional.ofNullable((FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN));
	}

	public static void setLineMuted(Line line, boolean muted) {
		muteControl(line).ifPresent(control -> control.setValue(muted));
	}

	public static void setAllLinesMuted(boolean muted) {
		for (Mixer.Info info : AudioSystem.getMixerInfo()) {
			for (Line line : AudioSystem.getMixer(info).getSourceLines()) {
				setLineMuted(line, muted);
			}
		}
	}

	/**
	 * Sets the volume ("master gain"), if possible, as a number between 0 and 1.
	 * 
	 * @param value new volume, number between 0 and 1
	 */
	public static void setLineVolume(Line line, float value) {
		if (value < 0 || value > 1) {
			throw new IllegalArgumentException("Volume must be between 0 and 1, but is: " + value);
		}
		masterGainControl(line).ifPresent(control -> control.setValue((float) (20 * Math.log10(value))));
	}

	/**
	 * Returns the (optional) volume ("master gain") of this clip as a number between 0 and 1.
	 * 
	 * @return the optional volume as a value between 0 and 1
	 */
	public static Optional<Float> getLineVolume(Line line) {
		return masterGainControl(line).map(control -> (float) Math.pow(10, control.getValue() / 20));
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

	/**
	 * Starts or, if the clip is already running, restarts the clip playback. If the application is
	 * muted, this clip gets muted too.
	 */
	public void startFromBeginning(SoundClip soundClip) {
		Clip clip = soundClip.line();
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0);
		clip.start();
		setLineMuted(clip, muted);
	}

	/**
	 * Starts the clip playback.
	 */
	public void start(SoundClip soundClip) {
		Clip clip = soundClip.line();
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.start();
		setLineMuted(clip, muted);
	}

	/**
	 * Stops the clip playback.
	 */
	public void stop(SoundClip soundClip) {
		Clip clip = soundClip.line();
		if (clip.isRunning()) {
			clip.stop();
		}
		setLineMuted(clip, muted);
	}

	/**
	 * Plays the clip in an infinite loop.
	 */
	public void loop(SoundClip soundClip) {
		Clip clip = soundClip.line();
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		setLineMuted(clip, muted);
	}
}