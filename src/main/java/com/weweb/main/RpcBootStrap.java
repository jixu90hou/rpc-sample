package com.weweb.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by jackshen on 2017/5/21.
 */
public class RpcBootStrap {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
