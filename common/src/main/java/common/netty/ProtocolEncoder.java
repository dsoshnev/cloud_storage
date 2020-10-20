package common.netty;

import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.handler.codec.*;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ProtocolEncoder extends MessageToByteEncoder<Serializable> {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        int startIdx = out.writerIndex();

        ByteBufOutputStream bout = new ByteBufOutputStream(out);
        ObjectOutputStream oout = null;
        try {
            bout.write(LENGTH_PLACEHOLDER);
            oout = new ObjectOutputStream(bout);
            oout.writeObject(msg);
            oout.flush();
        } finally {
            if (oout != null) {
                oout.close();
            } else {
                bout.close();
            }
        }

        int endIdx = out.writerIndex();

        out.setInt(startIdx, endIdx - startIdx - 4);
        System.out.printf("ProtocolEncoder:bytes to write %s:%s%n", msg.getClass().getName(), endIdx - startIdx - 4);
    }
}
