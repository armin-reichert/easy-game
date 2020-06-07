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

	/**
	 * Action executed on every clock tick.
	 */
	public Runnable onTick;

	public volatile boolean logging;

	private volatile boolean ticking;

	private Thread thread;

	private int targetSpeed;

	/** frame duration at current frame rate in nanoseconds */
	private long period;

	private long totalTicks;

	private int ticksPerInterval;

	private int frameRate;

	private long intervalStart;

	private float frameRateDiff;

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	/**
	 * Creates a clock running at the given frequency.
	 * 
	 * @param ticksPerSecond the target frequency (ticks per second)
	 */
	public Clock(int ticksPerSecond) {
		setTargetFrameRate(ticksPerSecond);
		onTick = () -> {
		};
	}

	public Clock() {
		this(60);
	}

	/**
	 * Starts the clock and the thread.
	 */
	public synchronized void start() {
		if (!ticking) {
			totalTicks = 0;
			ticking = true;
			thread = new Thread(this::tick, "Clock");
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

	private void tick() {
		while (ticking) {
			tickOnce();
		}
	}

	private void tickOnce() {
		long tickStart, tickEnd, tickDuration;

		// tick and perform client action
		tickStart = System.nanoTime();
		onTick.run();
		tickEnd = System.nanoTime();

		++ticksPerInterval;
		++totalTicks;

		tickDuration = tickEnd - tickStart;
		loginfo("Tick:  %5.2f ms", tickDuration / 1_000_000f);

		// measure frame rate
		int numIntervals = targetSpeed / 10;
		numIntervals = Math.max(numIntervals, 2);
		numIntervals = Math.min(numIntervals, 10);
		long intervalDuration = SECONDS.toNanos(1) / numIntervals;
		
		if (tickEnd >= intervalStart + intervalDuration) {
			// next interval
			frameRate = ticksPerInterval * numIntervals;
			ticksPerInterval = 0;
			frameRateDiff = ((float) (frameRate - targetSpeed)) / targetSpeed;
			loginfo("current frame rate difference: %.2f%%", frameRateDiff * 100);
			intervalStart = System.nanoTime();
		}

		// sleep as long as needed to reach target FPS
		long sleep = period - tickDuration;
		sleep += Math.round(sleep * frameRateDiff);
		if (sleep > 0) {
			try {
				NANOSECONDS.sleep(sleep);
				loginfo("Sleep: %5.2f ms", sleep / 1_000_000f);
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
	 * @return if the clock is ticking
	 */
	public boolean isTicking() {
		return ticking;
	}

	/**
	 * @return last reported number of frames/second
	 */
	public int getFrameRate() {
		return frameRate;
	}

	/**
	 * @return the clock's target frequency (ticks per second)
	 */
	public int getTargetFramerate() {
		return targetSpeed;
	}

	/**
	 * Sets the clock's target frequency to the given value (ticks per second).
	 * 
	 * @param ticksPerSecond number of ticks per second
	 */
	public void setTargetFrameRate(int ticksPerSecond) {
		if (this.targetSpeed == ticksPerSecond) {
			return;
		}
		if (ticksPerSecond < 1) {
			throw new IllegalArgumentException("Clock frequency must be at least 1");
		}
		int oldTargetSpeed = targetSpeed;
		targetSpeed = ticksPerSecond;
		period = SECONDS.toNanos(1) / ticksPerSecond;
		loginfo("Clock target frequency set to %d ticks/sec.", targetSpeed);
		changes.firePropertyChange("frequency", oldTargetSpeed, targetSpeed);
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
		return Math.round(targetSpeed * seconds);
	}

	private void loginfo(String format, Object... args) {
		if (logging) {
			Application.loginfo(format, args);
		}
	}
}