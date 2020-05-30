package de.amr.easy.game.timing;

import static de.amr.easy.game.Application.loginfo;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The clock that drives the application.
 * 
 * @author Armin Reichert
 */
public class Clock {

	public volatile boolean logging = false;
	private volatile boolean ticking;
	private Runnable work;
	private Thread thread;
	private int targetFPS;
	private long period;
	private long totalTicks;
	private int frames;
	private int framesPerSec;
	private long frameRateMeasurementStart = 0;
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	/**
	 * Creates a clock which triggers execution of the given work according to the clock frequency.
	 * 
	 * @param targetFrequency the target frequency (ticks per second)
	 * @param work            work to do
	 */
	public Clock(int targetFrequency, Runnable work) {
		setTargetFramerate(targetFrequency);
		this.work = work;
	}

	/**
	 * Starts the clock and the thread.
	 */
	public synchronized void start() {
		if (!ticking) {
			totalTicks = 0;
			ticking = true;
			thread = new Thread(this::run, "Clock");
			thread.start();
		}
	}

	/**
	 * Stops the clock and the thread.
	 */
	public synchronized void stop() {
		if (ticking) {
			ticking = false;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void run() {
		while (ticking) {
			tick();
		}
	}

	private void tick() {
		long start = System.nanoTime();
		work.run();
		long frameDuration = System.nanoTime() - start;
		if (logging) {
			loginfo("%-15s: %15.2f ms", "Frame", frameDuration / 1_000_000f);
		}
		++totalTicks;

		// measure FPS
		++frames;
		if (System.nanoTime() >= frameRateMeasurementStart + SECONDS.toNanos(1)) {
			framesPerSec = frames;
			frames = 0;
			frameRateMeasurementStart = System.nanoTime();
		}

		// sleep as long as needed to reach target FPS
		long timeLeft = (period - frameDuration);
		if (timeLeft > 0) {
			try {
				NANOSECONDS.sleep(timeLeft * 975 / 1000); // give a little reserve time
				if (logging) {
					loginfo("%-15s: %15.2f ms", "Slept", timeLeft / 1_000_000f);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds a listener for frequency changes.
	 * 
	 * @param listener frequency change listener
	 */
	public void addFrequencyChangeListener(PropertyChangeListener listener) {
		changes.addPropertyChangeListener("frequency", listener);
	}

	/**
	 * @return last reported number of frames/second
	 */
	public int getFrameRate() {
		return framesPerSec;
	}

	/**
	 * @return the clock's target frequency (ticks per second)
	 */
	public int getTargetFramerate() {
		return targetFPS;
	}

	/**
	 * Sets the clock's target frequency to the given value (ticks per second).
	 * 
	 * @param fps number of ticks per second
	 */
	public void setTargetFramerate(int fps) {
		if (this.targetFPS == fps) {
			return;
		}
		if (fps < 1) {
			throw new IllegalArgumentException("Clock frequency must be at least 1");
		}
		int oldTargetFPS = targetFPS;
		targetFPS = fps;
		period = SECONDS.toNanos(1) / fps;
		loginfo(String.format("Clock frequency changed to %d ticks/sec.", targetFPS));
		changes.firePropertyChange("frequency", oldTargetFPS, targetFPS);
	}

	/**
	 * @return total number of ticks since the clock was started
	 */
	public long getTotalTicks() {
		return totalTicks;
	}

	/**
	 * Converts a given time (in seconds) into the number of corresponding ticks at the clock's target
	 * speed.
	 * 
	 * @param seconds seconds
	 * @return number of clock ticks representing the given seconds
	 */
	public int sec(float seconds) {
		return Math.round(targetFPS * seconds);
	}
}