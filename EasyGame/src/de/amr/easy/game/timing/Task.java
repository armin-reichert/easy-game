package de.amr.easy.game.timing;

import java.util.concurrent.TimeUnit;

class Task {

	private final Runnable work;
	private long lastReportTimeNanos;
	private long usedTimeNanos;
	private int runs;
	private int rate;

	public Task(Runnable work) {
		this.work = work;
	}

	public long getUsedTimeNanos() {
		return usedTimeNanos;
	}

	public int getRate() {
		return rate;
	}

	public void run() {
		long startTime = System.nanoTime();
		work.run();
		long endTime = System.nanoTime();
		usedTimeNanos = endTime - startTime;
		if (endTime >= lastReportTimeNanos + TimeUnit.SECONDS.toNanos(1)) {
			rate = runs;
			runs = 0;
			lastReportTimeNanos = System.nanoTime();
		}
		++runs;
	}
}