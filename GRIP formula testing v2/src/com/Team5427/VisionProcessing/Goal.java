package com.Team5427.VisionProcessing;

import java.util.ArrayList;

public class Goal {

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

	private Line temp1, temp2;

	private Line[] lines;

	private double distanceToGoal = -1;
	private double distanceToTower = -1;
	private double angleOfElevation = -1;
	private double area = -1;

	private boolean goalCompleted = false;
	private boolean isValid = false;

	/**
	 * Used when only two of the three lines that will comprise the Goal are
	 * known.
	 * 
	 * @param temp1
	 *            One of the two lines, with no designation as to whether it is
	 *            the left, right, or bottom line in the U shaped Goal.
	 * @param temp2
	 *            One of the two lines, with no designation as to whether it is
	 *            the left, right, or bottom line in the U shaped Goal.
	 */
	public Goal(Line temp1, Line temp2) {
		this.temp1 = temp1;
		this.temp2 = temp2;
		System.out.println("made a Goal!!"); // TODO delete when done
	}

	/**
	 * Used when all three of the lines that will comprise a Goal are known.
	 * 
	 * @param leftLine
	 * @param centerLine
	 * @param rightLine
	 */
	/*
	 * public Goal(Line leftLine, Line centerLine, Line rightLine) {
	 * this.leftLine = leftLine; this.centerLine = centerLine; this.rightLine =
	 * rightLine;
	 * 
	 * Line[] newLines = new Line[3];
	 * 
	 * newLines[0] = leftLine; newLines[1] = rightLine; newLines[2] =
	 * centerLine;
	 * 
	 * this.lines = newLines;
	 * 
	 * goalCompleted = true; }
	 */

	public Goal(Line[] lines) {
		// System.out.println("making a Goal"); // TODO remove once the program
		// is
		// working
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

//			System.out.println(vertLines[0].toString() + "    \n    " + vertLines[1].toString() + "\n\n");

			// System.out.println("the left line is: "+
			// vertLines[0].getSmallestX()+", and the right line is: "+
			// vertLines[1].getLargestX());
		} else {
			leftLine = vertLines[1];
			rightLine = vertLines[0];
			
		}

		if (setCenter = false)
			goalCompleted = false;
		else
			goalCompleted = true;
		
		area = leftLine.getLength()*centerLine.getLength();

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

	public double getArea() {
		return area;
	}

	public Line[] getLines() {
		return lines;
	}

	public Line getLeftLine() {
		return leftLine;
	}

	public void setLeftLine(Line leftLine) {
		this.leftLine = leftLine;
	}

	public Line getCenterLine() {
		return centerLine;
	}

	public void setCenterLine(Line centerLine) {
		this.centerLine = centerLine;
	}

	public Line getRightLine() {
		return rightLine;
	}

	public void setRightLine(Line rightLine) {
		this.rightLine = rightLine;
	}

	public Line getTemp1() {
		return temp1;
	}

	public void setTemp1(Line temp1) {
		this.temp1 = temp1;
	}

	public Line getTemp2() {
		return temp2;
	}

	public void setTemp2(Line temp2) {
		this.temp2 = temp2;
	}

	public boolean isGoalCompleted() {
		return goalCompleted;
	}

	public void setGoalCompleted(boolean goalCompleted) {
		this.goalCompleted = goalCompleted;
	}

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

		distanceToTower = (TOWER_HEIGHT - ROBOT_HEIGHT) / Math.tan(getAngleElevationRadians());

		return distanceToTower;
	}

	/**
	 * Returns the angle of elevation from the target to the robot in radians
	 * 
	 * @return the angle of the robot to the goal in radians
	 */
	public double getAngleElevationRadians() {

		if (angleOfElevation > 0)
			return angleOfElevation;

		// System.err.println(TOWER_HEIGHT);
		// System.err.println(ROBOT_HEIGHT);
		// System.err.println(getDistanceToRobot());

		angleOfElevation = Math.asin((TOWER_HEIGHT - ROBOT_HEIGHT) / getDistanceToRobot());

		return angleOfElevation;
	}

	/**
	 * Returns the angle of elevation from the target to the robot in degrees
	 * 
	 * @return the angle of the robot to the goal in degrees
	 */
	public double getAngleElevationDegrees() {
		return Math.toDegrees(getAngleElevationRadians());
	}
}