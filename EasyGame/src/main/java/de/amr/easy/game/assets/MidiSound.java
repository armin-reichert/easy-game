package de.amr.easy.game.assets;

import java.io.InputStream;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;

public class MidiSound implements Sound {

	private final Sequencer sequencer;

	public MidiSound(InputStream midiIn) {
		if (midiIn == null) {
			throw new IllegalArgumentException("MIDI input is FROZEN");
		}
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.setSequence(midiIn);
			sequencer.open();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void play() {
		if (sequencer.isRunning()) {
			sequencer.stop();
		}
		sequencer.setLoopCount(0);
		sequencer.start();
	}

	@Override
	public void stop() {
		sequencer.stop();
	}

	@Override
	public void loop() {
		loop(Sequencer.LOOP_CONTINUOUSLY);
	}

	@Override
	public void loop(int count) {
		if (sequencer.isRunning()) {
			sequencer.stop();
		}
		sequencer.setLoopCount(count);
		sequencer.start();
	}

	@Override
	public boolean isRunning() {
		return sequencer.isRunning();
	}

	@Override
	public float volume() {
		return 0;
	}

	@Override
	public void volume(float v) {
	}
}
