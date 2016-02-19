package com.Team5427.VisionProcessing;

public class Goal {

	// TODO remove this, for testing purposes only

	public static final double MIN_VERTICAL_SLOPE = -5;
	public static final double MAX_VERTICAL_SLOPE = 5;
	public static final double MIN_HORIZONTAL_SLOPE = -1;
	public static final double MAX_HORIZONTAL_SLOPE = 1;

	/**
	 * The FOV of the attached webcam. It is used in calculating the distance to
	 * the goals.
	 */
	public static double FOV = 58.157320136644095;
	// public static double FOV = Main.CAMERA_FOV;

	public static final boolean ENABLE_FOV_CALIBRATION = true; // Set this to
																// false if we
																// don't want to
																// calibrate the
																// FOV

	// Measurements are in inches
	public static final double TRUE_GOAL_WIDTH = 20;
	public static final double TRUE_GOAL_HEIGHT = 14;
	/**
	 * Distance from the carpet to bottom of the tape on the goal. This is the
	 * distance from the carpet, to the very bottom of the goal's opening
	 */
	public static final double TOWER_HEIGHT = 85;
	/**
	 * Height of the camera on the robot which must be accounted for while
	 * making calculations.
	 */
	public static final double ROBOT_HEIGHT = 0;

	private Line leftLine, centerLine, rightLine;

	private double distanceToGoal = -1;
	private double distanceToTower = -1;
	private double angleOfElevation = -1;
	private double area = -1;

	private boolean isValid = false, goalCompleted = false;

	/**
	 * Receives an Array of three lines, then determines which of the three
	 * lines is horiSzontal line, sets it as the horizontal line, and then
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

		if (!setCenter)
			goalCompleted = false;
		else
			goalCompleted = true;

		if ((goalCompleted && (rightLine.getLargestX() - leftLine.getLargestX()) > centerLine.getLength() / 1.5)) {
			area = leftLine.getLength() * centerLine.getLength();
			getDistanceToTower();
		} else
			goalCompleted = false;

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

	public double getArea() {
		return area;
	}

	public boolean isComplete() {
		return goalCompleted;
	}

	/**
	 * Calculates the width in inches from the center of the camera to the
	 * horizontal edge.* This is used to calculate the distance from the goal to
	 * the robot and to calibrate the FOV.
	 *
	 * @return vertical distance in inches from the center to the horizontal
	 *         edge of the camera. edge of the camera.
	 */
	public double getNormalizedVerticalDistance() {
		double verticalAvg = (leftLine.getLength() + rightLine.getLength()) / 2;
		double pixelWidth = verticalAvg * TRUE_GOAL_WIDTH / TRUE_GOAL_HEIGHT;
		return (VisionFrame.width / 2) * TRUE_GOAL_WIDTH / pixelWidth;
	}

	// TODO: Code this and add comments

	public double getNormalizedHorizontalDistance() {
		double horizontalAvg = (centerLine.getLength() + getTopLength()) / 2;
		return (VisionFrame.width / 2) * TRUE_GOAL_WIDTH / horizontalAvg;
	}

	/**
	 * Gets the distance from the goal to the robot
	 * 
	 * @return distance from robot to goal in inches
	 */
	public double getDistanceToGoal() {
		return distanceToGoal;
	}

	/**
	 * TODO: Needs testing, not sure if this works yet Determines if current
	 * goal is a valid for shooting
	 */

	public void determineValid() {
		if (leftLine.getMidpointY() > centerLine.getMidpointY() || rightLine.getMidpointY() < centerLine.getMidpointY())
			return;

		isValid = true;
	}

	/**
	 * Returns the distance from the goal to the robot
	 *
	 * @return the distance from goal to the robot
	 */
	public double getDistanceToRobot() {

		if (distanceToGoal > 0)
			return distanceToGoal;

		double radAngle = Math.toRadians(FOV / 2);
		double verticalDistance = getNormalizedHorizontalDistance();

		distanceToGoal = verticalDistance / Math.tan(radAngle);

		return distanceToGoal;
	}

	/**
	 * Gets the horizontal distance to the tower
	 * 
	 * @return distance from robot to tower
	 */
	public double getDistanceToTower() {
		if (distanceToTower > 0)
			return distanceToTower;

		distanceToTower = (TOWER_HEIGHT - ROBOT_HEIGHT) / Math.tan(getAngleOfElevationInRadians());

		return distanceToTower;
	}

	/**
	 * @return The angle of the robot to the goal in radians
	 */
	public double getAngleOfElevationInRadians() {

		if (angleOfElevation > 0)
			return angleOfElevation;

		angleOfElevation = Math.asin((TOWER_HEIGHT - ROBOT_HEIGHT) / getDistanceToRobot());
		// TODO that used to be getDistanceToGoal,not sure if changing it was
		// good
		return angleOfElevation;
	}

	/**
	 * @return the angle of the robot to the goal in degrees
	 */
	public double getAngleOfElevationInDegrees() {
		return Math.toDegrees(getAngleOfElevationInRadians());
	}

	/**
	 * TODO: Fix this, probably not working right now Gets the angle the robot
	 * has to aim in the horizontal axis in degrees
	 *
	 * @return horizontal angle in degrees from the center of the robot to the
	 *         goal. A negative angle represents that the goal is to the left
	 *         from the center of the robot. A positive angle represents that
	 *         the goal is to the right from the center of the robot.
	 */
	public double getHorizontalAngleInDegrees() {
		double halfResolution = VisionPanel.RESOLUTION.getWidth() / 2;
		double fromCenter = centerLine.getMidpointX() - halfResolution;

		return (FOV / 2 * fromCenter) / halfResolution;
	}

	/**
	 * Gets the angle the robot has to aim in the horizontal axis in radians
	 *
	 * @return horizontal angle in radians from the center of the robot to the
	 *         goal. A negative angle represents that the goal is to the left
	 *         from the center of the robot. A positive angle represents that
	 *         the goal is to the right from the center of the robot.
	 */
	public double getHorizontalAngleInRadians() {
		return Math.toRadians(getAngleOfElevationInDegrees());
	}

}