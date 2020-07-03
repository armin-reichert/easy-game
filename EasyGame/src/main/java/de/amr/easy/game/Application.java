package de.amr.easy.game;

import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.CLOSE;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.PAUSE;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.RESUME;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationEvent.SHOW_SETTINGS_DIALOG;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.CLOSING;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.PAUSED;
import static de.amr.easy.game.ApplicationLifecycle.ApplicationState.RUNNING;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
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

	private static Application theApp;

	/**
	 * @return the application instance
	 */
	public static Application app() {
		if (theApp == null) {
			throw new IllegalStateException("Application not yet created");
		}
		return theApp;
	}

	/** Application-global logger. */
	public static final Logger LOGGER = Logger.getLogger(Application.class.getName());

	/**
	 * Creates and starts the application of the given class. The command-line arguments are parsed and
	 * assigned to the implicitly created application settings.
	 * 
	 * @param appClass application class
	 * @param cmdLine  command-line arguments
	 */
	public static void launch(Class<? extends Application> appClass, String[] cmdLine) {
		launch(appClass, new AppSettings(), cmdLine);
	}

	/**
	 * Creates and starts the application of the given class. The command-line arguments are parsed and
	 * assigned to the given application settings.
	 * 
	 * @param appClass application class
	 * @param settings application settings
	 * @param cmdLine  command-line arguments
	 */
	public static void launch(Class<? extends Application> appClass, AppSettings settings, String[] cmdLine) {
		try {
			readLoggingConfig(appClass);

			loginfo("Creating application of class '%s'", appClass.getName());
			theApp = appClass.getDeclaredConstructor().newInstance();

			loginfo("Reading configuring for application '%s'", theApp.getName());
			theApp.configure(settings, cmdLine);

			loginfo("Creating life cycle for application '%s'", theApp.getName());
			theApp.lifecycle = new ApplicationLifecycle(theApp);

			loginfo("Initializing application '%s'", theApp.getName());
			theApp.lifecycle.init();

		} catch (Exception e) {
			loginfo("Error during launch of application '%s'", theApp.getName());
			e.printStackTrace(System.err);
		}
	}

	private static void readLoggingConfig(Class<? extends Application> appClass) throws IOException {
		String path = "de/amr/easy/game/logging.properties";
		InputStream config = Application.class.getClassLoader().getResourceAsStream(path);
		if (config != null) {
			LogManager.getLogManager().readConfiguration(config);
		} else {
			System.err.println("Logging configuration '" + path + "' not available.");
			System.err.println(String.format("Application of class '%s' could not be launched.", appClass));
			System.exit(0);
		}
	}

	SoundManager soundManager;
	CollisionHandler collisionHandler;
	Lifecycle controller;
	ApplicationLifecycle lifecycle;
	AppSettings settings;
	AppShell shell;
	Image icon;

	private void configure(AppSettings settings, String... commandLine) {
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

	void createUserInterface(int width, int height, boolean fullScreen) {
		loginfo("Creating user interface for application '%s'", getName());
		String lafName = NimbusLookAndFeel.class.getName();
		try {
			UIManager.setLookAndFeel(lafName);
		} catch (Exception x) {
			loginfo("Could not set look and feel %s", lafName);
		}
		if (controller == null) {
			int defaultWidth = 640, defaultHeight = 480;
			Lifecycle defaultController = new AppInfoView(this, defaultWidth, defaultHeight);
			setController(defaultController);
			shell = new AppShell(this, defaultWidth, defaultHeight);
		} else {
			shell = new AppShell(this, width, height);
		}
		configureF2Dialog(shell.f2Dialog);
		if (fullScreen) {
			shell.showFullScreenWindow();
		} else {
			shell.showWindow();
		}
		soundManager = new SoundManager();
		if (settings.muted) {
			soundManager.muteAll();
		}
		loginfo("User interface for application '%s' has been created", getName());
	}

	void readInput() {
		Keyboard.handler.poll();
		Mouse.handler.poll();
		collisionHandler().ifPresent(CollisionHandler::update);
	}

	void render() {
		currentView().ifPresent(view -> invokeLater(() -> shell.render(view)));
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
		return shell().map(shell -> shell.f2Dialog);
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

	public boolean isPaused() {
		return lifecycle.is(PAUSED);
	}

	public boolean isRunning() {
		return lifecycle.is(RUNNING);
	}

	public void pause() {
		lifecycle.process(PAUSE);
	}

	public void resume() {
		lifecycle.process(RESUME);
	}

	public void togglePause() {
		if (isPaused()) {
			resume();
		} else {
			pause();
		}
	}

	public void showF2Dialog() {
		lifecycle.process(SHOW_SETTINGS_DIALOG);
	}

	public void toggleFullScreen() {
		shell().ifPresent(shell -> {
			if (shell.inFullScreenMode()) {
				shell.showWindow();
			} else {
				shell.showFullScreenWindow();
			}
		});
	}

	public void close() {
		lifecycle.process(CLOSE);
	}

	public void onClose(Runnable closeHandler) {
		lifecycle.addStateEntryListener(CLOSING, state -> closeHandler.run());
	}

	public boolean inFullScreenMode() {
		return shell().map(AppShell::inFullScreenMode).orElse(false);
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

	public Optional<AppShell> shell() {
		return Optional.ofNullable(shell);
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

	public AppSettings settings() {
		return settings;
	}

	public Clock clock() {
		return lifecycle.clock();
	}

	public SoundManager soundManager() {
		return soundManager;
	}

	public Lifecycle getController() {
		return controller;
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
		shell().ifPresent(shell -> shell.setIconImage(icon));
	}

	/**
	 * Sets the icon displayed in the application window.
	 * 
	 * @param path image path in class path e.g. "/images/icon.png"
	 */
	public void setIcon(String path) {
		setIcon(new ImageIcon(getClass().getResource(path)).getImage());
	}

}