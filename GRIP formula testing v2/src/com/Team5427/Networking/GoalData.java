package com.Team5427.Networking;

import com.Team5427.VisionProcessing.Goal;

/**
 * The object that will be sent from the driver station and will be received by
 * the robot. This class only contains data that may be useful for the robot.
 */
public class GoalData {

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

    public GoalData(Goal goal) {
	distance = goal.getGoalDistance();
	angleOfElevation = goal.getAngleOfElevation();
	horizontalAngle = goal.getHorizontalAngle();
    }

    public GoalData(double distance, double angleOfElevation, double horizontalAngle) {
	this.distance = distance;
	this.angleOfElevation = angleOfElevation;
	this.horizontalAngle = horizontalAngle;
    }

    public GoalData(String s) {

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

    /**
     * The toString use by the network to identify the class type
     *
     * @return the class type for networking use
     */
    public String toString() {
	return "Team 5427 - GoalData";
    }
}