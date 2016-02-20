package com.Team5427.res;

/**
 * Made to store all of the variables that will be accessed in multiple classes
 * and will not be changed very often. All measurements are in inches.
 *
 */
public class Config {

	/**
	 * Minimum slope of a line in a goal for it to be considered as vertical.
	 */
	public static final double MIN_VERTICAL_SLOPE = -5;
	/**
	 * Maximum slope of a line in a goal for it to be considered as vertical.
	 */
	public static final double MAX_VERTICAL_SLOPE = 5;
	/**
	 * Minimum slope of a line in a goal for it to be considered as horizontal.
	 */
	public static final double MIN_HORIZONTAL_SLOPE = -1;
	/**
	 * Maximum slope of a line in a goal for it to be considered as horizontal.
	 */
	public static final double MAX_HORIZONTAL_SLOPE = 1;

	/**
	 * Determines whether or not the user will be able to calibrate the camera
	 * from the VisionPanel.
	 */
	public static final boolean ENABLE_FOV_CALIBRATION = true;
	/**
	 * Horizontal FOV of the attached Camera
	 */
//	public static double horizontalFOV = 55.689320368051696;
	public static double horizontalFOV = 67;
	/**
	 * Vertical FOV of the attached Camera
	 */
	public static double verticalFOV = 45.39860400495973;
	/**
	 * Angle at which the camera is mounted on the robot, in degrees.
	 */
	public static final double CAMERA_START_ANGLE = 0;
	/**
	 * Actual width of the goal.
	 */
	public static final double TRUE_GOAL_WIDTH = 20;
	/**
	 * Actual height of the goal.
	 */
	public static final double TRUE_GOAL_HEIGHT = 14;
	/**
	 * Elevation of the goal from the bottom of the reflective tape to the
	 * carpet.
	 */
	public static final double TOWER_HEIGHT = 85;
	/**
	 * Elevation of the camera from the carpet, at the point where it is
	 * attached to the robot.
	 */
	public static final double ROBOT_HEIGHT = 0;

}
