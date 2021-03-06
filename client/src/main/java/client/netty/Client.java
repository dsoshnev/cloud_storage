package client.netty;

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
        ns.sendAuthCommand(login, password);
        ns.sendStorageCommand(CommandType.LS, ".");
        ns.sendStorageCommand(CommandType.UPLOAD, login + "forUpload.txt");
        ns.sendStorageCommand(CommandType.DOWNLOAD, login + "forDownload.txt");
        ns.sendCommand(Command.endCommand());
        Thread.sleep(10000);
    }

    @Override
    public void close() {
        try {
            ns.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
