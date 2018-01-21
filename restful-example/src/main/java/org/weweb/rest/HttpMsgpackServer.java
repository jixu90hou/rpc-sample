package org.weweb.rest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.weweb.rest.codec.MsgpackDecoder;
import org.weweb.rest.codec.MsgpackEncoder;
import org.weweb.rest.entity.UserInfo;

/**
 * @author jackshen
 */
public class HttpMsgpackServer {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
               // ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                pipeline.addLast(new MsgpackDecoder());
                pipeline.addLast(new LengthFieldPrepender(2));
                pipeline.addLast(new MsgpackEncoder());
               // pipeline.addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                //  pipeline.addLast(new StringDecoder());
                pipeline.addLast(new SimpleChannelInboundHandler<UserInfo>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, UserInfo userInfo) throws Exception {
                        //.addListener(ChannelFutureListener.CLOSE);
                        System.out.println("userInfo:"+userInfo);
                        ctx.writeAndFlush(userInfo);
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("---active---");
                        super.channelActive(ctx);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        cause.printStackTrace();
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
