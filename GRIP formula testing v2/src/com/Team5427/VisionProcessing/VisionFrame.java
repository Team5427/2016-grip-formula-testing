package com.Team5427.VisionProcessing;

import java.awt.*;
import javax.swing.*;

public class VisionFrame extends JFrame {

	public static final String title = "Vision Filtering Test";

	/*
	 * Change this if we're using camera or images - Image w:h - 720:502 -
	 * Multiply the width and height by the resize
	 */

	public static final int width = 632;
	public static final int height = 600;

	private VisionPanel p;

	public VisionFrame() {
		super(title);

		pack();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Insets insect = getInsets();
		int fWidth = insect.left + insect.right + width;
		int fHeight = insect.top + insect.bottom + height;

		p = new VisionPanel(fWidth, fHeight);

		setPreferredSize(new Dimension(fWidth, fHeight));

		add(p);

		// May change this later
		setResizable(false);

		pack();

		setVisible(true);

	}

	public VisionPanel getPanel() {
		return p;
	}
}
