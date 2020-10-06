package server;

import common.UserData;
import java.io.IOException;
import java.util.*;

public class BaseAuthService implements AuthService {

    //private Connection conn;

    private static final List<UserData> users = new ArrayList<>();

    @Override
    public UserData AuthorizeUser(String login, String password) {
        for (UserData user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void setUsername(String login, String nickname) {
        // update DB
    }

    @Override
    public void run() throws IOException {
        /*
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:MyDatabase.db");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users");
        while (rs.next()) {
            String login = rs.getString(1);
            String password = rs.getString(2);
            String username = rs.getString(3);
            users.add(new UserData(login, password, username));
        }
        */
        users.add(new UserData("login1", "pass1", "username1"));
        users.add(new UserData("login2", "pass2", "username2"));
        users.add(new UserData("login3", "pass3", "username3"));
        LogService.info("auth service is started");
    }

    @Override
    public void shutdown() throws IOException {
        /*if (conn != null) {
            conn.close();
        }*/
        LogService.info("auth service is stopped");
    }
}

