package de.amr.easy.game.assets;

/**
 * Silence.
 * 
 * @author Armin Reichert
 */
public enum Silence implements Sound {

	/** The sound of silence. */
	SILENCE;

	@Override
	public void play() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void loop(int times) {
	}

	@Override
	public void loop() {
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public float volume() {
		return 0;
	}

	@Override
	public void volume(float v) {
	}
}