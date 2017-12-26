package org.weweb.netty.rpc.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.weweb.netty.rpc.bean.SampleRequest;
import org.weweb.netty.rpc.bean.SampleResponse;
import org.weweb.netty.rpc.codec.RpcDecoder;
import org.weweb.netty.rpc.codec.RpcEncoder;

public class SimpleClientHandler extends SimpleChannelInboundHandler<SampleResponse> {
    private SampleResponse sampleResponse;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SampleResponse sampleResponse) throws Exception {
        this.sampleResponse=sampleResponse;
    }
    public SampleResponse send(SampleRequest sampleRequest) throws InterruptedException {
        int port = 9070;
        String host = "127.0.0.1";
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                // todo 管道流添加解码器
                pipeline.addLast(new RpcEncoder(SampleRequest.class));
                pipeline.addLast(new RpcDecoder(SampleResponse.class));
                pipeline.addLast(SimpleClientHandler.this);
            }
        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        ChannelFuture future = bootstrap.connect(host, port).sync();
        Channel channel = future.channel();
        channel.writeAndFlush(sampleRequest).sync();
        channel.closeFuture().sync();
        return sampleResponse;
    }
}
