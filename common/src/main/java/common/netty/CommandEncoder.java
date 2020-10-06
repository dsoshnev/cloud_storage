package common.netty;

import common.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommandEncoder extends MessageToByteEncoder<Command> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws Exception {
        byte[] bytes = Command.toByteArray(msg);
        out.writeBytes(bytes);
        //System.out.printf("bytes to write %s:%s%n", bytes.length, Arrays.toString(bytes));

        /*
        ByteBufOutputStream bbos = new ByteBufOutputStream(out);
        ObjectOutputStream oos = new ObjectOutputStream(bbos);
        oos.writeObject(msg);
        oos.flush();
        System.out.println("bytes to write");
        */

    }
}
