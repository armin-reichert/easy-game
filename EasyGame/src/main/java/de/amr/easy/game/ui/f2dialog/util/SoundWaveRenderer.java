package de.amr.easy.game.ui.f2dialog.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class SoundWaveRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

	private final ImageIcon waveIcon;
	private final JLabel label;

	public SoundWaveRenderer(int height) {
		waveIcon = new ImageIcon(getClass().getResource("/icons/soundwave.png"));
		resizeIcon(waveIcon, height);
		label = new JLabel();
	}

	private void resizeIcon(ImageIcon icon, int height) {
		int scaledWidth = height * icon.getIconWidth() / icon.getIconHeight();
		BufferedImage img = new BufferedImage(scaledWidth, height, BufferedImage.TYPE_INT_ARGB);
		img.getGraphics().drawImage(icon.getImage(), 0, 0, scaledWidth, height, null);
		icon.setImage(img);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		boolean running = (boolean) value;
		label.setOpaque(true);
		label.setBackground(Color.WHITE);
		label.setIcon(running ? waveIcon : null);
		return label;
	}
}