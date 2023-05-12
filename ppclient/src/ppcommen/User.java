package ppcommen;

import java.io.Serializable;

/**
 * 用户信息
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userID;
    private String passWD;

    public User() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassWD() {
        return passWD;
    }

    public void setPassWD(String passWD) {
        this.passWD = passWD;
    }

    public User(String userID, String passWD) {
        this.userID = userID;
        this.passWD = passWD;
    }
}
