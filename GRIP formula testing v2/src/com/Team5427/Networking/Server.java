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

	/**
	 * Call whenever sending something to the client in order to check whether
	 * or not it is connected to the server.
	 * 
	 * @return whether the client is connected.
	 */
	public static boolean hasConnection() {
		return (connection != null && !connection.isClosed() );
	}


	public static synchronized void reset() {
		try {
			connection.close();
//			serverSocket.close();
			in.close();
			out.close();
			connection = null;
//			serverSocket = null;
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
						Object o = in.readObject();

						if (o instanceof Task) {

							Task t = (Task) o;

							switch (t.getTask()) {
								case GOAL_ATTACHED:
									break;
								case LOG:
									break;
								case MESSAGE:
									String message = (String)(t.getObject());
									System.out.println("ROBORIO replied with message: " + message);
									break;
								case AUTO_START:
									VisionPanel.taskCommand(TaskDescription.AUTO_START);
									break;
								case TELEOP_START:
									VisionPanel.taskCommand(TaskDescription.TELEOP_START);
									break;
								case DEFAULT_MODE:
									VisionPanel.taskCommand(TaskDescription.DEFAULT_MODE);
									break;
							}

						}

					}

				} catch (SocketException e) {
					System.out.println("\n\tConnection to the client has been lost. Attempting to re-establish connection");
					reset();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	);
}
