package com.weweb.service;

import com.weweb.annotation.RpcService;

/**
 * Created by jackshen on 2017/5/21.
 */
@RpcService(HelloService.class)
public interface HelloService {

    String hello(String name);
}
