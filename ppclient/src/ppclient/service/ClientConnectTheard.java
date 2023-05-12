package ppclient.service;

import ppclient.view.PPview;
import ppcommen.Message;
import ppcommen.MessageType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectTheard extends Thread {
    private Socket socket;

    public ClientConnectTheard(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ObjectInputStream ino = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ino.readObject();
                switch (message.getMesType()){
                    case MessageType.MESSAGE_COMM_MES:
                        getMessage(message);
                        break;
                    case MessageType.MESSAGE_FILE_MES:
                        getFile(message);
                        System.out.println("接收了一个文件");

                        break;
                    case MessageType.MESSAGE_RETURN_ONLINE_USERLIST:
                        getonline(message);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static void getonline(Message message) {
        String[] s = message.getContent().split(" ");
        System.out.println("\t\t--当前在线用户列表--\t\t");
        for (String s1 : s) {


            System.out.println("用户名" + s1);
        }
        System.out.println("请输入选项");
    }
    private static void getMessage(Message message){
        String content = message.getContent();
        String sender = message.getSender();

        System.out.println(sender+"对你说："+content);
        System.out.println("请输入选项");

    }
    private static void getFile(Message message){
        String name = message.getContent();
        try (FileOutputStream outputStream = new FileOutputStream(name)) {
            byte[] bytes = message.getBytes();
            outputStream.write(bytes);
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;

    }
}
