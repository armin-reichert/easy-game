package de.amr.easy.game.timing;

class Task {

	private final Runnable work;
	private final long reportIntervalNanos;
	private long lastReportTimeNanos;
	private long usedTimeNanos;
	private int runs;
	private int rate;

	public Task(Runnable work, long reportIntervalNanos) {
		this.work = work;
		this.reportIntervalNanos = reportIntervalNanos;
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
		if (endTime >= lastReportTimeNanos + reportIntervalNanos) {
			rate = runs;
			runs = 0;
			lastReportTimeNanos = System.nanoTime();
		}
		++runs;
	}
}