# easy-game

A simple Java2D/Swing based library for programming Java 2D games and other applications using active rendering. Makes it easy to create and launch a game application, provides integrated window-mode/full-screen switching, polling for keyboard and mouse events, standard hooks for initializing, updating and rendering, game entities with an associated transform object, support for sprites with different animation types, asset management (images, fonts, sounds), some common widgets etc. The animation frequency ("frame rate") can be changed interactively using a dialog window. This allows to run each application at an arbitray frame rate and to inspect it at runtime in slow motion.

## Common functionality

- CTRL+P pauses/resumes the game
- F2 opens a dialog where the game clock frequency and (full-)screen resolution can be changed
- F11 toggles between window and full-screen exclusive mode
 
## Command-line arguments:

The following command-line arguments are available for every application:

  - Framerate (default=60): `-fps 30`
  - Scaling (default=1): `-scale 2.5`
  - Full-screen mode on start (default=off): `-fullScreenOnStart`
  - Full-screen resolution & depth (default=maximal available resolution): `-fullScreenMode 800,600,32`
  - Window title (default="My Application!"): `-title "Pac-Man Game"`
  - Window title is additionally showing framerate and resolution (default=off): `-titleExtended`
  - Window width (default=640): `-width 1024`
  - Window height (default=480): `-height 768`
  - Rendering with image smoothing (default=off); `-smoothRendering`

## A minimal application

A minimal application is created by the following code:

```java
public class MyFirstApp extends Application {

	public static void main(String... args) {
		launch(MyFirstApp.class, args);
	}

	@Override
	public void configure(AppSettings settings) {
		settings.width = 800;
		settings.height = 600;
		settings.title = "My First Application";
	}

	@Override
	public void init() {
		setController(new MyFirstAppController());
}
 
class MyFirstAppController implements Lifecycle {
 
	@Override
	public void init() {
	}

	@Override
	// gets called at every tick of the application clock, normally 60 times/sec
	public void update() {
	}
}
```

## Application lifecycle

The lifecycle of any application is defined by the following finite-state machine:

```java
StateMachine.beginStateMachine(ApplicationState.class, ApplicationEvent.class, EventMatchStrategy.BY_EQUALITY)
	.description(String.format("[%s]", getClass().getName()))
	.initialState(STARTING)
	.states()

		.state(STARTING)
			.onEntry(() -> {
				// let application initialize itself and select a main controller:
				init();
				if (controller == null) {
					// use fallback controller
					int width = 640, height = 480;
					setController(new AppInfoView(this, width, height));
					shell = new AppShell(this, width, height);
				} else {
					shell = new AppShell(this, settings.width, settings.height);
				}
				loginfo("Starting application '%s'", getClass().getName());
				SwingUtilities.invokeLater(this::showUIAndStartClock);
			})

		.state(RUNNING)
			.onTick(() -> {
				Keyboard.handler.poll();
				Mouse.handler.poll();
				collisionHandler().ifPresent(CollisionHandler::update);
				controller.update();
				currentView().ifPresent(shell::render);
			})

		.state(PAUSED)
			.onTick(() -> currentView().ifPresent(shell::render))

		.state(CLOSED)
			.onTick(() -> {
				shell.dispose();
				loginfo("Exit application '%s'", getClass().getName());
				System.exit(0);
			})

	.transitions()

		.when(STARTING).then(RUNNING).condition(() -> clock.isTicking())

		.when(RUNNING).then(PAUSED).on(TOGGLE_PAUSE).act(SoundSupport::muteAll)

		.when(RUNNING).then(CLOSED).on(CLOSE)

		.stay(RUNNING).on(TOGGLE_FULLSCREEN).act(() -> shell.toggleDisplayMode())

		.stay(RUNNING).on(SHOW_SETTINGS_DIALOG).act(() -> shell.showSettingsDialog())

		.when(PAUSED).then(RUNNING).on(TOGGLE_PAUSE).act(SoundSupport::unmuteAll)

		.when(PAUSED).then(CLOSED).on(CLOSE)

		.stay(PAUSED).on(TOGGLE_FULLSCREEN).act(() -> shell.toggleDisplayMode())

		.stay(PAUSED).on(SHOW_SETTINGS_DIALOG).act(() -> shell.showSettingsDialog())

.endStateMachine();
```

## Integrated control dialog

By pressing the F2-key an integrated dialog is opened where you can interactively change the frequency of the game clock, watch the frame rate history and change the resolution of the full-screen mode.

<img src="EasyGame/screenshots/appcontrol.png">

## Examples

- [Pong](https://github.com/armin-reichert/pong)

<img src="EasyGame/screenshots/pong.png">

- [Flappy Bird](https://github.com/armin-reichert/birdy)

<img src="EasyGame/screenshots/flappybird.png">

- [PacMan](https://github.com/armin-reichert/pacman)

<img src="EasyGame/screenshots/pacman.png">

- [Nine-mens morris](https://github.com/armin-reichert/nine-mens-morris)

<img src="EasyGame/screenshots/nine-mens-morris.png">

- [Game of life](https://github.com/armin-reichert/game-of-life)

<img src="EasyGame/screenshots/game-of-life.png">


