package de.amr.easy.game.entity;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

import de.amr.easy.game.entity.collision.CollisionSensitive;
import de.amr.easy.game.math.Vector2f;
import de.amr.easy.game.sprite.Sprite;
import de.amr.easy.game.view.View;
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
public abstract class GameEntity implements ViewController, CollisionSensitive {

	public BooleanSupplier visibility;
	public final Transform tf;

	public GameEntity() {
		this.tf = new Transform();
		this.visibility = () -> currentSprite() != null;
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public View currentView() {
		return this;
	}

	@Override
	public int getWidth() {
		return currentSprite().getWidth();
	}

	@Override
	public int getHeight() {
		return currentSprite().getHeight();
	}

	@Override
	public void draw(Graphics2D g) {
		if (isVisible() && currentSprite() != null) {
			Graphics2D pen = (Graphics2D) g.create();
			pen.translate(tf.getX(), tf.getY());
			pen.rotate(tf.getRotation());
			currentSprite().draw(pen);
			pen.dispose();
		}
	}

	public abstract Sprite currentSprite();

	public abstract Stream<Sprite> getSprites();

	public void enableAnimation(boolean animated) {
		getSprites().forEach(sprite -> sprite.enableAnimation(animated));
	}

	public boolean isVisible() {
		return visibility.getAsBoolean();
	}

	@Override
	public Rectangle2D getCollisionBox() {
		return new Rectangle2D.Double(tf.getX(), tf.getY(), getWidth(), getHeight());
	}

	public boolean collidesWith(GameEntity other) {
		return getCollisionBox().intersects(other.getCollisionBox());
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