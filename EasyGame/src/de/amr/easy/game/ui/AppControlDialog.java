package de.amr.easy.game.ui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.easy.game.Application;

@SuppressWarnings("serial")
public class AppControlDialog extends JDialog {

	private final Application app;
	private JSlider fpsControl;

	public AppControlDialog(JFrame parent, Application app) {
		super(parent);
		this.app = app;
		setTitle("Application Control for: " + app.settings.title);
		addFPSControl();
		setSize(600, 100);
	}

	private void addFPSControl() {
		fpsControl = new JSlider(0, 100);
		fpsControl.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				app.pulse.setFrequency(fpsControl.getValue());
			}
		});
		fpsControl.setValue(app.pulse.getFrequency());
		fpsControl.setMajorTickSpacing(10);
		fpsControl.setMinorTickSpacing(1);
		fpsControl.setPaintTicks(true);
		fpsControl.setLabelTable(fpsControl.createStandardLabels(5));
		fpsControl.setPaintLabels(true);
		fpsControl.setToolTipText("Rendering FPS");
		add(fpsControl);
	}

}
