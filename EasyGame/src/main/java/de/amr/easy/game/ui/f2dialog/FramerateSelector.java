package de.amr.easy.game.ui.f2dialog;

import static de.amr.easy.game.Application.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.easy.game.controller.Lifecycle;
import net.miginfocom.swing.MigLayout;
import javax.swing.SwingConstants;

public class FramerateSelector extends JComponent implements Lifecycle {
	private static final int MAX_FPS = 180;
	private JSlider sliderFPS;
	private JLabel lblFPS;

	private ChangeListener fpsSliderChanged = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			int fps = Math.max(sliderFPS.getValue(), 1);
			app().clock().setTargetFrameRate(fps);
			update();
		}
	};

	public FramerateSelector() {
		setLayout(new MigLayout("", "[grow,fill][130px:130px:130px,fill]", "[]"));
		lblFPS = new JLabel("100 Ticks/sec");
		lblFPS.setHorizontalAlignment(SwingConstants.CENTER);
		lblFPS.setPreferredSize(new Dimension(140, 16));
		lblFPS.setMaximumSize(new Dimension(140, 16));
		lblFPS.setMinimumSize(new Dimension(140, 16));
		lblFPS.setBorder(new EmptyBorder(0, 3, 0, 6));
		lblFPS.setForeground(Color.BLUE);
		lblFPS.setFont(new Font("SansSerif", Font.BOLD, 18));
		add(lblFPS, "cell 1 0,alignx right,aligny center");
		sliderFPS = new JSlider(0, MAX_FPS);
		lblFPS.setLabelFor(sliderFPS);
		add(sliderFPS, "cell 0 0,growx");
		sliderFPS.addChangeListener(fpsSliderChanged);
		sliderFPS.setMajorTickSpacing(50);
		sliderFPS.setMinorTickSpacing(10);
		sliderFPS.setPaintTicks(true);
		sliderFPS.setLabelTable(sliderFPS.createStandardLabels(10));
		sliderFPS.setPaintLabels(true);
	}

	@Override
	public void init() {
		sliderFPS.setValue(app().clock().getTargetFramerate());
	}

	@Override
	public void update() {
		lblFPS.setText(String.format("%d ticks/sec", app().clock().getTargetFramerate()));
		sliderFPS.setValue(app().clock().getTargetFramerate());
		sliderFPS.setToolTipText("Frame rate = " + sliderFPS.getValue());
	}
}
