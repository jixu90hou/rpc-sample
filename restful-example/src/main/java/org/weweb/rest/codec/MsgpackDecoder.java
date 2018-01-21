package org.weweb.rest.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;
import org.weweb.rest.entity.UserInfo;

import java.util.List;

/**
 * @author jackshen
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int length=msg.readableBytes();
        final byte[] array=new byte[length];
        MessagePack messagePack=new MessagePack();
        msg.getBytes(msg.readerIndex(),array,0,length);
        out.add(messagePack.read(array, UserInfo.class));
    }
}
