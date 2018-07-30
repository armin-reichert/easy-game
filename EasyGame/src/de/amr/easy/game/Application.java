package de.amr.easy.game;

import static de.amr.easy.game.input.Keyboard.keyDown;
import static de.amr.easy.game.input.Keyboard.keyPressedOnce;
import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_P;

import java.awt.EventQueue;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.easy.game.config.AppSettings;
import de.amr.easy.game.controls.ApplicationInfo;
import de.amr.easy.game.entity.EntityMap;
import de.amr.easy.game.entity.collision.CollisionHandler;
import de.amr.easy.game.input.KeyboardHandler;
import de.amr.easy.game.input.MouseHandler;
import de.amr.easy.game.timing.Pulse;
import de.amr.easy.game.ui.ApplicationShell;
import de.amr.easy.game.view.Controller;

/**
 * Application base class. To start an application, create an application instance, define its
 * settings in the constructor and call the {@link #launch(Application)} method.
 * <p>
 * Example:
 * <p>
 * 
 * <pre>
 * public class MyApplication extends Application {
 * 
 * 	public static void main(String... args) {
 * 		launch(new MyApplication());
 * 	}
 * 
 * 	public MyApplication() {
 * 		settings.title = "My Application";
 * 		settings.width = 800;
 * 		settings.height = 600;
 * 	}
 * }
 * </pre>
 * 
 * @author Armin Reichert
 */
public abstract class Application {

	/**
	 * Launches the given application.
	 * 
	 * @param app
	 *          the application
	 */
	public static void launch(Application app) {
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (Exception e) {
			LOG.warning("Could not set Nimbus Look&Feel");
		}
		EventQueue.invokeLater(() -> {
			app.shell = new ApplicationShell(app);
			app.shell.showApplication();
			app.start();
		});
	}

	private static final int[] PAUSE_TOGGLE_KEY = { VK_CONTROL, VK_P };

	/** A logger that may be used by application subclasses. */
	public static Logger LOG;

	/** The settings of this application. */
	public final AppSettings settings;

	/** The entities of this application. */
	public final EntityMap entities;

	/** The default view of this application. */
	private final ApplicationInfo defaultView;

	/** The current application controller. */
	private Controller controller;

	/** The pulse of this application. */
	public final Pulse pulse;

	/** The collision handler of this application. */
	public final CollisionHandler collisionHandler;

	private boolean paused;
	private ApplicationShell shell;

	/**
	 * Base class constructor. By default, applications run at 60 frames/second.
	 */
	public Application() {
		LOG = Logger.getLogger(getClass().getName());
		settings = new AppSettings();
		entities = new EntityMap();
		defaultView = new ApplicationInfo(this);
		controller = defaultView;
		pulse = new Pulse(this::update, this::renderCurrentView, 60);
		collisionHandler = new CollisionHandler();
		LOG.info("Application " + getClass().getSimpleName() + " created.");
	}

	/** Called when the application is initialized. */
	public abstract void init();

	/**
	 * Sets the given controller and optionally initializes it.
	 * 
	 * @param controller
	 *          a controller
	 * @param initialize
	 *          if the controller should be initialized
	 */
	public void setController(Controller controller, boolean initialize) {
		this.controller = (controller == null) ? defaultView : controller;
		LOG.info("Set controller to: " + controller);
		if (initialize) {
			controller.init();
			LOG.info("Initialized controller: " + controller);
		}
	}

	/**
	 * Sets the given controller and initializes it.
	 * 
	 * @param controller
	 *          a controller
	 */
	public void setController(Controller controller) {
		setController(controller, true);
	}

	/** Called after initialization and starts the pulse. */
	private final void start() {
		defaultView.init();
		LOG.info("Default view initialized.");
		init();
		LOG.info("Application initialized.");
		pulse.start();
		LOG.info("Pulse started.");
	}

	private final void pause(boolean state) {
		paused = state;
		LOG.info("Application" + (state ? " paused." : " resumed."));
	}

	/**
	 * Exits the application and the Java VM.
	 */
	public final void exit() {
		pulse.stop();
		LOG.info("Application terminated.");
		System.exit(0);
	}

	private void update() {
		KeyboardHandler.poll();
		MouseHandler.poll();
		if (keyDown(PAUSE_TOGGLE_KEY[0]) && keyPressedOnce(PAUSE_TOGGLE_KEY[1])) {
			pause(!paused);
		}
		if (!paused) {
			collisionHandler.update();
			controller.update();
		}
	}

	private void renderCurrentView() {
		if (controller.currentView() != null) {
			shell.renderView(controller.currentView());
		}
	}

	/**
	 * Returns the application shell.
	 * 
	 * @return the application shell
	 */
	public ApplicationShell getShell() {
		return shell;
	}

	/**
	 * Tells if the application is paused.
	 * 
	 * @return if the application is paused
	 */
	public boolean isPaused() {
		return paused;
	}
}