package groupwork.server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws IOException {
        TCPSocketManagement tcp = new TCPSocketManagement(2333);
        tcp.start();
        File f = new File("/home/jj/log.txt");
        f.getName();
        Map m = new HashMap();
        m.put("Type","Send_File");
        m.put("From",1);
        m.put("To",2);
        m.put("File",f);
        byte[] data = Functions.objectToBytes(m);
        Socket socket = new Socket("127.0.0.1",2333);
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
        out.write(Functions.intToBytes(data.length));
        out.flush();
        out.write(data);
        out.flush();
    }
}