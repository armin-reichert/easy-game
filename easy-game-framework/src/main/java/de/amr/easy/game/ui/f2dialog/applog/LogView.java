package de.amr.easy.game.ui.f2dialog.applog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.amr.easy.game.Application;
import de.amr.easy.game.controller.Lifecycle;

public class LogView extends JPanel implements Lifecycle {
	private JTextArea textField;

	public LogView() {
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		textField = new JTextArea();
		textField.setFont(new Font("Consolas", Font.PLAIN, 14));
		textField.setTabSize(3);
		textField.setForeground(Color.YELLOW);
		textField.setBackground(Color.BLACK);
		scrollPane.setViewportView(textField);
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		List<String> lines = Application.getLoggedLines();
		if (lines.size() > textField.getLineCount()) {
			if (textField.getLineCount() == 1) {
				textField.append(lines.get(0));
				textField.append("\n");
			}
			for (int line = textField.getLineCount(); line < lines.size(); ++line) {
				textField.append(lines.get(line));
				textField.append("\n");
			}
		}
	}

}
