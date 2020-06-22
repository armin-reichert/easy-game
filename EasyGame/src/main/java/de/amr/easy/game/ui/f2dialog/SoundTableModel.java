package de.amr.easy.game.ui.f2dialog;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.amr.easy.game.assets.Assets;
import de.amr.easy.game.assets.SoundClip;

public class SoundTableModel extends AbstractTableModel {

	static class SoundData {
		String path;
		float durationSeconds;
		float volume;
		boolean running;
	}

	public enum Column {
		Path(String.class), Duration("Duration (sec)", Float.class), Volume(Float.class), Running(Boolean.class);

		private Column(Class<?> class_) {
			this.class_ = class_;
			this.text = name();
		}

		private Column(String text, Class<?> class_) {
			this.class_ = class_;
			this.text = text;
		}

		final String text;
		final Class<?> class_;

		static Column at(int col) {
			return values()[col];
		}
	}

	static final SoundTableModel SAMPLE_DATA = new SoundTableModel() {

		Object[] sampleRow = { "path", 90, .76f, true };

		@Override
		public Object getValueAt(int row, int col) {
			return sampleRow[col];
		}

		@Override
		public int getRowCount() {
			return 7;
		}

	};
	private List<SoundData> tableData = new ArrayList<>();

	public SoundTableModel() {
		update();
	}

	public synchronized void update() {
		tableData.clear();
		Assets.soundNames().forEach(path -> {
			SoundClip sound = Assets.sound(path);
			SoundData data = new SoundData();
			data.path = path;
			data.durationSeconds = sound.internal().getMicrosecondLength() / 1_000_000f;
			data.running = sound.isRunning();
			data.volume = sound.volume();
			tableData.add(data);
		});
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int col) {
		return Column.at(col).text;
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return Column.at(col).class_;
	}

	@Override
	public int getRowCount() {
		return tableData.size();
	}

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		switch (Column.at(col)) {
		case Path:
			return tableData.get(row).path;
		case Duration:
			return tableData.get(row).durationSeconds;
		case Running:
			return tableData.get(row).running;
		case Volume:
			return tableData.get(row).volume;
		default:
			return null;
		}
	}
}