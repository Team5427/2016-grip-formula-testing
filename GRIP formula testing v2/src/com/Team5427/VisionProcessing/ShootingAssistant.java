package com.Team5427.VisionProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class ShootingAssistant {

	private static HashMap<Double, Double> distancePower;

	static {
		distancePower = new HashMap<Double, Double>();

		try {
			Scanner scan = new Scanner(new File("src/com/Team5427/VisionProcessing/DistanceToPower.dat"));
			Scanner currentLine;
			String s;
			while (scan.hasNextLine()) {
				s = scan.nextLine();
				if (!s.contains("*")) {
					currentLine = new Scanner(s).useDelimiter("=");

					distancePower.put(Double.parseDouble(currentLine.next()), Double.parseDouble(currentLine.next()));
				}
			}
			System.out.println(distancePower.toString());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static double getShootingPower(double distance) {
		String s = distance + "";
		

		return -1;
	}

}
