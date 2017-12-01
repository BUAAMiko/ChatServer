package groupwork.server;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatService {

    public static ExecutorService threadPool;

    public static void createNewSocketThread() {
        Thread network = new SocketManage(2333 + SocketManage.socketNum);
        threadPool.execute(network);
    }

    public static void main(String args[]) {
        threadPool = Executors.newCachedThreadPool();
        createNewSocketThread();
        Scanner input = new Scanner(System.in);
        while (input.hasNext()) {
            String command = input.next();
            switch (command) {
                case "connectNum":
                    System.out.println(SocketManage.socketNum);
            }
        }
    }
}
