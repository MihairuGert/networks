package org.lab2.commands;

import org.lab2.RentTableState;

import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class Discover implements Command {
    @Override
    public DatagramPacket execute(ConcurrentHashMap<String, RentTableState> rentTable, Ip availableIp) {
        String freeIp = availableIp.toString();
        rentTable.put(availableIp.toString(), RentTableState.Offered);
        availableIp.plusOne();
        String serverIp = "localhost " + freeIp;
        byte[] buffer = serverIp.getBytes();
        return new DatagramPacket(buffer, buffer.length);
    }

    public Discover(String[] args) {
        Command.args[0] = args[0];
        if (args.length >= 2)
            Command.args[1] = args[1];
    }
}
