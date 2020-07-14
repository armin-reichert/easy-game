package de.amr.easy.game;

import static de.amr.easy.game.Application.loginfo;
import static de.amr.easy.game.ApplicationImpl.ApplicationEvent.CLOSE;
import static de.amr.easy.game.ApplicationImpl.ApplicationEvent.ENTER_FULLSCREEN_MODE;
import static de.amr.easy.game.ApplicationImpl.ApplicationEvent.ENTER_WINDOW_MODE;
import static de.amr.easy.game.ApplicationImpl.ApplicationEvent.PAUSE;
import static de.amr.easy.game.ApplicationImpl.ApplicationEvent.RESUME;
import static de.amr.easy.game.ApplicationImpl.ApplicationEvent.SHOW_SETTINGS_DIALOG;
import static de.amr.easy.game.ApplicationImpl.ApplicationState.CLOSING;
import static de.amr.easy.game.ApplicationImpl.ApplicationState.PAUSED;
import static de.amr.easy.game.ApplicationImpl.ApplicationState.RUNNING;
import static de.amr.easy.game.ApplicationImpl.ApplicationState.STARTING;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.LogManager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.beust.jcommander.JCommander;

import de.amr.easy.game.ApplicationImpl.ApplicationEvent;
import de.amr.easy.game.ApplicationImpl.ApplicationState;
import de.amr.easy.game.assets.SoundManager;
import de.amr.easy.game.config.AppSettings;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.entity.collision.CollisionHandler;
import de.amr.easy.game.input.Keyboard;
import de.amr.easy.game.input.Mouse;
import de.amr.easy.game.timing.Clock;
import de.amr.easy.game.ui.AppInfoView;
import de.amr.easy.game.ui.AppShell;
import de.amr.easy.game.view.View;
import de.amr.easy.game.view.VisualController;
import de.amr.statemachine.api.EventMatchStrategy;
import de.amr.statemachine.core.StateMachine;

/**
 * Implementation and lifecycle definition of an application.
 * 
 * @author Armin Reichert
 */
class ApplicationImpl extends StateMachine<ApplicationState, ApplicationEvent> {

	public enum ApplicationState {
		STARTING, RUNNING, PAUSED, CLOSING;
	}

	public enum ApplicationEvent {
		PAUSE, RESUME, ENTER_FULLSCREEN_MODE, ENTER_WINDOW_MODE, SHOW_SETTINGS_DIALOG, CLOSE
	}

	private final Application app;
	private final AppSettings settings;
	private final Clock clock;
	private final SoundManager soundManager;

	private AppShell shell;
	private Image icon;
	private CollisionHandler collisionHandler;
	private Lifecycle controller;

	public ApplicationImpl(Application app, AppSettings settings, String[] cmdLine) {
		super(ApplicationState.class, EventMatchStrategy.BY_EQUALITY);
		this.app = app;
		this.settings = settings;
		soundManager = new SoundManager();
		clock = new Clock();
		clock.setTargetFrameRate(settings.fps);
		clock.onTick = this::update;
		loginfo("Configuring logger for application '%s'", app.getName());
		configureLogger(app.getClass());
		/*@formatter:off*/		
		beginStateMachine()
			.description(String.format("[%s]", app.getName()))
			.initialState(STARTING)
			.states()
				
				.state(STARTING)
					.onEntry(() -> {
						loginfo("Configuring application '%s'", app.getName());
						app.configure(settings);
						processCommandLine(cmdLine);
						app.printSettings();
						app.init();
						if (settings.muted) {
							soundManager.muteAll();
						}
						SwingUtilities.invokeLater(() -> {
							createUserInterface(settings.width, settings.height, settings.fullScreen);
							clock.start();
							loginfo("Application is running, %d frames/second", clock.getTargetFramerate());
						});
					})
				
				.state(RUNNING)
					.onTick(() -> {
						readInput();
						controller.update();
						render();
					})
				
				.state(PAUSED)
					.onTick(this::render)
				
				.state(CLOSING)
					.onEntry(() -> {
						loginfo("Closing application '%s'", app.getName());
					})
					.onTick(() -> {
						shell.dispose();
						// cannot exit in onEntry because CLOSING listeners would not get executed!
						System.exit(0);
					})
					
			.transitions()

				.when(STARTING).then(RUNNING).condition(() -> clock.isTicking())
				
				.when(RUNNING).then(PAUSED).on(PAUSE).act(() -> soundManager.muteAll())
				
				.when(RUNNING).then(CLOSING).on(CLOSE)
	
				.stay(RUNNING).on(ENTER_FULLSCREEN_MODE).act(() -> shell.showFullScreenWindow())

				.stay(RUNNING).on(ENTER_WINDOW_MODE).act(() -> shell.showWindow())
					
				.stay(RUNNING).on(SHOW_SETTINGS_DIALOG).act(() -> shell.showF2Dialog())
				
				.when(PAUSED).then(RUNNING).on(RESUME).act(() -> soundManager.unmuteAll())
			
				.when(PAUSED).then(CLOSING).on(CLOSE)
				
				.stay(PAUSED).on(ENTER_FULLSCREEN_MODE).act(() -> shell.showFullScreenWindow())

				.stay(PAUSED).on(ENTER_WINDOW_MODE).act(() -> shell.showWindow())
	
				.stay(PAUSED).on(SHOW_SETTINGS_DIALOG).act(() -> shell.showF2Dialog())

		.endStateMachine();
		/*@formatter:on*/
	}

	private void configureLogger(Class<? extends Application> appClass) {
		String path = "de/amr/easy/game/logging.properties";
		InputStream config = Application.class.getClassLoader().getResourceAsStream(path);
		if (config != null) {
			try {
				LogManager.getLogManager().readConfiguration(config);
				return;
			} catch (IOException x) {
				x.printStackTrace(System.err);
			}
		}
		System.err.println("Logging configuration '" + path + "' not available.");
		System.err.println(String.format("Application of class '%s' could not be launched.", appClass));
		System.exit(0);
	}

	private void createUserInterface(int width, int height, boolean fullScreen) {
		loginfo("Creating user interface for application '%s'", app.getName());
		String lafName = NimbusLookAndFeel.class.getName();
		try {
			UIManager.setLookAndFeel(lafName);
			loginfo("Look-and-Feel is %s", UIManager.getLookAndFeel().toString());
		} catch (Exception x) {
			loginfo("Could not set look and feel %s", lafName);
		}
		if (controller == null) {
			loginfo("No controller has been set, using default controller");
			int defaultWidth = 640, defaultHeight = 480;
			AppInfoView defaultController = new AppInfoView(app, defaultWidth, defaultHeight);
			app.setController(defaultController);
			shell = new AppShell(app, defaultWidth, defaultHeight);
		} else {
			shell = new AppShell(app, width, height);
		}
		app.configureF2Dialog(shell.f2Dialog);
		if (fullScreen) {
			shell.showFullScreenWindow();
		} else {
			shell.showWindow();
		}
		loginfo("User interface for application '%s' has been created", app.getName());
	}

	private void readInput() {
		Keyboard.poll();
		Mouse.handler.poll();
		app.collisionHandler().ifPresent(CollisionHandler::update);
	}

	private void render() {
		app.currentView().ifPresent(view -> invokeLater(() -> shell.render(view)));
	}

	private void processCommandLine(String[] commandLine) {
		JCommander commander = JCommander.newBuilder().addObject(settings).build();
		commander.parse(commandLine);
		if (settings.help) {
			commander.setProgramName(app.getName());
			commander.usage();
			System.exit(0);
		}
	}

	public Clock clock() {
		return clock;
	}

	public Optional<AppShell> shell() {
		return Optional.ofNullable(shell);
	}

	public Optional<View> currentView() {
		if (controller instanceof View) {
			return Optional.ofNullable((View) controller);
		}
		if (controller instanceof VisualController) {
			return ((VisualController) controller).currentView();
		}
		return Optional.empty();
	}

	public void createCollisionHandler() {
		if (collisionHandler == null) {
			collisionHandler = new CollisionHandler();
		}
	}

	public Optional<CollisionHandler> collisionHandler() {
		return Optional.ofNullable(collisionHandler);
	}

	public SoundManager soundManager() {
		return soundManager;
	}

	public Lifecycle controller() {
		return controller;
	}

	public void setController(Lifecycle controller, boolean initializeIt) {
		if (controller == null) {
			throw new IllegalArgumentException("Application controller must not be null.");
		}
		if (controller != this.controller) {
			this.controller = controller;
			loginfo("Application controller is: %s", controller);
			if (initializeIt) {
				controller.init();
				loginfo("Controller initialized.");
			}
		}
	}

	public Image icon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
		shell().ifPresent(shell -> shell.setIconImage(icon));
	}

	public AppSettings settings() {
		return settings;
	}
}