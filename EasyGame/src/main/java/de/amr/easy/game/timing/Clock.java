package de.amr.easy.game.timing;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.amr.easy.game.Application;

/**
 * The clock that drives the application.
 * 
 * @author Armin Reichert
 */
public class Clock {

	public volatile boolean logging;

	/**
	 * Action executed on every clock tick.
	 */
	public Runnable onTick;

	private Thread thread;
	private volatile boolean ticking;
	private long totalTicks;
	private int targetFrameRate;

	// frame rate control and measurement
	private int currentFrameRate;
	private long frameCountStart;
	private int frames;

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	/**
	 * Creates a clock ticking with the given frequency.
	 * 
	 * @param ticksPerSecond the target frequency (ticks per second)
	 */
	public Clock(int ticksPerSecond) {
		setTargetFrameRate(ticksPerSecond);
		onTick = () -> {
		};
	}

	/**
	 * Creates a clock ticking 60 times/sec.
	 */
	public Clock() {
		this(60);
	}

	/**
	 * Starts the clock if not yet ticking.
	 */
	public synchronized void start() {
		if (!ticking) {
			totalTicks = 0;
			ticking = true;
			thread = new Thread(this::tick, "Clock-" + hashCode());
			thread.start();
		}
	}

	/**
	 * Stops the clock and ends the thread.
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

	private void tick() {
		while (ticking) {
			try {
				singleTick();
			} catch (InterruptedException x) {
				// ignore
			}
		}
	}

	private void singleTick() throws InterruptedException {
		long startTime = System.nanoTime();
		if (onTick != null) {
			onTick.run();
		}
		long frameDuration = System.nanoTime() - startTime;
		loginfo("Tick  %.2f millisec", frameDuration / 1_000_000f);
		++frames;

		long now = System.nanoTime();
		boolean tooSlow = false;

		// half a second has passed since last measurement start, check if running fast enough
		if (now - frameCountStart > SECONDS.toNanos(1) / 2) {
			if (frames < targetFrameRate / 2) {
				tooSlow = true;
			}
		}

		// one second has passed, store framerate
		if (now - frameCountStart > SECONDS.toNanos(1)) {
			currentFrameRate = frames;
			frames = 0;
			frameCountStart = now;
		}

		// sleep to keep target framerate
		if (!tooSlow) {
			long sleepTime = SECONDS.toNanos(1) / targetFrameRate - frameDuration;
			NANOSECONDS.sleep(sleepTime);
			loginfo("Sleep %.2f millisec", sleepTime / 1_000_000f);
		}
	}

	/**
	 * Adds a listener for target frequency changes.
	 * 
	 * @param listener target frequency change listener
	 */
	public void addFrequencyChangeListener(PropertyChangeListener listener) {
		changes.addPropertyChangeListener("frequency", listener);
	}

	/**
	 * @return if the clock is ticking
	 */
	public boolean isTicking() {
		return ticking;
	}

	/**
	 * @return current number of frames/second
	 */
	public int getFrameRate() {
		return currentFrameRate;
	}

	/**
	 * @return the clock's target framerate (ticks per second)
	 */
	public int getTargetFramerate() {
		return targetFrameRate;
	}

	/**
	 * Sets the clock's target framerate to the given value.
	 * 
	 * @param newTargetFrameRate new target framerate in ticks per second
	 */
	public void setTargetFrameRate(int newTargetFrameRate) {
		if (newTargetFrameRate < 1) {
			throw new IllegalArgumentException("Clock target framerate must be at least 1");
		}
		int oldTargetFrameRate = targetFrameRate;
		targetFrameRate = newTargetFrameRate;
		loginfo("Clock target framerate set to %d ticks/sec.", targetFrameRate);
		changes.firePropertyChange("frequency", oldTargetFrameRate, targetFrameRate);
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
		return Math.round(targetFrameRate * seconds);
	}

	private void loginfo(String format, Object... args) {
		if (logging) {
			Application.loginfo(format, args);
		}
	}
}