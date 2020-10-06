package common;

import java.io.File;
import java.io.Serializable;

public class UserData implements Serializable {
    public String login;
    public String password;
    public String username;
    public String homeDir;

    public UserData(String login, String password, String username) {
        this.login = login;
        this.password = password;
        this.username = username;
        this.homeDir = File.separator + login + File.separator;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "login='" + login + '\'' +
                ", password='***'" +
                ", username='" + username + '\'' +
                ", homeDir='" + homeDir + '\'' +
                '}';
    }
}
