package org.lab2.commands;

import org.lab2.RentTableState;

import java.net.DatagramPacket;
import java.util.HashMap;

public sealed interface Command permits Discover, Offer, Request, Acknowledge {
    DatagramPacket execute(HashMap<String, RentTableState> rentTable);
    String[] args = new String[2];
}
