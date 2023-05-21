package ppclient.service;

import ppcommen.Message;
import ppcommen.MessageType;
import ppcommen.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class UserClientService {
    private User u = new User();
    private Socket socket;

    public boolean checkUser(String user, String passWD) {
        boolean result = false;
        u.setUserID(user);
        u.setPassWD(passWD);
        try {
            socket = new Socket("43.143.186.174", 52113);
            ObjectOutputStream outuser = new ObjectOutputStream(socket.getOutputStream());
            outuser.writeObject(u);

            ObjectInputStream inmessage = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) inmessage.readObject();

            if (message.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {
                ClientConnectTheard connectThread = new ClientConnectTheard(socket);
                connectThread.start();
                MannageCConnectThread.addCConnectThread(user,connectThread);
                result = true;
            } else
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public void onlineUserList(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_USERLIST);
        message.setSender(u.getUserID());

        ClientConnectTheard connectThread = MannageCConnectThread.getCConnectThread(u.getUserID());
        try {
            OutputStream outputStream = connectThread.getSocket().getOutputStream();
            ObjectOutputStream ooutput = new ObjectOutputStream(outputStream);
            ooutput.writeObject(message);
        } catch (IOException e) {
           e.printStackTrace();
        }
    }
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserID());
        message.setGetter("server");
        try {
            ObjectOutputStream exitoutput = new ObjectOutputStream
                    (MannageCConnectThread.getCConnectThread(u.getUserID()).getSocket().getOutputStream());
            exitoutput.writeObject(message);
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
