package de.amr.easy.game;

import static java.awt.event.KeyEvent.VK_P;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.beust.jcommander.JCommander;

import de.amr.easy.game.config.AppSettings;
import de.amr.easy.game.entity.collision.CollisionHandler;
import de.amr.easy.game.input.Keyboard;
import de.amr.easy.game.input.Keyboard.Modifier;
import de.amr.easy.game.input.KeyboardHandler;
import de.amr.easy.game.input.MouseHandler;
import de.amr.easy.game.timing.Clock;
import de.amr.easy.game.ui.AppInfoView;
import de.amr.easy.game.ui.AppShell;
import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.View;
import de.amr.easy.game.view.ViewController;

/**
 * Application base class.
 * <p>
 * Static method {@code launch(Application, String[])} shows the application UI and starts the
 * application. Command-line arguments override the corresponding application settings. The
 * following arguments are supported:
 * <ul>
 * <li>-width <i>pixels</i>
 * <li>-height <i>pixels</i>
 * <li>-scale <i>float value</i>
 * <li>-title <i>text</i>
 * <li>-titleExtended
 * <li>-bgColor <i>rgbcolor</i>
 * <li>-fullScreenOnStart
 * <li>-fullScreenMode <i>width,height,bitdepth</i>
 * <li>-fullScreenCursor
 * </ul>
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * java -jar mygame.jar -scale 1 -title "My Awesome Game" -fullScreenOnStart -fullScreenMode 800,600,32
 * </pre>
 * 
 * The application class might look like this:
 * 
 * <pre>
 * public class MyGame extends Application {
 * 
 * 	public static void main(String... args) {
 * 		launch(new MyGame(), args);
 * 	}
 * 
 * 	public MyGame() {
 * 		settings.title = "My Game";
 * 		settings.width = 300;
 * 		settings.height = 200;
 * 		settings.scale = 2;
 * 		settings.fullScreenMode = FullScreen.Mode(640, 480, 32);
 * 		settings.fullScreenOnStart = false;
 * }
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

	public enum State {
		NEW, INITIALIZED, RUNNING, PAUSED;
	}

	/** Creates a logger with single line output and millisecond precision. */
	private static Logger createLogger() {
		InputStream stream = Application.class.getClassLoader().getResourceAsStream("logging.properties");
		if (stream == null) {
			throw new RuntimeException("Could not load logging property file");
		}
		try {
			LogManager.getLogManager().readConfiguration(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Logger.getLogger(Application.class.getName());
	}

	/** A logger that may be used by application subclasses. */
	public static final Logger LOGGER = createLogger();

	/** Static reference to the application instance. */
	private static Application INSTANCE;

	/** Static access to application instance. */
	public static Application app() {
		return INSTANCE;
	}

	/**
	 * Launches the specified application. The arguments are parsed and assigned to the application
	 * settings.
	 * 
	 * @param app
	 *               application instance
	 * @param args
	 *               command-line arguments
	 */
	public static void launch(Application app, String[] args) {
		LOGGER.info(String.format("Launching application '%s' ", app.getClass().getSimpleName()));
		JCommander.newBuilder().addObject(app.settings).build().parse(args);
		EventQueue.invokeLater(() -> {
			app.shell = new AppShell(app);
			app._init();
			app.start();
		});
	}

	/** The settings of this application. */
	public final AppSettings settings;

	/** The clock defining the speed of the application. */
	public final Clock clock;

	/** The collision handler of this application. */
	public final CollisionHandler collisionHandler;

	/** The window displaying the current view of the application. */
	private AppShell shell;

	/** The current controller. */
	private Controller controller;

	/** The application state. */
	private State state;

	/** State change listeners. */
	private final Set<BiConsumer<State, State>> stateChangeListeners = new LinkedHashSet<>();

	/** The application icon. */
	private Image icon;

	/**
	 * Base class constructor. By default, applications run at 60 frames/second.
	 */
	public Application() {
		INSTANCE = this;
		settings = new AppSettings();
		collisionHandler = new CollisionHandler();
		MouseHandler.INSTANCE.fnScale = () -> settings.scale;
		clock = new Clock(this::update, this::render);
		state = State.NEW;
	}

	public State getState() {
		return state;
	}

	public synchronized void addStateChangeListener(BiConsumer<State, State> listener) {
		stateChangeListeners.add(listener);
	}

	public synchronized void removeStateChangeListener(BiConsumer<State, State> listener) {
		stateChangeListeners.remove(listener);
	}

	private void fireStateChange(State oldState, State newState) {
		stateChangeListeners.forEach(listener -> listener.accept(oldState, newState));
	}

	private void changeState(State newState) {
		State oldState = state;
		if (oldState != newState) {
			state = newState;
			LOGGER.info(String.format("Application state changes from '%s' to '%s'", oldState, newState));
			fireStateChange(oldState, newState);
		}
	}

	/** Called when the application is initialized. */
	public abstract void init();

	private void _init() {
		controller = new AppInfoView(this);
		controller.init();
		init();
		changeState(State.INITIALIZED);
		LOGGER.info("Application initialized.");
	}

	/**
	 * Makes the given controller the current one and optionally initializes it.
	 * 
	 * @param controller
	 *                     a controller
	 * @param initialize
	 *                     if the controller should be initialized
	 */
	public void setController(Controller controller, boolean initialize) {
		if (controller == null) {
			throw new IllegalArgumentException("Controller cannot be null");
		}
		this.controller = controller;
		LOGGER.info("Controller set: " + controller);
		if (initialize) {
			controller.init();
			LOGGER.info("Controller initialized.");
		}
	}

	/**
	 * @return the current controller
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 * Sets the given controller and initializes it.
	 * 
	 * @param controller
	 *                     a controller
	 */
	public void setController(Controller controller) {
		setController(controller, true);
	}

	/**
	 * Sets the icon shown in the application window.
	 * 
	 * @param icon
	 *               application icon
	 */
	public void setIcon(Image icon) {
		this.icon = icon;
		shell.setIcon(icon);
	}

	/**
	 * @return the application's icon
	 */
	public Image getIcon() {
		return icon;
	}

	/** Starts the application. */
	private final void start() {
		clock.start();
		LOGGER.info(String.format("Clock started, running with %d ticks/sec.", clock.getFrequency()));
		changeState(State.RUNNING);
	}

	/**
	 * Exits the application and the Java VM.
	 */
	public final void exit() {
		clock.stop();
		LOGGER.info("Application terminated.");
		System.exit(0);
	}

	/**
	 * Tells if the application is paused.
	 * 
	 * @return if the application is paused
	 */
	public boolean isPaused() {
		return state == State.PAUSED;
	}

	private void update() {
		if (Keyboard.keyPressedOnce(KeyEvent.VK_F11)) {
			shell.toggleDisplayMode();
		}
		switch (state) {
		case NEW:
			throw new IllegalStateException("Application not initialized");
		case INITIALIZED:
		case RUNNING:
			KeyboardHandler.poll();
			if (Keyboard.keyPressedOnce(Modifier.CONTROL, VK_P)) {
				changeState(State.PAUSED);
				return;
			}
			if (Keyboard.keyPressedOnce(KeyEvent.VK_F2)) {
				shell.showSettingsDialog();
				return;
			}
			MouseHandler.poll();
			collisionHandler.update();
			controller.update();
			break;
		case PAUSED:
			KeyboardHandler.poll();
			if (Keyboard.keyPressedOnce(KeyEvent.VK_F2)) {
				shell.showSettingsDialog();
			}
			if (Keyboard.keyPressedOnce(Modifier.CONTROL, VK_P)) {
				changeState(State.RUNNING);
				return;
			}
			break;
		default:
			throw new IllegalStateException();
		}
	}

	private void render() {
		getCurrentView().ifPresent(view -> shell.render(view));
	}

	private Optional<View> getCurrentView() {
		if (controller instanceof View) {
			return Optional.ofNullable((View) controller);
		}
		if (controller instanceof ViewController) {
			return Optional.ofNullable(((ViewController) controller).currentView());
		}
		return Optional.empty();
	}
}