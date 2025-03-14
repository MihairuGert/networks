package org.lab1;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public record RouterClient(String ip,
                           String mac,
                           Socket socket) {
    public void sendMessageClient(String message) {
        try {
            socket.getOutputStream().write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getClientMessage() {
        try {
            byte[] data = new byte[256];
            int status = socket.getInputStream().read(data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
