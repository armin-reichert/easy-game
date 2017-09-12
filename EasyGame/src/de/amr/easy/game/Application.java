package de.amr.easy.game;

import static de.amr.easy.game.input.Keyboard.keyDown;
import static de.amr.easy.game.input.Keyboard.keyPressedOnce;
import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_P;

import java.awt.EventQueue;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
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
 * Application base class. To start an application, create an application instance, define its settings in the
 * constructor and call the {@link #launch(Application)} method.
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
	 * Starts the given application inside a window or in full-screen mode according to the settings defined after its
	 * creation.
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

	/** The views of this application. */
	private final Set<View> views;

	/** The default view of this application. */
	private final ViewController defaultView;

	/** The currently displayed view. */
	private View selectedView;

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
		views = new LinkedHashSet<>();
		defaultView = new DefaultView(this);
		selectedView = defaultView;
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
		if (!paused) {
			if (currentView() != null) {
				collisionHandler.update();
				if (currentView() instanceof Controller) {
					((Controller) currentView()).update();
				}
			} else {
				defaultView.update();
			}
		}
	}

	private void draw() {
		shell.draw(currentView());
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
	 * Returns the default view which is displayed in case no view has been created so far or no view is selected.
	 * 
	 * @return the default view
	 */
	public View getDefaultView() {
		return defaultView;
	}

	/**
	 * Adds a view to the set of views.
	 * 
	 * @param view
	 *          view to be added
	 * @return view that was added
	 */
	public <V extends View> V addView(V view) {
		if (view == null) {
			throw new IllegalArgumentException("Cannot add null view");
		}
		views.add(view);
		return view;
	}

	/**
	 * Finds a view by its class. Only one view of any class should be added.
	 * 
	 * @param viewClass
	 *          class of view to be found
	 * @return view of given class
	 */
	@SuppressWarnings("unchecked")
	public <V extends View> V findView(Class<V> viewClass) {
		for (View view : views) {
			if (viewClass.isAssignableFrom(view.getClass())) {
				return (V) view;
			}
		}
		throw new IllegalArgumentException("No view with class '" + viewClass.getName() + "' exists");
	}

	/**
	 * The current view.
	 * 
	 * @return the current view
	 */
	@SuppressWarnings("unchecked")
	public <V extends View> V currentView() {
		return (V) selectedView;
	}

	/**
	 * Selects the view of the given class as the current view.
	 * 
	 * @param viewClass
	 *          class of view to be selected
	 */
	public <V extends View> void selectView(Class<V> viewClass) {
		selectView(findView(viewClass));
	}

	/**
	 * Selects the given view and displays it.
	 * 
	 * @param view
	 *          the view to be displayed
	 */
	public void selectView(View view) {
		selectedView = (view == null) ? defaultView : view;
		if (selectedView instanceof Controller) {
			((Controller) selectedView).init(); // TODO should this be done here?
		}
		views.add(selectedView);
		LOG.info("Current view: " + selectedView);
	}

	/**
	 * Returns the currently selected view.
	 * 
	 * @return current view
	 */
	public View getSelectedView() {
		return selectedView;
	}

	/**
	 * Returns the set of views. The iteration order corresponds to the insertion order.
	 * 
	 * @return set of views
	 */
	public Set<View> views() {
		return Collections.unmodifiableSet(views);
	}
}