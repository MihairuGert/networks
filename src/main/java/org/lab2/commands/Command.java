package org.lab2.commands;

import org.lab2.RentTableState;

import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public sealed interface Command permits Discover, Request {
    DatagramPacket execute(ConcurrentHashMap<String, RentTableState> rentTable, Ip availableIp);
    String[] args = new String[2];
}
