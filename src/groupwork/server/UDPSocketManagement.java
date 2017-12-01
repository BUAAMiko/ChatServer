package groupwork.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSocketManagement extends Thread{

    DatagramSocket socket;
    DatagramPacket packet;

    UDPSocketManagement() throws SocketException {
        socket = new DatagramSocket(0);
        byte[] data = new byte[1024];
        packet = new DatagramPacket(data,data.length);
        MainService.setPort(socket.getLocalPort());
    }

    UDPSocketManagement(int port) throws SocketException {
        socket = new DatagramSocket(port);
        byte[] data = new byte[1024];
        packet = new DatagramPacket(data,data.length);
        MainService.setPort(socket.getLocalPort());
    }

    public void sendDatagramPacket(byte[] data, int offset, int length, InetAddress address) throws IOException {
        packet.setData(data,offset,length);
        packet.setAddress(address);
        socket.send(packet);
    }

    public void sendDatagramPacket(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    public DatagramPacket receivedDatagramPacket() throws IOException {
        packet.setData(new byte[1024]);
        socket.receive(packet);
        return packet;
    }

    void closeDatagramSocket() {
        socket.close();
    }

    @Override
    public void run() {
        try {
            receivedDatagramPacket();
            sendDatagramPacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
