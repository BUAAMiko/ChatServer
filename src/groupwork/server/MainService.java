package groupwork.server;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainService {

    private static int port;

    public static ExecutorService threadPool;

    public static void createNewSocketThread() {
        try {
            Thread network = new TCPSocketManagement();
            threadPool.execute(network);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public static int getPort() {
        createNewSocketThread();
        return MainService.port;
    }

    public static void setPort(int port) {
        MainService.port = port;
    }

    public static void main(String args[]) {
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
