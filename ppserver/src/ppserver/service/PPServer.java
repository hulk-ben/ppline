package ppserver.service;

import ppcommen.Message;
import ppcommen.MessageType;
import ppcommen.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class PPServer {

    private ServerSocket serverSocket = null;
    private static DatabaseService databaseService;
//
//    static {
//
//    }

    private boolean checkUser(User user) {
        if (ManagerClientThread.getThread(user.getUserID()) == null) {
            try {
                return databaseService.queryUser(user);
            } catch (Exception e) {
                System.out.println("用户查询失败");
                return false;
            }
        }
        return false;

    }

    public PPServer() {
        try {
            serverSocket = new ServerSocket(52113);
            System.out.println("服务器在52113");

            while (true) {
                try {
                    databaseService = new  DatabaseService();
                } catch (Exception e) {
                    System.out.println("数据库连接失败");
                    e.printStackTrace();
                }
                Socket socket = serverSocket.accept();
                ObjectInputStream ino = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outo = new ObjectOutputStream(socket.getOutputStream());
                User o = (User) ino.readObject();
                Message message = new Message();
                if (checkUser(o)) {
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    outo.writeObject(message);
                    ServerConnectClientThread serverThread = new ServerConnectClientThread(o.getUserID(), socket,databaseService);
                    serverThread.start();
                    ManagerClientThread.addThreads(o.getUserID(), serverThread);

                } else {
                    System.out.println(o.getUserID() + "验证失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    outo.writeObject(message);
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
