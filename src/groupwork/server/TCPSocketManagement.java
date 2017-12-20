package groupwork.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TCPSocketManagement extends Thread {
    static int socketNum = 0;
    private int port;
    private String ip;
    private ServerSocket serverSocket;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    /**
     * 无参数构造，分配一个空闲的端口
     *
     * @throws IOException 如果端口被占用将可能抛出异常
     */
    TCPSocketManagement() throws IOException {
        serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        MainService.setPort(port);
    }

    /**
     * 传入参数port用于监听，如端口被占用将会抛出异常
     *
     * @param port 监听的端口
     * @throws IOException 如果端口被占用将可能抛出异常
     */
    TCPSocketManagement(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.port = serverSocket.getLocalPort();
        MainService.setPort(this.port);
    }

    /**
     * 监听目标端口的连接请求
     *
     * @throws IOException 监听时可能会丢出异常
     */
    private void listenPort() throws IOException {
        MainService.log.println(new Date() + ":服务器正在监听 " + port + " 端口");
        socket = serverSocket.accept();
        ip = String.valueOf(socket.getInetAddress());
        MainService.log.println(new Date() + ":收到来自" + ip + "的连接，现有连接数" + (++socketNum));
    }

    /**
     * 向客户端发送心跳包，检查对方是否已经断开连接
     */
    private void checkConnection() {
        try {
            socket.sendUrgentData(0xFF);
        } catch (IOException e) {
            try {
                closeConnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 接受数据并保存在byte数组中
     *
     * @return 返回读取到的数据
     * @throws IOException 读取的时候可能抛出异常
     */
    private byte[] receiveData() throws IOException {
        byte[] data = new byte[in.available()];
        in.read(data);
        return data;
    }

    /**
     * 将发送的数据data作为参数传入然后发送数据
     * @param data 输出的数据
     * @throws IOException 写入的时候可能抛出异常
     */
    private void sendData(byte[] data) throws IOException {
        out.write(data);
    }

    /**
     * 关闭输入输出流和TCPSocket
     *
     * @throws IOException 关闭时可能会丢出异常
     */
    private void closeConnect() throws IOException {
        MainService.log.println(new Date() + ":服务器已检测到来自" + ip + "的连接已断开");
        socket.shutdownInput();
        in.close();
        out.close();
        socket.close();
        socketNum--;
    }

    @Override
    public void run() {
        try {
            //监听端口并且在有连接访问目标端口时初始化输入输出流
            listenPort();
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //定时检查客户端是否关闭了socket连接
        Timer timer = new Timer();
        TimerTask checkConnection = new TimerTask() {
            @Override
            public void run() {
                checkConnection();
            }
        };
        timer.schedule(checkConnection,5000);
        //如果连接没有被关闭则读取socket连接传输过来的数据并交由TCPPacketAnalysis处理,并返回结果
        while (socket.isConnected()) {
            try {
                byte[] data = receiveData();
                data = TCPPacketAnalysis.packetAnalysis(data);
                sendData(data);
            } catch (IOException | SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
