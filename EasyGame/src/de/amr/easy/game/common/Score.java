package de.amr.easy.game.common;

import java.util.function.Predicate;

public class Score {

	private Predicate<Integer> winCondition;
	public int points;
	
	public Score() {
		this(always -> true);
	}

	public Score(Predicate<Integer> winCondition) {
		this.winCondition = winCondition;
	}

	public void reset() {
		points = 0;
	}

	public boolean isWinning() {
		return winCondition.test(points);
	}
}
