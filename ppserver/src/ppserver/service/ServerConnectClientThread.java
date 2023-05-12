package ppserver.service;

import ppcommen.Message;
import ppcommen.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class ServerConnectClientThread extends Thread {
    private String userID;//连接的用户
    private Socket socket;

    public ServerConnectClientThread(String userID, Socket socket) {
        this.userID = userID;
        this.socket = socket;
    }

    @Override
    public void run() {
        label:
        while (true) {
            System.out.println("服务端保持连接" + userID + "已连接");
            try {
                ObjectInputStream outo = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) outo.readObject();
                switch (message.getMesType()) {
                    case MessageType.MESSAGE_COMM_MES:
                        if (message.getGetter() == null) {
                            if (sendMessage(message)) {
                                System.out.println(message.getSender()+"给所有人发送了一条消息");
                            }

                        }
                        sendPrivatemessage(message);
                        break;
                    case MessageType.MESSAGE_FILE_MES:
                        sendFile(message);
                        System.out.println(message.getSender()+"给"+message.getGetter()+"发了个文件");
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

            }

        }
    }

    private static void sendPrivatemessage(Message message) throws IOException {
        ServerConnectClientThread thread = ManagerClientThread.getThread(message.getGetter());
        ObjectOutputStream outmessage = new ObjectOutputStream(thread.getSocket().getOutputStream());

        outmessage.writeObject(message);
        System.out.println(message.getSender()+"对"+ message.getGetter()+"发了一条消息");
    }

    private void getonline() throws IOException {
        System.out.println(userID + "查看用户列表");
        String olineUser = ManagerClientThread.getOlineUser();
        Message message1 = new Message("test", userID, olineUser, "11:00", MessageType.MESSAGE_RETURN_ONLINE_USERLIST);
        ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
        oout.writeObject(message1);
    }


    private static boolean sendMessage(Message message){
        HashMap<String, ServerConnectClientThread> threads = ManagerClientThread.getThreads();
        try {
            threads.forEach((key,vulue)->{
                try {
                    ObjectOutputStream outmess = new ObjectOutputStream(vulue.getSocket().getOutputStream());
                    outmess.writeObject(message);

                } catch (IOException e) {
                   e.printStackTrace();
                }
            });
            return true;
        } catch (Exception e) {
           e.printStackTrace();
           return false;
        }
    }
    public static boolean sendFile(Message message){
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
}
