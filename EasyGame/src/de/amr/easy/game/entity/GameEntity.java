package de.amr.easy.game.entity;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

import de.amr.easy.game.entity.collision.CollisionBoxSupplier;
import de.amr.easy.game.math.Vector2f;
import de.amr.easy.game.sprite.Sprite;
import de.amr.easy.game.view.ViewController;

/**
 * Base class for game entities with the lifetime methods {@link #init()}, {@link #update()} and
 * {@link #draw(Graphics2D)}.
 * 
 * Provides a {@link Transform transform} object for storing position, velocity and rotation. A game
 * entity can store a list of sprites. By overriding the {@link #currentSprite()} method, the sprite
 * used for drawing is defined.
 * <p>
 * Game entities can be stored and accessed in the entity set of an application which serves as a
 * generic container for the application's entities.
 * 
 * @author Armin Reichert
 */
public class GameEntity implements ViewController, CollisionBoxSupplier {

	private String name;
	private Sprite[] sprites;
	public BooleanSupplier visibility;
	public final Transform tf;

	public GameEntity(Sprite... sprites) {
		this.tf = new Transform();
		this.name = super.toString();
		this.sprites = sprites;
		this.visibility = () -> currentSprite() != null;
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public void draw(Graphics2D g) {
		if (visibility.getAsBoolean()) {
			Graphics2D pen = (Graphics2D) g.create();
			pen.translate(tf.getX(), tf.getY());
			pen.rotate(tf.getRotation());
			currentSprite().draw(pen);
			pen.dispose();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return currentSprite() != null ? currentSprite().getWidth() : 0;
	}

	public int getHeight() {
		return currentSprite() != null ? currentSprite().getHeight() : 0;
	}

	@Override
	public Rectangle2D getCollisionBox() {
		return new Rectangle2D.Double(tf.getX(), tf.getY(), getWidth(), getHeight());
	}

	public boolean collidesWith(GameEntity other) {
		return getCollisionBox().intersects(other.getCollisionBox());
	}

	public Sprite currentSprite() {
		return sprites.length == 0 ? null : sprites[0];
	}

	public Sprite getSprite(int i) {
		return sprites[i];
	}

	protected Stream<Sprite> getSprites() {
		return Arrays.stream(sprites);
	}

	public void setSprites(Sprite... sprites) {
		this.sprites = sprites;
	}

	public void enableAnimation(boolean animated) {
		getSprites().forEach(sprite -> sprite.setAnimationEnabled(animated));
	}

	public Vector2f getCenter() {
		return Vector2f.of(tf.getX() + getWidth() / 2, tf.getY() + getHeight() / 2);
	}

	public void hCenter(int width) {
		tf.setX((width - getWidth()) / 2);
	}

	public void vCenter(int height) {
		tf.setY((height - getHeight()) / 2);
	}

	public void center(int width, int height) {
		tf.moveTo((width - getWidth()) / 2, (height - getHeight()) / 2);
	}
}
