package ppserver.service;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import ppcommen.Message;
import ppcommen.User;

import javax.sql.DataSource;
import javax.swing.plaf.IconUIResource;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseService {
    private static Properties properties;
    private static DataSource dataSource;
    private Connection connection;

    static {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("/home/ubuntu/.ppconf/druid.properties"));
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public DatabaseService() throws SQLException {

        this.connection = dataSource.getConnection();
    }

    public String getTime() throws SQLException {
        String sql = "SELECT NOW();";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            return resultSet.getString(1);
        }
        return LocalDateTime.now().toString();
    }

    public boolean insertMessage(Message message) throws SQLException {
        String getter = "'" + message.getGetter() + "'";
        String sender = "'" + message.getSender() + "'";
        String text = "'" + message.getContent() + "'";
        String sql = String.format("INSERT INTO message(userId,sendID,message,time) values(%s,%s,%s,NOW());", getter, sender, text);
//        for (String s : ManagerClientThread.getOlineUser().split(" ")) {
//            if (s.equals(getter)) {
//                sql = String.format("INSERT INTO message(userId,sendID,message,status,time) values(%s,%s,%s,%d,NOW());",getter,sender,text,1);
//            }
//        }
        if (ServerConnectClientThread.isOnline(getter)) {
            sql = String.format("INSERT INTO message(userId,sendID,message,status,time) values(%s,%s,%s,%d,NOW());", getter, sender, text, 1);
        }
        Statement statement = connection.createStatement();
        int i = statement.executeUpdate(sql);

        if (i > 0) {
            return true;
        }
        return false;
    }
    public String querUser(String user) throws SQLException {
        String sql = String.format("SELECT sendID FROM message WHERE message.userID = '%s' and status = 3 Limit 1;", user);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        String mess = "";
        String sender = "服务器";
        while (resultSet.next()){

            sender = resultSet.getString(1);

        }
        mess += sender;
        return mess;

    }
    public String querFilename(String user) throws SQLException {
        String sql = String.format("SELECT message FROM message WHERE message.userID = '%s' and status = 3 Limit 1;", user);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        String path= "";

        while (resultSet.next()){
            path = resultSet.getString(1);
        }
        File file = new File(path);
        return file.getName();

    }
    public byte[] getOffFile(String user) throws SQLException {
        String sql = String.format("SELECT message FROM message WHERE message.userID = '%s' and status = 3 Limit 1;", user);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        String path = "";

        while (resultSet.next()){
            path += resultSet.getString(1);
        }
        sql = String.format("UPDATE message set status = 1 where userID='%s' and message = '%s'", user,path);
        statement.executeUpdate(sql);
        System.out.println(path);
        File file = new File(path);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bfin = new BufferedInputStream(fileInputStream);

            byte[] bytes = new byte[(int) file.length()];
            bfin.read(bytes);

            sql = String.format("DELETE from message where status = 1 and userID = '%s';",user);
            statement.executeUpdate(sql);
            return bytes;
        } catch (IOException e) {
            System.out.println("file not exitis");
            return null;
        }



    }

    public String getOffMessage(String user) throws SQLException {

        String sql = String.format("SELECT sendID,message FROM message WHERE message.userID = '%s' and status = 0;", user);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        String sender = "服务器";
        String text = "";
        String mess = "nothing";
        int i = 0;
        while (resultSet.next()) {
            if (i == 0) {
                mess = "";
            }
            sender = resultSet.getString(1);
            mess += sender + "对你说:";
            text = resultSet.getString(2);
            mess += text + "\n";
            i ++;
            System.out.println(mess);
        }
        sql = String.format("UPDATE message set status = 1 where userID='%s'", user);
        statement.executeUpdate(sql);
        sql = String.format("DELETE from message where message = null;");
        statement.executeUpdate(sql);

        return mess;
    }

    public ArrayList<String> getMessage(User user) throws SQLException {
        String sql = String.format("SELECT message FROM message WHERE message.userID = '%s';", user.getUserID());
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<String> strings = new ArrayList<>(20);
        while (resultSet.next()) {
            strings.add(resultSet.getString(1));
        }
        return strings;
    }

    public boolean queryUser(User user) throws Exception {
        String userId = "'" + user.getUserID() + "'";
        String sql = String.format("SELECT userID,passWD FROM user WHERE userID = %s;", userId);
        System.out.println(sql);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        String passwd = null;
        while (resultSet.next()) {
            passwd = resultSet.getString(2);
        }
        if (user.getPassWD().equals(passwd)) {
            System.out.println(userId + ":login succeed");
            return true;
        }

        System.out.println(userId + ":login fail");
        return false;
    }

    public boolean insertFile(Message message) {
        String dirname = "/home/ubuntu/ppfile";
        String sql = String.format("INSERT INTO message(userID, sendID, message, status,time) " +
                "VALUES('%s','%s','"+dirname+"/%s',3,NOW());",message.getGetter(),message.getSender(),message.getContent());
        try {
            File dir = new File(dirname);
            File file = new File(dir, message.getContent());
            dir.mkdirs();
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            byte[] bytes = message.getBytes();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(message.getSender()+"发的文件丢失");
            return false;
        }
    }



    public String getUsers() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT userID FROM user;");
        String name = "";
        while (resultSet.next()){
            name += resultSet.getString(1) + " ";

        }
        System.out.println(name);
        return name;
    }
}
