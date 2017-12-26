package org.weweb.netty.rpc;

import java.net.InetAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @Author wshen
 */
public class SampleServer {
	public static void main(String[] args) throws InterruptedException {
		int port = 9070;
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(1);
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						// todo 管道流添加解码器
						// pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192,
						// Delimiters.lineDelimiter()));
						pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
						pipeline.addLast("decoder", new StringDecoder());
						pipeline.addLast("encoder", new StringEncoder());
						pipeline.addLast("handler", new SimpleChannelInboundHandler<String>() {
							@Override
							protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
								// 收到消息直接打印输出
								System.out.println(ctx.channel().remoteAddress() + " Say : " + msg);
								ctx.writeAndFlush("Received your message !\n").addListener(ChannelFutureListener.CLOSE);
							}

							@Override
							public void channelActive(ChannelHandlerContext ctx) throws Exception {
								System.out.println("Remote : " + ctx.channel().remoteAddress() + " active !");
								ctx.writeAndFlush(
										"Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");
								super.channelActive(ctx);
							}
						});
					}
				});
		ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
		channelFuture.channel().closeFuture().sync();
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
}
