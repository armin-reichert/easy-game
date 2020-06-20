package de.amr.easy.game.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.amr.easy.game.assets.Assets;
import de.amr.easy.game.assets.SoundClip;

public class SoundTableModel extends AbstractTableModel {

	private List<SoundClip> clips = new ArrayList<>();

	public void update() {
		clips.clear();
		Assets.sounds().forEach(clips::add);
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return 0;
	}

	@Override
	public int getColumnCount() {
		return 0;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return null;
	}

}
