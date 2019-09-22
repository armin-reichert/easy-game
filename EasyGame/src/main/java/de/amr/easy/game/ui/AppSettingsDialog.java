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

/**
 * Dialog for changing clock frequency and full-screen display mode.
 * 
 * @author Armin Reichert
 *
 */
public class AppSettingsDialog extends JDialog {

	private static class DisplayModeItemRenderer extends JLabel implements ListCellRenderer<DisplayMode> {

		public DisplayModeItemRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends DisplayMode> list, DisplayMode mode,
				int index, boolean isSelected, boolean cellHasFocus) {
			String text = "";
			if (mode != null) {
				text = String.format("%d x %d Pixel, %d Bit, %s Hz", mode.getWidth(), mode.getHeight(),
						mode.getBitDepth(),
						mode.getRefreshRate() == 0 ? "unknown" : String.valueOf(mode.getRefreshRate()));
			}
			setText(text);
			return this;
		}
	}

	private JSlider sliderFPS;
	private DisplayModeItemRenderer displayModeComboRenderer = new DisplayModeItemRenderer();

	public AppSettingsDialog(JFrame parent, Application app) {
		super(parent);
		setSize(600, 150);
		setTitle(String.format("Application '%s'", app.settings.title));
		sliderFPS = new JSlider(0, 120);
		sliderFPS.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				app.clock.setFrequency(sliderFPS.getValue());
				setFpsTooltip();
			}
		});
		sliderFPS.setValue(app.clock.getFrequency());
		sliderFPS.setMajorTickSpacing(50);
		sliderFPS.setMinorTickSpacing(10);
		sliderFPS.setPaintTicks(true);
		sliderFPS.setLabelTable(sliderFPS.createStandardLabels(10));
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));

		JLabel lblFPS = new JLabel("Ticks/sec");
		getContentPane().add(lblFPS, "cell 0 0");
		sliderFPS.setPaintLabels(true);
		setFpsTooltip();
		getContentPane().add(sliderFPS, "cell 1 0,growx");

		JLabel lblDisplayMode = new JLabel("Display Mode");
		getContentPane().add(lblDisplayMode, "cell 0 1,alignx trailing");

		JComboBox<DisplayMode> cbDisplayMode = new JComboBox<>(createComboModel());
		cbDisplayMode.setMaximumRowCount(cbDisplayMode.getItemCount());
		cbDisplayMode.setRenderer(displayModeComboRenderer);
		DisplayMode fullScreenMode = app.settings.fullScreenMode;
		if (fullScreenMode != null) {
			for (int i = 0; i < cbDisplayMode.getItemCount(); ++i) {
				DisplayMode mode = cbDisplayMode.getItemAt(i);
				if (mode.getWidth() == fullScreenMode.getWidth() && mode.getHeight() == fullScreenMode.getHeight()
						&& mode.getBitDepth() == fullScreenMode.getBitDepth()) {
					cbDisplayMode.setSelectedIndex(i);
					break;
				}
			}
		}
		cbDisplayMode.addActionListener(e -> {
			app.settings.fullScreenMode = (DisplayMode) cbDisplayMode.getSelectedItem();
		});
		getContentPane().add(cbDisplayMode, "cell 1 1,growx");
		app.clock.addFrequencyChangeListener(e -> sliderFPS.setValue((Integer) e.getNewValue()));
	}

	private void setFpsTooltip() {
		sliderFPS.setToolTipText("Frame rate = " + sliderFPS.getValue());
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