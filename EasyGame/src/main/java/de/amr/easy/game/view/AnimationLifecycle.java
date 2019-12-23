package de.amr.easy.game.view;

/**
 * Lifecycle interface for animations.
 * 
 * @author Armin Reichert
 */
public interface AnimationLifecycle extends Lifecycle {

	void start();

	void stop();

	boolean complete();
}