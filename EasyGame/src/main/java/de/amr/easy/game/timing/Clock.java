package de.amr.easy.game.timing;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private Task render;
	private Task update;
	private int frequency;
	private long ticks;
	private long period;
	private Thread thread;
	private volatile boolean running;

	/**
	 * Creates a clock which triggers execution of the given update and render code
	 * according to the clock frequency.
	 * 
	 * @param update update code
	 * @param render render code
	 */
	public Clock(int fps, Runnable update, Runnable render) {
		setFrequency(fps);
		this.update = new Task(update);
		this.render = new Task(render);
	}

	/**
	 * @return last reported update rate (updates per second)
	 */
	public int getUpdateRate() {
		return update.getFrameRate();
	}

	/**
	 * @return last reported rendering rate (frames per second)
	 */
	public int getRenderRate() {
		return render.getFrameRate();
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
			thread = new Thread(this::loop, "GameLoop");
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
		long overTime = 0;
		while (running) {
			update.run();
			render.run();
			log(() -> "Update", update.getRunningTime());
			log(() -> "Render", render.getRunningTime());
			++ticks;
			long usedTime = update.getRunningTime() + render.getRunningTime();
			long timeLeft = (period - usedTime);
			if (timeLeft > 0) {
				try {
					// removing 5% sleep time seems to lead to better fps
					NANOSECONDS.sleep(timeLeft * 95 / 100);
					log(() -> "Slept", timeLeft);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (timeLeft < 0) {
				overTime += (-timeLeft);
				for (int xUpdates = 3; xUpdates > 0 && overTime > period; overTime -= period, --xUpdates) {
					update.run();
					log(() -> "UpdateX", update.getRunningTime());
					++ticks;
				}
			}
		}
	}
}