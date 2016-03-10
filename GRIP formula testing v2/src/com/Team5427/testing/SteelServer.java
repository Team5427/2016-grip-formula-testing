/**
 * NOTE: Do not use this. This server isn't as good as Andrew's
 */

package com.Team5427.testing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Charlemagne Wong on 3/6/2016.
 */
public class SteelServer implements Runnable {

	public static final String DEFAULT_IP = "10.54.27.1";
	public static final int DEFAULT_PORT = 20161;

	public static String ip;
	public static int port;

	Thread networkThread;
	public ArrayList<Object> inputStreamData = null;

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private ObjectOutputStream os;
	private ObjectInputStream is;

	private boolean running = false;

	public SteelServer() {
		port = DEFAULT_PORT;
		initialize();
	}

	public SteelServer(int port) {
		this.port = port;
		initialize();
	}

	/**
	 * Initializes the server
	 */
	public void initialize() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (Exception e) {
			System.out.println("SteelServer failed to initialize");
		}
	}

	/*    *//**
			 * Waits to find connection from the client
			 *
			 * @return true if connection is a success, false if failed
			 *//*
				 * public void findClient() { // clientSocket = new
				 * ClientSearcher(serverSocket).findClient(500); ClientSearcher
				 * searcher = new ClientSearcher(this); searcher.findClient();
				 * 
				 */
	/*
	 * if (clientSocket != null) { establishStream();
	 * 
	 * return true; }
	 *//*
		
		*//*
		 * if (serverSocket == null) System.err.println("hi"); try {
		 * clientSocket = serverSocket.accept(); } catch (IOException e) {
		 * e.printStackTrace(); }
		 * 
		 *//*
			 * 
			 * 
			 * }
			 */

	/**
	 * Establishes the input and output stream between client and server
	 *
	 * @return true if stream established sucessfully, false if otherwise
	 */
	private boolean establishStream() {
		if (clientSocket != null && !clientSocket.isClosed()) {
			try {
				is = new ObjectInputStream(clientSocket.getInputStream());
				System.out.println("Input stream established");
				os = new ObjectOutputStream(clientSocket.getOutputStream());
				System.out.println("Output stream established");
				inputStreamData = new ArrayList<>();
				System.out.println("Connection has been established with the driver station.");
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Input and Output stream failed to establish with the driver station.");
				return false;
			}
		}

		return false;
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

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		SteelServer.port = port;
	}

	public ArrayList<Object> getInputStreamData() {
		return inputStreamData;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
		System.err.println("establishing connection");
		establishStream();
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	/**
	 * Sends an object to the server
	 *
	 * @param o
	 *            object to be sent to the server
	 * @return true if the object is sent successfully, false if otherwise.
	 */
	public boolean send(Object o) {
		try {
			os.writeObject(o);
			os.reset();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Enables the thread to start receiving data from a network
	 *
	 * @return true if the thread starts successfully, false if otherwise.
	 */
	public boolean start() {
		if (serverSocket != null) {
			networkThread = new Thread(this);
			networkThread.start();
			running = true;
			return true;
		}

		return false;
	}

	/**
	 * Stops the thread from receiving data from the server
	 *
	 * @return true if the thread is stopped successfully, false if otherwise.
	 */
	public boolean stop() {
		if (networkThread.isAlive()) { // The thread is found running and is
			// stopped
			running = false;
			return true;
		} else { // The thread is not running in the first place
			return false;
		}
	}

	/**
	 * Running method that receives data from the server.
	 */
	@Override
	public void run() {

		try {
			clientSocket = serverSocket.accept();
			System.out.println("client found");

			is = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Input stream established");

			os = new ObjectOutputStream(clientSocket.getOutputStream());
			System.out.println("Output stream established");

			System.out.println("\n~Connection established");

		} catch (IOException e) {
			e.printStackTrace();
		}

		while (running && is != null) {
			if (clientSocket.isClosed()) {
				running = false;
				break;
			}

			try {
				inputStreamData.add(is.readObject());
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				networkThread.sleep(10);
			} catch (Exception e) {
				System.out.println("Error in sleeping SteelServer");
			}
		}
	}
}