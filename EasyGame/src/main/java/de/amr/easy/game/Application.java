package de.amr.easy.game;

import static de.amr.easy.game.ApplicationImpl.ApplicationEvent.CLOSE;
import static de.amr.easy.game.ApplicationImpl.ApplicationEvent.PAUSE;
import static de.amr.easy.game.ApplicationImpl.ApplicationEvent.RESUME;
import static de.amr.easy.game.ApplicationImpl.ApplicationEvent.SHOW_SETTINGS_DIALOG;
import static de.amr.easy.game.ApplicationImpl.ApplicationState.CLOSING;
import static de.amr.easy.game.ApplicationImpl.ApplicationState.PAUSED;
import static de.amr.easy.game.ApplicationImpl.ApplicationState.RUNNING;

import java.awt.Image;
import java.util.Optional;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import de.amr.easy.game.assets.SoundManager;
import de.amr.easy.game.config.AppSettings;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.entity.collision.CollisionHandler;
import de.amr.easy.game.timing.Clock;
import de.amr.easy.game.ui.AppShell;
import de.amr.easy.game.ui.f2dialog.F2DialogAPI;
import de.amr.easy.game.view.View;

/**
 * Applications must extend this abstract class and provide a public no-args constructor. To start
 * the application, either of the static methods {@link #launch(Class, String[])} or
 * {@link #launch(Class, AppSettings, String[])} may be called. The second variant allows an
 * application to specify an extended settings object, otherwise a settings object of class
 * {@link AppSettings} is used. Command-line arguments are merged into the application settings
 * after the {@link #configure(AppSettings)} hook method has been called. For a complete list of the
 * supported command-line arguments and application settings, see class {@link AppSettings}.
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

	private static Application theApp;

	/**
	 * @return the application instance
	 */
	public static Application app() {
		return theApp;
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

	/**
	 * Creates and starts the application of the given class. The command-line arguments are parsed and
	 * assigned to the implicitly created application settings.
	 * 
	 * @param appClass application class
	 * @param cmdLine  command-line arguments
	 */
	public static void launch(Class<? extends Application> appClass, String[] cmdLine) {
		launch(appClass, new AppSettings(), cmdLine);
	}

	/**
	 * Creates and starts the application of the given class. The command-line arguments are parsed and
	 * assigned to the given application settings.
	 * 
	 * @param appClass application class
	 * @param settings application settings
	 * @param cmdLine  command-line arguments
	 */
	public static void launch(Class<? extends Application> appClass, AppSettings settings, String[] cmdLine) {
		try {
			theApp = appClass.getDeclaredConstructor().newInstance();
			theApp.impl = new ApplicationImpl(theApp, settings, cmdLine);
			theApp.impl.init();
		} catch (Exception e) {
			loginfo("Application '%s' could not be created", theApp.getName());
			e.printStackTrace(System.err);
		}
	}

	private ApplicationImpl impl;

	/**
	 * Hook method where the application settings can be configured. The command-line arguments are
	 * parsed and merged into the settings object immediately <em>after</em> this method has been called
	 * such that command-line arguments can override the settings made here.
	 * 
	 * @param settings application settings
	 */
	protected abstract void configure(AppSettings settings);

	/**
	 * Hook method getting called after the application has been configured and before the clock starts
	 * ticking.
	 */
	public abstract void init();

	/**
	 * Hook method that is called after the application shell has been created. Used to configure the F2
	 * dialog.
	 * 
	 * @param f2 the F2 dialog
	 */
	public void configureF2Dialog(F2DialogAPI f2) {
	}

	/**
	 * @return the F2 dialog if already created
	 */
	public Optional<F2DialogAPI> f2Dialog() {
		return shell().map(shell -> shell.f2Dialog);
	}

	/**
	 * Prints the application settings to the logger.
	 */
	protected void printSettings() {
		impl.settings().print();
	}

	/**
	 * @return the current application controller
	 */
	public Lifecycle getController() {
		return impl.controller();
	}

	/**
	 * Sets the given controller and calls its initializer method.
	 * 
	 * @param controller new controller
	 */
	public void setController(Lifecycle controller) {
		setController(controller, true);
	}

	/**
	 * Makes the given controller the current one and optionally initializes it.
	 * 
	 * @param controller the new application controller
	 * @param init       if the controller should be initialized
	 */
	public void setController(Lifecycle controller, boolean init) {
		impl.setController(controller, init);
	}

	/**
	 * @return the application name
	 */
	public String getName() {
		return getClass().getSimpleName();
	}

	/**
	 * @return the application settings
	 */
	public AppSettings settings() {
		return impl.settings();
	}

	/**
	 * @return the optional current view
	 */
	public Optional<View> currentView() {
		return impl.currentView();
	}

	/**
	 * @return the application clock
	 */
	public Clock clock() {
		return impl.clock();
	}

	/**
	 * @return the optional collision handler
	 */
	public Optional<CollisionHandler> collisionHandler() {
		return impl.collisionHandler();
	}

	/**
	 * If an application wants to use the built-in collision handling, it must initialize it using this
	 * method.
	 */
	public void createCollisionHandler() {
		impl.createCollisionHandler();
	}

	/**
	 * @return the application sound handler
	 */
	public SoundManager soundManager() {
		return impl.soundManager();
	}

	/**
	 * @return the application shell
	 */
	public Optional<AppShell> shell() {
		return impl.shell();
	}

	/**
	 * Opens the F2-dialog.
	 */
	public void showF2Dialog() {
		impl.process(SHOW_SETTINGS_DIALOG);
	}

	/**
	 * @return the application icon
	 */
	public Image getIcon() {
		return impl.icon();
	}

	/**
	 * Sets the icon displayed in the application window.
	 * 
	 * @param path image path in class path e.g. "/images/icon.png"
	 */
	public void setIcon(String path) {
		setIcon(new ImageIcon(getClass().getResource(path)).getImage());
	}

	/**
	 * Sets the icon displayed in the application window.
	 * 
	 * @param image image of the icon
	 */
	public void setIcon(Image image) {
		impl.setIcon(image);
	}

	/**
	 * Pauses the application.
	 */
	public void pause() {
		impl.process(PAUSE);
	}

	/**
	 * Resumes the application.
	 */
	public void resume() {
		impl.process(RESUME);
	}

	/**
	 * Toggles between the pause and running state.
	 */
	public void togglePause() {
		if (isPaused()) {
			resume();
		} else {
			pause();
		}
	}

	/**
	 * @return if the application is paused
	 */
	public boolean isPaused() {
		return impl.is(PAUSED);
	}

	/**
	 * @return if the application is running
	 */
	public boolean isRunning() {
		return impl.is(RUNNING);
	}

	/**
	 * @return if the application window is in fullscreen state
	 */
	public boolean inFullScreenMode() {
		return shell().map(AppShell::inFullScreenMode).orElse(false);
	}

	/**
	 * Toggles between fullscreen and window mode.
	 */
	public void toggleFullScreen() {
		shell().ifPresent(shell -> {
			if (shell.inFullScreenMode()) {
				shell.showWindow();
			} else {
				shell.showFullScreenWindow();
			}
		});
	}

	/**
	 * Sends a close request to the application.
	 */
	public void close() {
		impl.process(CLOSE);
	}

	/**
	 * Handler that gets called just before closing the application.
	 * 
	 * @param closeHandler
	 */
	public void onClose(Runnable closeHandler) {
		impl.addStateEntryListener(CLOSING, state -> closeHandler.run());
	}
}