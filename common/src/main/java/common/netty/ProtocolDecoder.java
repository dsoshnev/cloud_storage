package common.netty;

import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.handler.codec.*;

import org.apache.logging.log4j.*;

import java.io.ObjectInputStream;

public class ProtocolDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger logger = LogManager.getLogger();

    public ProtocolDecoder() {
        //this(1048576);
        this(Integer.MAX_VALUE);
    }

    public ProtocolDecoder(int maxObjectSize) {
        super(maxObjectSize, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        logger.info("bytes to read " + in.toString());
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
