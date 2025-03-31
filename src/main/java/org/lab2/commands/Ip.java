package org.lab2.commands;

public class Ip {
    private final int[] ipOct;

    public Ip() {
        ipOct = new int[4];
        setIp("0.0.0.0");
    }

    public Ip(String ip) {
        ipOct = new int[4];
        try {
            setIp(ip);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            setIp("0.0.0.0");
        }
    }

    public void setIp(String ip) {
        String[] parsedIp = ip.split("\\.");
        if (parsedIp.length < 4) {
            throw new RuntimeException("Not enough args");
        }
        for (int i = 0; i < 4; i++) {
            ipOct[i] = Integer.parseInt(parsedIp[i]);
        }
    }

    public void plusOne() {
        for (int i = 3; i >= 0; i--) {
            if (ipOct[i] + 1 < 256) {
                ipOct[i]++;
                break;
            }
            ipOct[i] = 0;
        }
    }

    public boolean equals(String ip) {
        return toString().equals(ip);
    }

    @Override
    public String toString() {
        return ipOct[0] + "." + ipOct[1] + "." + ipOct[2] + "." + ipOct[3];
    }
}
