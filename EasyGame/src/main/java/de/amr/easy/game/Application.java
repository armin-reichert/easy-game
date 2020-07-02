package de.amr.easy.game;

import static de.amr.easy.game.Application.ApplicationEvent.CLOSE;
import static de.amr.easy.game.Application.ApplicationEvent.SHOW_SETTINGS_DIALOG;
import static de.amr.easy.game.Application.ApplicationEvent.TOGGLE_FULLSCREEN;
import static de.amr.easy.game.Application.ApplicationEvent.TOGGLE_PAUSE;
import static de.amr.easy.game.Application.ApplicationState.CLOSING;
import static de.amr.easy.game.Application.ApplicationState.PAUSED;
import static de.amr.easy.game.Application.ApplicationState.RUNNING;
import static de.amr.easy.game.Application.ApplicationState.STARTING;

import java.awt.Image;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.beust.jcommander.JCommander;

import de.amr.easy.game.assets.SoundManager;
import de.amr.easy.game.config.AppSettings;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.entity.collision.CollisionHandler;
import de.amr.easy.game.input.Keyboard;
import de.amr.easy.game.input.Mouse;
import de.amr.easy.game.timing.Clock;
import de.amr.easy.game.ui.AppInfoView;
import de.amr.easy.game.ui.AppShell;
import de.amr.easy.game.ui.f2dialog.F2DialogAPI;
import de.amr.easy.game.view.View;
import de.amr.easy.game.view.VisualController;
import de.amr.statemachine.api.EventMatchStrategy;
import de.amr.statemachine.core.State;
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

	public enum ApplicationState {
		STARTING, RUNNING, PAUSED, CLOSING;
	}

	enum ApplicationEvent {
		TOGGLE_PAUSE, TOGGLE_FULLSCREEN, SHOW_SETTINGS_DIALOG, CLOSE
	}

	/** Application-global logger. */
	public static final Logger LOGGER = Logger.getLogger(Application.class.getName());

	private static Application theApplication;
	private AppSettings settings;
	private StateMachine<ApplicationState, ApplicationEvent> life;
	private Clock clock;
	private CollisionHandler collisionHandler;
	private Lifecycle controller;
	private AppShell shell;
	private Image icon;
	private SoundManager soundManager = new SoundManager();

	/**
	 * @return the application instance
	 */
	public static Application app() {
		if (theApplication == null) {
			throw new IllegalStateException("Application not yet created");
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
	 * @param appClass    application class
	 * @param settings    application settings
	 * @param commandLine command-line arguments
	 */
	public static void launch(Class<? extends Application> appClass, AppSettings settings, String[] commandLine) {
		try {
			String loggingConfigPath = "de/amr/easy/game/logging.properties";
			InputStream in = Application.class.getClassLoader().getResourceAsStream(loggingConfigPath);
			if (in == null) {
				System.err.println("Could not start application");
				System.err.println("Reason: logging configuration at '" + loggingConfigPath + "' not available.");
				return;
			}
			LogManager.getLogManager().readConfiguration(
					Application.class.getClassLoader().getResourceAsStream("de/amr/easy/game/logging.properties"));

			loginfo("Creating application of class '%s'", appClass.getName());
			theApplication = appClass.getDeclaredConstructor().newInstance();

			loginfo("Configuring application '%s'", theApplication.getName());
			theApplication.configureAndMergeCommandLine(settings, commandLine);

			theApplication.clock = new Clock();
			theApplication.clock.setTargetFrameRate(settings.fps);

			loginfo("Creating life cycle for application '%s'", theApplication.getName());
			createLife(theApplication);
			theApplication.life.init();

		} catch (Exception e) {
			loginfo("Could not launch application");
			e.printStackTrace(System.err);
		}
	}

	private static void createLife(Application app) {
		app.life = StateMachine. // cool line ;-)
		/*@formatter:off*/		
		beginStateMachine(ApplicationState.class, ApplicationEvent.class, EventMatchStrategy.BY_EQUALITY)
			.description(String.format("[%s]", app.getName()))
			.initialState(STARTING)
			.states()
				
				.state(STARTING)
					.onEntry(() -> {
						app.init();
						SwingUtilities.invokeLater(app::showUIAndStart);
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
						loginfo("Closing application '%s'", app.getName());
					})
					.onTick(() -> {
						app.shell.dispose();
						// cannot exit in onEntry because CLOSING listeners would not get executed!
						System.exit(0);
					})
					
			.transitions()

				.when(STARTING).then(RUNNING).condition(() -> app.clock.isTicking())
				
				.when(RUNNING).then(PAUSED).on(TOGGLE_PAUSE).act(() -> app.soundManager.muteAll())
				
				.when(RUNNING).then(CLOSING).on(CLOSE)
	
				.stay(RUNNING).on(ApplicationEvent.TOGGLE_FULLSCREEN).act(() -> app.shell.toggleDisplayMode())
					
				.stay(RUNNING).on(SHOW_SETTINGS_DIALOG).act(() -> app.shell.showF2Dialog())
				
				.when(PAUSED).then(RUNNING).on(TOGGLE_PAUSE).act(() -> app.soundManager.unmuteAll())
			
				.when(PAUSED).then(CLOSING).on(CLOSE)
				
				.stay(PAUSED).on(TOGGLE_FULLSCREEN).act(() -> app.shell.toggleDisplayMode())
	
				.stay(PAUSED).on(SHOW_SETTINGS_DIALOG).act(() -> app.shell.showF2Dialog())

		.endStateMachine();
		/*@formatter:on*/
	}

	private void configureAndMergeCommandLine(AppSettings settings, String... commandLine) {
		this.settings = settings;
		configure(settings);
		processCommandLine(commandLine);
		printSettings();
	}

	private void processCommandLine(String[] commandLine) {
		JCommander commander = JCommander.newBuilder().addObject(settings).build();
		commander.parse(commandLine);
		if (settings.help) {
			commander.setProgramName(getName());
			commander.usage();
			System.exit(0);
		}
	}

	private void showUIAndStart() {
		loginfo("Starting application '%s'", getName());
		if (settings.muted) {
			soundManager.muteAll();
		}
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException x) {
			loginfo("Could not set Nimbus look and feel");
		}
		int width = settings.width, height = settings.height;
		if (controller == null) {
			width = 640;
			height = 480;
			// use fallback controller
			setController(new AppInfoView(this, width, height));
		}
		shell = new AppShell(this, width, height);
		configureF2Dialog(shell.f2Dialog);
		shell.display(settings.fullScreen);
		clock.onTick = life::update;
		clock.start();
		loginfo("Application is running, %d frames/second", clock.getTargetFramerate());
	}

	private void readInput() {
		Keyboard.handler.poll();
		Mouse.handler.poll();
		collisionHandler().ifPresent(CollisionHandler::update);
	}

	private void render() {
		currentView().ifPresent(shell::render);
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
	 * Hook method that is called after the application shell has been created. Used to configure the F2
	 * dialog.
	 * 
	 * @param dialog the F2 dialog
	 */
	public void configureF2Dialog(F2DialogAPI dialog) {
	}

	/**
	 * Prints the application settings to the logger.
	 */
	protected void printSettings() {
		loginfo("Configuration:");
		settings.print();
	}

	/**
	 * @return the F2 dialog if already created
	 */
	public Optional<F2DialogAPI> f2Dialog() {
		return shell != null ? Optional.of(shell.f2Dialog) : Optional.empty();
	}

	/**
	 * Convenience method for logging to application logger with level INFO.
	 * 
	 * @param format message format
	 * @param args   message arguments
	 */
	public static void loginfo(String format, Object... args) {
		LOGGER.info(String.format(format, args));
	}

	public String getName() {
		return getClass().getSimpleName();
	}

	public Lifecycle getController() {
		return controller;
	}

	public boolean isPaused() {
		return life.is(PAUSED);
	}

	public boolean isRunning() {
		return life.is(RUNNING);
	}

	public void togglePause() {
		life.process(TOGGLE_PAUSE);
	}

	public void showF2Dialog() {
		life.process(SHOW_SETTINGS_DIALOG);
	}

	public void toggleFullScreen() {
		life.process(TOGGLE_FULLSCREEN);
	}

	public void close() {
		life.process(CLOSE);
	}

	public boolean inFullScreenMode() {
		return shell != null && shell.inFullScreenMode();
	}

	public Optional<CollisionHandler> collisionHandler() {
		return Optional.ofNullable(collisionHandler);
	}

	/**
	 * If an application wants to use the built-in collision handling, it must initialize it using this
	 * method.
	 */
	public void createCollisionHandler() {
		if (collisionHandler == null) {
			collisionHandler = new CollisionHandler();
		}
	}

	public AppShell shell() {
		return shell;
	}

	public AppSettings settings() {
		return settings;
	}

	public Clock clock() {
		return clock;
	}

	public SoundManager soundManager() {
		return soundManager;
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

	/**
	 * @return the current view if available
	 */
	public Optional<View> currentView() {
		if (controller instanceof View) {
			return Optional.ofNullable((View) controller);
		}
		if (controller instanceof VisualController) {
			return ((VisualController) controller).currentView();
		}
		return Optional.empty();
	}

	/**
	 * @return the application window icon
	 */
	public Image getIcon() {
		return icon;
	}

	/**
	 * Sets the icon displayed in the application window.
	 * 
	 * @param icon the icon to use
	 */
	public void setIcon(Image icon) {
		this.icon = icon;
		if (shell != null) {
			shell.setIconImage(icon);
		}
	}

	/**
	 * Sets the icon displayed in the application window.
	 * 
	 * @param path image path in class path e.g. "/images/icon.png"
	 */
	public void setIcon(String path) {
		setIcon(new ImageIcon(getClass().getResource(path)).getImage());
	}

	/**
	 * Adds a listener that is called when the given state is entered. <br>
	 * Example:
	 * 
	 * <pre>
	 * app().onEntry(ApplicationState.PAUSED, state -> goNapping(state));
	 * </pre>
	 * 
	 * @param state    state to be observed
	 * @param listener called when the state is entered
	 */
	public void onEntry(ApplicationState state, Consumer<State<ApplicationState>> listener) {
		life.addStateEntryListener(state, listener);
	}

	/**
	 * Adds a listener that is called when the given state is left.
	 * 
	 * Example:
	 * 
	 * <pre>
	 * app().onExit(ApplicationState.PAUSED, state -> wakeUp(state));
	 * </pre>
	 * 
	 * @param state    state to be observed
	 * @param listener called when the state is left
	 */
	public void onExit(ApplicationState state, Consumer<State<ApplicationState>> listener) {
		life.addStateExitListener(state, listener);
	}
}