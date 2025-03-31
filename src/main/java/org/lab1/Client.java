package org.lab1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Client {
    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    private String ip;
    private final String mac;
    private Socket router;

    public Client() {
        ip = generateIp();
        mac = generateMac();
    }

    public Client(String ip, String mac) {
        this.ip = ip;
        this.mac = mac;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIpFromDHCP() {
        int serverPort = 6767;
        try (DatagramSocket socket = new DatagramSocket()) {
            System.out.println("Client: Try to find DHCP server...");
            socket.setBroadcast(true);
            String message = "DHCPDISCOVER";
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), serverPort);
            socket.send(packet);

            buffer = new byte[512];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);
            System.out.println("Client: Found DHCP server.");
            String[] response = new String(responsePacket.getData()).trim().split(" ");
            String dhcpIp = response[0];
            String clientIp = response[1];

            message = "DHCPREQUEST " + clientIp;
            byte[] buffer2 = message.getBytes();
            packet = new DatagramPacket(buffer2, buffer2.length, InetAddress.getByName(dhcpIp), serverPort);
            socket.send(packet);

            System.out.println("Client: Got ip " + clientIp);
            return clientIp;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.setIp(client.getIpFromDHCP());
        try {
            client.connectToRouter("localhost", 80);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        while (true) {
            String command = scanner.next();
            try {
                String response = client.commandRouter(command);
                System.out.println(response);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public String commandRouter(String command) throws IOException {
        switch (command.trim().split(" ")[0]) {
            case "PING":
                router.getOutputStream().write(command.getBytes());
                router.getOutputStream().flush();
                byte[] response = new byte[256];
                int status = router.getInputStream().read(response);
                return new String(response).trim();
            case "DISCONNECT":
                router.getOutputStream().write("DISCONNECT".getBytes());
                router.getOutputStream().flush();
                router.close();
                return "Successfully disconnected.";
            case "CONNECT":
                connectToRouter(command.trim().split(" ")[1], 80);
                return "Successfully connected.";
            default:
                return """
                        Try commands:
                        \t PING <IP Address>
                        \t DISCONNECT
                        \t CONNECT <Router IP Address>""";
        }
    }

    public void connectToRouter(String ip, int port) {
        try {
            router = new Socket(ip, port);
            String sendString = "CONNECT " + this.ip + " " + mac;
            router.getOutputStream().write(sendString.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
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
