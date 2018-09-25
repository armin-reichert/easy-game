package de.amr.easy.game.view;

public interface AnimationController extends Controller {

	void startAnimation();

	void stopAnimation();

	boolean isAnimationCompleted();
}