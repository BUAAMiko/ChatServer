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

    /**
     * 无参的构造函数，将会以默认的url用户名和密码登录数据库
     *
     * @throws ClassNotFoundException 如果jdbc未能加载则可能会抛出异常
     * @throws SQLException 如果没有相应的数据库和表可能在新建时抛出异常
     */
    DatabaseManagement() throws ClassNotFoundException, SQLException {
        url = "jdbc:mysql://139.199.119.183:3306/ChatSoftware?useSSL=true";
        username = "jj";
        password = "15975302648";
        init();
    }

    /**
     * 有参数的构造函数，将会用参数初始化成员变量
     *
     * @param url 数据库的url
     * @param username 登录的用户名
     * @param password 登录的密码
     * @throws ClassNotFoundException 如果jdbc未能加载则可能会抛出异常
     * @throws SQLException 如果没有相应的数据库和表可能在新建时抛出异常
     */
    DatabaseManagement(String url, String username, String password) throws ClassNotFoundException, SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        init();
    }

    /**
     * 初始化jdbc驱动并且检测是否有相应的数据库（只有在没有数据库的情况下才会新建数据库和表）
     *
     * @throws ClassNotFoundException 如果jdbc未能加载则可能会抛出异常
     * @throws SQLException 如果没有相应的数据库和表可能在新建时抛出异常
     */
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
            //如果实例不为空则关闭
            close(statement);
            close(conn);
        }
    }

    /**
     * 连接数据库并初始化conn和statement
     *
     * @throws SQLException 如果没有相应的数据库和表可能抛出异常
     */
    private void connect() throws SQLException {
        conn = DriverManager.getConnection(url,username,password);
        statement = conn.createStatement();
    }

    /**
     * 如果传入的实例非空且未关闭，则关闭
     *
     * @param o 传入的实例
     * @throws SQLException 关闭的过程中可能抛出异常
     */
    private void close(Object o) throws SQLException {
        if (o != null) {
            if (o instanceof Connection && !((Connection) o).isClosed()) {
                ((Connection) o).close();
            } else if (o instanceof Statement && !((Statement) o).isClosed()) {
                ((Statement) o).close();
            } else if (o instanceof ResultSet && !((ResultSet) o).isClosed()) {
                ((ResultSet) o).close();
            }
        }
    }

    /**
     * 将ResultSet，Statement，Connection都传进来并且一起关闭
     *
     * @param conn 要关闭的conn
     * @param statement 要关闭的statement
     * @param rs 要关闭的rs
     * @throws SQLException 关闭的过程中可能抛出异常
     */
    private void close(Connection conn, Statement statement, ResultSet rs) throws SQLException {
        rs.close();
        statement.close();
        conn.close();
    }

    /**
     * 新建一个名为ChatSoftware的数据库，并且新建相应的表
     */
    private void createDatabase() throws SQLException {
        sql = "CREATE DATABASE ChatSoftware";
        conn = DriverManager.getConnection("jdbc:mysql://139.199.119.183:3306/?useSSL=true",username,password);
        statement = conn.createStatement();
        statement.execute(sql);
        close(statement);
        close(conn);
        createTables();
    }

    /**
     * 新建一个UserInfo表和ChatMessage表
     *
     * @throws SQLException 新建表的时候可能因为sql语句而抛出异常
     */
    private void createTables() throws SQLException {
        createUserInfoTable();
        createChatMessageTable();
    }

    /**
     * 新建一个UserInfo表
     *
     * @throws SQLException 新建表的时候可能因为sql语句而抛出异常
     */
    private void createUserInfoTable() throws SQLException {
        sql = "CREATE TABLE UserInfo" +
                "(" +
                "    Id INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                "    Username TEXT NOT NULL," +
                "    Password TEXT NOT NULL" +
                ");";
        updateSql();
    }

    /**
     * 新建一个ChatMessage表
     *
     * @throws SQLException 新建表的时候可能因为sql语句而抛出异常
     */
    private void createChatMessageTable() throws SQLException {
        sql = "CREATE TABLE ChatMessage\n" +
                "(\n" +
                "    Id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "    Date DATETIME NOT NULL,\n" +
                "    `From` INT NOT NULL,\n" +
                "    `To` INT NOT NULL,\n" +
                "    MessageType LONGTEXT NOT NULL,\n" +
                "    Message LONGTEXT,\n" +
                "    SubMessage LONGTEXT\n" +
                ");";
        updateSql();
    }

    /**
     * 设定将要执行的sql语句
     *
     * @param sql sql语句
     */
    void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * 对数据库进行查找并且将返回值通过Map保存在一个List中
     *
     * @return 返回查询的结果
     * @throws SQLException 在执行sql语句的时候可能抛出异常
     */
    List querySql() throws SQLException {
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
            for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                result.put(rsMetaData.getColumnName(i),rs.getString(i));
            }
            resultList.add(result);
        }
        close(conn,statement,rs);
        sql = null;
        return resultList;
    }

    /**
     * 对数据库的内容进行变更和添加并且返回新增的Id
     *
     * @return 返回新增数据行的Id
     * @throws SQLException 在执行sql语句的时候可能抛出异常
     */
    int updateSql () throws SQLException {
        //执行sql语句
        if (sql == null) {
            System.out.println("empty sql");
            return -1;
        }
        connect();
        statement.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
        //获取新增的数据行的Id
        ResultSet result = statement.getGeneratedKeys();
        int num = -1;
        if (result.next()) {
            num = result.getInt(1);
        }
        close(statement);
        close(conn);
        sql = null;
        return num;
    }
}
