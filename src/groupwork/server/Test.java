package groupwork.server;

import sun.security.provider.MD5;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws IOException {
        Map m = new HashMap();
        m.put("From",1123);
        m.put("To",2123);
        m.put("Type","Send_Message");
        m.put("Message","");
        byte[] data = ByteProcessingFunction.objectToBytes(m);
        System.out.print(data.length);
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
            byte[] data = ByteProcessingFunction.objectToBytes(f);
            m.put("Data",data);
            MD5 a = new MD5();
            m.put("PacketIdentify", "123");
            m.put("FileName",f.getName());
            Socket s = new Socket("127.0.0.1",2333);
            BufferedInputStream in = new BufferedInputStream(s.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
            byte[] Data = ByteProcessingFunction.objectToBytes(m);
            out.write(Data.length);
            out.flush();
            out.write(Data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}