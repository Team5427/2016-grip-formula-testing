package com.Team5427.Networking;

import java.io.Serializable;

import com.Team5427.VisionProcessing.Goal;

import java.io.Serializable;

/**
 * The object that will be sent from the driver station and will be received by
 * the robot. This class only contains data that may be useful for the robot.
 */
public class GoalData implements Serializable {

	/**
	 * Distance between the robot and the goal. The value is
	 */
	private double distance;
	/**
	 * The angle of elevation from the robot to the goal
	 */
	private double angleOfElevation;
	/**
	 * The horizontal angle from the camera to the
	 */
	private double horizontalAngle;
	/**
	 * The value that the motor needs to be set at for the given distance
	 */
	private double motorValue;

	public GoalData(double distance, double angleOfElevation, double horizontalAngle, double motorValue) {
		this.distance = distance;
		this.angleOfElevation = angleOfElevation;
		this.horizontalAngle = horizontalAngle;
		this.motorValue = motorValue;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getAngleOfElevation() {
		return angleOfElevation;
	}

	public void setAngleOfElevation(double angleOfElevation) {
		this.angleOfElevation = angleOfElevation;
	}

	public double getHorizontalAngle() {
		return horizontalAngle;
	}

	public void setHorizontalAngle(double horizontalAngle) {
		this.horizontalAngle = horizontalAngle;
	}

	public double getMotorValue() {
		return motorValue;
	}

	/**
	 * The toString use by the network to identify the class type
	 *
	 * @return the class type for networking use
	 */
	public String toString() {
		return "Team 5427 - GoalData";
	}
}