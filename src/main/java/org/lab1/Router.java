package org.lab1;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Router {
    private final ConcurrentHashMap<String, String> arpTable = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RouterClient> pingMap = new ConcurrentHashMap<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private ServerSocket server;
    private final int port = 80;
    private volatile boolean isRunning;

    Router() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Router router = new Router();
        router.start();
    }

    public void start() {
        isRunning = true;
        threadPool.submit(this::listenNewClients);
    }

    private void listenNewClients() {
        while (isRunning) {
            try {
                Socket socket = server.accept();
                threadPool.submit(() -> handleClient(socket));
            } catch (IOException e) {
                if (isRunning) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket socket) {
        String clientIp = null;
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[256];
            int status = inputStream.read(bytes);
            String[] attributes = parseCommand(bytes);
            clientIp = attributes[1];
            String clientMac = attributes[2];

            arpTable.put(clientIp, clientMac);
            pingMap.put(clientMac, new RouterClient(clientIp, clientMac, socket));
            System.out.println("Router info: new client connected " + clientIp + " " + clientMac);

            while (isRunning && !socket.isClosed()) {
                byte[] message = pingMap.get(arpTable.get(clientIp)).getClientMessage();
                if (message != null) {
                    processCommand(clientMac, message);
                } else {
                    String mac = arpTable.get(clientIp);
                    arpTable.remove(clientIp);
                    pingMap.remove(mac);
                    socket.close();
                    System.out.println("Router warning: client " + clientIp + " disconnected");
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
        try {
            if (clientIp != null) {
                String mac = arpTable.get(clientIp);
                arpTable.remove(clientIp);
                pingMap.remove(mac);
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }

    private void processCommand(String clientMac, byte[] message) {
        String[] attributes = parseCommand(message);
        RouterClient client = pingMap.get(clientMac);

        if ("PING".equals(attributes[0])) {
            String targetIp = attributes[1];
            long time = -System.currentTimeMillis();
            boolean doesExist = arpTable.containsKey(targetIp);

            if (doesExist && !pingMap.get(arpTable.get(targetIp)).socket().isClosed()) {
                time += System.currentTimeMillis();
                System.out.println("Router info: ping request from: " + client.ip() + " to " + targetIp);
                client.sendMessageClient("Answer from " + targetIp + ": bytes=32 time=" + time + "ms TTL=1");
            } else {
                System.out.println("Router warning: ping request from: " + client.ip() + " to " + targetIp + " failed");
                client.sendMessageClient("Checking the network could not find: " + targetIp);
            }
        } else if ("DISCONNECT".equals(attributes[0])) {
            try {
                System.out.println("Router info: closed the connection with " + client.ip());
                arpTable.remove(client.ip());
                pingMap.remove(client.mac());
                client.socket().close();
            } catch (IOException e) {
                System.out.println("Router warning: closing connection with " + client.ip() + " failed");
            }
        }
    }

    private String[] parseCommand(byte[] data) {
        String string = new String(data);
        return string.trim().split(" ");
    }

    public void stop() {
        isRunning = false;
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
            server.close();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error stopping router: " + e.getMessage());
        }
    }
}