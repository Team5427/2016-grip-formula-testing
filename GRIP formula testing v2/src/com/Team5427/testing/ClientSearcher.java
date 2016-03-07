/**
 * This class is dedicated to finding a client for a server
 */

package com.Team5427.testing;

import javax.jws.soap.SOAPBinding;
import javax.xml.stream.events.StartElement;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientSearcher implements Runnable{

    SteelServer server;
    ServerSocket serverSocket = null;
    Socket clientSocket;

    public ClientSearcher(SteelServer server) {
        this.server = server;
    }

    public ClientSearcher(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void findClient() {
        new Thread(this).start();
    }

    /**
     * @deprecated
     * Returns a client for a server socket
     *
     * @param timeoutNano time the thread will look for a client
     * @return a client socket if found, null if non is found
     */
    public Socket findClient(long timeoutNano) {
        clientSocket = null;

        long startTime = System.nanoTime();
        long endTime = System.nanoTime() + timeoutNano;

        Thread t = new Thread(this);
        t.start();


        try {
            do {
                if (serverSocket != null) {
                    t.stop();                   // TODO: Deprecated?
                    break;
                }

                t.sleep(20);

            } while (System.nanoTime() < endTime);
        } catch (Exception e) {

        }

        return clientSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public void run() {
        if (server.getServerSocket() != null) {
            try {
                clientSocket = serverSocket.accept();
                System.err.println("after accept");

                if (clientSocket != null)
                    server.setClientSocket(clientSocket);
            } catch (Exception e) {

            }
        }
    }
}
