package groupwork.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManage {

    private String url;
    private String username;
    private String password;
    Connection conn;
    private Statement statement;

    DatabaseManage() throws ClassNotFoundException {
        init();
        url = "127.0.0.1";
        username = "root";
        password = "123456";
    }

    DatabaseManage(String url,String username,String password) throws ClassNotFoundException {
        init();
        this.url = url;
        this.username = username;
        this.password = password;
    }

    void init() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
    }

    void connect() throws SQLException {
        conn = DriverManager.getConnection(url,username,password);
        statement = conn.createStatement();
    }

    void close(Object o) throws SQLException {
        if (o != null) {
            if (o instanceof Connection) {
                ((Connection) o).close();
            } else if (o instanceof Statement) {
                ((Statement) o).close();
            } else if (o instanceof ResultSet) {
                ((ResultSet) o).close();
            }
        }
    }

    void close(Connection conn, Statement statement, ResultSet rs) throws SQLException {
        rs.close();
        statement.close();
        conn.close();
    }

    List quarySql(String sql) throws SQLException {
        List resultList = new ArrayList();
        connect();
        ResultSet rs = statement.executeQuery(sql);
        ResultSetMetaData rsMetaData = rs.getMetaData();
        while (rs.next()) {
            Map<String, String> result = new HashMap<String, String>();
            for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
                result.put(rsMetaData.getColumnName(i),rs.getString(i));
            }
            resultList.add(result);
        }
        close(conn,statement,rs);
        return resultList;
    }

    int updateSql (String sql) throws SQLException {
        connect();
        int result = statement.executeUpdate(sql);
        close(statement);
        close(conn);
        return result;
    }
}
