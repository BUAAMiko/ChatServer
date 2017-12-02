package groupwork.server;

import sun.applet.Main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TCPSocketManagement extends Thread {
    static int socketNum = 0;
    private int port;
    private String ip;
    private DatabaseManagement db;
    private ServerSocket serverSocket;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;


    TCPSocketManagement() throws ClassNotFoundException, IOException {
        db = MainService.db;
        serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        MainService.setPort(port);
    }

    TCPSocketManagement(int port) throws ClassNotFoundException, IOException {
        db = MainService.db;
        serverSocket = new ServerSocket(port);
        this.port = serverSocket.getLocalPort();
        MainService.setPort(this.port);
    }

    private void listenPort() throws IOException {
        MainService.log.println(new Date() + ":服务器正在监听 " + port + " 端口");
        socket = serverSocket.accept();
        ip = String.valueOf(socket.getInetAddress());
        MainService.log.println(new Date() + ":收到来自" + ip + "的连接，现有连接数" + (++socketNum));
    }

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

    private void closeConnect() throws IOException {
        MainService.log.println(new Date() + "服务器已检测到来自" + ip + "的连接已断开");
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
        Timer timer = new Timer();
        TimerTask checkConnection = new TimerTask() {
            @Override
            public void run() {
                checkConnection();
            }
        };
        timer.schedule(checkConnection,5000);
        while (socket.isConnected()) {
            try {
                byte[] data = new byte[in.available()];
                in.read(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
