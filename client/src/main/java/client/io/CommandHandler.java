package client.io;

import common.*;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class CommandHandler implements Runnable {

    private final NetworkService networkService;
    private final Socket socket;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public CommandHandler(NetworkService networkService, Socket socket) {
        this.networkService = networkService;
        this.socket = socket;
    }

    private void printInfo(String format, Object... args) {
        System.out.printf("Client" + socket.getLocalPort() + ":" + format, args);
    }
    private void printError(String format, Object... args) {
        System.out.printf("Client" + socket.getLocalPort() + ":" + format, args);
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            networkService.subscribe(this);
            printInfo("client connected to %s%n", socket);
            readMessages();
        } catch (IOException e) {
            printError("failed to establish connection%n");
        } finally {
            close();
        }
    }

    private void readMessages() throws IOException{
        while (true) {
            Command command = readCommand();
            if (command != null) {
                switch (command.getType()) {
                    case AUTH:
                        AuthCommand aCommand = (AuthCommand) command;
                        networkService.setUserData(aCommand.getUserData());
                        break;
                    case MESSAGE:
                        MessageCommand mCommand = (MessageCommand) command;
                        //TODO
                        break;
                    case ERROR:
                        ErrorCommand eCommand = (ErrorCommand) command;
                        //TODO
                        break;
                    case UPDATE_USERS_LIST:
                        UpdateUsersListCommand uCommand = (UpdateUsersListCommand) command;
                        //TODO
                        break;
                    case DOWNLOAD:
                        StorageCommand sCommand = (StorageCommand) command;
                        downloadFile(sCommand);
                        break;
                    case LS:
                        //TODO
                        break;
                    case END:
                        //TODO
                        return;
                }
            }
        }
    }

    public void sendCommand(Command command) throws IOException {
        switch (command.getType()) {
            case UPLOAD:
                StorageCommand sCommand = (StorageCommand) command;
                uploadFile(sCommand);
                break;
            default:
                out.writeObject(command);
                printInfo("send: %s%n", command);
                break;
        }
    }

    public void uploadFile(StorageCommand sCommand) throws IOException {
        String pathName = sCommand.getParam1();
        File file = new File(pathName);
        sCommand.setLongParam2(file.length());
        out.writeObject(sCommand);
        printInfo("send: %s%n", sCommand);
        FileUtility.writeFileToStream(pathName, out);
        printInfo("%s was uploaded to the server%n", pathName);
    }

    public void downloadFile(StorageCommand storageCommand) throws IOException {
        String pathName = storageCommand.getParam1();
        FileUtility.readFileFromStream(pathName, in);
        printInfo("%s was downloaded from the server%n", pathName);
    }

    private Command readCommand() throws IOException {
        try {
            Command command = (Command) in.readObject();
            printInfo("read: %s%n", command);
            return command;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    private void close() {
        try {
            networkService.unsubscribe(this);
            printInfo("client %s disconnected successfully at %s%n", socket, new Date());
            socket.close();
         } catch (IOException e) {
            printError("connection closing failed%n");
        }
    }
}
