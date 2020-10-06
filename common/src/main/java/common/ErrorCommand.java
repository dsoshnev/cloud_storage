package common;

public class ErrorCommand extends Command {
    private final String error;

    public ErrorCommand(String error) {
        setType(CommandType.ERROR);
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "ErrorCommand{" +
                "error='" + getError() + '\'' +
                '}';
    }
}

