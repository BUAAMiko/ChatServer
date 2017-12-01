package groupwork.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSocketManagement {

    DatagramSocket socket;
    DatagramPacket packet;

    UDPSocketManagement() throws SocketException {
        socket = new DatagramSocket(0);
        byte[] data = new byte[1024];
        packet = new DatagramPacket(data,data.length);
    }

    UDPSocketManagement(int port) throws SocketException {
        socket = new DatagramSocket(port);
        byte[] data = new byte[1024];
        packet = new DatagramPacket(data,data.length);
    }

    public void sendDatagramPacket(byte[] data, int offset, int length, InetAddress address) throws IOException {
        packet.setData(data,offset,length);
        packet.setAddress(address);
        socket.send(packet);
    }

    public DatagramPacket receivedDatagramPacket() throws IOException {
        packet.setData(new byte[1024]);
        socket.receive(packet);
        return packet;
    }
}
