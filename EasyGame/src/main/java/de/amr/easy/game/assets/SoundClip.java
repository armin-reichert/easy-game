package de.amr.easy.game.assets;

import static de.amr.easy.game.Application.loginfo;

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

	public Clip internal() {
		return clip;
	}

	public void close() {
		clip.close();
	}

	public void play() {
		clip.stop();
		clip.setFramePosition(0);
		clip.start();
	}

	public void loop() {
		loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void loop(int count) {
		clip.stop();
		clip.setFramePosition(0);
		clip.loop(count);
	}

	public void stop() {
		clip.stop();
	}

	public boolean isRunning() {
		return clip.isRunning();
	}

	public float volume() {
		FloatControl masterGain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float volume = (float) Math.pow(10f, masterGain.getValue() / 20f);
		return Math.min(volume, 1);
	}

	public void volume(float volume) {
		if (volume < 0f || volume > 1f)
			throw new IllegalArgumentException("Volume not valid: " + volume);
		FloatControl masterGain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float range = masterGain.getMaximum() - masterGain.getMinimum();
		float gain = (range * volume) + masterGain.getMinimum();
		masterGain.setValue(gain);
		// TODO: clarify this
//		gainControl.setValue(20f * (float) Math.log10(volume));
	}
}