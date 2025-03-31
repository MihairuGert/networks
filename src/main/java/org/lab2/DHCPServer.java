package org.lab2;

import org.lab2.commands.Command;
import org.lab2.commands.CommandFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class DHCPServer {
    private final HashMap<String, RentTableState> rentTable;
    private volatile boolean isWorking = true;
    private final int port = 67;

    public DHCPServer() {
        rentTable = new HashMap<>();
    }

    private void listen() {
        CommandFactory commandFactory = new CommandFactory();
        try (DatagramSocket server = new DatagramSocket(port)) {
            while (isWorking) {
                byte[] buffer = new byte[512];
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                server.receive(requestPacket);
                Command cmd = commandFactory.newCommand(new String(requestPacket.getData()));
                if (cmd == null) {
                    continue;
                }
                DatagramPacket responsePacket = cmd.execute(rentTable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(this::listen).start();
    }

    public void stop() {
        isWorking = true;
    }
}
