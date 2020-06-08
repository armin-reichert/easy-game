package de.amr.easy.game.assets;

import static de.amr.easy.game.Application.LOGGER;
import static de.amr.easy.game.assets.Silence.SILENCE;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Objects;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import javazoom.spi.mpeg.sampled.file.MpegAudioFormat;

/**
 * Clips are usually short audio sequences that are loaded completely into memory.
 * 
 * @author Armin Reichert
 */
public class SoundClip implements Sound {

	private final Clip clip;

	private SoundClip(Clip clip) {
		this.clip = Objects.requireNonNull(clip);
		if (volume() > 1) {
			volume(1);
		}
	}

	@Override
	public String toString() {
		return clip.toString();
	}

	public static Sound of(InputStream is) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
			if (ais.getFormat() instanceof MpegAudioFormat) {
				AudioFormat mp3 = ais.getFormat();
				AudioFormat pcm = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, mp3.getSampleRate(), 16, mp3.getChannels(),
						mp3.getChannels() * 2, mp3.getSampleRate(), false);
				ais = AudioSystem.getAudioInputStream(pcm, ais);
			}
			Clip clip = AudioSystem.getClip(null);
			clip.open(ais);
			return new SoundClip(clip);
		} catch (Exception e) {
			LOGGER.info("Could not create audio clip, will be silent.");
			e.printStackTrace(System.err);
			return SILENCE;
		}
	}

	@Override
	public void play() {
		clip.stop();
		clip.setFramePosition(0);
		clip.start();
	}

	@Override
	public void loop() {
		loop(Clip.LOOP_CONTINUOUSLY);
	}

	@Override
	public void loop(int count) {
		clip.stop();
		clip.setFramePosition(0);
		clip.loop(count);
	}

	@Override
	public void stop() {
		clip.stop();
	}

	@Override
	public boolean isRunning() {
		return clip.isRunning();
	}

	@Override
	public float volume() {
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float volume = (float) Math.pow(10f, gainControl.getValue() / 20f);
		return Math.min(volume, 1);
	}

	@Override
	public void volume(float volume) {
		if (volume < 0f || volume > 1f)
			throw new IllegalArgumentException("Volume not valid: " + volume);
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float range = gainControl.getMaximum() - gainControl.getMinimum();
		float gain = (range * volume) + gainControl.getMinimum();
		gainControl.setValue(gain);
//		gainControl.setValue(20f * (float) Math.log10(volume));
	}

	public void close() {
		clip.close();
	}
}