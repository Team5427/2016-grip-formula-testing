package com.Team5427.Networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

   private static ServerSocket	 serverSocket;
   private static Socket		 connection	= null;
   private static ObjectInputStream	 in;
   private static ObjectOutputStream out;

   private static final int		 PORT		= 25565;

   public static void start() {

	try {
	   serverSocket = new ServerSocket(PORT);
	   serverSocket.setSoTimeout(100);

	} catch (Exception e) {
	   e.printStackTrace();
	}

	t.start();
   }

   private static Thread t = new Thread(new Runnable() {

	@Override
	public void run() {

	   while (true) {
		try {

		   if (connection == null || connection.isClosed()) {
			System.out.println("Searching for a connection...");
			try {

			   connection = serverSocket.accept();
			   out = new ObjectOutputStream(connection.getOutputStream());
			   in = new ObjectInputStream(connection.getInputStream());

			} catch (Exception e) {
			} finally {
			   if (connection != null && !connection.isClosed()) System.out.println("Connected!");
			}
		   } else {
			Object o = in.readObject();

			if (o instanceof Task) {

			}

		   }

		} catch (Exception e) {
		   e.printStackTrace();
		}
	   }

	}
   }

   );

}
