package de.amr.easy.game;

import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.CLOSE;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.ENTER_FULLSCREEN_MODE;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.ENTER_WINDOW_MODE;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.PAUSE;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.RESUME;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.SHOW_SETTINGS_DIALOG;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.CLOSING;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.PAUSED;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.RUNNING;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.STARTING;

import javax.swing.SwingUtilities;

import de.amr.easy.game.ApplicationLifecycle.ApplicationEvent;
import de.amr.easy.game.ApplicationLifecycle.ApplicationState;
import de.amr.easy.game.timing.Clock;
import de.amr.statemachine.api.EventMatchStrategy;
import de.amr.statemachine.core.StateMachine;

/**
 * Defines the lifecycle behavior of an application.
 * 
 * @author Armin Reichert
 */
class ApplicationLifecycle extends StateMachine<ApplicationState, ApplicationEvent> {

	private final Clock clock = new Clock();

	public enum ApplicationState {
		STARTING, RUNNING, PAUSED, CLOSING;
	}

	public enum ApplicationEvent {
		PAUSE, RESUME, ENTER_FULLSCREEN_MODE, ENTER_WINDOW_MODE, SHOW_SETTINGS_DIALOG, CLOSE
	}

	public ApplicationLifecycle(Application app) {
		super(ApplicationState.class, EventMatchStrategy.BY_EQUALITY);
		/*@formatter:off*/		
			beginStateMachine()
				.description(String.format("[%s]", app.getName()))
				.initialState(STARTING)
				.states()
					
					.state(STARTING)
						.onEntry(() -> {
							clock.setTargetFrameRate(app .settings.fps);
							clock.onTick = this::update;
							app.init();
							SwingUtilities.invokeLater(() -> {
								app.createUserInterface(app.settings.width, app.settings.height, app.settings.fullScreen);
								clock.start();
								Application.loginfo("Application is running, %d frames/second", clock.getTargetFramerate());
							});
						})
					
					.state(RUNNING)
						.onTick(() -> {
							app.readInput();
							app.controller.update();
							app.render();
						})
					
					.state(PAUSED)
						.onTick(app::render)
					
					.state(CLOSING)
						.onEntry(() -> {
							Application.loginfo("Closing application '%s'", app.getName());
						})
						.onTick(() -> {
							app.shell.dispose();
							// cannot exit in onEntry because CLOSING listeners would not get executed!
							System.exit(0);
						})
						
				.transitions()

					.when(STARTING).then(RUNNING).condition(() -> clock.isTicking())
					
					.when(RUNNING).then(PAUSED).on(PAUSE).act(() -> app.soundManager.muteAll())
					
					.when(RUNNING).then(CLOSING).on(CLOSE)
		
					.stay(RUNNING).on(ENTER_FULLSCREEN_MODE).act(() -> app.shell.showFullScreenWindow())

					.stay(RUNNING).on(ENTER_WINDOW_MODE).act(() -> app.shell.showWindow())
						
					.stay(RUNNING).on(SHOW_SETTINGS_DIALOG).act(() -> app.shell.showF2Dialog())
					
					.when(PAUSED).then(RUNNING).on(RESUME).act(() -> app.soundManager.unmuteAll())
				
					.when(PAUSED).then(CLOSING).on(CLOSE)
					
					.stay(PAUSED).on(ENTER_FULLSCREEN_MODE).act(() -> app.shell.showFullScreenWindow())

					.stay(PAUSED).on(ENTER_WINDOW_MODE).act(() -> app.shell.showWindow())
		
					.stay(PAUSED).on(SHOW_SETTINGS_DIALOG).act(() -> app.shell.showF2Dialog())

			.endStateMachine();
			/*@formatter:on*/
	}

	public Clock clock() {
		return clock;
	}
}