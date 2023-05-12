package ppserver.service;

import ppcommen.Message;
import ppcommen.MessageType;
import ppcommen.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class PPServer {

    private ServerSocket serverSocket = null;
    private static HashMap<String, User> validUsers = new HashMap<>();

    static {

        validUsers.put("sjx", new User("sjx", " "));
        validUsers.put("qsy", new User("qsy", "123"));
        validUsers.put("lxl", new User("lxl", " "));
    }

    private boolean checkUser(String userID, String passwd) {
        User user = validUsers.get(userID);
        if (user == null) {
            return false;
        }
        if (!user.getPassWD().equals(passwd)) {
            return false;
        }
        return true;
    }

    public PPServer() {
        try {
            serverSocket = new ServerSocket(44444);
            System.out.println("服务器在44444");

            while (true) {
                Socket socket = serverSocket.accept();
                ObjectInputStream ino = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outo = new ObjectOutputStream(socket.getOutputStream());
                User o = (User) ino.readObject();
                Message message = new Message();
                if (checkUser(o.getUserID(), o.getPassWD())) {
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    outo.writeObject(message);
                    ServerConnectClientThread serverThread = new ServerConnectClientThread(o.getUserID(), socket);
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
