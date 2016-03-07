package com.Team5427.testing;

/**
 * Created by Frian on 3/6/2016.
 */
public class TestMain {

    public static void main(String[] args) {
        SteelServer server = new SteelServer();
//        server.findClient();
/*        while (server.getClientSocket() == null) {
            System.out.println("Finding client");

            try {
                Thread.sleep(30);
            } catch (Exception e) {

            }
        }*/

        server.start();


    }
}
