package common;

import java.io.*;
import java.util.List;

public class Command implements Serializable {

    private CommandType type;

    public CommandType getType() { return type; }

    public void setType(CommandType type) {
        this.type = type;
    }

    public static Command authCommand(String login, String password, String username) {
        return new AuthCommand(login, password, username);
    }

    public static Command errorCommand(String errorMessage) {
        return new ErrorCommand(errorMessage);
    }

    public static Command messageCommand(UserData toUser, String message) {
        return new MessageCommand(null, toUser, message);
    }

    public static Command storageCommand(CommandType type, String param1) {
        return new StorageCommand(type, param1);
    }

    public static Command updateUsersListCommand(List<UserData> users) {
        return new UpdateUsersListCommand(users);
    }

    public static Command endCommand() {
        Command command = new Command();
        command.setType(CommandType.END);
        return command;
    }

    public static byte[] toByteArray(Command command) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(command);
            return bos.toByteArray();
        }
    }
    public static Command fromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (Command) in.readObject();
        }
    }

    @Override
    public String toString() {
        return "Command{" +
                "type='" + getType() + '\'' +
                '}';
    }
}
