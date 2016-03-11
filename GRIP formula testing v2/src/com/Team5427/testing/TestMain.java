package com.Team5427.testing;

import com.Team5427.Networking.client.NetworkClient;

public class TestMain {

	public static NetworkClient client;
	
	public static void main(String[] args) {
		client = new NetworkClient("localhost", NetworkClient.DEFAULT_PORT);
		client.start();

		Object o = client;
		System.out.println(o.getClass());
	}
}