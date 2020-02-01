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

	private Thread thread;
	private volatile boolean running;
	private Task task;
	private int frequency;
	private long ticks;
	private long period;
	private int sleepTimePercentage = 100; // percent
	private int[] fpsHistory = new int[60];
	private int fpsHistoryIndex = 0;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Creates a clock which triggers execution of the given workload according to
	 * the clock frequency.
	 * 
	 * @param work work to do
	 */
	public Clock(int fps, Runnable work) {
		setFrequency(fps);
		this.task = new Task(work);
	}

	/**
	 * @return last reported number of frames/second
	 */
	public int getFrameRate() {
		return task.getFrameRate();
	}

	/**
	 * @return clock frequency (ticks per second)
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Sets the clock frequency to the given value (per second).
	 * 
	 * @param ticksPerSecond number of ticks per second
	 */
	public void setFrequency(int ticksPerSecond) {
		int oldFrequency = this.frequency;
		this.frequency = ticksPerSecond;
		period = ticksPerSecond > 0 ? (SECONDS.toNanos(1) / ticksPerSecond) : Integer.MAX_VALUE;
		LOGGER.info(String.format("Clock frequency is %d ticks/sec.", frequency));
		if (oldFrequency != frequency) {
			pcs.firePropertyChange("frequency", oldFrequency, frequency);
		}
	}

	/**
	 * @return number of ticks since the clock was started
	 */
	public long getTicks() {
		return ticks;
	}

	/**
	 * @param seconds seconds
	 * @return number of clock ticks representing the given seconds
	 */
	public int sec(float seconds) {
		return Math.round(frequency * seconds);
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
	 * Starts the clock and the game loop thread.
	 */
	public synchronized void start() {
		if (!running) {
			running = true;
			thread = new Thread(this::loop, "Clock");
			thread.start();
			ticks = 0;
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
			task.run();
			log(() -> "Work", task.getRunningTime());
			++ticks;
			long usedTime = task.getRunningTime();
			long timeLeft = (period - usedTime);
			computeSleepTimeAdjustment();
			if (timeLeft > 0) {
				try {
					long sleepTime = timeLeft * sleepTimePercentage / 100;
					NANOSECONDS.sleep(sleepTime);
					log(() -> "Slept", sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void computeSleepTimeAdjustment() {
		fpsHistory[fpsHistoryIndex++] = task.getFrameRate();
		if (fpsHistoryIndex == fpsHistory.length) {
			fpsHistoryIndex = 0;
			long avg = Math.round(Arrays.stream(fpsHistory).average().getAsDouble());
			if (avg > frequency) {
				sleepTimePercentage++;
			} else if (avg < frequency) {
				sleepTimePercentage--;
			}
		}
	}
}