package de.amr.easy.game.assets;

public interface Sound {

	public void play();

	public void stop();

	public void loop();

	public boolean isRunning();

	public void volume(float v);
}
