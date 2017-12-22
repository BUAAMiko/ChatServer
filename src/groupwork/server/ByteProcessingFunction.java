package groupwork.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ByteProcessingFunction {

    /**
     * 传入字节数组读取\0的位置并返回
     *
     * @param src 字节数组
     * @return \0位于字符串的位置
     */
    static int byteArrayEffectiveLength(byte[] src) {
        String s = new String(src);
        return s.indexOf("\0");
    }

    /**
     * 传入一个int并且将其对应的字节数组返回
     *
     * @param value 传入的整形
     * @return 返回对应的字节数组
     */
    static byte[] intToBytes(int value)
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }

    /**
     * 传入一个字节数组并且将其对应的整形返回
     *
     * @param src 传入的字节数组
     * @param offset 字节数组中整形的偏置
     * @return 返回对应的整形
     */
    static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }

    /**
     * 将一个实例转换成字节数组并返回
     *
     * @param o 传入的实例
     * @return 返回一个字节数组
     * @throws IOException 转换的时候可能抛出异常
     */
    static byte[] objectToBytes(Object o) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(o);
        byte[] data = byteOut.toByteArray();
        objOut.close();
        byteOut.close();
        return data;
    }

    /**
     * 将一个字节数组转换成实例并返回
     *
     * @param data 传入的字节数组
     * @return 返回一个实例
     * @throws IOException 转换的时候可能抛出异常
     * @throws ClassNotFoundException 转换的时候可能抛出异常
     */
    static Object bytesToObject(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        Object o = objIn.readObject();
        objIn.close();
        byteIn.close();
        return o;
    }
}
