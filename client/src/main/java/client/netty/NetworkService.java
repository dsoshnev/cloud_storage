package client.netty;

import common.*;
import common.netty.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.apache.logging.log4j.*;

import java.io.IOException;

public class NetworkService {

    private static final Logger logger = LogManager.getLogger();

    private final String host;
    private final int port;
    private final EventLoopGroup workerGroup;

    private Channel channel;

    private UserData userData;

    public void printInfo(String format, Object... args) {
        logger.info(String.format("Client" + channel.localAddress() + ":" + format, args));
    }
    public void printError(String format, Object... args) {
        logger.error(String.format("Client" + channel.localAddress() + ":" + format, args));
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
        b.group(this.workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new ProtocolDecoder(),
                        new ProtocolEncoder(),
                        //new ObjectEncoder(),
                        //new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                        //new ChunkedFileClientHandler(),
                        //new CommandDecoder(),
                        //new CommandEncoder(),
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

    public void readCommand(ChannelHandlerContext ctx, Command command) {
        try {
            printInfo("read: %s%n", command);
            if (command != null) {
                switch (command.getType()) {
                    case END:
                        //System.out.println("do end!");
                        break;
                    case AUTH:
                        //System.out.println("do auth!");
                        break;
                    case UPLOAD:
                        StorageCommand sCommand = (StorageCommand) command;
                        FileDecoder fileDecoder = new FileDecoder(sCommand.getLongResult1(), sCommand.getParam1());
                        ctx.pipeline().addFirst("filedecoder", fileDecoder);
                        //System.out.println("do upload!");
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            printError("IOException %s%n", e.getMessage());
        }
    }

    public void shutdown() throws InterruptedException {
        // Wait until the connection is closed.
        this.channel.closeFuture().sync();
        this.workerGroup.shutdownGracefully();
    }
}

