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

	/**
	 * Action executed on every clock tick.
	 */
	public Runnable onTick;

	public volatile boolean logging = false;

	private volatile boolean ticking;
	private Thread thread;
	private int targetSpeed;
	private long period;
	private long totalTicks;
	private int ticks;
	private int ticksPerSec;
	private long measurementStart;
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
		onTick.run();
		long frameDuration = System.nanoTime() - start;
		if (logging) {
			loginfo("Tick:  %5.2f ms", frameDuration / 1_000_000f);
		}
		++totalTicks;

		// measure FPS
		++ticks;
		int intervals = targetSpeed / 10;
		intervals = Math.max(intervals, 2);
		intervals = Math.min(intervals, 10);
		if (System.nanoTime() >= measurementStart + SECONDS.toNanos(1) / intervals) {
			ticksPerSec = ticks * intervals;
			ticks = 0;
			measurementStart = System.nanoTime();

			// measure difference from target frame rate
			frameRateDiff = ((float) (ticksPerSec - targetSpeed)) / targetSpeed;
			if (logging) {
				loginfo("frame rate difference: %.2f%%", frameRateDiff * 100);
			}
		}

		// sleep as long as needed to reach target FPS
		long sleep = period - frameDuration;
		if (frameRateDiff < 0) {
			// we are too slow, reduce sleep time
			sleep += Math.round(sleep * frameRateDiff);
		}
		if (sleep > 0) {
			try {
				NANOSECONDS.sleep(sleep);
				if (logging) {
					loginfo("Sleep: %5.2f ms", sleep / 1_000_000f);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	private static float average(float[] values) {
		float average = 0;
		for (float value : values) {
			average += value;
		}
		return average / values.length;
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
		return ticksPerSec;
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
}