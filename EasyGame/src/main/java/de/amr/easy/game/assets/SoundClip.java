package de.amr.easy.game.assets;

import static de.amr.easy.game.Application.loginfo;
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
 * Clips are usually short audio sequences that are loaded completely into memory.
 * 
 * @see Clip
 * 
 * @author Armin Reichert
 */
public class SoundClip {

	public static SoundClip of(InputStream is) {
		try {
			return new SoundClip(is);
		} catch (LineUnavailableException x) {
			loginfo("Cannot create sound clip: line unavailable");
			throw new RuntimeException(x);
		} catch (IOException x) {
			loginfo("Cannot read sound clip");
			throw new RuntimeException(x);
		} catch (UnsupportedAudioFileException x) {
			loginfo("Unsupported audio format");
			throw new RuntimeException(x);
		}
	}

	private final Clip clip;

	private SoundClip(InputStream is) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
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
		return clip.toString();
	}

	/**
	 * @return the internal Clip API
	 */
	public Clip internal() {
		return clip;
	}

	/**
	 * Closes the clip.
	 */
	public void close() {
		clip.close();
	}

	/**
	 * Starts or, if the clip is already running, restarts the clip playback.
	 */
	public void play() {
		clip.stop();
		clip.setFramePosition(0);
		clip.start();
	}

	/**
	 * Plays the clip in an infinite loop.
	 */
	public void loop() {
		loop(Clip.LOOP_CONTINUOUSLY);
	}

	/**
	 * Plays the clip the given number of times in a loop.
	 * 
	 * @param repetitions number of repetitions
	 */
	public void loop(int repetitions) {
		clip.stop();
		clip.setFramePosition(0);
		clip.loop(repetitions);
	}

	/**
	 * Stops the clip and releases its resources.
	 */
	public void stop() {
		clip.stop();
		clip.flush();
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
		return clipToRange(0, 1, linearValue);
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

	private float clipToRange(float min, float max, float value) {
		return Math.max(Math.min(value, 1), 0);
	}
}