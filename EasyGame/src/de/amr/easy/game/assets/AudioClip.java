package de.amr.easy.game.assets;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import de.amr.easy.game.Application;
import javazoom.spi.mpeg.sampled.file.MpegAudioFormat;

/**
 * Wrapper around AudioClip supporting playback of mp3 files. If clip construction fails it becomes
 * a silent clip doing nothing.
 * 
 * @author Armin Reichert
 */
public class AudioClip implements Sound {

	private Clip clip;

	public AudioClip(InputStream stream) {
		if (stream == null) {
			throw new IllegalArgumentException("Audio input stream is NULL");
		}
		try {
			clip = AudioSystem.getClip(null);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(stream);
			if (audioIn.getFormat() instanceof MpegAudioFormat) {
				AudioFormat mp3 = audioIn.getFormat();
				AudioFormat pcm = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, mp3.getSampleRate(), 16,
						mp3.getChannels(), mp3.getChannels() * 2, mp3.getSampleRate(), false);
				AudioInputStream decodedStream = AudioSystem.getAudioInputStream(pcm, audioIn);
				clip.open(decodedStream);
			} else {
				clip.open(audioIn);
			}
		} catch (Exception e) {
			clip = null;
			e.printStackTrace(System.err);
			Application.LOGGER.info("Could not create audio clip");
		}
	}

	@Override
	public void play() {
		if (clip != null) {
			if (isRunning()) {
				return;
			}
			clip.setFramePosition(0);
			clip.start();
		}
	}

	@Override
	public void loop() {
		if (clip != null) {
			stop();
			clip.setFramePosition(0);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	@Override
	public void stop() {
		if (clip != null) {
			if (clip.isRunning()) {
				clip.stop();
			}
		}
	}

	@Override
	public boolean isRunning() {
		return clip != null ? clip.isRunning() : false;
	}

	@Override
	public void volume(float volume) {
		if (clip != null) {
			FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			volumeControl.setValue(volume);
		}
	}

	public void close() {
		if (clip != null) {
			stop();
			clip.close();
		}
	}
}