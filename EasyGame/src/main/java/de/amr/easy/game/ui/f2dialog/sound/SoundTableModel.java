package de.amr.easy.game.ui.f2dialog.sound;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.amr.easy.game.assets.Assets;
import de.amr.easy.game.assets.SoundClip;

public class SoundTableModel extends AbstractTableModel {

	static final SoundTableModel SAMPLE_DATA = new SoundTableModel() {

		Object[] sampleRecord = { true, "path", 90f, .76f, "audio format" };

		@Override
		public Object getValueAt(int row, int col) {
			return sampleRecord[col];
		}

		@Override
		public int getRowCount() {
			return 7;
		}
	};

	static class Record {
		String path;
		String audioFormat;
		float durationSeconds;
		float volume;
		boolean running;
	}

	public enum Field {
		//@formatter:off
		Running(Boolean.class),
		Path(String.class), 
		Duration(Float.class),
		Volume(Float.class),
		Format(String.class);
		//@formatter:on

		static Field at(int col) {
			return values()[col];
		}

		private Field(Class<?> class_) {
			this.class_ = class_;
			this.text = name();
		}

		private Field(String text, Class<?> class_) {
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
		switch (Field.at(col)) {
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
		return Field.at(col).text;
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return Field.at(col).class_;
	}

	@Override
	public int getRowCount() {
		return records.size();
	}

	@Override
	public int getColumnCount() {
		return Field.values().length;
	}
}