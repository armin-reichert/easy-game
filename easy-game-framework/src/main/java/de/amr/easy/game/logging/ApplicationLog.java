package de.amr.easy.game.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Armin Reichert
 */
public class ApplicationLog {

	private static final Logger LOGGER = Logger.getLogger(ApplicationLog.class.getName());

	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

	private final List<String> lines = new ArrayList<>();
	private boolean shutUp = false;

	public void loginfo(String message, Object... args) {
		if (shutUp) {
			return;
		}
		LOGGER.info(() -> message.formatted(args));

		var formattedMsg = message.formatted(args);
		var timestamp = LocalDateTime.now().format(TIME_FORMAT);
		var thread = Thread.currentThread().getName();
		var line = "%s - %s [%s]".formatted(timestamp, formattedMsg, thread);
		lines.add(line);
	}

	public List<String> getLoggedLines() {
		return Collections.unmodifiableList(lines);
	}

	public boolean isShutUp() {
		return shutUp;
	}

	public void shutUp(boolean shutUp) {
		this.shutUp = shutUp;
	}
}