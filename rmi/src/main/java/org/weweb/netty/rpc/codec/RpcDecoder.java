package org.weweb.netty.rpc.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.weweb.netty.rpc.common.SerializationUtil;

public class RpcDecoder extends ByteToMessageDecoder {
	private Class<?> aClass;

	public RpcDecoder(Class<?> aClass) {
		this.aClass = aClass;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
		int fixLength = 4;
		if (in.readableBytes() < fixLength) {
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			return;
		}
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		out.add(SerializationUtil.deserialize(data, aClass));
	}
}
