package de.amr.easy.game.scene;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import de.amr.easy.game.Application;
import de.amr.easy.game.entity.GameEntity;
import de.amr.easy.game.view.View;

/**
 * Base class for scenes of an application.
 * 
 * @author Armin Reichert
 *
 * @param <A>
 *          type of application
 */
public abstract class Scene<A extends Application> implements View {

	public final A app;
	private Image bgImage;
	private Color bgColor;

	/**
	 * Creates a scene for the given app with a black background.
	 * 
	 * @param app
	 *          an application
	 */
	public Scene(A app) {
		this.app = app;
		bgColor = Color.BLACK;
	}

	public int getWidth() {
		return app.getWidth();
	}

	public int getHeight() {
		return app.getHeight();
	}

	public Color getBgColor() {
		return bgColor;
	}

	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	public Image getBgImage() {
		return bgImage;
	}

	public void setBgImage(Image bgImage) {
		this.bgImage = bgImage;
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		app.entities.all().forEach(GameEntity::update);
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(bgColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (bgImage != null) {
			g.drawImage(bgImage, 0, 0, null);
		}
		app.entities.all().forEach(e -> e.draw(g));
	}
}