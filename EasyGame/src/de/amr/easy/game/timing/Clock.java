package de.amr.easy.game.timing;

import static de.amr.easy.game.Application.LOGGER;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.beans.PropertyChangeListener;

/**
 * The clock that triggers ticks and runs the game loop.
 * 
 * @author Armin Reichert
 */
public class Clock {

	private Task renderTask;
	private Task updateTask;
	private int frequency;
	private long updateCount;
	private long period;
	private Thread thread;
	private volatile boolean running;
	private boolean loggingEnabled;

	public Clock() {
		setUpdateTask(() -> {
		});
		setRenderTask(() -> {
		});
	}

	public void setUpdateTask(Runnable code) {
		updateTask = new Task(code, "ups", SECONDS.toNanos(1));
	}

	public void setRenderTask(Runnable code) {
		renderTask = new Task(code, "fps", SECONDS.toNanos(1));
	}

	public void setLoggingEnabled(boolean enabled) {
		this.loggingEnabled = enabled;
	}

	public void setFrequency(int fps) {
		this.frequency = fps;
		period = fps > 0 ? SECONDS.toNanos(1) / fps : Integer.MAX_VALUE;
		LOGGER.info("Clock frequency set to " + frequency);
	}

	public int getFrequency() {
		return frequency;
	}

	public long getUpdateCount() {
		return updateCount;
	}

	public int secToTicks(float seconds) {
		return Math.round(getFrequency() * seconds);
	}

	public float ticksToSec(int ticks) {
		return (float) ticks / getFrequency();
	}

	public synchronized void addRenderListener(PropertyChangeListener observer) {
		renderTask.addListener(observer);
	}

	public synchronized void addUpdateListener(PropertyChangeListener observer) {
		updateTask.addListener(observer);
	}

	public synchronized void start() {
		if (!running) {
			running = true;
			thread = new Thread(this::gameLoop, "GameLoop");
			thread.start();
		}
	}

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
			++updateCount;
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
					++updateCount;
				}
			}
		}
	}

	private void logTime(String taskName, long nanos) {
		LOGGER.info(format("%-7s: %10.2f ms", taskName, nanos / 1_000_000f));
	}
}
