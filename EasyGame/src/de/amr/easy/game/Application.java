package de.amr.easy.game;

import static java.awt.event.KeyEvent.VK_P;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

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

	/** A logger that may be used by application subclasses. */
	public static final Logger LOGGER = Logger.getLogger(Application.class.getName());

	static {
		// configuration with single line output and millisecond precision
		InputStream stream = Application.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		JCommander.newBuilder().addObject(app.settings).build().parse(args);
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (Exception e) {
			LOGGER.warning("Could not set Nimbus Look&Feel");
		}
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

	/** Tells if the application is paused (updates and collision checks are stopped). */
	private boolean paused;

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
		LOGGER.info(String.format("Application '%s' created.", getClass().getSimpleName()));
	}

	/** Called when the application is initialized. */
	public abstract void init();

	private void _init() {
		controller = new AppInfoView(this);
		controller.init();
		init();
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
	 * Sets the icon used by the application shell.
	 * 
	 * @param icon
	 *               application icon
	 */
	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public Image getIcon() {
		return icon;
	}

	/** Sarts the application. */
	private final void start() {
		clock.start();
		LOGGER.info(String.format("Clock started, running with %d ticks/sec.", clock.getFrequency()));
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
		return paused;
	}

	private void pause(boolean state) {
		paused = state;
		LOGGER.info("Application" + (state ? " paused." : " resumed."));
	}

	private void update() {
		KeyboardHandler.poll();
		MouseHandler.poll();
		if (Keyboard.keyPressedOnce(Modifier.CONTROL, VK_P)) {
			pause(!paused);
		}
		if (Keyboard.keyPressedOnce(KeyEvent.VK_F11)) {
			shell.toggleDisplayMode();
		}
		if (Keyboard.keyPressedOnce(KeyEvent.VK_F2)) {
			shell.showSettingsDialog();
		}
		if (!paused) {
			collisionHandler.update();
			controller.update();
		}
	}

	private void render() {
		View view = getCurrentView();
		if (view != null) {
			shell.render(view);
		}
	}

	public View getCurrentView() {
		if (controller instanceof View) {
			return (View) controller;
		}
		if (controller instanceof ViewController) {
			return ((ViewController) controller).currentView();
		}
		return null;
	}
}