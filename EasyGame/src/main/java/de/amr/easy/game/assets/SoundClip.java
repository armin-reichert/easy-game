package de.amr.easy.game.assets;

import static de.amr.easy.game.Application.app;
import static java.lang.Math.log10;
import static java.lang.Math.pow;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.spi.mpeg.sampled.file.MpegAudioFormat;

/**
 * Clips are usually short audio sequences that are loaded completely into memory. This class uses
 * the Java audio {@link Clip} implementation and adds support for mp3-files. Also the global muting
 * state of the application is respected.
 * 
 * @author Armin Reichert
 * 
 * @see Clip
 * @see SoundManager
 */
public class SoundClip {

	private final Clip clip;

	public SoundClip(InputStream is) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
		if (ais.getFormat() instanceof MpegAudioFormat) {
			AudioFormat mp3 = ais.getFormat();
			AudioFormat pcm = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, mp3.getSampleRate(), 16, mp3.getChannels(),
					mp3.getChannels() * 2, mp3.getSampleRate(), false);
			ais = AudioSystem.getAudioInputStream(pcm, ais);
		}
		clip = AudioSystem.getClip(null);
		clip.open(ais);
		if (volume() > 1) {
			volume(1);
		}
	}

	@Override
	public String toString() {
		return String.format("SoundClip[%s]", clip);
	}

	/**
	 * @return the internal Clip API
	 */
	public Clip internal() {
		return clip;
	}

	public void mute() {
		app().soundManager().mute(this);
	}

	public void unmute() {
		app().soundManager().unmute(this);
	}

	/**
	 * Starts or, if the clip is already running, restarts the clip playback. If the application is
	 * muted, this clip gets muted too.
	 */
	public void play() {
		app().soundManager().play(this);
	}

	/**
	 * Starts the clip playback.
	 */
	public void start() {
		app().soundManager().start(this);
	}

	/**
	 * Stops the clip playback.
	 */
	public void stop() {
		app().soundManager().stop(this);
	}

	/**
	 * Plays the clip in an infinite loop.
	 */
	public void loop() {
		app().soundManager().playLoop(this);
	}

	/**
	 * Plays the clip the given number of times in a loop.
	 * 
	 * @param times number of repetitions
	 */
	public void loop(int times) {
		app().soundManager().playLoop(this, times);
	}

	/**
	 * Tells if the clip is running.
	 * 
	 * @return if the clip is running
	 */
	public boolean isRunning() {
		return clip.isRunning();
	}

	/**
	 * Returns the volume ("master gain") of this clip as a number in the range [0..1].
	 * 
	 * @return the volumne ("master gain") as a value from the range [0..1]
	 */
	public float volume() {
		FloatControl masterGain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float masterGainDB = masterGain.getValue();
		float linearValue = (float) pow(10.0, masterGainDB / 20.0);
		return limitToRange(0, 1, linearValue);
	}

	private float limitToRange(float min, float max, float value) {
		return Math.max(Math.min(value, 1), 0);
	}

	/**
	 * Sets the volume ("master gain") of this clip as a number in the range [0..1].
	 * 
	 * @param volume new volumne from the range [0..1]
	 */
	public void volume(float volume) {
		if (volume < 0f || volume > 1f) {
			throw new IllegalArgumentException("Volume not valid: " + volume);
		}
		FloatControl masterGain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float masterGainDB = (float) log10(volume * 20.0);
		masterGain.setValue(masterGainDB);
	}

}