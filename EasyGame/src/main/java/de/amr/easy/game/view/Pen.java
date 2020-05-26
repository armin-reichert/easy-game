package de.amr.easy.game.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Helper class for drawing texts.
 * 
 * @author Armin Reichert
 */
public class Pen implements AutoCloseable {

	private final Graphics2D g;

	public Pen(Graphics2D g2) {
		g = (Graphics2D) g2.create();
		g.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
		g.setColor(Color.BLUE);
	}

	@Override
	public void close() {
		g.dispose();
	}

	public void color(Color c) {
		g.setColor(c);
	}

	public void font(Font f) {
		g.setFont(f);
	}

	public FontMetrics getFontMetrics() {
		return g.getFontMetrics();
	}

	public void fontSize(float size) {
		g.setFont(g.getFont().deriveFont(size));
	}

	public void smooth(Runnable ops) {
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		ops.run();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}

	public void up(int pixels) {
		g.translate(0, -pixels);
	}

	public void down(int pixels) {
		g.translate(0, pixels);
	}

	public void drawAtGridPosition(String s, int col, int row, int cellSize) {
		FontMetrics fm = g.getFontMetrics();
		g.drawString(s, col * cellSize, row * cellSize + fm.getAscent());
	}

	public void drawString(String s, float x, float y) {
		g.drawString(s, x, y);
	}

	public void hcenter(String s, int containerWidth, int row, int cellSize) {
		float x = (containerWidth - getFontMetrics().stringWidth(s)) / 2, y = row * cellSize;
		g.drawString(s, x, y);
	}
}