package groupwork.server;

import java.io.IOException;
import java.net.*;

public class PortRequestService extends Thread {

    static UDPSocketManagement udp;
    DatagramPacket packet;

    @Override
    public void run() {
        while(true) {
            try {
                udp = new UDPSocketManagement(2333,0);
                packet = udp.receivedDatagramPacket();
                byte[] data = packet.getData();
                String string = new String(data, 0,ByteProcessingFunction.byteArrayEffectiveLength(data));
                if (string.equals("TCP_Port")) {
                    data = ByteProcessingFunction.intToBytes(MainService.createNewTCPSocketThread());
                    udp.sendDatagramPacket(data, 0, data.length, packet.getAddress());
                } else if (string.equals("UDP_Port")) {
                    data = ByteProcessingFunction.intToBytes(MainService.createNewUDPSocketThread());
                    udp.sendDatagramPacket(data, 0, data.length, packet.getAddress());
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (udp != null)
                    udp.closeDatagramSocket();
            }
        }
    }
}
