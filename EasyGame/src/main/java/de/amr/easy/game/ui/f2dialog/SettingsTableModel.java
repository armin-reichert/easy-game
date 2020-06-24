package de.amr.easy.game.ui.f2dialog;

import static de.amr.easy.game.Application.app;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.amr.easy.game.config.AppSettings;

public class SettingsTableModel extends AbstractTableModel {

	private List<String> keys = new ArrayList<>();
	private List<String> values = new ArrayList<>();

	@Override
	public int getRowCount() {
		return keys.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int col) {
		return col == 0 ? "Name" : "Value";
	}

	@Override
	public Object getValueAt(int row, int col) {
		return col == 0 ? keys.get(row) : values.get(row);
	}

	public void update() {
		keys.clear();
		values.clear();
		AppSettings s = app().settings();
		addString("-- Predefined settings --", "");
		addInteger("fps", s.fps);
		addBoolean("fullScreen", s.fullScreen);
		addBoolean("fullScreenCursor", s.fullScreenCursor);
		addString("fullScreenMode", s.fullScreenMode.toString());
		addInteger("height", s.height);
		addBoolean("muted", s.muted);
		addFloat("scale", s.scale);
		addBoolean("smoothRendering", s.smoothRendering);
		addString("title", s.title);
		addBoolean("titleExtended", s.titleExtended);
		addInteger("width", s.width);
		// add generic entries
		if (s.keys().count() != 0) {
			addString("-- Application defined settings --", "");
		}
		s.keys().forEach(key -> {
			Object value = s.get(key);
			if (value instanceof Boolean) {
				addBoolean(key, (boolean) value);
			} else if (value instanceof Integer) {
				addInteger(key, (int) value);
			} else if (value instanceof Float) {
				addFloat(key, (float) value);
			} else {
				addString(key, String.valueOf(value));
			}
		});
		fireTableDataChanged();
	}

	private void addFloat(String key, float value) {
		keys.add(key);
		values.add(String.format("%.2f", value));
	}

	private void addString(String key, String value) {
		keys.add(key);
		values.add(value);
	}

	private void addBoolean(String key, boolean b) {
		keys.add(key);
		values.add(String.valueOf(b));
	}

	private void addInteger(String key, int value) {
		keys.add(key);
		values.add(String.valueOf(value));
	}
}