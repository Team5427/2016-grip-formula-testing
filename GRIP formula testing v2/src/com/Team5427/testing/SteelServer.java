/**
 * NOTE: This is not the same server class as the one in the robotics 2016 respository for
 *       the main robot. This is a server class for testing purposes.
 */

package com.Team5427.testing;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Charlemagne Wong on 3/6/2016.
 */
public class SteelServer implements Runnable{

    public static final String DEFAULT_IP = "10.54.27.1";
    public static final int DEFAULT_PORT  = 20161;

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

    /**
     * Waits to find connection from the client
     *
     * @return true if connection is a success, false if failed
     */
    public void findClient() {
//        clientSocket = new ClientSearcher(serverSocket).findClient(500);
        ClientSearcher searcher = new ClientSearcher(this);
        System.err.println("Ran");
        searcher.findClient();

/*        if (clientSocket != null) {
            establishStream();

            return true;
        }*/

/*        if (serverSocket == null)
            System.err.println("hi");
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }

        */


    }

    /**
     * Establishes the input and output stream between client and server
     *
     * @return true if stream established sucessfully, false if otherwise
     */
    public boolean establishStream() {
        if (clientSocket != null && !clientSocket.isClosed()) {
            try {
                serverSocket = new ServerSocket(port);
                is = new ObjectInputStream(clientSocket.getInputStream());
                os = new ObjectOutputStream(clientSocket.getOutputStream());
                inputStreamData = new ArrayList<>();
                return true;
            } catch (Exception e) {
                System.out.println("Input and Output stream failed to establish with the driver station.");
                return false;
            }
        }

        return false;
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

        while (running && (clientSocket == null || clientSocket.isClosed())) {
            findClient();

            if (clientSocket == null) {
                try {
                    networkThread.sleep(10);
                } catch (Exception e) {
                    System.out.println("Error in sleeping SteelServer");
                }
            }
        }

        while (running) {
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