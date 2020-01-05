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
 * Application base class.
 * <p>
 * Method {@code launch(Application, String[])} starts the application's game loop and shows its user interface inside a
 * window or in fullscreen mode.
 * 
 * <p>
 * For a complete list of the supported command-line arguments / application settings, see class {@link AppSettings}.
 * <p>
 * Example:
 * 
 * <pre>
 * java -jar mygame.jar -scale 1 -title "My Game" -fullScreenOnStart -fullScreenMode 800,600,32
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
		return APP;
	}

	/**
	 * Launches the specified application. The command-line arguments are parsed and assigned to the application settings.
	 * 
	 * @param app
	 *               application instance
	 * @param args
	 *               command-line arguments
	 */
	public static void launch(Application app, String[] args) {

		if (app == null) {
			throw new IllegalArgumentException("Cannot launch application, got NULL as application reference");
		}

		LOGGER.info(String.format("Launching application '%s' ", app.getClass().getSimpleName()));

		// 1. Parse command-line
		JCommander.newBuilder().addObject(app.settings).build().parse(args);

		// Continue on the event-dispatch thread
		SwingUtilities.invokeLater(() -> {

			// 2. Set look-and-feel
			try {
				UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
			} catch (Exception e) {
				LOGGER.warning("Could not set Nimbus Look&Feel.");
				e.printStackTrace();
			}

			// 3. Call initialization hook
			app.init();
			if (app.controller == null) {
				// application controller not specified, use default controller/view
				app.controller = new AppInfoView();
				app.controller.init();
			}

			app.changeState(ApplicationState.INITIALIZED);
			LOGGER.info("Application initialized.");

			// 5. Create the shell and show the application UI inside
			app.shell = new AppShell();
			app.shell.display(app.settings.fullScreenOnStart);

			// 6. Start the clock
			app.clock.start();
			LOGGER.info(String.format("Clock started, %d ticks/sec.", app.clock.getFrequency()));

			app.changeState(ApplicationState.RUNNING);
			LOGGER.info("Application is running.");
		});
	}

	public final CollisionHandler collisionHandler;
	private Consumer<Application> exitHandler;

	private final AppSettings settings;
	private final Clock clock;
	private AppShell shell;
	private Lifecycle controller;
	private ApplicationState state;

	/**
	 * Base class constructor. By default, applications run at 60 frames/second.
	 */
	public Application() {
		APP = this;
		settings = createAppSettings();
		clock = new Clock(settings.fps, this::update, this::render);
		// TODO make this optional:
		collisionHandler = new CollisionHandler();
		MouseHandler.INSTANCE.fnScale = () -> settings.scale;
		state = ApplicationState.NEW;
	}

	/**
	 * Can be overridden by application to provide additional settings.
	 * 
	 * @return the settings object
	 */
	protected AppSettings createAppSettings() {
		return new AppSettings();
	}

	public ApplicationState getState() {
		return state;
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

	private void changeState(ApplicationState newState) {
		ApplicationState oldState = state;
		if (oldState != newState) {
			state = newState;
			LOGGER.info(String.format("Application state changes from '%s' to '%s'", oldState, newState));
		}
	}

	/** Called when the application is initialized. */
	public abstract void init();

	/**
	 * Makes the given controller the current one and optionally initializes it.
	 * 
	 * @param controller
	 *                     a controller
	 * @param initialize
	 *                     if the controller should be initialized
	 */
	public void setController(Lifecycle controller, boolean initialize) {
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
	public Lifecycle getController() {
		return controller;
	}

	/**
	 * Sets the given controller and initializes it.
	 * 
	 * @param controller
	 *                     a controller
	 */
	public void setController(Lifecycle controller) {
		setController(controller, true);
	}

	/**
	 * Sets the icon shown in the application window.
	 * 
	 * @param image
	 *                application icon
	 */
	public void setIcon(Image image) {
		if (shell != null) {
			shell.setIconImage(image);
		}
	}

	/**
	 * @return the application's icon
	 */
	public Image getIcon() {
		return shell != null ? shell.getIconImage() : null;
	}

	/**
	 * Exits the application and the Java VM.
	 */
	public final void exit() {
		clock.stop();
		if (exitHandler != null) {
			exitHandler.accept(this);
		}
		LOGGER.info("Application terminated.");
		System.exit(0);
	}

	/**
	 * Tells if the application is paused.
	 * 
	 * @return if the application is paused
	 */
	public boolean isPaused() {
		return state == ApplicationState.PAUSED;
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
			collisionHandler.update();
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
		getCurrentView().ifPresent(view -> shell.render(view));
	}

	private Optional<View> getCurrentView() {
		if (controller instanceof View) {
			return Optional.ofNullable((View) controller);
		}
		if (controller instanceof VisualController) {
			return ((VisualController) controller).currentView();
		}
		return Optional.empty();
	}

}