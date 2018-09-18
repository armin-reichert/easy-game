package de.amr.easy.game;

import static java.awt.event.KeyEvent.VK_P;

import java.awt.EventQueue;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

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
 * Application base class with utility method {@code launch(Application)} for starting an
 * application.
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * public class MyGame extends Application {
 * 
 * 	public static void main(String... args) {
 * 		launch(new MyGame());
 * 	}
 * 
 * 	public MyGame() {
 * 		settings.title = "My Game";
 * 		settings.width = 300;
 * 		settings.height = 200;
 * 		settings.scale = 2.5;
 * 		settings.fullScreenMode = FullScreen.Mode(800, 600, 32);
 * 		settings.fullScreenOnStart = false;
 * }
 * </pre>
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
	 * Launches the given application.
	 * 
	 * @param app
	 *              instance of application subclass
	 */
	public static void launch(Application app) {
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (Exception e) {
			LOGGER.warning("Could not set Nimbus Look&Feel");
		}
		EventQueue.invokeLater(() -> {
			app.shell = new AppShell(app);
			if (app.settings.fullScreenOnStart) {
				app.shell.enterFullScreenExclusiveMode();
			} else {
				app.shell.enterWindowMode();
			}
			app.start();
		});
	}

	/** The settings of this application. */
	public final AppSettings settings;

	/** The window displaying the application. */
	private AppShell shell;

	/** The clock running the application. */
	public final Clock clock;

	/** The collision handler of this application. */
	public final CollisionHandler collisionHandler;

	/** The default view of this application. */
	private final AppInfoView defaultView;

	/** The current controller. */
	private Controller controller;

	/** Tells if the application is paused (updates and collision checks are stopped). */
	private boolean paused;

	/**
	 * Base class constructor. By default, applications run at 60 frames/second.
	 */
	public Application() {
		INSTANCE = this;
		clock = new Clock(this::update, this::render);
		clock.setFrequency(60);
		settings = new AppSettings();
		defaultView = new AppInfoView(this);
		controller = defaultView;
		collisionHandler = new CollisionHandler();
		MouseHandler.INSTANCE.fnScale = () -> settings.scale;
		LOGGER.info(String.format("Application '%s' created.", getClass().getSimpleName()));
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
	public void setController(Controller controller, boolean initialize) {
		this.controller = (controller == null) ? defaultView : controller;
		LOGGER.info("Controller set: " + controller);
		if (initialize) {
			controller.init();
			LOGGER.info("Controller initialized.");
		}
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

	/** Initializes the application and starts the clock. */
	private final void start() {
		defaultView.init();
		LOGGER.info("Default view initialized.");
		init();
		LOGGER.info("Application initialized.");
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
		if (!paused) {
			collisionHandler.update();
			controller.update();
		}
	}

	private void render() {
		if (controller instanceof View) {
			shell.renderView((View) controller);
		} else if (controller instanceof ViewController) {
			ViewController vc = ((ViewController) controller);
			if (vc.currentView() != null) {
				shell.renderView(vc.currentView());
			}
		}
	}
}