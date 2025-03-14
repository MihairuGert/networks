package org.lab1;

import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    private String ip;
    private final String mac;

    public Client() {
        ip = generateIp();
        mac = generateMac();
    }

    public Client(String ip, String mac) {
        this.ip = ip;
        this.mac = mac;
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connectToRouter("localhost", 80);
    }

    public void connectToRouter(String ip, int port) {
        try (Socket socket = new Socket(ip, port)) {
            String localIp = getLocalIp();
            if (localIp == null) {
                socket.close();
                return;
            }
            String sendString = "CONNECT " + localIp + " " + mac;
            socket.getOutputStream().write(sendString.getBytes());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private String getLocalIp() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }

    private String generateIp() {
        StringBuilder generatedString = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            generatedString.append((int) (Math.random() * 1000) % 256);
            if (i == 3) {
                break;
            }
            generatedString.append(".");
        }
        return generatedString.toString();
    }

    private String generateMac() {
        StringBuilder generatedString = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            String append = Integer.toHexString((int) (Math.random() * 1000) % 256);
            generatedString.append(append);
            if (append.length() < 2) {
                generatedString.append(0);
            }
            if (i == 5) {
                break;
            }
            generatedString.append("-");
        }
        return generatedString.toString();
    }
}
