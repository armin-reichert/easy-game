package de.amr.easy.game.ui.sprites;

public enum AnimationType {
	/** Plays frames once from first to last. */
	LINEAR,

	/** Plays frames in a cycle. */
	CYCLIC,

	/** Plays frames repeatedly forward and backwards. */
	FORWARD_BACKWARDS;
}