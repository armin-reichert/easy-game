package de.amr.easy.game.timing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

class Task implements Runnable {

	private final Runnable work;
	private final PropertyChangeSupport listeners;
	private final String propertyName;
	private final long reportInterval;
	private long lastReportTime;
	private long usedTime;
	private int numRuns;

	public Task(Runnable work, String propertyName, long reportInterval) {
		this.work = work;
		this.propertyName = propertyName;
		this.reportInterval = reportInterval;
		listeners = new PropertyChangeSupport(this);
	}

	public void addListener(PropertyChangeListener observer) {
		listeners.addPropertyChangeListener(observer);
	}

	public long getUsedTime() {
		return usedTime;
	}

	@Override
	public void run() {
		long startTime = System.nanoTime();
		work.run();
		long endTime = System.nanoTime();
		usedTime = endTime - startTime;
		++numRuns;
		if (endTime >= lastReportTime + reportInterval) {
			listeners.firePropertyChange(propertyName, null, numRuns);
			numRuns = 0;
			lastReportTime = System.nanoTime();
		}
	}
}
