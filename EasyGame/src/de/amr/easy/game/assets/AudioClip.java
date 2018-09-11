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
			throw new RuntimeException(e);
		}
	}

	@Override
	public void play() {
		if (isRunning()) {
			return;
		}
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