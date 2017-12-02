package groupwork.server;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TCPPacketAnalysis {

    static byte[] pakcetAnalysis(byte[] data) throws SQLException, IOException, ClassNotFoundException {
        ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(data));
        Map map = (Map) objIn.readObject();
        String s = (String) map.get("Type");
        byte[] response = new byte[1];
        switch (s) {
            case "Media": {

            }
            break;
            case "Picture": {

            }
            break;
            case "File": {

            }
        }
        return response;
    }
}
