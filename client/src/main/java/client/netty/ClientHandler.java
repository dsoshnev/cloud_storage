package client.netty;

import common.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final NetworkService ns;

    public ClientHandler(NetworkService ns) {
        this.ns = ns;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof Command) {
            ns.readCommand((Command) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ns.printError("error: %s%n", cause.getMessage());
        //cause.printStackTrace();
        //ctx.close();
    }
}
