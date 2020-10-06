package server.io;

import common.*;
import server.AuthService;
import server.BaseAuthService;
import server.LogService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements AutoCloseable {

    private static final int DEFAULT_PORT = 8189;
    private static final String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_DATA = "server/data/";

    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final AuthService authService;
    private final ExecutorService executorService;


    public Server(int port) {
        this.port = port;
        this.authService = new BaseAuthService();
        this.executorService = Executors.newCachedThreadPool();
    }

    public void init() throws IOException {
        FileUtility.createDirectory(Server.DEFAULT_DATA);
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByName(DEFAULT_HOST))) {
            LogService.info("server is started at %s", serverSocket);
            init();
            authService.run();
            while (true) {
                LogService.info("waiting client connection...");
                Socket clientSocket = serverSocket.accept();
                createClientHandler(clientSocket);
            }
        }
    }

    private void createClientHandler(Socket clientSocket) {
        executorService.execute(new ClientHandler(this, clientSocket));
    }

    public AuthService getAuthService() {
        return authService;
    }


    public void sendMessage(Command command, ClientHandler clientHandler) throws IOException {

        MessageCommand mCommand = (MessageCommand) command;
        UserData toUser = mCommand.getToUser();
        UserData fromUser = clientHandler.getUserData() ;
        mCommand.setFromUser(fromUser);
        if(toUser.login.equals("all")) {
            LogService.info("send broadcast Message: %s",  command);
            broadcastMessage(command, clientHandler);
        } else {
            LogService.info("send personal Message: %s", command);
            personalMessage(command, clientHandler);
        }
    }

    private synchronized void broadcastMessage(Command command, ClientHandler clientHandler) throws IOException {
        for (ClientHandler client : clients) {
            if (client != clientHandler) {
                client.sendCommand(command);
            }
        }
    }

    private synchronized void personalMessage(Command command, ClientHandler clientHandler) throws IOException {
        MessageCommand messageCommand = (MessageCommand) command;
        UserData toUser = messageCommand.getToUser();
        for (ClientHandler client : clients) {
            if (client.getUserData().login.equals(toUser.login)) {
                client.sendCommand(command);
            }
        }
    }

    private synchronized void updateUsersListMessage(ClientHandler clientHandler) throws IOException {
        List<UserData> users = new ArrayList<>();
        users.add(new UserData("all",null, null));
        for (ClientHandler client : clients) {
            users.add(client.getUserData());
        }
        for (ClientHandler client : clients) {
            client.sendCommand(Command.updateUsersListCommand(users));
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        updateUsersListMessage(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        clientHandler.sendCommand(Command.endCommand());
        updateUsersListMessage(clientHandler);
    }


    public static void main(String[] args) throws Exception {
        int port = getServerPort(args);
        try (Server server = new Server(port)) {
            server.start();
        }
    }

    private static int getServerPort(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                LogService.error("invalid port number: %s", args[0]);
            }
        }
        return port;
    }

    @Override
    public void close() throws Exception {
        executorService.shutdown();
        authService.shutdown();
        LogService.info("server is closed");
    }

}
