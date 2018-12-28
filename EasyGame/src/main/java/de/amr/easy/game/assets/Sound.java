package de.amr.easy.game.assets;

/**
 * Common interface for sound objects.
 * 
 * @author Armin Reichert
 */
public interface Sound {

	/**
	 * Plays the sound once.
	 */
	void play();

	/**
	 * Stops the sound if playing.
	 */
	void stop();

	/**
	 * Loops the sound the given number of times.
	 * 
	 * @param times
	 */
	void loop(int times);

	/**
	 * Loops the sound forever.
	 */
	void loop();

	/**
	 * Tells if the sound is running.
	 * 
	 * @return if the sound is running
	 */
	boolean isRunning();

	/**
	 * Returns the volume level of the sound.
	 * 
	 * @return volume level
	 */
	float volume();

	/**
	 * Sets the volume level of this sound.
	 * 
	 * @param v
	 */
	void volume(float v);
}