package groupwork.server;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
                s = "SELECT * FROM ChatMessage WHERE `To` = " + map.get("Id");
                MainService.db.setSql(s);
                List l = MainService.db.querySql();
                response = Functions.objectToBytes(l);
            }
            break;
            case "Send_File":
            case "Send_Picture": {
                //获得系统时间和文件信息
                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String type;
                if (((String) map.get("Type")).equals("Send_File"))
                    type = "File";
                else
                    type = "Picture";
                File f = (File) map.get("File");
                //更新数据库并获得新增数据行的Id
                s = "INSERT INTO ChatMessage (Date,`From`,`To`,MessageType,SubMessage) VALUES (\""
                        + date.format(new Date()) + "\",\""
                        + map.get("From") + "\",\""
                        + map.get("To") + "\",\""
                        + type + "\",\""
                        + f.getName() + "\""
                        + ")";
                MainService.db.setSql(s);
                int id = MainService.db.updateSql();
                //将文件保存到本地并且更新数据库中文间的位置
                String path = "./File/" + id + "-" + f.getName();
                Functions.saveFile(f,path);
                s = "UPDATE ChatMessage SET Message = \"" + path + "\" WHERE Id = " + id;
                MainService.db.setSql(s);
                MainService.db.updateSql();
                //加工返回数据并返回
                Map m = new HashMap();
                m.put("PacketIdentify",m.get("PacketIdentify"));
                m.put("Id",id);
                response = Functions.objectToBytes(m);
            }
            break;
            case "Ask_File":
            case "Ask_Picture": {
                //查询数据库
                s = "SELECT * FROM ChatMessage WHERE Id =" + map.get("Id");
                MainService.db.setSql(s);
                List l = MainService.db.querySql();
                //获得文件的信息
                String path = (String) ((Map)(l.get(0))).get("Message");
                String type = (String) ((Map)(l.get(0))).get("MessageType");
                StringBuilder name = new StringBuilder(path);
                name.delete(0,name.indexOf("-"));
                //从本地目录获取File实例
                File f = new File(path);
                //加工返回数据并返回
                Map m = new HashMap();
                m.put("FileName",name.toString());
                m.put("File",f);
                m.put("Type",type);
                response = Functions.objectToBytes(m);
            }
        }
        return response;
    }
}
