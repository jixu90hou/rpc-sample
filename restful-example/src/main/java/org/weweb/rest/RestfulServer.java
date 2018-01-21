package org.weweb.rest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class RestfulServer {
    static Logger logger = Logger.getLogger(RestfulServer.class.getName());
    static AtomicInteger count=new AtomicInteger(0);
    public static void main(String[] args) throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*2);
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpRequestDecoder());
                pipeline.addLast(new HttpResponseEncoder());
                pipeline.addLast(new SimpleChannelInboundHandler<Object>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof HttpRequest) {
                            HttpRequest httpRequest = (HttpRequest) msg;
                            HttpMethod httpMethod = httpRequest.getMethod();
                            String uri = httpRequest.getUri();
                            //logger.info("method name:" + httpMethod.name() + "\t uri:" + uri);
                        }
                        if (msg instanceof HttpContent) {
                            HttpContent httpContent = (HttpContent) msg;
                            ByteBuf byteBuf = httpContent.content();
                            byte[] bytes = new byte[byteBuf.readableBytes()];
                            byteBuf.readBytes(bytes);
                            String requestMsg=new String(bytes);
                            logger.info("receive the content:" + new String(bytes)+"\t"+Thread.currentThread().getName()+"\t"+count.addAndGet(1));
                            response(ctx,requestMsg);
                        }
                    }

                    public void response(ChannelHandlerContext ctx,String requestMsg) throws Exception {
                        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                        HttpHeaders httpHeaders = response.headers();
                        Result result = new Result(200, "operate success", "hello restful");
                        String responseValue = requestMsg;
                        httpHeaders.set(HttpHeaders.Names.CONTENT_LENGTH, responseValue.length());
                        httpHeaders.set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
                        ByteBuf byteBuf = response.content();
                        byteBuf.writeBytes(responseValue.getBytes());
                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                        //super.channelReadComplete(ctx);
                        //{"id":1,"key":"2"}
                    }

                });
            }
        });
        ChannelFuture channelFuture = bootstrap.bind(8084).sync();
    }
}