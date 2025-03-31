package org.lab2.commands;

import org.lab2.RentTableState;

import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class Request implements Command {
    @Override
    public DatagramPacket execute(ConcurrentHashMap<String, RentTableState> rentTable, Ip availableIp) {
        RentTableState rentTableState = rentTable.get(args[1]);
        String response = "";
        if (rentTableState != RentTableState.Offered) {
            response += "DHCPERR";
            return new DatagramPacket(response.getBytes(), response.getBytes().length);
        }
        rentTable.remove(args[1]);
        rentTable.put(args[1], RentTableState.Ack);
        response += "DHCPACK";
        return new DatagramPacket(response.getBytes(), response.getBytes().length);
    }

    public Request(String[] args) {
        Command.args[0] = args[0];
        if (args.length >= 2)
            Command.args[1] = args[1];
    }
}
