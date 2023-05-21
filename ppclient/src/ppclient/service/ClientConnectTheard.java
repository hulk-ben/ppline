package ppclient.service;

import ppclient.view.PPview;
import ppcommen.Message;
import ppcommen.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

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
        String[] ts = message.getSendTime().split(" ");
        System.out.println("\t\t--当前在线用户列表--\t\t");
        for (String s1 : s) {


            System.out.println("用户名:" + s1);
        }
        System.out.println("现在时间："+ts[1]);
        System.out.print("请输入选项:");
    }
    private static void getMessage(Message message){
        String content = message.getContent();
        String sender = message.getSender();

        System.out.println(sender+"对你说：\n"+"\t"+content);
        System.out.print("请输入选项:");

    }
    private static void getFile(Message message){
        String name = message.getContent();
        File file = null;
        try {
             File dir = new File(".\\ppgetfiles");
             dir.mkdirs();
            file = new File(dir, name);
            System.out.println("正在接收文件");
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] bytes = message.getBytes();
                outputStream.write(bytes);
                System.out.println("接收完毕保存在"+file.getAbsolutePath());
            }catch (FileNotFoundException e) {
                System.out.println("文件未找到");
            } catch (IOException e) {
                System.out.println("未知错误，尝试重新发送");
            }
        } catch (Exception e) {
            System.out.println("目录无法创建");
        }

    }

    public Socket getSocket() {
        return socket;

    }
}
