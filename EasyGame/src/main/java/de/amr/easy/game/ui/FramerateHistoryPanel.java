package de.amr.easy.game.ui;

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
				view.setSize(getWidth(), getHeight());
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (view != null) {
			view.draw((Graphics2D) g.create());
		}
	}
}