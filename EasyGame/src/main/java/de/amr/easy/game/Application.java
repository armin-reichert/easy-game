package de.amr.easy.game;

import static de.amr.easy.game.Application.ApplicationEvent.CLOSE;
import static de.amr.easy.game.Application.ApplicationEvent.SHOW_SETTINGS_DIALOG;
import static de.amr.easy.game.Application.ApplicationEvent.TOGGLE_FULLSCREEN;
import static de.amr.easy.game.Application.ApplicationEvent.TOGGLE_PAUSE;
import static de.amr.easy.game.Application.ApplicationState.CLOSED;
import static de.amr.easy.game.Application.ApplicationState.CREATING_UI;
import static de.amr.easy.game.Application.ApplicationState.PAUSED;
import static de.amr.easy.game.Application.ApplicationState.RUNNING;
import static de.amr.easy.game.Application.ApplicationState.STARTING;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.beust.jcommander.JCommander;

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
 * Every application must extend this class and provide a public constructor without arguments. To
 * start the application, either of the static methods {@link #launch(Class, String[])} or
 * {@link #launch(Class, AppSettings, String[])} has to be called. The second variant allows an
 * application to specify an extended settings object, otherwise the default settings object of
 * class {@link AppSettings} is used. Command-line arguments are merged into the application
 * settings after having executed the {@link #configure(AppSettings)} hook method. For a complete
 * list of the supported command-line arguments and application settings, see class
 * {@link AppSettings}.
 * <p>
 * Example:
 * 
 * <pre>
 * public class MyFirstApp extends Application {
 * 
 * 	public static void main(String... args) {
 * 		launch(MyFirstApp.class, args);
 * 	}
 * 
 *	&#64;Override
 * 	public void configure(AppSettings settings) {
 * 		settings.width = 800;
 * 		settings.height = 600;
 * 		settings.title = "My First Application";
 * 	}
 * 
 *	&#64;Override
 *	public void init() {
 *		setController(new MyFirstAppController());
 *	}
 *
 *  class MyFirstAppController implements Lifecycle {
 *  
 *  	&#64;Override
 *  	public void init() {
 *  	}
 *  
 *  	&#64;Override
 *  	// <em>called at every tick of the application clock, normally 60 times/sec</em>
 *  	public void update() {
 *  	}
 *  }
 * </pre>
 * 
 * <p>
 * The following keyboard shortcuts are predefined:
 * <ul>
 * <li>{@code F11}: Toggles between window and full-screen mode
 * <li>{@code F2}: Opens a dialog for changing the clock speed
 * <li>{@code CTRL+P}: Pauses/resumes the application
 * </ul>
 * 
 * @author Armin Reichert
 */
public abstract class Application {

	enum ApplicationState {
		CREATING_UI, STARTING, RUNNING, PAUSED, CLOSED;
	}

	enum ApplicationEvent {
		TOGGLE_PAUSE, TOGGLE_FULLSCREEN, SHOW_SETTINGS_DIALOG, CLOSE
	}

	/** Application singleton. */
	private static Application theApplication;

	/** Application-global logger. */
	public static final Logger LOGGER = Logger.getLogger(Application.class.getName());

	private AppSettings settings;
	private AppShell shell;
	private Clock clock;
	private CollisionHandler collisionHandler;
	private Consumer<Application> exitHandler;
	private Lifecycle controller;
	private Image icon;
	private StateMachine<ApplicationState, ApplicationEvent> lifecycle;
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	/** Static access to the application instance. */
	public static Application app() {
		if (theApplication == null) {
			throw new IllegalStateException("Application instance not yet accessible");
		}
		return theApplication;
	}

	/**
	 * Creates and starts the application of the given class. The command-line arguments are parsed and
	 * assigned to the implicitly created application settings.
	 * 
	 * @param appClass application class
	 * @param args     command-line arguments
	 */
	public static void launch(Class<? extends Application> appClass, String[] args) {
		launch(appClass, new AppSettings(), args);
	}

	/**
	 * Creates and starts the application of the given class. The command-line arguments are parsed and
	 * assigned to the given application settings.
	 * 
	 * @param appClass application class
	 * @param settings application settings
	 * @param args     command-line arguments
	 */
	public static void launch(Class<? extends Application> appClass, AppSettings settings, String[] args) {
		try {
			InputStream in = appClass.getClassLoader().getResourceAsStream("de/amr/easy/game/logging.properties");
			LogManager.getLogManager().readConfiguration(in);
		} catch (NullPointerException | SecurityException | IOException e) {
			System.err.println("Could not load logging configuration");
			e.printStackTrace(System.err);
			return;
		}
		try {
			theApplication = appClass.getDeclaredConstructor().newInstance();
			theApplication.build(settings, args);
			theApplication.lifecycle.init();
		} catch (Exception e) {
			loginfo("Could not launch application of class '%s'", appClass.getName());
			e.printStackTrace(System.err);
		}
	}

	private void build(AppSettings settings, String[] args) {
		loginfo("Building application '%s'", getClass().getName());
		this.settings = settings;
		configure(settings);
		JCommander commander = JCommander.newBuilder().addObject(settings).build();
		commander.parse(args);
		if (settings.help) {
			commander.setProgramName(getClass().getSimpleName());
			commander.usage();
			System.exit(0);
		}
		printSettings();
		lifecycle = createLifecycle();
		clock = new Clock(settings.fps);
		clock.onTick = lifecycle::update;
	}

	/**
	 * Prints the application settings to the logger.
	 */
	protected void printSettings() {
		loginfo("Configuration:");
		loginfo("\tTitle:     %s", settings.title);
		loginfo("\tWidth:     %d", settings.width);
		loginfo("\tHeight:    %d", settings.height);
		loginfo("\tScaling:   %.2f", settings.scale);
		loginfo("\tFramerate: %d ticks/sec", settings.fps);
	}

	private StateMachine<ApplicationState, ApplicationEvent> createLifecycle() {
		return StateMachine.
		/*@formatter:off*/		
		beginStateMachine(ApplicationState.class, ApplicationEvent.class, EventMatchStrategy.BY_EQUALITY)
			.description(String.format("[%s]", getClass().getSimpleName()))
			.initialState(STARTING)
			.states()
				
				.state(STARTING)
					.onEntry(() -> {
						init();
						clock.start();
						loginfo("Clock started, %d frames/second", clock.getTargetFramerate());
					})
				
				.state(CREATING_UI)
					.onEntry(() -> {
						try {
							UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
						} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
								| UnsupportedLookAndFeelException x) {
							loginfo("Could not set Nimbus look and feel");
						}
						if (controller != null) {
							shell = new AppShell(this, settings.width, settings.height);
						} else {
							shell = new AppShell(this, 800, 600);
							setController(new AppInfoView(800,600));
						}
						SwingUtilities.invokeLater(() -> shell.display(settings.fullScreenOnStart));
					})
				
				
				.state(RUNNING)
					.onTick(() -> {
						Keyboard.handler.poll();
						Mouse.handler.poll();
						collisionHandler().ifPresent(CollisionHandler::update);
						controller.update();
						currentView().ifPresent(shell::render);
					})
				
				.state(PAUSED)
					.onEntry(() -> fireChange("paused", false, true))
					.onTick(() -> currentView().ifPresent(shell::render))
					.onExit(() -> fireChange("paused", true, false))
				
				.state(CLOSED)
					.onEntry(() -> {
						if (exitHandler != null) {
							LOGGER.info(() -> "Running exit handler");
							exitHandler.accept(this);
						}
						clock.stop();
						LOGGER.info(() -> "Application terminated.");
						System.exit(0);
					})
					
			.transitions()

				.when(STARTING).then(CREATING_UI).condition(() -> clock.isTicking())
				
				.when(CREATING_UI).then(RUNNING).condition(() -> shell.isVisible())
				
				.when(RUNNING).then(PAUSED).on(TOGGLE_PAUSE)
				
				.when(RUNNING).then(CLOSED).on(CLOSE)
	
				.stay(RUNNING).on(TOGGLE_FULLSCREEN).act(() -> shell.toggleDisplayMode())
					
				.stay(RUNNING).on(SHOW_SETTINGS_DIALOG).act(() -> shell.showSettingsDialog())
				
				.when(PAUSED).then(RUNNING).on(TOGGLE_PAUSE)
			
				.when(PAUSED).then(CLOSED).on(CLOSE)
				
				.stay(PAUSED).on(TOGGLE_FULLSCREEN).act(() -> shell.toggleDisplayMode())
	
				.stay(PAUSED).on(SHOW_SETTINGS_DIALOG).act(() -> shell.showSettingsDialog())

		.endStateMachine();
		/*@formatter:on*/
	}

	/**
	 * Hook method where the application settings can be configured. The command-line arguments are
	 * parsed and merged into the settings object immediately <em>after</em> this method has been called
	 * such that command-line arguments can override the settings made here.
	 * 
	 * @param settings application settings
	 */
	protected abstract void configure(AppSettings settings);

	/**
	 * Hook method getting called after the application has been configured and before the clock starts
	 * ticking.
	 */
	public abstract void init();

	/**
	 * Convenience method for logging to application logger with level INFO.
	 * 
	 * @param format message format
	 * @param args   message arguments
	 */
	public static void loginfo(String format, Object... args) {
		LOGGER.info(String.format(format, args));
	}

	public boolean isPaused() {
		return lifecycle.is(PAUSED);
	}

	public void togglePause() {
		lifecycle.process(TOGGLE_PAUSE);
	}

	public void showSettingsDialog() {
		lifecycle.process(SHOW_SETTINGS_DIALOG);
	}

	public void toggleFullScreen() {
		lifecycle.process(TOGGLE_FULLSCREEN);
	}

	public void close() {
		lifecycle.process(CLOSE);
	}

	public boolean inFullScreenMode() {
		return shell != null && shell.inFullScreenMode();
	}

	public Optional<CollisionHandler> collisionHandler() {
		return Optional.ofNullable(collisionHandler);
	}

	public void createCollisionHandler() {
		if (collisionHandler == null) {
			collisionHandler = new CollisionHandler();
		}
	}

	public AppSettings settings() {
		return settings;
	}

	public Clock clock() {
		return clock;
	}

	public void setExitHandler(Consumer<Application> exitHandler) {
		this.exitHandler = Objects.requireNonNull(exitHandler);
	}

	/**
	 * Makes the given controller the current one and optionally initializes it.
	 * 
	 * @param controller   the new application controller
	 * @param initializeIt if the controller should be initialized
	 */
	public void setController(Lifecycle controller, boolean initializeIt) {
		if (controller == null) {
			throw new IllegalArgumentException("Application controller must not be null.");
		}
		if (controller != this.controller) {
			this.controller = controller;
			LOGGER.info("Application controller is: " + controller);
			if (initializeIt) {
				controller.init();
				LOGGER.info("Controller initialized.");
			}
		}
	}

	/**
	 * Sets the given controller and calls its initializer method.
	 * 
	 * @param controller new controller
	 */
	public void setController(Lifecycle controller) {
		setController(controller, true);
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

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
		if (shell != null) {
			shell.setIconImage(icon);
		}
	}

	public void addChangeListener(PropertyChangeListener listener) {
		changes.addPropertyChangeListener(listener);
	}

	public void removeChangeListener(PropertyChangeListener listener) {
		changes.removePropertyChangeListener(listener);
	}

	public void fireChange(String changeName, Object oldValue, Object newValue) {
		changes.firePropertyChange(changeName, oldValue, newValue);
	}
}