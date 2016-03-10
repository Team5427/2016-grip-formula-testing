package com.Team5427.Networking.client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class NetworkClient implements Runnable {

	public static final String DEFAULT_IP = "10.54.27.1";
	public static final int DEFAULT_PORT = 25565;

	public static String ip;
	public static int port;

	Thread networkThread;
	public ArrayList<Object> inputStreamData = null;

	private Socket clientSocket;
	private ObjectInputStream is;
	private ObjectOutputStream os;

	private boolean running = false;

	public NetworkClient() {
		ip = DEFAULT_IP;
		port = DEFAULT_PORT;
	}

	public NetworkClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	/**
	 * @deprecated Connects to the client server. This will prevent reconnection
	 *             if connection is already established
	 *
	 * @return true if connection is successful, returns false if not or
	 *         connection has already been established
	 */
	public boolean connect() {
		if (clientSocket == null || clientSocket.isClosed())
			return reconnect();

		return false;
	}

	/**
	 * Reconnect to the client to the server
	 *
	 * @return true if connection is a success, false if failed
	 */
	public boolean reconnect() {
		try {
			clientSocket = new Socket(ip, port);
			is = new ObjectInputStream(clientSocket.getInputStream());
			os = new ObjectOutputStream(clientSocket.getOutputStream());
			inputStreamData = new ArrayList<>();

			System.out.println("Connection to the server has been established successfully.");

			return true;
		} catch (Exception e) {
			System.out.println("Connection failed to establish.");
			return false;
		}
	}

	/**
	 * Checks if the client is connected to a server
	 *
	 * @return true if server connection is established, false if not.
	 */
	public boolean isConnected() {
		return clientSocket != null && !clientSocket.isClosed();
	}

	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		NetworkClient.ip = ip;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		NetworkClient.port = port;
	}

	public ArrayList<Object> getInputStreamData() {
		return inputStreamData;
	}

	/**
	 * Sends an object to the server
	 *
	 * @param o
	 *            object to be sent to the server
	 * @return true if the object is sent successfully, false if otherwise.
	 */
	public synchronized boolean send(Serializable o) {

		if (running) {
			try {
				if (os == null)
					System.out.println("The output stream is null");
				os.writeObject(o); // The error here is that writeObject is null
				os.reset();
                return true;
            } catch (NotSerializableException e) {
                System.err.println(getClass() + ":: send(Serializable o)\n\tThe object to be sent is not serializable.");
            } catch (SocketException e) {
				System.err.println("Socket Exception");
				reset();
			} catch (NullPointerException e) {
				System.err.println("\n\tThere was an error connecting to the server.");					// This error occurs when the client attempts to connect to a server, but the running
				stop();																					// server is having a SocketException
			} catch (Exception e) {
                e.printStackTrace();
            }
		}

		return false;
	}

	/**
	 * Enables the thread to start receiving data from a network
	 *
	 * @return true if the thread starts successfully, false if otherwise.
	 */
	public synchronized boolean start() {
		if (!running && (clientSocket == null || !clientSocket.isClosed())) {
			networkThread = new Thread(this);
			running = true;
			networkThread.start();
			return true;
		}

		return false;
	}

	/**
	 * Stops the thread from receiving data from the server
	 *
	 * @return true if the thread is stopped successfully, false if otherwise.
	 */
	public synchronized boolean stop() {
		running = false;


		try {
			clientSocket.close();
			os.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		clientSocket = null;
		os = null;
		is = null;

		if (!networkThread.isAlive()) {		 	// The thread is found running and is told to stop
			return true;
		} else {								// The thread is not running in the first place
			return false;
		}
	}

	public synchronized boolean reset() {
		stop();
		start();

		return false;
	}

	/**
	 * Running method that receives data from the server.
	 */
	@Override
	public void run() {

		reconnect();

		while (running && clientSocket != null && !clientSocket.isClosed() && is != null) {
			try {
				inputStreamData.add(is.readObject());
				// is.reset();

			} catch (SocketException e) {
				reset();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				networkThread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}