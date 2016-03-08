package com.Team5427.VisionProcessing;

import com.Team5427.res.Config;

public class Goal implements Comparable {

   private Line	 leftLine, centerLine, rightLine;

   private double	 distanceToGoal	= -1;
   private double	 distanceToTower	= -1;
   private double	 angleOfElevation	= -1;
   private double	 area			= -1;

   private boolean goalCompleted	= false;

   /**
    * Receives an Array of three lines, then determines which of the three lines
    * is horiSzontal line, sets it as the horizontal line, and then proceeds to
    * remove it from the Array. The remaining two lines then have their X values
    * compared in order to determine which of the remaining lines is the left
    * and which is the right. By the end of this constructor, there is no longer
    * an Array of lines, but instead the left, right, and center lines are all
    * set, in addition to the approximate area being calculated.
    * 
    * @param lines
    *           An array of three lines that will comprise the goal.
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
	   getGoalDistance();
	} else
	   goalCompleted = false;

   }

   /**
    * Calculates the angle of the goal from the robot to the top of the goal.
    * This does not take in account the starting angle of the camera.
    *
    * @return Angle from the robot to the top of the camera as viewed by the
    *         camera
    */
   public double getCameraAngle() {
	return Math.atan((VisionPanel.RESOLUTION.getHeight() / 2
		- (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2) / VisionPanel.pixelsToGoal);
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

	return Math.atan((VisionPanel.RESOLUTION.getHeight() / 2
		- (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2) / VisionPanel.pixelsToGoal)
		+ Math.toRadians(Config.CAMERA_START_ANGLE);

	/*
	 * return Math .atan(((leftLine.getMidpointY() + rightLine.getMidpointY())
	 * / 2 - VisionPanel.RESOLUTION.getHeight() / 2) /
	 * VisionPanel.pixelsToGoal) + Math.toRadians(Config.CAMERA_START_ANGLE);
	 */
   }

   /**
    * 
    * Determines whether or not the current goal is inside of another goal based
    * on whether the left and right line of the current goal are within the
    * bounds of the left and right lines of the goal that was given to it when
    * called.
    * 
    * @param g
    *           The goal which is potentially outside of the current goal.
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
    * Gets the angle the robot has to aim in the horizontal axis in radians
    *
    * @return horizontal angle in radians from the center of the robot to the
    *         goal. A negative angle represents that the goal is to the left
    *         from the center of the robot. A positive angle represents that the
    *         goal is to the right from the center of the robot.
    */
   public double getHorizontalAngle() {
	return Math
		.atan((centerLine.getMidpointX() - VisionPanel.RESOLUTION.getWidth() / 2) / VisionPanel.pixelsToGoal);
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
    * @deprecated Calculates the width in inches from the center of the camera
    *             to the horizontal edge.* This is used to calculate the
    *             distance from the goal to the robot and to calibrate the FOV.
    *
    * @return vertical distance in inches from the center to the horizontal edge
    *         of the camera. edge of the camera.
    */
   public double getNormalizedVerticalDistance() {
	double verticalAvg = (leftLine.getLength() + rightLine.getLength()) / 2;
	double pixelWidth = verticalAvg * Config.TRUE_GOAL_WIDTH / Config.TRUE_GOAL_HEIGHT;
	return (VisionFrame.width / 2) * Config.TRUE_GOAL_WIDTH / pixelWidth;
   }

   // TODO: Code this and add comments

   /**
    * @deprecated
    * @return
    */
   public double getNormalizedHorizontalDistance() {
	double horizontalAvg = (centerLine.getLength() + getTopLength()) / 2;
	return (VisionFrame.width / 2) * Config.TRUE_GOAL_WIDTH / horizontalAvg;
   }

   /**
    * Returns the distance between the robot to the center of the goal in inches
    * 
    * @return Returns the distance between the robot to the center of the goal
    *         in inches
    */
   public double getGoalDistance() {
	return (Config.TRUE_GOAL_HEIGHT + Config.TOWER_HEIGHT - Config.ROBOT_HEIGHT)
		/ Math.sin(getAngleOfElevation());
   }

   @Override
   public int compareTo(Object o) {
	if (o instanceof Goal) {
	   if (area > ((Goal) o).getArea())
		return 1;
	   else return 0;
	}
	return -1;
   }

   /**
    * @deprecated Gets the distance from the goal to the robot
    * 
    * @return distance from robot to goal in inches
    */
   public double getDistanceToGoal() {
	return distanceToGoal;
   }

   /**
    * @deprecated Returns the distance from the goal to the robot
    *
    * @return the distance from goal to the robot
    */
   public double getDistanceToRobot() {

	if (distanceToGoal > 0)
	   return distanceToGoal;

	double radAngle = Math.toRadians(Config.horizontalFOV / 2);
	double verticalDistance = getNormalizedVerticalDistance();

	distanceToGoal = verticalDistance / Math.tan(radAngle);

	return distanceToGoal;
   }

   /**
    * @deprecated Gets the horizontal distance to the tower
    * 
    * @return distance from robot to tower
    */
   public double getDistanceToTower() {
	if (distanceToTower > 0)
	   return distanceToTower;

	distanceToTower = (Config.TOWER_HEIGHT - Config.ROBOT_HEIGHT) / Math.tan(getAngleOfElevationInRadians());

	return distanceToTower;
   }

   /**
    * @deprecated
    * @return The angle of the robot to the goal in radians
    */
   public double getAngleOfElevationInRadians() {

	if (angleOfElevation > 0)
	   return angleOfElevation;

	angleOfElevation = Math.asin((Config.TOWER_HEIGHT - Config.ROBOT_HEIGHT) / getDistanceToRobot());
	// TODO that used to be getDistanceToGoal,not sure if changing it was
	// good
	return angleOfElevation;
   }

   /**
    * @deprecated
    * @return the angle of the robot to the goal in degrees
    */

   public double getAngleOfElevationInDegrees() {
	return Math.toDegrees(getAngleOfElevationInRadians());
   }

   /**
    * @deprecated TODO: Fix this, probably not working right now Gets the angle
    *             the robot has to aim in the horizontal axis in degrees
    *
    * @return horizontal angle in degrees from the center of the robot to the
    *         goal. A negative angle represents that the goal is to the left
    *         from the center of the robot. A positive angle represents that the
    *         goal is to the right from the center of the robot.
    */
   public double getHorizontalAngleInDegrees() {
	double halfResolution = VisionPanel.RESOLUTION.getWidth() / 2;
	double fromCenter = centerLine.getMidpointX() - halfResolution;

	return (Config.horizontalFOV / 2 * fromCenter) / halfResolution;
   }

}