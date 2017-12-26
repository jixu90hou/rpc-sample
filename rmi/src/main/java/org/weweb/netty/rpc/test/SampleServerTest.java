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
import org.weweb.netty.rpc.common.ClassUtil;
import org.weweb.netty.rpc.registry.ServiceRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author wshen
 */
public class SampleServerTest {
    private static Map<String, Object> map = new HashMap<>();
    private static ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>());

    public static void main(String[] args) throws Exception {
        initRpcService();
        response();
    }

    public static void initRpcService() throws Exception {
        String packagePath = "org.weweb.netty.rpc.service";
        Set<Class<?>> classes = ClassUtil.getClasses(packagePath);
        String rpcServicePath = "org.weweb.netty.rpc.annoation.RpcService";
        for (Class clazz : classes) {
            Annotation[] annotations = clazz.getAnnotations();
            for (Annotation annotation : annotations) {
                if (rpcServicePath.equals(annotation.annotationType().getName())) {
                    // 指定接口获取内容
                    Class[] interfaces = clazz.getInterfaces();
                    String className;
                    if (interfaces.length > 0) {
                        className = interfaces[0].getName();
                    } else {
                        className = clazz.getName();
                    }
                    map.put(className, clazz.newInstance());
                }
            }
        }
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        String requstUrl = "127.0.0.1:9070";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String serviceName = entry.getKey();
            executorService.execute(() -> {
                try {
                    serviceRegistry.registry(serviceName, requstUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
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
