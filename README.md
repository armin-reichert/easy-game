# easy-game

A Java2D/Swing based library for programming Java 2D games and other applications using active rendering. Makes it easy to create and launch a game application, provides integrated window-mode/full-screen switching, polling for keyboard and mouse events, standard hooks for initializing, updating and rendering, game entities with an associated transform object, support for sprites with different animation types, asset management (images, fonts, sounds), some common widgets etc. The animation frequency ("frame rate") can be changed interactively using a dialog window. This allows to run each application at an arbitray frame rate and to inspect it at runtime in slow motion.

Example applications:
- [Pong](https://github.com/armin-reichert/pong)
- [Flappy Bird](https://github.com/armin-reichert/birdy)
- [PacMan](https://github.com/armin-reichert/pacman)
- [Nine-mens morris](https://github.com/armin-reichert/nine-mens-morris)
- [Game of life](https://github.com/armin-reichert/school/GameOfLife)

A minimal (empty) application is created by the following code:

```java
public class EmptyApp extends Application {

	public static void main(String[] args) {
		launch(EmptyApp.class, args);
	}

	@Override
	protected void configure(AppSettings settings) {
		settings.title = "Empty App";
	}

	@Override
	public void init() {
	}
}
```
