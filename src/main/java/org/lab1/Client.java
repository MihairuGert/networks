package org.lab1;

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
            generatedString.append(Integer.toHexString((int) (Math.random() * 1000) % 256));
            if (i == 5) {
                break;
            }
            generatedString.append("-");
        }
        return generatedString.toString();
    }
}
