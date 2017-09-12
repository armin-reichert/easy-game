package de.amr.easy.game.scene;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import de.amr.easy.game.Application;
import de.amr.easy.game.view.View;

/**
 * A convenience class for passive scenes.
 * 
 * @author Armin Reichert
 *
 * @param <A>
 *          class of application
 */
public class PassiveScene<A extends Application> implements View {

	public final A app;
	private Image bgImage;
	private Color bgColor;

	/**
	 * Creates a scene for the given application with a black background.
	 * 
	 * @param app
	 *          an application
	 */
	public PassiveScene(A app) {
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
	public void draw(Graphics2D g) {
		g.setColor(bgColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (bgImage != null) {
			g.drawImage(bgImage, 0, 0, null);
		}
	}
}