package com.Team5427.testing;

import com.Team5427.Networking.Task;
import com.Team5427.Networking.TaskDescription;
import com.Team5427.Networking.client.Client;

import java.util.Scanner;

public class TestMain {

	public static Client client;
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		client = new Client("localhost", Client.DEFAULT_PORT);
		client.start();

		// TODO: Fix this, looks like spaghetti

		System.out.println("\t===Commands enabled, type \"help\" for help===");
		while (true) {
			String caseSelection = scanner.nextLine();
			String lowSelection = caseSelection.toLowerCase();
			System.out.println(lowSelection);

			if (lowSelection.equals("exit")) {
				break;
			} else if (lowSelection.equals("send")) {
				System.out.println("\tTypes to send: MESSAGE");
				caseSelection = scanner.nextLine();

				if (caseSelection.equalsIgnoreCase("message")) {
					System.out.println("\tEnter your message:");
					String stringMessage = scanner.nextLine();

					boolean sent = client.send(new Task(TaskDescription.MESSAGE, stringMessage));

					if (sent)
						System.out.println("\tMessage sent successfully");
					else
						System.err.println("\tMessage failed to deliver");
				}
			} else if (lowSelection.equals("autostart")) {
				client.send(new Task(TaskDescription.AUTO_START));
				System.out.println("\tSent AUTO_START command to driver station");
			} else if (lowSelection.equals("teleopstart")) {
				client.send(new Task(TaskDescription.TELEOP_START));
				System.out.println("\tsent TELEOP_START command to driver station");
			} else if (lowSelection.equals("help")) {
				System.out.println("\n\tToo lazy to list out help commands, read the code :)");
			}
			else {
				System.err.println("\tInvalid command");
			}
		}
	}
}