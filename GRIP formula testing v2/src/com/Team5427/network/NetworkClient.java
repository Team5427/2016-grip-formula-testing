package com.Team5427.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Charlemagne Wong on 3/6/2016.
 */
public class NetworkClient implements Runnable{

    public static final String DEFAULT_IP = "10.54.27.1";
    public static final int DEFAULT_PORT  = 20161;

    public static String ip;
    public static int port;

    Thread networkThread;
    public ArrayList<Object> inputStreamData = null;

    private Socket clientSocket;
    private ObjectOutputStream os;
    private ObjectInputStream is;

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
     * Connects to the client server. This will prevent reconnection if connection is already
     * established
     *
     * @return true if connection is successful, returns false if not or connection has already
     *         been established
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
            return true;
        } catch (Exception e) {
            System.out.println("Connection failed to establish");
            return false;
        }
    }

    /**
     * Checks if the client is connected to a server
     *
     * @return true if server connection is established, false
     *         if not.
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
     * @param o object to be sent to the server
     * @return true if the object is sent successfully, false
     *         if otherwise.
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
     * @return true if the thread starts successfully, false if
     *         otherwise.
     */
    public boolean startRecieve() {
        if (clientSocket != null) {
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
     * @return true if the thread is stopped successfully, false
     *         if otherwise.
     */
    public boolean stopRecieve() {
        if (networkThread.isAlive()) {      // The thread is found running and is stopped
            running = false;
            return true;
        } else {                            // The thread is not running in the first place
            return false;
        }
    }

    /**
     * Running method that receives data from the server.
     */
    @Override
    public void run() {

        while (running) {
            try {
                inputStreamData.add(is.readObject());
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