package org.weweb.rpc.test;

import org.weweb.rpc.bean.SampleRequest;
import org.weweb.rpc.bean.SampleResponse;
import org.weweb.rpc.registry.ServiceDiscovery;
import org.weweb.rpc.service.SampleService;
import org.weweb.rpc.service.TestService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class SampleClientTest {
    public static void main(String[] args) throws Exception {
        SampleService sampleService = createProxy(SampleService.class);
        System.out.println("-----------------");
        int result = sampleService.add(200, 100);
        System.out.println("result:" + result);

        String result2 = sampleService.say("沈伟峰");
        System.out.println("result:" + result2);


        TestService testService = createProxy(TestService.class);
        String responseResult = testService.print("zhang");
        System.out.println("responseResult:" + responseResult);
        System.exit(0);

    }

    public static <T> T createProxy(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        SampleRequest sampleRequest = new SampleRequest();
                        sampleRequest.setParameters(args);
                        sampleRequest.setMethodName(method.getName());
                        sampleRequest.setInterfaceName(interfaceClass.getName());
                        sampleRequest.setParameterTypes(method.getParameterTypes());
                        String requestId = UUID.randomUUID().toString();
                        sampleRequest.setRequestId(requestId);
                        // 远程请求数据
                        //通过注册中心获取请求地址
                        ServiceDiscovery serviceDiscovery=new ServiceDiscovery();
                        String serviceAddress = serviceDiscovery.discovery(sampleRequest.getInterfaceName());
                        String[] addresses = serviceAddress.split(":");
                        String host = addresses[0];
                        int port = Integer.valueOf(addresses[1]);
                        host="127.0.0.1";
                        port=9070;
                        SampleClient simpleClient = new SampleClient(host,port);
                        SampleResponse sampleResponse = simpleClient.send(sampleRequest);
                        return sampleResponse.getResult();
                    }
                });
    }
}
