package de.amr.easy.game.ui;

import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.easy.game.Application;
import net.miginfocom.swing.MigLayout;

public class AppSettingsDialog extends JDialog {

	private static class DisplayModeItemRenderer extends JLabel implements ListCellRenderer<DisplayMode> {

		public DisplayModeItemRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends DisplayMode> list, DisplayMode mode,
				int index, boolean isSelected, boolean cellHasFocus) {
			setText(
					String.format("%d x %d Pixel, %d Bit, %s Hz", mode.getWidth(), mode.getHeight(), mode.getBitDepth(),
							mode.getRefreshRate() == 0 ? "unknown" : String.valueOf(mode.getRefreshRate())));
			return this;
		}
	}

	private JSlider fpsControl;
	private DisplayModeItemRenderer displayModeComboRenderer = new DisplayModeItemRenderer();

	public AppSettingsDialog(JFrame parent, Application app) {
		super(parent);
		setSize(600, 290);
		setTitle("Settings for app: " + app.settings.title);
		fpsControl = new JSlider(0, 100);
		fpsControl.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				app.clock.setFrequency(fpsControl.getValue());
			}
		});
		fpsControl.setValue(app.clock.getFrequency());
		fpsControl.setMajorTickSpacing(10);
		fpsControl.setMinorTickSpacing(1);
		fpsControl.setPaintTicks(true);
		fpsControl.setLabelTable(fpsControl.createStandardLabels(5));
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));

		JLabel lblClockFrequency = new JLabel("Clock frequency");
		getContentPane().add(lblClockFrequency, "cell 0 0");
		fpsControl.setPaintLabels(true);
		fpsControl.setToolTipText("Rendering FPS");
		getContentPane().add(fpsControl, "cell 1 0,growx");

		JLabel lblDisplayMode = new JLabel("Display Mode");
		getContentPane().add(lblDisplayMode, "cell 0 1,alignx trailing");

		JComboBox<DisplayMode> displayModeCombo = new JComboBox<>(createComboModel());
		displayModeCombo.setMaximumRowCount(displayModeCombo.getItemCount());
		displayModeCombo.setRenderer(displayModeComboRenderer);
		DisplayMode fullScreenMode = app.settings.fullScreenMode;
		if (fullScreenMode != null) {
			for (int i = 0; i < displayModeCombo.getItemCount(); ++i) {
				DisplayMode mode = displayModeCombo.getItemAt(i);
				if (mode.getWidth() == fullScreenMode.getWidth() && mode.getHeight() == fullScreenMode.getHeight()
						&& mode.getBitDepth() == fullScreenMode.getBitDepth()) {
					displayModeCombo.setSelectedIndex(i);
					break;
				}
			}
		}
		displayModeCombo.addActionListener(e -> {
			app.settings.fullScreenMode = (DisplayMode) displayModeCombo.getSelectedItem();
		});
		getContentPane().add(displayModeCombo, "cell 1 1,growx");
		app.clock.addFrequencyChangeListener(e -> fpsControl.setValue((Integer) e.getNewValue()));
	}

	private ComboBoxModel<DisplayMode> createComboModel() {
		DisplayMode[] modes = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDisplayModes();
		Vector<DisplayMode> withoutDuplicates = new Vector<>();
		boolean duplicate = false;
		for (int i = 0; i < modes.length; ++i) {
			if (i > 0) {
				duplicate = modes[i - 1].getWidth() == modes[i].getWidth()
						&& modes[i - 1].getHeight() == modes[i].getHeight()
						&& modes[i - 1].getBitDepth() == modes[i].getBitDepth();
			}
			if (!duplicate) {
				withoutDuplicates.add(modes[i]);
			}
		}
		return new DefaultComboBoxModel<>(withoutDuplicates);
	}

}