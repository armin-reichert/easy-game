package de.amr.easy.game.controls;

import de.amr.easy.game.view.Controller;

public interface AnimationController extends Controller {

	void start();

	void stop();

	boolean isCompleted();
}