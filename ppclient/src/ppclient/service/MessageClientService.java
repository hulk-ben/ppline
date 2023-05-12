package ppclient.service;

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
        String[] split = path.split("/");
        message.setContent(split[split.length-1]);
        File file = new File(path);
        byte[] bytes = new byte[(int)file.length()];


        try (FileInputStream fileInputStream = new FileInputStream(file);) {
            fileInputStream.read(bytes);
            message.setBytes(bytes);
            ObjectOutputStream outfile = new ObjectOutputStream(MannageCConnectThread.getCConnectThread(sender).getSocket().getOutputStream());
            outfile.writeObject(message);
            return true;
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;

    }
}
