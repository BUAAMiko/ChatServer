package groupwork.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManagement {

    private String url;
    private String username;
    private String password;
    private Connection conn;
    private Statement statement;
    private String sql;

    DatabaseManagement() throws ClassNotFoundException, SQLException {

        init();
    }

    DatabaseManagement(String url, String username, String password) throws ClassNotFoundException, SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        init();
    }

    private void init() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        try {
            connect();
        } catch (SQLException e) {
            createDatabase();
            try {
                connect();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            close(statement);
            close(conn);
        }
    }

    private void connect() throws SQLException {
        conn = DriverManager.getConnection(url,username,password);
        statement = conn.createStatement();
    }

    private void close(Object o) throws SQLException {
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

    private void close(Connection conn, Statement statement, ResultSet rs) throws SQLException {
        rs.close();
        statement.close();
        conn.close();
    }

    private void createDatabase() {
        sql = "CREATE DATABASE ChatSoftware";
        try {
            conn = DriverManager.getConnection("jdbc:mysql://139.199.119.183:3306/?useSSL=true",username,password);
            statement = conn.createStatement();
            statement.execute(sql);
            close(statement);
            close(conn);
            createTables();
        } catch (SQLException e) {
            System.out.print(e.getErrorCode());
            e.printStackTrace();
        }
    }

    private void createTables() {
        sql = "CREATE TABLE userinfo" +
                "(" +
                "    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "    username TEXT NOT NULL," +
                "    password TEXT NOT NULL" +
                ");";
        try {
            updateSql();
        } catch (SQLException e) {
            System.out.print(e.getErrorCode());
            e.printStackTrace();
        }
    }

    void setSql(String sql) {
        this.sql = sql;
    }

    List quarySql() throws SQLException {
        if (sql == null) {
            System.out.println("empty sql");
            return null;
        }
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
        sql = null;
        return resultList;
    }

    int updateSql () throws SQLException {
        if (sql == null) {
            System.out.println("empty sql");
            return -1;
        }
        connect();
        int result = statement.executeUpdate(sql);
        close(statement);
        close(conn);
        sql = null;
        return result;
    }
}
