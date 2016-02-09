package com.Team5427.VisionProcessing;

public class Goal {

	public static final double MIN_VERTICAL_SLOPE = -5;
	public static final double MAX_VERTICAL_SLOPE = 5;
	public static final double MIN_HORIZONTAL_SLOPE = -1;
	public static final double MAX_HORIZONTAL_SLOPE = 1;

	/**
	 * The FOV of the attached webcam. It is used in calculating the distance to
	 * the goals.
	 */
	public static final double FOV = Main.CAMERA_FOV;

	// Measurements are in inches
	public static final double TRUE_GOAL_WIDTH = 20;
	public static final double TRUE_GOAL_HEIGHT = 14;
	// public static final double TOWER_HEIGHT = 97; // TOWER_HEIGHT is actually
	// the distance from the carpet to the center of the entire target, not the
	// bottom edge of the tape
	// TODO damn it Charlie, this makes no sense ^^
	/**
	 * Distance from the carpet to bottom of the tape on the goal.
	 */
	public static final double TOWER_HEIGHT = 83;
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

		if (setCenter = false)
			goalCompleted = false;
		else
			goalCompleted = true;

		area = leftLine.getLength() * centerLine.getLength();
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
	 * @return Whether or not the current goal is inside of the goal passed
	 *         through the parameters.
	 */
	public boolean isInsideGoal(Goal g) {
		if (g.leftLine.getSmallestX() > leftLine.getSmallestX()
				&& g.rightLine.getLargestY() < rightLine.getLargestX()) {

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

	public double getArea() {
		return area;
	}

	public boolean isGoalCompleted() {
		return goalCompleted;
	}

	public void setGoalCompleted(boolean goalCompleted) {
		this.goalCompleted = goalCompleted;
	}

	// TODO Charlie, document these two methods properly, i'm not which one is
	// which. They also have unclear names.
	public double getDistanceToRobot() {

		if (distanceToGoal > 0)
			return distanceToGoal;

		double verticalAvg = (leftLine.getLength() + rightLine.getLength()) / 2; // Averages
																					// vertical
																					// lengths
		double radAngle = Math.toRadians(FOV / 2);
		double pixelWidth = verticalAvg * TRUE_GOAL_WIDTH / TRUE_GOAL_HEIGHT;
		double verticalDistance = (VisionFrame.width / 2) * TRUE_GOAL_WIDTH / pixelWidth;

		distanceToGoal = verticalDistance / Math.tan(radAngle);

		return distanceToGoal;
	}

	public double getDistanceToTower() {
		if (distanceToTower > 0)
			return distanceToTower;

		distanceToTower = (TOWER_HEIGHT - ROBOT_HEIGHT) / Math.tan(getAngleOfELevationInRadians());

		return distanceToTower;
	}

	/**
	 * @return The angle of the robot to the goal in radians
	 */
	public double getAngleOfELevationInRadians() {

		if (angleOfElevation > 0)
			return angleOfElevation;

		angleOfElevation = Math.asin((TOWER_HEIGHT - ROBOT_HEIGHT) / getDistanceToRobot());

		return angleOfElevation;
	}

	/**
	 * @return the angle of the robot to the goal in degrees
	 */
	public double getAngleOfElevationInDegrees() {
		return Math.toDegrees(getAngleOfELevationInRadians());
	}
}