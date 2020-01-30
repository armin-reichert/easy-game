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
import de.amr.statemachine.core.EventMatchStrategy;
import de.amr.statemachine.core.StateMachine;

/**
 * Applications inherit from this class. To start an application, use the static
 * method {@code launch(Application, String[])}. For a complete list of the
 * supported command-line arguments / application settings, see class
 * {@link AppSettings}.
 * <p>
 * Example:
 * 
 * <pre>
 * java -jar game.jar -scale 1.5 -title "My Very First Game" -fullScreenOnStart
 * 
 * public class MyFirstGame extends Application {
 * 
 * 	public static void main(String... args) {
 * 		launch(MyFirstGame.class, args);
 * 	}
 * 
 *	&#64;Override
 * 	public void configure(AppSettings settings) {
 * 		settings.width = 800;
 * 		settings.height = 600;
 * 		settings.scale = 2;
 * 		settings.title = "My First Game";
 * 	}
 * 
 *	&#64;Override
 *	public void init() {
 *		setController(new MyFirstGameController());
 *	}
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
	 * Launches the specified application. The command-line arguments are parsed and
	 * assigned to the passed application settings.
	 * 
	 * @param appClass application class
	 * @param settings application settings
	 * @param args     command-line arguments
	 */
	public static void launch(Class<? extends Application> appClass, AppSettings settings, String[] args) {
		try {
			theApplication = appClass.newInstance();
		} catch (InstantiationException | IllegalAccessException x) {
			throw new RuntimeException("Could not create application", x);
		}
		theApplication.settings = settings;
		theApplication.configure(theApplication.settings);
		JCommander.newBuilder().addObject(theApplication.settings).build().parse(args);
		theApplication.construct();
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warning("Could not set Nimbus Look&Feel.");
		}
		SwingUtilities.invokeLater(() -> theApplication.lifecycle.init());
	}

	/**
	 * Launches the specified application. The command-line arguments are parsed and
	 * assigned to the implicitly created application settings.
	 * 
	 * @param appClass application class
	 * @param args     command-line arguments
	 */
	public static void launch(Class<? extends Application> appClass, String[] args) {
		launch(appClass, new AppSettings(), args);
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

	private void construct() {
		lifecycle = createLifecycle();
		internalKeyHandler = createInternalKeyHandler();
		appKeyHandler = new KeyboardHandler();
		appMouseHandler = new MouseHandler();
		windowHandler = createWindowHandler();
		clock = new Clock(settings.fps, lifecycle::update, this::render);
	}

	protected abstract void configure(AppSettings settings);

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
						init();
						Keyboard.handler = appKeyHandler;
						Mouse.handler = appMouseHandler;
						Mouse.handler.fnScale = () -> settings.scale;
						if (controller != null) {
							createShell(settings.width, settings.height);
						} else {
							setController(new AppInfoView(800, 600));
							createShell(800, 600);
						}
						shell.display(settings.fullScreenOnStart);
						clock.setFrequency(settings.fps);
						clock.start();
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
	 * Initialization hook for application. Application should set main controller
	 * in this method.
	 */
	public abstract void init();

	public boolean isPaused() {
		return lifecycle.is(PAUSED);
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

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image image) {
		this.icon = Objects.requireNonNull(image);
		if (shell != null) {
			shell.setIconImage(image);
		}
	}

	private void render() {
		currentView().ifPresent(shell::render);
	}

	private Optional<View> currentView() {
		if (controller instanceof View) {
			return Optional.ofNullable((View) controller);
		}
		if (controller instanceof VisualController) {
			return ((VisualController) controller).currentView();
		}
		return Optional.empty();
	}
}