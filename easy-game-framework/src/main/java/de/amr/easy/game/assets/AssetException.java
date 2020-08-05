package de.amr.easy.game.assets;

/**
 * Thrown when any access to some asset (image, sound etc.) fails.
 * 
 * @author Armin Reichert
 */
public class AssetException extends RuntimeException {

	public AssetException(String message) {
		super(message);
	}

	public AssetException(String message, Throwable cause) {
		super(message, cause);
	}
}