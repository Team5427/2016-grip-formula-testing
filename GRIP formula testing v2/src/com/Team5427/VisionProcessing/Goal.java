package com.Team5427.VisionProcessing;

import com.Team5427.Networking.ByteDictionary;
import com.Team5427.res.Config;

import java.nio.ByteBuffer;

@SuppressWarnings("rawtypes")
public class Goal implements Comparable {

	private Line leftLine, centerLine, rightLine;

	private double cameraDistanceToGoal = Double.MIN_VALUE;
	private double cameraDistanceToTower = Double.MIN_VALUE;
	private double angleOfElevation = Double.MIN_VALUE;
	private double cameraXAngle = Double.MIN_VALUE;
	private double turretDistanceToGoal = Double.MIN_VALUE;
	private double turretXAngle = Double.MIN_VALUE;
	private double area = -1;

	private boolean goalCompleted = false;

	/**
	 * Receives an Array of three lines, then determines which of the three
	 * lines is horizontal line, sets it as the horizontal line, and then
	 * proceeds to remove it from the Array. The remaining two lines then have
	 * their X values compared in order to determine which of the remaining
	 * lines is the left and which is the right. By the end of this constructor,
	 * there is no longer an Array of lines, but instead the left, right, and
	 * center lines are all set, in addition to the approximate area being
	 * calculated.
	 * 
	 * @param lines
	 *            An array of three lines that will comprise the goal.
	 */
	public Goal(Line[] lines) {
		Line[] vertLines = new Line[2];

		int index = 0;

		boolean setCenter = false;
		;

		for (int i = 0; i < lines.length; i++) {
			if (lines[i].isHorizontal()) {

				centerLine = lines[i];
				setCenter = true;

			} else
				vertLines[index++] = lines[i];
		}

		if (vertLines[0].getSmallestX() < vertLines[1].getLargestX()) {

			leftLine = vertLines[0];
			rightLine = vertLines[1];

		} else {

			leftLine = vertLines[1];
			rightLine = vertLines[0];

		}

		// System.out.println(getAngleOfELevation());

		if (!setCenter)
			goalCompleted = false;
		else
			goalCompleted = true;

		if ((goalCompleted && (rightLine.getLargestX() - leftLine.getLargestX()) > centerLine.getLength() / 1.5)) {
			area = leftLine.getLength() * centerLine.getLength();
			getGoalDistanceCamera();
		} else
			goalCompleted = false;

	}

	/**
	 * NOTE: This should not be used outside of the goal class
	 *
	 * Calculates the angle of the goal from the robot to the top of the goal.
	 * This does not take in account the starting angle of the camera.
	 *
	 * @return Angle from the robot to the top of the camera as viewed by the
	 *         camera in radians
	 */
	protected double getCameraAngleY() {
		return Math.atan(
				(VisionPanel.RESOLUTION.getHeight() / 2 - (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2)
						/ VisionPanel.pixelsToGoal);
	}

	/**
	 * Calculates the angle of elevation from the robot to the top of the goal.
	 * It utilizes the vertical FOV in order to determine the angle.
	 * 
	 * @return the angle from the camera mounted on the robot, to the top of the
	 *         goal.
	 */
	public double getAngleOfElevation() {
		/*
		 * System.out.println((VisionPanel.RESOLUTION.getHeight() / 2 -
		 * (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2));
		 */

		if (angleOfElevation == Double.MIN_VALUE)
			angleOfElevation = Math
					.atan((VisionPanel.RESOLUTION.getHeight() / 2
							- (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2) / VisionPanel.pixelsToGoal)
					+ Math.toRadians(Config.CAMERA_START_ANGLE);

		return angleOfElevation;

		/*
		 * return Math .atan(((leftLine.getMidpointY() +
		 * rightLine.getMidpointY()) / 2 - VisionPanel.RESOLUTION.getHeight() /
		 * 2) / VisionPanel.pixelsToGoal) +
		 * Math.toRadians(Config.CAMERA_START_ANGLE);
		 */
	}

	/**
	 * 
	 * Determines whether or not the current goal is inside of another goal
	 * based on whether the left and right line of the current goal are within
	 * the bounds of the left and right lines of the goal that was given to it
	 * when called.
	 * 
	 * @param g
	 *            The goal which is potentially outside of the current goal.
	 *
	 * @return Whether or not the current goal is inside of the goal passed
	 *         through the parameters.
	 */
	public boolean isInsideGoal(Goal g) {
		if (g.leftLine.getSmallestX() > leftLine.getSmallestX()
				&& g.rightLine.getLargestX() < rightLine.getLargestX()) {

			return true;

		}
		return false;
	}

	/**
	 * Returns the length between the pixel length of the top side of the goal
	 *
	 * @return pixel length of the the top side of the goal
	 */
	public double getTopLength() {
		double x1 = leftLine.getTopPointX();
		double y1 = leftLine.getTopPointY();
		double x2 = rightLine.getTopPointX();
		double y2 = rightLine.getTopPointY();

		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	/**
	 * Note: This should not be used outside of the goal class
	 *
	 * Gets the angle the robot has to aim in the x axis in radians
	 *
	 * @return x angle in radians from the center of the robot to the goal. A
	 *         negative angle represents that the goal is to the left from the
	 *         center of the robot. A positive angle represents that the goal is
	 *         to the right from the center of the robot.
	 */
	protected double getCameraXAngle() {
		if (cameraXAngle == Double.MIN_VALUE)
			cameraXAngle = Math.atan((centerLine.getMidpointX() - VisionPanel.RESOLUTION.getWidth() / 2) / VisionPanel.pixelsToGoal);
		

		return cameraXAngle;
	}

	/**
	 * Returns the angle from the center of the turret to the goal in radians in
	 * the x axis
	 *
	 * @return Returns the angle from the center of the turret to the goal in
	 *         radians in the x axis
	 */
	public double getTurretXAngle() {
		if (Config.CAMERA_TURRET_DISTANCE == 0) {
			return getCameraXAngle();
		} else if (turretXAngle == Double.MIN_VALUE) {
			double B = Math.acos((Math.pow(getGoalDistanceCamera(), 2) - Math.pow(Config.CAMERA_TURRET_DISTANCE, 2)
					- Math.pow(getGoalDistanceTurret(), 2))
					/ (-2 * Config.CAMERA_TURRET_DISTANCE * getGoalDistanceTurret()));

			turretXAngle = Math.PI / 2 - B;
		}

		return turretXAngle;
	}

	public Line getCenterLine() {
		return centerLine;
	}

	public void setCenterLine(Line centerLine) {
		this.centerLine = centerLine;
	}

	public Line getLeftLine() {
		return leftLine;
	}

	public void setLeftLine(Line leftLine) {
		this.leftLine = leftLine;
	}

	public Line getRightLine() {
		return rightLine;
	}

	public void setRightLine(Line rightLine) {
		this.rightLine = rightLine;
	}

	public double getArea() {
		return area;
	}

	public boolean isComplete() {
		return goalCompleted;
	}

	/**
	 * Returns the distance between the turret to the center of the goal in
	 * inches
	 *
	 * @return Returns the distance between the turret to the center of the goal
	 *         in inches
	 */
	public double getGoalDistanceTurret() {
		if (Config.CAMERA_TURRET_DISTANCE == 0) {
			return getGoalDistanceCamera();
		} else if (turretDistanceToGoal == Double.MIN_VALUE) {
			turretDistanceToGoal = Math.sqrt(Math.pow(Config.CAMERA_TURRET_DISTANCE, 2)
					+ Math.pow(getGoalDistanceCamera(), 2) - 2 * Config.CAMERA_TURRET_DISTANCE * getGoalDistanceCamera()
							* Math.cos(getCameraXAngle() + Math.PI / 2));
		}

		return turretDistanceToGoal;
	}

	/**
	 * NOTE: This should not be used outside of the goal class
	 *
	 * Returns the distance between the camera to the center of the goal in
	 * inches
	 * 
	 * @return Returns the camera between the robot to the center of the goal in
	 *         inches
	 */
	protected double getGoalDistanceCamera() {

		if (cameraDistanceToGoal == Double.MIN_VALUE)
			cameraDistanceToGoal = (Config.TRUE_GOAL_HEIGHT + Config.TOWER_HEIGHT - Config.ROBOT_HEIGHT)
					/ Math.sin(getAngleOfElevation());

		return cameraDistanceToGoal;
	}

	/**
	 * TODO: Replace hard code, index 0 to indicate data type is probably wrong
	 * 		 Replace index 0 with whatever value is in the ByteDictionary.
	 * Returns a byte array of the goal data
	 *
	 * @return byte array of goal data
     */
	public byte[] getByteBuffer(double motorValue) {
		byte[] buff = new byte[17];

		/* Begin writing the data */
		// Indicates the data type
		buff[0] = ByteDictionary.GOAL_ATTACHED;

		// Converts distance to byte array
		ByteBuffer.wrap(buff, 1, 8).putDouble(motorValue);

		// Converts vertical angle to byte array
		ByteBuffer.wrap(buff, 9, 8).putDouble(getTurretXAngle());


		return buff;
	}

	// TODO make this print out values to make a new goalData.
	public String toString() {
		return null;

	}

	/**
	 * used to compare the area of two goals to each other.
	 * 
	 * @param o
	 *            a goal to be compared to the current goal.
	 * 
	 * @return 1 if the current goal is larger than the one given, 0 if it is
	 *         not, and -1 if the object given is n ot an instance of a goal.
	 */
	@Override
	public int compareTo(Object o) {
		if (o instanceof Goal) {
			if (area > ((Goal) o).getArea())
				return 1;
			else
				return 0;
		}
		return -1;
	}

}