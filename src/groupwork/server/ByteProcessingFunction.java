package groupwork.server;

import java.io.*;

public class ByteProcessingFunction {

    static int byteArrayEffectiveLength(byte[] src) {
        String s = new String(src);
        return s.indexOf("END");
    }

    static byte[] intToBytes(int value)
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }

    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }

    static byte[] objectToBytes(Object o) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(o);
        byte[] data = byteOut.toByteArray();
        objOut.close();
        byteOut.close();
        return data;
    }

    static Object bytesToObject(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        Object o = objIn.readObject();
        objIn.close();
        byteIn.close();
        return o;
    }
}
