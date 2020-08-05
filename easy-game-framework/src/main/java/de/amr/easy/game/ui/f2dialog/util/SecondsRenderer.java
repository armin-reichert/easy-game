package de.amr.easy.game.ui.f2dialog.util;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class SecondsRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		float duration = (float) value;
		label.setText(String.format("%.2f sec", duration));
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		return this;
	}
}