package de.amr.easy.game.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.amr.statemachine.api.Log;

public class ApplicationLog implements Log {

	private List<String> loggedMessages = new ArrayList<>();
	private DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
	private boolean shutUp = false;

	@Override
	public void loginfo(String format, Object... args) {
		if (shutUp) {
			return;
		}
		String message = String.format("%s [%s] %s", LocalDateTime.now().format(df), Thread.currentThread().getName(),
				String.format(format, args));
		loggedMessages.add(message);
		System.out.println(message);
	}

	@Override
	public boolean isShutUp() {
		return shutUp;
	}

	@Override
	public void shutUp(boolean shutUp) {
		this.shutUp = shutUp;
	}

	public List<String> getLoggedLines() {
		return Collections.unmodifiableList(loggedMessages);
	}
}