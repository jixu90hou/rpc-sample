package org.weweb.netty.rpc.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.weweb.netty.rpc.bean.SampleRequest;
import org.weweb.netty.rpc.bean.SampleResponse;
import org.weweb.netty.rpc.codec.RpcDecoder;
import org.weweb.netty.rpc.codec.RpcEncoder;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author wshen
 */
public class SampleServerTest {
    private static Map<String, Object> map = new HashMap<>();

    public static void main(String[] args) throws Exception {
        String className = "org.weweb.netty.rpc.service.SampleServiceImpl";
        Class aClass = Class.forName(className);
        // 指定接口
        String interfaceName = aClass.getInterfaces()[0].getName();
        map.put(interfaceName, aClass.newInstance());
        response();
    }

    public static void response() throws Exception {
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
                        pipeline.addLast(new RpcDecoder(SampleRequest.class));
                        pipeline.addLast(new RpcEncoder(SampleResponse.class));
                        pipeline.addLast(new SimpleChannelInboundHandler<SampleRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, SampleRequest msg) throws Exception {
                                // 收到消息直接打印输出
                                System.out.println(ctx.channel().remoteAddress() + " Say : " + msg);
                                String interfaceName = msg.getInterfaceName();
                                Object bean = map.get(interfaceName);
                                Class clazz = bean.getClass();
                                String methodName = msg.getMethodName();
                                Method method = clazz.getMethod(methodName, msg.getParameterTypes());
                                Object result = method.invoke(bean, msg.getParameters());
                                SampleResponse response = new SampleResponse();
                                response.setResult(result);
                                // SampleRequest sampleRequest= SerializationUtil.deserialize()
                                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
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
