package de.amr.easy.game.ui;

import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Drop-down list for selecting the display mode used in fullscreen-exclusive mode.
 * 
 * @author Armin Reichert
 */
public class DisplayModeSelector extends JComboBox<DisplayMode> {

	public DisplayModeSelector() {
		DisplayMode[] modes = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayModes();
		Vector<DisplayMode> modesWithoutDuplicates = new Vector<>();
		boolean duplicate = false;
		for (int i = 0; i < modes.length; ++i) {
			if (i > 0) {
				duplicate = modes[i - 1].getWidth() == modes[i].getWidth() && modes[i - 1].getHeight() == modes[i].getHeight()
						&& modes[i - 1].getBitDepth() == modes[i].getBitDepth();
			}
			if (!duplicate) {
				modesWithoutDuplicates.add(modes[i]);
			}
		}
		setModel(new DefaultComboBoxModel<>(modesWithoutDuplicates));
		setRenderer(new ItemRenderer());
	}

	public void select(DisplayMode displayMode) {
		if (displayMode != null) {
			for (int i = 0; i < getItemCount(); ++i) {
				DisplayMode mode = getItemAt(i);
				if (mode.getWidth() == displayMode.getWidth() && mode.getHeight() == displayMode.getHeight()
						&& mode.getBitDepth() == displayMode.getBitDepth()) {
					setSelectedIndex(i);
					break;
				}
			}
		}
	}

	private static class ItemRenderer extends JLabel implements ListCellRenderer<DisplayMode> {

		@Override
		public Component getListCellRendererComponent(JList<? extends DisplayMode> list, DisplayMode mode, int index,
				boolean isSelected, boolean cellHasFocus) {
			String text = "No entries";
			if (mode != null) { // inside Window Builder, mode can be NULL
				text = String.format("%d x %d Pixel, %d Bit, %s Hz", mode.getWidth(), mode.getHeight(), mode.getBitDepth(),
						mode.getRefreshRate() == 0 ? "unknown" : String.valueOf(mode.getRefreshRate()));
			}
			setText(text);
			return this;
		}
	}
}