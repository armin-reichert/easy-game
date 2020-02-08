package de.amr.easy.game.timing;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The clock that drives the game loop.
 * 
 * @author Armin Reichert
 */
public class Clock {

	private static final Logger LOGGER = Logger.getLogger(Clock.class.getName());

	static {
		LOGGER.setLevel(Level.OFF);
	}

	private static void log(Supplier<String> task, long nanos) {
		LOGGER.info(() -> format("%-7s: %10.2f ms", task.get(), nanos / 1_000_000f));
	}

	private volatile boolean running;
	private Thread thread;
	private int targetFramerate;
	private long tickDurationNanos;
	private long totalTicks;

	private float sleepTimePct = 100;
	private int[] fpsHistory = new int[60];
	private int fpsHistoryIndex = 0;

	private Runnable work;
	private long runningTimeNanos; // nanoseconds
	private long fpsMeasurementStartNanos; // nanoseconds
	private int frameCount;
	private int fps;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Creates a clock which triggers execution of the given work according to the
	 * clock frequency.
	 * 
	 * @param targetFrequency the target frequency (ticks per second)
	 * @param work            work to do
	 */
	public Clock(int targetFrequency, Runnable work) {
		setTargetFramerate(targetFrequency);
		this.work = work;
	}

	/**
	 * @return last reported number of frames/second
	 */
	public int getFrameRate() {
		return fps;
	}

	/**
	 * @return the clock's target frequency (ticks per second)
	 */
	public int getTargetFramerate() {
		return targetFramerate;
	}

	/**
	 * Sets the clock's target frequency to the given value (ticks per second).
	 * 
	 * @param ticksPerSecond number of ticks per second
	 */
	public void setTargetFramerate(int ticksPerSecond) {
		if (ticksPerSecond < 1) {
			throw new IllegalArgumentException("Clock frequency must be at least 1");
		}
		int oldFrequency = this.targetFramerate;
		this.targetFramerate = ticksPerSecond;
		tickDurationNanos = SECONDS.toNanos(1) / ticksPerSecond;
		if (oldFrequency != targetFramerate) {
			pcs.firePropertyChange("frequency", oldFrequency, targetFramerate);
			LOGGER.info(String.format("Clock frequency changed to %d ticks/sec.", targetFramerate));
		}
	}

	/**
	 * @return number of ticks since the clock was started
	 */
	public long getTotalTicks() {
		return totalTicks;
	}

	/**
	 * @param seconds seconds
	 * @return number of clock ticks representing the given seconds
	 */
	public int sec(float seconds) {
		return Math.round(targetFramerate * seconds);
	}

	/**
	 * Adds a listener for frequency changes.
	 * 
	 * @param listener frequency change listener
	 */
	public void addFrequencyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener("frequency", listener);
	}

	/**
	 * Starts the clock and the thread on which the work is executed.
	 */
	public synchronized void start() {
		if (!running) {
			thread = new Thread(this::loop, "Clock");
			thread.start();
			totalTicks = 0;
			running = true;
		}
	}

	/**
	 * Stops the clock and the game loop thread.
	 */
	public synchronized void stop() {
		if (running) {
			running = false;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void loop() {
		while (running) {
			long workStartTimeNanos = System.nanoTime();
			work.run();
			long workEndTimeNanos = System.nanoTime();
			runningTimeNanos = workEndTimeNanos - workStartTimeNanos;
			log(() -> "Work (nanoseconds)", runningTimeNanos);
			++totalTicks;

			// measure FPS
			++frameCount;
			if (workEndTimeNanos - fpsMeasurementStartNanos >= SECONDS.toNanos(1)) {
				fps = frameCount;
				frameCount = 0;
				fpsMeasurementStartNanos = System.nanoTime();
			}

			// update FPS history, adjust sleep time if frame rate deviates from target
			fpsHistory[fpsHistoryIndex++] = fps;
			if (fpsHistoryIndex == fpsHistory.length) {
				fpsHistoryIndex = 0;
				Arrays.stream(fpsHistory).average().ifPresent(avgFramerate -> {
					double deviation = avgFramerate - targetFramerate;
					float correction = .1f; // just a heuristic value
					if (deviation > 0) { // too fast
						sleepTimePct += correction;
					} else if (deviation < 0) { // too slow
						sleepTimePct -= correction;
					}
				});
			}
			long timeLeftNanos = (tickDurationNanos - runningTimeNanos);
			if (timeLeftNanos > 0) {
				try {
					long sleepTime = Math.round(timeLeftNanos * sleepTimePct) / 100;
					NANOSECONDS.sleep(sleepTime);
					log(() -> "Slept", sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}