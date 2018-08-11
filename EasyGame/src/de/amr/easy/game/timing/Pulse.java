package de.amr.easy.game.timing;

import static de.amr.easy.game.Application.LOGGER;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.beans.PropertyChangeListener;

/**
 * The "pulse" which runs the game loop with a specified frequency.
 * 
 * @author Armin Reichert
 */
public class Pulse {

	private Task renderTask;
	private Task updateTask;
	private int frequency;
	private long updateCount;
	private long period;
	private Thread thread;
	private volatile boolean running;
	private boolean loggingEnabled;

	public Pulse() {
		setFrequency(60);
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
				LOGGER.info(format("Update: %10.2f ms", updateTask.getUsedTime() / 1_000_000f));
				LOGGER.info(format("Render: %10.2f ms", renderTask.getUsedTime() / 1_000_000f));
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
					LOGGER.info(format("Sleep:  %10.2f ms", sleepTime / 1_000_000f));
				}
			} else if (timeLeft < 0) {
				overTime += (-timeLeft);
				for (int extraUpdates = 3; extraUpdates > 0 && overTime > period; overTime -= period, --extraUpdates) {
					updateTask.run();
					if (loggingEnabled) {
						LOGGER.info(format("Xpdate: %10.2f ms", updateTask.getUsedTime() / 1_000_000f));
					}
					++updateCount;
				}
			}
		}
	}
}
