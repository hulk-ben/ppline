package ppclient.view;

import ppclient.service.ClientConnectTheard;
import ppclient.service.MannageCConnectThread;
import ppclient.service.MessageClientService;
import ppclient.service.UserClientService;
import ppclient.util.Utility;

import java.io.IOException;

public class PPview {
    private boolean loop = true;
    private String key = "";

    public static void main(String[] args) {
        PPview pPview = new PPview();
        pPview.mainMenu();
        System.out.println("客户端退出");
    }
    private void mainMenu(){
        while (loop) {
            System.out.println("\t\t欢迎登陆系统\t\t");
            System.out.println("\t\t1、登陆系统\t\t");
            System.out.println("\t\t9、退出系统\t\t");
            System.out.print("请输入选项序号：");
            key = Utility.readString(1);
            switch (key){
                case "1":
                    System.out.println("请输入账户：");
                    String userID = Utility.readString(50);
                    System.out.println("请输入密码：");
                    String passWD = Utility.readString(50);
                    UserClientService userClientService = new UserClientService();
                    MessageClientService mesSev = new MessageClientService();

                    if (userClientService.checkUser(userID,passWD)) {
                        System.out.println("\t\t欢迎"+userID+"登陆成功\t\t");
                        int count = 1;
                        while (loop){
                            getMenu();
                            if (count == 1) {
                                System.out.println("请输入选项序号：");
                                count ++;
                            }
                            key = Utility.readString(1);
                            switch (key){
                                case "1":
                                    System.out.println("在线用户");
                                    userClientService.onlineUserList();


                                    break;
                                case "2":
                                    System.out.println("群发消息");
                                    System.out.println("消息:");
                                    String text = Utility.readString(100);
                                    mesSev.sendMessage(text,userID);
                                    break;
                                case "3":
                                    System.out.println("私聊消息");
                                    System.out.println("请输入要私聊的朋友：");
                                    String s = Utility.readString(10);
                                    System.out.println("消息:");
                                    String test = Utility.readString(100);
                                    if (mesSev.sendPrivateMessage(test,userID,s)) {
                                        System.out.println(userID+"对"+s+"说"+test);
                                    } else {
                                        System.out.println("faile");
                                    }
                                    break;
                                case "4":
                                    System.out.println("文件发送");
                                    System.out.println("请输入要send的朋友：");
                                    String s4 = Utility.readString(50);
                                    System.out.println("请输入要send的文件的绝对路径：");
                                    String path = Utility.readString(50);
                                    if (mesSev.sendFile(userID,s4,path)) {
                                        System.out.println("发送中");
                                    }
                                    break;
                                case "9":
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }

                        }
                    }else {
                        System.out.println("登陆失败");
                    }
                    break;
                case "9":

                    loop = false;
                    break;

            }
        }

    }

    public static void getMenu() {
        System.out.println("\t\t系统二级菜单\t\t");
        System.out.println("\t\t1、显示在线用户");
        System.out.println("\t\t2、群发消息");
        System.out.println("\t\t3、私聊消息");
        System.out.println("\t\t4、发送文件");
        System.out.println("\t\t9、退出系统");


    }
}
