package groupwork.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketManage extends Thread {
    static int socketNum = 0;
    int port;
    DatabaseManage db;
    ServerSocket serverSocket;
    Socket socket;
    OutputStream out;
    InputStream in;


    SocketManage() {
        try {
            db = new DatabaseManage();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    SocketManage(int port) {
        try {
            db = new DatabaseManage();
            this.port = port;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void listenPort() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("服务器正在监听 " + port + " 端口");
        socket = serverSocket.accept();
        System.out.println("收到来自" + socket.getInetAddress() + "的连接，现有连接数" + (++socketNum));
        ChatService.createNewSocketThread();
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
        while (socket.isConnected()) {
            System.out.println("connect" + socket.isConnected() + "close" + socket.isClosed());
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("stop");
        socketNum--;
    }
}
