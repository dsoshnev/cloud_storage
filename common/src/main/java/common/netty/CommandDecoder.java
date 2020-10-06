package common.netty;

import common.Command;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import sun.jvm.hotspot.oops.Array;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class CommandDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        ByteBufInputStream bbis = new ByteBufInputStream(in.copy());
        ObjectInputStream ois = new ObjectInputStream(bbis);
        try {
            Object object = ois.readObject();
            byte[] bytes = Command.toByteArray((Command) object);
            out.add(object);
            in.skipBytes(bytes.length);
            //System.out.printf("bytes to read %s:%s%n", bytes.length, Arrays.toString(bytes));
        } catch (Exception e) {
            System.out.println("wait next object!!!");
        }
    }
}
