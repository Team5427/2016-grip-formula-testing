package com.Team5427.VisionProcessing;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

import java.net.MalformedURLException;
import java.util.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class VisionPanel extends JPanel implements Runnable, KeyListener {

	public static final String IP_CAMERA_URL = "10.54.27.11";

	private int width, height;
	private BufferedImage buffer;

	private Webcam webcam;
	private Dimension resolution;

	Scanner scanner;

	private int updatesPerSecond = 30;
	private long updateCount = 0;

	public VisionPanel(int width, int height) {

		super();

		this.width = width;
		this.height = height;

		resolution = new Dimension(width, height);

		setSize(width, height);

		buffer = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		scanner = new Scanner(System.in);

		addKeyListener(this);

		// Creates a new webcam
		try {
			initializeCamera();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// new Thread(this).start();
	}

	/**
	 * Initializes the camera for use
	 */
	 public void initializeCamera() throws MalformedURLException{
		 Webcam.setDriver(new IpCamDriver());

		 IpCamDeviceRegistry.register(new IpCamDevice("Robot Vision", IP_CAMERA_URL, IpCamMode.PUSH));

		 try {
			 webcam = Webcam.getWebcams().get(0);
			 webcam.setViewSize(resolution); // Sets the correct resolution
			 webcam.open(); // I think this "opens" the camera. This line is needed
		 }
		 catch (NoClassDefFoundError e) {
			 System.err.println("Cannot find usb camera");
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }

	@Override
	public void addNotify() {
		super.addNotify();
		requestFocus();
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		char key = Character.toLowerCase(e.getKeyChar());

		if (key == 'c') {
			initializeCalibration();
		}
	}

	/**
	 * Initializes the calibration sequence
	 */
	public void initializeCalibration() {
		System.out.println("===FOV Calibration===");

		if (Goal.ENABLE_FOV_CALIBRATION && Main.goals != null && Main.goals.size() == 1) {
			Goal g = Main.goals.get(0);

			System.out.print("Do you want to calibrate the camera? (y,n): ");
			char input;

			try {
				input = scanner.next().charAt(0);
			} catch (InputMismatchException e) {
				System.out.println("\nThe selection you entered is invalid.");
				input = 'n';
			}

			input = Character.toLowerCase(input);

			if (input == 'y') {

				System.out.print("Enter the distance from the camera to the goal: ");
				double distance = scanner.nextDouble();

				double FOV = calibrateFOV(g, distance);

				System.out.println("The new FOV is: " + FOV);

			} else
				System.out.println("\nExiting calibration.");

		} else {
			System.out.println("Unable to calibrate");

			if (Main.goals == null)
				System.out.println("Goals is null");
			else if (Main.goals.size() > 1)
				System.out.println("There are " + Main.goals.size()
						+ " goals. Only 1 must visible in the camera for calibration.");
			else if (Main.goals.size() == 0) {
				System.out.println("There are no goals found.");
			} else if (!Goal.ENABLE_FOV_CALIBRATION)
				System.out.println("FOV Calibration has been disabled.");

			System.out.println("\nExiting calibration.");
		}
	}

	/**
	 * Calibrates the FOV based on goal and distance
	 * 
	 * @param goal
	 *            Reference goal
	 * @param distance
	 *            Actual distance between goal to robot
	 * @return
	 */
	public double calibrateFOV(Goal goal, double distance) {
		double verticalDistance = goal.getVerticalDistance();

		double FOV = Math.toDegrees(Math.atan(verticalDistance / distance));
		Goal.FOV = 2 * FOV;

		return Goal.FOV;
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

		bg.setColor(Color.BLACK);
		bg.fillRect(0, 0, getWidth(), getHeight());

		// Gets image from camera and paints it to the buffer
		if (webcam != null) {
			BufferedImage cameraImg = webcam.getImage();
			bg.drawImage(cameraImg, 0, 0, null);
		}

		/*
		 * // temp for testing the creation of goals if (Main.goals.size() > 0)
		 * { bg.setColor(Color.GREEN); bg.drawLine((int)
		 * Main.goals.get(0).getCenterLine().getX1(), (int)
		 * Main.goals.get(0).getCenterLine().getY1(), (int)
		 * Main.goals.get(0).getCenterLine().getX2(), (int)
		 * Main.goals.get(0).getCenterLine().getY2());
		 * 
		 * bg.setColor(Color.BLUE); bg.drawLine((int)
		 * Main.goals.get(0).getLeftLine().getX1(), (int)
		 * Main.goals.get(0).getLeftLine().getY1(), (int)
		 * Main.goals.get(0).getLeftLine().getX2(), (int)
		 * Main.goals.get(0).getLeftLine().getY2());
		 * 
		 * bg.setColor(Color.RED); bg.drawLine((int)
		 * Main.goals.get(0).getRightLine().getX1(), (int)
		 * Main.goals.get(0).getRightLine().getY1(), (int)
		 * Main.goals.get(0).getRightLine().getX2(), (int)
		 * Main.goals.get(0).getRightLine().getY2());
		 * 
		 * }
		 * 
		 */

		int x1, y1, x2, y2;
		for (int i = 0; i < Main.goals.size(); i++) {

			bg.setColor(Color.GREEN);
			bg.drawLine((int) Main.goals.get(i).getCenterLine().getX1(),
					(int) Main.goals.get(i).getCenterLine().getY1(), (int) Main.goals.get(i).getCenterLine().getX2(),
					(int) Main.goals.get(i).getCenterLine().getY2());

			bg.setColor(Color.BLUE);
			bg.drawLine((int) Main.goals.get(i).getLeftLine().getX1(), (int) Main.goals.get(i).getLeftLine().getY1(),
					(int) Main.goals.get(i).getLeftLine().getX2(), (int) Main.goals.get(i).getLeftLine().getY2());

			bg.setColor(Color.RED);
			bg.drawLine((int) Main.goals.get(i).getRightLine().getX1(), (int) Main.goals.get(i).getRightLine().getY1(),
					(int) Main.goals.get(i).getRightLine().getX2(), (int) Main.goals.get(i).getRightLine().getY2());

		}

		/* Draws distance of goal on the bottom left */
		// This can be later merged the for each loop that draws the lines. This
		// is temporary for readability
		bg.setColor(Color.WHITE);
		bg.setFont(new Font("Comic Sans", Font.PLAIN, 10));

		for (int i = 0; i < Main.goals.size(); i++) {

			int x = (int) Main.goals.get(i).getCenterLine().getX1() - 5;
			int y = (int) Main.goals.get(i).getCenterLine().getY1() + 15;

			String distance = String.format("%.2f", Main.goals.get(i).getDistanceToGoal());
			String towerDistance = String.format("%.2f", Main.goals.get(i).getDistanceToTower());
			String angleDegrees = String.format("%.2f", Main.goals.get(i).getAngleOfElevationInDegrees());

			bg.drawString("Distance: " + distance + "in.", x, y);
			bg.drawString("Tower Distance: " + towerDistance + "in.", x, y += 12);
			bg.drawString("Angle " + angleDegrees + "°", x, y + 12);

		}

		g.drawImage(buffer, 0, 0, null);
	}

}
