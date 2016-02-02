package com.Team5427.VisionProcessing;

/**
 * 
 * @author Andrew Kennedy
 * 
 *         A simple class that is used to store all of the data from a line,
 *         including the points of both of the ends, as well as the length of
 *         the line. It also includes a built in method to determine whether or
 *         not two lines are close to each other.
 *
 */
public class Line {

	private double x1, y1, x2, y2;
	private double length;
	private boolean horizontal, vertical;

	public Line(double x1, double y1, double x2, double y2, double length) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.length = length;

		if (getSlope() > -.3 && getSlope() < .3)
			horizontal = true;
		else
			vertical = true;

	}

	public double compareTo(Line l) {
		double[] d = new double[4];
		double lowestDistance = 500;

		d[0] = Math.sqrt((x1 - l.getX1()) * (x1 - l.getX1()) + (y1 - l.getY1()) * (y1 - l.getY1()));

		d[1] = Math.sqrt((x2 - l.getX2()) * (x2 - l.getX2()) + (y2 - l.getY2()) * (y2 - l.getY2()));

		d[2] = Math.sqrt((x2 - l.getX1()) * (x2 - l.getX1()) + (y2 - l.getY1()) * (y2 - l.getY1()));

		d[3] = Math.sqrt((x1 - l.getX2()) * (x1 - l.getX2()) + (y1 - l.getY2()) * (y1 - l.getY2()));

		for (double distance : d) {
			if (distance < lowestDistance)
				lowestDistance = distance;
		}
		// System.out.println(lowestDistance);
		return lowestDistance;

	}

	public String toString() {
		return "Point 1:  (" + x1 + " , " + y1 + ")  Point 2:  (" + x2 + " , " + y2 + ")  Length:  " + length;

	}

	public double getX1() {
		return x1;
	}

	public double getY1() {
		return y1;
	}

	public double getX2() {
		return x2;
	}

	public double getY2() {
		return y2;
	}

	public double getMidpointY() {
		return (y1 + y2) / 2;
	}

	public double getLength() {
		return length;
	}

	public double getSmallestX() {
		if (x1 < x2)
			return x1;

		return x2;
	}

	public double getLargestX() {
		if (x1 > x2)
			return x1;

		return x2;
	}

	public double getLargestY() {
		if (y1 > y2)
			return y1;

		return y2;
	}

	public double getSlope() {

		if (x1 == x2)
			return 1 / Double.MAX_VALUE;

		return (y2 - y1) / (x2 - x1);
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public boolean isVertical() {
		return vertical;
	}

}