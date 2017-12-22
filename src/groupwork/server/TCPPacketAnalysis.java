package groupwork.server;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TCPPacketAnalysis {

    static byte[] packetAnalysis(byte[] data) throws SQLException, IOException, ClassNotFoundException {
        ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(data));
        Map map = (Map) objIn.readObject();
        String s = (String) map.get("Type");
        byte[] response = new byte[1];
        switch (s) {
            case "Ask_Message": {
                s = "SELECT * FROM ChatMessage WHERE `From` = " + map.get("From") + " AND `To` = " + map.get("To");
                MainService.db.setSql(s);
                List tmp = MainService.db.querySql();
                List l = ByteProcessingFunction.topTenList(tmp);
                response = ByteProcessingFunction.objectToBytes(l);
            }
            break;
            case "Send_Picture": {
                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                s = "INSERT INTO ChatMessage (Date,`From`,`To`,MessageType,SubMessage) VALUES (\""
                        + date.format(new Date()) + "\",\""
                        + map.get("From") + "\",\""
                        + map.get("To") + "\",\""
                        + "Picture" + "\",\""
                        + map.get("FileName") + "\""
                        + ")";
                MainService.db.setSql(s);
                int id = MainService.db.updateSql();
                String fileName = id + "-" + map.get("FileName");
                FileOutputStream out = new FileOutputStream(fileName);
                out.write((byte[]) map.get("Data"));
                s = "UPDATE ChatMessage SET Message = \"" + fileName + "\" WHERE Id = " + id;
                MainService.db.setSql(s);
                MainService.db.updateSql();
                response = ((String) map.get("PacketIdentify")).getBytes();
            }
            break;
            case "Ask_Picture": {

            }
            break;
            case "Send_File": {
                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                s = "INSERT INTO ChatMessage (Date,`From`,`To`,MessageType,SubMessage) VALUES (\""
                        + date.format(new Date()) + "\",\""
                        + map.get("From") + "\",\""
                        + map.get("To") + "\",\""
                        + "File" + "\",\""
                        + map.get("FileName") + "\""
                        + ")";
                MainService.db.setSql(s);
                int id = MainService.db.updateSql();
                String fileName = id + "-" + map.get("FileName");
                FileOutputStream out = new FileOutputStream(fileName);
                out.write((byte[]) map.get("Data"));
                s = "UPDATE ChatMessage SET Message = \"" + fileName + "\" WHERE Id = " + id;
                MainService.db.setSql(s);
                MainService.db.updateSql();
                response = ((String) map.get("PacketIdentify")).getBytes();
            }
            break;
            case "Ask_File": {

            }
        }
        return response;
    }
}
