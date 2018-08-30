package de.amr.easy.game.controls;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import de.amr.easy.game.Application;
import de.amr.easy.game.entity.GameEntity;
import de.amr.easy.game.math.Vector2f;
import de.amr.easy.game.view.View;

public class FlashText extends GameEntity implements View {

	private static final Font DEFAULT_FONT = new Font(Font.DIALOG, Font.BOLD, 12);

	public static void show(Application app, String text, Font font, Color color, int displayTime,
			Vector2f position, Vector2f velocity) {
		FlashText flash = new FlashText(app);
		flash.setText(text);
		flash.setFont(font);
		flash.setColor(color);
		flash.setDisplayTime(displayTime);
		flash.tf.moveTo(position);
		flash.tf.setVelocity(velocity);
	}

	private Application app;
	private int timer;
	private String text;
	private Font font;
	private Color color;

	public FlashText(Application app) {
		this.app = app;
		app.entities.store(this);
		timer = 60;
		text = "";
		font = DEFAULT_FONT;
		color = Color.BLACK;
		tf.setVelocity(0, 0);
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		if (timer == 0) {
			app.entities.removeEntity(this);
		} else {
			tf.move();
			--timer;
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, tf.getX(), tf.getY());
	}

	public void setDisplayTime(int displayTime) {
		timer = displayTime;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
