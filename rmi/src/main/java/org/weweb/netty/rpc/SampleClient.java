package org.weweb.netty.rpc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @Author wshen
 */
public class SampleClient {
	public static ExecutorService executorService = new ThreadPoolExecutor(16, 16, 60L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());

	public static void main(String[] args) throws InterruptedException {
		int port = 9070;
		String host = "127.0.0.1";

		EventLoopGroup workerGroup = new NioEventLoopGroup(1);
		Bootstrap bootstrap = new Bootstrap();
		try {
			bootstrap.group(workerGroup).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							// todo 管道流添加解码器
							pipeline.addLast("framer",
									new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
							pipeline.addLast("decoder", new StringDecoder());
							pipeline.addLast("encoder", new StringEncoder());
							pipeline.addLast("handler", new SimpleChannelInboundHandler<String>() {
								@Override
								protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
									System.out.println("Server say:" + msg);
								}

								@Override
								public void channelActive(ChannelHandlerContext ctx) throws Exception {
									System.out.println("Client active ");
									super.channelActive(ctx);
								}

								@Override
								public void channelInactive(ChannelHandlerContext ctx) throws Exception {
									System.out.println("Client channelInactive ");
									super.channelInactive(ctx);
								}
							});
						}
					});
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
			Channel channel = channelFuture.channel();
			channel.writeAndFlush("bbbb \n").sync();
			channel.closeFuture().sync();
			channel.close();
		} finally {
			// workerGroup.shutdownGracefully();
		}

	}
}
