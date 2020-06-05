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
	private long measurementStart = 0;
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
		if (System.nanoTime() >= measurementStart + SECONDS.toNanos(1)) {
			ticksPerSec = ticks;
			ticks = 0;
			measurementStart = System.nanoTime();
		}

		// sleep as long as needed to reach target FPS
		long timeLeft = (period - frameDuration);
		if (timeLeft > 0) {
			try {
				NANOSECONDS.sleep(timeLeft * 975 / 1000); // give a little reserve time
				if (logging) {
					loginfo("Sleep: %5.2f ms", timeLeft / 1_000_000f);
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