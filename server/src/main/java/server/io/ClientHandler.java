package server.io;

import common.*;
import server.LogService;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;


public class ClientHandler implements Runnable {

    private static final int AUTHENTICATION_TIMEOUT = 120000;
    private final Server server;
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private UserData userData;
    private final Date createdAt;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.clientSocket = socket;
        this.createdAt = new Date();
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            LogService.info("client %s connected at %s", clientSocket, createdAt);
            if (authentication()) {
                init();
                readMessages();
            }
        } catch (IOException e) {
            LogService.error("failed to establish client connection");
        } finally {
            close();
        }
    }

    private void init() throws IOException {
        FileUtility.createDirectory(Server.DEFAULT_DATA + userData.homeDir);
    }
    private boolean authentication() throws IOException {
        clientSocket.setSoTimeout(AUTHENTICATION_TIMEOUT);
        while (true) {
            try {
                Command command = readCommand();
                if(command != null) {
                    switch (command.getType()) {
                        case END:
                            return false;
                        case AUTH:
                            AuthCommand authCommand = (AuthCommand) command;
                            UserData userData = server.getAuthService().AuthorizeUser(authCommand.getLogin(), authCommand.getPassword());
                            if (userData != null) {
                                this.userData = userData;
                                userData.username = authCommand.getUsername();
                                server.getAuthService().setUsername(userData.login, userData.username);
                                sendCommand(command);
                                server.subscribe(this);
                                return true;
                            } else {
                                sendCommand(Command.errorCommand("user not found"));
                            }
                            break;
                    }
                }
            } catch (SocketTimeoutException e) {
                if (checkAuthTimeout()) {
                    return false;
                }
            }
        }
    }

    private void readMessages() throws IOException {
        clientSocket.setSoTimeout(0);
        while (true) {
            try {
                Command command = readCommand();
                if (command != null) {
                    switch (command.getType()) {
                        case END:
                            return;
                        case CD:
                        case RM:
                        case MKDIR:
                            break;
                        case LS:
                        case UPLOAD:
                        case DOWNLOAD:
                            sendCommand(command);
                            break;
                        case MESSAGE:
                            server.sendMessage(command, this);
                            break;
                    }
                }
            } catch(SocketTimeoutException e) {
                //if timeout not equals 0
            }
        }
    }



    private boolean checkAuthTimeout() {
        long timeout = new Date().getTime() - createdAt.getTime();
        return timeout >= AUTHENTICATION_TIMEOUT;
    }


    private Command readCommand() throws IOException {
        try {
            Command command = (Command) in.readObject();
            LogService.info("read: %s: %s", userData, command);
            return command;
        } catch (ClassNotFoundException e) {
            sendCommand(Command.errorCommand("unknown command from client"));
            return null;
        }
    }

    public synchronized void sendCommand(Command command) throws IOException {
        switch (command.getType()) {
            case DOWNLOAD:
                downloadFile((StorageCommand)command);
                break;
            case UPLOAD:
                uploadFile((StorageCommand)command);
                break;
            case LS:
                listFiles((StorageCommand)command);
                break;
            default:
                out.writeObject(command);
                break;
        }
        LogService.info("send: %s: %s", userData, command);
    }

    private void listFiles(StorageCommand sCommand) throws IOException {
        String pathName = Server.DEFAULT_DATA + userData.homeDir + sCommand.getParam1();
        sCommand.setResults(FileUtility.listFiles(pathName));
        out.writeObject(sCommand);
    }

    private void uploadFile(StorageCommand sCommand) throws IOException {
        String pathName = Server.DEFAULT_DATA + userData.homeDir + sCommand.getParam1();
        FileUtility.readFileFromStream(pathName, in);
        LogService.info("file %s was uploaded to the server", pathName);
        sCommand.setResult1(pathName);
        out.writeObject(sCommand);
    }

    private void downloadFile(StorageCommand sCommand) throws IOException {
        String pathName = Server.DEFAULT_DATA + userData.homeDir + sCommand.getParam1();
        File file = new File(pathName);
        sCommand.setLongResult1(file.length());
        out.writeObject(sCommand);
        FileUtility.writeFileToStream(pathName, out);
        LogService.info("file %s was downloaded from the server", pathName);
    }
    public UserData getUserData() {
        return userData;
    }

    private void close() {
        try {
            server.unsubscribe(this);
            clientSocket.close();
            LogService.info("client %s disconnected successfully at %s%n", clientSocket, new Date());
        } catch (IOException e) {
            LogService.error("connection closing failed");
        }
    }
}

