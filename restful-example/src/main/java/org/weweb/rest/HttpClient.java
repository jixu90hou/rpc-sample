package org.weweb.rest;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

public class HttpClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                ByteBuf delimiter=Unpooled.copiedBuffer("$_".getBytes());
                pipeline.addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
              //  pipeline.addLast(new StringDecoder());
                pipeline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        Channel channel=ctx.channel();
                        ByteBuf message;
                        for (int i=0;i<100000;i++){
                            String reqMessage="$_zhang"+i;
                            message=Unpooled.buffer(reqMessage.length());
                            message.writeBytes(reqMessage.getBytes());
                            channel.writeAndFlush(message);
                        }
                        System.out.println("=============");
                    }


                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
                        // String message = "aaa";
                        byte[] bytes = new byte[byteBuf.readableBytes()];
                        byteBuf.readBytes(bytes);
                        String msg = new String(bytes, "utf-8");
                        System.out.println("client receive msg:" + msg);
                        //ctx.writeAndFlush(message);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        cause.printStackTrace();
                    }
                });
            }
        });
        String host = "127.0.0.1";
        int port = 9090;
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        Channel channel = channelFuture.channel();

        //byteBuf.writeBytes(message.getBytes());
        // channel.close().sync();
    }
}
