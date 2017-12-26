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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author wshen
 */
public class SampleServerTest {
    private static Map<String, Object> map = new HashMap<>();

    public static void main(String[] args) throws Exception {
        initRpcService();
        response();
    }
    public static void initRpcService() throws IllegalAccessException, InstantiationException {
        String packagePath="org.weweb.netty.rpc.service";
        Set<Class<?>> classes= ClassUtil.getClasses(packagePath);
        String rpcServicePath="org.weweb.netty.rpc.annoation.RpcService";
        for (Class clazz:classes){
            Annotation[] annotations=clazz.getAnnotations();
            for (Annotation annotation:annotations){
                if(rpcServicePath.equals(annotation.annotationType().getName())){
                    // 指定接口获取内容
                    Class[] interfaces=clazz.getInterfaces();
                    String className;
                    if(interfaces.length>0){
                        className= interfaces[0].getName();
                    }else{
                        className=clazz.getName();
                    }
                    map.put(className, clazz.newInstance());
                }
            }
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
