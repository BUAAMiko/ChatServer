package groupwork.server;

import sun.applet.Main;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UDPPacketAnalysis {
    static byte[] pakcetAnalysis(byte[] data) throws SQLException, IOException, ClassNotFoundException {
        ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(data));
        Map map = (Map) objIn.readObject();
        String s = (String) map.get("Type");
        byte[] response = new byte[1];
        switch (s) {
            case "SQL_Q": {
                s = (String) map.get("SQL");
                MainService.db.setSql(s);
                List l = MainService.db.quarySql();
                response = ByteProcessingFunction.objectToBytes(l);
            }
            break;
            case "Register": {
                s = "INSERT INTO UserInfo(Username, Password) VALUES (\"" + map.get("Username") + "\",MD5(\"" + map.get("Password") + "\"))";
                MainService.db.setSql(s);
                List l = MainService.db.quarySql();
                if (l.get(0) instanceof Map) {
                    int Id = (int) ((Map) l.get(0)).get("Id");
                    response = ByteProcessingFunction.intToBytes(Id);
                }
            }
            break;
            case "Login": {
                s = "SELECT * FROM UserInfo WHERE Id = " + map.get("Id") + " AND Password = MD5(\"" + map.get("Password") +"\")";
                MainService.db.setSql(s);
                List l = MainService.db.quarySql();
                if (l.get(0) instanceof Map) {
                    String Username = (String) ((Map) l.get(0)).get("Username");
                    response = Username.getBytes();
                } else {
                    response = "FALSE".getBytes();
                }
            }
            break;
            case "Ask_Message": {
                s = "SELECT * FROM ChatMessage WHERE `To` = " + map.get("Id");
                MainService.db.setSql(s);
                List l = MainService.db.quarySql();
                response = ByteProcessingFunction.objectToBytes(l);
            }
            case "Send_Message": {
                s = "INSERT INTO ChatMessage (Date,`From`,`To`,Type,Message,SubMessage) VALUES (\""
                        + map.get("Date") + "\",\""
                        + map.get("From") + "\",\""
                        + map.get("To") + "\",\""
                        + map.get("Type") + "\",\""
                        + map.get("Message") + "\",\""
                        + map.get("SubMessage") + "\""
                        + ")";
                MainService.db.setSql(s);
                MainService.db.updateSql();
                int i = Integer.parseInt((String) map.get("Id"));
                response = ByteProcessingFunction.intToBytes(i);
            }
        }
        return response;
    }
}
