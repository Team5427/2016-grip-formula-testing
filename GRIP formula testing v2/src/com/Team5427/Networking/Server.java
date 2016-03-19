package com.Team5427.Networking;

import com.Team5427.Networking.Task;
import com.Team5427.Networking.TaskDescription;
import com.Team5427.VisionProcessing.VisionPanel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Server {

	private static Socket connection = null;
	private static ServerSocket serverSocket;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;

	private static final int PORT = 25565;

	/**
	 * Always use this method to send anything to the client, as it will ensure
	 * that the proper thing is sent in order to avoid any unnecessary errors.
	 * 
	 * @param o
	 *            Object to be sent to the client
	 * @param t
	 *            an enum from the class TaskDescription, used to tell the
	 *            client what to do with the received information.
	 * @return whether or not the operation was successful.
	 * 
	 * @deprecated sending objects over the stream wasn't working for some
	 *             reason, so now only strings will be sent
	 */
	public static boolean send(TaskDescription t, Object o) {
		if (hasConnection()) {
			try {
				out.writeObject(new Task(t, o));
				out.flush(); // is reset needed? No, we need to flush it I
								// believe
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public static boolean send(String s) {
		if (hasConnection()) {
			try {
				out.writeObject(s);
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

		Scanner taskReader;

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
						String s = in.readUTF();

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

								VisionPanel.taskCommand(TaskDescription.TELEOP_START);

							} else if (s.contains(StringDictionary.AUTO_START)) {

								VisionPanel.taskCommand(TaskDescription.AUTO_START);

							} else {
								System.out.println("Valid task was recieved, but with unrecognized contents.");
							}

						} else {
							System.out.println("unrecognized task");
						}

					}

				} catch (SocketException e) {
					System.out.println(
							"\n\tConnection to the client has been lost. Attempting to re-establish connection");
					reset();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	);
}
