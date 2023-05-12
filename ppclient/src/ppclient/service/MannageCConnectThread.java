package ppclient.service;

import java.util.HashMap;

public class MannageCConnectThread {
    private static HashMap<String, ClientConnectTheard> threads = new HashMap<>();
    public static void addCConnectThread(String userID, ClientConnectTheard connectTheard){
        threads.put(userID,connectTheard);
    }
    public static ClientConnectTheard getCConnectThread(String userId){
        return threads.get(userId);
    }
}
