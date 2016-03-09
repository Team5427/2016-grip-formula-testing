package com.Team5427.VisionProcessing;

import com.Team5427.res.Config;
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
import javax.swing.text.html.HTMLDocument;

public class VisionPanel extends JPanel implements Runnable, KeyListener {

	public static final String IP_CAMERA_URL = "http://10.54.27.11/mjpg/video.mjpg";
	public static final Dimension RESOLUTION = new Dimension(640, 480);
	public static double pixelsToGoal;

	private int width, height;

	private BufferedImage buffer;
	private Webcam webcam;
	private BufferedImage cameraImg;

	Scanner scanner;

	private static ArrayList<Color> colorList;

	private int updatesPerSecond = 30;
	private long updateCount = 0;
	private double previousFrameTime = 0; // Previous System nanotime for last
	// frame

	private boolean donePainting = false;

	public VisionPanel(int width, int height) {

		super();

		this.width = width;
		this.height = height;

		setSize(width, height);

		buffer = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		scanner = new Scanner(System.in);

		addKeyListener(this);

		// Creates a new webcam
		try {
			// initializeCamera();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ne w Thread(this).start();
	}

	/**
	 * Initializes the camera for use
	 */
	static {
		Webcam.setDriver(new IpCamDriver());
		calculateVerticalFOV();

		// Color ArrayList
		colorList = new ArrayList<>();
		colorList.add(Color.BLUE);
		colorList.add(Color.GREEN);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.CYAN);
	}

	public void initializeCamera() throws MalformedURLException {

		IpCamDeviceRegistry.register(new IpCamDevice("Robot Vision", IP_CAMERA_URL, IpCamMode.PUSH));

		try {
			webcam = Webcam.getWebcams().get(0);
			webcam.setViewSize(RESOLUTION); // Sets the correct RESOLUTION
			webcam.open(); // I think this "opens" the camera. This line is
			// needed
		} catch (NoClassDefFoundError e) {
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
		char key = Character.toLowerCase(e.getKeyChar());

		if (key == 'r') {
			connectToNetwork();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		char key = Character.toLowerCase(e.getKeyChar());

		if (key == 'c') {
			initializeCalibration();
		}
	}

	/**
	 * Attempts to establish connection with the roborio
	 *
	 * // TODO: Do something so that the driver will not accidentally reconnect
	 * to the roborio when connection has already been established.
	 */
	public void connectToNetwork() {
		System.out.println("test");
		Main.client.start();
	}

	/**
	 * Initializes the calibration sequence
	 */
	@SuppressWarnings("unused")
	public void initializeCalibration() {
		System.out.println("===FOV Calibration===");

		if (Config.ENABLE_FOV_CALIBRATION && Main.goals != null && Main.goals.size() == 1) {
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

				System.out.print(
						"Enter the height of the center of the goal, the distance to the base of the tower, and the distance to the center of the goal.");

				calibrateEverything(scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble());

				// double FOV = calibrateFOV(g, distance);

				// System.out.println("The new FOV is: " + FOV);

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
			} else if (!Config.ENABLE_FOV_CALIBRATION)
				System.out.println("FOV Calibration has been disabled.");
			System.out.println("\nExiting calibration.");
		}
	}

	// TODO possibly finish later
	public static double calibrateEverything(double goalHeight, double distanceToTower, double distanceToGoal) {

		double angleOfElevation = Math.atan(goalHeight / distanceToTower);

		return -1;
	}

	/**
	 * @deprecated Calibrates the FOV based on goal and distance
	 * 
	 * @param goal
	 *            Reference goal
	 * @param distance
	 *            Actual distance between goal to robot
	 * @return calibrated FOV
	 */
	public static double calibrateFOV(Goal goal, double distance) {
		double verticalDistance = goal.getNormalizedVerticalDistance();

		double FOV = Math.toDegrees(Math.atan(verticalDistance / distance));

		Config.horizontalFOV = 2 * FOV;
		calculateVerticalFOV();

		return Config.horizontalFOV;
	}

	/**
	 * @deprecated // TODO: Finish this
	 *
	 *             NOTE: Mr. Segura told me(Charlie) that there's no need for
	 *             calibration since we have encoders. If we do need this code,
	 *             then finish the method and refactor the method name to
	 *             something more relevant.
	 *
	 *             Calibrates the angle of the robot using angles
	 * @param goal
	 * @param distance
	 * @return
	 */
	public static double callibrateFOVfromAngle(Goal goal, double distance) {
		double standardAngle = goal.getCameraAngle();

		double theta = Math.asin((Config.TOWER_HEIGHT - Config.ROBOT_HEIGHT) / distance);

		return -1;
	}

	public static void calculateVerticalFOV() {
		pixelsToGoal = (RESOLUTION.getWidth() / 2) / Math.tan(Math.toRadians(Config.horizontalFOV / 2));

		Config.verticalFOV = Math.toDegrees(RESOLUTION.getHeight() / 2 / pixelsToGoal) * 2;
		System.out.println(Config.verticalFOV);

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

	public boolean isDonePainting() {
		return donePainting;
	}

	public void setDonePainting(boolean donePainting) {
		this.donePainting = donePainting;
	}

	@Override
	public synchronized void paint(Graphics g) {
		Graphics bg = buffer.getGraphics();

		int xStart = getWidth() / 4;
		int yStart = (int) RESOLUTION.getHeight();

		double timeDifference = -1;

		bg.setColor(Color.BLACK);
		bg.fillRect(0, 0, getWidth(), getHeight());

		// Drawing the panel in the bottom
		bg.setColor(Color.GRAY);
		bg.fillRect(0, (int) RESOLUTION.getHeight() + 1, getWidth(), getHeight() - (int) RESOLUTION.getHeight());

		bg.setColor(Color.BLACK);
		for (int i = 0; i < 3; i++) {
			int xPos = xStart * (i + 1);
			bg.drawLine(xPos, (int) RESOLUTION.getHeight(), xPos, getHeight());

			bg.setColor(Color.BLACK);
			bg.setFont(new Font("Arial", Font.BOLD, 16));

			xPos = xStart * i + 10;
			bg.fillRect(xPos, yStart + 10, 20, 20);
			bg.drawString("Goal " + (i + 1), xPos + 50, yStart + 27);
		}

		// Gets image from camera and paints it to the buffer
		if (webcam != null) {
			cameraImg = webcam.getImage();
			timeDifference = System.nanoTime() - previousFrameTime;
			previousFrameTime = System.nanoTime();
			bg.drawImage(cameraImg, 0, 0, null);

		}

		int x1, y1, x2, y2;
		for (int i = 0; i < Main.goals.size(); i++) {

			int j = i;
			while (j > colorList.size())
				j = i % colorList.size();

			bg.setColor(colorList.get(j));
			// bg.setColor(Color.GREEN);
			bg.drawLine((int) Main.goals.get(i).getCenterLine().getX1(),
					(int) Main.goals.get(i).getCenterLine().getY1(), (int) Main.goals.get(i).getCenterLine().getX2(),
					(int) Main.goals.get(i).getCenterLine().getY2());

			// bg.setColor(Color.BLUE);
			bg.drawLine((int) Main.goals.get(i).getLeftLine().getX1(), (int) Main.goals.get(i).getLeftLine().getY1(),
					(int) Main.goals.get(i).getLeftLine().getX2(), (int) Main.goals.get(i).getLeftLine().getY2());

			// bg.setColor(Color.RED);
			bg.drawLine((int) Main.goals.get(i).getRightLine().getX1(), (int) Main.goals.get(i).getRightLine().getY1(),
					(int) Main.goals.get(i).getRightLine().getX2(), (int) Main.goals.get(i).getRightLine().getY2());

		}

		/* Draws distance of goal on the bottom left */
		// This can be later merged the for each loop that draws the lines. This
		// is temporary for readability
		bg.setFont(new Font("Arial", Font.BOLD, 12));

		for (int i = 0; i < Main.goals.size(); i++) {

			// bg.setColor(new Color(255, 255, 255, 150));

			/*
			 * int x = (int) Main.goals.get(i).getCenterLine().getX1() - 8; int
			 * y = (int) Main.goals.get(i).getCenterLine().getY1() + 15;
			 */
			int x = xStart * i + 10;
			int y = yStart + 50;

			// bg.fillRect(x - 3, y - 10, 100, 48);

			bg.setColor(colorList.get(i));
			int xPos = xStart * i + 10;
			bg.fillRect(xPos, yStart + 10, 20, 20);

			bg.setColor(Color.BLACK);

			String distance = String.format("%.2f", Main.goals.get(i).getGoalDistance());
			String distnaceToBase = String.format("%.2f", Main.goals.get(i).getGoalDistance());
			String angleDegrees = String.format("%.2f", Math.toDegrees(Main.goals.get(i).getAngleOfElevation()));
			String horizontalAngle = String.format("%.2f", Math.toDegrees(Main.goals.get(i).getHorizontalAngle()));

			System.out.println("Distance: " + distance + "in." + "    Elevation Angle: " + angleDegrees + "°"
					+ "     Horizontal Angle: " + horizontalAngle + "°");

			int interval = 15;
			bg.drawString("Distance: " + distance + "in.", x, y);
			bg.drawString("Elevation Angle: " + angleDegrees + "°", x, y += interval);
			bg.drawString("Horizontal Angle: " + horizontalAngle + "°", x, y += interval);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Paints data from the roborio if connection is established
		bg.setFont(new Font("Arial", Font.BOLD, 12));
		if (Main.client.isConnected()) {
			bg.setColor(Color.GREEN);
			bg.fillOval(489, 493, 10, 10);
			bg.drawString("Connected to Roborio", 510, 503);
			System.out.println(Main.client.send(bg));
		} else {
			bg.setColor(Color.RED);
			bg.fillOval(490, 493, 10, 10);
			bg.drawString("No Connection", 520, 503);
			// System.out.println("Paint");
		}

		// Draws frame rate
		bg.setColor(Color.GREEN);
		bg.setFont(new Font("Arial Narrow", Font.PLAIN, 14));
		/*
		 * double FPS = 1000000000 / timeDifference; String fpsOutput =
		 * String.format("%.2f", FPS);
		 */

		bg.drawString("FPS: " + Main.FPS, 2, 14);

		g.drawImage(buffer, 0, 0, null);

		donePainting = true;
	}

}
