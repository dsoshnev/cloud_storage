package server.netty;

import common.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import server.LogService;

import java.io.IOException;
import java.util.Date;

@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final Server server;

    public ServerHandler(Server server) {
        this.server = server;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Command) {
            Command command = (Command) msg;
            readMessages(ctx, command);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogService.info("client %s connected at %s", ctx.channel().remoteAddress(), new Date());
        // subscribe after authorization
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogService.info("client %s disconnected at %s", ctx.channel().remoteAddress(), new Date());
        server.unsubscribe(ctx);
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        server.unsubscribe(ctx);
        ctx.close();

    }

    private void readMessages(ChannelHandlerContext ctx, Command command) throws IOException, InterruptedException {
        server.readCommand(ctx, command);
        if (command != null) {
            switch (command.getType()) {
                case END:
                    ctx.channel().close().sync();
                    break;
                case AUTH:
                    authorizeUser(ctx, (AuthCommand) command);
                    break;
                case LS:
                    listFiles(ctx, (StorageCommand) command);
                    break;
                default:
                    server.sendCommand(ctx, Command.errorCommand("command not supported"));
                    break;
            }
        }
    }

    private void listFiles(ChannelHandlerContext ctx, StorageCommand sCommand) throws IOException {
        String pathName = Server.DEFAULT_DATA + server.getUserData(ctx).homeDir + sCommand.getParam1();
        sCommand.setResults(FileUtility.listFiles(pathName));
        server.sendCommand(ctx, sCommand);
    }

    private void authorizeUser(ChannelHandlerContext ctx, AuthCommand authCommand) throws IOException {
        UserData userData = server.getAuthService().AuthorizeUser(authCommand.getLogin(), authCommand.getPassword());
        if (userData != null) {
            FileUtility.createDirectory(Server.DEFAULT_DATA + userData.homeDir);
            userData.username = authCommand.getUsername();
            server.getAuthService().setUsername(userData.login, userData.username);
            server.sendCommand(ctx, authCommand);
            server.subscribe(ctx, userData);
        } else {
            server.sendCommand(ctx, Command.errorCommand("user not found"));
        }

    }
}
