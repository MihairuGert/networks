package org.lab4;

import java.util.HashMap;

public class NATServer {
    private HashMap<Short, String> portToIp;
    private HashMap<String, Short> ipToPort;
    private String publicIp;
    private String localIpRegex;

    private volatile boolean isWorking = true;

    public NATServer(String publicIp, String localIpRegex) {
        portToIp = new HashMap<>();
        ipToPort = new HashMap<>();
        this.publicIp = publicIp;
        this.localIpRegex = localIpRegex;
    }

//    public void listen(int port) {
//        try (DatagramSocket server = new DatagramSocket(port)) {
//            System.out.println("NAT: Start working...");
//            while (isWorking) {
//                byte[] buffer = new byte[1024];
//                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//                server.receive(packet);
//                System.out.println("NAT: Received msg.");
//                DatagramPacket publicPacket = new DatagramPacket(packet.getData(), packet.getLength());
//                System.out.println("NAT: " + packet.getAddress() + "-> " + publicIp);
//                publicIp.setAddress(requestPacket.getAddress());
//                responsePacket.setPort(requestPacket.getPort());
//                server.send(responsePacket);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private short getPort(String ip) {
        return ipToPort.getOrDefault(ip, (short) -1);
    }

    private String getIp(short port) {
        return portToIp.get(port);
    }

    public void stop() {
        isWorking = false;
    }

    public void register(String ip) {
        portToIp.put((short) (ip.hashCode() % Short.MAX_VALUE), ip);
        ipToPort.put(ip, (short) (ip.hashCode() % Short.MAX_VALUE));
    }
}
