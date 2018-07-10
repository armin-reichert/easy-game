package de.amr.easy.game.scene;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Optional;

import de.amr.easy.game.Application;
import de.amr.easy.game.view.Controller;
import de.amr.easy.game.view.View;

/**
 * Represents a view for a given application. This class expects a separate controller and does not handle clock events
 * by itself.
 * 
 * @author Armin Reichert
 *
 * @param <A>
 *          application type
 */
public abstract class Scene<A extends Application> implements View {

	/** The application of this view. */
	public final A app;

	private Image bgImage;

	private Color bgColor;

	/**
	 * Creates a scene for the given application with a black background.
	 * 
	 * @param app
	 *          the application of this view
	 */
	public Scene(A app) {
		this.app = app;
		bgColor = Color.BLACK;
	}

	/**
	 * @return the controller which handles the clock events
	 */
	public abstract Controller getController();

	/**
	 * @return the width in pixels
	 */
	public int getWidth() {
		return app.getWidth();
	}

	/**
	 * @return the height in pixels
	 */
	public int getHeight() {
		return app.getHeight();
	}

	/**
	 * @return the background color
	 */
	public Optional<Color> getBgColor() {
		return Optional.ofNullable(bgColor);
	}

	/**
	 * Sets the background color.
	 * 
	 * @param bgColor
	 *          some color
	 */
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * @return the background image
	 */
	public Optional<Image> getBgImage() {
		return Optional.ofNullable(bgImage);
	}

	/**
	 * Sets the background image.
	 * 
	 * @param bgImage
	 *          some image
	 */
	public void setBgImage(Image bgImage) {
		this.bgImage = bgImage;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(bgColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (bgImage != null) {
			g.drawImage(bgImage, 0, 0, null);
		}
	}
}