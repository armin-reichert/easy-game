package de.amr.easy.game.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;

/**
 * Integrates a framerate history view into a Swing component.
 * 
 * @author Armin Reichert
 */
public class FramerateHistoryPanel extends JComponent {

	private final FramerateHistoryView view;

	public FramerateHistoryPanel(FramerateHistoryView view) {
		this.view = view;
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				view.setSize(getWidth() - 10, getHeight() - 20);
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (view != null) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.translate(5, 10);
			view.draw(g2);
			g2.dispose();
		}
	}
}