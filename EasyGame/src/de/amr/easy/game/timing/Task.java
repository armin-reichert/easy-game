package de.amr.easy.game.timing;

class Task implements Runnable {

	private final Runnable work;
	private final long reportInterval;
	private long lastReportTime;
	private long usedTime;
	private int numRuns;
	private int rate;

	public Task(Runnable work, long reportInterval) {
		this.work = work;
		this.reportInterval = reportInterval;
	}

	public long getUsedTime() {
		return usedTime;
	}

	public int getRate() {
		return rate;
	}

	@Override
	public void run() {
		long startTime = System.nanoTime();
		work.run();
		long endTime = System.nanoTime();
		usedTime = endTime - startTime;
		++numRuns;
		if (endTime >= lastReportTime + reportInterval) {
			rate = numRuns;
			numRuns = 0;
			lastReportTime = System.nanoTime();
		}
	}
}