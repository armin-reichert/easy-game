package de.amr.easy.game.assets;

import static de.amr.easy.game.Application.app;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.tagtraum.ffsampledsp.FFAudioInputStream;

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

	private final Clip line;

	public SoundClip(InputStream is) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		line = AudioSystem.getClip(null);
		try (AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is))) {
			if (ais instanceof FFAudioInputStream) {
				AudioFormat mp3Format = ais.getFormat();
				AudioFormat pcmFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, mp3Format.getSampleRate(), 16,
						mp3Format.getChannels(), mp3Format.getChannels() * 2, mp3Format.getSampleRate(), false);
				try (AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(pcmFormat, ais)) {
					line.open(mp3Stream);
				}
			} else {
				line.open(ais);
			}
			if (volume() > 1) {
				setVolume(1);
			}
		}
	}

	@Override
	public String toString() {
		return String.format("SoundClip[%s]", line);
	}

	/**
	 * @return the internal Clip API
	 */
	public Clip line() {
		return line;
	}

	public void mute() {
		SoundManager.setLineMuted(line(), true);
	}

	public void unmute() {
		SoundManager.setLineMuted(line(), false);
	}

	public void play() {
		app().soundManager().startFromBeginning(this);
	}

	public void start() {
		app().soundManager().start(this);
	}

	public void stop() {
		app().soundManager().stop(this);
	}

	public void loop() {
		app().soundManager().loop(this);
	}

	public boolean isRunning() {
		return line.isRunning();
	}

	/**
	 * @see SoundManager#getLineVolume(javax.sound.sampled.Line)
	 */
	public float volume() {
		return SoundManager.getLineVolume(line()).orElse(0f);
	}

	/**
	 * @param value value between 0 and 1
	 * @see SoundManager#setLineVolume(javax.sound.sampled.Line, float)
	 */
	public void setVolume(float value) {
		SoundManager.setLineVolume(line(), value);
	}
}