package de.amr.easy.game.assets;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import javazoom.spi.mpeg.sampled.file.MpegAudioFormat;

public class AudioClip implements Sound {

	private final Clip clip;

	public AudioClip(InputStream stream) {
		if (stream == null) {
			throw new IllegalArgumentException("Audio input is NULL");
		}
		try {
			clip = AudioSystem.getClip();
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(stream);
			if (audioStream.getFormat() instanceof MpegAudioFormat) {
				AudioFormat encodedFormat = audioStream.getFormat();
				AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, encodedFormat.getSampleRate(), 16,
						encodedFormat.getChannels(), encodedFormat.getChannels() * 2, encodedFormat.getSampleRate(), false);
				AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);
				clip.open(decodedStream);
			} else {
				clip.open(audioStream);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Override
	public void play() {
		if (isRunning()) {
			return;
		}
		// stop();
		clip.setFramePosition(0);
		clip.start();
	}

	@Override
	public void loop() {
		stop();
		clip.setFramePosition(0);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	@Override
	public void stop() {
		if (clip.isRunning()) {
			clip.stop();
		}
	}

	@Override
	public boolean isRunning() {
		return clip.isRunning();
	}

	@Override
	public void volume(float volume) {
		FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		volumeControl.setValue(volume);
	}

	public void close() {
		stop();
		clip.close();
	}
}
