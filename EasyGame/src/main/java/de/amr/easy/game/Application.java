package de.amr.easy.game;

import static java.awt.event.KeyEvent.VK_P;

import java.awt.Image;
import java.awt.event.KeyEvent;
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
import de.amr.easy.game.input.Keyboard.Modifier;
import de.amr.easy.game.input.KeyboardHandler;
import de.amr.easy.game.input.MouseHandler;
import de.amr.easy.game.timing.Clock;
import de.amr.easy.game.ui.AppInfoView;
import de.amr.easy.game.ui.AppShell;
import de.amr.easy.game.view.View;
import de.amr.easy.game.view.VisualController;

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
 * 		launch(new MyFirstGame(), args);
 * 	}
 * 
 *	&#64;Override
 * 	public AppSettings createAppSettings() {
 *		AppSettings settings = new AppSettings();
 * 		settings.width = 800;
 * 		settings.height = 600;
 * 		settings.scale = 2;
 * 		settings.title = "My First Game";
 * 		return settings;
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
		NEW, INITIALIZED, RUNNING, PAUSED;
	}

	/** Application-global logger. */
	public static final Logger LOGGER = Logger.getLogger(Application.class.getName());

	static {
		InputStream stream = Application.class.getClassLoader().getResourceAsStream("logging.properties");
		if (stream == null) {
			throw new RuntimeException("Could not load logging property file");
		}
		try {
			LogManager.getLogManager().readConfiguration(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Singleton. */
	private static Application APP;

	/** Static access to application instance. */
	public static Application app() {
		if (APP == null) {
			throw new IllegalStateException("Application instance not yet accessible at this point");
		}
		return APP;
	}

	/**
	 * Launches the specified application. The command-line arguments are parsed and
	 * assigned to the application settings.
	 * 
	 * @param app  application instance
	 * @param args command-line arguments
	 */
	public static void launch(Application app, String[] args) {
		if (app == null) {
			throw new IllegalArgumentException("Cannot launch application, got NULL as application reference");
		}
		APP = app;
		JCommander.newBuilder().addObject(app.settings).build().parse(args);
		SwingUtilities.invokeLater(() -> app.startAndShowUserInterface());
	}

	private final AppSettings settings;
	private final Clock clock;
	private CollisionHandler collisionHandler;
	private Consumer<Application> exitHandler;
	private AppShell shell;
	private Lifecycle controller;
	private ApplicationState state;
	private Image icon;

	/**
	 * Initialization hook for application. Application should set main controller
	 * in this method.
	 */
	public abstract void init();

	public Application() {
		settings = createAppSettings();
		clock = new Clock(settings.fps, this::update, this::render);
		MouseHandler.INSTANCE.fnScale = () -> settings.scale;
		state = ApplicationState.NEW;
	}

	private void startAndShowUserInterface() {
		int width = settings.width, height = settings.height;
		LOGGER.info(String.format("Launching application '%s' ", getClass().getName()));
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warning("Could not set Nimbus Look&Feel.");
		}
		init();
		if (controller == null) {
			width = 800;
			height = 600;
			settings.scale = 1;
			controller = new AppInfoView(width, height);
			controller.init();
			LOGGER.warning("WARNING: Application did not specify a main controller! Using default controller.");
		}
		changeState(ApplicationState.INITIALIZED);
		shell = new AppShell(width, height);
		shell.display(settings.fullScreenOnStart);
		clock.start();
		LOGGER.info(String.format("Clock started, %d ticks/sec.", clock.getFrequency()));
		changeState(ApplicationState.RUNNING);
	}

	/**
	 * Creates the settings for this application
	 */
	protected AppSettings createAppSettings() {
		return new AppSettings();
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

	public Lifecycle getController() {
		return controller;
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

	/**
	 * Called when the application shell is closed. Stops the clock, executes the
	 * optional exit handler and terminates the VM.
	 */
	public final void exit() {
		clock.stop();
		if (exitHandler != null) {
			exitHandler.accept(this);
		}
		LOGGER.info("Application terminated.");
		System.exit(0);
	}

	public boolean isPaused() {
		return state == ApplicationState.PAUSED;
	}

	private void changeState(ApplicationState newState) {
		ApplicationState oldState = state;
		if (oldState != newState) {
			state = newState;
			LOGGER.info(String.format("Application state changes from '%s' to '%s'", oldState, newState));
		}
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
				changeState(ApplicationState.PAUSED);
				return;
			}
			if (Keyboard.keyPressedOnce(KeyEvent.VK_F2)) {
				shell.showSettingsDialog();
				return;
			}
			MouseHandler.poll();
			collisionHandler().update();
			controller.update();
			break;
		case PAUSED:
			KeyboardHandler.poll();
			if (Keyboard.keyPressedOnce(KeyEvent.VK_F2)) {
				shell.showSettingsDialog();
			}
			if (Keyboard.keyPressedOnce(Modifier.CONTROL, VK_P)) {
				changeState(ApplicationState.RUNNING);
				return;
			}
			break;
		default:
			throw new IllegalStateException();
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