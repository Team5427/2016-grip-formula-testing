package com.Team5427.Networking.server;

import com.Team5427.Networking.Task;

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
	/**
	 * This is set to true when the server is running, false when it's not. This
	 * is also used to stop the server when needed
	 */
	private static boolean running = false;

	private static final int PORT = 25565;

	/**
	 * Sends an object to the client
	 * 
	 * @param o
	 *            object to be sent
	 * @return true if object has been sent and false if otherwise
	 */
	public static boolean send(Object o) {
		if (connection != null && !connection.isClosed()) {
			try {
				out.writeObject(o);
				out.reset(); // is reset needed?
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public static synchronized void reset() {
		stop();
		start();
	}

	public static synchronized void start() {

		try {
			serverSocket = new ServerSocket(PORT);
			serverSocket.setSoTimeout(100);

		} catch (Exception e) {
			e.printStackTrace();
		}

		running = true;
		listener.start();
	}

	public static synchronized void stop() {
		running = false;
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

			while (running) {
				try {

					if (connection == null || connection.isClosed()) {
						System.out.println("Searching for a connection...");
						try {

							connection = serverSocket.accept();
							out = new ObjectOutputStream(connection.getOutputStream());
							in = new ObjectInputStream(connection.getInputStream());

							sender.start();

						} catch (Exception e) {
						} finally {
							if (connection != null && !connection.isClosed())
								System.out.println("Connected!");
						}
					} else {
						Object o = in.readObject();

						/**
						 * These won't really be used, as the robot won't really
						 * be sending data to the grip program. Just there for
						 * comical effect
						 */
						if (o instanceof Task) {

							switch (((Task) o).getTask()) {
							case GOAL_ATTACHED:
								break;
							case LOG:
								break;
							}

						}

					}

				} catch (SocketException e) {
					System.out.println("\n\tConnection to the client has been lost. Attempting to re-establish" +
							"connection");
//					reset();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	);

	private static Thread sender = new Thread(new Runnable() {

		@Override
		public void run() {
			while (connection != null && !connection.isClosed()) {
				// TODO send goals here
			}

		}

	});

}
