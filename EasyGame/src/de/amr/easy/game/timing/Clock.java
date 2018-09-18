package de.amr.easy.game.timing;

import static de.amr.easy.game.Application.LOGGER;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The clock that produces the ticks for the game loop.
 * 
 * @author Armin Reichert
 */
public class Clock {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private Task renderTask;
	private Task updateTask;
	private int frequency;
	private long ticks;
	private long period;
	private Thread thread;
	private volatile boolean running;
	private boolean loggingEnabled;

	/**
	 * Creates a clock which triggers the given update and render task according to the clock frequency.
	 * 
	 * @param update
	 *                 update task
	 * @param render
	 *                 render task
	 */
	public Clock(Runnable update, Runnable render) {
		updateTask = new Task(update, SECONDS.toNanos(1));
		renderTask = new Task(render, SECONDS.toNanos(1));
	}

	/**
	 * @return last reported update rate (updates per second)
	 */
	public int getUpdateRate() {
		return updateTask.getRate();
	}

	/**
	 * @return last reported rendering rate (frames per second)
	 */
	public int getRenderRate() {
		return renderTask.getRate();
	}

	public void setLoggingEnabled(boolean enabled) {
		this.loggingEnabled = enabled;
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
	 * @param ticksPerSecond
	 *                         number of ticks per second
	 */
	public void setFrequency(int ticksPerSecond) {
		int oldFrequency = this.frequency;
		this.frequency = ticksPerSecond;
		period = ticksPerSecond > 0 ? (SECONDS.toNanos(1) / ticksPerSecond) : Integer.MAX_VALUE;
		LOGGER.info(String.format("Clock frequency has been set to %d Hz.", frequency));
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
	 * @param seconds
	 *                  seconds
	 * @return number of clock ticks representing the given seconds
	 */
	public int sec(float seconds) {
		return Math.round(frequency * seconds);
	}

	/**
	 * Adds a listener for frequency changes.
	 * 
	 * @param listener
	 *                   frequency change listener
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
			thread = new Thread(this::gameLoop, "GameLoop");
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

	private void gameLoop() {
		long overTime = 0;
		while (running) {
			updateTask.run();
			renderTask.run();
			if (loggingEnabled) {
				logTime("Update", updateTask.getUsedTime());
				logTime("Render", renderTask.getUsedTime());
			}
			++ticks;
			long usedTime = updateTask.getUsedTime() + renderTask.getUsedTime();
			long timeLeft = (period - usedTime);
			if (timeLeft > 0) {
				long sleepTime = timeLeft;
				try {
					NANOSECONDS.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (loggingEnabled) {
					logTime("Sleep", sleepTime);
				}
			} else if (timeLeft < 0) {
				overTime += (-timeLeft);
				for (int xUpdates = 3; xUpdates > 0 && overTime > period; overTime -= period, --xUpdates) {
					updateTask.run();
					if (loggingEnabled) {
						logTime("UpdateX", updateTask.getUsedTime());
					}
					++ticks;
				}
			}
		}
	}

	private void logTime(String taskName, long nanos) {
		LOGGER.info(format("%-7s: %10.2f ms", taskName, nanos / 1_000_000f));
	}
}