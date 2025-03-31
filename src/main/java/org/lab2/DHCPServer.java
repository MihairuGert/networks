package org.lab2;

import org.lab2.commands.Command;
import org.lab2.commands.CommandFactory;
import org.lab2.commands.Ip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class DHCPServer {
    private final ConcurrentHashMap<String, RentTableState> rentTable;
    private volatile boolean isWorking = true;
    private final int port = 6767;
    private Ip availableIp = new Ip("192.168.0.1");

    public DHCPServer() {
        rentTable = new ConcurrentHashMap <>();
    }

    private void listen() {
        CommandFactory commandFactory = new CommandFactory();
        try (DatagramSocket server = new DatagramSocket(port)) {
            System.out.println("DHCP Server: Start working...");
            while (isWorking) {
                byte[] buffer = new byte[512];
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                server.receive(requestPacket);
                System.out.println("DHCP Server: Received cmd.");
                Command cmd = commandFactory.newCommand(new String(requestPacket.getData()).trim());
                if (cmd == null) {
                    continue;
                }
                DatagramPacket responsePacket = cmd.execute(rentTable, availableIp);
                System.out.println("DHCP Server: " + new String(responsePacket.getData()));
                responsePacket.setAddress(requestPacket.getAddress());
                responsePacket.setPort(requestPacket.getPort());
                server.send(responsePacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(this::listen).start();
    }

    public void stop() {
        isWorking = false;
    }

    public static void main(String[] args) {
        DHCPServer server = new DHCPServer();
        server.start();
    }
}
