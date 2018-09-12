package de.amr.easy.game.assets;

import static de.amr.easy.game.Application.LOGGER;
import static de.amr.easy.game.assets.Silence.SILENCE;

import java.io.InputStream;

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
		this.clip = clip;
	}

	public static Sound of(InputStream stream) {
		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(stream);
			if (audioStream.getFormat() instanceof MpegAudioFormat) {
				AudioFormat mp3 = audioStream.getFormat();
				AudioFormat pcm = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, mp3.getSampleRate(), 16,
						mp3.getChannels(), mp3.getChannels() * 2, mp3.getSampleRate(), false);
				audioStream = AudioSystem.getAudioInputStream(pcm, audioStream);
			}
			Clip clip = AudioSystem.getClip(null);
			clip.open(audioStream);
			return new SoundClip(clip);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			LOGGER.info("Could not create audio clip, will be silent.");
			return SILENCE;
		}
	}

	@Override
	public void play() {
		if (!isRunning()) {
			clip.setFramePosition(0);
			clip.start();
		}
	}

	@Override
	public void loop() {
		loop(Clip.LOOP_CONTINUOUSLY);
	}

	@Override
	public void loop(int count) {
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
		return clip.getLevel();
	}

	@Override
	public void volume(float volume) {
		FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		volumeControl.setValue(volume);
	}

	public void close() {
		clip.close();
	}
}