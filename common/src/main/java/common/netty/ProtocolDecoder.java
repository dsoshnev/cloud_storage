package common.netty;

import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.handler.codec.*;

import java.io.ObjectInputStream;

public class ProtocolDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolDecoder() {
        //this(1048576);
        this(Integer.MAX_VALUE);
    }

    public ProtocolDecoder(int maxObjectSize) {
        super(maxObjectSize, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        System.out.printf("bytes to read %s%n", in.toString());
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        ObjectInputStream ois = new ObjectInputStream(new ByteBufInputStream(frame, true));
        try {
            return ois.readObject();
        } finally {
            ois.close();
        }
    }
}
