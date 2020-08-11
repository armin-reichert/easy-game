package de.amr.easy.game.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.amr.statemachine.api.Log;

public class ApplicationLog implements Log {

	private boolean shutUp = false;
	private List<String> loggedMessages = new ArrayList<>();

	@Override
	public boolean isShutUp() {
		return shutUp;
	}

	@Override
	public void shutUp(boolean shutUp) {
		this.shutUp = shutUp;
	}

	@Override
	public void loginfo(String format, Object... args) {
		if (shutUp) {
			return;
		}
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
		String timestamp = LocalDateTime.now().format(df);
		String message = String.format("[%s] %s", timestamp, String.format(format, args));
		loggedMessages.add(message);
		System.out.println(message);
	}

	public List<String> getLoggedLines() {
		return Collections.unmodifiableList(loggedMessages);
	}
}