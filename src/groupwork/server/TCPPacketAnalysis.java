package groupwork.server;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TCPPacketAnalysis {

    /**
     * 将传入的数据进行解析并且返回相应的数据，在调用函数中进行发送，具体格式见Readme.md
     *
     * @param data 接收到的数据
     * @return 准备返回的数据
     * @throws SQLException SQL查询的时候可能抛出异常
     * @throws IOException 实例转换字节数组的时候可能抛出异常
     * @throws ClassNotFoundException jdbc驱动未能成功加载时可能抛出异常
     */
    static byte[] packetAnalysis(byte[] data) throws SQLException, IOException, ClassNotFoundException {
        ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(data));
        Map map = (Map) objIn.readObject();
        String s = (String) map.get("Type");
        byte[] response = new byte[1];
        switch (s) {
            case "Ask_Message": {
                s = "SELECT * FROM ChatMessage WHERE `From` = " + map.get("From");
                MainService.db.setSql(s);
                List l = MainService.db.querySql();
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
