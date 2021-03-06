package de.amr.easy.game.view;

import static de.amr.easy.game.Application.app;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

/**
 * Helper class for drawing texts.
 * 
 * @author Armin Reichert
 */
public class Pen implements AutoCloseable {

	private final Graphics2D g;
	private int cellSize = 8;

	public Pen(Graphics2D g2) {
		g = (Graphics2D) g2.create();
		g.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
		g.setColor(Color.BLUE);
		if (app().settings().smoothRendering) {
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
	}

	@Override
	public void close() {
		g.dispose();
	}
	
	public void move(double x, double y) {
		g.translate(x, y);
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

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}

	public void drawAtGridPosition(String s, int col, int row) {
		int y = row * cellSize;
		g.drawString(s, col * cellSize, y);
	}

	public void draw(String s, float x, float y) {
		g.drawString(s, x, y);
	}

	public void drawCentered(String s, float x, float y) {
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D box = fm.getStringBounds(s, g);
		g.drawString(s, (float) (x - box.getWidth() / 2), y - fm.getAscent()); // TODO not sure
	}

	public void hcenter(String s, int containerWidth) {
		float x = (containerWidth - getFontMetrics().stringWidth(s)) / 2;
		g.drawString(s, x, 0);
	}

	public void turnSmoothRenderingOn() {
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	public void turnSmoothRenderingOff() {
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
}