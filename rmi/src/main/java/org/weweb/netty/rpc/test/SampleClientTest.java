package org.weweb.netty.rpc.test;

import org.weweb.netty.rpc.bean.SampleRequest;
import org.weweb.netty.rpc.bean.SampleResponse;
import org.weweb.netty.rpc.service.SampleService;
import org.weweb.netty.rpc.service.TestService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class SampleClientTest {
	public static void main(String[] args) throws Exception {
		SampleService sampleService = createProxy(SampleService.class);
		int result = sampleService.add(12222, 100);
		System.out.println("result:" + result);

		String result2=sampleService.say("zhangmin44444gming");
		System.out.println("result:"+result2);


		TestService testService=createProxy(TestService.class);
		String responseResult=testService.print("zhang");
		System.out.println("responseResult:"+responseResult);
		System.exit(0);

	}

	public static <T> T createProxy(Class<T> interfaceClass) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
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
                        SimpleClientHandler simpleClient = new SimpleClientHandler();
						SampleResponse sampleResponse = simpleClient.send(sampleRequest);
						return sampleResponse.getResult();
					}
				});
	}

}
