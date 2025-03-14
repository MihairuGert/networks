package org.lab1;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.HashMap;

public class Router {
    private HashMap<String, Client> routingTable;
    private ServerSocket server;
    private final int port = 80;

    Router() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Router router = new Router();
        router.start();
    }

    public void start() {
        new Thread(() -> {
            listenNewClients();
        });
    }

    private void listenNewClients() {
        while(true) {
            try {
                Socket socket = server.accept();
                addClient(socket);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        }
    }

    private void addClient(Socket socket) {
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[256];
            int status = inputStream.read(bytes);
            String[] attributes = parseCommand(bytes);
            System.out.println("Router info: new client connected " + attributes[1] + " " + attributes[2]);
            routingTable.put(attributes[1], new Client(attributes[1], attributes[2]));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private String[] parseCommand(byte[] data) {
        String string = new String(data);
        return string.split(" ");
    }
}
