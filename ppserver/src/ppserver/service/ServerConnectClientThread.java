package ppserver.service;

import ppcommen.Message;
import ppcommen.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

public class ServerConnectClientThread extends Thread {
    private String userID;//连接的用户
    private Socket socket;
    private DatabaseService dbconncation;

    public ServerConnectClientThread(String userID, Socket socket, DatabaseService dbconnection) {
        this.userID = userID;
        this.socket = socket;
        this.dbconncation = dbconnection;
    }

    @Override
    public void run() {
        label:
        while (true) {

            try {
                ObjectInputStream outo = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) outo.readObject();
                System.out.println("服务端保持连接" + userID + "已连接");
                switch (message.getMesType()) {
                    case MessageType.MESSAGE_CAT:
                       getoffFlie(userID);
                        getOffmess(userID);

                    case MessageType.MESSAGE_COMM_MES:

                        try {
                            if (message.getGetter() == null) {
                                sendMessage(message);
                            } else sendPrivatemessage(message);
                        } catch (IOException e) {
                            System.out.println("信息发送失败");
                        } catch (SQLException e) {
                            System.out.println("数据库查询出错");;
                        }
                        break;
                    case MessageType.MESSAGE_FILE_MES:
                        if (isOnline(message.getGetter())) {
                            sendFile(message);
                            System.out.println(message.getSender() + "给" + message.getGetter() + "发了个文件");
                        }else {
                            dbconncation.insertFile(message);
                        }


                        break;


                    case MessageType.MESSAGE_GET_ONLINE_USERLIST:

                        getonline();
                        break;
                    case MessageType.MESSAGE_CLIENT_EXIT:
                        boolean b = ManagerClientThread.removeServerConnectClientThread(userID);
                        if (b) {
                            socket.close();
                            System.out.println(userID + "已断开");
                        } else System.out.println("删除失败");
                        break label;
                }
            } catch (Exception e) {
                boolean b = ManagerClientThread.removeServerConnectClientThread(userID);
                if (b) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        System.out.println("关闭失败");
                    }
                } else System.out.println("删除失败");
                break;
            }

        }
    }

    private void getoffFlie(String userID) {
        String text = "无文件";
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setGetter(userID);

        try {
            String sender = dbconncation.querUser(userID);
            String filename = dbconncation.querFilename(userID);
            byte[] offFile = dbconncation.getOffFile(userID);
            message.setContent(filename);
            message.setBytes(offFile);
            message.setSender(sender);
            sendFile(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private void getOffmess(String userID) throws SQLException, IOException, InterruptedException {
        String text = "无消息";
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setGetter(userID);
        message.setSender("服务器");


        String offMessage = dbconncation.getOffMessage(userID);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        if (!"nothing".equals(offMessage)) {
            System.out.println(offMessage);
            text = offMessage;

        }
        message.setContent(text);
        objectOutputStream.writeObject(message);

    }

    private boolean sendMessage(Message message) {
        HashMap<String, ServerConnectClientThread> threads = ManagerClientThread.getThreads();
        try {String users = dbconncation.getUsers();
            String[] s = users.split(" ");
            System.out.println(Arrays.toString(s));
            for (String s1 : s) {
                if (isOnline(s1)) {
                    threads.forEach((key, vulue) -> {
                        try {
                            if (!(message.getSender().equals(key))) {
                                message.setGetter(key);
                                sendPrivatemessage(message);
                            }
                        } catch (IOException | SQLException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    message.setGetter(s1);
                    dbconncation.insertMessage(message);
                }
            }


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendPrivatemessage(Message message) throws IOException, SQLException {
        if (message.getContent() != null) {
            dbconncation.insertMessage(message);
        }
        if (isOnline(message.getGetter())) {
            ServerConnectClientThread thread = ManagerClientThread.getThread(message.getGetter());
            ObjectOutputStream outmessage = new ObjectOutputStream(thread.getSocket().getOutputStream());

            outmessage.writeObject(message);
            System.out.println(message.getSender() + "对" + message.getGetter() + "发了一条消息");
        } else {
            message.setGetter(message.getSender());
            message.setContent("对方离线");
            ObjectOutputStream o = new ObjectOutputStream(ManagerClientThread.getThread(message.getGetter()).getSocket().getOutputStream());
            o.writeObject(message);
        }
    }

    private String getonline() throws IOException, SQLException {
        String time = dbconncation.getTime();
        System.out.println(userID + "查看用户列表");
        String olineUser = ManagerClientThread.getOlineUser();
        Message message1 = new Message("test", userID, olineUser, time, MessageType.MESSAGE_RETURN_ONLINE_USERLIST);
        ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
        oout.writeObject(message1);
        return olineUser;
    }

    public static boolean isOnline(String userID) {
        String olineUser = ManagerClientThread.getOlineUser();
        String[] s = olineUser.split(" ");
        for (String s1 : s) {
            if (userID.equals(s1)) return true;
        }
        return false;

    }


    public static boolean sendFile(Message message) {
        String getter = message.getGetter();
        try {
            ObjectOutputStream outfile = new ObjectOutputStream(ManagerClientThread.getThread(getter).getSocket().getOutputStream());
            outfile.writeObject(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Socket getSocket() {
        return socket;
    }

    public static void main(String[] args) throws SQLException {
//        DatabaseService databaseService = new DatabaseService();
//        ArrayList<String> offMessage = databaseService.getOffMessage("qsy");
//        System.out.println(offMessage);
//        String text = "";
//        if (offMessage.size() != 0) {
//            for (String s : offMessage) {
//                text += s + "\n";
//            }
//
//
//        }
//        System.out.println(text);

    }
}
