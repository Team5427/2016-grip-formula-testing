package com.Team5427.VisionProcessing;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.networktables.*;

public class Main {

	// TODO redo documentation when finished and methods have changed.

	public static final double CAMERA_FOV = 74;
	public static NetworkTable table;
	static double[] x1Values = new double[20];
	static double[] y1Values = new double[20];
	static double[] x2Values = new double[20];
	static double[] y2Values = new double[20];
	static double[] lengthValues = new double[20];
	static ArrayList<Line> lines = new ArrayList<Line>();
	static ArrayList<Goal> goals = new ArrayList<Goal>();

	private final static int lowestAcceptableValue = 10; // for determining
															// which
															// line is closest

	static VisionFrame vf;

	public static void main(String[] args) {
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress("localhost");
		table = NetworkTable.getTable("GRIP/myLinesReport");
		vf = new VisionFrame();

		setValues();

		while (true) {
			try {
				
				setValues(); // no modifications needed

				createLines(); // no modifications needed

				findGoals(); // no modifications needed

				filterGoals(); // major modifications needed (reverse it)

				Thread.sleep(100);

				vf.getPanel().repaint();

				// Thread.sleep(100);

				lines.clear();
				goals.clear();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * This method goes through the entire ArrayList of lines that is given to
	 * the program by GRIP, and is used to determine which, if any lines are
	 * touching the given line. If there are multiple, then it will only return
	 * the one that is first in the ArrayList.
	 * 
	 * 
	 * @param l
	 *            The line that you would like to compare to the rest of the
	 *            ArrayList
	 * @return The first line in the ArrayList of lines that is close to the
	 *         current line
	 */
	public static Line isClose(Line l) {
		int index = -1;
		double lowestValue = lowestAcceptableValue;

		for (int i = 0; i < lines.size(); i++) {

			if (l.isHorizontal() != lines.get(i).isHorizontal()) {

				double distance = l.compareTo(lines.get(i));
				if (distance <= lowestValue) {
					index = i;
					lowestValue = distance;
				}
			}
		}
		if (index >= 0)
			return lines.remove(index);
		else
			return null;
	}

	/**
	 * 
	 * This method goes through the entire ArrayList of lines that is given to
	 * the program by GRIP, and is used to determine which, if any lines are
	 * touching the given lines. If there are multiple, then it will only return
	 * the one that is first in the ArrayList.
	 * 
	 * @param l1
	 *            One of the line that you would like to compare to the rest of
	 *            the ArrayList
	 * @param l2
	 *            One of the line that you would like to compare to the rest of
	 *            the ArrayList
	 * @return An array of the two lines given, plus the third line that was
	 *         found
	 */
	public static Line[] isClose(Line l1, Line l2) {

		int index = -1;
		double lowestValue = lowestAcceptableValue;
		boolean needHorizontal;

		// determines whether a vertical or horizontal line is needed in order
		// to complete the goal
		if (l1.isHorizontal() || l2.isHorizontal())
			needHorizontal = false;
		else
			needHorizontal = true;

		for (int i = 0; i < lines.size(); i++) {
			if (needHorizontal == lines.get(i).isHorizontal()) {

				double d = returnLowestDouble(l1.compareTo(lines.get(i)), l2.compareTo(lines.get(i)));

				if (d < lowestValue) {
					index = i;
					lowestValue = d;
				}

			}

		}

		if (index >= 0)
			return new Line[] { l1, l2, lines.remove(index) };
		else
			return null;

	}

	/**
	 * Retrieves all of the data from the network table in the form of arrays
	 */
	private static void setValues() {
		x1Values = table.getNumberArray("x1", x1Values);
		y1Values = table.getNumberArray("y1", y1Values);
		x2Values = table.getNumberArray("x2", x2Values);
		y2Values = table.getNumberArray("y2", y2Values);
		lengthValues = table.getNumberArray("length", lengthValues);

		if (x1Values.length != y1Values.length || x1Values.length != x2Values.length || x1Values.length != y2Values.length)			// Prevents index out of bounds when data is being gathered
			setValues();
	}

	/**
	 * Takes all of the data previously gotten from the NetworkTable, and turns
	 * it into an ArrayList of lines.
	 */
	private static void createLines() {
		for (int i = 0; i < lengthValues.length; i++) {
			if (lengthValues[i] != 0)
				lines.add(new Line(x1Values[i], y1Values[i], x2Values[i], y2Values[i], lengthValues[i]));
		}
	}

	/**
	 * Iterates through the ArrayList of lines, and if two are found to have
	 * ends less than two pixels away from each other, then it will remove both
	 * of the lines from the ArrayList, and proceed to add both of them to a new
	 * Goal, leaving the third line of the Goal to be fixed later.
	 */
	private static void findGoals() {
		for (int i = 0; i < lines.size() - 1;) {
			Line l = isClose(lines.get(i));
			if (l == null)
				lines.remove(i);
			else {
				Line[] temp = isClose(l, lines.remove(i));
				// System.out.println(temp);
				if (temp != null)
					goals.add(new Goal(temp));
			}

		}
	}

	private synchronized static void filterGoals() {
		for (int index = 0; index<goals.size(); index++) {
			Goal g = goals.get(index);
			for (int i = 0; i < goals.size(); i++) {
				if (g.isInsideGoal(goals.get(i))) {
					goals.remove(i);
					i--;
				}
			}
		}
		
	}

	private static double returnLowestDouble(double d1, double d2) {
		if (d1 < d2)
			return d1;
		else
			return d2;

	}

	public void filterHorizontalLines() {
		lines = getHorizontalLines();
	}

	public ArrayList<Line> getHorizontalLines() {
		ArrayList<Line> horizontalLines = new ArrayList<>();

		for (Line l:lines) {
			if (l.isHorizontal())
				horizontalLines.add(l);
		}

		return horizontalLines;
	}
}
