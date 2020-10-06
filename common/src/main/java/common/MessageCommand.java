package common;

public class MessageCommand extends Command {
    private UserData fromUser;
    private final UserData toUser;
    private final String message;

    public MessageCommand(UserData fromUser, UserData toUser, String message) {
        setType(CommandType.MESSAGE);
        this.message = message;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public UserData getFromUser() {
        return fromUser;
    }

    public UserData getToUser() {
        return toUser;
    }

    public String getMessage() { return message; }

    public void setFromUser(UserData fromUser) {
        this.fromUser = fromUser;
    }

    @Override
    public String toString() {
        return "MessageCommand{" +
                "fromUser='" + getFromUser() + '\'' +
                ", toUser='" + getToUser() + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }

}

