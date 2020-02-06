package de.amr.easy.game.ui;

import java.awt.Color;
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
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.easy.game.Application;
import net.miginfocom.swing.MigLayout;
import java.awt.Dimension;

/**
 * Dialog for changing clock frequency and full-screen display mode.
 * 
 * @author Armin Reichert
 *
 */
public class AppSettingsDialog extends JDialog {

	private static class DisplayModeItemRenderer extends JLabel implements ListCellRenderer<DisplayMode> {

		@Override
		public Component getListCellRendererComponent(JList<? extends DisplayMode> list, DisplayMode mode, int index,
				boolean isSelected, boolean cellHasFocus) {
			String text = "No entries";
			if (mode != null) { // inside Window Builder, mode can be NULL
				text = String.format("%d x %d Pixel, %d Bit, %s Hz", mode.getWidth(), mode.getHeight(), mode.getBitDepth(),
						mode.getRefreshRate() == 0 ? "unknown" : String.valueOf(mode.getRefreshRate()));
			}
			setText(text);
			return this;
		}
	}

	private Application app;
	private JSlider sliderFPS;
	private DisplayModeItemRenderer displayModeComboRenderer = new DisplayModeItemRenderer();
	private JComboBox<DisplayMode> cbDisplayMode;
	private JPanel fpsHistoryPanel;
	private FramerateHistoryView fpsHistoryView;

	public AppSettingsDialog(JFrame parent) {
		super(parent);
		setSize(665, 334);
		sliderFPS = new JSlider(0, 120);
		sliderFPS.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (sliderFPS.getValue() > 0) {
					app.clock().setTargetFramerate(sliderFPS.getValue());
					setFpsTooltip();
				} else {
					sliderFPS.setValue(1);
				}
			}
		});
		sliderFPS.setMajorTickSpacing(50);
		sliderFPS.setMinorTickSpacing(10);
		sliderFPS.setPaintTicks(true);
		sliderFPS.setLabelTable(sliderFPS.createStandardLabels(10));
		getContentPane().setLayout(new MigLayout("", "[][3px:n:3px][grow,fill]", "[][][grow,fill]"));
		JLabel lblFPS = new JLabel("Ticks/sec");
		getContentPane().add(lblFPS, "cell 0 0,alignx right");
		sliderFPS.setPaintLabels(true);
		setFpsTooltip();
		getContentPane().add(sliderFPS, "cell 2 0,growx");
		JLabel lblDisplayMode = new JLabel("Display Mode");
		getContentPane().add(lblDisplayMode, "cell 0 1,alignx trailing");
		ComboBoxModel<DisplayMode> comboModel = createComboModel();
		cbDisplayMode = new JComboBox<>(comboModel);
		cbDisplayMode.setMinimumSize(new Dimension(220, 26));
		cbDisplayMode.addActionListener(e -> {
			app.settings().fullScreenMode = (DisplayMode) cbDisplayMode.getSelectedItem();
		});
		cbDisplayMode.setMaximumRowCount(cbDisplayMode.getItemCount());
		cbDisplayMode.setRenderer(displayModeComboRenderer);
		getContentPane().add(cbDisplayMode, "cell 1 1");

		fpsHistoryPanel = new JPanel();
		fpsHistoryPanel
				.setBorder(new TitledBorder(null, "Framerate History", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(fpsHistoryPanel, "cell 0 2 3 1,grow");
		fpsHistoryPanel.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));

		fpsHistoryView = new FramerateHistoryView(500, 150);
		fpsHistoryView.setBackground(Color.BLACK);
		fpsHistoryPanel.add(fpsHistoryView, "cell 0 0,grow");
	}

	public void setApp(Application app) {
		this.app = app;
		fpsHistoryView.setApp(app);
		setTitle(String.format("Application '%s'", app.settings().title));
		sliderFPS.setValue(app.clock().getTargetFramerate());
		app.clock().addFrequencyChangeListener(e -> sliderFPS.setValue((Integer) e.getNewValue()));
		DisplayMode fullScreenMode = app.settings().fullScreenMode;
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
	}

	private void setFpsTooltip() {
		sliderFPS.setToolTipText("Frame rate = " + sliderFPS.getValue());
	}

	private ComboBoxModel<DisplayMode> createComboModel() {
		DisplayMode[] modes = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayModes();
		Vector<DisplayMode> withoutDuplicates = new Vector<>();
		boolean duplicate = false;
		for (int i = 0; i < modes.length; ++i) {
			if (i > 0) {
				duplicate = modes[i - 1].getWidth() == modes[i].getWidth() && modes[i - 1].getHeight() == modes[i].getHeight()
						&& modes[i - 1].getBitDepth() == modes[i].getBitDepth();
			}
			if (!duplicate) {
				withoutDuplicates.add(modes[i]);
			}
		}
		return new DefaultComboBoxModel<>(withoutDuplicates);
	}
}