package common.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final String name;
    private final long length;
    private FileChannel fileChannel;
    private long receivedLength = 0;

    public FileDecoder(long length, String name) throws IOException {
        this.length = length;
        this.name = name;

        Path directoryPath = Paths.get(".");
        Path filePath = directoryPath.resolve(name);
        fileChannel = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        System.out.printf("FileDecoder:bytes to read %s%n", msg.toString());
        int bytes = msg.readableBytes();
        int remain = (int) (length - receivedLength);

        if(remain > bytes) {
            msg.readBytes(bytes);
            receivedLength += bytes;
            fileChannel.write(msg.nioBuffer(0,bytes));
        } else {
            msg.readBytes(remain);
            receivedLength += remain;
            fileChannel.write(msg.nioBuffer(0,remain));
            fileChannel.close();
            ctx.pipeline().remove("filedecoder");
            out.add(msg.readBytes(bytes - remain));
        }

    }
}
