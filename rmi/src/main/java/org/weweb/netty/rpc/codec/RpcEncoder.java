package org.weweb.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.weweb.netty.rpc.common.SerializationUtil;

public class RpcEncoder extends MessageToByteEncoder {
	private Class<?> aClass;

	public RpcEncoder(Class<?> aClass) {
		this.aClass = aClass;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		if (aClass.isInstance(msg)) {
			byte[] data = SerializationUtil.serialize(msg);
			out.writeInt(data.length);
			out.writeBytes(data);
		}
	}
}
