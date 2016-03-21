package com.Team5427.Networking;

import com.Team5427.VisionProcessing.VisionPanel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Server {

	private static Socket connection = null;
	private static ServerSocket serverSocket;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;
	private static OutputStream byteOutStream;
	private static InputStream byteInputStream;

	private static final int PORT     = 25565;
	public static int MAX_BYTE_BUFFER = 256;

	/**
	 * Sends a byte array over the network
	 *
	 * @param buff The buffer to be send
	 * @return true if sent successfully, false if otherwise
     */
	public static synchronized boolean send(byte[] buff) {
		if (hasConnection()) {
			try {
				out.write(buff);
				out.reset();
				out.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Sends String over the network
	 *
	 * @param s The string to be sent over the network
	 * @return true if sent successfully, false if otherwise
     */
	public static synchronized boolean send(String s) {
		if (hasConnection()) {
			try {
				out.writeObject(s);
				out.reset();
				out.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}


	/**
	 * Call whenever sending something to the client in order to check whether
	 * or not it is connected to the server.
	 * 
	 * @return whether the client is connected.
	 */
	public static boolean hasConnection() {
		return (connection != null && !connection.isClosed());
	}

	public static synchronized void reset() {
		try {
			connection.close();
			// serverSocket.close();
			in.close();
			out.close();
			connection = null;
			// serverSocket = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized void start() {

		try {
			serverSocket = new ServerSocket(PORT);
			serverSocket.setSoTimeout(800);

		} catch (Exception e) {
			e.printStackTrace();
		}

		listener.start();
	}

	public static synchronized void stop() {
		listener.interrupt();

		try {
			connection.close();
			serverSocket.close();
			in.close();
			out.close();
			connection = null;
			serverSocket = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Thread listener = new Thread(new Runnable() {

		@Override
		public void run() {

			while (!listener.isInterrupted()) {
				try {

					if (connection == null || connection.isClosed()) {
						System.out.println("Searching for a connection...");
						try {

							connection = serverSocket.accept();
							out = new ObjectOutputStream(connection.getOutputStream());
							in = new ObjectInputStream(connection.getInputStream());

							if (connection != null && !connection.isClosed())
								System.out.println("Connected!");
						} catch (Exception e) {
						}
					} else {

//						System.out.println("Reached");
/*
						byte b = in.readByte();

						System.out.println("Byte: " + b);*/

//						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte buffer[] = new byte[MAX_BYTE_BUFFER];

						int numFromStream = in.read(buffer, 0, buffer.length);
						System.out.println("num from stream: " + numFromStream);
						System.out.print("Data from stream: ");
						printByteArray(buffer);
/*
						in.close();
						in = new ObjectInputStream(connection.getInputStream());*/


						/*ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte buffer[] = new byte[1024];
						for(int s; (s=in.read(buffer, 0, buffer.length)) != -1; )
						{
							baos.write(buffer, 0, s);
							System.out.println("Write to buffer");
						}
						byte result[] = baos.toByteArray();*/

//						System.out.println("Byte array recieved: " + result);

						/*System.out.println("else reached");
						String s = in.readUTF();
						System.out.println("Message recieved: " + s);

						// TODO make sure that these are all working

						if (s.contains(StringDictionary.TASK)) {

							s = s.substring(StringDictionary.TASK.length(), s.length() - 1);

							if (s.contains(StringDictionary.GOAL_ATTACHED)) {

							} else if (s.contains(StringDictionary.LOG)) {

								send(StringDictionary.TASK + StringDictionary.LOG
										+ "roborio told the driverstation to log something, it should be the other way around.");

							} else if (s.contains(StringDictionary.MESSAGE)) {

								System.out.println("ROBORIO replied with message: " + s);

							} else if (s.contains(StringDictionary.TELEOP_START)) {

								VisionPanel.taskCommand(s);

							} else if (s.contains(StringDictionary.AUTO_START)) {

								VisionPanel.taskCommand(s);

							} else {
								System.out.println("Valid task was recieved, but with unrecognized contents.");
							}

						} else {
							System.out.println("unrecognized task");
						}*/

					}

				} catch (SocketException e) {
					System.out.println(
							"\n\tConnection to the client has been lost. Attempting to re-establish connection");
					reset();
				} catch (EOFException e) {
					System.exit(1);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	);

	public static void printByteArray(byte[] arr) {

		String str = "[";

		for (int i = 0; i < arr.length; i++)
			str += arr[i] + ",";

		str.substring(0, str.length() - 2);

		System.out.println(str + "]");
	}
}
