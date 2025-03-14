package org.lab1;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

public class Router {
    private HashMap<String, Client> routingTable;
    private DatagramSocket server;
    private final int port = 80;

    Router() {
        try {
            server = new DatagramSocket(port);
        } catch (SocketException e) {
            System.err.println(e.getMessage());
        }
    }

    public void start() {

    }

    private void listen() {
        while(true) {

        }
    }
}
