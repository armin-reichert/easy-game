package de.amr.easy.game.controller;

import java.awt.Color;
import java.awt.Graphics2D;

import de.amr.easy.game.entity.Entity;
import de.amr.easy.game.view.View;

/**
 * A "all-in-one" (entity, view, life-cycle) game object.
 * 
 * @author Armin Reichert
 */
public abstract class GameObject extends Entity implements Lifecycle, View {

	@Override
	public void init() {
	};

	@Override
	public void update() {
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
		g.drawRect((int) tf.x, (int) tf.y, tf.width, tf.height);
	}
}
