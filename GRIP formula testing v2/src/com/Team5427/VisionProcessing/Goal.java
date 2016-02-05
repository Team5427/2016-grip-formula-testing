package com.Team5427.VisionProcessing;

public class Goal {

	// TODO remove this, for testing purposes only

	public static final double MIN_VERTICAL_SLOPE = -5;
	public static final double MAX_VERTICAL_SLOPE = 5;
	public static final double MIN_HORIZONTAL_SLOPE = -1;
	public static final double MAX_HORIZONTAL_SLOPE = 1;

	public static final double FOV = 51.5;

	// Measurements are in inches
	public static final double TRUE_GOAL_WIDTH = 20;
	public static final double TRUE_GOAL_HEIGHT = 14;
	// public static final double TOWER_HEIGHT = 97; // TOWER_HEIGHT is actually
	// the distance from the carpet to the center of the entire target, not the
	// bottom edge of the tape
	public static final double TOWER_HEIGHT = 83; // TOWER_HEIGHT is distance
													// from carpet to tape
	public static final double ROBOT_HEIGHT = 0; // Height of the robot has to
													// be accounted during angle
													// of elevation calculation

	private Line leftLine, centerLine, rightLine;

	private double distanceToGoal = -1;
	private double distanceToTower = -1;
	private double angleOfElevation = -1;
	private double area = -1;

	private boolean goalCompleted = false;

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

<<<<<<< HEAD
			// System.out.println(vertLines[0].toString() + " \n " +
			// vertLines[1].toString() + "\n\n");

			// System.out.println("the left line is: "+
			// vertLines[0].getSmallestX()+", and the right line is: "+
			// vertLines[1].getLargestX());
=======
>>>>>>> c5def68609f4c11cf9ddad47dd6525745033781f
		} else {

			leftLine = vertLines[1];
			rightLine = vertLines[0];

		}

		if (setCenter = false)
			goalCompleted = false;
		else
			goalCompleted = true;
<<<<<<< HEAD

		area = leftLine.getLength() * centerLine.getLength();

		/*
		 * filterVerticalLines(lines); filterHorizontalLines(lines);
		 * 
		 * Line[] newLines = new Line[3];
		 * 
		 * newLines[0] = leftLine; newLines[1] = rightLine; newLines[2] =
		 * centerLine;
		 * 
		 * this.lines = newLines;
		 * 
		 * goalCompleted = true;
		 */
	}
=======
>>>>>>> c5def68609f4c11cf9ddad47dd6525745033781f

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

	/**
	 * Method to access the horizontal line in the goal that
	 * 
	 * @return The center line of the goal.
	 */
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

<<<<<<< HEAD
	public boolean isInsideGoal(Goal g) {
		if (g.leftLine.getSmallestX() > leftLine.getSmallestX()
				&& g.rightLine.getLargestY() < rightLine.getLargestX()) {

			return true;

		}
		return false;
	}

	/**
	 * Filters the vertical lines to appropriate left and right line for the
	 * goal
	 *
	 * @param lineList
	 *            Lines within the goal
	 */
	public void filterVerticalLines(Line[] lineList) {

		ArrayList<Line> verticalLines = new ArrayList<>();

		/* Filters all vertical lines that meets the criteria */
		for (Line l : lineList) {
			double slope = l.getSlope();
			// System.out.println("Slope of line: " + slope + " - is horizontal:
			// " + (MIN_HORIZONTAL_SLOPE < slope && MAX_HORIZONTAL_SLOPE >
			// slope));

			if (MIN_VERTICAL_SLOPE > slope || MAX_VERTICAL_SLOPE < slope)
				verticalLines.add(l);
		}

		/* Finds the left line */
		leftLine = verticalLines.get(0);
		for (int i = 1; i < verticalLines.size(); i++) {
			if (verticalLines.get(i).getSmallestX() < leftLine.getSmallestX())
				leftLine = verticalLines.get(i);
		}

		/* Finds the right line */
		rightLine = verticalLines.get(0);
		for (int i = 1; i < verticalLines.size(); i++) {
			if (verticalLines.get(i).getLargestX() > rightLine.getLargestX())
				rightLine = verticalLines.get(i);
		}

	}

	/**
	 * Finds the bottom line of the target and sets it as the goal's bottom line
	 *
	 * @param lineList
	 *            lines within the goal
	 */
	public void filterHorizontalLines(Line[] lineList) {

		// System.out.println(lineList.length);

		try {

			ArrayList<Line> horizontalLines = new ArrayList<>();

			/* Filters all vertical lines that meets the criteria */
			for (Line l : lineList) {
				// System.out.println(l);
				double slope = l.getSlope();

				// System.out.println("Slope of line: " + slope + " - is
				// horizontal: " + (MIN_HORIZONTAL_SLOPE < slope &&
				// MAX_HORIZONTAL_SLOPE > slope));

				if (MIN_HORIZONTAL_SLOPE < slope || MAX_HORIZONTAL_SLOPE > slope)
					horizontalLines.add(l);
			}

			/* Finds the bottom line */
			centerLine = horizontalLines.get(0);
			for (int i = 1; i < horizontalLines.size(); i++) {
				if (horizontalLines.get(i).getLargestY() > centerLine.getLargestY())
					centerLine = horizontalLines.get(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (centerLine == null)
			System.err.println("Whoops! centerline is null");
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
=======
>>>>>>> c5def68609f4c11cf9ddad47dd6525745033781f
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