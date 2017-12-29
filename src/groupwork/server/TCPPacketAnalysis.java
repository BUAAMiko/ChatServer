package groupwork.server;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        MainService.log.println(new Date() + "[" + Thread.currentThread().getName() + "]:数据包类型为" + s + "，正在返回数据");
        byte[] response = new byte[1];
        switch (s) {
            case "Ask_Message": {
                //获取聊天记录
                s = "SELECT * FROM ChatMessage WHERE `To` = " + map.get("Id") + " OR `From` = " + map.get("Id");
                MainService.db.setSql(s);
                List message = MainService.db.querySql();
                //获取好友
                s = "SELECT DISTINCT `From` FROM ChatMessage WHERE `To` = " + map.get("Id");
                List friendIdList = MainService.db.querySql();
                List friend = new ArrayList();
                for (int i = 0; i < friendIdList.size(); i++) {
                    Map tmp = (Map) friendIdList.get(i);
                    s = "SELECT Username FROM UserInfo WHERE Id = " + tmp.get("From");
                    List result = MainService.db.querySql();
                    if (!result.isEmpty()) {
                        Map m = (Map) result.get(0);
                        m.put("Id", tmp.get("From"));
                        friend.add(m);
                    }
                }
                Map m = new HashMap();
                m.put("Message",message);
                m.put("Friends",friend);
                response = Functions.objectToBytes(m);
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
                response = ((String) map.get("PacketIdentify")).getBytes();
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
