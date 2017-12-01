package groupwork.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class PortRequestService extends Thread {

    UDPSocketManagement udp;
    DatagramPacket packet;

    @Override
    public void run() {
        try {
            udp = new UDPSocketManagement(2333);
            packet = udp.receivedDatagramPacket();
            byte[] data = packet.getData();
            if (data[0] == 0x0F) {
                data = ByteConversionFunction.intToBytes(MainService.getPort());
                udp.sendDatagramPacket(data, 0, data.length, packet.getAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
