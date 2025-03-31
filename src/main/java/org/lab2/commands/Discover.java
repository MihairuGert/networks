package org.lab2.commands;

import org.lab2.RentTableState;

import java.net.DatagramPacket;
import java.util.HashMap;

public final class Discover implements Command {
    @Override
    public DatagramPacket execute(HashMap<String, RentTableState> rentTable) {
        return null;
    }

    public Discover(String[] args) {
        Command.args[0] = args[0];
        Command.args[1] = args[1];
    }
}
