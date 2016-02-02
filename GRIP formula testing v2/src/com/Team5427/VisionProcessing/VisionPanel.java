package com.Team5427.VisionProcessing;

import com.github.sarxos.webcam.Webcam;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class VisionPanel extends JPanel implements Runnable {

	private int width, height;
	private BufferedImage buffer;

	private Webcam webcam;
	private Dimension resolution;

	private int updatesPerSecond = 30;
	private long updateCount = 0;

	public VisionPanel(int width, int height) {

		super();

		this.width = width;
		this.height = height;

		resolution = new Dimension(width, height);

		setSize(width, height);

		buffer = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		// Creates a new webcam
		enableCamera();

		// new Thread(this).start();
	}

	/**
	 * Enables the usb camera for viewing
	 */
	public void enableCamera() {
		try {
			webcam = Webcam.getWebcams().get(1);			// 1 is a usb camera, 0 is for built-in camera
			webcam.setViewSize(resolution);					// Sets the correct resolution
			webcam.open();									// I think this "opens" the camera. This line is needed
		} catch (NoClassDefFoundError e) {
			System.err.println("Cannot find usb camera");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// calculates how many miliseconds to wait for the next update
		int waitToUpdate = (1000 / updatesPerSecond);

		long startTime = System.nanoTime();

		while (true) {
			// is true when you update
			boolean shouldRepaint = false;

			// Finds the current time
			long currentTime = System.nanoTime();

			// Finds out how many updates are needed
			long updatesNeeded = (((currentTime - startTime) / 1000000)) / waitToUpdate;
			for (long x = updateCount; x < updatesNeeded; x++) {
				shouldRepaint = true;
				updateCount++;
			}

			if (shouldRepaint) {
				// repaint();
			}

			// sleep so other threads have time to run
			try {
				Thread.sleep(5);
			} catch (Exception e) {
				System.err.println("Error sleeping in run method: " + e.getMessage());
			}
		}
	}

	@Override
	public synchronized void paint(Graphics g) {

		Graphics bg = buffer.getGraphics();

/*

		bg.setColor(Color.BLACK);
		bg.fillRect(0, 0, getWidth(), getHeight());
*/

		// Gets image from camera, then draws it
		try {
			BufferedImage cameraImage = webcam.getImage();
			bg.drawImage(cameraImage, 0, 0, null);

			bg.setColor(Color.YELLOW);
		}
		catch (NullPointerException e) {											// Null pointer occurs when webcam was never initialized
			System.err.println("Webcam not initialized");

			// Tries to initialize camera
//			enableCamera();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		
		/*
		// temp for testing the creation of goals
		if (Main.goals.size() > 0) {
			bg.setColor(Color.GREEN);
			bg.drawLine((int) Main.goals.get(0).getCenterLine().getX1(),
					(int) Main.goals.get(0).getCenterLine().getY1(), (int) Main.goals.get(0).getCenterLine().getX2(),
					(int) Main.goals.get(0).getCenterLine().getY2());

			bg.setColor(Color.BLUE);
			bg.drawLine((int) Main.goals.get(0).getLeftLine().getX1(), (int) Main.goals.get(0).getLeftLine().getY1(),
					(int) Main.goals.get(0).getLeftLine().getX2(), (int) Main.goals.get(0).getLeftLine().getY2());

			bg.setColor(Color.RED);
			bg.drawLine((int) Main.goals.get(0).getRightLine().getX1(), (int) Main.goals.get(0).getRightLine().getY1(),
					(int) Main.goals.get(0).getRightLine().getX2(), (int) Main.goals.get(0).getRightLine().getY2());

		}
		
*/

		
		int x1, y1, x2, y2;
		for (int i = 0; i<Main.goals.size();i++) {

			
			  
			  bg.setColor(Color.GREEN); bg.drawLine((int)
			  Main.goals.get(i).getCenterLine().getX1(), (int) Main.goals.get(i).getCenterLine().getY1(),
			  (int) Main.goals.get(i).getCenterLine().getX2(), (int)
			  Main.goals.get(i).getCenterLine().getY2());
			  
			  bg.setColor(Color.BLUE); bg.drawLine((int)
			  Main.goals.get(i).getLeftLine().getX1(), (int) Main.goals.get(i).getLeftLine().getY1(), (int)
			  Main.goals.get(i).getLeftLine().getX2(), (int) Main.goals.get(i).getLeftLine().getY2());
			  
			  bg.setColor(Color.RED); bg.drawLine((int)
			  Main.goals.get(i).getRightLine().getX1(), (int) Main.goals.get(i).getRightLine().getY1(), (int)
			  Main.goals.get(i).getRightLine().getX2(), (int) Main.goals.get(i).getRightLine().getY2());
			 

		}

		/* Draws distance of goal on the bottom left */
		// This can be later merged the for each loop that draws the lines. This
		// is temporary for readability
		bg.setColor(Color.WHITE);
		bg.setFont(new Font("Comic Sans", Font.PLAIN, 10));

		for (int i = 0; i<Main.goals.size();i++) {

			
			  
			  int x = (int)Main.goals.get(i).getCenterLine().getX1() - 5; int y =
			  (int)Main.goals.get(i).getCenterLine().getY1() + 15;
			  
			  String distance = String.format("%.2f", Main.goals.get(i).getDistanceToRobot());
			  String towerDistance = String.format("%.2f",
			  Main.goals.get(i).getDistanceToTower()); String angleDegrees =
			  String.format("%.2f", Main.goals.get(i).getAngleElevationDegrees());
			  
			  bg.drawString("Distance: " + distance + "in.", x, y);
			  bg.drawString("Tower Distance: " + towerDistance + "in.", x, y +=
			  12); bg.drawString("Angle " + angleDegrees + "°", x, y + 12);
			 
		}

		g.drawImage(buffer, 0, 0, null);
	}

}
