package de.amr.easy.game.timing;

import java.util.concurrent.TimeUnit;

/**
 * Encapsulates some "work" and measures the frame rate of its execution.
 * 
 * @author Armin Reichert
 */
class Task {

	private final Runnable work;
	private long runningTime; // nanoseconds
	private long measurementStartTime; // nanoseconds
	private int frames;
	private int frameRate;

	public Task(Runnable work) {
		this.work = work;
	}

	/**
	 * @return last running time of this task (nanoseconds)
	 */
	public long getRunningTime() {
		return runningTime;
	}

	/**
	 * @return number of times this task has been run during the last measurement period ("frame rate")
	 */
	public int getFrameRate() {
		return frameRate;
	}

	public void run() {
		long start = System.nanoTime();
		work.run();
		long end = System.nanoTime();
		runningTime = end - start;
		++frames;
		if (end - measurementStartTime >= TimeUnit.SECONDS.toNanos(1)) {
			frameRate = frames;
			frames = 0;
			measurementStartTime = System.nanoTime();
		}
	}
}