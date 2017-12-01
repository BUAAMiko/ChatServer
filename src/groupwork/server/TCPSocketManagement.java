package groupwork.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class TCPSocketManagement extends Thread {
    static int socketNum = 0;
    int port;
    String ip;
    DatabaseManagement db;
    ServerSocket serverSocket;
    Socket socket;
    DataOutputStream out;
    DataInputStream in;


    TCPSocketManagement() throws ClassNotFoundException, IOException {
        db = new DatabaseManagement();
        serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        MainService.setPort(port);
    }

    TCPSocketManagement(int port) throws ClassNotFoundException, IOException {
        db = new DatabaseManagement();
        serverSocket = new ServerSocket(port);
        this.port = serverSocket.getLocalPort();
        MainService.setPort(this.port);
    }

    public void listenPort() throws IOException {
        MainService.log.println(new Date() + ":服务器正在监听 " + port + " 端口");
        socket = serverSocket.accept();
        ip = String.valueOf(socket.getInetAddress());
        MainService.log.println(new Date() + ":收到来自" + ip + "的连接，现有连接数" + (++socketNum));
    }

    void closeConnect() throws IOException {
        socket.shutdownInput();
        in.close();
        out.close();
        socket.close();
        socketNum--;
    }

    @Override
    public void run() {
        try {
            listenPort();
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            byte[] b = new byte[in.available()];
            int i = 0;
            while (true) {
                b[i++] = in.readByte();
            }
        } catch (IOException e) {
            MainService.log.println(new Date() + "服务器已检测到来自" + ip + "的连接已断开");
            try {
                closeConnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            socketNum--;
        }
    }
}
