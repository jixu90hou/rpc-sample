package org.weweb.rest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

public class HttpServer {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                ByteBuf delimiter=Unpooled.copiedBuffer("$_".getBytes());
               pipeline.addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
              //  pipeline.addLast(new StringDecoder());
                pipeline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
                        byte[] bytes=new byte[byteBuf.readableBytes()];
                        byteBuf.readBytes(bytes);
                        String msg=new String(bytes,"utf-8");
                        System.out.println("msg:" + new String(bytes));
                        ctx.writeAndFlush(Unpooled.copiedBuffer("$_神大名".getBytes()));
                        //.addListener(ChannelFutureListener.CLOSE);
                    }
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("---active---");
                        super.channelActive(ctx);
                    }

                });
            }
        });

        ChannelFuture channelFuture = bootstrap.bind(9090).sync();
        Channel channel = channelFuture.channel();
        System.out.println("-------start up-------");
        //channel.close().sync();
    }
}
