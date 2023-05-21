package ppclient.service;

import org.junit.Test;
import ppcommen.Message;
import ppcommen.MessageType;

import java.io.*;

public class MessageClientService {
    public boolean sendPrivateMessage(String text,String senduser,String getuser){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSender(senduser);
        message.setGetter(getuser);
        message.setContent(text);
        try  {
            ObjectOutputStream outputStream = new ObjectOutputStream(MannageCConnectThread.getCConnectThread(senduser).getSocket().getOutputStream());
            outputStream.writeObject(message);
            return true;
        } catch (IOException e) {

            return false;
        }
    }
    public boolean sendMessage(String text,String senduser){
        Message message = new Message();
        message.setContent(text);
        message.setSender(senduser);

        message.setMesType(MessageType.MESSAGE_COMM_MES);
        try {
            ObjectOutputStream groupmessage = new ObjectOutputStream(MannageCConnectThread.getCConnectThread(senduser).getSocket().getOutputStream());
            groupmessage.writeObject(message);

            return true;
        } catch (IOException e) {
           e.printStackTrace();
           return false;
        }
    }
    public boolean sendFile(String sender,String getter,String path){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(sender);
        message.setGetter(getter);
        File file = new File(path);
        message.setContent(file.getName());
        byte[] bytes = new byte[(int)file.length()];


        try (FileInputStream fileInputStream = new FileInputStream(file);) {
            fileInputStream.read(bytes);
            message.setBytes(bytes);
            ObjectOutputStream outfile = new ObjectOutputStream(MannageCConnectThread.getCConnectThread(sender).getSocket().getOutputStream());
            outfile.writeObject(message);
            return true;
        } catch (FileNotFoundException e) {

            System.out.println("没有找到文件");
        } catch (IOException e) {
            System.out.println("没有找到文件");
        }
        return false;

    }

    public void catOffMess(String userID) {
        Message message = new Message();
        message.setSender(userID);
        message.setGetter("服务器");
        message.setMesType(MessageType.MESSAGE_CAT);
        try {
            ObjectOutputStream groupmessage = new ObjectOutputStream(MannageCConnectThread.getCConnectThread(userID).getSocket().getOutputStream());
            groupmessage.writeObject(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
