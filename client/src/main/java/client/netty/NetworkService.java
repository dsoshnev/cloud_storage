package client.netty;

import client.io.CommandHandler;
import common.Command;
import common.CommandType;
import common.UserData;
import common.netty.CommandDecoder;
import common.netty.CommandEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

public class NetworkService {

    private final String host;
    private final int port;
    private final EventLoopGroup workerGroup;

    private Channel channel;

    private UserData userData;

    public void printInfo(String format, Object... args) {
        System.out.printf("Client" + channel.localAddress() + ":" + format, args);
    }
    public void printError(String format, Object... args) {
        System.out.printf("Client" + channel.localAddress() + ":" + format, args);
    }

    public NetworkService(String host, int port) {
        this.host = host;
        this.port = port;
        this.workerGroup = new NioEventLoopGroup();
    }

    public NetworkService init() {
        return this;
    }

    public void run() throws InterruptedException {

        Bootstrap b = new Bootstrap();
        b.group(this.workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new CommandDecoder(),
                                new CommandEncoder(),
                                new ClientHandler(init()));
                    }
                });

        // Start the client.
        this.channel = b.connect(host, port).sync().channel();
    }

    public synchronized UserData getUserData() {
        return userData;
    }

    public synchronized void setUserData(UserData userData) {
        this.userData = userData;
    }

    public synchronized void subscribe(CommandHandler handler) {
        //TODO
    }

    public synchronized void unsubscribe(CommandHandler handler) {
        //TODO
    }

    public void sendAuthCommand(String login, String password) throws IOException {
        sendCommand(Command.authCommand(login, password, null));
    }

    public void sendStorageCommand(CommandType type, String param1) throws IOException {
        sendCommand(Command.storageCommand(type, param1));
    }

    public void sendMessageCommand(String login, String message) throws IOException {
        sendCommand(Command.messageCommand(new UserData(login, null, null), message));
    }

    public void sendCommand(Command command) {
        channel.writeAndFlush(command);
        printInfo("send: %s%n", command);
    }

    public void readCommand(Command command) {
        printInfo("read: %s%n", command);
    }

    public void shutdown() throws InterruptedException {
        // Wait until the connection is closed.
        this.channel.closeFuture().sync();
        this.workerGroup.shutdownGracefully();
    }
}

