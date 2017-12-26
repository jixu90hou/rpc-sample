package org.weweb.netty.rpc.service;

import org.weweb.netty.rpc.annoation.RpcService;

@RpcService
public class SampleServiceImpl implements SampleService {
	@Override
	public int add(int a, int b) {
		return a + b;
	}

	@Override
	public String say(String info) {
		return "你好啊："+info;
	}
}
