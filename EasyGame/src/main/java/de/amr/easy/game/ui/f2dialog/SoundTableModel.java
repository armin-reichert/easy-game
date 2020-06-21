package de.amr.easy.game.ui.f2dialog;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.amr.easy.game.assets.Assets;
import de.amr.easy.game.assets.SoundClip;

public class SoundTableModel extends AbstractTableModel {

	static class SoundInfo {
		String path;
		boolean running;
	}

	public enum Column {
		Path(String.class), Running(Boolean.class);

		private Column(Class<?> class_) {
			this.class_ = class_;
		}

		final Class<?> class_;

		static Column at(int col) {
			return values()[col];
		}
	}

	private List<SoundInfo> data = new ArrayList<>();

	public SoundTableModel() {
		update();
	}

	public void update() {
		data.clear();
		Assets.soundNames().forEach(path -> {
			SoundClip sound = Assets.sound(path);
			SoundInfo info = new SoundInfo();
			info.path = path;
			info.running = sound.isRunning();
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
		return 2;
	}

	@Override
	public Object getValueAt(int row, int col) {
		switch (Column.at(col)) {
		case Path:
			return data.get(row).path;
		case Running:
			return data.get(row).running;
		default:
			return null;
		}
	}
}