package groupwork.server;

import java.io.IOException;
import java.util.Date;
import java.net.*;

public class PortRequestService extends Thread {

    static UDPSocketManagement udp;
    DatagramPacket packet;

    @Override
    public void run() {
        while(true) {
            try {
                //初始化一个UDP接口并接收数据包
                udp = new UDPSocketManagement(2333);
                packet = udp.receivedDatagramPacket();
                byte[] data = packet.getData();
                String string = new String(data, 0, Functions.byteArrayEffectiveLength(data));
                //根据数据包中的字符串来确定新建什么类型的端口
                if (string.equals("TCP_Port")) {
                    data = Functions.intToBytes(MainService.createNewTCPSocketThread());
                    udp.sendDatagramPacket(data, 0, data.length, packet.getAddress());
                } else if (string.equals("UDP_Port")) {
                    data = Functions.intToBytes(MainService.createNewUDPSocketThread());
                    udp.sendDatagramPacket(data, 0, data.length, packet.getAddress());
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                //如果发生错误且udp端口新建成功则关闭udp端口
                if (udp != null)
                    udp.closeDatagramSocket();
            }
        }
    }
}
