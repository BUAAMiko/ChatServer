package groupwork.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.SQLException;

public class UDPSocketManagement extends Thread{

    private DatagramSocket socket;
    private DatagramPacket packet;

    /**
     * 无参数构造，分配一个空闲的端口
     *
     * @throws SocketException 可能会因为端口被占用而抛出异常
     */
    UDPSocketManagement() throws SocketException {
        //初始化成员变量并分配数据包的buff
        socket = new DatagramSocket(0);
        byte[] data = new byte[1024];
        packet = new DatagramPacket(data,data.length);
        //将分配的端口返回到主类
        MainService.setPort(socket.getLocalPort());
        Thread.currentThread().setName("" + socket.getLocalPort());
    }

    /**
     * 传入参数port用于监听，如端口被占用将会抛出异常
     *
     * @param port 监听的端口
     * @throws SocketException 可能会因为端口被占用而抛出异常
     */
    UDPSocketManagement(int port) throws SocketException {
        //初始化成员变量并分配数据包的buff
        socket = new DatagramSocket(port);
        byte[] data = new byte[1024];
        packet = new DatagramPacket(data,data.length);
        //将分配的端口返回到主类
        MainService.setPort(socket.getLocalPort());
        Thread.currentThread().setName("" + socket.getLocalPort());
    }

    /**
     * 根据传入的数据初始化数据包并且发送UDP数据包
     *
     * @param data 数据包中发送的数据的所在数组
     * @param offset data数组中数据的偏置
     * @param length data数组中数据的长度
     * @param address 目标地址
     * @throws IOException 可能在发送数据包的过程中抛出异常
     */
    void sendDatagramPacket(byte[] data, int offset, int length, InetAddress address) throws IOException {
        packet.setData(data,offset,length);
        packet.setAddress(address);
        socket.send(packet);
    }

    /**
     * 直接传入初始化好的UDP数据包并发送
     *
     * @param packet 所要传送的数据包
     * @throws IOException 可能在发送数据包的过程中抛出异常
     */
    private void sendDatagramPacket(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    /**
     * 监听端口并且接收一个数据长度为1024字节(1kb)的数据包，超过1kb部分将会被舍弃
     *
     * @return 返回接收到的数据包
     * @throws IOException 可能在接收数据报的过程中抛出异常
     */
    DatagramPacket receivedDatagramPacket() throws IOException {
        packet.setData(new byte[1024]);
        socket.receive(packet);
        return packet;
    }

    /**
     * 关闭UDP的socket
     */
    void closeDatagramSocket() {
        socket.close();
    }

    @Override
    public void run() {
        try {
            //监听并且接收发送到端口的数据包并交由UDPPacketAnalysis并将返回的字节数组发送
            receivedDatagramPacket();
            byte[] response = UDPPacketAnalysis.packetAnalysis(packet.getData());
            packet.setData(response);
            sendDatagramPacket(packet);
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
