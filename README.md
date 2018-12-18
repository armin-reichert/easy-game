# easy-game

A home-grown library for programming Java 2D games and other applications using active rendering. Makes it easy to create and launch a game application, provides integrated window-mode/full-screen switching, polling for keyboard and mouse events, standard hooks for initializing, updating and rendering, game entities with an associated transform object, support for (animated) sprites, asset management, some common widgets etc.

At runtime, the animation frequency ("frame rate") can be changed using a dialog. This allows for example to run an application in slow motion.

Example applications:
- [PacMan](https://github.com/armin-reichert/pacman)
- [Bird](https://github.com/armin-reichert/birdy)
- [Nine-mens morris](https://github.com/armin-reichert/nine-mens-morris)
- [Pong](https://github.com/armin-reichert/pong)

A minimal (empty) application can be created by the following code:

```java
public class EmptyApp extends Application {

	public static void main(String[] args) {
		launch(new EmptyApp(), args);
	}

	@Override
	public void init() {
	}
}
```
