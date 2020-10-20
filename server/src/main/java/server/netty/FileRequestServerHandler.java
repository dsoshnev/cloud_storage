package server.netty;

import java.io.File;

public class FileRequestServerHandler {
}
/*extends
    SimpleChannelInboundHandler<FileRequestProtocol>
} {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FileRequestProtocol fileRequest) {
        logger.info("Server new FileRequest " + fileRequest);
        f = new File(fileRequest.getFilePath());
        fileRequest.setFileSize(f.length());
        ctx.writeAndFlush(fileRequest);

        new ChunkedFileServerHandler(ctx,f);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.info("Server read complete");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
*/