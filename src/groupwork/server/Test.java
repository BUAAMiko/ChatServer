package groupwork.server;

import sun.security.provider.MD5;

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

class client extends Thread {
    @Override
    public void run() {
        try {
            Map m = new HashMap();
            m.put("Type","Send_File");
            m.put("From","1");
            m.put("To","2");
            File f = new File("file.txt");
            byte[] data = Functions.objectToBytes(f);
            m.put("Data",data);
            MD5 a = new MD5();
            m.put("PacketIdentify", "123");
            m.put("FileName",f.getName());
            Socket s = new Socket("127.0.0.1",2333);
            BufferedInputStream in = new BufferedInputStream(s.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
            byte[] Data = Functions.objectToBytes(m);
            out.write(Data.length);
            out.flush();
            out.write(Data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}