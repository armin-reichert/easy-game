package de.amr.easy.game.ui.f2dialog;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.amr.easy.game.assets.Assets;
import de.amr.easy.game.assets.SoundClip;

public class SoundTableModel extends AbstractTableModel {

	static class SoundData {
		String path;
		long durationMicroSeconds;
		float volume;
		boolean running;
	}

	public enum Column {
		Path(String.class), Duration(Float.class), Volume(Float.class), Running(Boolean.class);

		private Column(Class<?> class_) {
			this.class_ = class_;
		}

		final Class<?> class_;

		static Column at(int col) {
			return values()[col];
		}
	}

	private List<SoundData> data = new ArrayList<>();

	public SoundTableModel() {
		update();
	}

	public void update() {
		data.clear();
		Assets.soundNames().forEach(path -> {
			SoundClip sound = Assets.sound(path);
			SoundData info = new SoundData();
			info.path = path;
			info.durationMicroSeconds = sound.internal().getMicrosecondLength();
			info.running = sound.isRunning();
			info.volume = sound.volume();
			data.add(info);
		});
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int col) {
		return Column.at(col).name();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return Column.at(col).class_;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		switch (Column.at(col)) {
		case Path:
			return data.get(row).path;
		case Duration:
			return data.get(row).durationMicroSeconds / 1000_000f;
		case Running:
			return data.get(row).running;
		case Volume:
			return data.get(row).volume;
		default:
			return null;
		}
	}
}