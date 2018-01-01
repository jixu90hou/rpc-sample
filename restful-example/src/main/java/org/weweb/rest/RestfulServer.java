package org.weweb.rest;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.util.logging.Logger;

public class RestfulServer {
    static Logger logger = Logger.getLogger(String.valueOf(RestfulServer.class));

    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpResponseEncoder());
                pipeline.addLast(new HttpRequestDecoder());
                pipeline.addLast(new SimpleChannelInboundHandler<Object>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof HttpRequest) {
                            System.out.println("--------");
                            HttpRequest httpRequest = (HttpRequest) msg;
                            HttpMethod method = httpRequest.getMethod();
                            String methodName = method.name();
                            String uri = httpRequest.getUri();
                            logger.info("method name:" + methodName + "\t uri:" + uri);
                        }
                        if (msg instanceof HttpContent) {
                            ((DefaultLastHttpContent) msg).content().readableBytes();
                            HttpContent httpContent = (HttpContent) msg;
                            ByteBuf byteBuf = httpContent.content();
                            byte[] bytes=new byte[byteBuf.readableBytes()];
                            byteBuf.readBytes(bytes);
                            String info=new String(bytes);
                            RequestBody requestBody= JSON.parseObject(info,RequestBody.class);
                            logger.info("requestBody:"+requestBody);
                        }
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("---active---");
                        super.channelActive(ctx);
                    }

                    @Override
                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("===================");
                       // ctx.channel().closeFuture();
                        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
                        HttpHeaders httpHeaders=response.headers();
                        String responseValue="response value";
                        httpHeaders.add(HttpHeaders.Names.CONTENT_TYPE,"application/json");
                        httpHeaders.add(HttpHeaders.Names.CONTENT_LENGTH,responseValue.length());
                        ByteBuf byteBuf=response.content();
                        byteBuf.writeBytes(responseValue.getBytes("utf-8"));
                        ctx.writeAndFlush(response);
                        //.addListener(ChannelFutureListener.CLOSE);
                    }
                });
            }
        });
        bootstrap.option(ChannelOption.SO_BACKLOG,128);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
        ChannelFuture channelFuture = bootstrap.bind(8082).sync();
        Channel channel = channelFuture.channel();
       // channelFuture.addListener(ChannelFutureListener.CLOSE);
        System.out.println("-------start up-------");
        //channel.close().sync();
    }
}
