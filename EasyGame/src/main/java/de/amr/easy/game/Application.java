package de.amr.easy.game;

import static de.amr.easy.game.Application.ApplicationEvent.CLOSE;
import static de.amr.easy.game.Application.ApplicationEvent.SHOW_SETTINGS_DIALOG;
import static de.amr.easy.game.Application.ApplicationEvent.TOGGLE_FULLSCREEN;
import static de.amr.easy.game.Application.ApplicationEvent.TOGGLE_PAUSE;
import static de.amr.easy.game.Application.ApplicationState.CLOSED;
import static de.amr.easy.game.Application.ApplicationState.PAUSED;
import static de.amr.easy.game.Application.ApplicationState.RUNNING;
import static de.amr.easy.game.Application.ApplicationState.STARTING;

import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.beust.jcommander.JCommander;

import de.amr.easy.game.config.AppSettings;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.entity.collision.CollisionHandler;
import de.amr.easy.game.input.Keyboard;
import de.amr.easy.game.input.KeyboardHandler;
import de.amr.easy.game.input.Mouse;
import de.amr.easy.game.input.MouseHandler;
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
 * settings and can be modified inside the {@link #configure(AppSettings)} hook method. For a
 * complete list of the supported command-line arguments and application settings, see class
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
		STARTING, RUNNING, PAUSED, CLOSED;
	}

	public enum ApplicationEvent {
		TOGGLE_PAUSE, TOGGLE_FULLSCREEN, SHOW_SETTINGS_DIALOG, CLOSE
	}

	static {
		InputStream stream = Application.class.getClassLoader().getResourceAsStream("logging.properties");
		if (stream == null) {
			throw new RuntimeException("Could not load logging property file");
		}
		try {
			LogManager.getLogManager().readConfiguration(stream);
		} catch (IOException | SecurityException e) {
			throw new RuntimeException("Could not read logging configuration");
		}
	}

	/** Application-global logger. */
	public static final Logger LOGGER = Logger.getLogger(Application.class.getName());

	/**
	 * Convenience method for logging to application logger with level INFO.
	 * 
	 * @param format message format
	 * @param args   message arguments
	 */
	public static void loginfo(String format, Object... args) {
		LOGGER.info(String.format(format, args));
	}

	/** Application singleton. */
	private static Application theApplication;

	/** Static access to application instance. */
	public static Application app() {
		if (theApplication == null) {
			throw new IllegalStateException("Application instance not yet accessible at this point");
		}
		return theApplication;
	}

	/**
	 * Launches the specified application. The command-line arguments are parsed and assigned to the
	 * implicitly created application settings.
	 * 
	 * @param appClass application class
	 * @param args     command-line arguments
	 */
	public static void launch(Class<? extends Application> appClass, String[] args) {
		launch(appClass, new AppSettings(), args);
	}

	/**
	 * Launches the specified application. The command-line arguments are parsed and assigned to the
	 * passed application settings.
	 * 
	 * @param appClass application class
	 * @param settings application settings
	 * @param args     command-line arguments
	 */
	public static void launch(Class<? extends Application> appClass, AppSettings settings, String[] args) {
		try {
			theApplication = appClass.getDeclaredConstructor().newInstance();
		} catch (Exception x) {
			throw new RuntimeException("Could not create application", x);
		}
		theApplication.settings = settings;
		theApplication.configure(theApplication.settings);
		JCommander.newBuilder().addObject(theApplication.settings).build().parse(args);
		theApplication.construct();
		theApplication.printSettings();
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warning("Could not set Nimbus Look&Feel.");
		}
		SwingUtilities.invokeLater(() -> theApplication.lifecycle.init());
	}

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	private AppSettings settings;
	private StateMachine<ApplicationState, ApplicationEvent> lifecycle;
	private KeyboardHandler appKeyHandler;
	private KeyListener internalKeyHandler;
	private MouseHandler appMouseHandler;
	private WindowListener windowHandler;
	private Clock clock;
	private CollisionHandler collisionHandler;
	private Consumer<Application> exitHandler;
	private AppShell shell;
	private Lifecycle controller;
	private Image icon;

	public void addChangeListener(PropertyChangeListener listener) {
		changes.addPropertyChangeListener(listener);
	}

	public void removeChangeListener(PropertyChangeListener listener) {
		changes.removePropertyChangeListener(listener);
	}

	public void fireChange(String changeName, Object oldValue, Object newValue) {
		changes.firePropertyChange(changeName, oldValue, newValue);
	}

	private void construct() {
		lifecycle = createLifecycle();
		internalKeyHandler = createInternalKeyHandler();
		appKeyHandler = new KeyboardHandler();
		appMouseHandler = new MouseHandler();
		windowHandler = createWindowHandler();
		clock = new Clock(settings.fps, () -> {
			lifecycle.update();
			currentView().ifPresent(shell::render);
		});
		loginfo("Application '%s' constructed.", getClass().getName());
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

	private KeyListener createInternalKeyHandler() {
		return new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_P) {
					lifecycle.process(TOGGLE_PAUSE);
				} else if (e.getKeyCode() == KeyEvent.VK_F2) {
					lifecycle.process(SHOW_SETTINGS_DIALOG);
				} else if (e.getKeyCode() == KeyEvent.VK_F11) {
					lifecycle.process(TOGGLE_FULLSCREEN);
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && shell.inFullScreenMode()) {
					lifecycle.process(TOGGLE_FULLSCREEN);
				}
			}
		};
	}

	private WindowListener createWindowHandler() {
		return new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				LOGGER.info("Application window closing, app will exit...");
				lifecycle.process(CLOSE);
			}
		};
	}

	private StateMachine<ApplicationState, ApplicationEvent> createLifecycle() {
		return StateMachine.
		/*@formatter:off*/		
		beginStateMachine(ApplicationState.class, ApplicationEvent.class, EventMatchStrategy.BY_EQUALITY)
			.description(String.format("[%s]", getClass().getName()))
			.initialState(STARTING)
			.states()
				
				.state(STARTING)
					.onEntry(() -> {
						Keyboard.handler = appKeyHandler;
						Mouse.handler = appMouseHandler;
						Mouse.handler.fnScale = () -> settings.scale;
						init();
						if (controller != null) {
							createShell(settings.width, settings.height);
						} else {
							setController(new AppInfoView(800, 600));
							createShell(800, 600);
						}
						shell.display(settings.fullScreenOnStart);
						clock.start();
						loginfo("Clock started, %d frames/second", clock.getTargetFramerate());
					})
				
				.state(RUNNING)
					.onTick(() -> {
						appKeyHandler.poll();
						appMouseHandler.poll();
						if (collisionHandler != null) {
							collisionHandler.update();
						}
						controller.update();
					})
				
				.state(PAUSED)
					.onEntry(() -> fireChange("paused", false, true))
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
			
				.when(STARTING).then(RUNNING)
				
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

	private void createShell(int w, int h) {
		shell = new AppShell(this, w, h);

		shell.addKeyListener(internalKeyHandler);
		shell.getFullScreenWindow().addKeyListener(internalKeyHandler);

		shell.addKeyListener(appKeyHandler);
		shell.getFullScreenWindow().addKeyListener(appKeyHandler);

		shell.addWindowListener(windowHandler);
		shell.getFullScreenWindow().addWindowListener(windowHandler);

		shell.getCanvas().addMouseListener(appMouseHandler);
		shell.getCanvas().addMouseMotionListener(appMouseHandler);
		shell.getFullScreenWindow().addMouseListener(appMouseHandler);
		shell.getFullScreenWindow().addMouseMotionListener(appMouseHandler);
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
	 * Hook method called after having configured the application and before the clock is driving the
	 * application.
	 */
	public abstract void init();

	public boolean isPaused() {
		return lifecycle.is(PAUSED);
	}

	public void togglePause() {
		lifecycle.process(TOGGLE_PAUSE);
	}

	public boolean inFullScreenMode() {
		return shell != null && shell.inFullScreenMode();
	}

	public CollisionHandler collisionHandler() {
		if (collisionHandler == null) {
			collisionHandler = new CollisionHandler();
		}
		return collisionHandler;
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
	 * @param controller a controller
	 * @param initialize if the controller should be initialized
	 */
	public void setController(Lifecycle controller, boolean initialize) {
		if (controller == null) {
			throw new IllegalArgumentException("Application controller must not be null.");
		}
		if (controller != this.controller) {
			this.controller = controller;
			LOGGER.info("Application controller is: " + controller);
			if (initialize) {
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

	public void setIcon(Image icon) {
		if (icon == null) {
			return;
		}
		this.icon = icon;
		if (shell != null) {
			shell.setIconImage(icon);
		}
	}

	public Image getIcon() {
		return icon;
	}
}