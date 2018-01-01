package org.weweb.rpc.service;

import org.weweb.rpc.annoation.RpcService;

@RpcService(SampleService.class)
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
