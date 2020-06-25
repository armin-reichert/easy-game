package de.amr.easy.game.ui.f2dialog.util;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class PercentRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		float floatValue = (float) value;
		setText(DecimalFormat.getPercentInstance(Locale.ENGLISH).format(floatValue));
		return this;
	}
}
