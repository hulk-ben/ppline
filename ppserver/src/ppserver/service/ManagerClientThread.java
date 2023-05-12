package ppserver.service;

import java.util.HashMap;
import java.util.Set;

public class ManagerClientThread {
    private static HashMap<String,ServerConnectClientThread> threads= new HashMap<>();
    public static void addThreads(String user,ServerConnectClientThread thread){
        threads.put(user,thread);
    }
    public static ServerConnectClientThread getThread(String user){
        return threads.get(user);
    }

    public static HashMap<String, ServerConnectClientThread> getThreads() {
        return threads;
    }

    public static boolean removeServerConnectClientThread(String user){
        if (threads.remove(user) == null) {
            return false;
        }
        return true;

    }
    public static String getOlineUser(){
        Set<String> users = threads.keySet();
        String ol = "";
        for (String user: users) {
            ol += user+" ";
        }
        return ol;
    }
}
