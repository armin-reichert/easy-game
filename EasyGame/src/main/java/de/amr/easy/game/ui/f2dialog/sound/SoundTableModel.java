package de.amr.easy.game.ui.f2dialog.sound;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.amr.easy.game.assets.Assets;
import de.amr.easy.game.assets.SoundClip;

public class SoundTableModel extends AbstractTableModel {

	static final SoundTableModel LOREM_IPSUM = new SoundTableModel() {

		private final Record sampleRecord = new Record();

		{
			sampleRecord.running = true;
			sampleRecord.path = "/path/to/sound/file";
			sampleRecord.durationSeconds = 90;
			sampleRecord.volume = 0.66f;
			sampleRecord.audioFormat = "wav";
		};

		@Override
		public Object getValueAt(int row, int col) {
			switch (ColumnInfo.at(col)) {
			case Running:
				return sampleRecord.running;
			case Path:
				return sampleRecord.path;
			case Duration:
				return sampleRecord.durationSeconds;
			case Format:
				return sampleRecord.audioFormat;
			case Volume:
				return sampleRecord.volume;
			default:
				return null;
			}
		}

		@Override
		public int getRowCount() {
			return 10;
		}
	};

	static class Record {
		String path;
		String audioFormat;
		float durationSeconds;
		float volume;
		boolean running;
	}

	public enum ColumnInfo {
		//@formatter:off
		Running(Boolean.class),
		Path(String.class), 
		Duration(Float.class),
		Volume(Float.class),
		Format(String.class);
		//@formatter:on

		static ColumnInfo at(int col) {
			return values()[col];
		}

		private ColumnInfo(Class<?> class_) {
			this.class_ = class_;
			this.text = name();
		}

		private ColumnInfo(String text, Class<?> class_) {
			this.class_ = class_;
			this.text = text;
		}

		final String text;
		final Class<?> class_;

	}

	private List<Record> records = new ArrayList<>();

	public SoundTableModel() {
	}

	public void update() {
		List<Record> newRecords = new ArrayList<>();
		newRecords.clear();
		Assets.soundNames().forEach(path -> {
			SoundClip sound = Assets.sound(path);
			Record r = new Record();
			r.path = path;
			r.audioFormat = sound.internal().getFormat().toString();
			r.durationSeconds = sound.internal().getMicrosecondLength() / 1_000_000f;
			r.running = sound.isRunning();
			r.volume = sound.volume();
			newRecords.add(r);
		});
		records = newRecords;
		fireTableDataChanged();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Record r = records.get(row);
		switch (ColumnInfo.at(col)) {
		case Running:
			return r.running;
		case Path:
			return r.path;
		case Duration:
			return r.durationSeconds;
		case Format:
			return r.audioFormat;
		case Volume:
			return r.volume;
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int col) {
		return ColumnInfo.at(col).text;
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return ColumnInfo.at(col).class_;
	}

	@Override
	public int getRowCount() {
		return records.size();
	}

	@Override
	public int getColumnCount() {
		return ColumnInfo.values().length;
	}
}