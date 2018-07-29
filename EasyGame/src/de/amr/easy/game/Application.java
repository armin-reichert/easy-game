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
import de.amr.easy.game.entity.EntitySet;
import de.amr.easy.game.entity.collision.CollisionHandler;
import de.amr.easy.game.input.KeyboardHandler;
import de.amr.easy.game.input.MouseHandler;
import de.amr.easy.game.timing.Pulse;
import de.amr.easy.game.ui.ApplicationShell;
import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.DefaultView;
import de.amr.easy.game.view.View;
import de.amr.easy.game.view.ViewController;

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
	 * Starts the given application inside a window or in full-screen mode according to the settings
	 * defined after its creation.
	 * 
	 * @param app
	 *          the application
	 */
	public static void launch(Application app) {
		EventQueue.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
			} catch (Exception e) {
				LOG.warning("Could not set Nimbus Look&Feel");
			}
			app.shell = new ApplicationShell(app);
			app.shell.showApplication();
			app.start();
		});
	}

	private static final int[] PAUSE_TOGGLE_KEY = { VK_CONTROL, VK_P };

	/** A logger that may be used by any application. */
	public static final Logger LOG = Logger.getLogger(Application.class.getName());

	/** The settings of this application. */
	public final AppSettings settings;

	/** The set of entities used by this application. */
	public final EntitySet entities;

	/** The default view of this application. */
	private final DefaultView defaultView;

	/** The currently displayed view. */
	private Controller selectedController;

	/** The pulse (tact) of this application. */
	public final Pulse pulse;

	/** The collision handler of this application. */
	public final CollisionHandler collisionHandler;

	private boolean paused;

	private ApplicationShell shell;

	/**
	 * Base class constructor. By default, applications run at 60 frames/second.
	 */
	protected Application() {
		settings = new AppSettings();
		defaultView = new DefaultView(this);
		selectedController = defaultView;
		entities = new EntitySet();
		pulse = new Pulse(this::update, this::draw, 60);
		collisionHandler = new CollisionHandler();
		LOG.info("Application " + getClass().getSimpleName() + " created.");
	}

	/** Called when the application should be initialized. */
	public abstract void init();

	/** Called after initialization and starts the pulse. */
	private final void start() {
		defaultView.init();
		LOG.info("Default view initialized.");
		init();
		LOG.info("Application initialized.");
		pulse.start();
		LOG.info("Application started.");
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
		if (paused) {
			return;
		}
		if (selectedController != null) {
			collisionHandler.update();
			selectedController.update();
		} else {
			defaultView.update();
		}
	}

	private void draw() {
		if (selectedController instanceof ViewController) {
			ViewController vc = (ViewController) selectedController;
			shell.draw(vc.currentView());
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
	 * The width of this application (without scaling).
	 * 
	 * @return the width in pixels
	 */
	public int getWidth() {
		return settings.width;
	}

	/**
	 * The height of this application (without scaling).
	 * 
	 * @return the height in pixels
	 */
	public int getHeight() {
		return settings.height;
	}

	/**
	 * Tells if the application is paused.
	 * 
	 * @return if the application is paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Returns the default view which is displayed in case no view has been created so far or no view
	 * is selected.
	 * 
	 * @return the default view
	 */
	public View getDefaultView() {
		return defaultView;
	}

	/**
	 * Selects the given controller, initializes it and initializes the current view.
	 * 
	 * @param controller
	 *          a controller (for example a view controller or a scene)
	 */
	public void select(Controller controller) {
		selectedController = (controller == null) ? defaultView : controller;
		selectedController.init();
		LOG.info("Initialized current controller: " + selectedController);
	}
}