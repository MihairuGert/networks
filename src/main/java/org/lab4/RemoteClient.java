package org.lab4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class RemoteClient {
    private final String ip;
    private final int port;

    private volatile boolean isWorking = true;

    public RemoteClient() {
        ip = "57.51.3.120";
        port = 1204;
    }

    public static void main(String[] args) {
        RemoteClient remoteClient = new RemoteClient();
        new Thread(remoteClient::start).start();
    }

    private int processPort(byte[] data) {
        String[] string = new String(data).split(":");
        int port = Integer.parseInt(string[0]);
        if (port <= 65535 && port >= 0) {
            return port;
        }
        return -1;
    }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            while (isWorking) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                int port = processPort(packet.getData());
                if (port == -1) {
                    continue;
                }
                byte[] responseBuffer = (port+ ":100.100.100.100:"+ip + ":Hello!!!").getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, packet.getAddress(), packet.getPort());
                System.out.println("RC: " + port+ ":100.100.100.100:"+ip + ":Hello!!!");
                socket.send(responsePacket);
             }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void stop() {
        isWorking = false;
    }
}
