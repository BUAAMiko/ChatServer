package groupwork.server;

import java.io.*;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainService {

    private static int port;
    public static PrintStream log;
    public static ExecutorService threadPool;

    public static int createNewTCPSocketThread() throws IOException, ClassNotFoundException {
        Thread network = new TCPSocketManagement();
        threadPool.execute(network);
        return MainService.port;
    }

    static int createNewUDPSocketThread() throws SocketException {
        Thread network = new UDPSocketManagement();
        threadPool.execute(network);
        return MainService.port;
    }

    public static void setPort(int port) {
        MainService.port = port;
    }

    public static void main(String args[]) {
        try {
            File file = new File("./log.txt");
            log = new PrintStream(new FileOutputStream(file, true), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        threadPool = Executors.newCachedThreadPool();
        threadPool.execute(new PortRequestService());
        Scanner input = new Scanner(System.in);
        while (input.hasNext()) {
            String command = input.next();
            switch (command) {
                case "connectNum":
                    System.out.println(TCPSocketManagement.socketNum);
            }
        }
    }
}
