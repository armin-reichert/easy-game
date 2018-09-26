package de.amr.easy.game.ui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.easy.game.Application;

public class AppSettingsDialog extends JDialog {

	private JSlider fpsControl;

	public AppSettingsDialog(JFrame parent, Application app) {
		super(parent);
		setSize(600, 100);
		setTitle("Change clock frequency for application: " + app.settings.title);
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
		fpsControl.setPaintLabels(true);
		fpsControl.setToolTipText("Rendering FPS");
		add(fpsControl);
		app.clock.addFrequencyChangeListener(e -> fpsControl.setValue((Integer) e.getNewValue()));
	}
}