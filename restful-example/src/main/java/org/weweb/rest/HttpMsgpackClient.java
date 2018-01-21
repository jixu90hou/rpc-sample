package org.weweb.rest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.weweb.rest.codec.MsgpackDecoder;
import org.weweb.rest.codec.MsgpackEncoder;
import org.weweb.rest.entity.UserInfo;

public class HttpMsgpackClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
               // ByteBuf delimiter= Unpooled.copiedBuffer("$_".getBytes());
                pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                pipeline.addLast(new MsgpackDecoder());
                pipeline.addLast(new LengthFieldPrepender(2));
                pipeline.addLast(new MsgpackEncoder());
                //pipeline.addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
              //  pipeline.addLast(new StringDecoder());
                pipeline.addLast(new SimpleChannelInboundHandler<Object>() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        Channel channel=ctx.channel();
                        for (int i=1;i<100;i++){
                            UserInfo userInfo=new UserInfo();
                            userInfo.setId(Long.valueOf(i));
                            userInfo.setName("wang"+i);
                            channel.writeAndFlush(userInfo);
                          }
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, Object userInfo) throws Exception {
                        // String message = "aaa";
                        System.out.println("receive userInfo:"+userInfo);
                       /* byte[] bytes = new byte[byteBuf.readableBytes()];
                        byteBuf.readBytes(bytes);
                        String msg = new String(bytes, "utf-8");
                        System.out.println("client receive msg:" + msg);
                        //ctx.writeAndFlush(message);*/
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
