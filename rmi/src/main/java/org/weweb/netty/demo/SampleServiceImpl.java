package org.weweb.netty.demo;

import java.lang.reflect.Method;

public class SampleServiceImpl{
    public int add(int a, int b) {
        return a+b;
    }

    public static void main(String[] args) throws Exception{
        Class clazz=Class.forName("org.weweb.netty.demo.SampleServiceImpl");
        Object object=clazz.newInstance();
        Method[] methods=clazz.getMethods();
        Method method=clazz.getMethod("add",int.class,int.class);
        Object result=method.invoke(object,1,2);
        System.out.println("result:"+result);
    }
}
