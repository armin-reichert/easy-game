package de.amr.easy.game.tests;

import static de.amr.easy.game.Application.app;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JLabel;

import de.amr.easy.game.Application;
import de.amr.easy.game.config.AppSettings;
import de.amr.easy.game.controller.Lifecycle;
import de.amr.easy.game.view.View;

public class CustomSettingsTabTestApp extends Application {

	public static void main(String[] args) {
		launch(CustomSettingsTabTestApp.class, args);
	}

	@Override
	protected void configure(AppSettings settings) {
		settings.title = "Test custom settings tab";
	}

	@Override
	public void init() {
		setController(new UI());
	}
}

class UI implements View, Lifecycle {

	@Override
	public void init() {
		app().f2Dialog().addCustomTab("Custom-Tab-0", new JLabel("Custom tab #0"));
		app().f2Dialog().addCustomTab("Custom-Tab-1", new JLabel("Custom tab #1"));
		app().f2Dialog().addCustomTab("Custom-Tab-2", new JLabel("Custom tab #2"));
		app().f2Dialog().selectCustomTab(1);
	}

	@Override
	public void update() {
	}

	@Override
	public void draw(Graphics2D g) {
		g.setBackground(Color.BLACK);
		g.fillRect(0, 0, app().settings().width, app().settings().height);
	}
}
