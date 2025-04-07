package org.lab4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.concurrent.ConcurrentHashMap;

public class NAT {
    private final ConcurrentHashMap<Integer, AddressPort> portMapping = new ConcurrentHashMap<>();
    private final String publicIp;
    private final String localIpRegex;
    private volatile boolean isWorking = true;

    private static class AddressPort {
        final String ip;
        final int port;

        AddressPort(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }

    public NAT(String publicIp, String localIpRegex) {
        this.publicIp = publicIp;
        this.localIpRegex = localIpRegex;
    }

    public void listen(int port) {
        try (DatagramSocket server = new DatagramSocket(port)) {
            System.out.println("NAT: Started on " + publicIp + ":" + port);
            while (isWorking) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                server.receive(packet);

                if (isPrivateAddress(new String(packet.getData()).trim().split(":")[2])) {
                    handlePrivatePacket(server, packet);
                } else {
                    handlePublicPacket(server, packet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPrivateAddress(String address) {
        return address.matches(localIpRegex);
    }

    private void handlePrivatePacket(DatagramSocket server, DatagramPacket packet) throws Exception {
        String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
        String[] parts = message.split(":", 4);

        if (parts.length < 4) {
            System.err.println("Invalid message format from private network");
            return;
        }

        int natPort = registerMapping(packet.getAddress().getHostAddress(), packet.getPort());

        int targetPort = Integer.parseInt(parts[0]);
        String publicMessage = natPort + ":" + parts[1] + ":" + parts[2];
        byte[] data = publicMessage.getBytes(StandardCharsets.UTF_8);

        DatagramPacket outgoing = new DatagramPacket(
                data,
                data.length,
                InetAddress.getByName("localhost"),
                targetPort
        );

        System.out.printf("NAT: Forwarding private [%s:%d] -> public [%s:%d]%n",
                parts[2],
                packet.getPort(),
                publicIp,
                targetPort);

        server.send(outgoing);
    }

    private void handlePublicPacket(DatagramSocket server, DatagramPacket packet) throws Exception {
        String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
        String[] parts = message.split(":", 2);

        if (parts.length < 2) {
            System.err.println("Invalid public message format");
            return;
        }

        int originalPort = Integer.parseInt(parts[0]);
        AddressPort target = portMapping.get(originalPort);

        if (target == null) {
            System.err.println("No mapping found for port: " + originalPort);
            return;
        }

        byte[] data = parts[1].getBytes(StandardCharsets.UTF_8);

        DatagramPacket response = new DatagramPacket(
                data,
                data.length,
                InetAddress.getByName(target.ip),
                target.port
        );

        System.out.printf("NAT: Routing public [%s:%d] -> private [%s:%d]%n",
                packet.getAddress().getHostAddress(),
                packet.getPort(),
                target.ip,
                target.port);

        server.send(response);
    }

    private synchronized int registerMapping(String privateIp, int privatePort) {
        int natPort = generatePort(privateIp, privatePort);
        portMapping.put(natPort, new AddressPort(privateIp, privatePort));
        return natPort;
    }

    private int generatePort(String ip, int port) {
        return (ip.hashCode() ^ port) & 0xFFFF;
    }

    public void stop() {
        isWorking = false;
    }
}