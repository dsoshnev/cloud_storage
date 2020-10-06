package client.io;

import common.*;

import java.io.IOException;

public class Client implements AutoCloseable {

    private static final int DEFAULT_PORT = 8189;
    private static  final String DEFAULT_HOST = "localhost";

    private final NetworkService ns;

    public Client(String serverHost, int serverPort) {
        this.ns = new NetworkService(serverHost, serverPort);
    }

    public void start(String login, String password) throws IOException, InterruptedException {
        ns.run();
        Thread.sleep(1000);
        ns.sendAuthCommand(login, password);
        /*
        Thread.sleep(1000);
        ns.sendMessageCommand("all", "message1");
        ns.sendMessageCommand("login1", "message2");
        ns.sendMessageCommand("unknown", "message3");*/
        ns.sendStorageCommand(CommandType.LS, ".");
        ns.sendStorageCommand(CommandType.UPLOAD, login + "forUpload.txt");
        ns.sendStorageCommand(CommandType.DOWNLOAD, login + "forDownload.txt");
        Thread.sleep(1000);
        ns.sendCommand(Command.endCommand());
    }

    @Override
    public void close() {
        ns.shutdown();
    }

    public static void main(String[] args) {
        new Thread(() -> {
            try (Client client = new Client(DEFAULT_HOST, DEFAULT_PORT)) {
                client.start("login1", "pass1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
/*
        new Thread(() -> {
            try (Client client = new Client(DEFAULT_HOST, DEFAULT_PORT)) {
                client.start("login2", "pass2");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


        new Thread(() -> {
            try (Client client = new Client(DEFAULT_HOST, DEFAULT_PORT)) {
                client.start("login3", "pass3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
*/
    }
}
