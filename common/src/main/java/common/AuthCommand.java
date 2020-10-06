package common;

public class AuthCommand extends Command {

    private final UserData userData;

    public AuthCommand(String login, String password, String username) {
        setType(CommandType.AUTH);
        this.userData = new UserData(login, password, username);
    }

    public String getLogin() {
        return userData.login;
    }

    public String getPassword() {
        return userData.password;
    }

    public String getUsername() {
        return userData.username;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUsername(String username) {
        this.userData.username = username;
    }

    @Override
    public String toString() {
        return "AuthCommand{" +
                "userData='" + getUserData() + '\'' +
                '}';
    }
}
