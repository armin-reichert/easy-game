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
		LOGGER.info(() -> format("%-15s: %15.2f ms", task.get(), nanos / 1_000_000f));
	}

	private volatile boolean running;
	private Runnable work;
	private Thread thread;
	private int targetFPS;
	private long period;
	private long totalTicks;

	private int frames;
	private int fps;
	private long fpsMmtStart = 0;

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
		return targetFPS;
	}

	/**
	 * Sets the clock's target frequency to the given value (ticks per second).
	 * 
	 * @param fps number of ticks per second
	 */
	public void setTargetFramerate(int fps) {
		if (fps < 1) {
			throw new IllegalArgumentException("Clock frequency must be at least 1");
		}
		int oldTargetFPS = this.targetFPS;
		this.targetFPS = fps;
		period = SECONDS.toNanos(1) / fps;
		if (oldTargetFPS != targetFPS) {
			pcs.firePropertyChange("frequency", oldTargetFPS, targetFPS);
			LOGGER.info(String.format("Clock frequency changed to %d ticks/sec.", targetFPS));
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
		return Math.round(targetFPS * seconds);
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
			totalTicks = 0;
			running = true;
			thread = new Thread(this::loop, "Clock");
			thread.start();
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

			long start = System.nanoTime();
			work.run();
			long frameDuration = System.nanoTime() - start;
			log(() -> "Frame", frameDuration);

			++totalTicks;

			// measure FPS
			++frames;
			if (System.nanoTime() >= fpsMmtStart + SECONDS.toNanos(1)) {
				fps = frames;
				frames = 0;
				fpsMmtStart = System.nanoTime();
			}

			// sleep as long as needed to reach target FPS
			long timeLeft = (period - frameDuration);
			if (timeLeft > 0) {
				try {
					NANOSECONDS.sleep(timeLeft);
					log(() -> "Slept", timeLeft);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}