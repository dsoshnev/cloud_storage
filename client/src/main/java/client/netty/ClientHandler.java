package client.netty;

import common.AuthCommand;
import common.Command;
import common.StorageCommand;
import common.netty.FileDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final NetworkService ns;

    public ClientHandler(NetworkService ns) {
        this.ns = ns;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ns.printInfo("channelRead:" + msg.getClass());
        if(msg instanceof Command) {
            ns.readCommand(ctx, (Command) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ns.printError("error: %s%n", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
