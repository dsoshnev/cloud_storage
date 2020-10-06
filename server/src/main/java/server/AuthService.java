package server;

import common.UserData;
import java.io.IOException;

public interface AuthService {
    UserData AuthorizeUser(String login, String password);
    void setUsername(String login, String nickname);
    void run() throws IOException;
    void shutdown() throws IOException;
}
