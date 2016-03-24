package com.Team5427.testing;


import com.Team5427.Networking.client.Client;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class TestMain {

    public static Client client;

    public static void main(String[] args) {
        client = new Client("localhost", Client.DEFAULT_PORT);
        client.start();
        Scanner scanner = new Scanner(System.in);


        // TODO: Fix this, looks like spaghetti

        System.out.println("\t===Commands enabled, type \"help\" for help===");
        while (true) {
            String caseSelection = scanner.nextLine();
            String lowSelection = caseSelection.toLowerCase();

            if (lowSelection.equals("exit")) {
                break;
            } /*else if (lowSelection.equals("send")) {
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
                System.out.println("\n\tToo lazy to list out help commands, read the code
                :)");
            }*/

            else if (lowSelection.equals("send")) {
                System.out.print("Enter your message: ");

                String message = scanner.nextLine();

                boolean sent = client.send(message);

                System.out.println("Messaged has been sent successfully?: " + sent);
            }

            else if (caseSelection.equals("sendByte")) {
                // TODO: Fix the issue mentioned below
                System.out.println("WARNING! Bytes has to be sent or the program will crash");

                ArrayList<Byte> byteArrayList = new ArrayList<>();

                byte b;

                while (true) {
                    b = scanner.nextByte();

                    if (b != -128)
                        byteArrayList.add(b);
                    else
                        break;
                }

                // Converts array list to regular byte array
                byte[] buff = new byte[byteArrayList.size()];
                for (int i = 0; i < byteArrayList.size(); i++)
                    buff[i] = byteArrayList.get(i);

                boolean sent = client.send(buff);

                System.out.println("Byte array sent successfully?: " + sent);
            }

            // TODO: Finish this
            else if (caseSelection.equals("sendCommand")) {
                System.out.println("WARNING! Bytes has to be sent or the program will crash");
                System.out.print("Enter the byte command you want to send");

//                client.
            }

            else {
                System.err.println("\tInvalid command");
            }
        }
    }
}