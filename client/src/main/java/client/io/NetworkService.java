package client.io;

import common.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkService {

    private final String host;
    private final int port;

    private final ExecutorService executorService;
    private final List<CommandHandler> handlers = new ArrayList<>();
    private UserData userData;

    public NetworkService(String host, int port) {
        this.host = host;
        this.port = port;
        this.executorService = Executors.newCachedThreadPool();
    }

    public void run() throws IOException {
        createCommandHandler(new Socket(host, port));
    }
    private void createCommandHandler(Socket socket) {
        // only one CommandHandler per client (reserved for future options)
        executorService.execute(new CommandHandler(this, socket));
    }

    public synchronized UserData getUserData() {
        return userData;
    }

    public synchronized void setUserData(UserData userData) {
        this.userData = userData;
    }

    public synchronized void subscribe(CommandHandler handler) {
        handlers.add(handler);
    }

    public synchronized void unsubscribe(CommandHandler handler) {
        handlers.remove(handler);
    }

    public void sendAuthCommand(String login, String password) throws IOException {
        handlers.get(0).sendCommand(Command.authCommand(login, password, null));
    }

    public void sendStorageCommand(CommandType type, String param1) throws IOException {
        handlers.get(0).sendCommand(Command.storageCommand(type, param1));
    }

    public void sendMessageCommand(String login, String message) throws IOException {
        handlers.get(0).sendCommand(Command.messageCommand(new UserData(login, null, null), message));
    }

    public void sendCommand(Command command) throws IOException {
        handlers.get(0).sendCommand(command);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}

