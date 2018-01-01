package org.weweb.rpc.test;

import org.weweb.rpc.bean.SampleRequest;
import org.weweb.rpc.common.SerializationUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SampleTest {
    private static Map<String, Class<?>> map = new HashMap<>();
    public static byte[] encodeSample() throws Exception {
        Class clazz = Class.forName("org.weweb.service.SampleServiceImpl");
        Method method = clazz.getMethod("add", int.class, int.class);
        // set args to request
        SampleRequest sampleRequest = new SampleRequest();
        sampleRequest.setInterfaceName(clazz.getInterfaces()[0].getName());
        String requestId = UUID.randomUUID().toString();
        sampleRequest.setRequestId(requestId);
        map.put(requestId, clazz);
        sampleRequest.setMethodName(method.getName());
        sampleRequest.setParameters(new Object[] { 1, 2 });
        sampleRequest.setParameterTypes(method.getParameterTypes());
        byte[] bytes = SerializationUtil.serialize(sampleRequest);
        System.out.println("bytes:" + bytes);
        // System.out.println("series:"+SerializationUtil.deserialize(bytes,SampleRequest.class));
        // 反序列化后的内容
        return bytes;
    }

    public static void decodeSample(byte[] bytes) throws Exception {
        SampleRequest newSampleRequest = SerializationUtil.deserialize(bytes, SampleRequest.class);
        newSampleRequest.getInterfaceName();
        Class clazz = map.get(newSampleRequest.getRequestId());
        Method method = clazz.getMethod(newSampleRequest.getMethodName(), newSampleRequest.getParameterTypes());
        Object object = clazz.newInstance();
        Object result = method.invoke(object, newSampleRequest.getParameters());
        System.out.println("result:" + result);
    }
}
