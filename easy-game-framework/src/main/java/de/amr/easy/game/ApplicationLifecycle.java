package de.amr.easy.game;

import static de.amr.easy.game.Application.loginfo;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.CLOSE;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.ENTER_FULLSCREEN_MODE;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.ENTER_WINDOW_MODE;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.PAUSE;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.RESUME;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.SHOW_SETTINGS_DIALOG;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.CLOSING;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.CREATING_UI;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.INITIALIZING;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.PAUSED;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.RUNNING;

import javax.swing.SwingUtilities;

import de.amr.easy.game.ApplicationLifecycle.ApplicationEvent;
import de.amr.easy.game.ApplicationLifecycle.ApplicationState;
import de.amr.statemachine.api.TransitionMatchStrategy;
import de.amr.statemachine.core.StateMachine;

/**
 * Implementation of the lifecycle of an application.
 * 
 * @author Armin Reichert
 */
class ApplicationLifecycle extends StateMachine<ApplicationState, ApplicationEvent> {

	enum ApplicationState {
		INITIALIZING, CREATING_UI, RUNNING, PAUSED, CLOSING;
	}

	enum ApplicationEvent {
		PAUSE, RESUME, ENTER_FULLSCREEN_MODE, ENTER_WINDOW_MODE, SHOW_SETTINGS_DIALOG, CLOSE
	}

	ApplicationLifecycle(Application app, String[] cmdLine) {
		super(ApplicationState.class, TransitionMatchStrategy.BY_VALUE);
		/*@formatter:off*/		
		beginStateMachine()
			.description(String.format("[%s]", app.getName()))
			.initialState(INITIALIZING)
			.states()
				
				.state(INITIALIZING)
					.onEntry(() -> {
						loginfo("Configuring application '%s'", app.getName());
						app.configure(app.settings);
						app.processCommandLine(cmdLine);
						app.printSettings();
						app.init();
						if (app.settings.muted) {
							app.soundManager.muteAll();
						}
						setState(CREATING_UI);
					})
					
				.state(CREATING_UI)	
					.onEntry(() -> {
						SwingUtilities.invokeLater(() -> {
							app.createUserInterface();
							app.clock.start();
							loginfo("Application is running, %d frames/second", app.clock.getTargetFramerate());
						});
					})
				
				.state(RUNNING)
					.onTick(() -> {
						app.readInput();
						app.controller.update();
						app.renderCurrentView();
					})
				
				.state(PAUSED)
					.onTick(app::renderCurrentView)
				
				.state(CLOSING)
					.onEntry(() -> {
						loginfo("Closing application '%s'", app.getName());
					})
					.onTick(() -> {
						app.shell.dispose();
						// cannot exit in onEntry because CLOSING listeners would not get executed!
						System.exit(0);
					})
					
			.transitions()

				.when(CREATING_UI).then(RUNNING).condition(() -> app.clock.isTicking())
				
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
}