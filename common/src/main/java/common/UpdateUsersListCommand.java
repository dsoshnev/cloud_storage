package common;

import java.util.List;

public class UpdateUsersListCommand extends Command {
    private final List<UserData> users;

    public UpdateUsersListCommand(List<UserData> users) {
        setType(CommandType.UPDATE_USERS_LIST);
        this.users = users;
    }

    public List<UserData> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "UpdateUsersListCommand{" +
                "users=" + getUsers() +
                '}';
    }
}
